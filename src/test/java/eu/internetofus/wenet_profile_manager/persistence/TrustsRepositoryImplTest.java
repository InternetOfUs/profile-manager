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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

package eu.internetofus.wenet_profile_manager.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

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
@ExtendWith(VertxExtension.class)
public class TrustsRepositoryImplTest {

  /**
   * Should not store trust event when DB failed.
   *
   * @param testContext context that executes the test.
   *
   * @see TrustsRepositoryImpl#storeTrustEvent(io.vertx.core.json.JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreTrustEventWehnDBFailed(final VertxTestContext testContext) {

    final var pool = mock(MongoClient.class);
    final var repository = new TrustsRepositoryImpl(new JsonObject(), pool,"version");
    final var expectedCause = new Throwable("Expected cause");
    repository.storeTrustEvent(new JsonObject(), testContext.failing(cause -> testContext.verify(() -> {

      assertThat(cause).isEqualTo(expectedCause);
      testContext.completeNow();
    })));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<String>>> saveHandler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, timeout(30000).times(1)).save(any(), any(), saveHandler.capture());
    saveHandler.getValue().handle(Future.failedFuture(expectedCause));

  }

  /**
   * Should not calculate the median when can not found the median value.
   *
   * @param testContext context that executes the test.
   *
   * @see TrustsRepositoryImpl#calculateMedianTrust(io.vertx.core.json.JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotCalculateMedianTrustWhenFailFindMadian(final VertxTestContext testContext) {

    final var pool = mock(MongoClient.class);
    final var repository = new TrustsRepositoryImpl(new JsonObject(), pool,"version");
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

}
