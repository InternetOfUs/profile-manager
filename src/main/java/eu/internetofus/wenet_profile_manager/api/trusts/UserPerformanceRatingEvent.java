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

package eu.internetofus.wenet_profile_manager.api.trusts;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationship;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipType;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Contains information that rates the performance of an user over a task that it has done in WeNet.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The event is used to rate the performance of an user over a task that it has done in WeNet.")
public class UserPerformanceRatingEvent extends ReflectionModel implements Model, Validable {

  /**
   * The identifier of the user that provide the performance.
   */
  @Schema(name = "sourceId", description = "The identifier of the user that is providing the performance of another user.", example = "88cb96277edd")
  public String sourceId;

  /**
   * The identifier of the user that has perform the action.
   */
  @Schema(name = "targetId", description = "The identifier of the user that has perform the task.", example = "bf2743937ed6")
  public String targetId;

  /**
   * The relationship with the user.
   */
  @Schema(description = "The relationship with the user that has perform the task", example = "friend")
  public SocialNetworkRelationshipType relationship;

  /**
   * The identifier of the application where the user has performed the action.
   */
  @Schema(description = "The identifier of the application where the user has performed the task", example = "7ens723h6ty32")
  public String appId;

  /**
   * The identifier of the community where the user has performed the action.
   */
  @Schema(description = "The identifier of the community where the user has performed the task", example = "43937ed6ty32")
  public String communityId;

  /**
   * The identifier of task type that the rating user has done.
   */
  @Schema(description = "The identifier of task type that the rating user has done.", example = "4ty37ed63932")
  public String taskTypeId;

  /**
   * The identifier of task that the rating user has done.
   */
  @Schema(description = "The identifier of task that the rating user has done.", example = "d64ty39s7e32")
  public String taskId;

  /**
   * The rating of the user performance. It has to be on the range {@code [0,1]}.
   */
  @Schema(description = "The rating of the user performance. It has to be on the range [0,1].", example = "0.43")
  public Double rating;

  /**
   * The time when this event is reported. It is measured as the difference, measured in seconds, between the time when
   * its was reported and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The difference, measured in seconds, between the time when this event was reported and midnight, January 1, 1970 UTC.", example = "1571412479710")
  public long reportTime;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.rating == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".rating", "You must define the rating value."));

    } else if (this.rating < 0d || this.rating > 1d) {

      promise.fail(new ValidationErrorException(codePrefix + ".rating", "The rating value has to be in the range [0,1]."));

    } else {

      try {

        this.sourceId = Validations.validateStringField(codePrefix, "sourceId", 255, this.sourceId);
        this.targetId = Validations.validateStringField(codePrefix, "targetId", 255, this.targetId);
        this.appId = Validations.validateNullableStringField(codePrefix, "appId", 255, this.appId);
        this.communityId = Validations.validateNullableStringField(codePrefix, "communityId", 255, this.communityId);
        this.taskTypeId = Validations.validateNullableStringField(codePrefix, "taskTypeId", 255, this.taskTypeId);
        this.taskId = Validations.validateNullableStringField(codePrefix, "taskId", 255, this.taskId);
        if (this.sourceId.equals(this.targetId)) {

          promise.fail(new ValidationErrorException(codePrefix + ".targetId", "The 'targetId' can not be the same as the 'sourceId'."));

        } else {

          future = future.compose(mapper -> {

            final Promise<Void> verifyRequesterIdExistPromise = Promise.promise();
            WeNetProfileManager.createProxy(vertx).retrieveProfile(this.sourceId, search -> {

              if (search.failed()) {

                verifyRequesterIdExistPromise.fail(new ValidationErrorException(codePrefix + ".sourceId", "The '" + this.sourceId + "' is not defined.", search.cause()));

              } else {

                final var profile = search.result();
                var validRelationship = true;
                if (this.relationship != null) {

                  validRelationship = false;
                  if (profile.relationships != null) {

                    for (final SocialNetworkRelationship relation : profile.relationships) {

                      if (relation.type == this.relationship && relation.userId.equals(this.targetId)) {
                        // exist relation between them
                        validRelationship = true;
                        break;
                      }
                    }
                  }
                }
                if (!validRelationship) {

                  verifyRequesterIdExistPromise.fail(
                      new ValidationErrorException(codePrefix + ".relationship", "The '" + this.relationship + "' is not defined by the source user '" + this.sourceId + "' with the target user '" + this.targetId + "'.", search.cause()));

                } else {

                  verifyRequesterIdExistPromise.complete();
                }

              }
            });
            return verifyRequesterIdExistPromise.future();
          });
          future = future.compose(mapper -> {

            final Promise<Void> verifyRequesterIdExistPromise = Promise.promise();
            WeNetProfileManager.createProxy(vertx).retrieveProfile(this.targetId, search -> {

              if (!search.failed()) {

                verifyRequesterIdExistPromise.complete();

              } else {

                verifyRequesterIdExistPromise.fail(new ValidationErrorException(codePrefix + ".targetId", "The '" + this.targetId + "' is not defined.", search.cause()));
              }
            });
            return verifyRequesterIdExistPromise.future();
          });

          if (this.appId != null) {

            future = future.compose(mapper -> {

              final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
              WeNetService.createProxy(vertx).retrieveApp(this.appId, app -> {

                if (!app.failed()) {

                  verifyNotRepeatedIdPromise.complete();

                } else {

                  verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".appId", "The '" + this.appId + "' is not defined."));
                }
              });
              return verifyNotRepeatedIdPromise.future();
            });

          }

          // if( this.communityId != null ) {
          //
          // future = future.compose(mcommunityer -> {
          //
          // final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
          // WeNetService.createProxy(vertx).retrieveCommunity(this.communityId, community -> {
          //
          // if (!community.failed()) {
          //
          // verifyNotRepeatedIdPromise.complete();
          //
          // } else {
          //
          // verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".communityId", "The '" + this.communityId
          // + "' is not defined."));
          // }
          // });
          // return verifyNotRepeatedIdPromise.future();
          // });
          //
          // }

          if (this.taskTypeId != null) {

            future = future.compose(mapper -> {

              final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
              WeNetTaskManager.createProxy(vertx).retrieveTaskType(this.taskTypeId, taskType -> {

                if (!taskType.failed()) {

                  verifyNotRepeatedIdPromise.complete();

                } else {

                  verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".taskTypeId", "The '" + this.taskTypeId + "' is not defined."));
                }
              });
              return verifyNotRepeatedIdPromise.future();
            });

          }

          if (this.taskId != null) {

            future = future.compose(mapper -> {

              final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
              WeNetTaskManager.createProxy(vertx).retrieveTask(this.taskId, retrieve -> {

                if (retrieve.failed()) {

                  verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".taskId", "The '" + this.taskId + "' is not defined."));

                } else {

                  final var task = retrieve.result();
                  if (this.appId != null && !this.appId.equals(task.appId)) {

                    verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".appId", "The '" + this.appId + "' is not associated to the task '" + this.taskId + "'."));

                  } else if (this.taskTypeId != null && !this.taskTypeId.equals(task.taskTypeId)) {

                    verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".taskTypeId", "The '" + this.taskTypeId + "' is not associated to the task '" + this.taskId + "'."));

                  } else {

                    verifyNotRepeatedIdPromise.complete();

                  }
                }

              });
              return verifyNotRepeatedIdPromise.future();
            });

          }

          promise.complete();
        }

      } catch (final ValidationErrorException validationError) {

        promise.fail(validationError);
      }
    }
    return future;

  }

}
