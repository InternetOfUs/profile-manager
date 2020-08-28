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

package eu.internetofus.wenet_profile_manager.api.trusts;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.queryParam;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.TrustsRepositoryIT;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Trusts}.
 *
 * @see Trusts
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class TrustsIT {

  /**
   * Verify that return error when try to add an event that is not an event.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotAddEventBecauseIsNotAValidEvent(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Trusts.PATH + Trusts.RATING_PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new JsonObject().put("key", "value"), testContext);
  }

  /**
   * Verify that return error when try to add a bad event.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotAddBadEvent(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var event = new UserPerformanceRatingEventTest().createModelExample(1);
    testRequest(client, HttpMethod.POST, Trusts.PATH + Trusts.RATING_PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty().endsWith(".sourceId");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(event.toJsonObject(), testContext);

  }

  /**
   * Verify can add an event.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldAddEvent(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new UserPerformanceRatingEventTest().createModelExample(1, vertx, testContext, testContext.succeeding(event -> {

      final var time = TimeManager.now();
      testRequest(client, HttpMethod.POST, Trusts.PATH + Trusts.RATING_PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
        final var stored = assertThatBodyIs(UserPerformanceRatingEvent.class, res);
        assertThat(stored).isNotNull();
        assertThat(stored.reportTime).isGreaterThanOrEqualTo(time);
        event.reportTime = stored.reportTime;
        assertThat(stored).isEqualTo(event);

      }).sendJson(event.toJsonObject(), testContext);

    }));

  }

  /**
   * Verify that can calculate the trust with a bad regular expression.
   *
   * @param aggregator  that has to fail.
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Trusts#calculateTrust(String, String, String, String, String, String, String, Long, Long, TrustAggregator,
   *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @ParameterizedTest(name = "Shoulf not calculate the {0} trust because a regular expresion is wrong")
  @EnumSource(TrustAggregator.class)
  public void shouldNotCalculateTrustWithBadRegex(final TrustAggregator aggregator, final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Trusts.PATH + "/1/with/2").with(queryParam("aggregator", aggregator.name()), queryParam("appId", "/1(?:{/")).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);
  }

  /**
   * Verify that can calculate the trust because not events match the query.
   *
   * @param aggregator  that has to fail.
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Trusts#calculateTrust(String, String, String, String, String, String, String, Long, Long, TrustAggregator,
   *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @ParameterizedTest(name = "Shoulf not calculate the {0} trust because a regular expresion is wrong")
  @EnumSource(TrustAggregator.class)
  public void shouldNotCalculateTrustWithNoMatchingEvents(final TrustAggregator aggregator, final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    testRequest(client, HttpMethod.GET, Trusts.PATH + "/" + id + "/with/" + id).with(queryParam("aggregator", aggregator.name())).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);
  }

  /**
   * Verify the trust calculus.
   *
   * @param aggregatorName name of the aggregation to use.
   * @param value          for the trust that it has to obtain.
   * @param vertx          event bus to use.
   * @param client         to connect to the server.
   * @param testContext    context to test.
   *
   * @see Trusts#calculateTrust(String, String, String, String, String, String, String, Long, Long, TrustAggregator,
   *      io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @ParameterizedTest(name = "Should calcuate the trust for {0}")
  @CsvSource(value = { "MAXIMUM,1.0", "MINIMUM,0.0", "AVERAGE,0.5", "MEDIAN,0.5", "RECENCY_BASED,0.5" })
  public void shouldCalculateTrust(final String aggregatorName, final String value, final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var aggregator = TrustAggregator.valueOf(aggregatorName);
    final var expectedTrust = Double.parseDouble(value);
    TrustsRepositoryIT.storeMultipleTamesAnUserPerformanceRatingEvent(5, vertx, testContext, (index, event) -> {
      if (index % 2 == 0) {

        event.rating = index * (1.0 / 4.0);

      } else {

        event.rating = (4 - index) * (1.0 / 4.0);
      }
    }).onComplete(testContext.succeeding(events -> {

      final var now = TimeManager.now();
      final var event0 = events.get(0);
      testRequest(client, HttpMethod.GET, Trusts.PATH + "/" + event0.sourceId + "/with/" + event0.targetId).with(queryParam("aggregator", aggregator.name())).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var trust = assertThatBodyIs(Trust.class, res);
        assertThat(trust.value).isNotNull();
        assertThat(trust.value.doubleValue()).isEqualTo(expectedTrust, offset(0.0000000001d));
        assertThat(trust.calculatedTime).isGreaterThanOrEqualTo(now);

      }).send(testContext);

    }));

  }

}
