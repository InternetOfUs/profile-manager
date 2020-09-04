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

package eu.internetofus.wenet_profile_manager.api.communities;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.vertx.ModelContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.OperationContext;
import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

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
  public void createCommunity(final JsonObject body, final OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    final var context = new OperationContext(request, resultHandler);
    ModelResources.createModel(this.vertx, body, model, this.repository::storeCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunity(final String id, final OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new OperationContext(request, resultHandler);
    ModelResources.retrieveModel(model, this.repository::searchCommunity, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(final String id, final JsonObject body, final OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new OperationContext(request, resultHandler);
    ModelResources.updateModelChain(this.vertx, body, model, this.repository::searchCommunity, this.repository::updateCommunity, context, () -> {

      model.value._lastUpdateTs = TimeManager.now();
      OperationReponseHandlers.responseOk(resultHandler, model.value);

    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunity(final String id, final JsonObject body, final OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new OperationContext(request, resultHandler);
    ModelResources.mergeModelChain(this.vertx, body, model, this.repository::searchCommunity, this.repository::updateCommunity, context, () -> {

      model.value._lastUpdateTs = TimeManager.now();
      OperationReponseHandlers.responseOk(resultHandler, model.value);

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var model = this.createCommunityContext();
    model.id = id;
    final var context = new OperationContext(request, resultHandler);

    ModelResources.deleteModel(model, this.repository::deleteCommunity, context);

  }

  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void addSocialPractice(final String userId, final JsonObject body, final OperationRequest request, final
  // Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.addModelToProfile(SocialPractice.class, body, "social_practice", userId, profile -> profile.socialPractices,
  // (profile, socialPractices) -> profile.socialPractices = socialPractices,
  // (socialPractice1, socialPractice2) -> socialPractice1.id.equals(socialPractice2.id), resultHandler);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void retrieveSocialPractices(final String userId, final OperationRequest request, final
  // Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.retrieveModelsFromProfile(userId, profile -> profile.socialPractices, resultHandler);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void retrieveSocialPractice(final String userId, final String socialPracticeId, final OperationRequest
  // context, final Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.retrieveModelFromProfile(userId, this.searchModelById(profile -> profile.socialPractices, socialPractice ->
  // socialPractice.id.equals(socialPracticeId)), "social_practice", resultHandler);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void updateSocialPractice(final String userId, final String socialPracticeId, final JsonObject body, final
  // OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.updateModelFromProfile(userId, body.put("id", socialPracticeId), SocialPractice.class, profile ->
  // profile.socialPractices, this.searchModelIndexById(socialPractice -> socialPractice.id.equals(socialPracticeId)),
  // "social_practice",
  // resultHandler);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void mergeSocialPractice(final String userId, final String socialPracticeId, final JsonObject body, final
  // OperationRequest request, final Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.mergeModelFromProfile(userId, body.put("id", socialPracticeId), SocialPractice.class, profile ->
  // profile.socialPractices, this.searchModelIndexById(socialPractice -> socialPractice.id.equals(socialPracticeId)),
  // "social_practice",
  // resultHandler);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void deleteSocialPractice(final String userId, final String socialPracticeId, final OperationRequest request,
  // final Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.deleteModelFromProfile(userId, "social_practice", profile -> profile.socialPractices,
  // this.searchModelIndexById(socialPractice -> socialPractice.id.equals(socialPracticeId)), resultHandler);
  //
  // }

}
