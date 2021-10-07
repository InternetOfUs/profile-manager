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

package eu.internetofus.wenet_profile_manager.api.trusts;

import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Contains information that rates the performance of an user over a task that
 * it has done in WeNet.
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
   * The time when this event is reported. It is measured as the difference,
   * measured in seconds, between the time when its was reported and midnight,
   * January 1, 1970 UTC.
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

      promise
          .fail(new ValidationErrorException(codePrefix + ".rating", "The rating value has to be in the range [0,1]."));

    } else if (this.sourceId.equals(this.targetId)) {

      promise.fail(new ValidationErrorException(codePrefix + ".targetId",
          "The 'targetId' can not be the same as the 'sourceId'."));

    } else {

      if (this.appId != null) {

        future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true,
            WeNetService.createProxy(vertx)::retrieveApp);

      }

      if (this.communityId != null) {

        future = Validations.composeValidateId(future, codePrefix, "communityId", this.communityId, true,
            WeNetProfileManager.createProxy(vertx)::retrieveCommunity);

      }

      if (this.taskTypeId != null) {

        future = Validations.composeValidateId(future, codePrefix, "taskTypeId", this.taskTypeId, true,
            WeNetTaskManager.createProxy(vertx)::retrieveTaskType);

      }

      if (this.taskId != null) {

        future = future.compose(mapper -> {

          final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
          WeNetTaskManager.createProxy(vertx).retrieveTask(this.taskId).onComplete(retrieve -> {

            if (retrieve.failed()) {

              verifyNotRepeatedIdPromise.fail(
                  new ValidationErrorException(codePrefix + ".taskId", "The '" + this.taskId + "' is not defined."));

            } else {

              final var task = retrieve.result();
              if (this.appId != null && !this.appId.equals(task.appId)) {

                verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".appId",
                    "The '" + this.appId + "' is not associated to the task '" + this.taskId + "'."));

              } else if (this.taskTypeId != null && !this.taskTypeId.equals(task.taskTypeId)) {

                verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".taskTypeId",
                    "The '" + this.taskTypeId + "' is not associated to the task '" + this.taskId + "'."));

              } else {

                verifyNotRepeatedIdPromise.complete();

              }
            }

          });
          return verifyNotRepeatedIdPromise.future();
        });

      }

      future = future.compose(mapper -> {

        final Promise<Void> verifyRequesterIdExistPromise = Promise.promise();
        WeNetProfileManager.createProxy(vertx).retrieveProfile(this.sourceId).onComplete(search -> {

          if (search.failed()) {

            verifyRequesterIdExistPromise.fail(new ValidationErrorException(codePrefix + ".sourceId",
                "The '" + this.sourceId + "' is not defined.", search.cause()));

          } else {

            final var profile = search.result();
            var validRelationship = true;
            if (this.relationship != null) {

              validRelationship = false;
              if (profile.relationships != null) {

                for (final SocialNetworkRelationship relation : profile.relationships) {

                  if (relation.type == this.relationship && relation.userId.equals(this.targetId)
                      && relation.appId.equals(this.appId)) {
                    // exist relation between them
                    validRelationship = true;
                    break;
                  }
                }
              }
            }
            if (!validRelationship) {

              verifyRequesterIdExistPromise.fail(new ValidationErrorException(
                  codePrefix + ".relationship", "The '" + this.relationship + "' is not defined on the '" + this.appId
                      + "' by the source user '" + this.sourceId + "' with the target user '" + this.targetId + "'.",
                  search.cause()));

            } else {

              verifyRequesterIdExistPromise.complete();
            }

          }
        });
        return verifyRequesterIdExistPromise.future();
      });

      future = Validations.composeValidateId(future, codePrefix, "targetId", this.targetId, true,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      promise.complete();

    }
    return future;

  }

}
