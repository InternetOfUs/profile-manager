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

package eu.internetofus.wenet_profile_manager.api.personalities;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.requestHeader;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

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
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.Questionnaire;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Personalities}.
 *
 * @see Personalities
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class PersonalitiesIT {

  /**
   * Verify that retrieve the personality questionnaire.
   *
   * @param lang        language to obtain the questionnaire.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#retrievePersonalityQuestionnaire(io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @ParameterizedTest(name = "Should return personality questionnaire for language {0}")
  @EmptySource
  @ValueSource(strings = { "*", "en", "es", "ca", "es-US,es;q=0.5", "ca,es,en", "en-US,en,es", "it" })
  public void shouldRetrievePersonalityQuestionnaire(final String lang, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Personalities.PATH).with(requestHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,es")).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final Questionnaire questionnaire = assertThatBodyIs(Questionnaire.class, res);
      assertThat(questionnaire.questions).hasSize(20);

    }).send(testContext);

  }

  /**
   * Verify that not calculate the personality because no answers are passed.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseNoAnswers(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new QuestionnaireAnswers(), testContext);

  }

  /**
   * Verify that not calculate the personality because no provide an empty answers list.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseNotEmptyAnswers(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the personality because no provide enough answers.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseNotEnoughAnswers(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (int i = 0; i < 19; i++) {

      answers.answerValues.add(0d);
      testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isEqualTo("bad_number_of_answers");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(answers, testContext);

    }

  }

  /**
   * Verify that not calculate the personality because provide too many answers.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseTooManyAnswers(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (int i = 0; i < 21; i++) {

      answers.answerValues.add(0d);

    }

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the personality because an answer value is too low.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseAnswerValueIsTooLow(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (int i = 0; i < 20; i++) {

      answers.answerValues.add(0d);

    }

    answers.answerValues.set(7, -1.01d);

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_answer_value_at_7");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the personality because an answer value is too high.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseAnswerValueIsTooHigh(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (int i = 0; i < 20; i++) {

      answers.answerValues.add(0d);

    }

    answers.answerValues.set(17, 1.01d);

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_answer_value_at_17");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that calculate a ENTJ personality.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldCalculateENTJPersonality(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (int i = 0; i < 20; i++) {

      answers.answerValues.add(1d);

    }

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final Personality personality = assertThatBodyIs(Personality.class, res);
      assertThat(personality).isNotNull();
      assertThat(personality.extrovert).isEqualTo(1d);
      assertThat(personality.perception).isEqualTo(1d);
      assertThat(personality.judgment).isEqualTo(1d);
      assertThat(personality.attitude).isEqualTo(1d);
      assertThat(personality.MBTI).isEqualTo("ENTJ");

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that calculate a ISFP personality.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldCalculateISFPPersonality(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (int i = 0; i < 20; i++) {

      answers.answerValues.add(-1d);

    }

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final Personality personality = assertThatBodyIs(Personality.class, res);
      assertThat(personality).isNotNull();
      assertThat(personality.extrovert).isEqualTo(-1d);
      assertThat(personality.perception).isEqualTo(-1d);
      assertThat(personality.judgment).isEqualTo(-1d);
      assertThat(personality.attitude).isEqualTo(-1d);
      assertThat(personality.MBTI).isEqualTo("ISFP");

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that calculate personality.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldCalculatePersonality(final WebClient client, final VertxTestContext testContext) {

    final QuestionnaireAnswers answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    Collections.addAll(answers.answerValues, 1d, -1d, 1d, 1d, 0d, 0d, 1d, 1d, -1d, -1d, -1d, 0d, -1d, -1d, 0d, 1d, 1d, -1d, 1d, 1d);

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final Personality personality = assertThatBodyIs(Personality.class, res);
      assertThat(personality).isNotNull();
      assertThat(personality.extrovert).isEqualTo(-0.4d);
      assertThat(personality.perception).isEqualTo(0.2d);
      assertThat(personality.judgment).isEqualTo(0.2d);
      assertThat(personality.attitude).isEqualTo(0.4d);
      assertThat(personality.MBTI).isEqualTo("INTJ");

    }).sendJson(answers, testContext);

  }

}
