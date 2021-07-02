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

package eu.internetofus.wenet_profile_manager.api.intelligences;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.components.models.Meaning;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireResources;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import java.util.ArrayList;
import javax.ws.rs.core.Response.Status;

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
  public static final String[] FACTOR_NAMES = { "Linguistic", "Logical mathematical", "Spatial", "Bodily kinesthetic",
      "Musical", "Intrapersonal", "Interpersonal", "Environmental" };

  /**
   * The types associated to each question.
   */
  public static final int[] QUESTION_FACTORS = { LINGUISTIC, LINGUISTIC, LINGUISTIC, LINGUISTIC, LOGICAL_MATHEMATICAL,
      LOGICAL_MATHEMATICAL, LOGICAL_MATHEMATICAL, LOGICAL_MATHEMATICAL, SPATIAL, SPATIAL, SPATIAL, SPATIAL,
      BODILY_KINESTHETIC, BODILY_KINESTHETIC, BODILY_KINESTHETIC, BODILY_KINESTHETIC, MUSICAL, MUSICAL, MUSICAL,
      MUSICAL, INTERPERSONAL, INTERPERSONAL, INTERPERSONAL, INTERPERSONAL, INTRAPERSONAL, INTRAPERSONAL, INTRAPERSONAL,
      INTRAPERSONAL, ENVIRONMENTAL, ENVIRONMENTAL, ENVIRONMENTAL };

  /**
   * The name of the category to store the meaning that refers to the
   * intelligences.
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
  public void retrieveIntelligencesQuestionnaire(final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    QuestionnaireResources.retrieveQuestionnaire(
        lang -> "eu/internetofus/wenet_profile_manager/api/intelligences/IntelligencesQuestionnaire." + lang + ".json",
        this.vertx, context, resultHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateGardnerIntelligences(final JsonObject body, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var questionnaireAnswers = Model.fromJsonObject(body, QuestionnaireAnswers.class);
    if (questionnaireAnswers.answerValues == null
        || questionnaireAnswers.answerValues.size() != QUESTION_FACTORS.length) {

      ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_number_of_answers",
          "To calculate the Gardner intelligences it is necessary the " + QUESTION_FACTORS.length
              + " responses of the intelligences questionnaire test.");
    } else {

      final var total = new double[FACTOR_NAMES.length];
      final var quantity = new int[FACTOR_NAMES.length];
      for (var index = 0; index < QUESTION_FACTORS.length; index++) {

        final double value = questionnaireAnswers.answerValues.get(index);
        if (value < 0d || value > 1d) {

          ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST,
              "bad_answer_value_at_" + index, "The answer[" + index + "] '" + value + "' is not on the range [0,1]");
          return;

        }
        final var factor = QUESTION_FACTORS[index];
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

      final var array = Model.toJsonArray(intelligences);
      ServiceResponseHandlers.responseOk(resultHandler, array);

    }
  }

}
