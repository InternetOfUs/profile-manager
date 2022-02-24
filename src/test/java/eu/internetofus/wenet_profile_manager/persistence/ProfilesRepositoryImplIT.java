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
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import eu.internetofus.common.vertx.Repository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the integration with a database of {@link ProfilesRepositoryImpl}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProfilesRepositoryImplIT {

  /**
   * Should fix duplicated relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   */
  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.MINUTES)
  public void shouldFixDuplicatedRelationships(final Vertx vertx, final VertxTestContext testContext) {

    Containers.status().startMongoContainer();
    final var persitenceConf = Containers.status().getMongoDBConfig();
    final var pool = MongoClient.createShared(vertx, persitenceConf, "shouldMigrateTo0_17_0");
    final var repository = new ProfilesRepositoryImpl(vertx, pool, "0.17.0");
    Future<?> future = Future.succeededFuture();
    final List<String> ids = new ArrayList<>();
    for (var i = 0; i < 1000; i++) {

      final var profile = new WeNetUserProfileTest().createModelExample(i).toJsonObject();
      profile.put(Repository.SCHEMA_VERSION, "0.16.0");
      final var relationships = new JsonArray();
      profile.put("relationships", relationships);
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":null}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.4}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"1\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.4,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.4}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"1\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.4,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"weight\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"weight\":0.5,\"appId\":\"0\"}"));
      future = future.compose(any -> pool.insert(ProfilesRepositoryImpl.PROFILES_COLLECTION, profile).map(id -> {
        ids.add(id);
        return null;
      }));

    }
    future.compose(any -> repository.migrateFixingDuplicatedRelationships()).compose(any -> {

      final Promise<?> promise = Promise.promise();
      var updateFuture = promise.future();
      for (final var id : ids) {

        updateFuture = updateFuture.compose(any2 -> pool
            .findOne(ProfilesRepositoryImpl.PROFILES_COLLECTION, new JsonObject().put("_id", id), new JsonObject())
            .map(found -> {

              testContext.verify(() -> {

                final var relations = found.getJsonArray("relationships", null);
                assertThat(relations).isNotNull();
                assertThat(relations.size()).isEqualTo(4);

              });
              return null;
            }));

      }

      promise.tryComplete();
      return updateFuture;

    }).compose(any -> repository.migrateFixingDuplicatedRelationships()).compose(any -> {

      final Promise<?> promise = Promise.promise();
      var updateFuture = promise.future();
      for (final var id : ids) {

        updateFuture = updateFuture.compose(any2 -> pool
            .findOne(ProfilesRepositoryImpl.PROFILES_COLLECTION, new JsonObject().put("_id", id), new JsonObject())
            .map(found -> {

              testContext.verify(() -> {

                final var relations = found.getJsonArray("relationships", null);
                assertThat(relations).isNotNull();
                assertThat(relations.size()).isEqualTo(4);

              });
              return null;
            }));

      }

      promise.tryComplete();
      return updateFuture;

    }).onComplete(testContext.succeeding(any -> testContext.completeNow()));

  }

  /**
   * Should delete the profile relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepositoryImpl#migrateProfileRelationshipsToItsOwnCollection()
   */
  @Test
  public void shouldMigrateProfileRelationshipsToItsOwnCollection(final Vertx vertx,
      final VertxTestContext testContext) {

    Containers.status().startMongoContainer();
    final var persitenceConf = Containers.status().getMongoDBConfig();
    final var pool = MongoClient.createShared(vertx, persitenceConf, "shouldDeleteProfileRelationshipsTo0_1_0");
    final var repository = new ProfilesRepositoryImpl(vertx, pool, "1.0.0");
    @SuppressWarnings("rawtypes")
    final List<Future> inserts = new ArrayList<>();
    final List<JsonObject> expectedRelationships = new ArrayList<>();
    final var friendId = UUID.randomUUID().toString();
    final var appId = UUID.randomUUID().toString();
    for (var i = 0; i < 50; i++) {

      final var profile = new JsonObject();
      profile.put(Repository.SCHEMA_VERSION, "0.17.0");
      if (i % 5 > 0) {

        final var relationships = new JsonArray();
        profile.put("relationships", relationships);

        if (i % 5 > 2) {

          relationships.add(new JsonObject(
              "{\"appId\":\"" + appId + "\",\"userId\":\"" + friendId + "\",\"type\":\"friend\",\"weight\":0.5}"));
          if (i % 5 > 3) {

            relationships.add(new JsonObject(
                "{\"appId\":\"" + appId + "\",\"userId\":\"" + friendId + "\",\"type\":\"colleague\",\"weight\":0.1}"));
            if (i % 5 > 4) {

              relationships.add(new JsonObject("{\"appId\":\"" + UUID.randomUUID().toString() + "\",\"userId\":\""
                  + friendId + "\",\"type\":\"colleague\",\"weight\":0.1}"));
              relationships.add(new JsonObject("{\"appId\":\"" + appId + "\",\"userId\":\""
                  + UUID.randomUUID().toString() + "\",\"type\":\"colleague\",\"weight\":0.1}"));
              relationships.add(new JsonObject("{\"appId\":\"" + UUID.randomUUID().toString() + "\",\"userId\":\""
                  + UUID.randomUUID().toString() + "\",\"type\":\"colleague\",\"weight\":0.1}"));

            }
          }
        }
        inserts.add(pool.insert(ProfilesRepositoryImpl.PROFILES_COLLECTION, profile).map(id -> {

          final var max = relationships.size();
          for (var j = 0; j < max; j++) {

            final var oldRelationship = relationships.getJsonObject(j);
            final var relationship = new JsonObject();
            relationship.put("appId", oldRelationship.getString("appId"));
            relationship.put("sourceId", oldRelationship.getString(id));
            relationship.put("target", oldRelationship.getString("userId"));
            relationship.put("type", oldRelationship.getString("type"));
            relationship.put("weight", oldRelationship.getNumber("weight"));
            expectedRelationships.add(relationship);
          }
          return null;
        }));
      }
    }

    testContext.assertComplete(CompositeFuture.all(inserts)).onSuccess(any -> {

      final var options = new FindOptions();
      options.setLimit(10);
      testContext.assertComplete(repository.migrateProfileRelationshipsToItsOwnCollection())
          .compose(migrated -> pool.findWithOptions(ProfilesRepositoryImpl.PROFILES_COLLECTION,
              new JsonObject().put(null, expectedRelationships), options))
          .onComplete(find -> {

            testContext.verify(() -> assertThat(find.result()).isNullOrEmpty());
            @SuppressWarnings("rawtypes")
            final List<Future> findRelations = new ArrayList<>();
            for (final var expectedRelationship : expectedRelationships) {

              findRelations
                  .add(testContext.assertComplete(pool.findOne(RelationshipsRepositoryImpl.RELATIONSHIPS_COLLECTION,
                      expectedRelationship, new JsonObject())));

            }
            CompositeFuture.all(findRelations).onSuccess(found -> testContext.completeNow());
          });

    });
  }

  /**
   * Should delete the relationships on historic profiles and remove duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see ProfilesRepositoryImpl#migrateDeletingRelationshipsOnHistoricProfiles()
   */
  @Test
  public void shouldMigrateDeletingRelationshipsOnHistoricProfiles(final Vertx vertx,
      final VertxTestContext testContext) {

    Containers.status().startMongoContainer();
    final var persitenceConf = Containers.status().getMongoDBConfig();
    final var pool = MongoClient.createShared(vertx, persitenceConf, "shouldDeleteProfileRelationshipsTo0_1_0");
    final var repository = new ProfilesRepositoryImpl(vertx, pool, "1.0.0");
    var future = Future.succeededFuture();
    final var oddId = UUID.randomUUID().toString();
    final var eventId = UUID.randomUUID().toString();
    final List<String> ids = new ArrayList<>();
    for (var i = 0; i < 10; i++) {

      final var historicProfile = new JsonObject();
      historicProfile.put(Repository.SCHEMA_VERSION, "0.17.0");
      historicProfile.put("from", i);
      historicProfile.put("to", i + 1);
      final var profile = new JsonObject();
      historicProfile.put("profile", profile);
      if (i % 2 == 0) {

        profile.put("id", eventId);
        profile.put("name", new JsonObject().put("first", "Jon").put("last", "Doe"));

      } else {

        profile.put("id", oddId);
        profile.put("name", new JsonObject().put("first", "Jane").put("last", "Doe"));
      }
      final var relationships = new JsonArray();
      profile.put("relationships", relationships);
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"weight\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"weight\":0.5}"));
      future = future
          .compose(any -> pool.insert(ProfilesRepositoryImpl.HISTORIC_PROFILES_COLLECTION, historicProfile).map(id -> {
            ids.add(id);
            return null;
          }));

    }
    final var options = new FindOptions();
    options.setLimit(10);
    testContext
        .assertComplete(future.compose(any -> repository.migrateDeletingRelationshipsOnHistoricProfiles())
            .compose(any -> pool.findWithOptions(ProfilesRepositoryImpl.HISTORIC_PROFILES_COLLECTION,
                new JsonObject().put("profile.id",
                    new JsonObject().put("$in", new JsonArray().add(eventId).add(oddId))),
                options)))
        .onSuccess(profiles -> {

          testContext.verify(() -> {

            assertThat(profiles).hasSize(2);
            JsonObject eventHistoric = null;
            JsonObject oddHistoric = null;
            if (profiles.get(0).getNumber("from").intValue() == 0) {

              eventHistoric = profiles.get(0);
              oddHistoric = profiles.get(1);

            } else {

              eventHistoric = profiles.get(1);
              oddHistoric = profiles.get(0);
            }

            assertThat(eventHistoric.getNumber("from").intValue()).isEqualTo(0);
            assertThat(eventHistoric.getNumber("to").intValue()).isEqualTo(1);
            assertThat(eventHistoric.getJsonObject("profile")).isEqualTo(new JsonObject().put("id", eventId).put("name",
                new JsonObject().put("first", "Jon").put("last", "Doe")));

            assertThat(oddHistoric.getNumber("from").intValue()).isEqualTo(1);
            assertThat(oddHistoric.getNumber("to").intValue()).isEqualTo(2);
            assertThat(oddHistoric.getJsonObject("profile")).isEqualTo(new JsonObject().put("id", oddId).put("name",
                new JsonObject().put("first", "Jane").put("last", "Doe")));

          });
          testContext.completeNow();

        });

  }

}
