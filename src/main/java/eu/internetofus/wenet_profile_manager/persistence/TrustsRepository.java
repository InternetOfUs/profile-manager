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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import eu.internetofus.common.components.Model;
import eu.internetofus.wenet_profile_manager.api.trusts.Trust;
import eu.internetofus.wenet_profile_manager.api.trusts.UserPerformanceRatingEvent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;

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
   * @param vertx that contains the event bus to use.
   * @param pool  to create the database connections.
   */
  static void register(final Vertx vertx, final MongoClient pool) {

    new ServiceBinder(vertx).setAddress(TrustsRepository.ADDRESS).register(TrustsRepository.class, new TrustsRepositoryImpl(pool));

  }

  /**
   * Store a trust event.
   *
   * @param event        of trust to store.
   * @param storeHandler handler to manage the store.
   */
  @GenIgnore
  default void storeTrustEvent(final UserPerformanceRatingEvent event, final Handler<AsyncResult<UserPerformanceRatingEvent>> storeHandler) {

    final JsonObject object = event.toJsonObject();
    if (object == null) {

      storeHandler.handle(Future.failedFuture("The event can not converted to JSON."));

    } else {

      this.storeTrustEvent(object, stored -> {
        if (stored.failed()) {

          storeHandler.handle(Future.failedFuture(stored.cause()));

        } else {

          final JsonObject value = stored.result();
          final UserPerformanceRatingEvent storedEvent = Model.fromJsonObject(value, UserPerformanceRatingEvent.class);
          if (storedEvent == null) {

            storeHandler.handle(Future.failedFuture("The stored event is not valid."));

          } else {

            storeHandler.handle(Future.succeededFuture(storedEvent));
          }

        }
      });
    }
  }

  /**
   * Store a trust event document.
   *
   * @param event        of trust to store.
   * @param storeHandler handler to manage the store.
   */
  void storeTrustEvent(JsonObject event, Handler<AsyncResult<JsonObject>> storeHandler);
}
