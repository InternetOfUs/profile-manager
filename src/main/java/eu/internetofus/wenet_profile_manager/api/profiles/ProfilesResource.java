/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.wenet_profile_manager.api.profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.profile_manager.Competence;
import eu.internetofus.common.components.profile_manager.Material;
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.PlannedActivity;
import eu.internetofus.common.components.profile_manager.RelevantLocation;
import eu.internetofus.common.components.profile_manager.Routine;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationship;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilder;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

/**
 * Resource that provide the methods for the {@link Profiles}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesResource implements Profiles {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * The repository to manage the profiles.
   */
  protected ProfilesRepository repository;

  /**
   * Create an empty resource. This is only used for unit tests.
   */
  protected ProfilesResource() {

  }

  /**
   * Create a new instance to provide the services of the {@link Profiles}.
   *
   * @param vertx with the event bus to use.
   */
  public ProfilesResource(final Vertx vertx) {

    this.vertx = vertx;
    this.repository = ProfilesRepository.createProxy(vertx);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfile(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    ModelResources.retrieveModel(this.repository::searchProfile, userId, "profile", context, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    ModelResources.createModel(this.vertx, WeNetUserProfile.class, body, "profile", this.repository::storeProfile, context, resultHandler, storedModel -> {

      OperationReponseHandlers.responseWith(resultHandler, Status.CREATED, storedModel);

      // Update the social context of the created user
      WeNetSocialContextBuilder.createProxy(this.vertx).retrieveJsonArraySocialRelations(storedModel.id, retrieve -> {

        if (retrieve.failed()) {

          Logger.trace(retrieve.cause(), "Cannot update the social relations of {}.", storedModel);

        } else {

          Logger.trace("Obtained for the user {} the next social relations {}.", () -> storedModel.id, () -> retrieve.result());
        }
      });
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var source = Model.fromJsonObject(body, WeNetUserProfile.class);
    if (source == null) {

      Logger.debug("The {} is not a valid WeNetUserProfile to update.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_profile_to_update", "The profile to update is not right.");

    } else {

      this.repository.searchProfile(userId, search -> {

        final var target = search.result();
        if (target == null) {

          Logger.debug(search.cause(), "Not found profile {} to update", userId);
          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_profile_to_update", "You can not update the profile of the user '" + userId + "', because it does not exist.");

        } else {

          source.id = null;
          source.validate("bad_new_profile", this.vertx).onComplete(validate -> {

            if (validate.failed()) {

              final var cause = validate.cause();
              Logger.debug(cause, "Cannot update {} with {}.", target, source);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

            } else {

              source.id = target.id;
              source._creationTs = target._creationTs;
              source._lastUpdateTs = target._lastUpdateTs;
              if (source.equals(target)) {

                OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "profile_to_update_equal_to_original",
                    "You can not update the profile of the user '" + userId + "', because the new values is equals to the current one.");

              } else {

                this.repository.updateProfile(source, update -> {

                  if (update.failed()) {

                    final var cause = update.cause();
                    Logger.debug(cause, "Cannot update {}.", target);
                    OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

                  } else {

                    final var historic = new HistoricWeNetUserProfile();
                    historic.from = target._lastUpdateTs;
                    historic.to = TimeManager.now();
                    historic.profile = target;
                    this.repository.storeHistoricProfile(historic, store -> {

                      if (store.failed()) {

                        Logger.debug(store.cause(), "Cannot store the updated profile as historic.");
                      }
                      source._lastUpdateTs = historic.to;
                      OperationReponseHandlers.responseOk(resultHandler, source);

                    });
                  }
                });
              }
            }
          });
        }
      });
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfile(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    ModelResources.mergeModel(this.vertx, userId, "profile", WeNetUserProfile.class, this.repository::searchProfile, body, this.repository::updateProfile, context, resultHandler, (source, target, merged) -> {

      final var historic = new HistoricWeNetUserProfile();
      historic.from = target._lastUpdateTs;
      historic.to = TimeManager.now();
      historic.profile = target;
      this.repository.storeHistoricProfile(historic, store -> {

        if (store.failed()) {

          Logger.debug(store.cause(), "Cannot store the merged profile as historic.");
        }
        OperationReponseHandlers.responseOk(resultHandler, merged);

      });

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    ModelResources.deleteModel(this.repository::deleteProfile, userId, "profile", context, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileHistoricPage(final String userId, final Long from, final Long to, final String order, final int offset, final int limit, final OperationRequest context,
      final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, from, to);
    final var sort = ProfilesRepository.createProfileHistoricPageSort(order);
    this.repository.searchHistoricProfilePage(query, sort, offset, limit, search -> {

      if (search.failed()) {

        final var cause = search.cause();
        Logger.debug(cause, "Cannot found historic profile for the user {}.", userId);
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.NOT_FOUND, cause);

      } else {

        final var page = search.result();
        if (page.total == 0l) {

          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "no_found", "Not found any historic profile that match to the specific parameters.");

        } else {

          OperationReponseHandlers.responseOk(resultHandler, page);
        }
      }
    });

  }

  /**
   * Validate a model.
   *
   * @param type          of model to validate.
   * @param value         of the model to verify.
   * @param modelName     name of the type.
   * @param resultHandler handler for the http response.
   * @param success       component to call if the model is valid.
   *
   * @param <T>           type of model to test.
   */
  protected <T extends Model & Validable> void validate(final Class<T> type, final JsonObject value, final String modelName, final Handler<AsyncResult<OperationResponse>> resultHandler, final Consumer<T> success) {

    final var model = Model.fromJsonObject(value, type);
    if (model == null) {

      Logger.debug("The JSON {} does not represents a {}.", value, modelName);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_json", "The JSON does not represents a " + modelName + ".");

    } else {

      final var codePrefix = "bad_" + modelName;
      model.validate(codePrefix, this.vertx).onComplete(valid -> {

        if (valid.failed()) {

          final var cause = valid.cause();
          Logger.debug(cause, "The {} is not a valid {}.", model, modelName);
          OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          success.accept(model);
        }
      });

    }
  }

  /**
   * Search for a profile.
   *
   * @param userId        identifier of the user to get the profile.
   * @param resultHandler handler for the http response.
   * @param success       component to call when the profile is found.
   */
  protected void searchProfile(final String userId, final Handler<AsyncResult<OperationResponse>> resultHandler, final Consumer<WeNetUserProfile> success) {

    this.repository.searchProfile(userId, search -> {

      final var target = search.result();
      if (target == null) {

        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "profile_not_defined", "Does not exist a profile associated to the user identifier.");

      } else {

        success.accept(target);
      }
    });

  }

  /**
   * Update a profile.
   *
   * @param original      profile before update.
   * @param profile       to update.
   * @param resultHandler handler for the response.
   * @param success       called when the profile is updated.
   */
  protected void updateProfile(final WeNetUserProfile original, final WeNetUserProfile profile, final Handler<AsyncResult<OperationResponse>> resultHandler, final Runnable success) {

    this.repository.updateProfile(profile, update -> {

      if (update.failed()) {

        final var cause = update.cause();
        Logger.debug(cause, "Cannot update profile {}.", profile);
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

      } else {

        final var historic = new HistoricWeNetUserProfile();
        historic.from = original._lastUpdateTs;
        historic.to = TimeManager.now();
        historic.profile = original;
        this.repository.storeHistoricProfile(historic, store -> {

          if (store.failed()) {

            Logger.debug(store.cause(), "Cannot update profile historic.");
          }
          success.run();

        });
      }
    });
  }

  /**
   * Add a model into a profile.
   *
   * @param type          of model.
   * @param value         of the model to add.
   * @param modelName     name of the type to validate.
   * @param userId        identifier of the user
   * @param getModels     function to get the models from the profile.
   * @param setModels     function to set the models for the profile.
   * @param equalsId      function to check if two model has the same identifier.
   * @param resultHandler handler for the response.
   *
   * @param <T>           type of model to add.
   */
  protected <T extends Model & Validable> void addModelToProfile(final Class<T> type, final JsonObject value, final String modelName, final String userId, final Function<WeNetUserProfile, List<T>> getModels,
      final BiConsumer<WeNetUserProfile, List<T>> setModels, final BiPredicate<T, T> equalsId, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.validate(type, value, modelName, resultHandler, model -> {

      this.searchProfile(userId, resultHandler, profile -> {

        final var newProfile = Model.fromJsonObject(profile.toJsonObject(), WeNetUserProfile.class);
        var models = getModels.apply(newProfile);
        if (models == null) {

          models = new ArrayList<>();
          setModels.accept(newProfile, models);

        } else {

          for (final T defined : models) {

            if (equalsId.test(defined, model)) {

              OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "duplicated_" + modelName + "_identifier", "Already exist a " + modelName + " with the specified identifier.");
              return;
            }

          }
        }

        models.add(model);
        this.updateProfile(profile, newProfile, resultHandler, () -> OperationReponseHandlers.responseOk(resultHandler, model));

      });
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addNorm(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(Norm.class, body, "norm", userId, profile -> profile.norms, (profile, norms) -> profile.norms = norms, (norm1, norm2) -> norm1.id.equals(norm2.id), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveNorms(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.norms, resultHandler);

  }

  /**
   * Retrieve the models defined in a profile.
   *
   * @param userId        identifier of the user of the profile.
   * @param modelsIn      return the models defined in a profile.
   * @param resultHandler to fill in with the response.
   *
   * @param <T>           type of model to retrieve.
   */
  protected <T extends Model> void retrieveModelsFromProfile(final String userId, final Function<WeNetUserProfile, List<T>> modelsIn, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.searchProfile(userId, resultHandler, profile -> {

      final var models = modelsIn.apply(profile);
      JsonArray array = null;
      if (models != null) {

        array = Model.toJsonArray(models);

      } else {

        array = new JsonArray();

      }

      OperationReponseHandlers.responseOk(resultHandler, array);

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveNorm(final String userId, final String normId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.searchModelById(profile -> profile.norms, norm -> norm.id.equals(normId)), "norm", resultHandler);

  }

  /**
   * Search model by identifier.
   *
   * @param modelsIn function to obtain the models from a profile.
   * @param checkId  function to check if the model has the specified identifier.
   *
   * @param <T>      type of models to check.
   *
   * @return the model associated to the identifier.
   */
  protected <T> Function<WeNetUserProfile, T> searchModelById(final Function<WeNetUserProfile, List<T>> modelsIn, final Predicate<T> checkId) {

    return profile -> {

      final var models = modelsIn.apply(profile);
      if (models != null) {

        for (final T model : models) {

          if (checkId.test(model)) {

            return model;
          }
        }

      }

      // Not found
      return null;

    };
  }

  /**
   * Retrieve the model defined in a profile.
   *
   * @param userId        identifier of the user of the profile.
   * @param seachModel    function to search the model that has the specified identifier. It return {@code null} if not
   *                      found.
   * @param modelName     name of the model.
   * @param resultHandler to fill in with the response.
   *
   * @param <T>           type of model to retrieve.
   */
  protected <T extends Model> void retrieveModelFromProfile(final String userId, final Function<WeNetUserProfile, T> seachModel, final String modelName, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.searchProfile(userId, resultHandler, profile -> {

      final var model = seachModel.apply(profile);
      if (model == null) {

        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, modelName + "_not_defined", "Does not exist in the profile a " + modelName + " with the identifier.");

      } else {

        OperationReponseHandlers.responseOk(resultHandler, model);
      }

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateNorm(final String userId, final String normId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body.put("id", normId), Norm.class, profile -> profile.norms, this.searchModelIndexById(norm -> norm.id.equals(normId)), "norm", resultHandler);

  }

  /**
   * Search index of model by identifier.
   *
   * @param checkId function to check if the model has the specified identifier.
   *
   * @param <T>     type of models to check.
   *
   * @return a function that can be used to get the index of the model that has the specified identifier if not found
   *         return {@code -1}.
   *
   */
  protected <T> Function<List<T>, Integer> searchModelIndexById(final Predicate<T> checkId) {

    return models -> {

      if (models != null) {

        final var max = models.size();
        for (var i = 0; i < max; i++) {

          final var model = models.get(i);
          if (checkId.test(model)) {

            return i;
          }
        }

      }

      // Not found
      return -1;

    };
  }

  /**
   * Update a model defined in a profile.
   *
   * @param userId        identifier of the user of the profile.
   * @param value         for the model.
   * @param type          of the model.
   * @param modelsIn      return the models defined in a profile.
   * @param modelIndex    function to return the index where the model is in the models list.
   * @param modelName     name of the model.
   * @param resultHandler to fill in with the response.
   *
   * @param <T>           type of model to update.
   */
  protected <T extends Model & Validable> void updateModelFromProfile(final String userId, final JsonObject value, final Class<T> type, final Function<WeNetUserProfile, List<T>> modelsIn, final Function<List<T>, Integer> modelIndex,
      final String modelName, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.validate(type, value, modelName, resultHandler, model -> {

      this.searchProfile(userId, resultHandler, profile -> {

        final var models = modelsIn.apply(profile);
        if (models != null) {

          final int index = modelIndex.apply(models);
          if (index > -1) {

            final var newProfile = Model.fromJsonObject(profile.toJsonObject(), WeNetUserProfile.class);
            final var newProfileModels = modelsIn.apply(newProfile);
            newProfileModels.remove(index);
            newProfileModels.add(index, model);
            this.updateProfile(profile, newProfile, resultHandler, () -> OperationReponseHandlers.responseOk(resultHandler, model));
            return;
          }

        }

        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, modelName + "_not_defined", "Cannot found a " + modelName + " with the specified parameters on the profile.");

      });
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeNorm(final String userId, final String normId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body.put("id", normId), Norm.class, profile -> profile.norms, this.searchModelIndexById(norm -> norm.id.equals(normId)), "norm", resultHandler);

  }

  /**
   * Merge a model defined in a profile.
   *
   * @param userId        identifier of the user of the profile.
   * @param value         for the model.
   * @param type          of the model.
   * @param modelsIn      return the models defined in a profile.
   * @param modelIndex    function to return the index where the model is in the models list.
   * @param modelName     name of the model.
   * @param resultHandler to fill in with the response.
   *
   * @param <T>           type of model to merge.
   */
  protected <T extends Model & Mergeable<T>> void mergeModelFromProfile(final String userId, final JsonObject value, final Class<T> type, final Function<WeNetUserProfile, List<T>> modelsIn, final Function<List<T>, Integer> modelIndex,
      final String modelName, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var source = Model.fromJsonObject(value, type);
    if (source == null) {

      Logger.debug("The JSON {} does not represents a {}.", value, modelName);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_json", "The JSON does not represents a " + modelName + ".");

    } else {

      this.searchProfile(userId, resultHandler, profile -> {

        final var models = modelsIn.apply(profile);
        if (models != null) {

          final int index = modelIndex.apply(models);
          if (index > -1) {

            final var target = models.get(index);
            target.merge(source, "bad_" + modelName, this.vertx).onComplete(merge -> {
              if (merge.failed()) {

                final var cause = merge.cause();
                Logger.debug(cause, "The {} can not be merged with {}.", source, target);
                OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

              } else {

                final var merged = merge.result();
                final var newProfile = Model.fromJsonObject(profile.toJsonObject(), WeNetUserProfile.class);
                final var newProfileModels = modelsIn.apply(newProfile);
                newProfileModels.remove(index);
                newProfileModels.add(index, merged);
                this.updateProfile(profile, newProfile, resultHandler, () -> OperationReponseHandlers.responseOk(resultHandler, merged));
              }
            });
            return;
          }
        }

        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, modelName + "_not_defined", "Cannot found a " + modelName + " with the specified parameters on the profile.");

      });
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteNorm(final String userId, final String normId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "norm", profile -> profile.norms, this.searchModelIndexById(norm -> norm.id.equals(normId)), resultHandler);

  }

  /**
   * Delete a model defined in a profile.
   *
   * @param userId        identifier of the user of the profile.
   * @param modelName     name of the model.
   * @param modelsIn      return the models defined in a profile.
   * @param modelIndex    function to return the index where the model is in the models list.
   * @param resultHandler to fill in with the response.
   *
   * @param <T>           type of model to delete.
   */
  protected <T> void deleteModelFromProfile(final String userId, final String modelName, final Function<WeNetUserProfile, List<T>> modelsIn, final Function<List<T>, Integer> modelIndex,
      final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.searchProfile(userId, resultHandler, profile -> {

      final var models = modelsIn.apply(profile);
      if (models != null) {

        final int index = modelIndex.apply(models);
        if (index > -1) {

          final var newProfile = Model.fromJsonObject(profile.toJsonObject(), WeNetUserProfile.class);
          final var newProfileModels = modelsIn.apply(newProfile);
          newProfileModels.remove(index);
          this.updateProfile(profile, newProfile, resultHandler, () -> OperationReponseHandlers.responseOk(resultHandler));
          return;
        }
      }

      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, modelName + "_not_defined", "Cannot found a " + modelName + " with the specified parameters on the profile.");

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPlannedActivity(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(PlannedActivity.class, body, "planned_activity", userId, profile -> profile.plannedActivities, (profile, plannedActivities) -> profile.plannedActivities = plannedActivities,
        (plannedActivity1, plannedActivity2) -> plannedActivity1.id.equals(plannedActivity2.id), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrievePlannedActivities(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.plannedActivities, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrievePlannedActivity(final String userId, final String plannedActivityId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.searchModelById(profile -> profile.plannedActivities, plannedActivity -> plannedActivity.id.equals(plannedActivityId)), "planned_activity", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updatePlannedActivity(final String userId, final String plannedActivityId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body.put("id", plannedActivityId), PlannedActivity.class, profile -> profile.plannedActivities, this.searchModelIndexById(plannedActivity -> plannedActivity.id.equals(plannedActivityId)),
        "planned_activity", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergePlannedActivity(final String userId, final String plannedActivityId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body.put("id", plannedActivityId), PlannedActivity.class, profile -> profile.plannedActivities, this.searchModelIndexById(plannedActivity -> plannedActivity.id.equals(plannedActivityId)),
        "planned_activity", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deletePlannedActivity(final String userId, final String plannedActivityId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "planned_activity", profile -> profile.plannedActivities, this.searchModelIndexById(plannedActivity -> plannedActivity.id.equals(plannedActivityId)), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addRelevantLocation(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(RelevantLocation.class, body, "relevant_location", userId, profile -> profile.relevantLocations, (profile, relevantLocations) -> profile.relevantLocations = relevantLocations,
        (relevantLocation1, relevantLocation2) -> relevantLocation1.id.equals(relevantLocation2.id), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveRelevantLocations(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.relevantLocations, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveRelevantLocation(final String userId, final String relevantLocationId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.searchModelById(profile -> profile.relevantLocations, relevantLocation -> relevantLocation.id.equals(relevantLocationId)), "relevant_location", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateRelevantLocation(final String userId, final String relevantLocationId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body.put("id", relevantLocationId), RelevantLocation.class, profile -> profile.relevantLocations, this.searchModelIndexById(relevantLocation -> relevantLocation.id.equals(relevantLocationId)),
        "relevant_location", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeRelevantLocation(final String userId, final String relevantLocationId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body.put("id", relevantLocationId), RelevantLocation.class, profile -> profile.relevantLocations, this.searchModelIndexById(relevantLocation -> relevantLocation.id.equals(relevantLocationId)),
        "relevant_location", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteRelevantLocation(final String userId, final String relevantLocationId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "relevant_location", profile -> profile.relevantLocations, this.searchModelIndexById(relevantLocation -> relevantLocation.id.equals(relevantLocationId)), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addRelationship(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(SocialNetworkRelationship.class, body, "relationship", userId, profile -> profile.relationships, (profile, relationships) -> profile.relationships = relationships,
        (relationship1, relationship2) -> relationship1.equals(relationship2), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveRelationships(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.relationships, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveRelationship(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.seachByIndex(index, profile -> profile.relationships), "relationship", resultHandler);

  }

  /**
   * Search a model by its position on the models list.
   *
   * @param modelsIn function to obtain the models from a profile.
   * @param index    of the model to return.
   *
   * @return the function to obtain the model at the index or {@code null} if any model is defined on the index.
   *
   * @param <T> type of model to search.
   */
  protected <T> Function<WeNetUserProfile, T> seachByIndex(final int index, final Function<WeNetUserProfile, List<T>> modelsIn) {

    return profile -> {

      final var models = modelsIn.apply(profile);
      if (models != null && index > -1 && index < models.size()) {

        return models.get(index);

      } else {
        // Not found
        return null;
      }

    };
  }

  /**
   * Validate that the index is valid for the specified models.
   *
   * @param index to validate.
   *
   * @return the function that will check if the index is valid for the models list.
   *
   * @param <T> type of model on the list.
   */
  protected <T> Function<List<T>, Integer> checkIndexOnModels(final int index) {

    return models -> {

      if (index > -1 && index < models.size()) {

        return index;

      } else {

        return -1;
      }

    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateRelationship(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body, SocialNetworkRelationship.class, profile -> profile.relationships, this.checkIndexOnModels(index), "relationship", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeRelationship(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body, SocialNetworkRelationship.class, profile -> profile.relationships, this.checkIndexOnModels(index), "relationship", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteRelationship(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "relationship", profile -> profile.relationships, this.checkIndexOnModels(index), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPersonalBehavior(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(Routine.class, body, "personal_behaviour", userId, profile -> profile.personalBehaviors, (profile, personalBehaviors) -> profile.personalBehaviors = personalBehaviors,
        (personalBehavior1, personalBehavior2) -> personalBehavior1.equals(personalBehavior2), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrievePersonalBehaviors(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.personalBehaviors, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrievePersonalBehavior(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.seachByIndex(index, profile -> profile.personalBehaviors), "personal_behaviour", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updatePersonalBehavior(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body, Routine.class, profile -> profile.personalBehaviors, this.checkIndexOnModels(index), "personal_behaviour", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergePersonalBehavior(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body, Routine.class, profile -> profile.personalBehaviors, this.checkIndexOnModels(index), "personal_behaviour", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deletePersonalBehavior(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "personal_behaviour", profile -> profile.personalBehaviors, this.checkIndexOnModels(index), resultHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMaterial(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(Material.class, body, "material", userId, profile -> profile.materials, (profile, materials) -> profile.materials = materials, (material1, material2) -> material1.equals(material2), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveMaterials(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.materials, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveMaterial(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.seachByIndex(index, profile -> profile.materials), "material", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateMaterial(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body, Material.class, profile -> profile.materials, this.checkIndexOnModels(index), "material", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeMaterial(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body, Material.class, profile -> profile.materials, this.checkIndexOnModels(index), "material", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteMaterial(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "material", profile -> profile.materials, this.checkIndexOnModels(index), resultHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCompetence(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(Competence.class, body, "competence", userId, profile -> profile.competences, (profile, competences) -> profile.competences = competences, (competence1, competence2) -> competence1.equals(competence2),
        resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCompetences(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.competences, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCompetence(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.seachByIndex(index, profile -> profile.competences), "competence", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCompetence(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body, Competence.class, profile -> profile.competences, this.checkIndexOnModels(index), "competence", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCompetence(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body, Competence.class, profile -> profile.competences, this.checkIndexOnModels(index), "competence", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCompetence(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "competence", profile -> profile.competences, this.checkIndexOnModels(index), resultHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMeaning(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.addModelToProfile(Meaning.class, body, "meaning", userId, profile -> profile.meanings, (profile, meanings) -> profile.meanings = meanings, (meaning1, meaning2) -> meaning1.equals(meaning2), resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveMeanings(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelsFromProfile(userId, profile -> profile.meanings, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveMeaning(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.retrieveModelFromProfile(userId, this.seachByIndex(index, profile -> profile.meanings), "meaning", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateMeaning(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.updateModelFromProfile(userId, body, Meaning.class, profile -> profile.meanings, this.checkIndexOnModels(index), "meaning", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeMeaning(final String userId, final int index, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.mergeModelFromProfile(userId, body, Meaning.class, profile -> profile.meanings, this.checkIndexOnModels(index), "meaning", resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteMeaning(final String userId, final int index, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.deleteModelFromProfile(userId, "meaning", profile -> profile.meanings, this.checkIndexOnModels(index), resultHandler);
  }

}
