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

package eu.internetofus.wenet_profile_manager.api.personalities;

import java.util.ArrayList;

import javax.ws.rs.core.Response.Status;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireResources;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

/**
 * Resource that provide the methods for the {@link Personalities}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PersonalitiesResource implements Personalities {

  /**
   * Factor used to evaluate the judgment of a person.
   */
  public static final int EXTROVERT_FACTOR = 0;

  /**
   * Factor used to evaluate the judgment of a person.
   */
  public static final int PERCEPTION_FACTOR = 1;

  /**
   * Factor used to evaluate the judgment of a person.
   */
  public static final int JUDGMENT_FACTOR = 2;

  /**
   * Factor used to evaluate the judgment of a person.
   */
  public static final int ATTITUDE_FACTOR = 3;

  /**
   * The names of the personality factors.
   */
  public static final String[] FACTOR_NAMES = { "Extrovert", "Perception", "Judgment", "Attitude" };

  /**
   * The types associated to each question.
   */
  public static final int[] QUESTION_FACTORS = { JUDGMENT_FACTOR, ATTITUDE_FACTOR, PERCEPTION_FACTOR, EXTROVERT_FACTOR, JUDGMENT_FACTOR, ATTITUDE_FACTOR, ATTITUDE_FACTOR, PERCEPTION_FACTOR, EXTROVERT_FACTOR, JUDGMENT_FACTOR,
      EXTROVERT_FACTOR, JUDGMENT_FACTOR, PERCEPTION_FACTOR, EXTROVERT_FACTOR, EXTROVERT_FACTOR, PERCEPTION_FACTOR, JUDGMENT_FACTOR, PERCEPTION_FACTOR, ATTITUDE_FACTOR, ATTITUDE_FACTOR };

  /**
   * The name of the category to store the meaning that refers to the personality.
   */
  public static final String MEANING_CATEGORY = "Post Jungian concepts";

  /**
   * The environment where this service is registered.
   */
  protected Vertx vertx;

  /**
   * Create a new instance to provide the services of the {@link Personalities}.
   *
   * @param vertx where resource is defined.
   */
  public PersonalitiesResource(final Vertx vertx) {

    this.vertx = vertx;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrievePersonalityQuestionnaire(final ServiceRequest context, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    QuestionnaireResources.retrieveQuestionnaire(lang -> "eu/internetofus/wenet_profile_manager/api/personalities/PersonalityQuestionnaire." + lang + ".json", this.vertx, context, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculatePersonality(final JsonObject body, final ServiceRequest context, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var questionnaireAnswers = Model.fromJsonObject(body, QuestionnaireAnswers.class);
    if (questionnaireAnswers.answerValues == null || questionnaireAnswers.answerValues.size() != QUESTION_FACTORS.length) {

      ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_number_of_answers",
          "To calculate the personality it is necessary the " + QUESTION_FACTORS.length + " responses of the personality questionnaire test.");
    } else {

      final var total = new double[FACTOR_NAMES.length];
      final var quantity = new int[FACTOR_NAMES.length];
      for (var index = 0; index < QUESTION_FACTORS.length; index++) {

        final double value = questionnaireAnswers.answerValues.get(index);
        if (value < -1d || value > 1d) {

          ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_answer_value_at_" + index, "The answer[" + index + "] '" + value + "' is not on the range [-1,1]");
          return;

        }
        final var factor = QUESTION_FACTORS[index];
        total[factor] += value;
        quantity[factor]++;
      }
      final var personality = new ArrayList<Meaning>();
      final var mbti = new StringBuilder();
      for (var i = 0; i < FACTOR_NAMES.length; i++) {

        if (quantity[i] > 0) {

          final var meaning = new Meaning();
          meaning.category = MEANING_CATEGORY;
          meaning.name = FACTOR_NAMES[i];
          meaning.level = total[i] / quantity[i];
          personality.add(meaning);
          if (meaning.level > 0) {

            mbti.append('1');

          } else {

            mbti.append('0');
          }

        }

      }

      if (mbti.length() == 4) {

        final var meaning = new Meaning();
        meaning.category = "Post Jungian concepts";
        meaning.name = "MBTI";
        meaning.level = (double) Integer.parseInt(mbti.toString(), 2);
        personality.add(meaning);

      }
      final var array = Model.toJsonArray(personality);
      ServiceResponseHandlers.responseOk(resultHandler, array);

    }

  }

}
