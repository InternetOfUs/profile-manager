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

package eu.internetofus.common.api.models.wenet;

import static eu.internetofus.common.api.models.MergesTest.assertCanMerge;
import static eu.internetofus.common.api.models.MergesTest.assertCannotMerge;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationsTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic test over the classes that extends the {@link PlannedActivity}.
 *
 * @param <T> type of class to test.
 *
 * @see PlannedActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class PlannedActivityTestCase<T extends PlannedActivity> extends ModelTestCase<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T createModelExample(int index) {

		final T activity = this.createEmptyModel();
		activity.id = null;
		activity.startTime = "2017-07-21T17:32:0" + index % 10 + "Z";
		activity.endTime = "2019-07-21T17:32:2" + index % 10 + "Z";
		activity.description = "description_" + index;
		activity.attendees = null;
		activity.status = PlannedActivityStatus.cancelled;
		return activity;
	}

	/**
	 * Create a n empty Planned activity to use in the tests.
	 *
	 * @return a new instance of the model to test.
	 */
	public abstract T createEmptyModel();

	/**
	 * Create an example model that has the specified index.
	 *
	 * @param index       to use in the example.
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @return the example.
	 */
	public Future<T> createModelExample(int index, Vertx vertx, VertxTestContext testContext) {

		final Promise<T> promise = Promise.promise();
		final T activity = this.createModelExample(index);
		activity.attendees = new ArrayList<>();
		this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {

			activity.attendees.add(profile.id);
			promise.complete(activity);
		}));
		activity.status = PlannedActivityStatus.cancelled;

		return promise.future();

	}

	/**
	 * Create a new empty user profile. It has to be stored into the repository.
	 *
	 * @param vertx    event bus to use.
	 * @param creation handler to manage the created user profile.
	 */
	protected abstract void createNewEmptyProfile(Vertx vertx, Handler<AsyncResult<WeNetUserProfile>> creation);

	/**
	 * Check that an empty model is valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String, Vertx)
	 */
	@Test
	public void shouldEmptyModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index       to verify
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
	 * valid.
	 *
	 * @param index       to verify
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleFromRepositoryBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(index, vertx, testContext)
				.onComplete(testContext.succeeding(model -> assertIsValid(model, vertx, testContext)));

	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldFullModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.id = " ";
		model.startTime = " 2017-07-21T17:32:00z ";
		model.endTime = " 2019-09-09t09:02:11Z ";
		model.description = " description ";
		model.attendees = new ArrayList<>();
		model.attendees.add("");
		model.attendees.add(null);
		model.attendees.add(" ");
		model.status = PlannedActivityStatus.tentative;

		assertIsValid(model, vertx, testContext, () -> {

			final T expected = this.createEmptyModel();
			expected.id = model.id;
			expected.startTime = "2017-07-21T17:32:00Z";
			expected.endTime = "2019-09-09T09:02:11Z";
			expected.description = "description";
			expected.attendees = new ArrayList<>();
			expected.status = PlannedActivityStatus.tentative;
			assertThat(model).isEqualTo(expected);
		});

	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnId(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.id = "has_id";
		assertIsNotValid(model, "id", vertx, testContext);

	}

	/**
	 * Check that not accept planned activity with bad start time.
	 *
	 * @param badTime     a bad time value.
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with startTime = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
	public void shouldNotBeValidWithABadStartTime(String badTime, Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.startTime = badTime;
		assertIsNotValid(model, "startTime", vertx, testContext);

	}

	/**
	 * Check that not accept planned activity with bad end time.
	 *
	 * @param badTime     a bad time value.
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with endTime = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
	public void shouldNotBeValidWithABadEndTime(String badTime, Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.endTime = badTime;
		assertIsNotValid(model, "endTime", vertx, testContext);

	}

	/**
	 * Check that not accept planned activity with bad description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadDescription(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.description = ValidationsTest.STRING_256;
		assertIsNotValid(model, "description", vertx, testContext);

	}

	/**
	 * Check that not accept planned activity with bad attender.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadAttender(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.attendees = new ArrayList<>();
		model.attendees.add("undefined attendee identifier");
		assertIsNotValid(model, "attendees[0]", vertx, testContext);

	}

	/**
	 * Check that is valid without attenders.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldBeValidEmptyAttender(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.attendees = new ArrayList<>();
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check is valid with some attenders.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldBeValidWithSomeAttenders(Vertx vertx, VertxTestContext testContext) {

		this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

			this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

				final T model = this.createEmptyModel();
				model.attendees = new ArrayList<>();
				model.attendees.add(stored.id);
				model.attendees.add(stored2.id);
				assertIsValid(model, vertx, testContext);

			}));

		}));

	}

	/**
	 * Check is not valid is one attender is duplicated.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldNotBeValidWithDuplicatedAttenders(Vertx vertx, VertxTestContext testContext) {

		this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

			this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

				final T model = this.createEmptyModel();
				model.attendees = new ArrayList<>();
				model.attendees.add(stored.id);
				model.attendees.add(stored2.id);
				model.attendees.add(stored.id);
				assertIsNotValid(model, "attendees[2]", vertx, testContext);

			}));

		}));

	}

	/**
	 * Check that not accept planned activity with bad attender.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#validate(String,Vertx)
	 */
	@Test
	public void shouldEmptyAttenderWillRemoved(Vertx vertx, VertxTestContext testContext) {

		final T model = this.createEmptyModel();
		model.attendees = new ArrayList<>();
		model.attendees.add(null);
		model.attendees.add("");
		model.attendees.add("      ");

		assertIsValid(model, vertx, testContext, () -> {

			final T expected = this.createEmptyModel();
			expected.attendees = new ArrayList<>();
			assertThat(model).isEqualTo(expected);
		});

	}

	/**
	 * Check that not merge planned activity with bad start time.
	 *
	 * @param badTime     a bad time value.
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with startTime = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
	public void shouldNotMergeWithABadStartTime(String badTime, Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity target = this.createModelExample(1);
		final T source = this.createEmptyModel();
		source.startTime = badTime;
		assertCannotMerge(target, source, "startTime", vertx, testContext);

	}

	/**
	 * Check that not merge planned activity with bad end time.
	 *
	 * @param badTime     a bad time value.
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with endTime = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
	public void shouldNotMergeWithABadEndTime(String badTime, Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity target = this.createModelExample(1);
		final T source = this.createEmptyModel();
		source.endTime = badTime;
		assertCannotMerge(target, source, "endTime", vertx, testContext);

	}

	/**
	 * Check that not merge planned activity with bad description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadDescription(Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity target = this.createModelExample(1);
		final T source = this.createEmptyModel();
		source.description = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "description", vertx, testContext);

	}

	/**
	 * Check that not merge planned activity with bad attender.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadAttender(Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity target = this.createModelExample(1);
		final T source = this.createEmptyModel();
		source.attendees = new ArrayList<>();
		source.attendees.add("undefined attendee identifier");
		assertCannotMerge(target, source, "attendees[0]", vertx, testContext);

	}

	/**
	 * Check that merge without attenders.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeEmptyAttender(Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity target = this.createModelExample(1);
		final T source = this.createEmptyModel();
		source.attendees = new ArrayList<>();
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.attendees).isEmpty());

	}

	/**
	 * Check merge with some attenders.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeWithSomeAttenders(Vertx vertx, VertxTestContext testContext) {

		this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

			this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

				final PlannedActivity target = this.createModelExample(1);
				final T source = this.createEmptyModel();
				source.attendees = new ArrayList<>();
				source.attendees.add(stored.id);
				source.attendees.add(stored2.id);
				assertCanMerge(target, source, vertx, testContext);

			}));

		}));

	}

	/**
	 * Check is not valid is one attender is duplicated.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithDuplicatedAttenders(Vertx vertx, VertxTestContext testContext) {

		this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

			this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

				final PlannedActivity target = this.createModelExample(1);
				final T source = this.createEmptyModel();
				source.attendees = new ArrayList<>();
				source.attendees.add(stored.id);
				source.attendees.add(stored2.id);
				source.attendees.add(stored.id);
				assertCannotMerge(target, source, "attendees[2]", vertx, testContext);

			}));

		}));

	}

	/**
	 * Check that not accept planned activity with bad attender.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeEmptyAttenderWillRemoved(Vertx vertx, VertxTestContext testContext) {

		final PlannedActivity target = this.createModelExample(1);
		final T source = this.createEmptyModel();
		source.attendees = new ArrayList<>();
		source.attendees.add(null);
		source.attendees.add("");
		source.attendees.add("      ");
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.attendees).isNotNull().isEmpty());

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {

			this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(source -> {

				target.id = "1";
				assertCanMerge(target, source, vertx, testContext, merged -> {

					assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
					source.id = "1";
					assertThat(merged).isEqualTo(source);

				});
			}));

		}));

	}

	/**
	 * Check that merge with {@code null} source.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {

			assertCanMerge(target, null, vertx, testContext, merged -> {

				assertThat(merged).isSameAs(target);
				testContext.completeNow();

			});

		}));

	}

	/**
	 * Check that merge only start time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyStartTime(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {
			target.id = "1";
			final T source = this.createEmptyModel();
			source.startTime = "2000-02-19T16:18:00Z";
			assertCanMerge(target, source, vertx, testContext, merged -> {
				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.startTime = "2000-02-19T16:18:00Z";
				assertThat(merged).isEqualTo(target);
				testContext.completeNow();
			});
		}));

	}

	/**
	 * Check that merge only end time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyEndTime(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {

			target.id = "1";
			final T source = this.createEmptyModel();
			source.endTime = "2020-02-19T16:18:00Z";
			assertCanMerge(target, source, vertx, testContext, merged -> {
				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.endTime = "2020-02-19T16:18:00Z";
				assertThat(merged).isEqualTo(target);
				testContext.completeNow();
			});
		}));

	}

	/**
	 * Check that merge only start time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyDescription(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {
			target.id = "1";
			final T source = this.createEmptyModel();
			source.description = "New description";
			assertCanMerge(target, source, vertx, testContext, merged -> {
				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.description = "New description";
				assertThat(merged).isEqualTo(target);
				testContext.completeNow();
			});
		}));

	}

	/**
	 * Check that merge only start time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyStatus(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {
			target.id = "1";
			final T source = this.createEmptyModel();
			source.status = PlannedActivityStatus.tentative;
			assertCanMerge(target, source, vertx, testContext, merged -> {
				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.status = PlannedActivityStatus.tentative;
				assertThat(merged).isEqualTo(target);
				testContext.completeNow();
			});
		}));

	}

	/**
	 * Check that merge only start time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeRemoveAttenders(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {
			target.id = "1";
			final T source = this.createEmptyModel();
			source.attendees = new ArrayList<>();
			assertCanMerge(target, source, vertx, testContext, merged -> {
				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.attendees.clear();
				assertThat(merged).isEqualTo(target);
				testContext.completeNow();
			});
		}));

	}

	/**
	 * Check that merge only start time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
	 */
	@Test
	public void shouldMergeAddNewAttenders(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(target -> {
			target.id = "1";
			this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

				final T source = this.createEmptyModel();
				source.attendees = new ArrayList<>();
				source.attendees.add(stored.id);
				source.attendees.addAll(target.attendees);
				assertCanMerge(target, source, vertx, testContext, merged -> {

					assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
					target.attendees.add(0, stored.id);
					assertThat(merged).isEqualTo(target);
					testContext.completeNow();
				});
			}));
		}));
	}

}
