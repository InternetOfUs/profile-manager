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

package eu.internetofus.wenet_profile_manager.api.relationships;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.queryParam;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.SocialNetworkRelationshipTest;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipsPage;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.Arrays;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The integration test over the {@link Relationships}.
 *
 * @see Relationships
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class RelationshipsIT {

  /**
   * Should not retrieve a page because the order is not right.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudFailRetrieveCommunityProfilesPageWithBadOrderParameter(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Relationships.PATH).with(queryParam("order", "undefinedKey")).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);
  }

  /**
   * Should retrieve the expected communities with the specified application
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudRetrieveCommunityProfilesPageMatchingAppId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeSocialNetworkRelationshipExample(1, vertx, testContext))
        .onSuccess(relationship -> {

          testRequest(client, HttpMethod.GET, Relationships.PATH).with(queryParam("appId", relationship.appId))
              .expect(res -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final var page = assertThatBodyIs(SocialNetworkRelationshipsPage.class, res);
                assertThat(page).isNotNull();
                assertThat(page.total).isEqualTo(1);
                assertThat(page.relationships).isNotEmpty().hasSize(1).contains(relationship);

              }).send(testContext);

        });

  }

  /**
   * Should not delete model that is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.DELETE, Relationships.PATH).with(queryParam("appId", UUID.randomUUID().toString()))
        .expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext);
  }

  /**
   * Should delete a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldDeleteModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeSocialNetworkRelationshipExample(1, vertx, testContext))
        .onSuccess(stored -> {

          testRequest(client, HttpMethod.DELETE, Relationships.PATH).with(queryParam("appId", stored.appId))
              .expect(res -> {

                assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

              }).send(testContext);

        });
  }

  /**
   * Should add or update some relationships.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldAddOrUpdateSomeRelationships(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    StoreServices.storeSocialNetworkRelationshipExample(1, vertx, testContext).onSuccess(stored -> {
      testContext.assertComplete(new SocialNetworkRelationshipTest().createModelExample(33, vertx, testContext))
          .onSuccess(example -> {
            example.appId = stored.appId;
            stored.weight /= 10.0;
            testContext
                .assertComplete(WeNetProfileManager.createProxy(vertx)
                    .addOrUpdateSocialNetworkRelationships(Arrays.asList(stored, example))
                    .compose(any -> WeNetProfileManager.createProxy(vertx)
                        .retrieveSocialNetworkRelationshipsPage(example.appId, null, null, null, null, 0, 10)))
                .onSuccess(page -> testContext.verify(() -> {

                  assertThat(page).isNotNull();
                  assertThat(page.total).isEqualTo(2l);
                  assertThat(page.relationships).hasSize(2).contains(example, stored);
                  testContext.completeNow();

                }));
          });
    });
  }

  /**
   * Should add or update some relationship.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldAddOrUpdateSomeRelationship(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new SocialNetworkRelationshipTest().createModelExample(33, vertx, testContext))
        .onSuccess(example -> {
          testContext
              .assertComplete(WeNetProfileManager.createProxy(vertx).addOrUpdateSocialNetworkRelationship(example)
                  .compose(any -> WeNetProfileManager.createProxy(vertx)
                      .retrieveSocialNetworkRelationshipsPage(example.appId, null, null, null, null, 0, 10)))
              .onSuccess(page -> testContext.verify(() -> {

                assertThat(page).isNotNull();
                assertThat(page.total).isEqualTo(1l);
                assertThat(page.relationships).hasSize(1).contains(example);
                testContext.completeNow();

              }));
        });
  }

}
