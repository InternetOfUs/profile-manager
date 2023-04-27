/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.wenet_profile_manager.api.profiles;

import eu.internetofus.common.components.WeNetModelContext;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngine;
import eu.internetofus.common.components.models.Competence;
import eu.internetofus.common.components.models.DeprecatedSocialNetworkRelationship;
import eu.internetofus.common.components.models.Material;
import eu.internetofus.common.components.models.Meaning;
import eu.internetofus.common.components.models.PlannedActivity;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.RelevantLocation;
import eu.internetofus.common.components.models.Routine;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.HistoricWeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.social_context_builder.ProfileUpdateNotification;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilder;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.TimeManager;
import eu.internetofus.common.vertx.ModelContext;
import eu.internetofus.common.vertx.ModelFieldContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepository;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import eu.internetofus.wenet_profile_manager.persistence.RelationshipsRepository;
import eu.internetofus.wenet_profile_manager.persistence.TrustsRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

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
  protected ProfilesRepository profilesRepository;

  /**
   * This is {@code true} if has to auto store all the profiles changes on the
   * history.
   */
  protected boolean autoStoreProfileChangesInHistory;

  /**
   * Create a new instance to provide the services of the {@link Profiles}.
   *
   * @param vertx                            with the event bus to use.
   * @param autoStoreProfileChangesInHistory is {@code true} if has to save the
   *                                         changes on the history.
   */
  public ProfilesResource(final Vertx vertx, final boolean autoStoreProfileChangesInHistory) {

    this.vertx = vertx;
    this.profilesRepository = ProfilesRepository.createProxy(vertx);
    this.autoStoreProfileChangesInHistory = autoStoreProfileChangesInHistory;

  }

  /**
   * Create the profile context.
   *
   * @return the context of the {@link WeNetUserProfile}.
   */
  protected WeNetModelContext<WeNetUserProfile, String> createProfileContext() {

    return WeNetModelContext.creteWeNetContext("profile", WeNetUserProfile.class, this.vertx);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfile(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModel(model, (id, handler) -> this.profilesRepository.searchProfile(id).onComplete(handler),
        context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.createModelChain(body, model,
        (profile, handler) -> this.profilesRepository.storeProfile(profile).onComplete(handler), context, () -> {

          ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, model.value);

          // Update the social context of the created user
          WeNetSocialContextBuilder.createProxy(this.vertx).initializeSocialRelations(model.value)
              .onComplete(retrieve -> {

                if (retrieve.failed()) {

                  Logger.trace(retrieve.cause(), "Cannot initialize the social relations of {}.", () -> model.value.id);

                } else {

                  Logger.trace("Initialized social relations of the user {}.", () -> model.value.id);
                }
              });

          this.addUpdateRelationships(model.value.id, model.source.relationships);

        });

  }

  /**
   * Add or update the deprecated relationships.
   *
   * @param profileId     identifier where the relationships are defined.
   * @param relationships to update or add.
   */
  private void addUpdateRelationships(final String profileId,
      final List<DeprecatedSocialNetworkRelationship> relationships) {

    if (relationships != null && !relationships.isEmpty()) {

      final var newRelationships = new ArrayList<SocialNetworkRelationship>();
      for (final DeprecatedSocialNetworkRelationship relationship : relationships) {

        final var newRelationship = new SocialNetworkRelationship();
        newRelationship.appId = relationship.appId;
        newRelationship.sourceId = profileId;
        newRelationship.targetId = relationship.userId;
        newRelationship.type = relationship.type;
        newRelationship.weight = relationship.weight;
        newRelationships.add(newRelationship);
      }

      WeNetProfileManager.createProxy(this.vertx).addOrUpdateSocialNetworkRelationships(newRelationships)
          .onComplete(updated -> {

            if (updated.failed()) {

              Logger.trace(updated.cause(), "Cannot update the social network relationships of {}.", profileId);
            }

          });

    }

  }

  /**
   * Detect if it has to store the chnages or not.
   *
   * @param storeProfileChangesInHistory is {@code true} if has to store the
   *                                     changes in the history.
   *
   * @return {@code true} if it has to store the changes.
   */
  protected boolean calculateStoreChanges(final Boolean storeProfileChangesInHistory) {

    if (storeProfileChangesInHistory != null) {

      return storeProfileChangesInHistory.booleanValue();

    } else {

      return this.autoStoreProfileChangesInHistory;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(final Boolean storeProfileChangesInHistory, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.updateModelChain(body, model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context, false,
        this.addProfileToHistoricChain(storeChanges, model, () -> {

          ServiceResponseHandlers.responseOk(resultHandler, model.value);
          this.addUpdateRelationships(model.id, model.source.relationships);

        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfile(final Boolean storeProfileChangesInHistory, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.mergeModelChain(body, model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, model, () -> {

          ServiceResponseHandlers.responseOk(resultHandler, model.value);
          this.addUpdateRelationships(model.id, model.source.relationships);

        }));
  }

  /**
   * Create the function to add a profile to the historic.
   *
   * @param storeHistoricProfile is {@code true} if has to store the profile
   *                             changes on the history.
   * @param model                context with the profile to add.
   * @param success              function to call if the profile has added.
   *
   * @return the function to call to store the profile into the historic.
   */
  protected Runnable addProfileToHistoricChain(final boolean storeHistoricProfile,
      final ModelContext<WeNetUserProfile, String, WeNetValidateContext> model, final Runnable success) {

    return () -> {

      if (model.value != null) {

        model.value._lastUpdateTs = TimeManager.now();
      }
      success.run();

      // notify the social context builder
      final var profileId = model.target.id;
      final var notification = new ProfileUpdateNotification();
      notification.updatedFieldNames = new HashSet<>();
      final var original = model.target.toJsonObject();
      if (model.value != null) {

        final var updated = model.value.toJsonObject();
        final var keys = new HashSet<String>();
        keys.addAll(updated.fieldNames());
        keys.addAll(original.fieldNames());
        for (final var key : keys) {

          final var originalValue = original.getValue(key);
          final var updatedValue = updated.getValue(key);
          if (originalValue != updatedValue && (originalValue == null || !originalValue.equals(updatedValue))) {

            notification.updatedFieldNames.add(key);

          }

        }

      } else {

        notification.updatedFieldNames.addAll(original.fieldNames());
      }

      notification.updatedFieldNames.remove("_id");
      notification.updatedFieldNames.remove("id");
      notification.updatedFieldNames.remove("_creationTs");
      notification.updatedFieldNames.remove("_lastUpdateTs");

      if (!notification.updatedFieldNames.isEmpty()) {

        if (storeHistoricProfile) {

          final var historic = new HistoricWeNetUserProfile();
          historic.from = model.target._lastUpdateTs;
          historic.to = model.value._lastUpdateTs;
          historic.profile = model.target;
          this.profilesRepository.storeHistoricProfile(historic).onComplete(store -> {

            if (store.failed()) {

              Logger.debug(store.cause(), "Cannot store the profile {} as historic.", historic);
            }

          });
        }

        WeNetSocialContextBuilder.createProxy(this.vertx).socialNotificationProfileUpdate(profileId, notification)
            .onComplete(retrieve -> {

              if (retrieve.failed()) {

                Logger.trace(retrieve.cause(),
                    "Cannot to the social context builder that the profile of the user {} has updated.", profileId);

              } else {

                Logger.trace("Notified to the social context builder that the profile of the user {} has updated.",
                    profileId);
              }
            });

      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    this.profilesRepository.deleteProfile(userId).onComplete(handler -> {

      if (handler.failed()) {

        ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "no_found",
            "Not found any profile associated to the identifier.");

      } else {

        ServiceResponseHandlers.responseOk(resultHandler);
        this.deleteAllReferenceToUser(userId);
        this.notifyProfileDeleted(userId);
      }

    });

  }

  /**
   * Remove all the data that has any reference to an user.
   *
   * @param userId identifier of the user that has been removed.
   */
  private void deleteAllReferenceToUser(final String userId) {

    this.profilesRepository.deleteHistoricProfile(userId).onComplete(deleted -> {

      if (deleted.failed()) {

        Logger.trace(deleted.cause(), "Cannot deleted the historic of {}.", userId);
      }

    });
    RelationshipsRepository.createProxy(this.vertx).deleteAllSocialNetworkRelationshipWith(userId)
        .onComplete(deleted -> {

          if (deleted.failed()) {

            Logger.trace(deleted.cause(), "Cannot deleted the social network relationships of {}.", userId);
          }

        });
    CommunitiesRepository.createProxy(this.vertx).deleteAllMembersForUser(userId).onComplete(deleted -> {

      if (deleted.failed()) {

        Logger.trace(deleted.cause(), "Cannot deleted the members from the community of {}.", userId);
      }

    });
    TrustsRepository.createProxy(this.vertx).deleteAllEventsForUser(userId).onComplete(deleted -> {

      if (deleted.failed()) {

        Logger.trace(deleted.cause(), "Cannot deleted the events for user {}.", userId);
      }

    });

  }

  /**
   * Called when has to notify that a profile has been removed.
   *
   * @param userId identifier of the user that is removed its profile.
   */
  private void notifyProfileDeleted(final String userId) {

    WeNetTaskManager.createProxy(this.vertx).profileDeleted(userId).onComplete(deleted -> {

      if (deleted.failed()) {

        Logger.trace(deleted.cause(), "Cannot notify to the task manager that the profile {} has been deleted.",
            userId);
      }

    });
    WeNetInteractionProtocolEngine.createProxy(this.vertx).profileDeleted(userId).onComplete(deleted -> {

      if (deleted.failed()) {

        Logger.trace(deleted.cause(),
            "Cannot notify to the interaction protocol engine that the profile {} has been deleted.", userId);
      }

    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileHistoricPage(final String userId, final Long from, final Long to, final String order,
      final int offset, final int limit, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, from, to);
    final var sort = ProfilesRepository.createProfileHistoricPageSort(order);
    this.profilesRepository.searchHistoricProfilePage(query, sort, offset, limit).onComplete(search -> {

      if (search.failed()) {

        final var cause = search.cause();
        Logger.debug(cause, "Cannot found historic profile for the user {}.", userId);
        ServiceResponseHandlers.responseFailedWith(resultHandler, Status.NOT_FOUND, cause);

      } else {

        final var page = search.result();
        if (page.total == 0l) {

          ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "no_found",
              "Not found any historic profile that match to the specific parameters.");

        } else {

          ServiceResponseHandlers.responseOk(resultHandler, page);
        }
      }
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileNorm(final Boolean storeProfileChangesInHistory, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, String, WeNetValidateContext>(), "norm",
        ProtocolNorm.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.norms, (profile, norms) -> profile.norms = norms,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileNorms(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.norms, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileNorm(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer, WeNetValidateContext>(), "norms",
        ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.norms, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileNorm(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer, WeNetValidateContext>(), "norms",
        ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.norms, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileNorm(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer, WeNetValidateContext>(), "norms",
        ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.norms, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileNorm(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer, WeNetValidateContext>(), "norms",
        ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.norms, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * Add into a {@link ModelFieldContext} the necessaries values.
   *
   * @param element to fill in.
   * @param name    for the element.
   * @param type    for the element.
   *
   * @param <T>     class for the element.
   * @param <I>     class for the element identifier.
   *
   * @return the filled element.
   */
  protected <T extends Model, I> ModelFieldContext<WeNetUserProfile, String, T, I, WeNetValidateContext> fillElementContext(
      final ModelFieldContext<WeNetUserProfile, String, T, I, WeNetValidateContext> element, final String name,
      final Class<T> type) {

    element.model = this.createProfileContext();
    element.name = name;
    element.type = type;
    element.validateContext = element.model.validateContext;
    return element;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfilePlannedActivity(final Boolean storeProfileChangesInHistory, final String userId,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String, WeNetValidateContext>(),
        "plannedActivities", PlannedActivity.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities,
        (profile, plannedActivities) -> profile.plannedActivities = plannedActivities,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilePlannedActivities(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilePlannedActivity(final String userId, final String plannedActivityId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String, WeNetValidateContext>(),
        "plannedActivities", PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(), context);

  }

  /**
   * Return the search for a plannedActivity.
   *
   * @return the function to obtain a plannedActivity in a list of
   *         plannedActivities.
   */
  private BiFunction<List<PlannedActivity>, String, Integer> searchProfilePlannedActivity() {

    return ModelResources.searchElementById((plannedActivity, id) -> id != null && id.equals(plannedActivity.id));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfilePlannedActivity(final Boolean storeProfileChangesInHistory, final String userId,
      final String plannedActivityId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String, WeNetValidateContext>(),
        "plannedActivities", PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;

    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfilePlannedActivity(final Boolean storeProfileChangesInHistory, final String userId,
      final String plannedActivityId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String, WeNetValidateContext>(),
        "plannedActivities", PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;

    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfilePlannedActivity(final Boolean storeProfileChangesInHistory, final String userId,
      final String plannedActivityId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String, WeNetValidateContext>(),
        "plannedActivities", PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileRelevantLocation(final Boolean storeProfileChangesInHistory, final String userId,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String, WeNetValidateContext>(),
        "relevant_location", RelevantLocation.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations,
        (profile, relevantLocations) -> profile.relevantLocations = relevantLocations,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileRelevantLocations(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileRelevantLocation(final String userId, final String relevantLocationId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String, WeNetValidateContext>(),
        "relevantLocations", RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(), context);

  }

  /**
   * Return the search for a relevant location.
   *
   * @return the function to obtain a relevant location in a list of relevant
   *         locations.
   */
  private BiFunction<List<RelevantLocation>, String, Integer> searchProfileRelevantLocation() {

    return ModelResources.searchElementById((relevantLocation, id) -> id != null && id.equals(relevantLocation.id));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileRelevantLocation(final Boolean storeProfileChangesInHistory, final String userId,
      final String relevantLocationId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String, WeNetValidateContext>(),
        "relevantLocations", RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;

    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileRelevantLocation(final Boolean storeProfileChangesInHistory, final String userId,
      final String relevantLocationId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String, WeNetValidateContext>(),
        "relevantLocations", RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;

    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileRelevantLocation(final Boolean storeProfileChangesInHistory, final String userId,
      final String relevantLocationId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String, WeNetValidateContext>(),
        "relevantLocations", RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;

    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfilePersonalBehavior(final Boolean storeProfileChangesInHistory, final String userId,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Routine, Integer, WeNetValidateContext>(), "personalBehaviors",
        Routine.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors,
        (profile, personalBehaviours) -> profile.personalBehaviors = personalBehaviours,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilePersonalBehaviors(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilePersonalBehavior(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Routine, Integer, WeNetValidateContext>(), "personalBehaviors",
        Routine.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfilePersonalBehavior(final Boolean storeProfileChangesInHistory, final String userId,
      final int index, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Routine, Integer, WeNetValidateContext>(), "personalBehaviors",
        Routine.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfilePersonalBehavior(final Boolean storeProfileChangesInHistory, final String userId,
      final int index, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Routine, Integer, WeNetValidateContext>(), "personalBehaviors",
        Routine.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfilePersonalBehavior(final Boolean storeProfileChangesInHistory, final String userId,
      final int index, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Routine, Integer, WeNetValidateContext>(), "personalBehaviors",
        Routine.class);
    element.id = index;
    element.model.id = userId;

    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileMaterial(final Boolean storeProfileChangesInHistory, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Material, Integer, WeNetValidateContext>(), "materials",
        Material.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, (profile, materials) -> profile.materials = materials,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileMaterials(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileMaterial(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Material, Integer, WeNetValidateContext>(), "materials",
        Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileMaterial(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Material, Integer, WeNetValidateContext>(), "materials",
        Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileMaterial(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Material, Integer, WeNetValidateContext>(), "materials",
        Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileMaterial(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Material, Integer, WeNetValidateContext>(), "materials",
        Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileCompetence(final Boolean storeProfileChangesInHistory, final String userId,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Competence, Integer, WeNetValidateContext>(), "competences",
        Competence.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, (profile, competences) -> profile.competences = competences,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileCompetences(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileCompetence(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Competence, Integer, WeNetValidateContext>(), "competences",
        Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileCompetence(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Competence, Integer, WeNetValidateContext>(), "competences",
        Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileCompetence(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Competence, Integer, WeNetValidateContext>(), "competences",
        Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileCompetence(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Competence, Integer, WeNetValidateContext>(), "competences",
        Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileMeaning(final Boolean storeProfileChangesInHistory, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer, WeNetValidateContext>(), "meanings",
        Meaning.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, (profile, meanings) -> profile.meanings = meanings,
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileMeanings(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileMeaning(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer, WeNetValidateContext>(), "meanings",
        Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileMeaning(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer, WeNetValidateContext>(), "meanings",
        Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileMeaning(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final JsonObject body, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer, WeNetValidateContext>(), "meanings",
        Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(body, element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileMeaning(final Boolean storeProfileChangesInHistory, final String userId, final int index,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var storeChanges = this.calculateStoreChanges(storeProfileChangesInHistory);
    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer, WeNetValidateContext>(), "meanings",
        Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.profilesRepository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.profilesRepository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(storeChanges, element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilesPage(final int offset, final int limit, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModelsPage(offset, limit, (page, promise) -> this.profilesRepository
        .retrieveProfilesPageObject(page.offset, page.limit, search -> promise.handle(search)), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isProfileDefined(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.checkModelExist(model,
        (modelId, handler) -> this.profilesRepository.searchProfile(modelId).onComplete(handler), context);

  }

}
