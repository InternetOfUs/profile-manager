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

import eu.internetofus.common.components.models.Competence;
import eu.internetofus.common.components.models.Material;
import eu.internetofus.common.components.models.Meaning;
import eu.internetofus.common.components.models.PlannedActivity;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.RelevantLocation;
import eu.internetofus.common.components.models.Routine;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.social_context_builder.ProfileUpdateNotification;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilder;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.TimeManager;
import eu.internetofus.common.vertx.ModelContext;
import eu.internetofus.common.vertx.ModelFieldContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
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
  protected ProfilesRepository repository;

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
   * Create the profile context.
   *
   * @return the context of the {@link WeNetUserProfile}.
   */
  protected ModelContext<WeNetUserProfile, String> createProfileContext() {

    final var context = new ModelContext<WeNetUserProfile, String>();
    context.name = "profile";
    context.type = WeNetUserProfile.class;
    return context;

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
    ModelResources.retrieveModel(model, (id, handler) -> this.repository.searchProfile(id).onComplete(handler),
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
    ModelResources.createModelChain(this.vertx, body, model,
        (profile, handler) -> this.repository.storeProfile(profile).onComplete(handler), context, () -> {

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
        });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.updateModelChain(this.vertx, body, model,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context, false,
        this.addProfileToHistoricChain(model, () -> ServiceResponseHandlers.responseOk(resultHandler, model.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfile(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.mergeModelChain(this.vertx, body, model,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context, false,
        this.addProfileToHistoricChain(model, () -> ServiceResponseHandlers.responseOk(resultHandler, model.value)));
  }

  /**
   * Create the function to add a profile to the historic.
   *
   * @param model   context with the profile to add.
   * @param success function to call if the profile has added.
   *
   * @return the function to call to store the profile into the historic.
   */
  protected Runnable addProfileToHistoricChain(final ModelContext<WeNetUserProfile, String> model,
      final Runnable success) {

    return () -> {

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

      notification.updatedFieldNames.remove("_creationTs");
      notification.updatedFieldNames.remove("_lastUpdateTs");

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

      final var historic = new HistoricWeNetUserProfile();
      historic.from = model.target._lastUpdateTs;
      historic.to = TimeManager.now();
      historic.profile = model.target;
      this.repository.storeHistoricProfile(historic).onComplete(store -> {

        if (store.failed()) {

          Logger.debug(store.cause(), "Cannot store the profile {} as historic.", historic);
        }
        if (model.value != null) {

          model.value._lastUpdateTs = historic.to;
        }
        success.run();
      });
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModelChain(model,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), context,
        () -> ModelResources.deleteModelChain(model, this.repository::deleteProfile, context,
            this.addProfileToHistoricChain(model, () -> ServiceResponseHandlers.responseOk(resultHandler))));

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
    this.repository.searchHistoricProfilePage(query, sort, offset, limit).onComplete(search -> {

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
  public void addProfileNorm(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, String>(),
        "norm", ProtocolNorm.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), profile -> profile.norms,
        (profile, norms) -> profile.norms = norms,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), profile -> profile.norms,
        context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileNorm(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), profile -> profile.norms,
        ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileNorm(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), profile -> profile.norms,
        ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileNorm(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), profile -> profile.norms,
        ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileNorm(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler), profile -> profile.norms,
        ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

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
  protected <T extends Model, I> ModelFieldContext<WeNetUserProfile, String, T, I> fillElementContext(
      final ModelFieldContext<WeNetUserProfile, String, T, I> element, final String name, final Class<T> type) {

    element.model = this.createProfileContext();
    element.name = name;
    element.type = type;
    return element;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfilePlannedActivity(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String>(), "plannedActivities",
        PlannedActivity.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities,
        (profile, plannedActivities) -> profile.plannedActivities = plannedActivities,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
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
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String>(), "plannedActivities",
        PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
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
  public void updateProfilePlannedActivity(final String userId, final String plannedActivityId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String>(), "plannedActivities",
        PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;

    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfilePlannedActivity(final String userId, final String plannedActivityId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String>(), "plannedActivities",
        PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;

    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfilePlannedActivity(final String userId, final String plannedActivityId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, PlannedActivity, String>(), "plannedActivities",
        PlannedActivity.class);
    element.model.id = userId;
    element.id = plannedActivityId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileRelevantLocation(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String>(), "relevant_location",
        RelevantLocation.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations,
        (profile, relevantLocations) -> profile.relevantLocations = relevantLocations,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
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
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String>(), "relevantLocations",
        RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
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
  public void updateProfileRelevantLocation(final String userId, final String relevantLocationId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String>(), "relevantLocations",
        RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;

    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileRelevantLocation(final String userId, final String relevantLocationId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String>(), "relevantLocations",
        RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;

    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileRelevantLocation(final String userId, final String relevantLocationId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, RelevantLocation, String>(), "relevantLocations",
        RelevantLocation.class);
    element.model.id = userId;
    element.id = relevantLocationId;

    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileRelationship(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, SocialNetworkRelationship, String>(), "relationship",
        SocialNetworkRelationship.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relationships, (profile, relationships) -> profile.relationships = relationships,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileRelationships(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relationships, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileRelationship(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, SocialNetworkRelationship, Integer>(), "relationships",
        SocialNetworkRelationship.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relationships, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileRelationship(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, SocialNetworkRelationship, Integer>(), "relationships",
        SocialNetworkRelationship.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relationships, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileRelationship(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, SocialNetworkRelationship, Integer>(), "relationships",
        SocialNetworkRelationship.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relationships, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileRelationship(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<WeNetUserProfile, String, SocialNetworkRelationship, Integer>(), "relationships",
        SocialNetworkRelationship.class);
    element.model.id = userId;
    element.id = index;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.relationships, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfilePersonalBehavior(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Routine, Integer>(),
        "personalBehaviors", Routine.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors,
        (profile, personalBehaviours) -> profile.personalBehaviors = personalBehaviours,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilePersonalBehavior(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Routine, Integer>(),
        "personalBehaviors", Routine.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfilePersonalBehavior(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Routine, Integer>(),
        "personalBehaviors", Routine.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfilePersonalBehavior(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Routine, Integer>(),
        "personalBehaviors", Routine.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfilePersonalBehavior(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Routine, Integer>(),
        "personalBehaviors", Routine.class);
    element.id = index;
    element.model.id = userId;

    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileMaterial(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Material, Integer>(),
        "materials", Material.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, (profile, materials) -> profile.materials = materials,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileMaterial(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Material, Integer>(),
        "materials", Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileMaterial(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Material, Integer>(),
        "materials", Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileMaterial(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Material, Integer>(),
        "materials", Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileMaterial(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Material, Integer>(),
        "materials", Material.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.materials, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileCompetence(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Competence, Integer>(),
        "competences", Competence.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, (profile, competences) -> profile.competences = competences,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileCompetence(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Competence, Integer>(),
        "competences", Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileCompetence(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Competence, Integer>(),
        "competences", Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileCompetence(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Competence, Integer>(),
        "competences", Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileCompetence(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Competence, Integer>(),
        "competences", Competence.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.competences, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addProfileMeaning(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer>(),
        "meanings", Meaning.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, (profile, meanings) -> profile.meanings = meanings,
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
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
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileMeaning(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer>(),
        "meanings", Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileMeaning(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer>(),
        "meanings", Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.updateModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileMeaning(final String userId, final int index, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer>(),
        "meanings", Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileMeaning(final String userId, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Meaning, Integer>(),
        "meanings", Meaning.class);
    element.id = index;
    element.model.id = userId;
    ModelResources.deleteModelFieldElementChain(element,
        (profileId, handler) -> this.repository.searchProfile(profileId).onComplete(handler),
        profile -> profile.meanings, ModelResources.searchElementByIndex(),
        (profile, handler) -> this.repository.updateProfile(profile).onComplete(handler), context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilesPage(final int offset, final int limit, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModelsPage(offset, limit, (page, promise) -> this.repository
        .retrieveProfilesPageObject(page.offset, page.limit, search -> promise.handle(search)), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOrUpdateProfileRelationship(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    this.repository.searchProfile(userId).onComplete(search -> {

      final var profile = search.result();
      if (profile != null && profile.relationships != null) {

        final var newRelationship = Model.fromJsonObject(body, SocialNetworkRelationship.class);
        if (newRelationship != null) {

          final var max = profile.relationships.size();
          for (var index = 0; index < max; index++) {

            final var relationship = profile.relationships.get(index);
            if (relationship.equalsByAppUserAndType(newRelationship)) {

              this.mergeProfileRelationship(userId, index, body, request, resultHandler);
              return;
            }
          }
        }
      }

      // not defined => add
      final var context = new ServiceContext(request, resultHandler);
      final var element = this.fillElementContext(
          new ModelFieldContext<WeNetUserProfile, String, SocialNetworkRelationship, String>(), "relationship",
          SocialNetworkRelationship.class);
      element.model.id = userId;
      ModelResources.createModelFieldElementChain(this.vertx, body, element, (any, handler) -> handler.handle(search),
          model -> model.relationships, (model, relationships) -> model.relationships = relationships,
          (model, handler) -> this.repository.updateProfile(model).onComplete(handler), context,
          this.addProfileToHistoricChain(element.model,
              () -> ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, element.value)));

    });

  }

}
