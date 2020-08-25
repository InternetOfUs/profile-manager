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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.vertx.Repository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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
   * @param pool to create the connections.
   */
  public ProfilesRepositoryImpl(final MongoClient pool) {

    super(pool);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchProfileObject(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final JsonObject query = new JsonObject().put("_id", id);
    this.findOneDocument(PROFILES_COLLECTION, query, null, found -> {
      final String _id = (String) found.remove("_id");
      return found.put("id", _id);
    }, searchHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> storeHandler) {

    final String id = (String) profile.remove("id");
    if (id != null) {

      profile.put("_id", id);
    }
    final long now = TimeManager.now();
    profile.put("_creationTs", now);
    profile.put("_lastUpdateTs", now);
    this.storeOneDocument(PROFILES_COLLECTION, profile, stored -> {

      final String _id = (String) stored.remove("_id");
      return stored.put("id", _id);

    }, storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(final JsonObject profile, final Handler<AsyncResult<Void>> updateHandler) {

    final Object id = profile.remove("id");
    final JsonObject query = new JsonObject().put("_id", id);
    final long now = TimeManager.now();
    profile.put("_lastUpdateTs", now);
    this.updateOneDocument(PROFILES_COLLECTION, query, profile, updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    final JsonObject query = new JsonObject().put("_id", id);
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

    final FindOptions options = new FindOptions();
    options.setSkip(offset);
    options.setLimit(limit);
    options.getFields().put("_id", 0);
    options.setSort(sort);
    this.searchPageObject(HISTORIC_PROFILES_COLLECTION, query, options, "profiles", null, searchHandler);

  }

}
