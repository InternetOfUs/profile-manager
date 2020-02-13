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
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.wenet_profile_manager.ModelTestCase;
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
		activity.startTime = "startTime_" + index;
		activity.endTime = "endTime_" + index;
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
		final PlannedActivity activity = new PlannedActivity();
		activity.id = null;
		activity.startTime = "startTime_" + index;
		activity.endTime = "endTime_" + index;
		activity.description = "description_" + index;
		activity.attendees = new ArrayList<>();
		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.id = UUID.randomUUID().toString();
		repository.storeProfile(profile, stored -> {

			if (stored.failed()) {

				promise.fail(stored.cause());

			} else {

				activity.attendees.add(profile.id);
				promise.complete(activity);
			}

		});
		activity.status = PlannedActivityStatus.cancelled;

		return future;
	}

	/**
	 * Check the copy of a model has to be equals to the original.
	 */
	@Test
	public void shouldCopyBeEqual() {

		final PlannedActivity model1 = this.createModelExample(1);
		final PlannedActivity model2 = new PlannedActivity(model1);
		assertThat(model1).isEqualTo(model2);

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

		testContext.verify(() -> {
			final PlannedActivity model = new PlannedActivity();
			model.validate("codePrefix", repository).onComplete(validate -> {

				if (validate.failed()) {

					testContext.failNow(validate.cause());

				} else {

					testContext.completeNow();
				}

			});
		});

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

		testContext.verify(() -> {
			final PlannedActivity model = this.createModelExample(index);
			model.validate("codePrefix", repository).onComplete(validate -> {

				if (validate.failed()) {

					testContext.failNow(validate.cause());

				} else {

					testContext.completeNow();
				}

			});
		});

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

		testContext.verify(() -> {
			this.createModelExample(index, repository).onComplete(created -> {

				if (created.failed()) {

					testContext.failNow(created.cause());

				} else {

					final PlannedActivity model = created.result();
					model.validate("codePrefix", repository).onComplete(validate -> {

						if (validate.failed()) {

							testContext.failNow(validate.cause());

						} else {

							testContext.completeNow();
						}
					});
				}

			});
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

		testContext.verify(() -> {

			final PlannedActivity model = new PlannedActivity();
			model.id = " ";
			model.startTime = " start time ";
			model.endTime = " end time ";
			model.description = " description ";
			model.attendees = new ArrayList<>();
			model.attendees.add("");
			model.attendees.add(null);
			model.attendees.add(" ");
			model.status = PlannedActivityStatus.tentative;
			model.validate("codePrefix", repository).onComplete(validate -> {

				if (validate.failed()) {

					testContext.failNow(validate.cause());

				} else {

					final PlannedActivity expected = new PlannedActivity();
					expected.id = model.id;
					expected.startTime = "start time";
					expected.endTime = "end time";
					expected.description = "description";
					expected.attendees = new ArrayList<>();
					expected.status = PlannedActivityStatus.tentative;
					assertThat(model).isEqualTo(expected);
					testContext.completeNow();
				}
			});
		});

	}

	// /**
	// * Check that the model with id is not valid.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithAnId() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.id = "has_id";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.id");
	// }
	//
	// /**
	// * Check that not accept planned activity with bad start time.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadStartTime() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.startTime = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.startTime");
	// }
	//
	// /**
	// * Check that not accept planned activity with bad end time.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadEndTime() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.endTime = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.endTime");
	// }
	//
	// /**
	// * Check that not accept planned activity with bad description.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadDescription() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.description = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.description");
	// }

}
