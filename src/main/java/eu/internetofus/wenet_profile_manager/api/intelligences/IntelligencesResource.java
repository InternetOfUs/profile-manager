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

import javax.ws.rs.core.Response.Status;

import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.common.components.Model;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireResources;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
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
	private static final int VERBAL_FACTOR = 0;

	/**
	 * Factor used to evaluate the logic/mathematics intelligence.
	 */
	private static final int LOGIC_MATHEMATICS_FACTOR = 1;

	/**
	 * Factor used to evaluate the visual/spatial intelligence.
	 */
	private static final int VISUAL_SPATIAL_FACTOR = 2;

	/**
	 * Factor used to evaluate the kinestesica/corporal intelligence.
	 */
	private static final int KINESTESICA_CORPORAL_FACTOR = 3;

	/**
	 * Factor used to evaluate the musical/rhythmic intelligence.
	 */
	private static final int MUSICAL_RHYTHMIC_FACTOR = 4;

	/**
	 * Factor used to evaluate the intrapersonal intelligence.
	 */
	private static final int INTRAPERSONAL_FACTOR = 5;

	/**
	 * Factor used to evaluate the interpersonal intelligence.
	 */
	private static final int INTERPERSONAL_FACTOR = 6;

	/**
	 * Factor used to evaluate the naturalist/environmental intelligence.
	 */
	private static final int NATURALIST_ENVIRONMENTAL_FACTOR = 7;

	/**
	 * The types associated to each question.
	 */
	private static final int[] QUESTION_FACTORS = { VERBAL_FACTOR, VISUAL_SPATIAL_FACTOR, INTRAPERSONAL_FACTOR,
			MUSICAL_RHYTHMIC_FACTOR, MUSICAL_RHYTHMIC_FACTOR, LOGIC_MATHEMATICS_FACTOR, INTRAPERSONAL_FACTOR,
			LOGIC_MATHEMATICS_FACTOR, KINESTESICA_CORPORAL_FACTOR, VERBAL_FACTOR, VISUAL_SPATIAL_FACTOR, INTERPERSONAL_FACTOR,
			NATURALIST_ENVIRONMENTAL_FACTOR, MUSICAL_RHYTHMIC_FACTOR, VISUAL_SPATIAL_FACTOR, LOGIC_MATHEMATICS_FACTOR,
			KINESTESICA_CORPORAL_FACTOR, VERBAL_FACTOR, INTERPERSONAL_FACTOR, KINESTESICA_CORPORAL_FACTOR,
			LOGIC_MATHEMATICS_FACTOR, KINESTESICA_CORPORAL_FACTOR, VERBAL_FACTOR, VISUAL_SPATIAL_FACTOR,
			NATURALIST_ENVIRONMENTAL_FACTOR, MUSICAL_RHYTHMIC_FACTOR, LOGIC_MATHEMATICS_FACTOR, INTRAPERSONAL_FACTOR,
			VISUAL_SPATIAL_FACTOR, MUSICAL_RHYTHMIC_FACTOR, NATURALIST_ENVIRONMENTAL_FACTOR, KINESTESICA_CORPORAL_FACTOR,
			VERBAL_FACTOR, INTRAPERSONAL_FACTOR, INTERPERSONAL_FACTOR, NATURALIST_ENVIRONMENTAL_FACTOR, INTRAPERSONAL_FACTOR,
			INTERPERSONAL_FACTOR, INTERPERSONAL_FACTOR, NATURALIST_ENVIRONMENTAL_FACTOR };

	/**
	 * The number of questions that have the intelligences test.
	 */
	private static final int NUMBER_OF_QUESTION_IN_INTELLIGENCES_TEST = QUESTION_FACTORS.length;

	/**
	 * The environment where this service is registered.
	 */
	protected Vertx vertx;

	/**
	 * Create a new instance to provide the services of the {@link Intelligences}.
	 *
	 * @param vertx where resource is defined.
	 */
	public IntelligencesResource(Vertx vertx) {

		this.vertx = vertx;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveIntelligencesQuestionnaire(OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		QuestionnaireResources.retrieveQuestionnaire(
				lang -> "eu/internetofus/wenet_profile_manager/api/intelligences/IntelligencesQuestionnaire." + lang + ".json",
				this.vertx, context, resultHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void calculateGardnerIntelligences(JsonObject body, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		final QuestionnaireAnswers questionnaireAnswers = Model.fromJsonObject(body, QuestionnaireAnswers.class);
		if (questionnaireAnswers.answerValues == null
				|| questionnaireAnswers.answerValues.size() != NUMBER_OF_QUESTION_IN_INTELLIGENCES_TEST) {

			OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_number_of_answers",
					"To calculate the personality it is necessary the " + NUMBER_OF_QUESTION_IN_INTELLIGENCES_TEST
							+ " responses of the personality questionnaire test.");
		} else {

			final GardnerIntelligences intelligences = new GardnerIntelligences();
			final double[] total = { 0, 0, 0, 0, 0, 0, 0, 0 };
			for (int index = 0; index < NUMBER_OF_QUESTION_IN_INTELLIGENCES_TEST; index++) {

				final double value = questionnaireAnswers.answerValues.get(index);
				if (value < 0d || value > 1d) {

					OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST,
							"bad_answer_value_at_" + index, "The answer[" + index + "] '" + value + "' is not on the range [0,1]");
					return;

				}

				switch (QUESTION_FACTORS[index]) {
				case VERBAL_FACTOR:
					intelligences.verbal += value;
					total[0]++;
					break;
				case LOGIC_MATHEMATICS_FACTOR:
					intelligences.logicMathematics += value;
					total[1]++;
					break;
				case VISUAL_SPATIAL_FACTOR:
					intelligences.visualSpatial += value;
					total[2]++;
					break;
				case KINESTESICA_CORPORAL_FACTOR:
					intelligences.kinestesicaCorporal += value;
					total[3]++;
					break;
				case MUSICAL_RHYTHMIC_FACTOR:
					intelligences.musicalRhythmic += value;
					total[4]++;
					break;
				case INTRAPERSONAL_FACTOR:
					intelligences.intrapersonal += value;
					total[5]++;
					break;
				case INTERPERSONAL_FACTOR:
					intelligences.interpersonal += value;
					total[6]++;
					break;
				default:
					// NATURALIST_ENVIRONMENTAL_FACTOR:
					intelligences.naturalistEnvironmental += value;
					total[7]++;
				}
			}
			intelligences.verbal = intelligences.verbal / total[0];
			intelligences.logicMathematics = intelligences.logicMathematics / total[1];
			intelligences.visualSpatial = intelligences.visualSpatial / total[2];
			intelligences.kinestesicaCorporal = intelligences.kinestesicaCorporal / total[3];
			intelligences.musicalRhythmic = intelligences.musicalRhythmic / total[4];
			intelligences.intrapersonal = intelligences.intrapersonal / total[5];
			intelligences.interpersonal = intelligences.interpersonal / total[6];
			intelligences.naturalistEnvironmental = intelligences.naturalistEnvironmental / total[7];

			OperationReponseHandlers.responseOk(resultHandler, intelligences);

		}
	}

}
