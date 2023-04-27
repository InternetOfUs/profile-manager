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

import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.vertx.Repository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * Implementation of the {@link RelationshipsRepository}.
 *
 * @see RelationshipsRepository
 * @see SocialNetworkRelationship
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RelationshipsRepositoryImpl extends Repository implements RelationshipsRepository {

  /**
   * The name of the collection that contains the relationships.
   */
  public static final String RELATIONSHIPS_COLLECTION = "relationships";

  /**
   * Create a new repository.
   *
   * @param vertx   event bus to use.
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public RelationshipsRepositoryImpl(final Vertx vertx, final MongoClient pool, final String version) {

    super(vertx, pool, version);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeOrUpdateSocialNetworkRelationship(final JsonObject relationship,
      final Handler<AsyncResult<String>> updateHandler) {

    final var query = RelationshipsRepository.createSocialNetworkRelationshipsPageQuery(relationship.getString("appId"),
        relationship.getString("sourceId"), relationship.getString("targetId"), relationship.getString("type"), null,
        null);
    this.upsertOneDocument(RELATIONSHIPS_COLLECTION, query, relationship, true).onComplete(updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSocialNetworkRelationship(final JsonObject query, final Handler<AsyncResult<Void>> deleteHandler) {

    this.deleteDocuments(RELATIONSHIPS_COLLECTION, query).onComplete(deleteHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveSocialNetworkRelationshipsPageObject(final JsonObject query, final JsonObject sort,
      final int offset, final int limit, final Handler<AsyncResult<JsonObject>> handler) {

    final var options = new FindOptions();
    options.setSort(sort);
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(RELATIONSHIPS_COLLECTION, query, options, "relationships", relation -> relation.remove("_id"))
        .onComplete(handler);

  }

  /**
   * Migrate the relationships to the current version.
   *
   * @return the future that will inform if the relationships migration is a
   *         success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    return this.migrateSchemaVersionOnCollectionTo(this.schemaVersion, RELATIONSHIPS_COLLECTION);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteAllSocialNetworkRelationshipWith(final String userId,
      final Handler<AsyncResult<Void>> deleteHandler) {

    final var fields = new JsonArray().add(new JsonObject().put("sourceId", userId))
        .add(new JsonObject().put("targetId", userId));
    final var query = new JsonObject().put("$or", fields);
    this.deleteDocuments(RELATIONSHIPS_COLLECTION, query).onComplete(deleteHandler);
  }

}
