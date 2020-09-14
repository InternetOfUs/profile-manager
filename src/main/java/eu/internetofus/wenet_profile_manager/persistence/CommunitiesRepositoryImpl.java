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
import eu.internetofus.common.vertx.ModelsPageContext;
import eu.internetofus.common.vertx.Repository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * Implementation of the {@link CommunitiesRepository}.
 *
 * @see CommunitiesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesRepositoryImpl extends Repository implements CommunitiesRepository {

  /**
   * The name of the collection that contains the communities.
   */
  public static final String COMMUNITIES_COLLECTION = "communities";

  /**
   * Create a new repository.
   *
   * @param pool to create the connections.
   */
  public CommunitiesRepositoryImpl(final MongoClient pool) {

    super(pool);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchCommunityObject(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var query = new JsonObject().put("_id", id);
    this.findOneDocument(COMMUNITIES_COLLECTION, query, null, found -> {
      final var _id = (String) found.remove("_id");
      return found.put("id", _id);
    }, searchHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeCommunity(final JsonObject community, final Handler<AsyncResult<JsonObject>> storeHandler) {

    final var id = (String) community.remove("id");
    if (id != null) {

      community.put("_id", id);
    }
    final var now = TimeManager.now();
    community.put("_creationTs", now);
    community.put("_lastUpdateTs", now);
    this.storeOneDocument(COMMUNITIES_COLLECTION, community, stored -> {

      final var _id = (String) stored.remove("_id");
      return stored.put("id", _id);

    }, storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(final JsonObject community, final Handler<AsyncResult<Void>> updateHandler) {

    final var id = community.remove("id");
    final var query = new JsonObject().put("_id", id);
    final var now = TimeManager.now();
    community.put("_lastUpdateTs", now);
    this.updateOneDocument(COMMUNITIES_COLLECTION, query, community, updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    final var query = new JsonObject().put("_id", id);
    this.deleteOneDocument(COMMUNITIES_COLLECTION, query, deleteHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityProfilesPageObject(final ModelsPageContext context, final Handler<AsyncResult<JsonObject>> handler) {

    final var options = context.toFindOptions();
    this.searchPageObject(COMMUNITIES_COLLECTION, context.query, options, "communities", community -> community.put("id", community.remove("_id")), handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityProfilesPageObject(final JsonObject query, final JsonObject sort, final int offset, final int limit, final Handler<AsyncResult<JsonObject>> handler) {

    final var options = new FindOptions();
    options.setSort(sort);
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(COMMUNITIES_COLLECTION, query, options, "communities", community -> community.put("id", community.remove("_id")), handler);

  }

}
