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
 * Represents a question used to extract information of a person.
 *
 * @see Questionnaire
 * @see Answer
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Question extends ReflectionModel implements Model {

  /**
   * The text of the question.
   */
  @Schema(description = "The text of the question", example = "The judges must be")
  public String text;

  /**
   * The text that helps to the users to answer the question.
   */
  @Schema(description = "A message to help to answer the question", example = "Example: If a judge judges your brother, you must follow the laws that apply to everyone equally.")
  public String help;

  /**
   * The possible answers for the question.
   */
  @ArraySchema(schema = @Schema(implementation = Answer.class), arraySchema = @Schema(description = "The possible answers for the question"))
  public List<Answer> answers;

}
