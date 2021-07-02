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

import eu.internetofus.common.model.TimeManager;
import eu.internetofus.common.vertx.Repository;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.AggregateOptions;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.tinylog.Logger;

/**
 * Implementation of the {@link TrustsRepository}.
 *
 * @see TrustsRepository
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class TrustsRepositoryImpl extends Repository implements TrustsRepository {

  /**
   * The name of the collection that contains the trusts.
   */
  public static final String TRUSTS_COLLECTION = "trusts";

  /**
   * The default value for n parameters used to
   * {@link TrustAggregator#RECENCY_BASED}.
   */
  public static final int DEFAULT_N = 5;

  /**
   * The n parameters to use in the {@link TrustAggregator#RECENCY_BASED}.
   *
   * @see #calculateRecencyBasedTrust(JsonObject, Handler)
   */
  protected int n;

  /**
   * Create a new repository.
   *
   * @param conf    configuration to use.
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public TrustsRepositoryImpl(final JsonObject conf, final MongoClient pool, final String version) {

    super(pool, version);
    this.n = conf.getJsonObject("TrustAggregator", new JsonObject()).getJsonObject("RECENCY_BASED", new JsonObject())
        .getInteger("n", DEFAULT_N);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeTrustEvent(final JsonObject event, final Handler<AsyncResult<JsonObject>> storeHandler) {

    final var now = TimeManager.now();
    event.put("reportTime", now);
    this.pool.save(TRUSTS_COLLECTION, event, store -> {

      if (store.failed()) {

        storeHandler.handle(Future.failedFuture(store.cause()));

      } else {

        event.remove("_id");
        storeHandler.handle(Future.succeededFuture(event));
      }

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateTrustBy(final TrustAggregator aggregator, final JsonObject query,
      final Handler<AsyncResult<Double>> trustHandler) {

    switch (aggregator) {

    case MAXIMUM:
      this.processAggregation(this.createMongoAggregationWith("max", query), trustHandler);
      break;
    case MINIMUM:
      this.processAggregation(this.createMongoAggregationWith("min", query), trustHandler);
      break;
    case AVERAGE:
      this.processAggregation(this.createMongoAggregationWith("avg", query), trustHandler);
      break;
    case MEDIAN:
      this.calculateMedianTrust(query, trustHandler);
      break;
    case RECENCY_BASED:
      this.calculateRecencyBasedTrust(query, trustHandler);
      break;
    default:
      trustHandler.handle(Future.failedFuture("The aggregation '" + aggregator + "' is not implemeted."));
    }
  }

  /**
   * Create the command to do the aggregation of the trust events.
   *
   * @param aggregator name of the mongoDB aggregation function to use.
   * @param query      for the events to aggregate.
   *
   * @return the aggregation command to execute.
   */
  protected JsonObject createMongoAggregationWith(final String aggregator, final JsonObject query) {

    final var pipeline = new JsonArray();
    pipeline.add(new JsonObject().put("$match", query));
    pipeline.add(new JsonObject().put("$group",
        new JsonObject().putNull("_id").put("trust", new JsonObject().put("$" + aggregator, "$rating"))));
    return new JsonObject().put("aggregate", TRUSTS_COLLECTION).put("pipeline", pipeline).put("cursor",
        new JsonObject().put("batchSize", AggregateOptions.DEFAULT_BATCH_SIZE));

  }

  /**
   * Execute an aggregation
   *
   * @param command      for the aggregation.
   * @param trustHandler handler to report the aggregation result.
   */
  private void processAggregation(final JsonObject command, final Handler<AsyncResult<Double>> trustHandler) {

    this.pool.runCommand("aggregate", command, aggregation -> {

      if (aggregation.failed()) {

        trustHandler.handle(Future.failedFuture(aggregation.cause()));

      } else {

        final JsonObject result = aggregation.result();
        try {

          final var cursor = result.getJsonObject("cursor");
          final var firstBatch = cursor.getJsonArray("firstBatch");
          final var batch = firstBatch.getJsonObject(0);
          final var trust = batch.getDouble("trust");
          trustHandler.handle(Future.succeededFuture(trust));

        } catch (final Throwable error) {

          Logger.trace(error, "The aggregation result {} is unexpected. May be no events match the query", result);
          trustHandler.handle(Future.failedFuture("No events match the query."));
        }
      }
    });

  }

  /**
   * Calculate the median trust.
   *
   * @param query        for the events to aggregate.
   * @param trustHandler handler of the calculated trust.
   */
  protected void calculateMedianTrust(final JsonObject query, final Handler<AsyncResult<Double>> trustHandler) {

    this.pool.count(TRUSTS_COLLECTION, query, counter -> {

      if (counter.failed()) {

        trustHandler.handle(Future.failedFuture(counter.cause()));

      } else {

        final long total = counter.result();
        if (total == 0) {

          trustHandler.handle(Future.failedFuture("No events match the query."));

        } else {
          final var options = new FindOptions();
          final var skip = (int) Math.round(total / 2.0 - 1);
          options.setSkip(skip);
          options.setLimit(1);
          options.setSort(new JsonObject().put("rating", 1));
          this.pool.findWithOptions(TRUSTS_COLLECTION, query, options, find -> {

            if (find.failed()) {

              trustHandler.handle(Future.failedFuture(find.cause()));

            } else {

              final var events = find.result();
              final double trust = events.get(0).getDouble("rating");
              trustHandler.handle(Future.succeededFuture(trust));
            }
          });
        }
      }

    });

  }

  /**
   * Calculate the recency based trust.
   *
   * @param query        for the events to aggregate.
   * @param trustHandler handler of the calculated trust.
   */
  protected void calculateRecencyBasedTrust(final JsonObject query, final Handler<AsyncResult<Double>> trustHandler) {

    final var pipeline = new JsonArray();
    pipeline.add(new JsonObject().put("$match", query));
    pipeline.add(new JsonObject().put("$sort", new JsonObject().put("reportTime", -1)));
    pipeline.add(new JsonObject().put("$limit", this.n));
    pipeline.add(new JsonObject().put("$group",
        new JsonObject().putNull("_id").put("trust", new JsonObject().put("$avg", "$rating"))));
    final var command = new JsonObject().put("aggregate", TRUSTS_COLLECTION).put("pipeline", pipeline).put("cursor",
        new JsonObject().put("batchSize", AggregateOptions.DEFAULT_BATCH_SIZE));
    this.processAggregation(command, trustHandler);
  }

  /**
   * Migrate the collections to the current version.
   *
   * @return the future that will inform if the migration is a success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    // No changes => only update schema information
    return this.updateSchemaVersionOnCollection(TRUSTS_COLLECTION);
  }

}
