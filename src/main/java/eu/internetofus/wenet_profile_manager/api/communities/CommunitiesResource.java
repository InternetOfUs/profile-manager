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

import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
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
   * Create an empty resource. This is only used for unit tests.
   */
  protected CommunitiesResource() {

  }

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
   * {@inheritDoc}
   */
  @Override
  public void createCommunity(final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final CommunityProfile community = Model.fromJsonObject(body, CommunityProfile.class);
    if (community == null) {

      Logger.debug("The {} is not a valid CommunityProfile.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_community_profile", "The community profile is not right.");

    } else {

      community.validate("bad_community_profile", this.vertx).onComplete(validation -> {

        if (validation.failed()) {

          final Throwable cause = validation.cause();
          Logger.debug(cause, "The {} is not valid.", community);
          OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          this.repository.storeCommunity(community, stored -> {

            if (stored.failed()) {

              final Throwable cause = validation.cause();
              Logger.debug(cause, "Cannot store {}.", community);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

            } else {

              final CommunityProfile storedCommunity = stored.result();
              OperationReponseHandlers.responseOk(resultHandler, storedCommunity);

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
  public void retrieveCommunity(final String id, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.repository.searchCommunityObject(id, search -> {

      final JsonObject profile = search.result();
      if (profile == null) {

        Logger.debug(search.cause(), "Not found community associated to the identifier {}", id);
        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_community", "Does not exist a community associated for the identifier '" + id + "'.");

      } else {

        OperationReponseHandlers.responseOk(resultHandler, profile);

      }
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(final String id, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final CommunityProfile source = Model.fromJsonObject(body, CommunityProfile.class);
    if (source == null) {

      Logger.debug("The {} is not a valid CommunityProfile to update.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_community_to_update", "The community to update is not right.");

    } else {

      this.repository.searchCommunity(id, search -> {

        final CommunityProfile target = search.result();
        if (target == null) {

          Logger.debug(search.cause(), "Not found community {} to update", id);
          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_community_to_update", "You can not update the community associated to the identifier '" + id + "', because it does not exist.");

        } else {

          source.id = null;
          source.validate("bad_new_community", this.vertx).onComplete(validate -> {

            if (validate.failed()) {

              final Throwable cause = validate.cause();
              Logger.debug(cause, "Cannot update {} with {}.", target, source);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

            } else {

              source.id = target.id;
              source._creationTs = target._creationTs;
              source._lastUpdateTs = target._lastUpdateTs;
              if (source.equals(target)) {

                OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "community_to_update_equal_to_original",
                    "You can not update the community of the '" + id + "', because the new values is equals to the current one.");

              } else {

                this.repository.updateCommunity(source, update -> {

                  if (update.failed()) {

                    final Throwable cause = update.cause();
                    Logger.debug(cause, "Cannot update {}.", target);
                    OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

                  } else {

                    source._creationTs = target._lastUpdateTs;
                    source._lastUpdateTs = TimeManager.now();
                    OperationReponseHandlers.responseOk(resultHandler, source);

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
  public void mergeCommunity(final String id, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final CommunityProfile source = Model.fromJsonObject(body, CommunityProfile.class);
    if (source == null) {

      Logger.debug("The {} is not a valid CommunityProfile to merge.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_community_to_merge", "The community to merge is not right.");

    } else {

      this.repository.searchCommunity(id, search -> {

        final CommunityProfile target = search.result();
        if (target == null) {

          Logger.debug(search.cause(), "Not found community {} to merge", id);
          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_community_to_merge", "You can not merge the community associated to the identifier '" + id + "', because it does not exist.");

        } else {

          target.merge(source, "bad_community", this.vertx).onComplete(merge -> {

            if (merge.failed()) {

              final Throwable cause = merge.cause();
              Logger.debug(cause, "Cannot merge {} with {}.", target, source);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

            } else {

              final CommunityProfile merged = merge.result();
              if (merged.equals(target)) {

                OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "community_to_merge_equal_to_original",
                    "You can not merge the community of the '" + id + "', because the new values is equals to the current one.");

              } else {

                this.repository.updateCommunity(merged, update -> {

                  if (update.failed()) {

                    final Throwable cause = update.cause();
                    Logger.debug(cause, "Cannot merge {}.", merged);
                    OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

                  } else {

                    target._lastUpdateTs = TimeManager.now();
                    OperationReponseHandlers.responseOk(resultHandler, merged);

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
  public void deleteCommunity(final String id, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.repository.deleteCommunity(id, delete -> {

      if (delete.failed()) {

        final Throwable cause = delete.cause();
        Logger.debug(cause, "Cannot delete the community {}.", id);
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.NOT_FOUND, cause);

      } else {

        OperationReponseHandlers.responseOk(resultHandler);
      }

    });

  }

  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // public void addSocialPractice(final String userId, final JsonObject body, final OperationRequest context, final
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
  // public void retrieveSocialPractices(final String userId, final OperationRequest context, final
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
  // OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {
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
  // OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {
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
  // public void deleteSocialPractice(final String userId, final String socialPracticeId, final OperationRequest context,
  // final Handler<AsyncResult<OperationResponse>> resultHandler) {
  //
  // this.deleteModelFromProfile(userId, "social_practice", profile -> profile.socialPractices,
  // this.searchModelIndexById(socialPractice -> socialPractice.id.equals(socialPracticeId)), resultHandler);
  //
  // }

}
