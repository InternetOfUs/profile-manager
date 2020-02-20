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
import java.util.concurrent.TimeUnit;

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
import io.vertx.junit5.Timeout;
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
	 * Create an basic model that has the specified index.
	 *
	 * @param index to use in the example.
	 *
	 * @return the basic example.
	 */
	public WeNetUserProfile createBasicExample(int index) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = null;
		model.name = new UserNameTest().createModelExample(index);
		model.dateOfBirth = new ProfileDateTest().createModelExample(index);
		model.gender = Gender.F;
		model.email = "user" + index + "@internetofus.eu";
		model.phoneNumber = "+34 987 65 43 " + (10 + index % 90);
		model.locale = "ca_AD";
		model.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/" + index + "/WeNet_logo.png";
		model.nationality = "nationality_" + index;
		model.languages = new ArrayList<>();
		model.languages.add(new LanguageTest().createModelExample(index));
		model.occupation = "occupation " + index;
		model._creationTs = 1234567891 + index;
		model._lastUpdateTs = 1234567991 + index * 2;
		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WeNetUserProfile createModelExample(int index) {

		final WeNetUserProfile model = this.createBasicExample(index);
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
		final WeNetUserProfile model = this.createModelExample(index);
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

		testContext.assertFailure(model.validate("codePrefix", repository)).setHandler(result -> testContext.verify(() -> {

			final Throwable cause = result.cause();
			assertThat(cause).isInstanceOf(ValidationErrorException.class);
			String expectedCode = "codePrefix";
			if (suffix != null && suffix.length() > 0) {

				expectedCode += "." + suffix;
			}
			assertThat(((ValidationErrorException) cause).getCode()).isEqualTo(expectedCode);
			testContext.completeNow();
		}));

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
	 * Check that not accept profiles with bad relationships.
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
	 * Check that not accept profiles with duplicated relationships.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithADuplicatedRelationships(ProfilesRepository repository,
			VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			final WeNetUserProfile model = new WeNetUserProfile();
			model.relationships = new ArrayList<>();
			model.relationships.add(new SocialNetworkRelationship());
			model.relationships.add(new SocialNetworkRelationship());
			model.relationships.get(0).userId = stored.id;
			model.relationships.get(0).type = SocialNetworkRelationshipType.friend;
			model.relationships.get(1).userId = stored.id;
			model.relationships.get(1).type = SocialNetworkRelationshipType.friend;
			this.assertFailValidate(model, "relationships[1]", repository, testContext);

		}));

	}

	/**
	 * Check that is valid with some relationships.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldBeValidWithSomeRelationships(ProfilesRepository repository, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			final WeNetUserProfile model = new WeNetUserProfile();
			model.relationships = new ArrayList<>();
			model.relationships.add(new SocialNetworkRelationship());
			model.relationships.add(new SocialNetworkRelationship());
			model.relationships.get(0).userId = stored.id;
			model.relationships.get(0).type = SocialNetworkRelationshipType.family;
			model.relationships.get(1).userId = stored.id;
			model.relationships.get(1).type = SocialNetworkRelationshipType.friend;
			testContext.assertComplete(model.validate("codePrefix", repository))
					.setHandler(result -> testContext.completeNow());

		}));

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

	/**
	 * Check that a model can not be merged.
	 *
	 * @param model       to validate.
	 * @param suffix      to the error code.
	 * @param repository  to use.
	 * @param source      to merge.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	public void assertFailMerge(WeNetUserProfile model, String suffix, ProfilesRepository repository,
			WeNetUserProfile source, VertxTestContext testContext) {

		testContext.assertFailure(model.merge(source, "codePrefix", repository))
				.setHandler(result -> testContext.verify(() -> {

					final Throwable cause = result.cause();
					assertThat(cause).isInstanceOf(ValidationErrorException.class);
					String expectedCode = "codePrefix";
					if (suffix != null && suffix.length() > 0) {

						expectedCode += "." + suffix;
					}
					assertThat(((ValidationErrorException) cause).getCode()).isEqualTo(expectedCode);
					testContext.completeNow();
				}));

	}

	/**
	 * Check that the name is not valid.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadName(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.name = new UserNameTest().createModelExample(1);
		source.name.first = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "name.first", repository, source, testContext);

	}

	/**
	 * Check that the birth date is not valid.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadBirthDate(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.dateOfBirth = new ProfileDateTest().createModelExample(1);
		source.dateOfBirth.month = 13;
		this.assertFailMerge(new WeNetUserProfile(), "dateOfBirth.month", repository, source, testContext);

	}

	/**
	 * Check that the birth date is not on the future.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABirthDateOnTheFuture(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.dateOfBirth = new ProfileDate();
		final LocalDate tomorrow = LocalDate.now().plusDays(1);
		source.dateOfBirth.year = tomorrow.getYear();
		source.dateOfBirth.month = (byte) tomorrow.getMonthValue();
		source.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
		this.assertFailMerge(new WeNetUserProfile(), "dateOfBirth", repository, source, testContext);

	}

	/**
	 * Check that the birth date is not before the oldest people on world.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.dateOfBirth = new ProfileDate();
		source.dateOfBirth.year = 1903;
		source.dateOfBirth.month = 1;
		source.dateOfBirth.day = 1;
		this.assertFailMerge(new WeNetUserProfile(), "dateOfBirth", repository, source, testContext);

	}

	/**
	 * Check that not accept profiles with bad email address.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadEmail(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.email = " bad email @ adrress ";
		this.assertFailMerge(new WeNetUserProfile(), "email", repository, source, testContext);

	}

	/**
	 * Check that not accept profiles with bad locale.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadLocale(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.locale = " bad locale";
		this.assertFailMerge(new WeNetUserProfile(), "locale", repository, source, testContext);
	}

	/**
	 * Check that not accept profiles with bad phone number.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadPhoneNumber(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.phoneNumber = " bad phone number";
		this.assertFailMerge(new WeNetUserProfile(), "phoneNumber", repository, source, testContext);

	}

	/**
	 * Check that not accept profiles with bad avatar address.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadAvatar(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.avatar = " bad avatar";
		this.assertFailMerge(new WeNetUserProfile(), "avatar", repository, source, testContext);
	}

	/**
	 * Check that not accept profiles with bad nationality.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadNationality(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.nationality = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "nationality", repository, source, testContext);

	}

	/**
	 * Check that not accept profiles with bad languages.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadLanguages(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.languages = new ArrayList<>();
		source.languages.add(new Language());
		source.languages.add(new Language());
		source.languages.add(new Language());
		source.languages.get(1).code = "bad code";
		this.assertFailMerge(new WeNetUserProfile(), "languages[1].code", repository, source, testContext);

	}

	/**
	 * Check that not accept profiles with bad occupation.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadOccupation(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.occupation = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "occupation", repository, source, testContext);

	}

	/**
	 * Check that not accept profiles with bad norms.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadNorms(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).attribute = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "norms[1].attribute", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated social practice identifiers.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedNormIds(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		source.norms.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.norms = new ArrayList<>();
		target.norms.add(new Norm());
		target.norms.get(0).id = "1";
		this.assertFailMerge(target, "norms[2].id", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with not defined social practice id.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecNormId(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		this.assertFailMerge(new WeNetUserProfile(), "norms[1].id", repository, source, testContext);

	}

	/**
	 * Check merge social practices profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeWithNorms(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.norms = new ArrayList<>();
		target.norms.add(new Norm());
		target.norms.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged.norms).isNotEqualTo(target.norms).isEqualTo(source.norms);
					assertThat(merged.norms.get(0).id).isNotEmpty();
					assertThat(merged.norms.get(1).id).isEqualTo("1");
					assertThat(merged.norms.get(2).id).isNotEmpty();
					testContext.completeNow();
				})));

	}

	/**
	 * Check that not accept profiles with bad planned activities.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadPlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).description = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "plannedActivities[1].description", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated planned activity identifiers.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedPlannedActivityIds(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).id = "1";
		source.plannedActivities.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.plannedActivities = new ArrayList<>();
		target.plannedActivities.add(new PlannedActivity());
		target.plannedActivities.get(0).id = "1";
		this.assertFailMerge(target, "plannedActivities[2].id", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with not defined planned activity id.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecPlannedActivityId(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).id = "1";
		this.assertFailMerge(new WeNetUserProfile(), "plannedActivities[1].id", repository, source, testContext);

	}

	/**
	 * Check merge planned activities profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeWithPlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.plannedActivities = new ArrayList<>();
		target.plannedActivities.add(new PlannedActivity());
		target.plannedActivities.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).id = "1";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged.plannedActivities).isNotEqualTo(target.plannedActivities)
							.isEqualTo(source.plannedActivities);
					assertThat(merged.plannedActivities.get(0).id).isNotEmpty();
					assertThat(merged.plannedActivities.get(1).id).isEqualTo("1");
					assertThat(merged.plannedActivities.get(2).id).isNotEmpty();
					testContext.completeNow();
				})));

	}

	/**
	 * Check that not accept profiles with bad relevant locations.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.relevantLocations = new ArrayList<>();
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.get(1).label = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "relevantLocations[1].label", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated relevant location identifiers.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedRelevantLocationIds(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.relevantLocations = new ArrayList<>();
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.get(1).id = "1";
		source.relevantLocations.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.relevantLocations = new ArrayList<>();
		target.relevantLocations.add(new RelevantLocation());
		target.relevantLocations.get(0).id = "1";
		this.assertFailMerge(target, "relevantLocations[2].id", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with not defined relevant location id.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecRelevantLocationId(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.relevantLocations = new ArrayList<>();
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.get(1).id = "1";
		this.assertFailMerge(new WeNetUserProfile(), "relevantLocations[1].id", repository, source, testContext);

	}

	/**
	 * Check merge relevant locations profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeWithRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.relevantLocations = new ArrayList<>();
		target.relevantLocations.add(new RelevantLocation());
		target.relevantLocations.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.relevantLocations = new ArrayList<>();
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.get(1).id = "1";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged.relevantLocations).isNotEqualTo(target.relevantLocations)
							.isEqualTo(source.relevantLocations);
					assertThat(merged.relevantLocations.get(0).id).isNotEmpty();
					assertThat(merged.relevantLocations.get(1).id).isEqualTo("1");
					assertThat(merged.relevantLocations.get(2).id).isNotEmpty();
					testContext.completeNow();
				})));

	}

	/**
	 * Check that not accept profiles with bad planned activities.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadRelationships(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.relationships = new ArrayList<>();
		source.relationships.add(new SocialNetworkRelationship());
		this.assertFailMerge(new WeNetUserProfile(), "relationships[0].type", repository, source, testContext);

	}

	/**
	 * Check that not merge with duplicated relationships.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithDuplicatedRelationships(ProfilesRepository repository, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			final WeNetUserProfile source = new WeNetUserProfile();
			source.relationships = new ArrayList<>();
			source.relationships.add(new SocialNetworkRelationship());
			source.relationships.add(new SocialNetworkRelationship());
			source.relationships.get(0).userId = stored.id;
			source.relationships.get(0).type = SocialNetworkRelationshipType.friend;
			source.relationships.get(1).userId = stored.id;
			source.relationships.get(1).type = SocialNetworkRelationshipType.friend;
			this.assertFailMerge(new WeNetUserProfile(), "relationships[1]", repository, source, testContext);

		}));

	}

	/**
	 * Check that merge some relationships.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeRelationships(ProfilesRepository repository, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			final WeNetUserProfile target = new WeNetUserProfile();
			target.relationships = new ArrayList<>();
			target.relationships.add(new SocialNetworkRelationship());
			target.relationships.get(0).userId = stored.id;
			target.relationships.get(0).type = SocialNetworkRelationshipType.friend;

			final WeNetUserProfile source = new WeNetUserProfile();
			source.relationships = new ArrayList<>();
			source.relationships.add(new SocialNetworkRelationship());
			source.relationships.add(new SocialNetworkRelationship());
			source.relationships.get(0).userId = stored.id;
			source.relationships.get(0).type = SocialNetworkRelationshipType.family;
			source.relationships.get(1).userId = stored.id;
			source.relationships.get(1).type = SocialNetworkRelationshipType.friend;
			testContext.assertComplete(target.merge(source, "codePrefix", repository))
					.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

						assertThat(merged.relationships).isNotEqualTo(target.relationships).isEqualTo(source.relationships);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Check that not accept profiles with bad social practices.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadSocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).label = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "socialPractices[1].label", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated social practice identifiers.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedSocialPracticeIds(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).id = "1";
		source.socialPractices.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.socialPractices = new ArrayList<>();
		target.socialPractices.add(new SocialPractice());
		target.socialPractices.get(0).id = "1";
		this.assertFailMerge(target, "socialPractices[2].id", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with not defined social practice id.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecSocialPracticeId(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).id = "1";
		this.assertFailMerge(new WeNetUserProfile(), "socialPractices[1].id", repository, source, testContext);

	}

	/**
	 * Check merge social practices profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeWithSocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.socialPractices = new ArrayList<>();
		target.socialPractices.add(new SocialPractice());
		target.socialPractices.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).id = "1";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged.socialPractices).isNotEqualTo(target.socialPractices).isEqualTo(source.socialPractices);
					assertThat(merged.socialPractices.get(0).id).isNotEmpty();
					assertThat(merged.socialPractices.get(1).id).isEqualTo("1");
					assertThat(merged.socialPractices.get(2).id).isNotEmpty();
					testContext.completeNow();
				})));

	}

	/**
	 * Check that not merge profiles with bad personal behaviors.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithABadPersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).label = ValidationsTest.STRING_256;
		this.assertFailMerge(new WeNetUserProfile(), "personalBehaviors[1].label", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated personal behavior identifiers.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedPersonalBehaviorIds(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).id = "1";
		source.personalBehaviors.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.personalBehaviors = new ArrayList<>();
		target.personalBehaviors.add(new Routine());
		target.personalBehaviors.get(0).id = "1";
		this.assertFailMerge(target, "personalBehaviors[2].id", repository, source, testContext);

	}

	/**
	 * Check that not merge profiles with not defined personal behavior id.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecPersonalBehaviorId(ProfilesRepository repository,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).id = "1";
		this.assertFailMerge(new WeNetUserProfile(), "personalBehaviors[1].id", repository, source, testContext);

	}

	/**
	 * Check merge personal behaviors profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeWithPersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.personalBehaviors = new ArrayList<>();
		target.personalBehaviors.add(new Routine());
		target.personalBehaviors.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).id = "1";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged.personalBehaviors).isNotEqualTo(target.personalBehaviors)
							.isEqualTo(source.personalBehaviors);
					assertThat(merged.personalBehaviors.get(0).id).isNotEmpty();
					assertThat(merged.personalBehaviors.get(1).id).isEqualTo("1");
					assertThat(merged.personalBehaviors.get(2).id).isNotEmpty();
					testContext.completeNow();
				})));

	}

	/**
	 * Check merge empty profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeEmptyModels(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		final WeNetUserProfile source = new WeNetUserProfile();
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged).isEqualTo(target);
					testContext.completeNow();
				})));

	}

	/**
	 * Check merge basic profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeBasicModels(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.id = "1";
		target._creationTs = 2;
		target._lastUpdateTs = 3;
		final WeNetUserProfile source = new WeNetUserProfile();
		source.id = "4";
		source._creationTs = 5;
		source._lastUpdateTs = 6;
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged).isEqualTo(target).isNotEqualTo(source);
					testContext.completeNow();
				})));

	}

	/**
	 * Check merge example profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeExampleModels(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createModelExample(1);
		target.id = "1";
		final WeNetUserProfile source = this.createModelExample(2);
		source.id = "2";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					source.id = target.id;
					source._creationTs = target._creationTs;
					source._lastUpdateTs = target._lastUpdateTs;
					assertThat(merged).isNotEqualTo(target).isEqualTo(source);
					testContext.completeNow();
				})));

	}

	/**
	 * Check merge stored profiles.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeStoredModels(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			testContext.assertComplete(this.createModelExample(2, repository)).setHandler(sourceToStore -> {

				repository.storeProfile(targetToStore.result(), testContext.succeeding(target -> {

					repository.storeProfile(sourceToStore.result(), testContext.succeeding(source -> {

						testContext.assertComplete(target.merge(source, "codePrefix", repository))
								.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

									source.id = target.id;
									source._creationTs = target._creationTs;
									source._lastUpdateTs = target._lastUpdateTs;
									assertThat(merged).isNotEqualTo(target).isEqualTo(source);
									testContext.completeNow();
								})));

					}));

				}));

			});

		});

	}

	/**
	 * Check merge only the user name.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyUserName(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createModelExample(1);
		testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

			final WeNetUserProfile source = new WeNetUserProfile();
			source.name = new UserName();
			source.name.middle = "NEW MIDDLE NAME";
			testContext.assertComplete(target.merge(source, "codePrefix", repository))
					.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

						assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
						target.name.middle = "NEW MIDDLE NAME";
						assertThat(merged).isEqualTo(target);
						testContext.completeNow();
					})));
		});

	}

	/**
	 * Check merge add user name.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddUserName(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.name = new UserName();
		source.name.middle = "NEW MIDDLE NAME";
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
					target.name = new UserName();
					target.name.middle = "NEW MIDDLE NAME";
					assertThat(merged).isEqualTo(target);
					testContext.completeNow();
				})));

	}

	/**
	 * Check merge only the birth date.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyBirthDate(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

			final WeNetUserProfile source = new WeNetUserProfile();
			source.dateOfBirth = new ProfileDate();
			source.dateOfBirth.year = 1923;
			testContext.assertComplete(target.merge(source, "codePrefix", repository))
					.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

						assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
						target.dateOfBirth.year = 1923;
						assertThat(merged).isEqualTo(target);
						testContext.completeNow();
					})));
		});

	}

	/**
	 * Check merge add birth date.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddBirthDate(ProfilesRepository repository, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.dateOfBirth = new ProfileDateTest().createModelExample(1);
		testContext.assertComplete(target.merge(source, "codePrefix", repository))
				.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
					target.dateOfBirth = new ProfileDateTest().createModelExample(1);
					assertThat(merged).isEqualTo(target);
					testContext.completeNow();
				})));

	}

	/**
	 * Check merge only the gender.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyGender(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.gender = Gender.M;
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.gender = Gender.M;
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge only the email.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyEmail(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.email = "new@email.com";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.email = "new@email.com";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge only the locale.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyLocale(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.locale = "en_NZ";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.locale = "en_NZ";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge only the phone number.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyPhoneNumber(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.phoneNumber = "+1 412 535 2223";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.phoneNumber = "+1 412-535-2223";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge only the avatar.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyAvatar(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.avatar = "http://new-avatar.com";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.avatar = "http://new-avatar.com";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge only the nationality.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyNationality(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.nationality = "Canadian";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.nationality = "Canadian";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge only the occupation.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeOnlyOccupation(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.occupation = "Bus driver";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.occupation = "Bus driver";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge remove languages.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeRemoveLanguages(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.languages = new ArrayList<>();
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.languages.clear();
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge add language and modify another.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddAndModifyLanguages(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			target.languages.add(new LanguageTest().createModelExample(2));
			target.languages.get(1).code = "en";
			target.languages.add(new LanguageTest().createModelExample(3));
			target.languages.get(2).code = "fr";
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.languages = new ArrayList<>();
				source.languages.add(new LanguageTest().createModelExample(2));
				source.languages.add(new LanguageTest().createModelExample(4));
				source.languages.add(new LanguageTest().createModelExample(3));
				source.languages.add(new LanguageTest().createModelExample(1));
				source.languages.get(0).code = "it";
				source.languages.get(1).code = "es";
				source.languages.get(2).code = "fr";
				source.languages.get(2).level = LanguageLevel.B1;
				source.languages.get(3).name = "Catalan";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.languages.add(target.languages.remove(0));
							target.languages.get(0).code = "it";
							target.languages.add(1, new LanguageTest().createModelExample(4));
							target.languages.get(1).code = "es";
							target.languages.get(2).level = LanguageLevel.B1;
							target.languages.get(3).name = "Catalan";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge remove planned activities.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeRemovePlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.plannedActivities = new ArrayList<>();
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.plannedActivities.clear();
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check fail merge with a bad defined planned activity.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadPlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.plannedActivities = new ArrayList<>();
				source.plannedActivities.add(new PlannedActivity());
				source.plannedActivities.get(0).id = target.plannedActivities.get(0).id;
				source.plannedActivities.get(0).description = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "plannedActivities[0].description", repository, source, testContext);
			});
		});
	}

	/**
	 * Check fail merge with a bad new planned activity.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadNewPlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.plannedActivities = new ArrayList<>();
				source.plannedActivities.add(new PlannedActivity());
				source.plannedActivities.get(0).description = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "plannedActivities[0].description", repository, source, testContext);
			});
		});
	}

	/**
	 * Check merge add modify planned activities.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddmodifyPlannedActivities(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.plannedActivities = new ArrayList<>();
				source.plannedActivities.add(new PlannedActivity());
				source.plannedActivities.add(new PlannedActivity());
				source.plannedActivities.get(0).id = target.plannedActivities.get(1).id;
				source.plannedActivities.get(0).description = "NEW description";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.plannedActivities.remove(0);
							target.plannedActivities.get(0).description = "NEW description";
							target.plannedActivities.add(new PlannedActivity());
							target.plannedActivities.get(1).id = merged.plannedActivities.get(1).id;
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge remove relevant locations.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeRemoveRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.relevantLocations = new ArrayList<>();
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.relevantLocations.clear();
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check fail merge with a bad defined relevant location.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.relevantLocations = new ArrayList<>();
				source.relevantLocations.add(new RelevantLocation());
				source.relevantLocations.get(0).id = target.relevantLocations.get(0).id;
				source.relevantLocations.get(0).label = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "relevantLocations[0].label", repository, source, testContext);
			});
		});
	}

	/**
	 * Check fail merge with a bad new relevant location.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadNewRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.relevantLocations = new ArrayList<>();
				source.relevantLocations.add(new RelevantLocation());
				source.relevantLocations.get(0).label = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "relevantLocations[0].label", repository, source, testContext);
			});
		});
	}

	/**
	 * Check merge add modify relevant locations.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddModifyRelevantLocations(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.relevantLocations = new ArrayList<>();
				source.relevantLocations.add(new RelevantLocation());
				source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
				source.relevantLocations.get(1).id = target.relevantLocations.get(0).id;
				source.relevantLocations.get(1).label = "NEW label";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.relevantLocations.add(0, new RelevantLocation());
							target.relevantLocations.get(0).id = merged.relevantLocations.get(0).id;
							target.relevantLocations.get(1).label = "NEW label";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge remove social practices.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeRemoveSocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.socialPractices = new ArrayList<>();
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.socialPractices.clear();
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check fail merge with a bad defined social practice.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadSocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.socialPractices = new ArrayList<>();
				source.socialPractices.add(new SocialPractice());
				source.socialPractices.get(0).id = target.socialPractices.get(0).id;
				source.socialPractices.get(0).label = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "socialPractices[0].label", repository, source, testContext);
			});
		});
	}

	/**
	 * Check fail merge with a bad new social practice.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldFailMergeBadNewSocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.socialPractices = new ArrayList<>();
				source.socialPractices.add(new SocialPractice());
				source.socialPractices.get(0).label = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "socialPractices[0].label", repository, source, testContext);
			});
		});
	}

	/**
	 * Check merge add modify social practices.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddModifySocialPractices(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.socialPractices = new ArrayList<>();
				source.socialPractices.add(new SocialPractice());
				source.socialPractices.add(new SocialPractice());
				source.socialPractices.get(1).id = target.socialPractices.get(0).id;
				source.socialPractices.get(1).label = "NEW label";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.socialPractices.add(0, new SocialPractice());
							target.socialPractices.get(0).id = merged.socialPractices.get(0).id;
							target.socialPractices.get(1).label = "NEW label";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check merge remove personal behaviors.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeRemovePersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.personalBehaviors = new ArrayList<>();
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.personalBehaviors.clear();
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

	/**
	 * Check fail merge with a bad defined personal behavior.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadPersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.personalBehaviors = new ArrayList<>();
				source.personalBehaviors.add(new Routine());
				source.personalBehaviors.get(0).id = target.personalBehaviors.get(0).id;
				source.personalBehaviors.get(0).label = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "personalBehaviors[0].label", repository, source, testContext);
			});
		});
	}

	/**
	 * Check fail merge with a bad new personal behavior.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadNewPersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.personalBehaviors = new ArrayList<>();
				source.personalBehaviors.add(new Routine());
				source.personalBehaviors.get(0).label = ValidationsTest.STRING_256;
				this.assertFailMerge(target, "personalBehaviors[0].label", repository, source, testContext);
			});
		});
	}

	/**
	 * Check merge add modify personal behaviors.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, ProfilesRepository)
	 */
	@Test
	public void shouldMergeAddModifyPersonalBehaviors(ProfilesRepository repository, VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(1, repository)).setHandler(targetToStore -> {

			final WeNetUserProfile target = targetToStore.result();
			testContext.assertComplete(target.validate("codePrefix", repository)).setHandler(none -> {

				final WeNetUserProfile source = new WeNetUserProfile();
				source.personalBehaviors = new ArrayList<>();
				source.personalBehaviors.add(new Routine());
				source.personalBehaviors.add(new Routine());
				source.personalBehaviors.get(1).id = target.personalBehaviors.get(0).id;
				source.personalBehaviors.get(1).label = "NEW label";
				testContext.assertComplete(target.merge(source, "codePrefix", repository))
						.setHandler(testContext.succeeding(merged -> testContext.verify(() -> {

							assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
							target.personalBehaviors.add(0, new Routine());
							target.personalBehaviors.get(0).id = merged.personalBehaviors.get(0).id;
							target.personalBehaviors.get(1).label = "NEW label";
							assertThat(merged).isEqualTo(target);
							testContext.completeNow();
						})));
			});
		});
	}

}
