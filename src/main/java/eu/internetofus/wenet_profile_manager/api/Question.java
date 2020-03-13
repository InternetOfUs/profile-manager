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

import eu.internetofus.common.api.models.Model;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a question used to extract information of a person.
 *
 * @see Questionnaire
 * @see Answer
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Question extends Model {

	/**
	 * The text of the question.
	 */
	@Schema(description = "The text of the question", example = "The judges must be")
	public String text;

	/**
	 * The text that helps to the users to answer the question.
	 */
	@Schema(
			description = "A message to help to answer the question",
			example = "Example: If a judge judges your brother, you must follow the laws that apply to everyone equally.")
	public String help;

	/**
	 * The possible answers for the question.
	 */
	@ArraySchema(
			schema = @Schema(implementation = Answer.class),
			arraySchema = @Schema(description = "The possible answers for the question"))
	public List<Answer> answers;

}
