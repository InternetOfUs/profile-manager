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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A previous profile of the user in the time.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "HistoricWeNetUserProfile", description = "The information of the user profile in a period of time.")
public class HistoricWeNetUserProfile extends ReflectionModel implements Model {

  /**
   * The difference, measured in milliseconds, between the time when the profile is modified to have the current state and
   * midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The difference, measured in seconds, between the time when the profile is modified to have the current state and midnight, January 1, 1970 UTC.", example = "1457166440")
  public long from;

  /**
   * The difference, measured in milliseconds, between the time when the profile is not more valid and midnight, January
   * 1, 1970 UTC.
   */
  @Schema(description = "The difference, measured in seconds, between the time when the profile is not more valid and midnight, January 1, 1970 UTC.", example = "1571664406")
  public long to;

  /**
   * The profile on the period of time
   */
  @Schema(description = "The profile on the period of time.", ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c28427ce0c05596ef9001ffa8a08f8eb125611f/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile")
  public WeNetUserProfile profile;

}
