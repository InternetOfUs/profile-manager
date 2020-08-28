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
import eu.internetofus.common.components.profile_manager.CommunityProfile;
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
 * The service to manage the {@link CommunityProfile} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface CommunitiesRepository {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.persistence.communities";

  /**
   * Create a proxy of the {@link CommunitiesRepository}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the community.
   */
  static CommunitiesRepository createProxy(final Vertx vertx) {

    return new CommunitiesRepositoryVertxEBProxy(vertx, CommunitiesRepository.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx that contains the event bus to use.
   * @param pool  to create the database connections.
   */
  static void register(final Vertx vertx, final MongoClient pool) {

    new ServiceBinder(vertx).setAddress(CommunitiesRepository.ADDRESS).register(CommunitiesRepository.class, new CommunitiesRepositoryImpl(pool));

  }

  /**
   * Search for the community with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  @GenIgnore
  default void searchCommunity(final String id, final Handler<AsyncResult<CommunityProfile>> searchHandler) {

    this.searchCommunityObject(id, search -> {

      if (search.failed()) {

        searchHandler.handle(Future.failedFuture(search.cause()));

      } else {

        final var value = search.result();
        final var community = Model.fromJsonObject(value, CommunityProfile.class);
        if (community == null) {

          searchHandler.handle(Future.failedFuture("The stored community is not valid."));

        } else {

          searchHandler.handle(Future.succeededFuture(community));
        }
      }
    });
  }

  /**
   * Search for the community with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  void searchCommunityObject(String id, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Store a community.
   *
   * @param community    to store.
   * @param storeHandler handler to manage the store.
   */
  @GenIgnore
  default void storeCommunity(final CommunityProfile community, final Handler<AsyncResult<CommunityProfile>> storeHandler) {

    final var object = community.toJsonObject();
    if (object == null) {

      storeHandler.handle(Future.failedFuture("The community can not converted to JSON."));

    } else {

      this.storeCommunity(object, stored -> {
        if (stored.failed()) {

          storeHandler.handle(Future.failedFuture(stored.cause()));

        } else {

          final var value = stored.result();
          final var storedCommunity = Model.fromJsonObject(value, CommunityProfile.class);
          if (storedCommunity == null) {

            storeHandler.handle(Future.failedFuture("The stored community is not valid."));

          } else {

            storeHandler.handle(Future.succeededFuture(storedCommunity));
          }

        }
      });
    }
  }

  /**
   * Store a community.
   *
   * @param community    to store.
   * @param storeHandler handler to manage the store.
   */
  void storeCommunity(JsonObject community, Handler<AsyncResult<JsonObject>> storeHandler);

  /**
   * Update a community.
   *
   * @param community     to update.
   * @param updateHandler handler to manage the update.
   */
  @GenIgnore
  default void updateCommunity(final CommunityProfile community, final Handler<AsyncResult<Void>> updateHandler) {

    final var object = community.toJsonObjectWithEmptyValues();
    if (object == null) {

      updateHandler.handle(Future.failedFuture("The community can not converted to JSON."));

    } else {

      this.updateCommunity(object, updateHandler);
    }

  }

  /**
   * Update a community.
   *
   * @param community     to update.
   * @param updateHandler handler to manage the update result.
   */
  void updateCommunity(JsonObject community, Handler<AsyncResult<Void>> updateHandler);

  /**
   * Delete a community.
   *
   * @param id            identifier of the user to delete.
   * @param deleteHandler handler to manage the delete result.
   */
  void deleteCommunity(String id, Handler<AsyncResult<Void>> deleteHandler);

}
