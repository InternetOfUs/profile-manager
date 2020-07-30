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

package eu.internetofus.wenet_profile_manager.api.intelligences;

import java.util.ArrayList;

import javax.ws.rs.core.Response.Status;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireResources;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

/**
 * Resource that provide the methods for the {@link Intelligences}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class IntelligencesResource implements Intelligences {

  /**
   * Factor used to evaluate the verbal intelligence.
   */
  public static final int LINGUISTIC = 0;

  /**
   * Factor used to evaluate the logic/mathematics intelligence.
   */
  public static final int LOGICAL_MATHEMATICAL = 1;

  /**
   * Factor used to evaluate the visual/spatial intelligence.
   */
  public static final int SPATIAL = 2;

  /**
   * Factor used to evaluate the kinestesica/corporal intelligence.
   */
  public static final int BODILY_KINESTHETIC = 3;

  /**
   * Factor used to evaluate the musical/rhythmic intelligence.
   */
  public static final int MUSICAL = 4;

  /**
   * Factor used to evaluate the intrapersonal intelligence.
   */
  public static final int INTRAPERSONAL = 5;

  /**
   * Factor used to evaluate the interpersonal intelligence.
   */
  public static final int INTERPERSONAL = 6;

  /**
   * Factor used to evaluate the naturalist/environmental intelligence.
   */
  public static final int ENVIRONMENTAL = 7;

  /**
   * The names of the intelligence factors.
   */
  public static final String[] FACTOR_NAMES = { "Linguistic", "Logical mathematical", "Spatial", "Bodily kinesthetic", "Musical", "Intrapersonal", "Interpersonal", "Environmental" };

  /**
   * The types associated to each question.
   */
  public static final int[] QUESTION_FACTORS = { LINGUISTIC, LINGUISTIC, LINGUISTIC, LINGUISTIC, LOGICAL_MATHEMATICAL, LOGICAL_MATHEMATICAL, LOGICAL_MATHEMATICAL, LOGICAL_MATHEMATICAL, SPATIAL, SPATIAL, SPATIAL, SPATIAL, BODILY_KINESTHETIC,
      BODILY_KINESTHETIC, BODILY_KINESTHETIC, BODILY_KINESTHETIC, MUSICAL, MUSICAL, MUSICAL, MUSICAL, INTERPERSONAL, INTERPERSONAL, INTERPERSONAL, INTERPERSONAL, INTRAPERSONAL, INTRAPERSONAL, INTRAPERSONAL, INTRAPERSONAL, ENVIRONMENTAL,
      ENVIRONMENTAL, ENVIRONMENTAL };

  /**
   * The name of the category to store the meaning that refers to the intelligences.
   */
  public static final String MEANING_CATEGORY = "Gardner intelligences";

  /**
   * The environment where this service is registered.
   */
  protected Vertx vertx;

  /**
   * Create a new instance to provide the services of the {@link Intelligences}.
   *
   * @param vertx where resource is defined.
   */
  public IntelligencesResource(final Vertx vertx) {

    this.vertx = vertx;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveIntelligencesQuestionnaire(final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    QuestionnaireResources.retrieveQuestionnaire(lang -> "eu/internetofus/wenet_profile_manager/api/intelligences/IntelligencesQuestionnaire." + lang + ".json", this.vertx, context, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateGardnerIntelligences(final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final QuestionnaireAnswers questionnaireAnswers = Model.fromJsonObject(body, QuestionnaireAnswers.class);
    if (questionnaireAnswers.answerValues == null || questionnaireAnswers.answerValues.size() != QUESTION_FACTORS.length) {

      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_number_of_answers",
          "To calculate the Gardner intelligences it is necessary the " + QUESTION_FACTORS.length + " responses of the intelligences questionnaire test.");
    } else {

      final double[] total = new double[FACTOR_NAMES.length];
      final int[] quantity = new int[FACTOR_NAMES.length];
      for (int index = 0; index < QUESTION_FACTORS.length; index++) {

        final double value = questionnaireAnswers.answerValues.get(index);
        if (value < 0d || value > 1d) {

          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_answer_value_at_" + index, "The answer[" + index + "] '" + value + "' is not on the range [0,1]");
          return;

        }
        final int factor = QUESTION_FACTORS[index];
        total[factor] += value;
        quantity[factor]++;
      }
      final var intelligences = new ArrayList<Meaning>();
      for (var i = 0; i < FACTOR_NAMES.length; i++) {

        if (quantity[i] > 0) {

          final var intelligence = new Meaning();
          intelligence.category = MEANING_CATEGORY;
          intelligence.name = FACTOR_NAMES[i];
          intelligence.level = total[i] / quantity[i];
          intelligences.add(intelligence);
        }

      }

      final JsonArray array = Model.toJsonArray(intelligences);
      OperationReponseHandlers.responseOk(resultHandler, array);

    }
  }

}
