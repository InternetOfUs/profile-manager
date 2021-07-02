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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains the possible answer to a {@link Question}.
 *
 * @see Question
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Answer extends ReflectionModel implements Model {

  /**
   * The text of the answer.
   */
  @Schema(description = "The text of the answer", example = "Compassionate")
  public String text;

  /**
   * The value associated to the answer.
   */
  @Schema(description = "The value associated with the answer", example = "-1")
  public double value;
}
