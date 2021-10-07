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
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
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
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":null}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.4}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"1\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.4,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.4}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"value\":0.5}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"1\"}"));
      relationships.add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"friend\",\"value\":0.4,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + i + "\",\"type\":\"familiar\",\"value\":0.5,\"appId\":\"0\"}"));
      relationships
          .add(new JsonObject("{\"userId\":\"" + (i + 1) + "\",\"type\":\"friend\",\"value\":0.5,\"appId\":\"0\"}"));
      future = future.compose(any -> pool.insert(ProfilesRepositoryImpl.PROFILES_COLLECTION, profile).map(id -> {
        ids.add(id);
        return null;
      }));

    }
    future.compose(any -> repository.fixDuplicatedRelationships()).compose(any -> {

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

    }).compose(any -> repository.fixDuplicatedRelationships()).compose(any -> {

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

}
