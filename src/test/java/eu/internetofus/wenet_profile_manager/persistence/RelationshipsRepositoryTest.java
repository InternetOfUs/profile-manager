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

package eu.internetofus.wenet_profile_manager.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.internetofus.common.model.ValidationErrorException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test {@link RelationshipsRepository}
 *
 * @see RelationshipsRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RelationshipsRepositoryTest {

  /**
   * Verify that can not create relationships page sort.
   *
   * @see RelationshipsRepository#createSocialNetworkRelationshipsPageSort(List)
   */
  @Test
  public void shouldFailcreateSocialNetworkRelationshipsPageSort() {

    final List<String> order = new ArrayList<>();
    order.add("-undefinedKey");
    assertThatThrownBy(() -> {
      RelationshipsRepository.createSocialNetworkRelationshipsPageSort(order);
    }).isInstanceOf(ValidationErrorException.class);

  }

  /**
   * Verify that can not create relationships page sort.
   *
   * @see RelationshipsRepository#createSocialNetworkRelationshipsPageSort(List)
   */
  @Test
  public void shouldcreateSocialNetworkRelationshipsPageSort() {

    final List<String> order = new ArrayList<>();
    order.add("+appId");
    order.add("-sourceId");
    order.add("targetId");
    order.add("+type");
    order.add("-weight");
    final var sort = RelationshipsRepository.createSocialNetworkRelationshipsPageSort(order);
    assertThat(sort).isNotNull();
    assertThat(sort.getInteger("appId")).isNotNull().isEqualTo(1);
    assertThat(sort.getInteger("sourceId")).isNotNull().isEqualTo(-1);
    assertThat(sort.getInteger("targetId")).isNotNull().isEqualTo(1);
    assertThat(sort.getInteger("type")).isNotNull().isEqualTo(1);
    assertThat(sort.getInteger("weight")).isNotNull().isEqualTo(-1);

  }

}
