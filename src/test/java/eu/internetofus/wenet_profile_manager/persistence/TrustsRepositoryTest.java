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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import eu.internetofus.common.components.profile_manager.UserPerformanceRatingEvent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test the {@link TrustsRepository}.
 *
 * @see TrustsRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class TrustsRepositoryTest {

  /**
   * Should not store trust event when DB failed.
   *
   * @param testContext context that executes the test.
   *
   * @see TrustsRepositoryImpl#storeTrustEvent(UserPerformanceRatingEvent)
   */
  @Test
  public void shouldNotStoreTrustEventWhenDBFailed(final VertxTestContext testContext) {

    final var repository = mock(TrustsRepository.class, Answers.CALLS_REAL_METHODS);
    final var expectedCause = new Throwable("Expected cause");
    final var event = new UserPerformanceRatingEvent();
    doReturn(Future.failedFuture(expectedCause)).when(repository).storeTrustEvent(event);
    testContext.assertFailure(repository.storeTrustEvent(event)).onFailure(cause -> testContext.verify(() -> {

      assertThat(cause).isEqualTo(expectedCause);
      testContext.completeNow();
    }));

  }

  /**
   * Should not store trust event when DB failed.
   *
   * @param testContext context that executes the test.
   *
   * @see TrustsRepositoryImpl#storeTrustEvent(UserPerformanceRatingEvent)
   */
  @Test
  public void shouldNotStoreTrustEventWhenReturnedValueIsNotAnEvent(final VertxTestContext testContext) {

    final var repository = mock(TrustsRepository.class, Answers.CALLS_REAL_METHODS);
    final var event = new UserPerformanceRatingEvent();
    testContext.assertFailure(repository.storeTrustEvent(event)).onFailure(cause -> testContext.completeNow());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> saveHandler = ArgumentCaptor.forClass(Handler.class);
    verify(repository, timeout(30000).times(1)).storeTrustEvent(any(JsonObject.class), saveHandler.capture());
    saveHandler.getValue().handle(Future.succeededFuture());

  }

}
