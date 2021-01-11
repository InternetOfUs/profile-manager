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

import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit test to increases coverage of the {@link ProfilesRepository}
 *
 * @see ProfilesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProfilesRepositoryTest {

  /**
   * Verify that can not found a profile because that returned by repository is
   * not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundProfileBecauseReturnedJsonObjectIsNotRight(final VertxTestContext testContext) {

    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      @Override
      public void searchProfile(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

        searchHandler.handle(Future.succeededFuture(new JsonObject().put("key", "value")));

      }
    };

    testContext.assertFailure(repository.searchProfile("any identifier")).onFailure(fail -> testContext.completeNow());

  }

  /**
   * Verify that can not store a profile because that returned by repository is
   * not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile)
   */
  @Test
  public void shouldNotStoreProfileBecauseReturnedJsonObjectIsNotRight(final VertxTestContext testContext) {

    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      @Override
      public void storeProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

        storeHandler.handle(Future.succeededFuture(new JsonObject().put("key", "value")));
      }
    };

    testContext.assertFailure(repository.storeProfile(new WeNetUserProfile()))
        .onFailure(fail -> testContext.completeNow());

  }

  /**
   * Verify that can not store a profile because that returned by repository is
   * not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile)
   */
  @Test
  public void shouldNotStoreProfileBecauseStoreFailed(final VertxTestContext testContext) {

    final Throwable cause = new IllegalArgumentException("Cause that can not be stored");
    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      @Override
      public void storeProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

        storeHandler.handle(Future.failedFuture(cause));
      }

    };

    testContext.assertFailure(repository.storeProfile(new WeNetUserProfile())).onFailure(fail -> {
      assertThat(fail).isEqualTo(cause);
      testContext.completeNow();
    });

  }

  /**
   * Verify that can not update a profile because that returned by repository is
   * not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateProfileBecauseUpdateFailed(final VertxTestContext testContext) {

    final Throwable cause = new IllegalArgumentException("Cause that can not be updated");
    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      @Override
      public void updateProfile(final JsonObject profile, final Handler<AsyncResult<Void>> updateHandler) {

        updateHandler.handle(Future.failedFuture(cause));
      }
    };

    testContext.assertFailure(repository.updateProfile(new WeNetUserProfile()))
        .onFailure(fail -> testContext.verify(() -> {

          assertThat(fail).isEqualTo(cause);
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can not store a profile because that returned by repository is
   * not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreHistoricProfileBecauseReturnedJsonObjectIsNotRight(final VertxTestContext testContext) {

    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      @Override
      public void storeHistoricProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

        storeHandler.handle(Future.succeededFuture(new JsonObject().put("key", "value")));
      }
    };

    testContext.assertFailure(repository.storeHistoricProfile(new HistoricWeNetUserProfile()))
        .onFailure(fail -> testContext.completeNow());

  }

  /**
   * Verify that can not store a profile because that returned by repository is
   * not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreHistoricProfileBecauseStoreFailed(final VertxTestContext testContext) {

    final Throwable cause = new IllegalArgumentException("Cause that can not be stored");
    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      @Override
      public void storeHistoricProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

        storeHandler.handle(Future.failedFuture(cause));
      }

    };

    testContext.assertFailure(repository.storeHistoricProfile(new HistoricWeNetUserProfile()))
        .onFailure(fail -> testContext.verify(() -> {

          assertThat(fail).isEqualTo(cause);
          testContext.completeNow();

        }));

  }

  /**
   * Verify that can not found a historic profile page because that returned by
   * repository is not right.
   *
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchHistoricProfilePage(JsonObject, JsonObject,
   *      int, int, Handler)
   */
  @Test
  public void shouldNotFoundHistoricProfileBecauseReturnedJsonObjectIsNotRight(final VertxTestContext testContext) {

    final ProfilesRepository repository = new ProfilesRepositoryImpl(null, null) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void searchHistoricProfilePageObject(final JsonObject query, final JsonObject sort, final int offset,
          final int limit, final Handler<AsyncResult<JsonObject>> searchHandler) {

        searchHandler.handle(Future.succeededFuture(new JsonObject().put("key", "value")));
      }
    };
    final var query = ProfilesRepository.createProfileHistoricPageQuery("any identifier", 0l, 100l);
    final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
    testContext.assertFailure(repository.searchHistoricProfilePage(query, sort, 0, 100))
        .onFailure(fail -> testContext.completeNow());

  }
}
