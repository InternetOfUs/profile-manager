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
import eu.internetofus.common.components.models.ProtocolNormTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link CommunitiesRepositoryImpl}.
 *
 * @see CommunitiesRepositoryImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class CommunitiesRepositoryImplIT {

  /**
   * Start a mongo container.
   */
  @BeforeAll
  public static void startMongoContainer() {

    Containers.status().startMongoContainer();
  }

  /**
   * Verify the conversion of the task to the O.6.0.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepositoryImpl#migrateComunitiesWithoutNorms_with_API_0_16_0_OR_LESS()
   */
  @Test
  public void shouldMigrateTo_0_16_0(final Vertx vertx, final VertxTestContext testContext) {

    final var pool = MongoClient.createShared(vertx, Containers.status().getMongoDBConfig(), "TEST");

    final var norm = new ProtocolNormTest().createModelExample(16).toJsonObject();
    final var modelsToMigrate = new JsonObject[] { new JsonObject(),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.1"),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.0"),
        new JsonObject().put("norms", "unexpected"),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.1").put("norms", "unexpected"),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.0").put("norms", "unexpected"),
        new JsonObject().put("norms", new JsonArray()),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.1").put("norms", new JsonArray()),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.0").put("norms", new JsonArray()) };

    final var modelsToNotMigrate = new JsonObject[] {
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.1"),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.1").put("norms", "unexpected"),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.1").put("norms", new JsonArray()),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.1").put("norms",
            new JsonArray().add(norm)),
        new JsonObject().put("norms", new JsonArray().add(norm)),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.1").put("norms", new JsonArray().add(norm)),
        new JsonObject().put(CommunitiesRepositoryImpl.SCHEMA_VERSION, "0.16.0").put("norms",
            new JsonArray().add(norm)) };

    Future<Void> future = Future.succeededFuture();
    final var ids = new JsonArray();
    final var migrateIds = new ArrayList<String>();
    for (var i = 0; i < 10; i++) {

      for (final var model : modelsToMigrate) {

        future = future.compose(empty -> {
          model.remove("_id");
          return pool.insert(CommunitiesRepositoryImpl.COMMUNITIES_COLLECTION, model).map(id -> {

            migrateIds.add(id);
            ids.add(id);
            return null;

          });
        });

      }

      for (final var model : modelsToNotMigrate) {

        future = future.compose(empty -> {
          model.remove("_id");
          return pool.insert(CommunitiesRepositoryImpl.COMMUNITIES_COLLECTION, model).map(id -> {

            ids.add(id);
            return null;

          });
        });

      }
    }

    final var repository = new CommunitiesRepositoryImpl(vertx, pool, "0.16.0");
    future = future.compose(empty -> repository.migrateComunitiesWithoutNorms_with_API_0_16_0_OR_LESS());
    future
        .compose(empty -> pool.find(CommunitiesRepositoryImpl.COMMUNITIES_COLLECTION,
            new JsonObject().put("_id", new JsonObject().put("$in", ids))))
        .onComplete(testContext.succeeding(models -> testContext.verify(() -> {

          final var expectedNorms = repository.defaultNormsForCommunitiesWitghoutNormsAndSchema_0_16_0_or_less();
          for (final var model : models) {

            final var id = model.getString("_id");
            if (migrateIds.contains(id)) {

              assertThat(model.getJsonArray("norms")).isEqualTo(expectedNorms);

            } else {

              var migrated = true;
              for (final var source : modelsToNotMigrate) {

                source.put("_id", id);
                if (source.equals(model)) {

                  migrated = false;
                  break;
                }

              }

              assertThat(migrated).describedAs("Unecpected migarted model {0}", model).isEqualTo(false);

            }

          }

          testContext.completeNow();
        })));

  }
}
