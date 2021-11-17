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

package eu.internetofus.wenet_profile_manager.api.communities;

import eu.internetofus.common.components.WeNetModelContext;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.CommunityMember;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.SocialPractice;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.vertx.ModelFieldContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.common.vertx.ServiceRequests;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

/**
 * Resource that provide the methods for the {@link Communities}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesResource implements Communities {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * The repository to manage the communities.
   */
  protected CommunitiesRepository repository;

  /**
   * Create a new instance to provide the services of the {@link Communities}.
   *
   * @param vertx with the event bus to use.
   */
  public CommunitiesResource(final Vertx vertx) {

    this.vertx = vertx;
    this.repository = CommunitiesRepository.createProxy(vertx);

  }

  /**
   * Create the community context.
   *
   * @return the context of the {@link CommunityProfile}.
   */
  protected WeNetModelContext<CommunityProfile, String> createCommunityContext() {

    return WeNetModelContext.creteWeNetContext("community", CommunityProfile.class, this.vertx);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createCommunity(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.createModel(body, model, this.repository::storeCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunity(final String id, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModel(model,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(final String id, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.updateModelChain(body, model,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        this.repository::updateCommunity, context, () -> {

          ServiceResponseHandlers.responseOk(resultHandler, model.value);

        });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunity(final String id, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.mergeModelChain(body, model,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        this.repository::updateCommunity, context, () -> {

          ServiceResponseHandlers.responseOk(resultHandler, model.value);

        });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new ServiceContext(request, resultHandler);

    ModelResources.deleteModel(model, this.repository::deleteCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCommunitySocialPractice(final String id, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("socialPractices", SocialPractice.class, String.class);
    element.model.id = id;
    ModelResources.createModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.socialPractices,
        (community, socialPractices) -> community.socialPractices = socialPractices, this.repository::updateCommunity,
        context);

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
  protected <T extends Model, I> ModelFieldContext<CommunityProfile, String, T, I, WeNetValidateContext> fillElementContext(
      final ModelFieldContext<CommunityProfile, String, T, I, WeNetValidateContext> element, final String name,
      final Class<T> type) {

    element.model = this.createCommunityContext();
    element.name = name;
    element.type = type;
    element.validateContext = element.model.validateContext;
    return element;
  }

  /**
   * Create a {@link ModelFieldContext} the necessaries values.
   *
   * @param name   for the element.
   * @param type   for the element.
   * @param idType type for the key.
   *
   * @param <T>    class for the element.
   * @param <I>    class for the element identifier.
   *
   * @return the filled element.
   */
  protected <T extends Model, I> ModelFieldContext<CommunityProfile, String, T, I, WeNetValidateContext> createElementContext(
      final String name, final Class<T> type, final Class<I> idType) {

    final var context = new ModelFieldContext<CommunityProfile, String, T, I, WeNetValidateContext>();
    this.fillElementContext(context, name, type);
    return context;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunitySocialPractices(final String id, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createCommunityContext();
    model.id = id;
    ModelResources.retrieveModelField(model,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.socialPractices, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunitySocialPractice(final String id, final String socialPracticeId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("socialPractices", SocialPractice.class, String.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.retrieveModelFieldElement(element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.socialPractices,
        ModelResources.searchElementById((socialPractice, searchId) -> socialPractice.id.equals(searchId)), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunitySocialPractice(final String id, final String socialPracticeId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(new ModelFieldContext<>(), "socialPractices", SocialPractice.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.deleteModelFieldElement(element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.socialPractices,
        ModelResources.searchElementById((socialPractice, searchId) -> socialPractice.id.equals(searchId)),
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunitySocialPractice(final String id, final String socialPracticeId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("socialPractices", SocialPractice.class, String.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.updateModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.socialPractices,
        ModelResources.searchElementById((socialPractice, searchId) -> socialPractice.id.equals(searchId)),
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunitySocialPractice(final String id, final String socialPracticeId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("socialPractices", SocialPractice.class, String.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.mergeModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.socialPractices,
        ModelResources.searchElementById((socialPractice, searchId) -> socialPractice.id.equals(searchId)),
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCommunityNorm(final String id, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("norms", ProtocolNorm.class, Integer.class);
    element.model.id = id;
    ModelResources.createModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.norms, (community, norms) -> community.norms = norms, this.repository::updateCommunity,
        context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityNorms(final String id, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createCommunityContext();
    model.id = id;
    ModelResources.retrieveModelField(model,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.norms, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityNorm(final String id, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("norms", ProtocolNorm.class, Integer.class);
    element.model.id = id;
    element.id = index;
    ModelResources.retrieveModelFieldElement(element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.norms, ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunityNorm(final String id, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("norms", ProtocolNorm.class, Integer.class);
    element.model.id = id;
    element.id = index;
    ModelResources.deleteModelFieldElement(element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.norms, ModelResources.searchElementByIndex(), this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunityNorm(final String id, final int index, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("norms", ProtocolNorm.class, Integer.class);
    element.model.id = id;
    element.id = index;
    ModelResources.updateModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.norms, ModelResources.searchElementByIndex(), this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunityNorm(final String id, final int index, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("norms", ProtocolNorm.class, Integer.class);
    element.model.id = id;
    element.id = index;
    ModelResources.mergeModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.norms, ModelResources.searchElementByIndex(), this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCommunityMember(final String id, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("members", CommunityMember.class, String.class);
    element.model.id = id;
    ModelResources.createModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.members, (community, communityMembers) -> community.members = communityMembers,
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityMembers(final String id, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = this.createCommunityContext();
    model.id = id;
    ModelResources.retrieveModelField(model,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.members, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityMember(final String id, final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("members", CommunityMember.class, String.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.retrieveModelFieldElement(element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.members,
        ModelResources.searchElementById((communityMember, searchId) -> communityMember.userId.equals(searchId)),
        context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunityMember(final String id, final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("members", CommunityMember.class, String.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.deleteModelFieldElement(element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.members,
        ModelResources.searchElementById((communityMember, searchId) -> communityMember.userId.equals(searchId)),
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunityMember(final String id, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("members", CommunityMember.class, String.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.updateModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.members,
        ModelResources.searchElementById((communityMember, searchId) -> communityMember.userId.equals(searchId)),
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunityMember(final String id, final String userId, final JsonObject body,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.createElementContext("members", CommunityMember.class, String.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.mergeModelFieldElement(body, element,
        (communityId, handler) -> this.repository.searchCommunity(communityId).onComplete(handler),
        community -> community.members,
        ModelResources.searchElementById((communityMember, searchId) -> communityMember.userId.equals(searchId)),
        this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityProfilesPage(final String appId, final String name, final String description,
      final String keywordsValue, final String membersValue, final String orderValue, final int offset, final int limit,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var keywords = ServiceRequests.extractQueryArray(keywordsValue);
    final var members = ServiceRequests.extractQueryArray(membersValue);
    final var order = ServiceRequests.extractQueryArray(orderValue);
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModelsPage(offset, limit, (page, promise) -> {

      page.query = CommunitiesRepository.createCommunityProfilesPageQuery(appId, name, description, keywords, members);
      page.sort = CommunitiesRepository.createCommunityProfilesPageSort(order);
      this.repository.retrieveCommunityProfilesPageObject(page, search -> promise.handle(search));

    }, context);

  }

}
