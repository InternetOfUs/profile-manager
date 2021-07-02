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

import eu.internetofus.common.components.models.CommunityMemberTest;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.CommunityProfileTest;
import eu.internetofus.common.vertx.ModelsPageContext;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

    CommunitiesRepository.createProxy(vertx).searchCommunity("undefined community identifier",
        testContext.failing(failed -> {
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can not found a community object if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunity(String)
   */
  @Test
  public void shouldNotFoundUndefinedCommunityObject(final Vertx vertx, final VertxTestContext testContext) {

    CommunitiesRepository.createProxy(vertx).searchCommunity("undefined community identifier")
        .onComplete(testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Verify that can found a community.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunity(String)
   */
  @Test
  public void shouldFoundCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(new CommunityProfile(), testContext.succeeding(storedCommunity -> {

      repository.searchCommunity(storedCommunity.id)
          .onComplete(testContext.succeeding(foundCommunity -> testContext.verify(() -> {
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
    CommunitiesRepository.createProxy(vertx).storeCommunity(community,
        testContext.succeeding(storedCommunity -> testContext.verify(() -> {

          assertThat(storedCommunity).isNotNull();
          assertThat(storedCommunity.id).isNotEmpty();
          assertThat(storedCommunity._creationTs).isEqualTo(0);
          assertThat(storedCommunity._lastUpdateTs).isEqualTo(1);
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
    community._creationTs = 1;
    community._lastUpdateTs = 2;
    CommunitiesRepository.createProxy(vertx).storeCommunity(community,
        testContext.succeeding(storedCommunity -> testContext.verify(() -> {

          assertThat(storedCommunity.id).isEqualTo(id);
          assertThat(storedCommunity._creationTs).isEqualTo(1);
          assertThat(storedCommunity._lastUpdateTs).isEqualTo(2);
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

    CommunitiesRepository.createProxy(vertx).storeCommunity(new JsonObject(),
        testContext.succeeding(storedCommunity -> testContext.verify(() -> {

          assertThat(storedCommunity).isNotNull();
          final var id = storedCommunity.getString("id");
          assertThat(id).isNotEmpty();
          assertThat(storedCommunity.containsKey("_creationTs")).isFalse();
          assertThat(storedCommunity.containsKey("_lastUpdateTs")).isFalse();
          testContext.completeNow();
        })));

  }

  /**
   * Verify that can not update a community if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(CommunityProfile,
   *      io.vertx.core.Handler)
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
   * @see CommunitiesRepository#updateCommunity(CommunityProfile,
   *      io.vertx.core.Handler)
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
   * @see CommunitiesRepository#updateCommunity(CommunityProfile,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateCommunity(final Vertx vertx, final VertxTestContext testContext) {

    final var community = new CommunityProfile();
    community.name = "NEW NAME";
    final var repository = CommunitiesRepository.createProxy(vertx);
    repository.storeCommunity(community, testContext.succeeding(stored -> testContext.verify(() -> {

      final var update = new CommunityProfileTest().createModelExample(23);
      update.id = stored.id;
      update._creationTs = stored._creationTs;
      update._lastUpdateTs = 1;
      repository.updateCommunity(update, testContext.succeeding(empty -> testContext.verify(() -> {

        repository.searchCommunity(stored.id)
            .onComplete(testContext.succeeding(foundCommunity -> testContext.verify(() -> {

              assertThat(stored).isNotNull();
              assertThat(foundCommunity.id).isNotEmpty().isEqualTo(stored.id);
              assertThat(foundCommunity._creationTs).isEqualTo(stored._creationTs);
              assertThat(foundCommunity._lastUpdateTs).isEqualTo(1);
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
    final var createTs = 123;
    final var updateTs = 456;
    repository.storeCommunity(
        new JsonObject().put("name", "Community Name").put("_creationTs", createTs).put("_lastUpdateTs", updateTs),
        testContext.succeeding(stored -> testContext.verify(() -> {

          final var id = stored.getString("id");
          final var update = new JsonObject().put("id", id).put("description", "Community Description")
              .put("_creationTs", createTs + 12345).put("_lastUpdateTs", updateTs + 12345);
          repository.updateCommunity(update, testContext.succeeding(empty -> testContext.verify(() -> {

            repository.searchCommunity(id, testContext.succeeding(foundCommunity -> testContext.verify(() -> {
              stored.put("_lastUpdateTs", updateTs + 12345);
              stored.put("description", "Community Description");
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

    CommunitiesRepository.createProxy(vertx).deleteCommunity("undefined community identifier",
        testContext.failing(failed -> {
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

        repository.searchCommunity(id, testContext.failing(search -> {

          testContext.completeNow();

        }));

      }));

    }));

  }

  /**
   * Create some
   * {@link CommunityProfileTest#createModelExample(int, Vertx, VertxTestContext)}.
   *
   * @param vertx           event bus to use.
   * @param testContext     context that executes the test.
   * @param change          function to modify the pattern before to store it.
   * @param max             number maximum of communities to create.
   * @param communities     list to add the created communities.
   * @param creationHandler that manage the creation.
   */
  public static void storeSomeCommunityProfiles(final Vertx vertx, final VertxTestContext testContext,
      final Consumer<CommunityProfile> change, final int max, final List<CommunityProfile> communities,
      final Handler<AsyncResult<Void>> creationHandler) {

    if (communities.size() == max) {

      creationHandler.handle(Future.succeededFuture());

    } else {

      final var community = new CommunityProfileTest().createModelExample(communities.size());
      community.id = null;
      change.accept(community);
      CommunitiesRepository.createProxy(vertx).storeCommunity(community, testContext.succeeding(stored -> {

        communities.add(stored);
        storeSomeCommunityProfiles(vertx, testContext, change, max, communities, creationHandler);
      }));
    }

  }

  /**
   * Check that retrieve the expected tasks by goal application identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPageObject(JsonObject,
   *      JsonObject, int, int, Handler)
   */
  @Test
  public void shouldRetrieveCommunityProfilesByAppId(final Vertx vertx, final VertxTestContext testContext) {

    final var appId = UUID.randomUUID().toString();
    final var context = new ModelsPageContext();
    context.query = CommunitiesRepository.createCommunityProfilesPageQuery(appId, null, null, null, null);
    context.limit = 10;
    CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(context,
        testContext.succeeding(search -> testContext.verify(() -> {

          assertThat(search).isNotNull();
          assertThat(search.total).isEqualTo(0);
          assertThat(search.offset).isEqualTo(0);
          final List<CommunityProfile> communities = new ArrayList<>();
          storeSomeCommunityProfiles(vertx, testContext, community -> community.appId = appId, 10, communities,
              testContext.succeeding(empty -> {

                CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(context,
                    testContext.succeeding(search2 -> testContext.verify(() -> {

                      assertThat(search2).isNotNull();
                      assertThat(search2.total).isEqualTo(10);
                      assertThat(search2.offset).isEqualTo(0);
                      assertThat(search2.communities).isEqualTo(communities);

                      context.offset = 2;
                      context.limit = 3;
                      CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(context,
                          testContext.succeeding(search3 -> testContext.verify(() -> {

                            assertThat(search3).isNotNull();
                            assertThat(search3.total).isEqualTo(10);
                            assertThat(search3.offset).isEqualTo(2);
                            assertThat(search3.communities).isEqualTo(communities.subList(2, 5));
                            testContext.completeNow();

                          })));

                    })));
              }));
        })));

  }

  /**
   * Check that retrieve the expected tasks by goal name.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPageObject(JsonObject,
   *      JsonObject, int, int, Handler)
   */
  @Test
  public void shouldRetrieveCommunityProfilesByName(final Vertx vertx, final VertxTestContext testContext) {

    final var name = UUID.randomUUID().toString();
    final var context = new ModelsPageContext();
    context.query = CommunitiesRepository.createCommunityProfilesPageQuery(null, "/.*" + name + ".*/", null, null,
        null);
    context.limit = 10;
    CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(context,
        testContext.succeeding(search -> testContext.verify(() -> {

          assertThat(search).isNotNull();
          assertThat(search.total).isEqualTo(0);
          assertThat(search.offset).isEqualTo(0);
          final List<CommunityProfile> communities = new ArrayList<>();
          storeSomeCommunityProfiles(vertx, testContext, community -> community.name = name + "_" + communities.size(),
              10, communities, testContext.succeeding(empty -> {

                context.sort = new JsonObject().put("name", -1);
                CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(context,
                    testContext.succeeding(search2 -> testContext.verify(() -> {

                      assertThat(search2).isNotNull();
                      assertThat(search2.total).isEqualTo(10);
                      assertThat(search2.offset).isEqualTo(0);
                      Collections.reverse(communities);
                      assertThat(search2.communities).isEqualTo(communities);
                      context.sort = new JsonObject().put("name", 1);

                      context.offset = 2;
                      CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(context,
                          testContext.succeeding(search3 -> testContext.verify(() -> {

                            Collections.reverse(communities);
                            assertThat(search3).isNotNull();
                            assertThat(search3.total).isEqualTo(10);
                            assertThat(search3.offset).isEqualTo(2);
                            assertThat(search3.communities).isEqualTo(communities.subList(2, 10));
                            testContext.completeNow();

                          })));

                    })));
              }));
        })));

  }

  /**
   * Check that retrieve the expected tasks by goal description.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPageObject(JsonObject,
   *      JsonObject, int, int, Handler)
   */
  @Test
  public void shouldRetrieveCommunityProfilesByDescription(final Vertx vertx, final VertxTestContext testContext) {

    final var description = UUID.randomUUID().toString();
    final var query = CommunitiesRepository.createCommunityProfilesPageQuery(null, null, description, null, null);
    CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, null, 0, 10,
        testContext.succeeding(search -> testContext.verify(() -> {

          assertThat(search).isNotNull();
          assertThat(search.total).isEqualTo(0);
          assertThat(search.offset).isEqualTo(0);
          final List<CommunityProfile> communities = new ArrayList<>();
          storeSomeCommunityProfiles(vertx, testContext, community -> community.description = description, 10,
              communities, testContext.succeeding(empty -> {

                CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, new JsonObject(), 0, 10,
                    testContext.succeeding(search2 -> testContext.verify(() -> {

                      assertThat(search2).isNotNull();
                      assertThat(search2.total).isEqualTo(10);
                      assertThat(search2.offset).isEqualTo(0);
                      assertThat(search2.communities).isEqualTo(communities);
                      CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, new JsonObject(), 2,
                          5, testContext.succeeding(search3 -> testContext.verify(() -> {

                            assertThat(search3).isNotNull();
                            assertThat(search3.total).isEqualTo(10);
                            assertThat(search3.offset).isEqualTo(2);
                            assertThat(search3.communities).isEqualTo(communities.subList(2, 7));
                            testContext.completeNow();

                          })));

                    })));
              }));
        })));

  }

  /**
   * Check that retrieve the expected tasks by goal keywords.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPageObject(JsonObject,
   *      JsonObject, int, int, Handler)
   */
  @Test
  public void shouldRetrieveCommunityProfilesByKeywords(final Vertx vertx, final VertxTestContext testContext) {

    final var keywords = new ArrayList<String>();
    final var keyword = UUID.randomUUID().toString();
    keywords.add(keyword);
    final var query = CommunitiesRepository.createCommunityProfilesPageQuery(null, null, null, keywords, null);
    CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, null, 0, 10,
        testContext.succeeding(search -> testContext.verify(() -> {

          assertThat(search).isNotNull();
          assertThat(search.total).isEqualTo(0);
          assertThat(search.offset).isEqualTo(0);
          final List<CommunityProfile> communities = new ArrayList<>();
          storeSomeCommunityProfiles(vertx, testContext, community -> community.keywords.add(keyword), 10, communities,
              testContext.succeeding(empty -> {

                CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, new JsonObject(), 0, 10,
                    testContext.succeeding(search2 -> testContext.verify(() -> {

                      assertThat(search2).isNotNull();
                      assertThat(search2.total).isEqualTo(10);
                      assertThat(search2.offset).isEqualTo(0);
                      assertThat(search2.communities).isEqualTo(communities);
                      CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, new JsonObject(), 2,
                          5, testContext.succeeding(search3 -> testContext.verify(() -> {

                            assertThat(search3).isNotNull();
                            assertThat(search3.total).isEqualTo(10);
                            assertThat(search3.offset).isEqualTo(2);
                            assertThat(search3.communities).isEqualTo(communities.subList(2, 7));
                            testContext.completeNow();

                          })));

                    })));
              }));
        })));

  }

  /**
   * Check that retrieve the expected tasks by goal members.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPageObject(JsonObject,
   *      JsonObject, int, int, Handler)
   */
  @Test
  public void shouldRetrieveCommunityProfilesByMembers(final Vertx vertx, final VertxTestContext testContext) {

    final var members = new ArrayList<String>();
    final var member = new CommunityMemberTest().createModelExample(1);
    member.userId = UUID.randomUUID().toString();
    members.add(member.userId);
    final var query = CommunitiesRepository.createCommunityProfilesPageQuery(null, null, null, null, members);
    CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, null, 0, 10,
        testContext.succeeding(search -> testContext.verify(() -> {

          assertThat(search).isNotNull();
          assertThat(search.total).isEqualTo(0);
          assertThat(search.offset).isEqualTo(0);
          final List<CommunityProfile> communities = new ArrayList<>();
          storeSomeCommunityProfiles(vertx, testContext, community -> community.members.add(member), 10, communities,
              testContext.succeeding(empty -> {

                CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query, new JsonObject(), 0, 10,
                    testContext.succeeding(search2 -> testContext.verify(() -> {

                      assertThat(search2).isNotNull();
                      assertThat(search2.total).isEqualTo(10);
                      assertThat(search2.offset).isEqualTo(0);
                      assertThat(search2.communities).isEqualTo(communities);

                      members.add("/User_.*/");
                      final var query2 = CommunitiesRepository.createCommunityProfilesPageQuery(null, null, null, null,
                          members);
                      CommunitiesRepository.createProxy(vertx).retrieveCommunityProfilesPage(query2, new JsonObject(),
                          2, 5, testContext.succeeding(search3 -> testContext.verify(() -> {

                            assertThat(search3).isNotNull();
                            assertThat(search3.total).isEqualTo(10);
                            assertThat(search3.offset).isEqualTo(2);
                            assertThat(search3.communities).isEqualTo(communities.subList(2, 7));
                            testContext.completeNow();

                          })));

                    })));
              }));
        })));

  }

}
