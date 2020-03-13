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

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
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
			arraySchema = @Schema(description = "The locations of interest for the user"))
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
	@Schema(description = "The time stamp representing the account creation instant.", example = "1563871899")
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

		this._creationTs = this._lastUpdateTs = TimeManager.now();
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
	 * @param source     model to get the values to merge.
	 * @param codePrefix prefix for the error code.
	 * @param repository used to get data to verify the profile.
	 *
	 * @return a future that provide the merged model or the error that explains why
	 *         can not be merged.
	 */
	public Future<WeNetUserProfile> merge(WeNetUserProfile source, String codePrefix, ProfilesRepository repository) {

		try {

			final Promise<WeNetUserProfile> promise = Promise.promise();
			Future<WeNetUserProfile> future = promise.future();

			final WeNetUserProfile merged = new WeNetUserProfile();
			merged.id = this.id;
			merged._creationTs = this._creationTs;
			merged._lastUpdateTs = this._lastUpdateTs;
			if (this.name != null) {

				merged.name = this.name.merge(source.name, codePrefix + ".name");

			} else if (source.name != null) {

				source.name.validate(codePrefix + ".name");
				merged.name = source.name;

			}

			if (this.dateOfBirth != null) {

				merged.dateOfBirth = this.dateOfBirth.mergeAsBirthDate(source.dateOfBirth, codePrefix + ".name");

			} else if (source.dateOfBirth != null) {

				source.dateOfBirth.validateAsBirthDate(codePrefix + ".dateOfBirth");
				merged.dateOfBirth = source.dateOfBirth;

			}

			merged.gender = source.gender;
			if (merged.gender == null) {

				merged.gender = this.gender;
			}

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

			merged.languages = Merges.mergeList(this.languages, source.languages, codePrefix + ".languages",
					language -> language.code != null || language.name != null,
					(targetLang,
							sourceLang) -> (targetLang.code != null && targetLang.code.equals(sourceLang.code)
									|| targetLang.name != null && targetLang.name.equals(sourceLang.name)),
					(targetLang, sourceLang, codeLang) -> targetLang.merge(sourceLang, codeLang),
					(lang, codeLang) -> lang.validate(codeLang));

			merged.occupation = Validations.validateNullableStringField(codePrefix, "occupation", 255, source.occupation);
			if (merged.occupation == null) {

				merged.occupation = this.occupation;
			}

			merged.norms = Merges.mergeListOfNorms(this.norms, source.norms, codePrefix + ".norms");

			future = future.compose(map -> this.mergePlannedActivities(source, codePrefix, repository, merged));

			merged.relevantLocations = Merges.mergeList(this.relevantLocations, source.relevantLocations,
					codePrefix + ".relevantLocations", relevantLocation -> relevantLocation.id != null,
					(targetRelevantLocation, sourceRelevantLocation) -> targetRelevantLocation.id
							.equals(sourceRelevantLocation.id),
					(targetRelevantLocation, sourceRelevantLocation, codeRelevantLocationPrefix) -> targetRelevantLocation
							.merge(sourceRelevantLocation, codeRelevantLocationPrefix),
					(relevantLocation, codeRelevantLocationPrefix) -> relevantLocation.validate(codeRelevantLocationPrefix));

			future = future.compose(map -> this.mergeRelationships(source, codePrefix, repository, merged));

			merged.socialPractices = Merges.mergeList(this.socialPractices, source.socialPractices,
					codePrefix + ".socialPractices", socialPractice -> socialPractice.id != null,
					(targetSocialPractice, sourceSocialPractice) -> targetSocialPractice.id.equals(sourceSocialPractice.id),
					(targetSocialPractice, sourceSocialPractice, codeSocialPracticePrefix) -> targetSocialPractice
							.merge(sourceSocialPractice, codeSocialPracticePrefix),
					(socialPractice, codeSocialPracticePrefix) -> socialPractice.validate(codeSocialPracticePrefix));

			merged.personalBehaviors = Merges.mergeList(this.personalBehaviors, source.personalBehaviors,
					codePrefix + ".personalBehaviors", personalBehavior -> personalBehavior.id != null,
					(targetPersonalBehavior, sourcePersonalBehavior) -> targetPersonalBehavior.id
							.equals(sourcePersonalBehavior.id),
					(targetPersonalBehavior, sourcePersonalBehavior, codePersonalBehaviorPrefix) -> targetPersonalBehavior
							.merge(sourcePersonalBehavior, codePersonalBehaviorPrefix),
					(personalBehavior, codePersonalBehaviorPrefix) -> personalBehavior.validate(codePersonalBehaviorPrefix));

			promise.complete(merged);
			return future;

		} catch (final ValidationErrorException exception) {

			return Future.failedFuture(exception);
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
	 * @throws ValidationErrorException if the model is not right.
	 */
	protected Future<WeNetUserProfile> mergePlannedActivities(WeNetUserProfile source, String codePrefix,
			ProfilesRepository repository, WeNetUserProfile merged) throws ValidationErrorException {

		final Promise<WeNetUserProfile> promise = Promise.promise();
		Future<WeNetUserProfile> future = promise.future();
		if (source.plannedActivities != null) {

			final List<PlannedActivity> original = new ArrayList<>();
			if (this.plannedActivities != null) {

				original.addAll(this.plannedActivities);
			}
			merged.plannedActivities = new ArrayList<>();
			INDEX: for (int index = 0; index < source.plannedActivities.size(); index++) {

				final String codeActivity = codePrefix + ".plannedActivities[" + index + "]";
				final PlannedActivity sourceActivity = source.plannedActivities.get(index);
				// search if it modify any original activity
				if (sourceActivity.id != null) {

					for (int j = 0; j < original.size(); j++) {

						final PlannedActivity targetActivity = original.get(j);
						if (targetActivity.id.equals(sourceActivity.id)) {

							original.remove(j);
							future = future.compose(map -> {
								final Promise<WeNetUserProfile> activityPromise = Promise.promise();

								targetActivity.merge(sourceActivity, codeActivity, repository).onComplete(merge -> {

									if (merge.failed()) {

										activityPromise.fail(merge.cause());

									} else {

										merged.plannedActivities.add(merge.result());
										activityPromise.complete(merged);
									}
								});
								return activityPromise.future();
							});
							continue INDEX;
						}

					}
				}

				// not found original in original => check it
				future = future.compose(map -> {
					final Promise<WeNetUserProfile> activityPromise = Promise.promise();

					sourceActivity.validate(codeActivity, repository).onComplete(validation -> {

						if (validation.failed()) {

							activityPromise.fail(validation.cause());

						} else {

							merged.plannedActivities.add(sourceActivity);
							activityPromise.complete(merged);
						}
					});
					return activityPromise.future();
				});
			}

		} else {

			merged.plannedActivities = this.plannedActivities;
		}

		promise.complete(merged);
		return future;

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

}