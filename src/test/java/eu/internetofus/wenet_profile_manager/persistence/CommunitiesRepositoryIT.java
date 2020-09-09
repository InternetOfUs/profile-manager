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

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Integration test over the {@link CommunitiesRepository}.
 *
 * @see CommunitiesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class CommunitiesRepositoryIT {

  /**
   * Verify that can not found a community if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunity(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundUndefinedCommunity(final Vertx vertx, final VertxTestContext testContext) {

    CommunitiesRepository.createProxy(vertx).searchCommunity("undefined community identifier", testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can not found a community object if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunityObject(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundUndefinedCommunityObject(final Vertx vertx, final VertxTestContext testContext) {

    CommunitiesRepository.createProxy(vertx).searchCommunityObject("undefined community identifier", testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can found a community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunity(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(new CommunityProfile(), testContext.succeeding(storedCommunity -> {

      repository.searchCommunity(storedCommunity.id, testContext.succeeding(foundCommunity -> testContext.verify(() -> {
        assertThat(foundCommunity).isEqualTo(storedCommunity);
        testContext.completeNow();
      })));

    }));

  }

  /**
   * Verify that can found a community object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunityObject(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundCommunityObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(new JsonObject(), testContext.succeeding(storedCommunity -> {

      repository.searchCommunityObject(storedCommunity.getString("id"), testContext.succeeding(foundCommunity -> testContext.verify(() -> {
        assertThat(foundCommunity).isEqualTo(storedCommunity);
        testContext.completeNow();
      })));

    }));

  }

  /**
   * Verify that can not store a community that can not be an object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#storeCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldNotStoreACommunityThatCanNotBeAnObject(final Vertx vertx, final VertxTestContext testContext) {

    final CommunityProfile community = new CommunityProfile() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObject() {

        return null;
      }
    };
    community.id = "undefined community identifier";
    CommunitiesRepository.createProxy(vertx).storeCommunity(community, testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can store a community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#storeCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldStoreCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var community = new CommunityProfile();
    community._creationTs = 0;
    community._lastUpdateTs = 1;
    final var now = TimeManager.now();
    CommunitiesRepository.createProxy(vertx).storeCommunity(community, testContext.succeeding(storedCommunity -> testContext.verify(() -> {

      assertThat(storedCommunity).isNotNull();
      assertThat(storedCommunity.id).isNotEmpty();
      assertThat(storedCommunity._creationTs).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
      assertThat(storedCommunity._lastUpdateTs).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
      testContext.completeNow();
    })));

  }

  /**
   * Verify that can store a community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#storeCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldStoreCommunityWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var community = new CommunityProfile();
    community.id = id;
    community._creationTs = 0;
    community._lastUpdateTs = 1;
    final var now = TimeManager.now();
    CommunitiesRepository.createProxy(vertx).storeCommunity(community, testContext.succeeding(storedCommunity -> testContext.verify(() -> {

      assertThat(storedCommunity.id).isEqualTo(id);
      assertThat(storedCommunity._creationTs).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
      assertThat(storedCommunity._lastUpdateTs).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
      testContext.completeNow();
    })));

  }

  /**
   * Verify that can store a community with an id of an stored community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#storeCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldNotStoreTwoCommunityWithTheSameId(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var community = new CommunityProfile();
    community.id = id;
    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(community, testContext.succeeding(storedCommunity -> testContext.verify(() -> {

      repository.storeCommunity(community, testContext.failing(error -> testContext.completeNow()));

    })));

  }

  /**
   * Verify that can store a community object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#storeCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldStoreCommunityObject(final Vertx vertx, final VertxTestContext testContext) {

    final var now = TimeManager.now();
    CommunitiesRepository.createProxy(vertx).storeCommunity(new JsonObject(), testContext.succeeding(storedCommunity -> testContext.verify(() -> {

      assertThat(storedCommunity).isNotNull();
      final var id = storedCommunity.getString("id");
      assertThat(id).isNotEmpty();
      assertThat(storedCommunity.getLong("_creationTs", 0l)).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
      assertThat(storedCommunity.getLong("_lastUpdateTs", 1l)).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
      testContext.completeNow();
    })));

  }

  /**
   * Verify that can not update a community if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(CommunityProfile, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateUndefinedCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var community = new CommunityProfile();
    community.id = "undefined community identifier";
    CommunitiesRepository.createProxy(vertx).updateCommunity(community, testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can not update a community if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateUndefinedCommunityObject(final Vertx vertx, final VertxTestContext testContext) {

    final var community = new JsonObject().put("id", "undefined community identifier");
    CommunitiesRepository.createProxy(vertx).updateCommunity(community, testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can not update a community if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(CommunityProfile, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateACommunityThatCanNotBeAnObject(final Vertx vertx, final VertxTestContext testContext) {

    final CommunityProfile community = new CommunityProfile() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObject() {

        return null;
      }
    };
    community.id = "undefined community identifier";
    CommunitiesRepository.createProxy(vertx).updateCommunity(community, testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can update a community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(CommunityProfile, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var community = new CommunityProfile();
    community.name = "NEW NAME";
    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(community, testContext.succeeding(stored -> testContext.verify(() -> {

      final var now = TimeManager.now();
      final var update = new CommunityProfileTest().createModelExample(23);
      update.id = stored.id;
      update._creationTs = stored._creationTs;
      update._lastUpdateTs = 1;
      repository.updateCommunity(update, testContext.succeeding(empty -> testContext.verify(() -> {

        repository.searchCommunity(stored.id, testContext.succeeding(foundCommunity -> testContext.verify(() -> {

          assertThat(stored).isNotNull();
          assertThat(foundCommunity.id).isNotEmpty().isEqualTo(stored.id);
          assertThat(foundCommunity._creationTs).isEqualTo(stored._creationTs);
          assertThat(foundCommunity._lastUpdateTs).isGreaterThanOrEqualTo(now);
          update._lastUpdateTs = foundCommunity._lastUpdateTs;
          assertThat(foundCommunity).isEqualTo(update);
          testContext.completeNow();
        })));
      })));

    })));

  }

  /**
   * Verify that update a defined community object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateCommunityObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(new JsonObject().put("nationality", "Italian"), testContext.succeeding(stored -> testContext.verify(() -> {

      final var id = stored.getString("id");
      final var update = new JsonObject().put("id", id).put("occupation", "Unemployed");
      repository.updateCommunity(update, testContext.succeeding(empty -> testContext.verify(() -> {

        repository.searchCommunityObject(id, testContext.succeeding(foundCommunity -> testContext.verify(() -> {
          stored.put("_lastUpdateTs", foundCommunity.getLong("_lastUpdateTs"));
          stored.put("occupation", "Unemployed");
          assertThat(foundCommunity).isEqualTo(stored);
          testContext.completeNow();
        })));
      })));

    })));

  }

  /**
   * Verify that can not delete a community if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunity(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotDeleteUndefinedCommunity(final Vertx vertx, final VertxTestContext testContext) {

    CommunitiesRepository.createProxy(vertx).deleteCommunity("undefined community identifier", testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can delete a community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldDeleteCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(new JsonObject(), testContext.succeeding(stored -> {

      final var id = stored.getString("id");
      repository.deleteCommunity(id, testContext.succeeding(success -> {

        repository.searchCommunityObject(id, testContext.failing(search -> {

          testContext.completeNow();

        }));

      }));

    }));

  }

}
