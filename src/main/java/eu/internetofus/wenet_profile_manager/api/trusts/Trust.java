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

package eu.internetofus.wenet_profile_manager.api.trusts;

import eu.internetofus.common.components.Model;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains information of the calculated trust.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The calculated trust between two users.")
public class Trust extends Model {

	/**
	 * The trust over the user. It has to be on the range {@code [0,1]}.
	 */
	@Schema(description = "The trust over an user respect another. It has to be on the range [0,1].", example = "0.43")
	public Number value;

	/**
	 * The time when the trust was calculated.
	 */
	@Schema(
			description = "The difference, measured in seconds, between the trust was calculated and midnight, January 1, 1970 UTC.",
			example = "1571412479710")
	public long calculatedTime;

}
