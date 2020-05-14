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

package eu.internetofus.wenet_profile_manager.api.trusts;

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import eu.internetofus.common.api.models.wenet.SocialNetworkRelationship;
import eu.internetofus.common.api.models.wenet.SocialNetworkRelationshipType;
import eu.internetofus.common.api.models.wenet.WeNetUserProfile;
import eu.internetofus.common.services.WeNetProfileManagerService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Contains information that rates the performance of an user over a task that
 * it has done in WeNet.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The event is used to rate the performance of an user over a task that it has done in WeNet.")
public class UserPerformanceRatingEvent extends Model {

	/**
	 * The identifier of the user that provide the performance.
	 */
	@Schema(
			name = "sourceId",
			description = "The identifier of the user that is providing the performance of another user.",
			example = "88cb96277edd")
	public String sourceId;

	/**
	 * The identifier of the user that has perform the action.
	 */
	@Schema(
			name = "targetId",
			description = "The identifier of the user that has perform the task.",
			example = "bf2743937ed6")
	public String targetId;

	/**
	 * The relationship with the user.
	 */
	@Schema(description = "The relationship with the user that has perform the task", example = "friend")
	public SocialNetworkRelationshipType relationship;

	/**
	 * The identifier of the community where the user has performed the action.
	 */
	@Schema(
			description = "The identifier of the community where the user has performed the task",
			example = "43937ed6ty32")
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
	@Schema(
			description = "The difference, measured in seconds, between the time when this event was reported and midnight, January 1, 1970 UTC.",
			example = "1571412479710")
	public long reportTime;

	/**
	 * Called when want to verify if a trust event is right.
	 *
	 * @param codePrefix      prefix for the error code.
	 * @param vertx           the infrastructure for the event bus to use.
	 * @param validateHandler the handler to manage the validation result.
	 */
	public void validate(String codePrefix, Vertx vertx, Handler<AsyncResult<Void>> validateHandler) {

		if (this.rating == null) {

			validateHandler.handle(Future
					.failedFuture(new ValidationErrorException(codePrefix + ".value", "You must define the rating value.")));

		} else if (this.rating < 0d || this.rating > 1d) {

			validateHandler.handle(Future.failedFuture(
					new ValidationErrorException(codePrefix + ".value", "The rating value has to be in the range [0,1].")));

		} else {

			try {
				this.sourceId = Validations.validateStringField(codePrefix, "sourceId", 255, this.sourceId);
				this.targetId = Validations.validateStringField(codePrefix, "targetId", 255, this.targetId);
				this.communityId = Validations.validateNullableStringField(codePrefix, "communityId", 255, this.communityId);
				this.taskTypeId = Validations.validateNullableStringField(codePrefix, "taskTypeId", 255, this.taskTypeId);
				if (this.sourceId.equals(this.targetId)) {

					validateHandler.handle(Future.failedFuture(new ValidationErrorException(codePrefix + ".targetId",
							"You cannot provide a trust event over the same user.")));

				} else {
					final WeNetProfileManagerService profileManager = WeNetProfileManagerService.createProxy(vertx);
					profileManager.retrieveProfile(this.sourceId, searchSource -> {

						if (searchSource.failed()) {

							validateHandler.handle(Future.failedFuture(new ValidationErrorException(codePrefix + ".sourceId",
									"The user that will provide the trust is not defined.")));

						} else {

							profileManager.retrieveProfile(this.targetId, searchTarget -> {

								if (searchTarget.failed()) {

									validateHandler.handle(Future.failedFuture(new ValidationErrorException(codePrefix + ".targetId",
											"The user that the trust refers to is not defined.")));

								} else {

									final WeNetUserProfile source = searchSource.result();
									if (this.relationship != null && (source.relationships == null || !source.relationships
											.contains(new SocialNetworkRelationship(this.relationship, this.targetId)))) {

										validateHandler
												.handle(Future.failedFuture(new ValidationErrorException(codePrefix + ".relationship",
														"The target user does not have the specified relationship with the source.")));

									} else {

										final Promise<Void> promise = Promise.promise();
										final Future<Void> future = promise.future();
										if (this.communityId != null) {

											// future = future.compose(mapper -> {
											//
											// final WeNetInteractionProtocolEngineService interactionProtocolEngineService
											// = WeNetInteractionProtocolEngineService
											// .createProxy(vertx);
											// interactionProtocolEngineService.retrieveCommunity(this.communityId, found ->
											// {
											//
											// // if( found.failed)
											//
											// });
											//
											// return Future.succeededFuture();
											//
											// });

										}

										validateHandler.handle(future);
									}

								}
							});

						}
					});

				}

			} catch (final ValidationErrorException error) {

				validateHandler.handle(Future.failedFuture(error));

			}
		}
	}

}
