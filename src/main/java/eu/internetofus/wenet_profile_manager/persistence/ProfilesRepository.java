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

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.vertx.QueryBuilder;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfilesPage;
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
 * The service to manage the {@link WeNetUserProfile} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface ProfilesRepository {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.persistence.profiles";

  /**
   * Create a proxy of the {@link ProfilesRepository}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the profile.
   */
  static ProfilesRepository createProxy(final Vertx vertx) {

    return new ProfilesRepositoryVertxEBProxy(vertx, ProfilesRepository.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx   that contains the event bus to use.
   * @param pool    to create the database connections.
   * @param version of the schemas.
   *
   * @return the future that inform when the repository will be registered or not.
   */
  static Future<Void> register(final Vertx vertx, final MongoClient pool, final String version) {

    final var repository = new ProfilesRepositoryImpl(pool, version);
    new ServiceBinder(vertx).setAddress(ProfilesRepository.ADDRESS).register(ProfilesRepository.class, repository);
    return repository.migrateDocumentsToCurrentVersions();

  }

  /**
   * Search for the profile with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  @GenIgnore
  default void searchProfile(final String id, final Handler<AsyncResult<WeNetUserProfile>> searchHandler) {

    this.searchProfileObject(id, search -> {

      if (search.failed()) {

        searchHandler.handle(Future.failedFuture(search.cause()));

      } else {

        final var value = search.result();
        final var profile = Model.fromJsonObject(value, WeNetUserProfile.class);
        if (profile == null) {

          searchHandler.handle(Future.failedFuture("The stored profile is not valid."));

        } else {

          searchHandler.handle(Future.succeededFuture(profile));
        }
      }
    });
  }

  /**
   * Search for the profile with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  void searchProfileObject(String id, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Store a profile.
   *
   * @param profile      to store.
   * @param storeHandler handler to manage the store.
   */
  @GenIgnore
  default void storeProfile(final WeNetUserProfile profile, final Handler<AsyncResult<WeNetUserProfile>> storeHandler) {

    final var object = profile.toJsonObject();
    if (object == null) {

      storeHandler.handle(Future.failedFuture("The profile can not converted to JSON."));

    } else {

      this.storeProfile(object, stored -> {
        if (stored.failed()) {

          storeHandler.handle(Future.failedFuture(stored.cause()));

        } else {

          final var value = stored.result();
          final var storedProfile = Model.fromJsonObject(value, WeNetUserProfile.class);
          if (storedProfile == null) {

            storeHandler.handle(Future.failedFuture("The stored profile is not valid."));

          } else {

            storeHandler.handle(Future.succeededFuture(storedProfile));
          }

        }
      });
    }
  }

  /**
   * Store a profile.
   *
   * @param profile      to store.
   * @param storeHandler handler to manage the store.
   */
  void storeProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler);

  /**
   * Update a profile.
   *
   * @param profile       to update.
   * @param updateHandler handler to manage the update.
   */
  @GenIgnore
  default void updateProfile(final WeNetUserProfile profile, final Handler<AsyncResult<Void>> updateHandler) {

    final var object = profile.toJsonObjectWithEmptyValues();
    if (object == null) {

      updateHandler.handle(Future.failedFuture("The profile can not converted to JSON."));

    } else {

      this.updateProfile(object, updateHandler);
    }

  }

  /**
   * Update a profile.
   *
   * @param profile       to update.
   * @param updateHandler handler to manage the update result.
   */
  void updateProfile(JsonObject profile, Handler<AsyncResult<Void>> updateHandler);

  /**
   * Delete a profile.
   *
   * @param id            identifier of the user to delete.
   * @param deleteHandler handler to manage the delete result.
   */
  void deleteProfile(String id, Handler<AsyncResult<Void>> deleteHandler);

  /**
   * Store a historic profile.
   *
   * @param profile      to store.
   * @param storeHandler handler to manage the store.
   */
  @GenIgnore
  default void storeHistoricProfile(final HistoricWeNetUserProfile profile, final Handler<AsyncResult<HistoricWeNetUserProfile>> storeHandler) {

    final var object = profile.toJsonObject();
    if (object == null) {

      storeHandler.handle(Future.failedFuture("The profile can not converted to JSON."));

    } else {

      this.storeHistoricProfile(object, stored -> {
        if (stored.failed()) {

          storeHandler.handle(Future.failedFuture(stored.cause()));

        } else {

          final var value = stored.result();
          value.remove("_id");
          final var storedProfile = Model.fromJsonObject(value, HistoricWeNetUserProfile.class);
          if (storedProfile == null) {

            storeHandler.handle(Future.failedFuture("The stored profile is not valid."));

          } else {

            storeHandler.handle(Future.succeededFuture(storedProfile));
          }

        }
      });
    }
  }

  /**
   * Store a historic profile.
   *
   * @param profile      to store.
   * @param storeHandler handler to manage the search.
   */
  void storeHistoricProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler);

  /**
   * Search for some historic profiles.
   *
   *
   * @param query         that define the historic profiles to return.
   * @param sort          define the order in with the historic profiles has to be returned.
   * @param offset        index of the first profile to return.
   * @param limit         number maximum of profiles to return.
   * @param searchHandler handler to manage the search.
   */
  @GenIgnore
  default void searchHistoricProfilePage(final JsonObject query, final JsonObject sort, final int offset, final int limit, final Handler<AsyncResult<HistoricWeNetUserProfilesPage>> searchHandler) {

    this.searchHistoricProfilePageObject(query, sort, offset, limit, search -> {

      if (search.failed()) {

        searchHandler.handle(Future.failedFuture(search.cause()));

      } else {

        final var value = search.result();
        final var page = Model.fromJsonObject(value, HistoricWeNetUserProfilesPage.class);
        if (page == null) {

          searchHandler.handle(Future.failedFuture("The stored page is not valid."));

        } else {

          searchHandler.handle(Future.succeededFuture(page));
        }
      }
    });

  }

  /**
   * Search for some historic profiles.
   *
   *
   * @param query         that define the historic profiles to return.
   * @param sort          define the order in with the historic profiles has to be returned.
   * @param offset        index of the first profile to return.
   * @param limit         number maximum of profiles to return.
   * @param searchHandler handler to manage the search.
   */
  void searchHistoricProfilePageObject(JsonObject query, JsonObject sort, int offset, int limit, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Create the query to obtain the historic pages that match the specified parameters.
   *
   * @param userId identifier of the user to get the historic values.
   * @param from   the minimum time stamp that define the range the profile is active.
   * @param to     the maximum time stamp that define the range the profile is active.
   *
   * @return the query to obtain the page with the profiles with the specified parameters.
   */
  static JsonObject createProfileHistoricPageQuery(final String userId, final Long from, final Long to) {

    return new QueryBuilder().with("profile.id", userId).withRange("from", from, null).withRange("to", null, to).build();
  }

  /**
   * Create the sort to obtain the historic profiles.
   *
   * @param order in with the profiles has to returned.
   *
   * @return the object that define the profiles order.
   */
  static JsonObject createProfileHistoricPageSort(final String order) {

    return new JsonObject().put("from", Integer.parseInt(order + "1"));
  }

  /**
   * Retrieve a page with some user identifiers.
   *
   * @param offset        to the first user identifier to return.
   * @param limit         the number maximum of identifiers to return.
   * @param searchHandler handler to manage the search.
   */
  void retrieveProfileUserIdsPageObject(int offset, int limit, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Retrieve a page with some profiles.
   *
   * @param offset        to the first profile to return.
   * @param limit         the number maximum of profiles to return.
   * @param searchHandler handler to manage the search.
   */
  void retrieveProfilesPageObject(int offset, int limit, Handler<AsyncResult<JsonObject>> searchHandler);

}
