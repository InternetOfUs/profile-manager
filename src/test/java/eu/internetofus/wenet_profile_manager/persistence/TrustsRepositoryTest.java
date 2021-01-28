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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import eu.internetofus.wenet_profile_manager.api.trusts.UserPerformanceRatingEvent;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
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
   * @see TrustsRepositoryImpl#storeTrustEvent(io.vertx.core.json.JsonObject,
   *      io.vertx.core.Handler)
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
   * @see TrustsRepositoryImpl#storeTrustEvent(io.vertx.core.json.JsonObject,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreTrustEventWhenReturnedValueIsNotAnEvent(final VertxTestContext testContext) {

    final var repository = mock(TrustsRepository.class, Answers.CALLS_REAL_METHODS);
    final var event = new UserPerformanceRatingEvent();
    doReturn(Future.succeededFuture()).when(repository).storeTrustEvent(event);
    testContext.assertFailure(repository.storeTrustEvent(event)).onFailure(cause -> testContext.verify(() -> {

      assertThat(cause.getMessage()).isEqualTo("The stored event is not valid.");
      testContext.completeNow();
    }));

  }

}
