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

import eu.internetofus.common.components.Model;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The personality factors of a person.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "Personality", description = "Provide information about the personality of a person")
public class Personality extends Model {

	/**
	 * The perception of a person.
	 */
	@Schema(description = "The perception grade of a person", example = "0.1", minimum = "0.0", maximum = "1.0")
	public double perception;

	/**
	 * The judgment of a person.
	 */
	@Schema(description = "The judgment grade of a person", example = "0.2", minimum = "0.0", maximum = "1.0")
	public double judgment;

	/**
	 * The extrovert of a person.
	 */
	@Schema(description = "The extrovert grade of a person", example = "0.3", minimum = "0.0", maximum = "1.0")
	public double extrovert;

	/**
	 * The attitude of a person.
	 */
	@Schema(description = "The attitude grade of a person", example = "0.4", minimum = "0.0", maximum = "1.0")
	public double attitude;

	/**
	 * The Myers–Briggs Type Indicator (MBTI) of the person personality.
	 */
	@Schema(description = " Myers–Briggs Type Indicator of the person personality", example = "INFJ")
	public String MBTI;
}
