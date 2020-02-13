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

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.wenet_profile_manager.ModelTestCase;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.ValidationsTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetUserProfile}.
 *
 * @see WeNetUserProfile
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class WeNetUserProfileTest extends ModelTestCase<WeNetUserProfile> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WeNetUserProfile createModelExample(int index) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = null;
		model.name = new UserNameTest().createModelExample(index);
		model.dateOfBirth = new ProfileDateTest().createModelExample(index);
		model.gender = Gender.F;
		model.email = "user" + index + "@internetofus.eu";
		model.phoneNumber = "+34" + (987654321 + index);
		model.locale = "ca_AD";
		model.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/" + index + "/WeNet_logo.png";
		model.nationality = "nationality_" + index;
		model.languages = new ArrayList<>();
		model.languages.add(new LanguageTest().createModelExample(index));
		model.occupation = "occupation " + index;
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(index));
		model.plannedActivities = new ArrayList<>();
		model.plannedActivities.add(new PlannedActivityTest().createModelExample(index));
		model.relevantLocations = new ArrayList<>();
		model.relevantLocations.add(new RelevantLocationTest().createModelExample(index));
		model.relationships = null;
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPracticeTest().createModelExample(index));
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new RoutineTest().createModelExample(index));
		model._creationTs = 1234567891 + index;
		model._lastUpdateTs = 1234567991 + index * 2;
		return model;

	}

	/**
	 * Create an example model that has the specified index.
	 *
	 * @param index      to use in the example.
	 * @param repository to use to create the model.
	 *
	 * @return the example.
	 */
	public Future<WeNetUserProfile> createModelExample(int index, ProfilesRepository repository) {

		final Promise<WeNetUserProfile> promise = Promise.promise();
		Future<WeNetUserProfile> future = promise.future();
		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = null;
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
		future = future.compose(map -> {

			final Promise<WeNetUserProfile> activityPromise = Promise.promise();
			new PlannedActivityTest().createModelExample(index, repository).onComplete(created -> {

				if (created.failed()) {

					activityPromise.fail(created.cause());

				} else {
					final PlannedActivity activity = created.result();
					model.plannedActivities.add(activity);
					activityPromise.complete(model);
				}
			});
			return activityPromise.future();

		});
		model.relevantLocations = new ArrayList<>();
		model.relevantLocations.add(new RelevantLocationTest().createModelExample(index));
		model.relationships = new ArrayList<>();
		future = future.compose(map -> {

			final Promise<WeNetUserProfile> relationshipPromise = Promise.promise();
			new SocialNetworkRelationshipTest().createModelExample(index, repository).onComplete(created -> {

				if (created.failed()) {

					relationshipPromise.fail(created.cause());

				} else {
					final SocialNetworkRelationship relationship = created.result();
					model.relationships.add(relationship);
					relationshipPromise.complete(model);
				}
			});
			return relationshipPromise.future();

		});
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPracticeTest().createModelExample(index));
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new RoutineTest().createModelExample(index));
		model._creationTs = 1234567891 + index;
		model._lastUpdateTs = 1234567991 + index * 2;
		promise.complete(model);
		return future;

	}

	/**
	 * Check that an empty model is valid.
	 *
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@Test
	public void shouldEmptyModelBeValid(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		testContext.assertComplete(model.validate("codePrefix", repository))
				.setHandler(result -> testContext.completeNow());

	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index       to verify
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = this.createModelExample(index);
		testContext.assertComplete(model.validate("codePrefix", repository))
				.setHandler(result -> testContext.completeNow());

	}

	/**
	 * Check that the {@link #createModelExample(int,ProfilesRepository)} is valid.
	 *
	 * @param index       to verify
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleFromRepositoryBeValid(int index, ProfilesRepository repository,
			VertxTestContext testContext) {

		this.createModelExample(index, repository).onComplete(created -> {

			if (created.failed()) {

				testContext.failNow(created.cause());

			} else {

				final WeNetUserProfile model = created.result();
				testContext.assertComplete(model.validate("codePrefix", repository))
						.setHandler(result -> testContext.completeNow());
			}

		});

	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldFullModelBeValid(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = " ";
		model.name = new UserNameTest().createModelExample(1);
		model.dateOfBirth = new ProfileDateTest().createModelExample(1);
		model.gender = Gender.F;
		model.email = " user1@internetofus.eu ";
		model.locale = " en_US ";
		model.avatar = " https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png ";
		model.phoneNumber = " +34987654321 ";
		model.nationality = " Spanish ";
		model.languages = new ArrayList<>();
		model.languages.add(new LanguageTest().createModelExample(1));
		model.occupation = " nurse ";
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(1));
		model.plannedActivities = new ArrayList<>();
		model.plannedActivities.add(new PlannedActivityTest().createModelExample(1));
		model.relevantLocations = new ArrayList<>();
		model.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
		model.relationships = new ArrayList<>();
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPracticeTest().createModelExample(1));
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new RoutineTest().createModelExample(1));

		testContext.assertComplete(model.validate("codePrefix", repository)).setHandler(result -> {

			final WeNetUserProfile expected = new WeNetUserProfile();
			expected.id = model.id;
			expected._creationTs = model._creationTs;
			expected._lastUpdateTs = model._lastUpdateTs;
			expected.name = new UserNameTest().createModelExample(1);
			expected.dateOfBirth = new ProfileDateTest().createModelExample(1);
			expected.gender = Gender.F;
			expected.email = "user1@internetofus.eu";
			expected.locale = "en_US";
			expected.phoneNumber = "+34 987 65 43 21";
			expected.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png";
			expected.nationality = "Spanish";
			expected.languages = new ArrayList<>();
			expected.languages.add(new LanguageTest().createModelExample(1));
			expected.occupation = "nurse";
			expected.norms = new ArrayList<>();
			expected.norms.add(new NormTest().createModelExample(1));
			expected.norms.get(0).id = model.norms.get(0).id;
			expected.plannedActivities = new ArrayList<>();
			expected.plannedActivities.add(new PlannedActivityTest().createModelExample(1));
			expected.plannedActivities.get(0).id = model.plannedActivities.get(0).id;
			expected.relevantLocations = new ArrayList<>();
			expected.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
			expected.relevantLocations.get(0).id = model.relevantLocations.get(0).id;
			expected.relationships = new ArrayList<>();
			expected.socialPractices = new ArrayList<>();
			expected.socialPractices.add(new SocialPracticeTest().createModelExample(1));
			expected.socialPractices.get(0).id = model.socialPractices.get(0).id;
			expected.socialPractices.get(0).materials.id = model.socialPractices.get(0).materials.id;
			expected.socialPractices.get(0).competences.id = model.socialPractices.get(0).competences.id;
			expected.socialPractices.get(0).norms.get(0).id = model.socialPractices.get(0).norms.get(0).id;
			expected.personalBehaviors = new ArrayList<>();
			expected.personalBehaviors.add(new RoutineTest().createModelExample(1));
			expected.personalBehaviors.get(0).id = model.personalBehaviors.get(0).id;
			assertThat(model).isEqualTo(expected);
			testContext.completeNow();
		});
	}

	/**
	 * Check that the validation of a model fails.
	 *
	 * @param model       to validate.
	 * @param suffix      to the error code.
	 * @param repository  to use.
	 * @param testContext context to test.
	 */
	public void assertFailValidate(WeNetUserProfile model, String suffix, ProfilesRepository repository,
			VertxTestContext testContext) {

		testContext.assertFailure(model.validate("codePrefix", repository)).setHandler(result -> {

			final Throwable cause = result.cause();
			assertThat(cause).isInstanceOf(ValidationErrorException.class);
			String expectedCode = "codePrefix";
			if (suffix != null && suffix.length() > 0) {

				expectedCode += "." + suffix;
			}
			assertThat(((ValidationErrorException) cause).getCode()).isEqualTo(expectedCode);
			testContext.completeNow();
		});

	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithAnId(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = "has_id";
		this.assertFailValidate(model, "id", repository, testContext);

	}

	/**
	 * Check that the name is not valid.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadName(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.name = new UserNameTest().createModelExample(1);
		model.name.first = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "name.first", repository, testContext);

	}

	/**
	 * Check that the birth date is not valid.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadBirthDate(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.dateOfBirth = new ProfileDateTest().createModelExample(1);
		model.dateOfBirth.month = 0;
		this.assertFailValidate(model, "dateOfBirth.month", repository, testContext);

	}

	/**
	 * Check that the birth date is not on the future.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABirthDateOnTheFuture(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.dateOfBirth = new ProfileDate();
		final LocalDate tomorrow = LocalDate.now().plusDays(1);
		model.dateOfBirth.year = tomorrow.getYear();
		model.dateOfBirth.month = (byte) tomorrow.getMonthValue();
		model.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
		this.assertFailValidate(model, "dateOfBirth", repository, testContext);

	}

	/**
	 * Check that the birth date is not before the oldest people on world.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.dateOfBirth = new ProfileDate();
		model.dateOfBirth.year = 1903;
		model.dateOfBirth.month = 1;
		model.dateOfBirth.day = 1;
		this.assertFailValidate(model, "dateOfBirth", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad email address.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadEmail(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.email = " bad email @ adrress ";
		this.assertFailValidate(model, "email", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad locale.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadLocale(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.locale = " bad locale";
		this.assertFailValidate(model, "locale", repository, testContext);
	}

	/**
	 * Check that not accept profiles with bad phone number.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadPhoneNumber(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.phoneNumber = " bad phone number";
		this.assertFailValidate(model, "phoneNumber", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad avatar address.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadAvatar(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.avatar = " bad avatar";
		this.assertFailValidate(model, "avatar", repository, testContext);
	}

	/**
	 * Check that not accept profiles with bad nationality.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadNationality(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.nationality = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "nationality", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad languages.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadLanguages(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.languages = new ArrayList<>();
		model.languages.add(new Language());
		model.languages.add(new Language());
		model.languages.add(new Language());
		model.languages.get(1).code = "bad code";
		this.assertFailValidate(model, "languages[1].code", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad occupation.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadOccupation(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.occupation = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "occupation", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad norms.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadNorms(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.norms = new ArrayList<>();
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.get(1).attribute = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "norms[1].attribute", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad planned activities.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadPlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.plannedActivities = new ArrayList<>();
		model.plannedActivities.add(new PlannedActivity());
		model.plannedActivities.add(new PlannedActivity());
		model.plannedActivities.add(new PlannedActivity());
		model.plannedActivities.get(1).description = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "plannedActivities[1].description", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad relevant locations.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.relevantLocations = new ArrayList<>();
		model.relevantLocations.add(new RelevantLocation());
		model.relevantLocations.add(new RelevantLocation());
		model.relevantLocations.add(new RelevantLocation());
		model.relevantLocations.get(1).label = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "relevantLocations[1].label", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad planned activities.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadRelationships(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.relationships = new ArrayList<>();
		model.relationships.add(new SocialNetworkRelationship());
		this.assertFailValidate(model, "relationships[0].type", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad social practices.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadSocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPractice());
		model.socialPractices.add(new SocialPractice());
		model.socialPractices.add(new SocialPractice());
		model.socialPractices.get(1).label = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "socialPractices[1].label", repository, testContext);

	}

	/**
	 * Check that not accept profiles with bad personal behaviors.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadPersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new Routine());
		model.personalBehaviors.add(new Routine());
		model.personalBehaviors.add(new Routine());
		model.personalBehaviors.get(1).label = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "personalBehaviors[1].label", repository, testContext);

	}

}
