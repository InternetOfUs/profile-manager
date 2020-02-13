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
 * Test the {@link PlannedActivity}.
 *
 * @see PlannedActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class PlannedActivityTest extends ModelTestCase<PlannedActivity> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlannedActivity createModelExample(int index) {

		final PlannedActivity activity = new PlannedActivity();
		activity.id = null;
		activity.startTime = "2017-07-21T17:32:0" + index % 10 + "Z";
		activity.endTime = "2019-07-21T17:32:2" + index % 10 + "Z";
		activity.description = "description_" + index;
		activity.attendees = null;
		activity.status = PlannedActivityStatus.cancelled;
		return activity;
	}

	/**
	 * Create an example model that has the specified index.
	 *
	 * @param index      to use in the example.
	 * @param repository to use to create the model.
	 *
	 * @return the example.
	 */
	public Future<PlannedActivity> createModelExample(int index, ProfilesRepository repository) {

		final Promise<PlannedActivity> promise = Promise.promise();
		final Future<PlannedActivity> future = promise.future();
		final PlannedActivity activity = this.createModelExample(index);
		activity.attendees = new ArrayList<>();
		repository.storeProfile(new WeNetUserProfile(), stored -> {

			if (stored.failed()) {

				promise.fail(stored.cause());

			} else {

				final WeNetUserProfile profile = stored.result();
				activity.attendees.add(profile.id);
				promise.complete(activity);
			}

		});
		activity.status = PlannedActivityStatus.cancelled;

		return future;
	}

	/**
	 * Check that an empty model is valid.
	 *
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@Test
	public void shouldEmptyModelBeValid(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		testContext.assertComplete(model.validate("codePrefix", repository))
				.setHandler(validation -> testContext.completeNow());
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index       to verify
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = this.createModelExample(index);
		testContext.assertComplete(model.validate("codePrefix", repository))
				.setHandler(validation -> testContext.completeNow());

	}

	/**
	 * Check that the {@link #createModelExample(int,ProfilesRepository)} is valid.
	 *
	 * @param index       to verify
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleFromRepositoryBeValid(int index, ProfilesRepository repository,
			VertxTestContext testContext) {

		testContext.assertComplete(this.createModelExample(index, repository)).setHandler(created -> {

			final PlannedActivity model = created.result();
			testContext.assertComplete(model.validate("codePrefix", repository))
					.setHandler(validation -> testContext.completeNow());
		});
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldFullModelBeValid(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.id = " ";
		model.startTime = " 2017-07-21T17:32:00z ";
		model.endTime = " 2019-09-09t09:02:11Z ";
		model.description = " description ";
		model.attendees = new ArrayList<>();
		model.attendees.add("");
		model.attendees.add(null);
		model.attendees.add(" ");
		model.status = PlannedActivityStatus.tentative;
		testContext.assertComplete(model.validate("codePrefix", repository)).setHandler(validate -> {

			final PlannedActivity expected = new PlannedActivity();
			expected.id = model.id;
			expected.startTime = "2017-07-21T17:32:00Z";
			expected.endTime = "2019-09-09T09:02:11Z";
			expected.description = "description";
			expected.attendees = new ArrayList<>();
			expected.status = PlannedActivityStatus.tentative;
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
	public void assertFailValidate(PlannedActivity model, String suffix, ProfilesRepository repository,
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
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithAnId(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.id = "has_id";
		this.assertFailValidate(model, "id", repository, testContext);
	}

	/**
	 * Check that not accept planned activity with bad start time.
	 *
	 * @param badTime     a bad time value.
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@ParameterizedTest(name = "Should not be valid with startTime = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
	public void shouldNotBeValidWithABadStartTime(String badTime, ProfilesRepository repository,
			VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.startTime = badTime;
		this.assertFailValidate(model, "startTime", repository, testContext);
	}

	/**
	 * Check that not accept planned activity with bad end time.
	 *
	 * @param badTime     a bad time value.
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@ParameterizedTest(name = "Should not be valid with endTime = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
	public void shouldNotBeValidWithABadEndTime(String badTime, ProfilesRepository repository,
			VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.endTime = badTime;
		this.assertFailValidate(model, "endTime", repository, testContext);
	}

	/**
	 * Check that not accept planned activity with bad description.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadDescription(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.description = ValidationsTest.STRING_256;
		this.assertFailValidate(model, "description", repository, testContext);

	}

	/**
	 * Check that not accept planned activity with bad attender.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidWithABadAttender(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.attendees = new ArrayList<>();
		model.attendees.add("undefined attendee identifier");
		this.assertFailValidate(model, "attendees[0]", repository, testContext);

	}

	/**
	 * Check that not accept planned activity with bad attender.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldBeValidEmptyAttender(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.attendees = new ArrayList<>();
		testContext.assertComplete(model.validate("codePrefix", repository))
				.setHandler(validation -> testContext.completeNow());

	}

	/**
	 * Check that not accept planned activity with bad attender.
	 *
	 * @param repository  to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldEmptyAttenderWillRemoved(ProfilesRepository repository, VertxTestContext testContext) {

		final PlannedActivity model = new PlannedActivity();
		model.attendees = new ArrayList<>();
		model.attendees.add(null);
		model.attendees.add("");
		model.attendees.add("      ");
		testContext.assertComplete(model.validate("codePrefix", repository)).setHandler(validation -> {

			assertThat(model.attendees).isEmpty();
			testContext.completeNow();
		});

	}

}
