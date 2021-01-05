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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
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
    ModelResources.retrieveModel(model, this.repository::searchProfile, context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createProfileContext();
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.createModelChain(this.vertx, body, model, this.repository::storeProfile, context, () -> {

      ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, model.value);

      // Update the social context of the created user
      WeNetSocialContextBuilder.createProxy(this.vertx).retrieveSocialRelations(model.value.id).onComplete(retrieve -> {

        if (retrieve.failed()) {

          Logger.trace(retrieve.cause(), "Cannot update the social relations of {}.", () -> model.value.id);

        } else {

          Logger.trace("Obtained for the user {} the next social relations {}.", () -> model.value.id,
              () -> retrieve.result());
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
    ModelResources.updateModelChain(this.vertx, body, model, this.repository::searchProfile,
        this.repository::updateProfile, context,
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
    ModelResources.mergeModelChain(this.vertx, body, model, this.repository::searchProfile,
        this.repository::updateProfile, context,
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
    ModelResources.retrieveModelChain(model, this.repository::searchProfile, context,
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
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Norm, String>(),
        "norms", Norm.class);
    element.model.id = userId;
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.norms, (profile, norms) -> profile.norms = norms, this.repository::updateProfile, context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

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
  public void retrieveProfileNorms(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createProfileContext();
    model.id = userId;
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.norms, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileNorm(final String userId, final String normId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Norm, String>(),
        "norms", Norm.class);
    element.model.id = userId;
    element.id = normId;
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile, profile -> profile.norms,
        this.searchProfileNorm(), context);

  }

  /**
   * Return the search for a norm.
   *
   * @return the function to obtain a norm in a list of norms.
   */
  private BiFunction<List<Norm>, String, Integer> searchProfileNorm() {

    return ModelResources.searchElementById((norm, id) -> id != null && id.equals(norm.id));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfileNorm(final String userId, final String normId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Norm, String>(),
        "norms", Norm.class);
    element.model.id = userId;
    element.id = normId;

    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.norms, this.searchProfileNorm(), this.repository::updateProfile, context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeProfileNorm(final String userId, final String normId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Norm, String>(),
        "norms", Norm.class);
    element.model.id = userId;
    element.id = normId;

    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.norms, this.searchProfileNorm(), this.repository::updateProfile, context,
        this.addProfileToHistoricChain(element.model,
            () -> ServiceResponseHandlers.responseOk(resultHandler, element.value)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfileNorm(final String userId, final String normId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<WeNetUserProfile, String, Norm, String>(),
        "norms", Norm.class);
    element.model.id = userId;
    element.id = normId;
    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile, profile -> profile.norms,
        this.searchProfileNorm(), this.repository::updateProfile, context,
        this.addProfileToHistoricChain(element.model, () -> ServiceResponseHandlers.responseOk(resultHandler)));

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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.plannedActivities,
        (profile, plannedActivities) -> profile.plannedActivities = plannedActivities, this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.plannedActivities,
        context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile,
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

    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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

    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile,
        profile -> profile.plannedActivities, this.searchProfilePlannedActivity(), this.repository::updateProfile,
        context,
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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.relevantLocations,
        (profile, relevantLocations) -> profile.relevantLocations = relevantLocations, this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.relevantLocations,
        context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile,
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

    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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

    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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

    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile,
        profile -> profile.relevantLocations, this.searchProfileRelevantLocation(), this.repository::updateProfile,
        context,
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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.relationships, (profile, relationships) -> profile.relationships = relationships,
        this.repository::updateProfile, context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.relationships, context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile, profile -> profile.relationships,
        ModelResources.searchElementByIndex(), context);

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
    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.relationships, ModelResources.searchElementByIndex(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.relationships, ModelResources.searchElementByIndex(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile,
        profile -> profile.relationships, ModelResources.searchElementByIndex(), this.repository::updateProfile,
        context,
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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.personalBehaviors,
        (profile, personalBehaviours) -> profile.personalBehaviors = personalBehaviours, this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.personalBehaviors,
        context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile,
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
    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(), this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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

    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile,
        profile -> profile.personalBehaviors, ModelResources.searchElementByIndex(), this.repository::updateProfile,
        context,
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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.materials, (profile, materials) -> profile.materials = materials,
        this.repository::updateProfile, context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.materials, context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile, profile -> profile.materials,
        ModelResources.searchElementByIndex(), context);

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
    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.materials, ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.materials, ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile, profile -> profile.materials,
        ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.competences, (profile, competences) -> profile.competences = competences,
        this.repository::updateProfile, context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.competences, context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile, profile -> profile.competences,
        ModelResources.searchElementByIndex(), context);

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
    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.competences, ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.competences, ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile, profile -> profile.competences,
        ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.createModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.meanings, (profile, meanings) -> profile.meanings = meanings, this.repository::updateProfile,
        context, this.addProfileToHistoricChain(element.model,
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
    ModelResources.retrieveModelField(model, this.repository::searchProfile, profile -> profile.meanings, context);

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
    ModelResources.retrieveModelFieldElement(element, this.repository::searchProfile, profile -> profile.meanings,
        ModelResources.searchElementByIndex(), context);

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
    ModelResources.updateModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.meanings, ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.mergeModelFieldElementChain(this.vertx, body, element, this.repository::searchProfile,
        profile -> profile.meanings, ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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
    ModelResources.deleteModelFieldElementChain(element, this.repository::searchProfile, profile -> profile.meanings,
        ModelResources.searchElementByIndex(), this.repository::updateProfile, context,
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

}
