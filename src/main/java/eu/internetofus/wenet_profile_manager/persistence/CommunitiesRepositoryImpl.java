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

import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.vertx.Repository;
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
 * Implementation of the {@link CommunitiesRepository}.
 *
 * @see CommunitiesRepository
 * @see CommunityProfile
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
   * @param vertx   event bus to use.
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public CommunitiesRepositoryImpl(final Vertx vertx, final MongoClient pool, final String version) {

    super(vertx, pool, version);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchCommunity(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var query = new JsonObject().put("_id", id);
    this.findOneDocument(COMMUNITIES_COLLECTION, query, null, found -> {
      final var _id = (String) found.remove("_id");
      return found.put("id", _id);
    }).onComplete(searchHandler);

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
    this.storeOneDocument(COMMUNITIES_COLLECTION, community, stored -> {

      final var _id = (String) stored.remove("_id");
      return stored.put("id", _id);

    }).onComplete(storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(final JsonObject community, final Handler<AsyncResult<Void>> updateHandler) {

    final var id = community.remove("id");
    final var query = new JsonObject().put("_id", id);
    this.updateOneDocument(COMMUNITIES_COLLECTION, query, community).onComplete(updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    final var query = new JsonObject().put("_id", id);
    this.deleteOneDocument(COMMUNITIES_COLLECTION, query).onComplete(deleteHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityProfilesPageObject(final JsonObject query, final JsonObject sort, final int offset,
      final int limit, final Handler<AsyncResult<JsonObject>> handler) {

    final var options = new FindOptions();
    options.setSort(sort);
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(COMMUNITIES_COLLECTION, query, options, "communities",
        community -> community.put("id", community.remove("_id"))).onComplete(handler);

  }

  /**
   * Migrate the collections to the current version.
   *
   * @return the future that will inform if the migration is a success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    return this.migrateComunitiesWithoutNorms_with_API_0_16_0_OR_LESS()
        .compose(empty -> this.migrateSchemaVersionOnCollectionTo(this.schemaVersion, COMMUNITIES_COLLECTION));
  }

  /**
   * Migrate the communities that with the API less than or equals to 0.16.0 that
   * does not have norms.
   *
   * @return the future that inform if the communities has been migrated.
   */
  protected Future<Void> migrateComunitiesWithoutNorms_with_API_0_16_0_OR_LESS() {

    final var notExists = new JsonObject().put("norms", new JsonObject().put("$exists", false));
    final var notString = new JsonObject().put("norms",
        new JsonObject().put("$not", new JsonObject().put("$type", "array")));
    final var notEq = new JsonObject().put("norms",
        new JsonObject().put("$exists", true).put("$type", "array").put("$eq", new JsonArray()));
    final var query = new JsonObject().put("$and",
        new JsonArray().add(this.createQueryToReturnDocumentsWithAVersionLessThan("0.16.1"))
            .add(new JsonObject().put("$or", new JsonArray().add(notExists).add(notString).add(notEq))));
    final var update = new JsonObject().put("$set",
        new JsonObject().put("norms", this.defaultNormsForCommunitiesWitghoutNormsAndSchema_0_16_0_or_less()));
    final var options = new UpdateOptions();
    options.setMulti(true);
    return this.pool.updateCollectionWithOptions(COMMUNITIES_COLLECTION, query, update, options).map(any -> null);
  }

  /**
   * The norms to set for the communities to migrate.
   *
   * @return the norms for the community profiles.
   */
  protected JsonArray defaultNormsForCommunitiesWitghoutNormsAndSchema_0_16_0_or_less() {

    final var norm = new ProtocolNorm();
    norm.description = "Notify user of any received incentive";
    norm.whenever = "is_received_send_incentive(Incentive)";
    norm.thenceforth = "send_user_message('INCENTIVE',Incentive)";
    return new JsonArray().add(norm.toJsonObject());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isCommunityDefined(final String id, final Handler<AsyncResult<Boolean>> searchHandler) {

    this.searchCommunity(id)
        .onComplete(search -> searchHandler.handle(Future.succeededFuture(search.result() != null)));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteAllMembersForUser(final String userId, final Handler<AsyncResult<Void>> deleteHandler) {

    final var query = new JsonObject().put("members.userId", userId);
    final var update = new JsonObject().put("$pull",
        new JsonObject().put("members", new JsonObject().put("userId", userId)));
    this.updateCollection(COMMUNITIES_COLLECTION, query, update).onComplete(deleteHandler);
  }

}
