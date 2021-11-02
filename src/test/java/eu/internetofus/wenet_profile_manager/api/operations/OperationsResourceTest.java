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

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

/**
 * Increase the test over the {@link OperationsResource}.
 *
 * @see OperationsResource
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OperationsResourceTest {

  /**
   * Should capture exception when try an error happens when get a value.
   *
   * @see OperationsResource#getProfileAttributeValue(String,
   *      io.vertx.core.json.JsonObject)
   */
  @Test
  public void shouldGetProfileAttributeValueCaptureException() {

    final var resource = new OperationsResource(null);
    assertThat(resource.getProfileAttributeValue(null, null)).isNull();

  }

  /**
   * Should not return value if not found a value.
   *
   * @see OperationsResource#getProfileAttributeValue(String,
   *      io.vertx.core.json.JsonObject)
   */
  @Test
  public void shouldGetProfileAttributeValueNotFound() {

    final var resource = new OperationsResource(null);
    final var model = new JsonObject();
    assertThat(resource.getProfileAttributeValue("undefined", model)).isNull();
    assertThat(resource.getProfileAttributeValue("dateOfBirth.undefined", model)).isNull();
    assertThat(resource.getProfileAttributeValue("materials.undefined", model)).isNull();
    assertThat(resource.getProfileAttributeValue("competences.undefined", model)).isNull();
    assertThat(resource.getProfileAttributeValue("meanings.undefined", model)).isNull();

  }

}
