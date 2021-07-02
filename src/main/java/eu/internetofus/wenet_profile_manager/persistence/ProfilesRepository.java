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
import eu.internetofus.common.model.Model;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.vertx.QueryBuilder;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfilesPage;
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
   * @param id identifier of the user to search.
   *
   * @return the future found profile.
   */
  @GenIgnore
  default Future<WeNetUserProfile> searchProfile(final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.searchProfile(id, promise);
    return Model.fromFutureJsonObject(promise.future(), WeNetUserProfile.class);

  }

  /**
   * Search for the profile with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  void searchProfile(String id, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Store a profile.
   *
   * @param profile to store.
   *
   * @return the future stored profile.
   */
  @GenIgnore
  default Future<WeNetUserProfile> storeProfile(final WeNetUserProfile profile) {

    final Promise<JsonObject> promise = Promise.promise();
    profile._creationTs = profile._lastUpdateTs = TimeManager.now();
    final var object = profile.toJsonObject();
    if (object == null) {

      promise.fail("The profile can not converted to JSON.");

    } else {

      this.storeProfile(object, promise);
    }

    return Model.fromFutureJsonObject(promise.future(), WeNetUserProfile.class);
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
   * @param profile to update.
   *
   * @return the future that inform when the profile is updated.
   */
  @GenIgnore
  default Future<Void> updateProfile(final WeNetUserProfile profile) {

    final Promise<Void> promise = Promise.promise();
    profile._lastUpdateTs = TimeManager.now();
    final var object = profile.toJsonObjectWithEmptyValues();
    if (object == null) {

      promise.fail("The profile can not converted to JSON.");

    } else {

      this.updateProfile(object, promise);
    }

    return promise.future();

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
   * Delete a profile.
   *
   * @param id identifier of the user to delete.
   *
   * @return the future that inform when the profile is removed.
   */
  @GenIgnore
  default Future<Void> deleteProfile(final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteProfile(id, promise);
    return promise.future();
  }

  /**
   * Store a historic profile.
   *
   * @param profile to store.
   *
   * @return the future stored profile.
   */
  @GenIgnore
  default Future<HistoricWeNetUserProfile> storeHistoricProfile(final HistoricWeNetUserProfile profile) {

    final Promise<JsonObject> promise = Promise.promise();
    final var object = profile.toJsonObject();
    if (object == null) {

      return Future.failedFuture("The profile can not converted to JSON.");

    } else {

      this.storeHistoricProfile(object, promise);

    }

    return Model.fromFutureJsonObject(promise.future(), HistoricWeNetUserProfile.class);
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
   * @param query  that define the historic profiles to return.
   * @param sort   define the order in with the historic profiles has to be
   *               returned.
   * @param offset index of the first profile to return.
   * @param limit  number maximum of profiles to return.
   *
   * @return the future with the found page.
   */
  @GenIgnore
  default Future<HistoricWeNetUserProfilesPage> searchHistoricProfilePage(final JsonObject query, final JsonObject sort,
      final int offset, final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.searchHistoricProfilePageObject(query, sort, offset, limit, promise);
    return Model.fromFutureJsonObject(promise.future(), HistoricWeNetUserProfilesPage.class);

  }

  /**
   * Search for some historic profiles.
   *
   *
   * @param query         that define the historic profiles to return.
   * @param sort          define the order in with the historic profiles has to be
   *                      returned.
   * @param offset        index of the first profile to return.
   * @param limit         number maximum of profiles to return.
   * @param searchHandler handler to manage the search.
   */
  void searchHistoricProfilePageObject(JsonObject query, JsonObject sort, int offset, int limit,
      Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Create the query to obtain the historic pages that match the specified
   * parameters.
   *
   * @param userId identifier of the user to get the historic values.
   * @param from   the minimum time stamp that define the range the profile is
   *               active.
   * @param to     the maximum time stamp that define the range the profile is
   *               active.
   *
   * @return the query to obtain the page with the profiles with the specified
   *         parameters.
   */
  static JsonObject createProfileHistoricPageQuery(final String userId, final Long from, final Long to) {

    return new QueryBuilder().with("profile.id", userId).withRange("from", from, null).withRange("to", null, to)
        .build();
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
