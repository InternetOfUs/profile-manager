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

import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilder;
import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
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

    this.repository.searchProfileObject(userId, search -> {

      final JsonObject profile = search.result();
      if (profile == null) {

        Logger.debug(search.cause(), "Not found profile for the user {}", userId);
        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_profile", "Does not exist a profile associated for the user '" + userId + "'.");

      } else {

        OperationReponseHandlers.responseOk(resultHandler, profile);

      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final WeNetUserProfile profile = Model.fromJsonObject(body, WeNetUserProfile.class);
    if (profile == null) {

      Logger.debug("The {} is not a valid WeNetUserProfile.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_profile", "The profile is not right.");

    } else {

      profile.validate("bad_profile", this.vertx).onComplete(validation -> {

        if (validation.failed()) {

          final Throwable cause = validation.cause();
          Logger.debug(cause, "The {} is not valid.", profile);
          OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          this.repository.storeProfile(profile, stored -> {

            if (stored.failed()) {

              final Throwable cause = validation.cause();
              Logger.debug(cause, "Cannot store {}.", profile);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

            } else {

              final WeNetUserProfile storedProfile = stored.result();
              OperationReponseHandlers.responseOk(resultHandler, storedProfile);

              // Update the social context of the created user
              WeNetSocialContextBuilder.createProxy(this.vertx).retrieveJsonArraySocialRelations(storedProfile.id, retrieve -> {

                if (retrieve.failed()) {

                  Logger.trace(retrieve.cause(), "Cannot update the social relations of {}.", storedProfile);

                } else {

                  Logger.trace("Obtained for the user {} the next social relations {}.", () -> storedProfile.id, () -> retrieve.result());
                }
              });
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
  public void updateProfile(final String userId, final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final WeNetUserProfile source = Model.fromJsonObject(body, WeNetUserProfile.class);
    if (source == null) {

      Logger.debug("The {} is not a valid WeNetUserProfile to update.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_profile_to_update", "The profile to update is not right.");

    } else {

      this.repository.searchProfile(userId, search -> {

        final WeNetUserProfile target = search.result();
        if (target == null) {

          Logger.debug(search.cause(), "Not found profile {} to update", userId);
          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_profile_to_update", "You can not update the profile of the user '" + userId + "', because it does not exist.");

        } else {

          target.merge(source, "bad_new_profile", this.vertx).onComplete(merge -> {

            if (merge.failed()) {

              final Throwable cause = merge.cause();
              Logger.debug(cause, "Cannot update {} with {}.", target, source);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

            } else {

              final WeNetUserProfile merged = merge.result();
              if (merged.equals(target)) {

                OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "profile_to_update_equal_to_original",
                    "You can not update the profile of the user '" + userId + "', because the new values is equals to the current one.");

              } else {
                this.repository.updateProfile(merged, update -> {

                  if (update.failed()) {

                    final Throwable cause = update.cause();
                    Logger.debug(cause, "Cannot update {}.", target);
                    OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

                  } else {

                    final HistoricWeNetUserProfile historic = new HistoricWeNetUserProfile();
                    historic.from = target._lastUpdateTs;
                    historic.to = TimeManager.now();
                    historic.profile = target;
                    this.repository.storeHistoricProfile(historic, store -> {

                      if (store.failed()) {

                        Logger.debug(store.cause(), "Cannot store the updated profile as historic.");
                      }
                      OperationReponseHandlers.responseOk(resultHandler, merged);

                    });

                  }

                });
              }
            }
          }

              );

        }
      });
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    this.repository.deleteProfile(userId, delete -> {

      if (delete.failed()) {

        final Throwable cause = delete.cause();
        Logger.debug(cause, "Cannot delete the profile of the user {}.", userId);
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.NOT_FOUND, cause);

      } else {

        OperationReponseHandlers.responseOk(resultHandler);
      }

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileHistoricPage(final String userId, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final JsonObject params = context.getParams().getJsonObject("query", new JsonObject());
    final long from = params.getLong("from", 0l);
    final long to = params.getLong("to", Long.MAX_VALUE);
    final boolean ascending = "ASC".equals(params.getString("order", "ASC"));
    final int offset = params.getInteger("offset", 0);
    final int limit = params.getInteger("limit", 10);
    this.repository.searchHistoricProfilePage(userId, from, to, ascending, offset, limit, search -> {

      if (search.failed()) {

        final Throwable cause = search.cause();
        Logger.debug(cause, "Cannot found historic profile for the user {}.", userId);
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.NOT_FOUND, cause);

      } else {
        final HistoricWeNetUserProfilesPage page = search.result();
        if (page.total == 0l) {

          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "no_found", "Not found any historic profile that match to the specific parameters.");

        } else {
          OperationReponseHandlers.responseOk(resultHandler, page);

        }
      }
    });

  }

}
