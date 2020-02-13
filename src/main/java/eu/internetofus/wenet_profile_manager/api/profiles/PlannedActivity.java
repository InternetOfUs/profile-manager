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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.function.Function;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * An activity planned by an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "An activity planned by an user.")
public class PlannedActivity extends Model {

	/**
	 * The identifier of the activity.
	 */
	@Schema(description = "The identifier of the activity", example = "hfdsfs888")

	public String id;

	/**
	 * The starting time of the activity.
	 */
	@Schema(description = "The starting time of the activity", example = "2017-07-21T17:32:00Z")
	public String startTime;

	/**
	 * The ending time of the activity.
	 */
	@Schema(description = "The ending time of the activity", example = "2019-07-21T17:32:23Z")
	public String endTime;

	/**
	 * The description of the activity.
	 */
	@Schema(description = "The description of the activity", example = "A few beers for relaxing")
	public String description;

	/**
	 * The identifier of other wenet user taking part to the activity.
	 */
	@ArraySchema(
			schema = @Schema(implementation = String.class),
			arraySchema = @Schema(
					description = "The identifier of other wenet user taking part to the activity",
					example = "[15d85f1d-b1ce-48de-b221-bec9ae954a88]"))
	public List<String> attendees;

	/**
	 * The current status of the activity.
	 */
	@Schema(description = "The current status of the activity", example = "confirmed")
	public PlannedActivityStatus status;

	/**
	 * Create an empty activity.
	 */
	public PlannedActivity() {

	}

	/**
	 * Verify that the planned activity is right.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param repository to the defined user profiles.
	 *
	 * @return the future to check if the model is right.
	 */
	public Future<Void> validate(String codePrefix, final ProfilesRepository repository) {

		try {

			final Promise<Void> promise = Promise.promise();
			Future<Void> future = promise.future();

			this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
			if (this.id != null) {

				return Future.failedFuture(new ValidationErrorException(codePrefix + ".id",
						"You can not specify the identifier of the planned activity to create"));

			} else {

				this.id = UUID.randomUUID().toString();
			}
			this.startTime = Validations.validateNullableDateField(codePrefix, "startTime", DateTimeFormatter.ISO_INSTANT,
					this.startTime);
			this.endTime = Validations.validateNullableDateField(codePrefix, "endTime", DateTimeFormatter.ISO_INSTANT,
					this.endTime);
			this.description = Validations.validateNullableStringField(codePrefix, "description", 255, this.description);
			if (this.attendees != null && !this.attendees.isEmpty()) {

				for (final ListIterator<String> ids = this.attendees.listIterator(); ids.hasNext();) {

					final int index = ids.nextIndex();
					final String id = Validations.validateNullableStringField(codePrefix, "attendees[" + index + "]", 255,
							ids.next());
					if (id == null) {

						ids.remove();

					} else {

						future = future.compose((Function<Void, Future<Void>>) map -> {

							final Promise<Void> searchPromise = Promise.promise();
							repository.searchProfile(id, search -> {
								if (search.result() != null) {

									searchPromise.complete();

								} else {

									searchPromise.fail(new ValidationErrorException(codePrefix + ".attendees[" + index + "]",
											"Does not exist an user with the profile identifier '" + id + "'."));
								}

							});

							return searchPromise.future();
						});
					}

				}

			}

			promise.complete();
			return future;

		} catch (

		final Throwable throwable) {

			return Future.failedFuture(throwable);
		}

	}

}
