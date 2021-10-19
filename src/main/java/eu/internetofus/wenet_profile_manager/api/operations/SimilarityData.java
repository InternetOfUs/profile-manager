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
package eu.internetofus.wenet_profile_manager.api.operations;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The data necessary to calculate the similarity between a text and the
 * attributes of a profile.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The information necessary to calculate the diversity between some users.")
public class SimilarityData extends ReflectionModel implements Model {

  /**
   * The identifier of the user to calculate the similarity of its attributes.
   */
  @Schema(description = "The identifier of the user to calculate the similarity of its attributes.", example = "\"1\"", nullable = false)
  public String userIds;

  /**
   * The text to compare the profile attributes.
   */
  @Schema(description = "The text to compare the profile attributes.", example = "\"Where to buy the best pizza?\"", nullable = false)
  public String source;

}
