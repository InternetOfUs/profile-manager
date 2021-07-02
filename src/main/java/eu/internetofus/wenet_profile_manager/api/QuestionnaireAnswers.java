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

package eu.internetofus.wenet_profile_manager.api;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * The answers to a {@link Questionnaire}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "QuestionnaireAnswers", description = "Contains the selected answers of a questionnaire")
public class QuestionnaireAnswers extends ReflectionModel implements Model {

  /**
   * The values of the answers for each question on the questionnaire.
   */
  @ArraySchema(arraySchema = @Schema(type = "number", description = "The selected values for the questions on the questionnaire. The answer values are on the same order of the question that refers to the questionnaire.", example = "[1,-1,0,1,1,-1,1,0,0,0,1,-1,-1,-1,1]"))
  public List<Double> answerValues;
}
