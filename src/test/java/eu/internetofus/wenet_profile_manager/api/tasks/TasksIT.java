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

package eu.internetofus.wenet_profile_manager.api.tasks;

import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator;
import eu.internetofus.wenet_profile_manager.api.trusts.UserPerformanceRatingEventTest;
import eu.internetofus.wenet_profile_manager.persistence.TrustsRepository;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The integration test over the {@link Tasks}.
 *
 * @see Tasks
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class TasksIT {

  /**
   * Verify that remove information about a task.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Tasks#taskDeleted(String, io.vertx.ext.web.api.service.ServiceRequest,
   *      Handler)
   */
  @Test
  public void shouldNotifiedDeletedTask(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var event = new UserPerformanceRatingEventTest().createModelExample(1);
    TrustsRepository.createProxy(vertx).storeTrustEvent(event).onSuccess(stored -> {

      testRequest(client, HttpMethod.DELETE, Tasks.PATH + "/" + event.taskId).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
        TrustsRepository.createProxy(vertx)
            .calculateTrustBy(TrustAggregator.MINIMUM, new JsonObject().put("taskId", event.taskId))
            .onComplete(result -> testContext.verify(() -> {

              assertThat(result.failed()).isTrue();
              testContext.completeNow();

            }));

      })).send(testContext, testContext.checkpoint(2));

    });
  }

}
