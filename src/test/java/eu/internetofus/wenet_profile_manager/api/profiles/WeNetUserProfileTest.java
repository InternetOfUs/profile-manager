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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.ModelTestCase;

/**
 * Test the {@link WeNetUserProfile}.
 *
 * @see WeNetUserProfile
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetUserProfileTest extends ModelTestCase<WeNetUserProfile> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WeNetUserProfile createModelExample(int index) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = String.valueOf(index);
		model.name = new UserNameTest().createModelExample(index);
		model.dateOfBirth = new ProfileDateTest().createModelExample(index);
		model.gender = Gender.F;
		model.email = "user1@internetofus.eu";
		model.phoneNumber = "+34987654321";
		model.locale = "ca_AD";
		model.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png";
		model.nationality = "nationality_1";
		model.languages = new ArrayList<>();
		model.languages.add(new LanguageTest().createModelExample(index));
		model.occupation = "occupation 1";
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(index));
		model.plannedActivities = new ArrayList<>();
		model.plannedActivities.add(new PlannedActivityTest().createModelExample(index));
		model.relevantLocations = new ArrayList<>();
		model.relevantLocations.add(new RelevantLocationTest().createModelExample(index));
		model.relationships = new ArrayList<>();
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPracticeTest().createModelExample(index));
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new RoutineTest().createModelExample(index));
		model._creationTs = 1234567891 + index;
		model._lastUpdateTs = 1234567991 + index * 2;

		return model;
	}

	/**
	 * Check the copy of a model has to be equals to the original.
	 */
	@Test
	public void shouldCopyBeEqual() {

		final WeNetUserProfile model1 = this.createModelExample(1);
		final WeNetUserProfile model2 = new WeNetUserProfile(model1);
		assertThat(model1).isEqualTo(model2);

	}

	// /**
	// * Check that an empty model is valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldEmptyModelBeValid() {
	//
	// final WeNetUserProfile profile = new WeNetUserProfile();
	// assertThat(catchThrowable(() ->
	// profile.validate("codePrefix"))).doesNotThrowAnyException();
	// }
	//
	// /**
	// * Check that the {@link #createModelExample(1)} is valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldExample1BeValid() {
	//
	// final WeNetUserProfile model = this.createModelExample(1);
	// assertThat(catchThrowable(() ->
	// model.validate("codePrefix"))).doesNotThrowAnyException();
	// }
	//
	// /**
	// * Check that the {@link #createModelExample2()} is valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldExample2BeValid() {
	//
	// final WeNetUserProfile model = this.createModelExample2();
	// assertThat(catchThrowable(() ->
	// model.validate("codePrefix"))).doesNotThrowAnyException();
	// }
	//
	// /**
	// * Check that a model with all the values is valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldFullModelBeValid() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.id = " ";
	// model.name = new UserNameTest().createModelExample(1);
	// model.dateOfBirth = new ProfileDateTest().createModelExample(1);
	// model.gender = Gender.F;
	// model.email = " user1@internetofus.eu ";
	// model.locale = " en_US ";
	// model.avatar = "
	// https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png ";
	// model.phoneNumber = " +34987654321 ";
	// model.nationality = " Spanish ";
	// model.languages = new ArrayList<>();
	// model.languages.add(new LanguageTest().createModelExample(1));
	// model.occupation = " nurse ";
	// model.norms = new ArrayList<>();
	// model.norms.add(new NormTest().createModelExample(1));
	// model.plannedActivities = new ArrayList<>();
	// model.plannedActivities.add(new PlannedActivityTest().createModelExample(1));
	// model.relevantLocations = new ArrayList<>();
	// model.relevantLocations.add(new
	// RelevantLocationTest().createModelExample(1));
	// model.relationships = new ArrayList<>();
	// model.socialPractices = new ArrayList<>();
	// model.socialPractices.add(new SocialPracticeTest().createModelExample(1));
	// model.personalBehaviors = new ArrayList<>();
	// model.personalBehaviors.add(new RoutineTest().createModelExample(1));
	// assertThat(catchThrowable(() ->
	// model.validate("codePrefix"))).doesNotThrowAnyException();
	//
	// final WeNetUserProfile expected = new WeNetUserProfile();
	// expected.id = model.id;
	// expected._creationTs = model._creationTs;
	// expected._lastUpdateTs = model._lastUpdateTs;
	// expected.name = new UserNameTest().createModelExample(1);
	// expected.dateOfBirth = new ProfileDateTest().createModelExample(1);
	// expected.gender = Gender.F;
	// expected.email = "user1@internetofus.eu";
	// expected.locale = "en_US";
	// expected.phoneNumber = "+34 987 65 43 21";
	// expected.avatar =
	// "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png";
	// expected.nationality = "Spanish";
	// expected.languages = new ArrayList<>();
	// expected.languages.add(new LanguageTest().createModelExample(1));
	// expected.occupation = "nurse";
	// expected.norms = new ArrayList<>();
	// expected.norms.add(new NormTest().createModelExample(1));
	// expected.norms.get(0).id = model.norms.get(0).id;
	// expected.plannedActivities = new ArrayList<>();
	// expected.plannedActivities.add(new
	// PlannedActivityTest().createModelExample(1));
	// expected.plannedActivities.get(0).id = model.plannedActivities.get(0).id;
	// expected.relevantLocations = new ArrayList<>();
	// expected.relevantLocations.add(new
	// RelevantLocationTest().createModelExample(1));
	// expected.relevantLocations.get(0).id = model.relevantLocations.get(0).id;
	// expected.relationships = new ArrayList<>();
	// expected.socialPractices = new ArrayList<>();
	// expected.socialPractices.add(new SocialPracticeTest().createModelExample(1));
	// expected.socialPractices.get(0).id = model.socialPractices.get(0).id;
	// expected.socialPractices.get(0).materials.id =
	// model.socialPractices.get(0).materials.id;
	// expected.socialPractices.get(0).competences.id =
	// model.socialPractices.get(0).competences.id;
	// expected.socialPractices.get(0).norms.get(0).id =
	// model.socialPractices.get(0).norms.get(0).id;
	// expected.personalBehaviors = new ArrayList<>();
	// expected.personalBehaviors.add(new RoutineTest().createModelExample(1));
	// expected.personalBehaviors.get(0).id = model.personalBehaviors.get(0).id;
	// assertThat(model).isEqualTo(expected);
	// }
	//
	// /**
	// * Check that the model with id is not valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithAnId() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.id = "has_id";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.id");
	// }
	//
	// /**
	// * Check that the name is not valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadName() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.name = new UserNameTest().createModelExample(1);
	// model.name.first = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.name.first");
	// }
	//
	// /**
	// * Check that the birth date is not valid.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadBirthDate() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.dateOfBirth = new ProfileDateTest().createModelExample(1);
	// model.dateOfBirth.month = 0;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.dateOfBirth.month");
	// }
	//
	// /**
	// * Check that the birth date is not on the future.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABirthDateOnTheFuture() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.dateOfBirth = new ProfileDate();
	// final LocalDate tomorrow = LocalDate.now().plusDays(1);
	// model.dateOfBirth.year = tomorrow.getYear();
	// model.dateOfBirth.month = (byte) tomorrow.getMonthValue();
	// model.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.dateOfBirth");
	// }
	//
	// /**
	// * Check that the birth date is not before the oldest people on world.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void
	// shouldNotBeValidWithABirthDateBeforeTheBirthDateOldestPersonOnWorld() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.dateOfBirth = new ProfileDate();
	// model.dateOfBirth.year = 1903;
	// model.dateOfBirth.month = 1;
	// model.dateOfBirth.day = 1;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.dateOfBirth");
	// }
	//
	// /**
	// * Check that not accept profiles with bad email address.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadEmail() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.email = " bad email @ adrress ";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.email");
	// }
	//
	// /**
	// * Check that not accept profiles with bad locale.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadLocale() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.locale = " bad locale";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.locale");
	// }
	//
	// /**
	// * Check that not accept profiles with bad phone number.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadPhoneNumber() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.phoneNumber = " bad phone number";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.phoneNumber");
	// }
	//
	// /**
	// * Check that not accept profiles with bad avatar address.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadAvatar() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.avatar = " bad avatar";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.avatar");
	// }
	//
	// /**
	// * Check that not accept profiles with bad nationality.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadNationality() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.nationality = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.nationality");
	// }
	//
	// /**
	// * Check that not accept profiles with bad languages.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadLanguages() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.languages = new ArrayList<>();
	// model.languages.add(new Language());
	// model.languages.add(new Language());
	// model.languages.add(new Language());
	// model.languages.get(1).code = "bad code";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.languages[1].code");
	// }
	//
	// /**
	// * Check that not accept profiles with bad occupation.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadOccupation() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.occupation = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.occupation");
	// }
	//
	// /**
	// * Check that not accept profiles with bad norms.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadNorms() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.norms = new ArrayList<>();
	// model.norms.add(new Norm());
	// model.norms.add(new Norm());
	// model.norms.add(new Norm());
	// model.norms.get(1).attribute = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.norms[1].attribute");
	// }
	//
	// /**
	// * Check that not accept profiles with bad planned activities.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadPlannedActivities() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.plannedActivities = new ArrayList<>();
	// model.plannedActivities.add(new PlannedActivity());
	// model.plannedActivities.add(new PlannedActivity());
	// model.plannedActivities.add(new PlannedActivity());
	// model.plannedActivities.get(1).description = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.plannedActivities[1].description");
	// }
	//
	// /**
	// * Check that not accept profiles with bad relevant locations.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadRelevantLocations() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.relevantLocations = new ArrayList<>();
	// model.relevantLocations.add(new RelevantLocation());
	// model.relevantLocations.add(new RelevantLocation());
	// model.relevantLocations.add(new RelevantLocation());
	// model.relevantLocations.get(1).label = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.relevantLocations[1].label");
	// }
	//
	// /**
	// * Check that not accept profiles with bad planned activities.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadRelationships() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.relationships = new ArrayList<>();
	// model.relationships.add(new SocialNetworkRelationship());
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.relationships[0].userId");
	// }
	//
	// /**
	// * Check that not accept profiles with bad social practices.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadSocialPractices() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.socialPractices = new ArrayList<>();
	// model.socialPractices.add(new SocialPractice());
	// model.socialPractices.add(new SocialPractice());
	// model.socialPractices.add(new SocialPractice());
	// model.socialPractices.get(1).label = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.socialPractices[1].label");
	// }
	//
	// /**
	// * Check that not accept profiles with bad personal behaviors.
	// *
	// * @see WeNetUserProfile#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadPersonalBehaviors() {
	//
	// final WeNetUserProfile model = new WeNetUserProfile();
	// model.personalBehaviors = new ArrayList<>();
	// model.personalBehaviors.add(new Routine());
	// model.personalBehaviors.add(new Routine());
	// model.personalBehaviors.add(new Routine());
	// model.personalBehaviors.get(1).label = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.personalBehaviors[1].label");
	// }
	//
}
