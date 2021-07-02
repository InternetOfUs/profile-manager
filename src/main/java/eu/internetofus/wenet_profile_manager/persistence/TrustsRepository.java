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

import eu.internetofus.common.model.Model;
import eu.internetofus.wenet_profile_manager.api.trusts.Trust;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustAggregator;
import eu.internetofus.wenet_profile_manager.api.trusts.UserPerformanceRatingEvent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import javax.validation.constraints.NotNull;

/**
 * The service to manage the {@link Trust} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface TrustsRepository {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.persistence.trusts";

  /**
   * Create a proxy of the {@link TrustsRepository}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the trust.
   */
  static TrustsRepository createProxy(final Vertx vertx) {

    return new TrustsRepositoryVertxEBProxy(vertx, TrustsRepository.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx   that contains the event bus to use.
   * @param conf    configuration to use.
   * @param pool    to create the database connections.
   * @param version of the schemas.
   *
   * @return the future that inform when the repository will be registered or not.
   */
  static Future<Void> register(final Vertx vertx, final JsonObject conf, final MongoClient pool, final String version) {

    final var repository = new TrustsRepositoryImpl(conf, pool, version);
    new ServiceBinder(vertx).setAddress(TrustsRepository.ADDRESS).register(TrustsRepository.class, repository);
    return repository.migrateDocumentsToCurrentVersions();

  }

  /**
   * Store a trust event.
   *
   * @param event of trust to store.
   *
   * @return the future stored event.
   */
  @GenIgnore
  default Future<UserPerformanceRatingEvent> storeTrustEvent(@NotNull final UserPerformanceRatingEvent event) {

    final Promise<JsonObject> promise = Promise.promise();
    this.storeTrustEvent(event.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), UserPerformanceRatingEvent.class);

  }

  /**
   * Store a trust event document.
   *
   * @param event        of trust to store.
   * @param storeHandler handler to manage the store.
   */
  void storeTrustEvent(JsonObject event, Handler<AsyncResult<JsonObject>> storeHandler);

  /**
   * Calculate the trust using the specified trust and the events that matches the
   * query.
   *
   * @param aggregator to use.
   * @param query      that has to match the vents.
   *
   * @return the future calculated trust.
   */
  @GenIgnore
  default Future<Double> calculateTrustBy(final TrustAggregator aggregator, final JsonObject query) {

    final Promise<Double> promise = Promise.promise();
    this.calculateTrustBy(aggregator, query, promise);
    return promise.future();

  }

  /**
   * Calculate the trust using the specified trust and the events that matches the
   * query.
   *
   * @param aggregator   to use.
   * @param query        that has to match the vents.
   * @param trustHandler handler to manage the calculated trust.
   */
  void calculateTrustBy(TrustAggregator aggregator, JsonObject query, Handler<AsyncResult<Double>> trustHandler);
}
