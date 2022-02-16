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

import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link RelationshipsRepository}.
 *
 * @see RelationshipsRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class RelationshipsRepositoryIT {

  /**
   * Verify that can not store a relationship that can not be an object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see RelationshipsRepository#storeOrUpdateSocialNetworkRelationship(SocialNetworkRelationship)
   */
  @Test
  public void shouldNotStoreARelationshipThatCanNotBeAnObject(final Vertx vertx, final VertxTestContext testContext) {

    final SocialNetworkRelationship relationship = new SocialNetworkRelationship() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObjectWithEmptyValues() {

        return null;
      }
    };
    testContext
        .assertFailure(RelationshipsRepository.createProxy(vertx).storeOrUpdateSocialNetworkRelationship(relationship))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can store a relationship.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see RelationshipsRepository#storeOrUpdateSocialNetworkRelationship(SocialNetworkRelationship)
   */
  @Test
  public void shouldStoreOrUpdateSocialNetworkRelationship(final Vertx vertx, final VertxTestContext testContext) {

    final var relationship = new SocialNetworkRelationshipTest().createModelExample(1);
    relationship.appId = UUID.randomUUID().toString();
    testContext
        .assertComplete(RelationshipsRepository.createProxy(vertx).storeOrUpdateSocialNetworkRelationship(relationship))
        .onSuccess(stored -> testContext.verify(() -> {

          assertThat(stored).isNotNull();
          testContext
              .assertComplete(
                  RelationshipsRepository.createProxy(vertx).storeOrUpdateSocialNetworkRelationship(relationship))
              .onSuccess(stored2 -> testContext.verify(() -> {

                assertThat(stored2).isNull();
                testContext.completeNow();
              }));
        }));

  }

  /**
   * Verify that can not delete a relationship if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see RelationshipsRepository#deleteSocialNetworkRelationship
   */
  @Test
  public void shouldNotDeleteUndefinedRelationship(final Vertx vertx, final VertxTestContext testContext) {

    final var query = new JsonObject().put("appId", UUID.randomUUID().toString());
    RelationshipsRepository.createProxy(vertx).deleteSocialNetworkRelationship(query, testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can delete some relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see RelationshipsRepository#deleteSocialNetworkRelationship
   */
  @Test
  public void shouldDeleteRelationships(final Vertx vertx, final VertxTestContext testContext) {

    final var relationship = new SocialNetworkRelationshipTest().createModelExample(1);
    relationship.appId = UUID.randomUUID().toString();
    final var repository = RelationshipsRepository.createProxy(vertx);
    testContext.assertComplete(repository.storeOrUpdateSocialNetworkRelationship(relationship).compose(any -> {

      relationship.sourceId = UUID.randomUUID().toString();
      return repository.storeOrUpdateSocialNetworkRelationship(relationship);

    }).compose(any -> {

      relationship.targetId = UUID.randomUUID().toString();
      return repository.storeOrUpdateSocialNetworkRelationship(relationship);

    }).compose(stored -> {

      final var query = new JsonObject().put("appId", relationship.appId);
      return repository.deleteSocialNetworkRelationship(query)
          .compose(any -> repository.retrieveSocialNetworkRelationshipsPage(query, new JsonObject(), 0, 0));

    })).onSuccess(page -> {

      testContext.verify(() -> {

        assertThat(page).isNotNull();
        assertThat(page.total).isEqualTo(0);

      });
      testContext.completeNow();

    });

  }

}
