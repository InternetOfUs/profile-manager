/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 4039 - 4022 UDT-IA, IIIA-CSIC
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

package eu.internetofus.wenet_profile_manager.api.intelligences;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.requestHeader;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.util.ArrayList;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.Questionnaire;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Intelligences}.
 *
 * @see Intelligences
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class IntelligencesIT {

  /**
   * Verify that retrieve the intelligences questionnaire.
   *
   * @param lang        language to obtain the questionnaire.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#retrieveIntelligencesQuestionnaire(io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @ParameterizedTest(name = "Should return intelligences questionnaire for language {0}")
  @EmptySource
  @ValueSource(strings = { "*", "en", "es", "ca", "es-US,es;q=0.5", "ca,es,en", "en-US,en,es", "it" })
  public void shouldRetrieveIntelligencesQuestionnaire(final String lang, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Intelligences.PATH).with(requestHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,es")).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var questionnaire = assertThatBodyIs(Questionnaire.class, res);
      assertThat(questionnaire.questions).hasSize(IntelligencesResource.QUESTION_FACTORS.length);

    }).send(testContext);

  }

  /**
   * Verify that not calculate the intelligences because no answers are passed.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotcalculateGardnerIntelligencesBecauseNoAnswers(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new QuestionnaireAnswers(), testContext);

  }

  /**
   * Verify that not calculate the intelligences because no provide an empty answers list.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotcalculateGardnerIntelligencesBecauseNotEmptyAnswers(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the intelligences because no provide enough answers.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotcalculateGardnerIntelligencesBecauseNotEnoughAnswers(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < IntelligencesResource.QUESTION_FACTORS.length - 1; i++) {

      answers.answerValues.add(0d);
      testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isEqualTo("bad_number_of_answers");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(answers, testContext);

    }

  }

  /**
   * Verify that not calculate the intelligences because provide too many answers.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotcalculateGardnerIntelligencesBecauseTooManyAnswers(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < IntelligencesResource.QUESTION_FACTORS.length + 1; i++) {

      answers.answerValues.add(0d);

    }

    testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the intelligences because an answer value is too low.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotcalculateGardnerIntelligencesBecauseAnswerValueIsTooLow(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < IntelligencesResource.QUESTION_FACTORS.length; i++) {

      answers.answerValues.add(0d);

    }

    answers.answerValues.set(7, -0.01d);

    testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_answer_value_at_7");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the intelligences because an answer value is too high.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotcalculateGardnerIntelligencesBecauseAnswerValueIsTooHigh(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < IntelligencesResource.QUESTION_FACTORS.length; i++) {

      answers.answerValues.add(0d);

    }

    answers.answerValues.set(17, 1.01d);

    testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_answer_value_at_17");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that calculate intelligences.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Intelligences#calculateGardnerIntelligences(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldcalculateGardnerIntelligences(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    Collections.addAll(answers.answerValues, 1d, 0.5d, 1d, 1d, 0d, 0d, 1d, 1d, 0.5d, 0.5d, 0.5d, 0d, 0.5d, 0.5d, 0d, 1d, 1d, 0.5d, 1d, 1d, 1d, 0.5d, 1d, 1d, 0d, 0d, 1d, 1d, 0.5d, 0.5d, 0.5d);

    testRequest(client, HttpMethod.POST, Intelligences.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final JsonArray array = res.bodyAsJsonArray();
      final var intelligences = Model.fromJsonArray(array, Meaning.class);
      assertThat(intelligences).isNotNull();
      final double[] values = { 0.875d, 0.5d, 0.375d, 0.5d, 0.875d, 0.5d, 0.875d, 0.5d };
      for (var factor = 0; factor < IntelligencesResource.FACTOR_NAMES.length; factor++) {

        assertIntelligence(intelligences.get(factor), factor, values[factor]);

      }

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that a meaning contains the specific intelligence factor with the specified value.
   *
   * @param meaning to check that contains the intelligence.
   * @param factor  index of the factor to check.
   * @param value   for the intelligence.
   */
  public static void assertIntelligence(final Meaning meaning, final int factor, final double value) {

    assertThat(meaning).isNotNull();
    assertThat(meaning.name).isEqualTo(IntelligencesResource.FACTOR_NAMES[factor]);
    assertThat(meaning.category).isEqualTo(IntelligencesResource.MEANING_CATEGORY);
    assertThat(meaning.level).isEqualTo(value, offset(0.0000001d));

  }

}
