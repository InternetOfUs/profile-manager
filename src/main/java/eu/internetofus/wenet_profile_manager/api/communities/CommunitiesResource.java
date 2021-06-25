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

package eu.internetofus.wenet_profile_manager.api.communities;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.models.CommunityMember;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.SocialPractice;
import eu.internetofus.common.vertx.ModelContext;
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
  protected ModelContext<CommunityProfile, String> createCommunityContext() {

    final var context = new ModelContext<CommunityProfile, String>();
    context.name = "community";
    context.type = CommunityProfile.class;
    return context;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createCommunity(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.createModel(this.vertx, body, model, this.repository::storeCommunity, context);

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
    ModelResources.retrieveModel(model, this.repository::searchCommunity, context);

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
    ModelResources.updateModelChain(this.vertx, body, model, this.repository::searchCommunity,
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
    ModelResources.mergeModelChain(this.vertx, body, model, this.repository::searchCommunity,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, SocialPractice, String>(), "socialPractices",
        SocialPractice.class);
    element.model.id = id;
    ModelResources.createModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
  protected <T extends Model, I> ModelFieldContext<CommunityProfile, String, T, I> fillElementContext(
      final ModelFieldContext<CommunityProfile, String, T, I> element, final String name, final Class<T> type) {

    element.model = this.createCommunityContext();
    element.name = name;
    element.type = type;
    return element;
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
    ModelResources.retrieveModelField(model, this.repository::searchCommunity, community -> community.socialPractices,
        context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunitySocialPractice(final String id, final String socialPracticeId,
      final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, SocialPractice, String>(), "socialPractices",
        SocialPractice.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.retrieveModelFieldElement(element, this.repository::searchCommunity,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, SocialPractice, String>(), "socialPractices",
        SocialPractice.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.deleteModelFieldElement(element, this.repository::searchCommunity,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, SocialPractice, String>(), "socialPractices",
        SocialPractice.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.updateModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, SocialPractice, String>(), "socialPractices",
        SocialPractice.class);
    element.model.id = id;
    element.id = socialPracticeId;
    ModelResources.mergeModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = id;
    ModelResources.createModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
    ModelResources.retrieveModelField(model, this.repository::searchCommunity, community -> community.norms, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityNorm(final String id, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = id;
    element.id = index;
    ModelResources.retrieveModelFieldElement(element, this.repository::searchCommunity, community -> community.norms,
        ModelResources.searchElementByIndex(), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunityNorm(final String id, final int index, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = id;
    element.id = index;
    ModelResources.deleteModelFieldElement(element, this.repository::searchCommunity, community -> community.norms,
        ModelResources.searchElementByIndex(), this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunityNorm(final String id, final int index, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = id;
    element.id = index;
    ModelResources.updateModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
        community -> community.norms, ModelResources.searchElementByIndex(), this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunityNorm(final String id, final int index, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, ProtocolNorm, Integer>(), "norms", ProtocolNorm.class);
    element.model.id = id;
    element.id = index;
    ModelResources.mergeModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
        community -> community.norms, ModelResources.searchElementByIndex(), this.repository::updateCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCommunityMember(final String id, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, CommunityMember, String>(), "members", CommunityMember.class);
    element.model.id = id;
    ModelResources.createModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
    ModelResources.retrieveModelField(model, this.repository::searchCommunity, community -> community.members, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityMember(final String id, final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, CommunityMember, String>(), "members", CommunityMember.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.retrieveModelFieldElement(element, this.repository::searchCommunity, community -> community.members,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, CommunityMember, String>(), "members", CommunityMember.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.deleteModelFieldElement(element, this.repository::searchCommunity, community -> community.members,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, CommunityMember, String>(), "members", CommunityMember.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.updateModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
    final var element = this.fillElementContext(
        new ModelFieldContext<CommunityProfile, String, CommunityMember, String>(), "members", CommunityMember.class);
    element.model.id = id;
    element.id = userId;
    ModelResources.mergeModelFieldElement(this.vertx, body, element, this.repository::searchCommunity,
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
