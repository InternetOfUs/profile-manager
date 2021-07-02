/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.wenet_profile_manager.api.personalities;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.requestHeader;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.components.models.Meaning;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.Questionnaire;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Collections;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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
   * @see Personalities#retrievePersonalityQuestionnaire(io.vertx.ext.web.api.service.ServiceRequest,
   *      Handler)
   */
  @ParameterizedTest(name = "Should return personality questionnaire for language {0}")
  @EmptySource
  @ValueSource(strings = { "*", "en", "es", "ca", "es-US,es;q=0.5", "ca,es,en", "en-US,en,es", "it" })
  public void shouldRetrievePersonalityQuestionnaire(final String lang, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Personalities.PATH).with(requestHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,es"))
        .expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final var questionnaire = assertThatBodyIs(Questionnaire.class, res);
          assertThat(questionnaire.questions).hasSize(20);

        }).send(testContext);

  }

  /**
   * Verify that not calculate the personality because no answers are passed.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseNoAnswers(final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_number_of_answers");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new QuestionnaireAnswers(), testContext);

  }

  /**
   * Verify that not calculate the personality because no provide an empty answers
   * list.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseNotEmptyAnswers(final WebClient client,
      final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
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
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseNotEnoughAnswers(final WebClient client,
      final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < 19; i++) {

      answers.answerValues.add(0d);
      testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
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
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseTooManyAnswers(final WebClient client,
      final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < 21; i++) {

      answers.answerValues.add(0d);

    }

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
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
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseAnswerValueIsTooLow(final WebClient client,
      final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < 20; i++) {

      answers.answerValues.add(0d);

    }

    answers.answerValues.set(7, -1.01d);

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_answer_value_at_7");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that not calculate the personality because an answer value is too
   * high.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotCalculatePersonalityBecauseAnswerValueIsTooHigh(final WebClient client,
      final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < 20; i++) {

      answers.answerValues.add(0d);

    }

    answers.answerValues.set(17, 1.01d);

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
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
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldCalculateENTJPersonality(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < 20; i++) {

      answers.answerValues.add(1d);

    }

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var personality = Model.fromJsonArray(res.bodyAsJsonArray(), Meaning.class);
      assertThat(personality).isNotNull();
      for (var factor = 0; factor < PersonalitiesResource.FACTOR_NAMES.length; factor++) {

        assertPersonality(personality.get(factor), factor, 1d);

      }

      final var mbti = personality.get(PersonalitiesResource.FACTOR_NAMES.length);
      assertThat(mbti).isNotNull();
      assertThat(mbti.name).isEqualTo("MBTI");
      assertThat(mbti.category).isEqualTo(PersonalitiesResource.MEANING_CATEGORY);
      assertThat(mbti.level).isEqualTo(15, offset(0.0000001d));

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that calculate a ISFP personality.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldCalculateISFPPersonality(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    for (var i = 0; i < 20; i++) {

      answers.answerValues.add(-1d);

    }

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var personality = Model.fromJsonArray(res.bodyAsJsonArray(), Meaning.class);
      assertThat(personality).isNotNull();
      for (var factor = 0; factor < PersonalitiesResource.FACTOR_NAMES.length; factor++) {

        assertPersonality(personality.get(factor), factor, -1d);

      }

      final var mbti = personality.get(PersonalitiesResource.FACTOR_NAMES.length);
      assertThat(mbti).isNotNull();
      assertThat(mbti.name).isEqualTo("MBTI");
      assertThat(mbti.category).isEqualTo(PersonalitiesResource.MEANING_CATEGORY);
      assertThat(mbti.level).isEqualTo(0, offset(0.0000001d));

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that calculate personality.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Personalities#calculatePersonality(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldCalculatePersonality(final WebClient client, final VertxTestContext testContext) {

    final var answers = new QuestionnaireAnswers();
    answers.answerValues = new ArrayList<Double>();
    Collections.addAll(answers.answerValues, 1d, -1d, 1d, 1d, 0d, 0d, 1d, 1d, -1d, -1d, -1d, 0d, -1d, -1d, 0d, 1d, 1d,
        -1d, 1d, 1d);

    testRequest(client, HttpMethod.POST, Personalities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var personality = Model.fromJsonArray(res.bodyAsJsonArray(), Meaning.class);
      assertThat(personality).isNotNull();
      final double[] values = { -0.4d, 0.2d, 0.2d, 0.4d };
      for (var factor = 0; factor < PersonalitiesResource.FACTOR_NAMES.length; factor++) {

        assertPersonality(personality.get(factor), factor, values[factor]);

      }

      final var mbti = personality.get(PersonalitiesResource.FACTOR_NAMES.length);
      assertThat(mbti).isNotNull();
      assertThat(mbti.name).isEqualTo("MBTI");
      assertThat(mbti.category).isEqualTo(PersonalitiesResource.MEANING_CATEGORY);
      assertThat(mbti.level).isEqualTo(7, offset(0.0000001d));

    }).sendJson(answers, testContext);

  }

  /**
   * Verify that a meaning contains the specific personality factor with the
   * specified value.
   *
   * @param meaning to check that contains the personality.
   * @param factor  index of the factor to check.
   * @param value   for the personality.
   */
  public static void assertPersonality(final Meaning meaning, final int factor, final double value) {

    assertThat(meaning).isNotNull();
    assertThat(meaning.name).isEqualTo(PersonalitiesResource.FACTOR_NAMES[factor]);
    assertThat(meaning.category).isEqualTo(PersonalitiesResource.MEANING_CATEGORY);
    assertThat(meaning.level).isEqualTo(value, offset(0.0000001d));

  }

}
