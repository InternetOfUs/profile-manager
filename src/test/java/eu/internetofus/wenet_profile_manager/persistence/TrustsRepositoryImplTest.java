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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TrustsRepositoryImpl}.
 *
 * @see TrustsRepositoryImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class TrustsRepositoryImplTest {

  /**
   * Should not calculate the median when can not found the median value.
   *
   * @param pool        mocked connection to MongoDB.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepositoryImpl#calculateMedianTrust(io.vertx.core.json.JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotCalculateMedianTrustWhenFailFindMadian(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final var repository = new TrustsRepositoryImpl(new JsonObject(), pool, "version");
    final var expectedCause = new Throwable("Expected cause");
    repository.calculateMedianTrust(new JsonObject(), testContext.failing(cause -> testContext.verify(() -> {

      assertThat(cause).isEqualTo(expectedCause);
      testContext.completeNow();
    })));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Long>>> countHandler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, timeout(30000).times(1)).count(any(), any(), countHandler.capture());
    countHandler.getValue().handle(Future.succeededFuture(109l));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<List<JsonObject>>>> saveHandler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, timeout(30000).times(1)).findWithOptions(any(), any(), any(), saveHandler.capture());
    saveHandler.getValue().handle(Future.failedFuture(expectedCause));

  }

  /**
   * Should not store an event when the MongoDB fails.
   *
   * @param pool        mocked connection to MongoDB.
   * @param testContext context that executes the test.
   *
   * @see TrustsRepositoryImpl#calculateMedianTrust(io.vertx.core.json.JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreTrustEventBecausePoolFails(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final var repository = new TrustsRepositoryImpl(new JsonObject(), pool, "version");
    final var expectedCause = new Throwable("Expected cause");
    repository.storeTrustEvent(new JsonObject(), testContext.failing(cause -> testContext.verify(() -> {

      assertThat(cause).isEqualTo(expectedCause);
      testContext.completeNow();
    })));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<String>>> countHandler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, timeout(30000).times(1)).save(any(), any(), countHandler.capture());
    countHandler.getValue().handle(Future.failedFuture(expectedCause));

  }

}
