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

import java.util.function.Function;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A relationship with another user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A social relationship with another WeNet user.")
public class SocialNetworkRelationship extends Model {

	/**
	 * The identifier of the wenet user the relationship is related to.
	 */
	@Schema(
			description = "The identifier of the wenet user the relationship is related to",
			example = "4c51ee0b-b7ec-4577-9b21-ae6832656e33")
	public String userId;

	/**
	 * The relationship type.
	 */
	@Schema(description = "The relationship type", example = "friend")
	public SocialNetworkRelationshipType type;

	/**
	 * Create a new empty relationship.
	 */
	public SocialNetworkRelationship() {

	}

	/**
	 * Create a relation with the values of another.
	 *
	 * @param relation to copy.
	 */
	public SocialNetworkRelationship(SocialNetworkRelationship relation) {

		this.userId = relation.userId;
		this.type = relation.type;
	}

	/**
	 * Verify that the relationship is right.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param repository to the defined user profiles.
	 *
	 * @return the future to check if the model is right.
	 */
	public Future<Void> validate(String codePrefix, ProfilesRepository repository) {

		try {

			if (this.type == null) {

				return Future.failedFuture(new ValidationErrorException(codePrefix + ".type",
						"It is not allowed a social relationship without a type'."));

			}

			this.userId = Validations.validateNullableStringField(codePrefix, "userId", 255, this.userId);
			if (this.userId == null) {

				return Future.failedFuture(new ValidationErrorException(codePrefix + ".userId",
						"It is not allowed a social relationship without an user identifier'."));

			} else {

				return Future.succeededFuture().compose((Function<Object, Future<Void>>) map -> {
					final Promise<Void> promise = Promise.promise();
					repository.searchProfile(this.userId, search -> {

						if (search.result() != null) {

							promise.complete();

						} else {

							promise.fail(new ValidationErrorException(codePrefix + ".userId",
									"Does not exist any user identifier by '" + this.userId + "'."));
						}

					});
					return promise.future();
				});
			}

		} catch (final Throwable throwable) {

			return Future.failedFuture(throwable);
		}

	}
}
