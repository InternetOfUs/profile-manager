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
