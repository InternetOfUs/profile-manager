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

import eu.internetofus.common.components.Model;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describe the intelligences factors of a person.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "Intelligences", description = "Provide information about the intelligences of a person")
public class GardnerIntelligences extends Model {

	/**
	 * The verbal intelligence.
	 */
	@Schema(description = "The verbal intelligence", example = "0.1", minimum = "0.0", maximum = "1.0")
	public double verbal;

	/**
	 * The logic-mathematics intelligence.
	 */
	@Schema(description = "The logic-mathematics intelligence", example = "0.2", minimum = "0.0", maximum = "1.0")
	public double logicMathematics;

	/**
	 * The visual-spatial intelligence.
	 */
	@Schema(description = "The visual-spatial intelligence", example = "0.3", minimum = "0.0", maximum = "1.0")
	public double visualSpatial;

	/**
	 * The kinestesica-corporal intelligence.
	 */
	@Schema(description = "The kinestesica-corporal intelligence", example = "0.4", minimum = "0.0", maximum = "1.0")
	public double kinestesicaCorporal;

	/**
	 * The musical-rhythmic intelligence.
	 */
	@Schema(description = "The musical-rhythmic intelligence", example = "0.5", minimum = "0.0", maximum = "1.0")
	public double musicalRhythmic;

	/**
	 * The intrapersonal intelligence.
	 */
	@Schema(description = "The intrapersonal intelligence", example = "0.6", minimum = "0.0", maximum = "1.0")
	public double intrapersonal;

	/**
	 * The interpersonal intelligence.
	 */
	@Schema(description = "The interpersonal intelligence", example = "0.7", minimum = "0.0", maximum = "1.0")
	public double interpersonal;

	/**
	 * The naturalist-environmental intelligence.
	 */
	@Schema(description = "The naturalist-environmental intelligence", example = "0.8", minimum = "0.0", maximum = "1.0")
	public double naturalistEnvironmental;

}
