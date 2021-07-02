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
 * The component used to evaluate some quality of a person.
 *
 * @see Questionnaire
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Questionnaire extends ReflectionModel implements Model {

  /**
   * The name of the questionnaire.
   */
  @Schema(description = "A name that identifies the questionnaire for a human", example = "Personality test")
  public String name;

  /**
   * The description of the questionnaire.
   */
  @Schema(description = "Explains what attribute of a person it evaluates", example = "With this questionnaire is possible to obtain the personality of a person.")
  public String description;

  /**
   * The questions that form the questionnaire.
   */
  @ArraySchema(schema = @Schema(implementation = Question.class), arraySchema = @Schema(description = "The set of questions used to evaluate the person."))
  public List<Question> questions;

}
