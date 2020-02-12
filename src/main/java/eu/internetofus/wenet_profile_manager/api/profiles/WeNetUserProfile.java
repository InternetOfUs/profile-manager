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

import eu.internetofus.wenet_profile_manager.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "WeNetUserProfile", description = "The profile of a WeNet user.")
@DataObject(generateConverter = true, publicConverter = false)
public class WeNetUserProfile extends Model {

	/**
	 * The identifier of the profile.
	 */
	@Schema(description = "The identifier of the profile.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
	public String id;

	/**
	 * Create empty profile.
	 */
	public WeNetUserProfile() {

	}

	/**
	 * Create a profile from JSON.
	 *
	 * @param json with the profile values.
	 */
	public WeNetUserProfile(JsonObject json) {

		WeNetUserProfileConverter.fromJson(json, this);
	}

	/**
	 * Return the JSON representation of the profile.
	 *
	 * @return the JSON encoding of this model
	 */
	public JsonObject toJson() {

		final JsonObject json = new JsonObject();
		WeNetUserProfileConverter.toJson(this, json);
		return json;
	}

	// /**
	// * Return the model identifier.
	// *
	// * @return the model identifier.
	// *
	// * @see #id
	// */
	// public String getId() {
	//
	// return this.id;
	// }
	//
	// /**
	// * Change the model identifier.
	// *
	// * @param id the identifier of the model.
	// *
	// * @see #id
	// */
	// public void setId(String id) {
	//
	// this.id = id;
	// }
	//
	// /**
	// * The name of the user.
	// */
	// @Schema(description = "The name of the user.")
	// @Embedded
	// public UserName name;
	//
	// /**
	// * The date of birth of the user.
	// */
	// @Schema(description = "The date of birth of the user.")
	// @Embedded
	// public ProfileDate dateOfBirth;
	//
	// /**
	// * The gender of the user.
	// */
	// @Schema(description = "The gender of the user", example = "F")
	// public Gender gender;
	//
	// /**
	// * The email of the user.
	// */
	// @Schema(description = "The email of the user", example =
	// "jonnyd@internetofus.eu")
	// public String email;
	//
	// /**
	// * The phone number of the user, on the E.164 format (^\+?[1-9]\d{1,14}$).
	// */
	// @Schema(
	// description = "The phone number of the user, on the E.164 format
	// (^\\+?[1-9]\\d{1,14}$)",
	// example = "+34987654321")
	// public String phoneNumber;
	//
	// /**
	// * The email of the user.
	// */
	// @Schema(description = "The locale of the user", example = "es_ES")
	// public String locale;
	//
	// /**
	// * The avatar of the user.
	// */
	// @Schema(
	// description = "The URL to an image that represents the avatar of the user.",
	// example =
	// "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png")
	// public String avatar;
	//
	// /**
	// * The email of the user.
	// */
	// @Schema(description = "The nationality of the user", example = "Spanish")
	// public String nationality;
	//
	// /**
	// * The languages that the user can understand.
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = Language.class),
	// arraySchema = @Schema(description = "The languages that the user can
	// understand"))
	// @ElementCollection
	// public List<Language> languages;
	//
	// /**
	// * Return the individual norms of the user
	// *
	// * @return the individual norms.
	// */
	// @JsonProperty("languages")
	// public List<Language> getLanguages() {
	//
	// return this.languages;
	//
	// }
	//
	// /**
	// * Change languages
	// *
	// * @param languages the languages to set
	// *
	// * @see #languages
	// */
	// public void setLanguages(List<Language> languages) {
	//
	// this.languages = languages;
	// }
	//
	// /**
	// * The email of the user.
	// */
	// @Schema(description = "The occupation of the user", example = "nurse")
	// public String occupation;
	//
	// /**
	// * The individual norms of the user
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = Norm.class),
	// arraySchema = @Schema(description = "The individual norms of the user"))
	// @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
	// public List<Norm> norms;
	//
	// /**
	// * Change norms
	// *
	// * @param norms the norms to set
	// *
	// * @see #norms
	// */
	// public void setNorms(List<Norm> norms) {
	//
	// this.norms = norms;
	// }
	//
	// /**
	// * Return the individual norms of the user
	// *
	// * @return the individual norms.
	// */
	// @JsonProperty("norms")
	// public List<Norm> getNorms() {
	//
	// return this.norms;
	//
	// }
	//
	// /**
	// * The planned activities of the user.
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = PlannedActivity.class),
	// arraySchema = @Schema(description = "The planned activities of the user"))
	// @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
	// public List<PlannedActivity> plannedActivities;
	//
	// /**
	// * Return the planned activities.
	// *
	// * @return the planned activities by the user.
	// */
	// @JsonProperty("plannedActivities")
	// public List<PlannedActivity> getPlannedActivities() {
	//
	// return this.plannedActivities;
	//
	// }
	//
	// /**
	// * Change plannedActivities
	// *
	// * @param plannedActivities the plannedActivities to set
	// *
	// * @see #plannedActivities
	// */
	// public void setPlannedActivities(List<PlannedActivity> plannedActivities) {
	//
	// this.plannedActivities = plannedActivities;
	// }
	//
	// /**
	// * The locations of interest for the user.
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = RelevantLocation.class),
	// arraySchema = @Schema(description = "The locations of interest for the
	// user"))
	// @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
	// public List<RelevantLocation> relevantLocations;
	//
	// /**
	// * Return the relevant locations.
	// *
	// * @return the locations of interest for the user.
	// */
	// @JsonProperty("relevantLocations")
	// public List<RelevantLocation> getRelevantLocations() {
	//
	// return this.relevantLocations;
	//
	// }
	//
	// /**
	// * Change relevantLocations
	// *
	// * @param relevantLocations the relevantLocations to set
	// *
	// * @see #relevantLocations
	// */
	// public void setRelevantLocations(List<RelevantLocation> relevantLocations) {
	//
	// this.relevantLocations = relevantLocations;
	// }
	//
	// /**
	// * The user relationships.
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = SocialNetworkRelationship.class),
	// arraySchema = @Schema(description = "The user relationships with other WeNet
	// users."))
	// @ElementCollection
	// public List<SocialNetworkRelationship> relationships;
	//
	// /**
	// * Return the user relationships.
	// *
	// * @return the relationships of the user.
	// */
	// @JsonProperty("relationships")
	// public List<SocialNetworkRelationship> getRelationships() {
	//
	// return this.relationships;
	//
	// }
	//
	// /**
	// * Change relationships
	// *
	// * @param relationships the relationships to set
	// *
	// * @see #relationships
	// */
	// public void setRelationships(List<SocialNetworkRelationship> relationships) {
	//
	// this.relationships = relationships;
	// }
	//
	// /**
	// * The user social practices.
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = SocialPractice.class),
	// arraySchema = @Schema(description = "The user social practices"))
	// @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
	// public List<SocialPractice> socialPractices;
	//
	// /**
	// * Return the user social practices.
	// *
	// * @return the social practices of the user.
	// */
	// @JsonProperty("socialPractices")
	// public List<SocialPractice> getSocialPractices() {
	//
	// return this.socialPractices;
	//
	// }
	//
	// /**
	// * Change socialPractices
	// *
	// * @param socialPractices the socialPractices to set
	// *
	// * @see #socialPractices
	// */
	// public void setSocialPractices(List<SocialPractice> socialPractices) {
	//
	// this.socialPractices = socialPractices;
	// }
	//
	// /**
	// * The user routines.
	// */
	// @ArraySchema(
	// schema = @Schema(implementation = Routine.class),
	// arraySchema = @Schema(description = "The user routines"))
	// @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
	// public List<Routine> personalBehaviors;
	//
	// /**
	// * Return the user personal behaviors.
	// *
	// * @return the routines of the user.
	// */
	// @JsonProperty("personalBehaviors")
	// public List<Routine> getPersonalbehaviors() {
	//
	// return this.personalBehaviors;
	//
	// }
	//
	// /**
	// * Change personalBehaviors
	// *
	// * @param personalBehaviors the personalBehaviors to set
	// *
	// * @see #personalBehaviors
	// */
	// @JsonProperty("personalBehaviors")
	// public void setPersonalBehaviors(List<Routine> personalBehaviors) {
	//
	// this.personalBehaviors = personalBehaviors;
	// }
	//
	// /**
	// * The instant of the creation.
	// */
	// @Schema(description = "The time stamp representing the account creation
	// instant.", example = "1563871899")
	// public long _creationTs;
	//
	// /**
	// * The instant of the last update.
	// */
	// @Schema(description = "The time stamp representing the last update instant.",
	// example = "1563898764")
	// public long _lastUpdateTs;
	//
	// /**
	// * Create a new profile.
	// */
	// public WeNetUserProfile() {
	//
	// this._creationTs = this._lastUpdateTs = System.currentTimeMillis();
	// }
	//
	// /**
	// * Create a new profile with the information of another profile.
	// *
	// * @param profile to copy.
	// */
	// public WeNetUserProfile(WeNetUserProfile profile) {
	//
	// this.id = profile.id;
	// if (profile.name != null) {
	//
	// this.name = new UserName(profile.name);
	// }
	// if (profile.dateOfBirth != null) {
	//
	// this.dateOfBirth = new ProfileDate(profile.dateOfBirth);
	// }
	// this.gender = profile.gender;
	// this.email = profile.email;
	// this.phoneNumber = profile.phoneNumber;
	// this.locale = profile.locale;
	// this.avatar = profile.avatar;
	// this.nationality = profile.nationality;
	// if (profile.languages != null) {
	//
	// this.languages = new ArrayList<>();
	// for (final Language profileLanguage : profile.languages) {
	//
	// final Language language = new Language(profileLanguage);
	// this.languages.add(language);
	//
	// }
	// }
	// this.occupation = profile.occupation;
	// if (profile.norms != null) {
	//
	// this.norms = new ArrayList<>();
	// for (final Norm profileNorm : profile.norms) {
	//
	// final Norm norm = new Norm(profileNorm);
	// this.norms.add(norm);
	//
	// }
	// }
	//
	// if (profile.plannedActivities != null) {
	//
	// this.plannedActivities = new ArrayList<>();
	// for (final PlannedActivity profileActivity : profile.plannedActivities) {
	//
	// final PlannedActivity activity = new PlannedActivity(profileActivity);
	// this.plannedActivities.add(activity);
	//
	// }
	// }
	//
	// if (profile.relevantLocations != null) {
	//
	// this.relevantLocations = new ArrayList<>();
	// for (final RelevantLocation profileLocation : profile.relevantLocations) {
	//
	// final RelevantLocation location = new RelevantLocation(profileLocation);
	// this.relevantLocations.add(location);
	//
	// }
	// }
	//
	// if (profile.relationships != null) {
	//
	// this.relationships = new ArrayList<>();
	// for (final SocialNetworkRelationship profileRelationship :
	// profile.relationships) {
	//
	// final SocialNetworkRelationship relationship = new
	// SocialNetworkRelationship(profileRelationship);
	// this.relationships.add(relationship);
	//
	// }
	// }
	//
	// if (profile.socialPractices != null) {
	//
	// this.socialPractices = new ArrayList<>();
	// for (final SocialPractice profilePractice : profile.socialPractices) {
	//
	// final SocialPractice relationship = new SocialPractice(profilePractice);
	// this.socialPractices.add(relationship);
	//
	// }
	// }
	//
	// if (profile.personalBehaviors != null) {
	//
	// this.personalBehaviors = new ArrayList<>();
	// for (final Routine profilebehavior : profile.personalBehaviors) {
	//
	// final Routine behavior = new Routine(profilebehavior);
	// this.personalBehaviors.add(behavior);
	//
	// }
	// }
	//
	// this._creationTs = profile._creationTs;
	// this._lastUpdateTs = profile._lastUpdateTs;
	//
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void validate(String codePrefix) throws ValidationErrorException {
	//
	// this.id = Validations.validateNullableStringField(codePrefix, "id", 255,
	// this.id);
	// if (this.id != null) {
	//
	// throw new ValidationErrorException(codePrefix + ".id",
	// "You can not specify the identifier of the profile to create");
	//
	// } else {
	//
	// this.id = UUID.randomUUID().toString();
	// }
	// if (this.name != null) {
	//
	// this.name.validate(codePrefix + ".name");
	// }
	// if (this.dateOfBirth != null) {
	//
	// this.dateOfBirth.validate(codePrefix + ".dateOfBirth");
	// final LocalDate birthDate = LocalDate.of(this.dateOfBirth.year,
	// this.dateOfBirth.month, this.dateOfBirth.day);
	// if (birthDate.isAfter(LocalDate.now())) {
	//
	// throw new ValidationErrorException(codePrefix + ".dateOfBirth", "The birth
	// date can not be on the future");
	// }
	// if (birthDate.isBefore(LocalDate.of(1903, 1, 2))) {
	//
	// throw new ValidationErrorException(codePrefix + ".dateOfBirth",
	// "The user can not be born before Kane Tanake, the oldest living person on
	// earth");
	// }
	// }
	// // Gender not verified because is a enumeration and this fix the possible
	// values
	// this.email = Validations.validateNullableEmailField(codePrefix, "email",
	// this.email);
	// this.locale = Validations.validateNullableLocaleField(codePrefix, "locale",
	// this.locale);
	// this.phoneNumber = Validations.validateNullableTelephoneField(codePrefix,
	// "phoneNumber", this.locale,
	// this.phoneNumber);
	// this.avatar = Validations.validateNullableURLField(codePrefix, "avatar",
	// this.avatar);
	// this.nationality = Validations.validateNullableStringField(codePrefix,
	// "nationality", 255, this.nationality);
	// if (this.languages != null && !this.languages.isEmpty()) {
	//
	// final String codeLanguages = codePrefix + ".languages";
	// for (int index = 0; index < this.languages.size(); index++) {
	//
	// final Language language = this.languages.get(index);
	// language.validate(codeLanguages + "[" + index + "]");
	// }
	//
	// }
	// this.occupation = Validations.validateNullableStringField(codePrefix,
	// "occupation", 255, this.occupation);
	// if (this.norms != null && !this.norms.isEmpty()) {
	//
	// final String codeNorms = codePrefix + ".norms";
	// for (int index = 0; index < this.norms.size(); index++) {
	//
	// final Norm norm = this.norms.get(index);
	// norm.validate(codeNorms + "[" + index + "]");
	// }
	//
	// }
	// if (this.plannedActivities != null && !this.plannedActivities.isEmpty()) {
	//
	// final String codeActivities = codePrefix + ".plannedActivities";
	// for (int index = 0; index < this.plannedActivities.size(); index++) {
	//
	// final PlannedActivity plannedActivity = this.plannedActivities.get(index);
	// plannedActivity.validate(codeActivities + "[" + index + "]");
	// }
	//
	// }
	//
	// if (this.relevantLocations != null && !this.relevantLocations.isEmpty()) {
	//
	// final String codeLocations = codePrefix + ".relevantLocations";
	// for (int index = 0; index < this.relevantLocations.size(); index++) {
	//
	// final RelevantLocation relevantLocation = this.relevantLocations.get(index);
	// relevantLocation.validate(codeLocations + "[" + index + "]");
	// }
	//
	// }
	//
	// if (this.relationships != null && !this.relationships.isEmpty()) {
	//
	// final String codeLocations = codePrefix + ".relationships";
	// for (int index = 0; index < this.relationships.size(); index++) {
	//
	// final SocialNetworkRelationship relationship = this.relationships.get(index);
	// relationship.validate(codeLocations + "[" + index + "]");
	// }
	//
	// }
	//
	// if (this.socialPractices != null && !this.socialPractices.isEmpty()) {
	//
	// final String codeLocations = codePrefix + ".socialPractices";
	// for (int index = 0; index < this.socialPractices.size(); index++) {
	//
	// final SocialPractice socialPractice = this.socialPractices.get(index);
	// socialPractice.validate(codeLocations + "[" + index + "]");
	// }
	//
	// }
	//
	// if (this.personalBehaviors != null && !this.personalBehaviors.isEmpty()) {
	//
	// final String codeLocations = codePrefix + ".personalBehaviors";
	// for (int index = 0; index < this.personalBehaviors.size(); index++) {
	//
	// final Routine personalBehavior = this.personalBehaviors.get(index);
	// personalBehavior.validate(codeLocations + "[" + index + "]");
	// }
	//
	// }
	// }

}
