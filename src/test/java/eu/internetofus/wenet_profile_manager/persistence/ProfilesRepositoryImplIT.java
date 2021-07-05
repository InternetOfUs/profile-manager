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

import eu.internetofus.common.components.Containers;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.vertx.Repository;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the integration of the {@link ProfilesRepositoryImpl}.
 *
 * @see ProfilesRepositoryImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesRepositoryImplIT {

  /**
   * Should migrate to the current version. This unit test is added because on the
   * migration process for the version < 0.20.0, a {@link StackOverflowError}
   * because the database has more that 10000 entries.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepositoryImpl#migrateDocumentsToCurrentVersions()
   */
  @Test
  @Timeout(value = 10, timeUnit = TimeUnit.MINUTES)
  @DisabledIfSystemProperty(named = "disable.large.unit.tests", matches = "true", disabledReason = "The integration test require around 10 minutes disabled.")
  public void shoudMigrateDocumentsToCurrentVersions(final Vertx vertx, final VertxTestContext testContext) {

    final var config = Containers.status().getMongoDBConfig();
    final var client = MongoClient.create(vertx, config);

    Future<List<WeNetUserProfile>> future = Future.succeededFuture(new ArrayList<>());
    future = future
        .compose(profiles -> new WeNetUserProfileTest().createModelExample(1, vertx, testContext).map(profile -> {

          profiles.add(profile);
          return profiles;
        }));

    future = future.compose(profiles -> {

      final Promise<Void> promise = Promise.promise();
      final var profile = profiles.remove(0);
      final var max = 1000;
      for (var i = 0; i < max; i++) {

        final var oldProfile = profile.toJsonObject().put(Repository.SCHEMA_VERSION, "0").put("extraValue", true)
            .put("extraField", new JsonObject().put("key", "1").put("index", i).put("isTrue", true)).put("norms",
                new JsonArray()
                    .add(new JsonObject().put("conditions", new JsonArray().add("condition1").add("condition2")))
                    .add(new JsonObject().put("actions", new JsonArray().add("action1").add("action2"))));
        client.insert(ProfilesRepositoryImpl.PROFILES_COLLECTION, oldProfile).onComplete(testContext.succeeding(id -> {

          final var storedProfile = Model.fromJsonObject(profile.toJsonObject(), WeNetUserProfile.class);
          storedProfile.id = id;
          profiles.add(storedProfile);
          if (profiles.size() >= max) {

            promise.complete();
          }

        }));
      }

      return promise.future().map(empty -> profiles);

    });

    future = future.compose(profiles -> {

      final Promise<String> promise = Promise.promise();
      @SuppressWarnings("rawtypes")
      final List<Future> storeFutures = new ArrayList<>();
      for (final WeNetUserProfile profile : profiles) {

        final var historic = new HistoricWeNetUserProfile();
        historic.from = 0;
        historic.to = 1;
        historic.profile = new WeNetUserProfile();
        final var historicDocument = historic.toJsonObject();
        storeFutures.add(client.insert(ProfilesRepositoryImpl.HISTORIC_PROFILES_COLLECTION, historicDocument.copy()));

        var from = 0;
        var to = 1;
        final var profileObject = profile.toJsonObject()
            .put("extraField", new JsonObject().put("key1", "1").put("key2", 2).put("key3", true)).put("norms",
                new JsonArray()
                    .add(new JsonObject().put("conditions", new JsonArray().add("condition1").add("condition2")))
                    .add(new JsonObject().put("actions", new JsonArray().add("action1").add("action2"))));
        for (final var key : profileObject.fieldNames()) {

          from = to;
          to += 1000;
          historicDocument.put("from", from);
          historicDocument.put("to", to);

          final var modifiedProfile = new JsonObject().put(key, profileObject.getValue(key));
          historicDocument.put("profile", modifiedProfile);
          storeFutures.add(client.insert(ProfilesRepositoryImpl.HISTORIC_PROFILES_COLLECTION, historicDocument.copy()));
        }

        from = to;
        to += 1000;
        historicDocument.put("from", from);
        historicDocument.put("to", to);
        historicDocument.put("profile", profileObject);
        storeFutures.add(client.insert(ProfilesRepositoryImpl.HISTORIC_PROFILES_COLLECTION, historicDocument.copy()));

      }

      CompositeFuture.all(storeFutures).onComplete(testContext.succeeding(results -> promise.complete()));
      return promise.future().map(empty -> profiles);
    });

    final var repository = new ProfilesRepositoryImpl(vertx, client, "1");
    future = future.compose(profiles -> {

      return repository.migrateDocumentsToCurrentVersions().map(empty -> {

        return profiles;
      });
    });

    future = future.compose(profiles -> {

      final Promise<Void> promise = Promise.promise();
      var checkFuture = promise.future();
      for (final var profile : profiles) {

        checkFuture = checkFuture.compose(empty -> repository.searchProfile(profile.id).compose(foundProfile -> {

          profile.norms.clear();
          profile.norms.add(new ProtocolNorm());
          profile.norms.add(new ProtocolNorm());
          testContext.verify(() -> assertThat(foundProfile).isEqualTo(profile));
          return Future.succeededFuture();
        }));

        checkFuture = checkFuture.compose(empty -> repository
            .searchHistoricProfilePage(new JsonObject().put("profile.id", profile.id), new JsonObject(), 0, 1000)
            .compose(foundPage -> {

              profile.norms.clear();
              profile.norms.add(new ProtocolNorm());
              profile.norms.add(new ProtocolNorm());
              testContext.verify(() -> {

                assertThat(foundPage).isNotNull();
                assertThat(foundPage.profiles).isNotNull().hasSize((int) foundPage.total);
                assertThat(foundPage.profiles.get((int) foundPage.total - 1).profile).isEqualTo(profile);

              });
              return Future.succeededFuture();
            }));

      }
      promise.complete();
      return checkFuture.map(empty -> profiles);
    });
    future.onComplete(testContext.succeeding(empty -> testContext.completeNow()));
  }

}
