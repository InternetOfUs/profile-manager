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

package eu.internetofus.wenet_profile_manager.api.user_identifiers;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.queryParam;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link UserIdentifiers}.
 *
 * @see UserIdentifiers
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class UserIdentifiersIT {

  /**
   * Verify that can retrieve the user identifiers.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see UserIdentifiers#getUserIdentifiersPage(int, int, io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldGetUserIdentifiersPage(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var checkpoint = testContext.checkpoint(2);
    testRequest(client, HttpMethod.GET, UserIdentifiers.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var page = assertThatBodyIs(UserIdentifiersPage.class, res);
      assertThat(page).isNotNull();
      assertThat(page.userIds).isNotNull().hasSize((int) page.total);
      testRequest(client, HttpMethod.GET, UserIdentifiers.PATH).with(queryParam("offset", "5"), queryParam("limit", "3")).expect(res2 -> {

        assertThat(res2.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var page2 = assertThatBodyIs(UserIdentifiersPage.class, res2);
        assertThat(page2).isNotNull();
        assertThat(page2.offset).isEqualTo(5);
        assertThat(page2.total).isEqualTo(page.total);
        assertThat(page2.userIds).isNotNull().hasSize(3).isEqualTo(page.userIds.subList(5, 8));
        testContext.completeNow();

      }).send(testContext, checkpoint);

    }).send(testContext, checkpoint);

  }

}
