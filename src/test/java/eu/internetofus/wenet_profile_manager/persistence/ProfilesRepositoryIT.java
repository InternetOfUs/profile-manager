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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfileTest;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfilesPage;
import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfilesPage;
import eu.internetofus.wenet_profile_manager.api.user_identifiers.UserIdentifiersPage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link ProfilesRepository}.
 *
 * @see ProfilesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesRepositoryIT {

  /**
   * Verify that can not found a profile if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundUndefinedProfile(final Vertx vertx, final VertxTestContext testContext) {

    ProfilesRepository.createProxy(vertx).searchProfile("undefined user identifier", testContext.failing(failed -> {
      testContext.completeNow();
    }));

  }

  /**
   * Verify that can not found a profile object if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundUndefinedProfileObject(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(ProfilesRepository.createProxy(vertx).searchProfile("undefined user identifier"))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can found a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String)
   */
  @Test
  public void shouldFoundProfile(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    testContext.assertComplete(repository.storeProfile(new WeNetUserProfile())).onSuccess(storedProfile -> {

      testContext.assertComplete(repository.searchProfile(storedProfile.id))
          .onSuccess(foundProfile -> testContext.verify(() -> {
            assertThat(foundProfile).isEqualTo(storedProfile);
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify that can found a profile object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundProfileObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    repository.storeProfile(new JsonObject(), testContext.succeeding(storedProfile -> {

      repository.searchProfile(storedProfile.getString("id"),
          testContext.succeeding(foundProfile -> testContext.verify(() -> {
            assertThat(foundProfile).isEqualTo(storedProfile);
            testContext.completeNow();
          })));

    }));

  }

  /**
   * Verify that can not store a profile that can not be an object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile)
   */
  @Test
  public void shouldNotStoreAProfileThatCanNotBeAnObject(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetUserProfile profile = new WeNetUserProfile() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObject() {

        return null;
      }
    };
    profile.id = "undefined user identifier";
    testContext.assertFailure(ProfilesRepository.createProxy(vertx).storeProfile(profile))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Verify that can store a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile)
   */
  @Test
  public void shouldStoreProfile(final Vertx vertx, final VertxTestContext testContext) {

    final var profile = new WeNetUserProfile();
    profile._creationTs = 0;
    profile._lastUpdateTs = 1;
    final var now = TimeManager.now();
    testContext.assertComplete(ProfilesRepository.createProxy(vertx).storeProfile(profile))
        .onSuccess(storedProfile -> testContext.verify(() -> {

          assertThat(storedProfile).isNotNull();
          assertThat(storedProfile.id).isNotEmpty();
          assertThat(storedProfile._creationTs).isBetween(now, now + 1);
          assertThat(storedProfile._lastUpdateTs).isEqualTo(storedProfile._creationTs);
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can store a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile )
   */
  @Test
  public void shouldStoreProfileWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var profile = new WeNetUserProfile();
    profile.id = id;
    profile._creationTs = 0;
    profile._lastUpdateTs = 1;
    final var now = TimeManager.now();
    testContext.assertComplete(ProfilesRepository.createProxy(vertx).storeProfile(profile))
        .onSuccess(storedProfile -> testContext.verify(() -> {

          assertThat(storedProfile.id).isEqualTo(id);
          assertThat(storedProfile._creationTs).isBetween(now, now + 1);
          assertThat(storedProfile._lastUpdateTs).isEqualTo(storedProfile._creationTs);
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can store a profile with an id of an stored profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile)
   */
  @Test
  public void shouldNotStoreTwoProfileWithTheSameId(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var profile = new WeNetUserProfile();
    profile.id = id;
    final var repository = ProfilesRepository.createProxy(vertx);
    testContext.assertComplete(repository.storeProfile(profile)).onComplete(storedProfile -> testContext
        .assertFailure(repository.storeProfile(profile)).onFailure(error -> testContext.completeNow()));

  }

  /**
   * Verify that can store a profile object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeProfile(WeNetUserProfile)
   */
  @Test
  public void shouldStoreProfileObject(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(ProfilesRepository.createProxy(vertx).storeProfile(new WeNetUserProfile()))
        .onSuccess(storedProfile -> testContext.verify(() -> {
          assertThat(storedProfile).isNotNull();
          assertThat(storedProfile.id).isNotEmpty();
          assertThat(storedProfile._creationTs).isGreaterThan(0l);
          assertThat(storedProfile._lastUpdateTs).isGreaterThan(0l);
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can not update a profile if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#updateProfile(WeNetUserProfile)
   */
  @Test
  public void shouldNotUpdateUndefinedProfile(final Vertx vertx, final VertxTestContext testContext) {

    final var profile = new WeNetUserProfile();
    profile.id = "undefined user identifier";
    testContext.assertFailure(ProfilesRepository.createProxy(vertx).updateProfile(profile))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can not update a profile if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#updateProfile(WeNetUserProfile)
   */
  @Test
  public void shouldNotUpdateAProfileThatCanNotBeAnObject(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetUserProfile profile = new WeNetUserProfile() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObject() {

        return null;
      }
    };
    profile.id = "undefined user identifier";
    testContext.assertFailure(ProfilesRepository.createProxy(vertx).updateProfile(profile))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can update a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#updateProfile(WeNetUserProfile)
   */
  @Test
  public void shouldUpdateProfile(final Vertx vertx, final VertxTestContext testContext) {

    final var profile = new WeNetUserProfile();
    profile.occupation = "Doctor";
    testContext.assertComplete(ProfilesRepository.createProxy(vertx).storeProfile(profile)).onSuccess(stored -> {

      final var update = new WeNetUserProfileTest().createModelExample(23);
      update.id = stored.id;
      update._creationTs = stored._creationTs;
      update._lastUpdateTs = 1;
      final var now = TimeManager.now();
      testContext
          .assertComplete(ProfilesRepository.createProxy(vertx).updateProfile(update)
              .compose(empty -> ProfilesRepository.createProxy(vertx).searchProfile(stored.id)))
          .onSuccess(foundProfile -> testContext.verify(() -> {

            assertThat(stored).isNotNull();
            assertThat(foundProfile.id).isNotEmpty().isEqualTo(stored.id);
            assertThat(foundProfile._creationTs).isEqualTo(stored._creationTs);
            assertThat(foundProfile._lastUpdateTs).isBetween(now, now + 1);
            update._lastUpdateTs = foundProfile._lastUpdateTs;
            assertThat(foundProfile).isEqualTo(update);
            testContext.completeNow();
          }));

    });

  }

  /**
   * Verify that update a defined profile object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#updateProfile(JsonObject, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    final var createTs = 123;
    final var updateTs = 456;
    repository.storeProfile(
        new JsonObject().put("nationality", "Italian").put("_creationTs", createTs).put("_lastUpdateTs", updateTs),
        testContext.succeeding(stored -> testContext.verify(() -> {

          final var id = stored.getString("id");
          final var update = new JsonObject().put("id", id).put("occupation", "Unemployed")
              .put("_creationTs", createTs + 12345).put("_lastUpdateTs", updateTs + 12345);
          repository.updateProfile(update, testContext.succeeding(empty -> testContext.verify(() -> {

            repository.searchProfile(id, testContext.succeeding(foundProfile -> testContext.verify(() -> {
              stored.put("_lastUpdateTs", updateTs + 12345);
              stored.put("occupation", "Unemployed");
              assertThat(foundProfile).isEqualTo(stored);
              testContext.completeNow();
            })));
          })));

        })));

  }

  /**
   * Verify that can not delete a profile if it is not defined.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotDeleteUndefinedProfile(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(ProfilesRepository.createProxy(vertx).deleteProfile("undefined user identifier"))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can delete a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#deleteProfile(String)
   */
  @Test
  public void shouldDeleteProfile(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    testContext.assertComplete(repository.storeProfile(new WeNetUserProfile())).onSuccess(stored -> {

      testContext.assertComplete(repository.deleteProfile(stored.id)).onSuccess(success -> {

        testContext.assertFailure(repository.searchProfile(stored.id)).onFailure(failed -> testContext.completeNow());

      });

    });

  }

  /**
   * Verify that can not store a profile that can not be an object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#storeHistoricProfile(HistoricWeNetUserProfile)
   */
  @Test
  public void shouldNotStoreAHistoricProfileThatCanNotBeAnObject(final Vertx vertx,
      final VertxTestContext testContext) {

    final HistoricWeNetUserProfile profile = new HistoricWeNetUserProfile() {

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObject() {

        return null;
      }
    };
    testContext.assertFailure(ProfilesRepository.createProxy(vertx).storeHistoricProfile(profile))
        .onFailure(failed -> testContext.completeNow());

  }

  /**
   * Verify that can store a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreHistoricProfile(final Vertx vertx, final VertxTestContext testContext) {

    final var profile = new HistoricWeNetUserProfile();
    testContext.assertComplete(ProfilesRepository.createProxy(vertx).storeHistoricProfile(profile))
        .onSuccess(storedProfile -> testContext.verify(() -> {

          assertThat(storedProfile).isNotNull();
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can store a profile object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreHistoricProfileObject(final Vertx vertx, final VertxTestContext testContext) {

    ProfilesRepository.createProxy(vertx).storeHistoricProfile(new JsonObject(),
        testContext.succeeding(storedProfile -> testContext.verify(() -> {

          assertThat(storedProfile).isNotNull();
          testContext.completeNow();
        })));

  }

  /**
   * Verify that can not found any historic profiles that match to an undefined
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchHistoricProfilePage(JsonObject, JsonObject,
   *      int, int)
   */
  @Test
  public void shouldNotFoundAnyHistoricProfileFromAnUdefinedId(final Vertx vertx, final VertxTestContext testContext) {

    final var query = ProfilesRepository.createProfileHistoricPageQuery("undefined user identifier", 0l,
        Long.MAX_VALUE);
    final var sort = ProfilesRepository.createProfileHistoricPageSort("-");
    testContext.assertComplete(ProfilesRepository.createProxy(vertx).searchHistoricProfilePage(query, sort, 0, 100))
        .onSuccess(found -> testContext.verify(() -> {
          assertThat(found.offset).isEqualTo(0);
          assertThat(found.total).isEqualTo(0);
          assertThat(found.profiles).isNull();
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can not found any profile object that match to an undefined
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchHistoricProfilePage(JsonObject, JsonObject,
   *      int, int)
   */
  @Test
  public void shouldNotFoundAnyHistoricProfileObjectFromAnUdefinedId(final Vertx vertx,
      final VertxTestContext testContext) {

    final var query = ProfilesRepository.createProfileHistoricPageQuery("undefined user identifier", 0l,
        Long.MAX_VALUE);
    final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
    ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 0, 100,
        testContext.succeeding(found -> testContext.verify(() -> {
          assertThat(found.getLong("offset")).isEqualTo(0);
          assertThat(found.getLong("total")).isEqualTo(0);
          assertThat(found.getJsonArray("profiles")).isNull();
          testContext.completeNow();
        })));

  }

  /**
   * Verify that can found a profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchHistoricProfilePage(JsonObject, JsonObject,
   *      int, int)
   */
  @Test
  public void shouldFoundHistoricProfilePage(final Vertx vertx, final VertxTestContext testContext) {

    final var historic = new HistoricWeNetUserProfile();
    historic.from = 10000;
    historic.to = 1000000;
    historic.profile = new WeNetUserProfileTest().createBasicExample(1);
    final var id = UUID.randomUUID().toString();
    historic.profile.id = id;
    final var repository = ProfilesRepository.createProxy(vertx);
    testContext.assertComplete(repository.storeHistoricProfile(historic)).onSuccess(storedProfile -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(id, 0l, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("-");
      testContext.assertComplete(repository.searchHistoricProfilePage(query, sort, 0, 100))
          .onSuccess(foundProfile -> testContext.verify(() -> {

            final var page = new HistoricWeNetUserProfilesPage();
            page.total = 1;
            page.offset = 0;
            page.profiles = new ArrayList<>();
            page.profiles.add(historic);
            assertThat(foundProfile).isEqualTo(page);
            testContext.completeNow();
          }));
    });

  }

  /**
   * Create a profile page.
   *
   * @param total       number of profiles that has to be defined.
   * @param userId      identifier of the profile to get the historic.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future profile page.
   */
  public static Future<HistoricWeNetUserProfilesPage> createProfilePage(final int total, final String userId,
      final Vertx vertx, final VertxTestContext testContext) {

    final Promise<HistoricWeNetUserProfilesPage> promise = Promise.promise();
    var future = promise.future();
    for (var i = 0; i < total; i++) {

      future = future.compose(page -> {

        final var numProfiles = page.profiles.size();
        final var historic = new HistoricWeNetUserProfileTest().createModelExample(numProfiles);
        historic.from = numProfiles * 10000;
        historic.to = (1 + numProfiles) * 10000;
        historic.profile.id = userId;

        return ProfilesRepository.createProxy(vertx).storeHistoricProfile(historic).compose(stored -> {

          page.profiles.add(stored);
          return Future.succeededFuture(page);
        });

      });

    }

    final var page = new HistoricWeNetUserProfilesPage();
    page.total = total;
    page.profiles = new ArrayList<>();
    promise.complete(page);

    return testContext.assertComplete(future);
  }

  /**
   * Verify that can found a profile object.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageObject(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 0l, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 0, 100,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();
          })));

    });

  }

  /**
   * Verify that can found a profile object from a date.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageObjectWithFrom(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 70000L, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 0, 100,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            created.total = 13;
            created.profiles = created.profiles.subList(7, 20);
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();
          })));

    });

  }

  /**
   * Verify that can found a profile object to a date.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageObjectWithTo(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 0L, 70000L);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 0, 100,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            created.total = 7;
            created.profiles = created.profiles.subList(0, 7);
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();
          })));

    });

  }

  /**
   * Verify that can found a profile object on descending order.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchHistoricProfilePage(JsonObject, JsonObject,
   *      int, int)
   */
  @Test
  public void shouldFoundHistoricProfilePageObjectOnDescendingOrder(final Vertx vertx,
      final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 0L, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("-");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 0, 100,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            Collections.reverse(created.profiles);
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();
          })));

    });

  }

  /**
   * Verify that can found a profile object from an offset.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageObjectWithOffset(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 0L, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 5, 100,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            created.profiles = created.profiles.subList(5, 20);
            created.offset = 5;
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();
          })));

    });

  }

  /**
   * Verify that return empty page if the offset is greater than the total.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageObjectWithOffsetBiggerThanTotal(final Vertx vertx,
      final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 0L, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 21, 100,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            created.profiles = null;
            created.offset = 21;
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();
          })));

    });

  }

  /**
   * Verify that can found a profile object with a limit.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageObjectWithLimit(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      final var query = ProfilesRepository.createProfileHistoricPageQuery(userId, 0L, Long.MAX_VALUE);
      final var sort = ProfilesRepository.createProfileHistoricPageSort("+");
      ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(query, sort, 0, 10,
          testContext.succeeding(found -> testContext.verify(() -> {

            final var foundModel = Model.fromJsonObject(found, HistoricWeNetUserProfilesPage.class);
            created.profiles = created.profiles.subList(0, 10);
            assertThat(foundModel).isEqualTo(created);
            testContext.completeNow();

          })));

    });

  }

  /**
   * Should update a full profile to an empty one.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   */
  @Test
  public void shouldUpdateToEmptyProfile(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfileExample(1, vertx, testContext)).onSuccess(profile -> {

      final var emptyProfile = new WeNetUserProfile();
      emptyProfile.id = profile.id;
      testContext.assertComplete(ProfilesRepository.createProxy(vertx).updateProfile(emptyProfile)
          .compose(stored -> ProfilesRepository.createProxy(vertx).searchProfile(profile.id))).onSuccess(

              found -> testContext.verify(() -> {

                emptyProfile._lastUpdateTs = found._lastUpdateTs;
                assertThat(found).isEqualTo(emptyProfile);
                testContext.completeNow();

              }));

    });

  }

  /**
   * Should retrieve profiles user identifiers page.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   */
  @Test
  public void shouldRetrieveProfileUserIdsPageObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    repository.retrieveProfileUserIdsPageObject(0, Integer.MAX_VALUE,
        testContext.succeeding(page -> testContext.verify(() -> {

          final var model = Model.fromJsonObject(page, UserIdentifiersPage.class);
          assertThat(model).isNotNull();
          if (model.total > 0) {

            assertThat(model.userIds).isNotEmpty().hasSize((int) model.total);
          }
          vertx.setTimer(1500, time -> {
            testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
                .onSuccess(profile -> {
                  testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
                      .onSuccess(profile2 -> {
                        testContext
                            .assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
                            .onSuccess(profile3 -> {

                              repository.retrieveProfileUserIdsPageObject(0, Integer.MAX_VALUE,
                                  testContext.succeeding(page2 -> testContext.verify(() -> {

                                    final var model2 = Model.fromJsonObject(page2, UserIdentifiersPage.class);
                                    assertThat(model2).isNotNull();
                                    assertThat(model2.offset).isEqualTo(0);
                                    assertThat(model2.total).isEqualTo(model.total + 3);
                                    model.userIds.add(profile.id);
                                    model.userIds.add(profile2.id);
                                    model.userIds.add(profile3.id);
                                    assertThat(model2.userIds).isNotEmpty().hasSize((int) model.total + 3)
                                        .isEqualTo(model.userIds);
                                    repository.retrieveProfileUserIdsPageObject((int) model.total, 2,
                                        testContext.succeeding(page3 -> testContext.verify(() -> {

                                          final var model3 = Model.fromJsonObject(page3, UserIdentifiersPage.class);
                                          assertThat(model3).isNotNull();
                                          assertThat(model3.offset).isEqualTo(model.total);
                                          assertThat(model3.total).isEqualTo(model.total + 3);
                                          assertThat(model3.userIds).isNotEmpty().hasSize(2).containsExactly(profile.id,
                                              profile2.id);
                                          testContext.completeNow();

                                        })));

                                  })));
                            });
                      });
                });
          });
        })));

  }

  /**
   * Should retrieve empty profiles user identifiers page.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   */
  @Test
  public void shouldRetrieveEmptyProfileUserIdsPageObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    repository.retrieveProfileUserIdsPageObject(Integer.MAX_VALUE, 100,
        testContext.succeeding(page -> testContext.verify(() -> {

          final var model = Model.fromJsonObject(page, UserIdentifiersPage.class);
          assertThat(model).isNotNull();
          assertThat(model.userIds).isNullOrEmpty();
          testContext.completeNow();

        })));
  }

  /**
   * Should retrieve profiles page.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   */
  @Test
  public void shouldRetrieveProfilesPageObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    repository.retrieveProfilesPageObject(0, Integer.MAX_VALUE,
        testContext.succeeding(page -> testContext.verify(() -> {

          final var model = Model.fromJsonObject(page, WeNetUserProfilesPage.class);
          assertThat(model).isNotNull();
          if (model.total > 0) {

            assertThat(model.profiles).isNotEmpty().hasSize((int) model.total);
          }
          vertx.setTimer(1500, time -> {
            testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
                .onSuccess(profile -> {
                  testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
                      .onSuccess(profile2 -> {
                        testContext
                            .assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
                            .onSuccess(profile3 -> {

                              repository.retrieveProfilesPageObject(0, Integer.MAX_VALUE,
                                  testContext.succeeding(page2 -> testContext.verify(() -> {

                                    final var model2 = Model.fromJsonObject(page2, WeNetUserProfilesPage.class);
                                    assertThat(model2).isNotNull();
                                    assertThat(model2.offset).isEqualTo(0);
                                    assertThat(model2.total).isEqualTo(model.total + 3);
                                    assertThat(model2.profiles).isNotEmpty().hasSize((int) model.total + 3)
                                        .isNotEqualTo(model.profiles).endsWith(profile, profile2, profile3);

                                    repository.retrieveProfilesPageObject((int) model.total, 2,
                                        testContext.succeeding(page3 -> testContext.verify(() -> {

                                          final var model3 = Model.fromJsonObject(page3, WeNetUserProfilesPage.class);
                                          assertThat(model3).isNotNull();
                                          assertThat(model3.offset).isEqualTo(model.total);
                                          assertThat(model3.total).isEqualTo(model.total + 3);
                                          assertThat(model3.profiles).isNotEmpty().hasSize(2).containsExactly(profile,
                                              profile2);
                                          testContext.completeNow();

                                        })));

                                  })));
                            });
                      });
                });
          });
        })));

  }

  /**
   * Should retrieve empty profiles page.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   */
  @Test
  public void shouldRetrieveEmptyProfilesPageObject(final Vertx vertx, final VertxTestContext testContext) {

    final var repository = ProfilesRepository.createProxy(vertx);
    repository.retrieveProfilesPageObject(Integer.MAX_VALUE, 100,
        testContext.succeeding(page -> testContext.verify(() -> {

          final var model = Model.fromJsonObject(page, WeNetUserProfilesPage.class);
          assertThat(model).isNotNull();
          assertThat(model.profiles).isNullOrEmpty();
          testContext.completeNow();

        })));
  }

}
