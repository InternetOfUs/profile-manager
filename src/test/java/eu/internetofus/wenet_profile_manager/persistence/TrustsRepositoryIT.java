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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

package eu.internetofus.wenet_profile_manager.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import eu.internetofus.common.model.TimeManager;
import eu.internetofus.common.model.Model;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator;
import eu.internetofus.wenet_profile_manager.api.trusts.UserPerformanceRatingEvent;
import eu.internetofus.wenet_profile_manager.api.trusts.UserPerformanceRatingEventTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Integration test over the {@link TrustsRepository}.
 *
 * @see TrustsRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class TrustsRepositoryIT {

  /**
   * Verify that can not store a trust that can not be an object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#storeTrustEvent(UserPerformanceRatingEvent)
   */
  @Test
  public void shouldNotStoreATrustThatCanNotBeAnObject(final Vertx vertx, final VertxTestContext testContext) {

    final UserPerformanceRatingEvent trust = new UserPerformanceRatingEvent() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObject() {

        return null;
      }
    };
    testContext.assertFailure(TrustsRepository.createProxy(vertx).storeTrustEvent(trust))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can store an event.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#storeTrustEvent(UserPerformanceRatingEvent)
   */
  @Test
  public void shouldStoreAnEvent(final Vertx vertx, final VertxTestContext testContext) {

    final var now = TimeManager.now();
    final var event = new UserPerformanceRatingEventTest().createModelExample(1);
    testContext.assertComplete(TrustsRepository.createProxy(vertx).storeTrustEvent(event))
        .onSuccess(stored -> testContext.verify(() -> {

          assertThat(stored).isNotNull();
          assertThat(stored.reportTime).isGreaterThanOrEqualTo(now);
          event.reportTime = stored.reportTime;
          assertThat(stored).isEqualTo(event);
          testContext.completeNow();
        }));

  }

  /**
   * Store some events.
   *
   * @param max         number of events to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param change      function to modify the pattern before to store it. The
   *                    first argument is the index and the second the created
   *                    event.
   *
   * @return the future with the created tasks.
   */
  public static Future<List<UserPerformanceRatingEvent>> assertStoreMultipleUserPerformanceRatingEvent(final int max,
      final Vertx vertx, final VertxTestContext testContext,
      final BiConsumer<Integer, UserPerformanceRatingEvent> change) {

    final var future = new UserPerformanceRatingEventTest().createModelExample(1, vertx, testContext)
        .compose(pattern -> {

          Future<List<UserPerformanceRatingEvent>> eventsFuture = Future.succeededFuture(new ArrayList<>());
          for (var i = 0; i < max; i++) {
            final var event = Model.fromJsonObject(pattern.toJsonObject(), UserPerformanceRatingEvent.class);
            event.rating = Math.random();
            if (change != null) {

              change.accept(i, event);
            }

            eventsFuture = eventsFuture
                .compose(events -> TrustsRepository.createProxy(vertx).storeTrustEvent(event).compose(storedEvent -> {

                  events.add(storedEvent);
                  return Future.succeededFuture(events);

                }));
          }

          return eventsFuture;

        });
    return testContext.assertComplete(future);

  }

  /**
   * Verify the trust calculus.
   *
   * @param aggregatorName name of the aggregation to use.
   * @param value          for the trust that it has to obtain.
   * @param vertx          event bus to use.
   * @param testContext    context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator,
   *      JsonObject)
   */
  @ParameterizedTest(name = "Should calcuate the trust for {0}")
  @CsvSource(value = { "MAXIMUM,1.0", "MINIMUM,0.0", "AVERAGE,0.5", "MEDIAN,0.5", "RECENCY_BASED,0.5" })
  public void shouldCalculateTrust(final String aggregatorName, final String value, final Vertx vertx,
      final VertxTestContext testContext) {

    final var aggregator = TrustAggregator.valueOf(aggregatorName);
    final var expectedTrust = Double.parseDouble(value);
    assertStoreMultipleUserPerformanceRatingEvent(5, vertx, testContext, (index, event) -> {
      if (index % 2 == 0) {

        event.rating = index * (1.0 / 4.0);

      } else {

        event.rating = (4 - index) * (1.0 / 4.0);
      }
    }).onSuccess(events -> {

      final var event0 = events.get(0);
      final var query = new JsonObject().put("sourceId", event0.sourceId).put("targetId", event0.targetId);
      testContext.assertComplete(TrustsRepository.createProxy(vertx).calculateTrustBy(aggregator, query))
          .onSuccess(trust -> testContext.verify(() -> {

            assertThat(trust).isEqualTo(expectedTrust, offset(0.0000000001d));
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify the maximum trust calculus.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator,
   *      JsonObject)
   */
  @Test
  public void shouldCalculateMaximumTrust(final Vertx vertx, final VertxTestContext testContext) {

    assertStoreMultipleUserPerformanceRatingEvent(100, vertx, testContext, null).onSuccess(events -> {

      final var event0 = events.get(0);
      final var query = new JsonObject().put("sourceId", event0.sourceId).put("targetId", event0.targetId);
      testContext.assertComplete(TrustsRepository.createProxy(vertx).calculateTrustBy(TrustAggregator.MAXIMUM, query))
          .onSuccess(trust -> testContext.verify(() -> {

            var max = 0d;
            for (final UserPerformanceRatingEvent event : events) {

              max = Math.max(max, event.rating);

            }
            assertThat(trust).isEqualTo(max, offset(0.0000000001d));
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify the minimum trust calculus.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator,
   *      JsonObject)
   */
  @Test
  public void shouldCalculateMinimumTrust(final Vertx vertx, final VertxTestContext testContext) {

    assertStoreMultipleUserPerformanceRatingEvent(100, vertx, testContext, null).onSuccess(events -> {

      final var event0 = events.get(0);
      final var query = new JsonObject().put("sourceId", event0.sourceId).put("targetId", event0.targetId);
      testContext.assertComplete(TrustsRepository.createProxy(vertx).calculateTrustBy(TrustAggregator.MINIMUM, query))
          .onSuccess(trust -> testContext.verify(() -> {

            var min = 1d;
            for (final UserPerformanceRatingEvent event : events) {

              min = Math.min(min, event.rating);

            }
            assertThat(trust).isEqualTo(min, offset(0.0000000001d));
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify the average trust calculus.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator,
   *      JsonObject)
   */
  @Test
  public void shouldCalculateAverageTrust(final Vertx vertx, final VertxTestContext testContext) {

    final var maxEvents = 100;
    assertStoreMultipleUserPerformanceRatingEvent(maxEvents, vertx, testContext, null).onSuccess(events -> {

      final var event0 = events.get(0);
      final var query = new JsonObject().put("sourceId", event0.sourceId).put("targetId", event0.targetId);
      testContext.assertComplete(TrustsRepository.createProxy(vertx).calculateTrustBy(TrustAggregator.AVERAGE, query))
          .onSuccess(trust -> testContext.verify(() -> {

            var avg = 0d;
            for (final UserPerformanceRatingEvent event : events) {

              avg += event.rating;

            }
            avg /= maxEvents;
            assertThat(trust).isEqualTo(avg, offset(0.0000000001d));
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify the median trust calculus.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator,
   *      JsonObject)
   */
  @Test
  public void shouldCalculateMedianTrust(final Vertx vertx, final VertxTestContext testContext) {

    final var maxEvents = 100;
    assertStoreMultipleUserPerformanceRatingEvent(maxEvents, vertx, testContext, null).onSuccess(events -> {

      final var event0 = events.get(0);
      final var query = new JsonObject().put("sourceId", event0.sourceId).put("targetId", event0.targetId);
      testContext.assertComplete(TrustsRepository.createProxy(vertx).calculateTrustBy(TrustAggregator.MEDIAN, query))
          .onSuccess(trust -> testContext.verify(() -> {

            events.sort((e1, e2) -> Double.compare(e1.rating, e2.rating));
            final var medianEvent = events.get(maxEvents / 2 - 1);
            assertThat(trust).isEqualTo(medianEvent.rating, offset(0.0000000001d));
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify the recency added trust calculus.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator,
   *      JsonObject)
   */
  @Test
  public void shouldCalculateRecencyAdded(final Vertx vertx, final VertxTestContext testContext) {

    final var maxEvents = 100;
    assertStoreMultipleUserPerformanceRatingEvent(maxEvents, vertx, testContext, (index, event) -> {

      if (index >= maxEvents - 6) {

        try {
          Thread.sleep(1000);
        } catch (final InterruptedException ignored) {
        }
      }

    }).onSuccess(events -> {

      final var event0 = events.get(0);
      final var query = new JsonObject().put("sourceId", event0.sourceId).put("targetId", event0.targetId);
      testContext
          .assertComplete(TrustsRepository.createProxy(vertx).calculateTrustBy(TrustAggregator.RECENCY_BASED, query))
          .onSuccess(trust -> testContext.verify(() -> {

            events.sort((e1, e2) -> Long.compare(e2.reportTime, e1.reportTime));
            var avg = 0d;
            for (final UserPerformanceRatingEvent event : events.subList(0, 5)) {

              avg += event.rating;

            }
            avg /= 5.0;
            assertThat(trust).isEqualTo(avg, offset(0.0000000001d));
            testContext.completeNow();
          }));

    });

  }

  /**
   * Should not calculate trust by {@code null} aggregator.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepository#calculateTrustBy(TrustAggregator, JsonObject)
   */
  @Test
  public void shouldNotCalculateTrustWithNullAggregator(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(TrustsRepository.createProxy(vertx).calculateTrustBy(null, null))
        .onFailure(failed -> testContext.completeNow());

  }

}
