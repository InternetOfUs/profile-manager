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

package eu.internetofus.wenet_profile_manager.api.profiles;

import java.util.List;

import eu.internetofus.common.api.models.Model;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains the found profiles.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "HistoricWeNetUserProfilesPage", description = "Contains a set of profiles found")
public class HistoricWeNetUserProfilesPage extends Model {

	/**
	 * The index of the first profile returned.
	 */
	@Schema(description = "The index of the first profile returned.", example = "0")
	public int offset;

	/**
	 * The number total of profiles that satisfies the search.
	 */
	@Schema(description = "The number total of profiles that satisfies the search.", example = "100")
	public long total;

	/**
	 * The found profiles.
	 */
	@ArraySchema(
			schema = @Schema(implementation = HistoricWeNetUserProfile.class),
			arraySchema = @Schema(description = "The set of profiles found"))
	public List<HistoricWeNetUserProfile> profiles;

}
