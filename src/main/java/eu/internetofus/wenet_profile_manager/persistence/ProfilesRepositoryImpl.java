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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.vertx.Repository;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

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
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public ProfilesRepositoryImpl(final MongoClient pool, final String version) {

    super(pool, version);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchProfileObject(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var query = new JsonObject().put("_id", id);
    this.findOneDocument(PROFILES_COLLECTION, query, null, found -> {
      final var _id = (String) found.remove("_id");
      return found.put("id", _id);
    }, searchHandler);

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
    final var now = TimeManager.now();
    profile.put("_creationTs", now);
    profile.put("_lastUpdateTs", now);
    this.storeOneDocument(PROFILES_COLLECTION, profile, stored -> {

      final var _id = (String) stored.remove("_id");
      return stored.put("id", _id);

    }, storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(final JsonObject profile, final Handler<AsyncResult<Void>> updateHandler) {

    final var id = profile.remove("id");
    final var query = new JsonObject().put("_id", id);
    final var now = TimeManager.now();
    profile.put("_lastUpdateTs", now);
    this.updateOneDocument(PROFILES_COLLECTION, query, profile, updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    final var query = new JsonObject().put("_id", id);
    this.deleteOneDocument(PROFILES_COLLECTION, query, deleteHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeHistoricProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

    this.storeOneDocument(HISTORIC_PROFILES_COLLECTION, profile, stored -> {
      stored.remove("_id");
      return stored;
    }, storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchHistoricProfilePageObject(final JsonObject query, final JsonObject sort, final int offset, final int limit, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var options = new FindOptions();
    options.setSkip(offset);
    options.setLimit(limit);
    options.getFields().put("_id", 0);
    options.setSort(sort);
    this.searchPageObject(HISTORIC_PROFILES_COLLECTION, query, options, "profiles", null, searchHandler);

  }

  /**
   * Migrate the collections to the current version.
   *
   * @return the future that will inform if the migration is a success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    return this.migrateCollection(PROFILES_COLLECTION, WeNetUserProfile.class).compose(map -> this.migrateCollection(HISTORIC_PROFILES_COLLECTION, HistoricWeNetUserProfile.class));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfileUserIdsPageObject(final int offset, final int limit, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final FindOptions options = new FindOptions();
    options.setFields(new JsonObject().put("_id", true));
    options.setSort(new JsonObject().put("_creationTs", 1).put("_id", 1));
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(PROFILES_COLLECTION, new JsonObject(), options, "profiles", null, profilesHandler -> {

      if (profilesHandler.failed()) {

        searchHandler.handle(Future.failedFuture(profilesHandler.cause()));

      } else {

        final var page = profilesHandler.result();
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
        searchHandler.handle(Future.succeededFuture(page));
      }

    });
  }

}
