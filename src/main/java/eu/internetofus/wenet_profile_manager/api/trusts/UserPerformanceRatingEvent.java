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

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Contains information that rates the performance of an user over a task that
 * it has done in WeNet.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The event is used to rate the performance of an user over a task that it has done in WeNet.")
public class UserPerformanceRatingEvent extends ReflectionModel implements Model, Validable<WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    context.validateNumberOnRangeField("rating", this.rating, 0.0d, 1.0d, promise);
    this.sourceId = context.normalizeString(this.sourceId);
    if (this.sourceId == null) {

      return context.failField("sourceId", "You must define the identifier of teh user that provide the rating");
    }
    future = future
        .compose(empty -> context.validateDefinedProfileByIdField("sourceId", this.sourceId).transform(search -> {

          if (search.failed()) {

            return context.failField("sourceId", "The '" + this.sourceId + "' is not defined.", search.cause());

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

              return context.failField("relationship",
                  "The '" + this.relationship + "' is not defined on the '" + this.appId + "' by the source user '"
                      + this.sourceId + "' with the target user '" + this.targetId + "'.");

            } else {

              return Future.succeededFuture();
            }

          }

        }));

    this.targetId = context.normalizeString(this.targetId);
    if (this.targetId == null) {

      return context.failField("targetId", "You must define the identifier of the user that provide the rating");

    } else if (this.sourceId.equals(this.targetId)) {

      return context.failField("targetId", "The 'targetId' can not be the same as the 'sourceId'.");
    }
    future = context.validateDefinedProfileIdField("targetId", this.targetId, future);

    if (this.appId != null) {

      future = context.validateDefinedAppIdField("appId", this.appId, future);
    }
    if (this.communityId != null) {

      future = context.validateDefinedCommunityIdField("communityId", this.communityId, future);
    }
    if (this.taskTypeId != null) {

      future = context.validateDefinedTaskTypeIdField("taskTypeId", this.taskTypeId, future);
    }
    if (this.taskId != null) {
      future = future.compose(empty -> context.validateDefinedTaskByIdField("taskId", this.taskId).transform(search -> {

        if (search.failed()) {

          return context.failField("taskId", "The '" + this.taskId + "' is not defined.", search.cause());

        } else {

          final var task = search.result();
          if (this.appId != null && !this.appId.equals(task.appId)) {

            return context.failField("appId",
                "The '" + this.appId + "' is not associated to the task '" + this.taskId + "'.");

          } else if (this.taskTypeId != null && !this.taskTypeId.equals(task.taskTypeId)) {

            return context.failField("taskTypeId",
                "The '" + this.taskTypeId + "' is not associated to the task '" + this.taskId + "'.");

          } else {

            return Future.succeededFuture();

          }

        }

      }));
    }

    promise.tryComplete();

    return future;

  }

}
