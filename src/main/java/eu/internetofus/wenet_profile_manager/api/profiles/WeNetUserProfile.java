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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "WeNetUserProfile", description = "The profile of a WeNet user.")
public class WeNetUserProfile extends Model {

	/**
	 * The identifier of the profile.
	 */
	@Schema(description = "The identifier of the profile.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
	public String id;

	/**
	 * The name of the user.
	 */
	@Schema(description = "The name of the user.")
	public UserName name;

	/**
	 * The date of birth of the user.
	 */
	@Schema(description = "The date of birth of the user.")
	public ProfileDate dateOfBirth;

	/**
	 * The gender of the user.
	 */
	@Schema(description = "The gender of the user", example = "F")
	public Gender gender;

	/**
	 * The email of the user.
	 */
	@Schema(description = "The email of the user", example = "jonnyd@internetofus.eu")
	public String email;

	/**
	 * The phone number of the user, on the E.164 format (^\+?[1-9]\d{1,14}$).
	 */
	@Schema(
			description = "The phone number of the user, on the E.164 format(^\\+?[1-9]\\d{1,14}$)",
			example = "+34987654321")

	public String phoneNumber;

	/**
	 * The email of the user.
	 */
	@Schema(description = "The locale of the user", example = "es_ES")
	public String locale;

	/**
	 * The avatar of the user.
	 */
	@Schema(
			description = "The URL to an image that represents the avatar of the user.",
			example = "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png")
	public String avatar;

	/**
	 * The email of the user.
	 */
	@Schema(description = "The nationality of the user", example = "Spanish")
	public String nationality;

	/**
	 * The languages that the user can understand.
	 */
	@ArraySchema(
			schema = @Schema(implementation = Language.class),
			arraySchema = @Schema(description = "The languages that the user canunderstand"))
	public List<Language> languages;

	/**
	 * The email of the user.
	 */
	@Schema(description = "The occupation of the user", example = "nurse")
	public String occupation;

	/**
	 * The individual norms of the user
	 */
	@ArraySchema(
			schema = @Schema(implementation = Norm.class),
			arraySchema = @Schema(description = "The individual norms of the user"))
	public List<Norm> norms;

	/**
	 * The planned activities of the user.
	 */
	@ArraySchema(
			schema = @Schema(implementation = PlannedActivity.class),
			arraySchema = @Schema(description = "The planned activities of the user"))
	public List<PlannedActivity> plannedActivities;

	/**
	 * The locations of interest for the user.
	 */
	@ArraySchema(
			schema = @Schema(implementation = RelevantLocation.class),
			arraySchema = @Schema(description = "The locations of interest for theuser"))
	public List<RelevantLocation> relevantLocations;

	/**
	 * The user relationships.
	 */
	@ArraySchema(
			schema = @Schema(implementation = SocialNetworkRelationship.class),
			arraySchema = @Schema(description = "The user relationships with other WeNetusers."))
	public List<SocialNetworkRelationship> relationships;

	/**
	 * The user social practices.
	 */
	@ArraySchema(
			schema = @Schema(implementation = SocialPractice.class),
			arraySchema = @Schema(description = "The user social practices"))
	public List<SocialPractice> socialPractices;

	/**
	 * The user routines.
	 */
	@ArraySchema(
			schema = @Schema(implementation = Routine.class),
			arraySchema = @Schema(description = "The user routines"))
	public List<Routine> personalBehaviors;

	/**
	 * The instant of the creation.
	 */
	@Schema(description = "The time stamp representing the account creationinstant.", example = "1563871899")
	public long _creationTs;

	/**
	 * The instant of the last update.
	 */
	@Schema(description = "The time stamp representing the last update instant.", example = "1563898764")
	public long _lastUpdateTs;

	/**
	 * Create a new profile.
	 */
	public WeNetUserProfile() {

		this._creationTs = this._lastUpdateTs = System.currentTimeMillis();
	}

	/**
	 * Validate that the values of the model are right.
	 *
	 * @param codePrefix prefix for the error code.
	 * @param repository used to get data to verify the profile.
	 *
	 * @return a future that inform if the model is valid.
	 */
	public Future<Void> validate(String codePrefix, ProfilesRepository repository) {

		try {

			final Promise<Void> promise = Promise.promise();
			Future<Void> future = promise.future();
			this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
			if (this.id != null) {

				return Future.failedFuture(new ValidationErrorException(codePrefix + ".id",
						"You can not specify the identifier of the profile to create"));

			}
			if (this.name != null) {

				this.name.validate(codePrefix + ".name");
			}
			if (this.dateOfBirth != null) {

				this.dateOfBirth.validateAsBirthDate(codePrefix + ".dateOfBirth");

			}
			// Gender not verified because is a enumeration and this fix the possible values
			this.email = Validations.validateNullableEmailField(codePrefix, "email", this.email);
			this.locale = Validations.validateNullableLocaleField(codePrefix, "locale", this.locale);
			this.phoneNumber = Validations.validateNullableTelephoneField(codePrefix, "phoneNumber", this.locale,
					this.phoneNumber);
			this.avatar = Validations.validateNullableURLField(codePrefix, "avatar", this.avatar);
			this.nationality = Validations.validateNullableStringField(codePrefix, "nationality", 255, this.nationality);
			if (this.languages != null && !this.languages.isEmpty()) {

				final String codeLanguages = codePrefix + ".languages";
				for (int index = 0; index < this.languages.size(); index++) {

					final Language language = this.languages.get(index);
					language.validate(codeLanguages + "[" + index + "]");
				}

			}
			this.occupation = Validations.validateNullableStringField(codePrefix, "occupation", 255, this.occupation);
			if (this.norms != null && !this.norms.isEmpty()) {

				final String codeNorms = codePrefix + ".norms";
				for (int index = 0; index < this.norms.size(); index++) {

					final Norm norm = this.norms.get(index);
					norm.validate(codeNorms + "[" + index + "]");
				}

			}
			if (this.plannedActivities != null && !this.plannedActivities.isEmpty()) {

				final String codeActivities = codePrefix + ".plannedActivities";
				for (int i = 0; i < this.plannedActivities.size(); i++) {

					final int index = i;
					final PlannedActivity plannedActivity = this.plannedActivities.get(index);
					future = future.compose(map -> plannedActivity.validate(codeActivities + "[" + index + "]", repository));
				}

			}

			if (this.relevantLocations != null && !this.relevantLocations.isEmpty()) {

				final String codeLocations = codePrefix + ".relevantLocations";
				for (int index = 0; index < this.relevantLocations.size(); index++) {

					final RelevantLocation relevantLocation = this.relevantLocations.get(index);
					relevantLocation.validate(codeLocations + "[" + index + "]");
				}

			}

			if (this.relationships != null && !this.relationships.isEmpty()) {

				final String codeRelationships = codePrefix + ".relationships";
				for (int i = 0; i < this.relationships.size(); i++) {

					final int index = i;
					final SocialNetworkRelationship relationship = this.relationships.get(index);
					for (int j = i + 1; j < this.relationships.size(); j++) {

						if (relationship.equals(this.relationships.get(j))) {

							throw new ValidationErrorException(codeRelationships + "[" + j + "]",
									"This relationship is duplicated. It is equal to relationship[" + i + "].");
						}
					}

					future = future.compose(map -> relationship.validate(codeRelationships + "[" + index + "]", repository));
				}

			}

			if (this.socialPractices != null && !this.socialPractices.isEmpty()) {

				final String codeSocialPractices = codePrefix + ".socialPractices";
				for (int index = 0; index < this.socialPractices.size(); index++) {

					final SocialPractice socialPractice = this.socialPractices.get(index);
					socialPractice.validate(codeSocialPractices + "[" + index + "]");
				}

			}

			if (this.personalBehaviors != null && !this.personalBehaviors.isEmpty()) {

				final String codePersonalBehavious = codePrefix + ".personalBehaviors";
				for (int index = 0; index < this.personalBehaviors.size(); index++) {

					final Routine personalBehavior = this.personalBehaviors.get(index);
					personalBehavior.validate(codePersonalBehavious + "[" + index + "]");
				}

			}

			promise.complete();
			return future;

		} catch (final ValidationErrorException exception) {

			return Future.failedFuture(exception);
		}

	}

	/**
	 * Merge this model with another and check that is valid.
	 *
	 * @param codePrefix prefix for the error code.
	 * @param repository used to get data to verify the profile.
	 * @param source     model to get the values to merge.
	 *
	 * @return a future that provide the merged model or the error that explains why
	 *         can not be merged.
	 */
	public Future<WeNetUserProfile> merge(String codePrefix, ProfilesRepository repository, WeNetUserProfile source) {

		try {

			final Promise<WeNetUserProfile> promise = Promise.promise();
			Future<WeNetUserProfile> future = promise.future();

			final WeNetUserProfile merged = new WeNetUserProfile();
			merged.id = this.id;
			merged._creationTs = this._creationTs;
			merged._lastUpdateTs = this._lastUpdateTs;
			if (source.name != null) {

				source.name.validate(codePrefix + ".name");
				merged.name = source.name;

			} else {

				merged.name = this.name;
			}

			if (source.dateOfBirth != null) {

				source.dateOfBirth.validateAsBirthDate(codePrefix + ".dateOfBirth");
				merged.dateOfBirth = source.dateOfBirth;

			} else {

				merged.dateOfBirth = this.dateOfBirth;
			}
			merged.gender = this.gender;

			merged.email = Validations.validateNullableEmailField(codePrefix, "email", source.email);
			if (merged.email == null) {

				merged.email = this.email;
			}
			merged.locale = Validations.validateNullableLocaleField(codePrefix, "locale", source.locale);
			if (merged.locale == null) {

				merged.locale = this.locale;
			}
			merged.phoneNumber = Validations.validateNullableTelephoneField(codePrefix, "phoneNumber", merged.locale,
					source.phoneNumber);
			if (merged.phoneNumber == null) {

				merged.phoneNumber = this.phoneNumber;
			}
			merged.avatar = Validations.validateNullableURLField(codePrefix, "avatar", source.avatar);
			if (merged.avatar == null) {

				merged.avatar = this.avatar;
			}

			merged.nationality = Validations.validateNullableStringField(codePrefix, "nationality", 255, source.nationality);
			if (merged.nationality == null) {

				merged.nationality = this.nationality;
			}

			if (source.languages != null) {

				final String codeLanguages = codePrefix + ".languages";
				for (int index = 0; index < source.languages.size(); index++) {

					final Language language = source.languages.get(index);
					language.validate(codeLanguages + "[" + index + "]");
				}
				merged.languages = source.languages;

			} else {

				merged.languages = this.languages;
			}

			merged.occupation = Validations.validateNullableStringField(codePrefix, "occupation", 255, source.occupation);
			if (merged.occupation == null) {

				merged.occupation = this.occupation;
			}

			this.mergeNorms(source, codePrefix, merged);
			future = future.compose(map -> this.mergePlannedActivities(source, codePrefix, repository, merged));
			this.mergeRelevantLocations(source, codePrefix, merged);
			future = future.compose(map -> this.mergeRelationships(source, codePrefix, repository, merged));
			this.mergeSocialPractices(source, codePrefix, merged);
			this.mergePersonalBehaviors(source, codePrefix, merged);

			promise.complete(merged);
			return future;

		} catch (final ValidationErrorException exception) {

			return Future.failedFuture(exception);
		}
	}

	/**
	 * Merge the norms between this model and another.
	 *
	 * @param source     profile to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param merged     profile to merge the values.
	 *
	 * @throws ValidationErrorException if the profile is not right.
	 */
	protected void mergeNorms(WeNetUserProfile source, String codePrefix, WeNetUserProfile merged)
			throws ValidationErrorException {

		if (source.norms != null) {

			final Set<String> ids = new HashSet<String>();
			if (this.norms != null) {

				for (final Norm norm : this.norms) {

					ids.add(norm.id);
				}
			}
			final String codeNorms = codePrefix + ".norms";
			for (int index = 0; index < source.norms.size(); index++) {

				final String codeNorm = codeNorms + "[" + index + "]";
				final Norm norm = source.norms.get(index);
				if (norm.id == null) {

					norm.validate(codeNorm);

				} else if (!ids.remove(norm.id)) {

					throw new ValidationErrorException(codeNorm + ".id",
							"Does not exist a norm with the specified identifier or it is duplicated.");

				} else {

					final String id = norm.id;

					try {

						norm.id = null;
						norm.validate(codeNorm);

					} finally {

						norm.id = id;
					}
				}
			}
			merged.norms = source.norms;

		} else {

			merged.norms = this.norms;
		}
	}

	/**
	 * Merge the plannedActivities between this model and another.
	 *
	 * @param source     profile to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param repository used to get data to verify the profile.
	 * @param merged     profile to merge the values.
	 *
	 * @return the future used to verify the profile.
	 *
	 * @throws ValidationErrorException if the profile is not right.
	 */
	protected Future<WeNetUserProfile> mergePlannedActivities(WeNetUserProfile source, String codePrefix,
			ProfilesRepository repository, WeNetUserProfile merged) throws ValidationErrorException {

		final Promise<WeNetUserProfile> promise = Promise.promise();
		Future<WeNetUserProfile> future = promise.future();

		if (source.plannedActivities != null) {

			final Set<String> ids = new HashSet<String>();
			if (this.plannedActivities != null) {

				for (final PlannedActivity plannedActivity : this.plannedActivities) {

					ids.add(plannedActivity.id);
				}
			}
			final String codePlannedActivities = codePrefix + ".plannedActivities";
			for (int index = 0; index < source.plannedActivities.size(); index++) {

				final String codePlannedActivity = codePlannedActivities + "[" + index + "]";
				final PlannedActivity plannedActivity = source.plannedActivities.get(index);
				final String id = plannedActivity.id;
				if (id != null && !ids.remove(id)) {

					throw new ValidationErrorException(codePlannedActivity + ".id",
							"Does not exist a planned activity with the specified identifier or it is duplicated.");

				}
				future = future.compose(map -> {
					final Promise<WeNetUserProfile> activityPromise = Promise.promise();
					plannedActivity.id = null;
					plannedActivity.validate(codePlannedActivity, repository).onComplete(validation -> {

						if (id != null) {

							plannedActivity.id = id;
						}
						if (validation.failed()) {

							activityPromise.fail(validation.cause());

						} else {

							activityPromise.complete(merged);
						}
					});
					return activityPromise.future();
				});
			}
			merged.plannedActivities = source.plannedActivities;

		} else {

			merged.plannedActivities = this.plannedActivities;
		}

		promise.complete(merged);
		return future;
	}

	/**
	 * Merge the relevant locations between this model and another.
	 *
	 * @param source     profile to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param merged     profile to merge the values.
	 *
	 * @throws ValidationErrorException if the profile is not right.
	 */
	protected void mergeRelevantLocations(WeNetUserProfile source, String codePrefix, WeNetUserProfile merged)
			throws ValidationErrorException {

		if (source.relevantLocations != null) {

			final Set<String> ids = new HashSet<String>();
			if (this.relevantLocations != null) {

				for (final RelevantLocation relevantLocation : this.relevantLocations) {

					ids.add(relevantLocation.id);
				}
			}
			final String codeRelevantLocations = codePrefix + ".relevantLocations";
			for (int index = 0; index < source.relevantLocations.size(); index++) {

				final String codeRelevantLocation = codeRelevantLocations + "[" + index + "]";
				final RelevantLocation relevantLocation = source.relevantLocations.get(index);
				if (relevantLocation.id == null) {

					relevantLocation.validate(codeRelevantLocation);

				} else if (!ids.remove(relevantLocation.id)) {

					throw new ValidationErrorException(codeRelevantLocation + ".id",
							"Does not exist a norm with the specified identifier or it is duplicated.");

				} else {

					final String id = relevantLocation.id;

					try {

						relevantLocation.id = null;
						relevantLocation.validate(codeRelevantLocation);

					} finally {

						relevantLocation.id = id;
					}
				}
			}
			merged.relevantLocations = source.relevantLocations;

		} else {

			merged.relevantLocations = this.relevantLocations;
		}
	}

	/**
	 * Merge the relationships between this model and another.
	 *
	 * @param source     profile to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param repository used to get data to verify the profile.
	 * @param merged     profile to merge the values.
	 *
	 * @return the future used to verify the profile.
	 */
	protected Future<WeNetUserProfile> mergeRelationships(WeNetUserProfile source, String codePrefix,
			ProfilesRepository repository, WeNetUserProfile merged) {

		final Promise<WeNetUserProfile> promise = Promise.promise();
		Future<WeNetUserProfile> future = promise.future();

		if (source.relationships != null) {

			final String codeRelationships = codePrefix + ".relationships";
			for (int i = 0; i < source.relationships.size(); i++) {

				final int index = i;
				final SocialNetworkRelationship relationship = source.relationships.get(index);
				future = future.compose(map -> {
					final Promise<WeNetUserProfile> activityPromise = Promise.promise();
					relationship.validate(codeRelationships + "[" + index + "]", repository).onComplete(validation -> {

						if (validation.failed()) {

							activityPromise.fail(validation.cause());

						} else {

							activityPromise.complete(merged);
						}
					});
					return activityPromise.future();
				});
				for (int j = i + 1; j < source.relationships.size(); j++) {

					if (relationship.equals(source.relationships.get(j))) {

						return Future.failedFuture(new ValidationErrorException(codeRelationships + "[" + j + "]",
								"This relationship is duplicated. It is equal to relationship[" + i + "]."));
					}
				}
			}
			merged.relationships = source.relationships;

		} else {

			merged.relationships = this.relationships;
		}

		promise.complete(merged);
		return future;
	}

	/**
	 * Merge the social practices between this model and another.
	 *
	 * @param source     profile to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param merged     profile to merge the values.
	 *
	 * @throws ValidationErrorException if the profile is not right.
	 */
	protected void mergeSocialPractices(WeNetUserProfile source, String codePrefix, WeNetUserProfile merged)
			throws ValidationErrorException {

		if (source.socialPractices != null) {

			final Set<String> ids = new HashSet<String>();
			if (this.socialPractices != null) {

				for (final SocialPractice socialPractice : this.socialPractices) {

					ids.add(socialPractice.id);
				}
			}
			final String codeSocialPractices = codePrefix + ".socialPractices";
			for (int index = 0; index < source.socialPractices.size(); index++) {

				final String codeSocialPractice = codeSocialPractices + "[" + index + "]";
				final SocialPractice socialPractice = source.socialPractices.get(index);
				if (socialPractice.id == null) {

					socialPractice.validate(codeSocialPractice);

				} else if (!ids.remove(socialPractice.id)) {

					throw new ValidationErrorException(codeSocialPractice + ".id",
							"Does not exist a norm with the specified identifier or it is duplicated.");

				} else {

					final String id = socialPractice.id;

					try {

						socialPractice.id = null;
						socialPractice.validate(codeSocialPractice);

					} finally {

						socialPractice.id = id;
					}
				}
			}
			merged.socialPractices = source.socialPractices;

		} else {

			merged.socialPractices = this.socialPractices;
		}
	}

	/**
	 * Merge the personal behaviors between this model and another.
	 *
	 * @param source     profile to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param merged     profile to merge the values.
	 *
	 * @throws ValidationErrorException if the profile is not right.
	 */
	protected void mergePersonalBehaviors(WeNetUserProfile source, String codePrefix, WeNetUserProfile merged)
			throws ValidationErrorException {

		if (source.personalBehaviors != null) {

			final Set<String> ids = new HashSet<String>();
			if (this.personalBehaviors != null) {

				for (final Routine personalBehavior : this.personalBehaviors) {

					ids.add(personalBehavior.id);
				}
			}
			final String codePersonalBehaviors = codePrefix + ".personalBehaviors";
			for (int index = 0; index < source.personalBehaviors.size(); index++) {

				final String codePersonalBehavior = codePersonalBehaviors + "[" + index + "]";
				final Routine personalBehavior = source.personalBehaviors.get(index);
				if (personalBehavior.id == null) {

					personalBehavior.validate(codePersonalBehavior);

				} else if (!ids.remove(personalBehavior.id)) {

					throw new ValidationErrorException(codePersonalBehavior + ".id",
							"Does not exist a norm with the specified identifier or it is duplicated.");

				} else {

					final String id = personalBehavior.id;

					try {

						personalBehavior.id = null;
						personalBehavior.validate(codePersonalBehavior);

					} finally {

						personalBehavior.id = id;
					}
				}
			}
			merged.personalBehaviors = source.personalBehaviors;

		} else {

			merged.personalBehaviors = this.personalBehaviors;
		}
	}
}
