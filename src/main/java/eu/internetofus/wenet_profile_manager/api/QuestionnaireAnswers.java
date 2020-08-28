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

package eu.internetofus.wenet_profile_manager.api;

import java.util.List;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

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
