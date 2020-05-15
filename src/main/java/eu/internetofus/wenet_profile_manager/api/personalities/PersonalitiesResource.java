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
 * Resource that provide the methods for the {@link Personalities}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PersonalitiesResource implements Personalities {

	/**
	 * Factor used to evaluate the judgment of a person.
	 */
	private static final int JUDGMENT_FACTOR = 0;

	/**
	 * Factor used to evaluate the judgment of a person.
	 */
	private static final int ATTITUDE_FACTOR = 1;

	/**
	 * Factor used to evaluate the judgment of a person.
	 */
	private static final int PERCEPTION_FACTOR = 2;

	/**
	 * Factor used to evaluate the judgment of a person.
	 */
	private static final int EXTROVERT_FACTOR = 3;

	/**
	 * The types associated to each question.
	 */
	private static final int[] QUESTION_FACTORS = { JUDGMENT_FACTOR, ATTITUDE_FACTOR, PERCEPTION_FACTOR, EXTROVERT_FACTOR,
			JUDGMENT_FACTOR, ATTITUDE_FACTOR, ATTITUDE_FACTOR, PERCEPTION_FACTOR, EXTROVERT_FACTOR, JUDGMENT_FACTOR,
			EXTROVERT_FACTOR, JUDGMENT_FACTOR, PERCEPTION_FACTOR, EXTROVERT_FACTOR, EXTROVERT_FACTOR, PERCEPTION_FACTOR,
			JUDGMENT_FACTOR, PERCEPTION_FACTOR, ATTITUDE_FACTOR, ATTITUDE_FACTOR };

	/**
	 * The number of questions that have the personality test.
	 */
	private static final int NUMBER_OF_QUESTION_IN_PERSONALITY_TEST = QUESTION_FACTORS.length;

	/**
	 * The environment where this service is registered.
	 */
	protected Vertx vertx;

	/**
	 * Create a new instance to provide the services of the {@link Personalities}.
	 *
	 * @param vertx where resource is defined.
	 */
	public PersonalitiesResource(Vertx vertx) {

		this.vertx = vertx;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrievePersonalityQuestionnaire(OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		QuestionnaireResources.retrieveQuestionnaire(
				lang -> "eu/internetofus/wenet_profile_manager/api/personalities/PersonalityQuestionnaire." + lang + ".json",
				this.vertx, context, resultHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void calculatePersonality(JsonObject body, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		final QuestionnaireAnswers questionnaireAnswers = Model.fromJsonObject(body, QuestionnaireAnswers.class);
		if (questionnaireAnswers.answerValues == null
				|| questionnaireAnswers.answerValues.size() != NUMBER_OF_QUESTION_IN_PERSONALITY_TEST) {

			OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_number_of_answers",
					"To calculate the personality it is necessary the " + NUMBER_OF_QUESTION_IN_PERSONALITY_TEST
							+ " responses of the personality questionnaire test.");
		} else {

			final Personality personality = new Personality();
			final double[] total = { 0d, 0d, 0d, 0d };
			for (int index = 0; index < NUMBER_OF_QUESTION_IN_PERSONALITY_TEST; index++) {

				final double value = questionnaireAnswers.answerValues.get(index);
				if (value < -1d || value > 1d) {

					OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST,
							"bad_answer_value_at_" + index, "The answer[" + index + "] '" + value + "' is not on the range [-1,1]");
					return;

				}
				switch (QUESTION_FACTORS[index]) {
				case JUDGMENT_FACTOR:
					personality.judgment += value;
					total[0]++;
					break;
				case ATTITUDE_FACTOR:
					personality.attitude += value;
					total[1]++;
					break;
				case PERCEPTION_FACTOR:
					personality.perception += value;
					total[2]++;
					break;
				default:
					// EXTROVERT_FACTOR:
					personality.extrovert += value;
					total[3]++;
				}
			}
			personality.judgment = personality.judgment / total[0];
			personality.attitude = personality.attitude / total[1];
			personality.perception = personality.perception / total[2];
			personality.extrovert = personality.extrovert / total[3];
			personality.MBTI = "";
			if (personality.extrovert > 0) {

				personality.MBTI += 'E';

			} else {

				personality.MBTI += 'I';
			}
			if (personality.perception > 0) {

				personality.MBTI += 'N';

			} else {

				personality.MBTI += 'S';
			}
			if (personality.judgment > 0) {

				personality.MBTI += 'T';

			} else {

				personality.MBTI += 'F';
			}
			if (personality.attitude > 0) {

				personality.MBTI += 'J';

			} else {

				personality.MBTI += 'P';
			}

			OperationReponseHandlers.responseOk(resultHandler, personality);

		}

	}

}
