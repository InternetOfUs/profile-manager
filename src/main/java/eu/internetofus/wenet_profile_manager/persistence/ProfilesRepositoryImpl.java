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

import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.vertx.Repository;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

/**
 * Implementation of the {@link ProfilesRepository}.
 *
 * @see ProfilesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesRepositoryImpl extends Repository implements ProfilesRepository {

  /**
   * The name of the collection that contains the profiles.
   */
  public static final String PROFILES_COLLECTION = "profiles";

  /**
   * The name of the collection that contains the historic profiles.
   */
  public static final String HISTORIC_PROFILES_COLLECTION = "historicProfiles";

  /**
   * Create a new repository.
   *
   * @param vertx   event bus to use.
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public ProfilesRepositoryImpl(final Vertx vertx, final MongoClient pool, final String version) {

    super(vertx, pool, version);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchProfile(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var query = new JsonObject().put("_id", id);
    this.findOneDocument(PROFILES_COLLECTION, query, null, found -> {
      final var _id = (String) found.remove("_id");
      return found.put("id", _id);
    }).onComplete(searchHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

    final var id = (String) profile.remove("id");
    if (id != null) {

      profile.put("_id", id);
    }
    this.storeOneDocument(PROFILES_COLLECTION, profile, stored -> {

      final var _id = (String) stored.remove("_id");
      return stored.put("id", _id);

    }).onComplete(storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(final JsonObject profile, final Handler<AsyncResult<Void>> updateHandler) {

    final var id = profile.remove("id");
    final var query = new JsonObject().put("_id", id);
    this.updateOneDocument(PROFILES_COLLECTION, query, profile).onComplete(updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    final var query = new JsonObject().put("_id", id);
    this.deleteOneDocument(PROFILES_COLLECTION, query).onComplete(deleteHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeHistoricProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

    this.storeOneDocument(HISTORIC_PROFILES_COLLECTION, profile, value -> {
      value.remove("_id");
      return value;
    }).onComplete(storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchHistoricProfilePageObject(final JsonObject query, final JsonObject sort, final int offset,
      final int limit, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var options = new FindOptions();
    options.setSkip(offset);
    options.setLimit(limit);
    options.getFields().put("_id", false);
    options.setSort(sort);
    this.searchPageObject(HISTORIC_PROFILES_COLLECTION, query, options, "profiles", value -> value.remove("_id"))
        .onComplete(searchHandler);

  }

  /**
   * Migrate the collections to the current version.
   *
   * @return the future that will inform if the migration is a success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    return this.fixDuplicatedRelationships()
        .compose(empty -> this.migrateCollection(PROFILES_COLLECTION, WeNetUserProfile.class))
        .compose(empty -> this.migrateCollection(HISTORIC_PROFILES_COLLECTION, HistoricWeNetUserProfile.class));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileUserIdsPageObject(final int offset, final int limit,
      final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var options = new FindOptions();
    options.setFields(new JsonObject().put("_id", true));
    options.setSort(new JsonObject().put("_creationTs", 1).put("_id", 1));
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(PROFILES_COLLECTION, new JsonObject(), options, "profiles", null).compose(page -> {

      final var userIds = new JsonArray();
      page.put("userIds", userIds);
      final var profiles = (JsonArray) page.remove("profiles");
      if (profiles != null) {

        for (var i = 0; i < profiles.size(); i++) {

          final var profile = profiles.getJsonObject(i);
          final var id = profile.getString("_id");
          userIds.add(id);

        }
      }
      return Future.succeededFuture(page);

    }).onComplete(searchHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfilesPageObject(final int offset, final int limit,
      final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var options = new FindOptions();
    options.setSort(new JsonObject().put("_creationTs", 1).put("_id", 1));
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(PROFILES_COLLECTION, new JsonObject(), options, "profiles",
        profile -> profile.put("id", profile.remove("_id"))).onComplete(searchHandler);

  }

  /**
   * Fix the duplicated social relationships.
   *
   * @return the future with the update result.
   */
  protected Future<Void> fixDuplicatedRelationships() {

    final var query = new JsonObject().put("$and", new JsonArray().add(new JsonObject(
        "{\"$nor\":[{\"relationships\":{\"$exists\":false}},{\"relationships\":{\"$size\":0}},{\"relationships\":{\"$size\":1}}]}"))
        .add(this.createQueryToReturnDocumentsWithAVersionLessThan("0.7.0")));
    final var update = new JsonArray(
        "[{\"$set\":{\"relationships\":{\"$reduce\":{\"input\":{\"$filter\":{\"input\":\"$relationships\",\"as\":\"relation\",\"cond\":{\"$and\":[{\"$ne\":[\"$$relation.appId\",null]},{\"$ne\":[{\"$type\":\"$$relation.appId\"},\"missing\"]}]}}},\"initialValue\":[],\"in\":{\"$concatArrays\":[\"$$value\",{\"$cond\":[{\"$and\":[{\"$in\":[\"$$this.appId\",\"$$value.appId\"]},{\"$in\":[\"$$this.userId\",\"$$value.userId\"]},{\"$in\":[\"$$this.type\",\"$$value.type\"]}]},[],[\"$$this\"]]}]}}}}}]");
    final var options = new UpdateOptions();
    options.setMulti(true);
    return this.pool.updateCollectionWithOptions(PROFILES_COLLECTION, query, update, options).map(any -> null);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isProfileDefined(final String id, final Handler<AsyncResult<Boolean>> searchHandler) {

    this.searchProfile(id).onComplete(search -> searchHandler.handle(Future.succeededFuture(search.result() != null)));

  }

}
