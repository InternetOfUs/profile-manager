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
import eu.internetofus.wenet_profile_manager.api.profiles.SocialNetworkRelationship;
import eu.internetofus.wenet_profile_manager.api.profiles.SocialNetworkRelationshipType;
import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Contains information of the trust of an user respect another.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The information of the trust of an user respect another.")
public class TrustEvent extends Model {

	/**
	 * The identifier of the user that provide the trust.
	 */
	@Schema(
			name = "sourceId",
			description = "The identifier of the user that is providing the trust of another user.",
			example = "88cb96277edd")
	public String sourceId;

	/**
	 * The identifier of the user that the source user provide the trust of it.
	 */
	@Schema(
			name = "targetId",
			description = "The identifier of the user that the trust refers to.",
			example = "bf2743937ed6")
	public String targetId;

	/**
	 * The relationship with the user.
	 */
	@Schema(description = "The relationship with the user")
	public SocialNetworkRelationshipType relationship;

	/**
	 * The identifier of the community that the trust refers
	 */
	@Schema(description = "The identifier of the community that the trust refers")
	public String communityId;

	/**
	 * The type of task that this trusts refers
	 */
	@Schema(description = "The type of task that this trusts refers")
	public String taskTypeId;

	/**
	 * The trust over the user. It has to be on the range {@code [0,1]}.
	 */
	@Schema(description = "The trust over the user. It has to be on the range [0,1].", example = "0.43")
	public Double value;

	/**
	 * The time when the trust happens. It is measured as the difference, measured
	 * in seconds, between the time when the trust even was reported and midnight,
	 * January 1, 1970 UTC.
	 */
	@Schema(
			description = "The difference, measured in seconds, between the time when the trust even was reported and midnight, January 1, 1970 UTC.",
			example = "1571412479710")
	public long reportTime;

	/**
	 * Called when want to verify if a trust event is right.
	 *
	 * @param codePrefix        prefix for the error code.
	 * @param profileRepository used to get data to verify the profile.
	 * @param validateHandler   the handler to manage the validation result.
	 */
	public void validate(String codePrefix, ProfilesRepository profileRepository,
			Handler<AsyncResult<Void>> validateHandler) {

		if (this.value == null) {

			validateHandler.handle(
					Future.failedFuture(new ValidationErrorException(codePrefix + ".value", "You must define the trust value.")));

		} else if (this.value < 0d || this.value > 1d) {

			validateHandler.handle(Future.failedFuture(
					new ValidationErrorException(codePrefix + ".value", "The trust value has to be in the range [0,1].")));

		} else {

			try {
				this.sourceId = Validations.validateStringField(codePrefix, "source", 255, this.sourceId);
				this.targetId = Validations.validateStringField(codePrefix, "target", 255, this.targetId);
				this.communityId = Validations.validateNullableStringField(codePrefix, "communityId", 255, this.communityId);
				this.taskTypeId = Validations.validateNullableStringField(codePrefix, "taskTypeId", 255, this.taskTypeId);
				if (this.sourceId.equals(this.targetId)) {

					validateHandler.handle(Future.failedFuture(new ValidationErrorException(codePrefix + ".targetId",
							"You cannot provide a trust event over the same user.")));

				} else {
					profileRepository.searchProfile(this.sourceId, searchSource -> {

						if (searchSource.failed()) {

							validateHandler.handle(Future.failedFuture(new ValidationErrorException(codePrefix + ".sourceId",
									"The user that will provide the trust is not defined.")));

						} else {

							profileRepository.searchProfile(this.targetId, searchTarget -> {

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

										// TODO check other components as communityId,tasktype,...
										validateHandler.handle(Future.succeededFuture());
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
