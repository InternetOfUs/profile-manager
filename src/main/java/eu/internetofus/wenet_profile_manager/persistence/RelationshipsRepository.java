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
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipsPage;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.vertx.ModelsPageContext;
import eu.internetofus.common.vertx.QueryBuilder;
import eu.internetofus.common.vertx.Repository;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.List;

/**
 * The service to manage the {@link SocialNetworkRelationship} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface RelationshipsRepository {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.persistence.relationships";

  /**
   * Create a proxy of the {@link RelationshipsRepository}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the socialnetworkrelationship.
   */
  static RelationshipsRepository createProxy(final Vertx vertx) {

    return new RelationshipsRepositoryVertxEBProxy(vertx, RelationshipsRepository.ADDRESS);
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

    final var repository = new RelationshipsRepositoryImpl(vertx, pool, version);
    new ServiceBinder(vertx).setAddress(RelationshipsRepository.ADDRESS).register(RelationshipsRepository.class,
        repository);
    return repository.migrateDocumentsToCurrentVersions();

  }

  /**
   * Store or update a relationship.
   *
   * @param relationship  to add or update.
   * @param updateHandler handler to manage the update.
   */
  @GenIgnore
  default void storeOrUpdateSocialNetworkRelationship(final SocialNetworkRelationship relationship,
      final Handler<AsyncResult<String>> updateHandler) {

    final var object = relationship.toJsonObjectWithEmptyValues();
    if (object == null) {

      updateHandler.handle(Future.failedFuture("The social network relationship can not converted to JSON."));

    } else {

      this.storeOrUpdateSocialNetworkRelationship(object, updateHandler);
    }

  }

  /**
   * Update a social network relationship.
   *
   * @param relationship  to add or update.
   * @param updateHandler handler to manage the update result.
   */
  void storeOrUpdateSocialNetworkRelationship(JsonObject relationship, Handler<AsyncResult<String>> updateHandler);

  /**
   * Delete a social network relationship.
   *
   * @param query         that match the relationships to delete.
   * @param deleteHandler handler to manage the delete result.
   */
  void deleteSocialNetworkRelationship(final JsonObject query, Handler<AsyncResult<Void>> deleteHandler);

  /**
   * Create a query to obtain the relationships that has the specified parameters.
   *
   * @param appId    application identifier to match in the relationships to
   *                 return.
   * @param sourceId user identifier to match the source of the relationships to
   *                 return.
   * @param targetId user identifier to match the target of the relationships to
   *                 return.
   * @param type     to match in the relationships to return.
   *
   * @return the query that will return the required relationships.
   */
  static JsonObject createSocialNetworkRelationshipsPageQuery(final String appId, final String sourceId,
      final String targetId, final String type) {

    return new QueryBuilder().withEqOrRegex("appId", appId).withEqOrRegex("sourceId", sourceId)
        .withEqOrRegex("targetId", targetId).withEqOrRegex("type", type).build();

  }

  /**
   * Create the components used to sort the relationships to return.
   *
   * @param order to use.
   *
   * @return the component that has to be used to sort the relationships.
   *
   * @throws ValidationErrorException if the sort parameter is not valid.
   */
  static JsonObject createSocialNetworkRelationshipsPageSort(final List<String> order) throws ValidationErrorException {

    final var sort = Repository.queryParamToSort(order, "bad_order", (value) -> {

      switch (value) {
      case "appId":
      case "sourceId":
      case "targetId":
      case "type":
      case "weight":
        return value;
      default:
        return null;
      }

    });
    return sort;
  }

  /**
   * Retrieve the relationships defined on the context.
   *
   * @param context that describe witch page want to obtain.
   * @param handler for the obtained page.
   */
  @GenIgnore
  default void retrieveSocialNetworkRelationshipsPageObject(final ModelsPageContext context,
      final Handler<AsyncResult<JsonObject>> handler) {

    this.retrieveSocialNetworkRelationshipsPageObject(context.query, context.sort, context.offset, context.limit,
        handler);
  }

  /**
   * Retrieve the relationships defined on the context.
   *
   * @param context       that describe witch page want to obtain.
   * @param searchHandler for the obtained page.
   */
  @GenIgnore
  default void retrieveSocialNetworkRelationshipsPage(final ModelsPageContext context,
      final Handler<AsyncResult<SocialNetworkRelationshipsPage>> searchHandler) {

    this.retrieveSocialNetworkRelationshipsPage(context.query, context.sort, context.offset, context.limit,
        searchHandler);

  }

  /**
   * Retrieve the relationships defined on the context.
   *
   * @param query         to obtain the required relationships.
   * @param sort          describe how has to be ordered the obtained
   *                      relationships.
   * @param offset        the index of the first social network relationship to
   *                      return.
   * @param limit         the number maximum of relationships to return.
   * @param searchHandler for the obtained page.
   */
  @GenIgnore
  default void retrieveSocialNetworkRelationshipsPage(final JsonObject query, final JsonObject sort, final int offset,
      final int limit, final Handler<AsyncResult<SocialNetworkRelationshipsPage>> searchHandler) {

    this.retrieveSocialNetworkRelationshipsPageObject(query, sort, offset, limit, search -> {

      if (search.failed()) {

        searchHandler.handle(Future.failedFuture(search.cause()));

      } else {

        final var value = search.result();
        final var page = Model.fromJsonObject(value, SocialNetworkRelationshipsPage.class);
        if (page == null) {

          searchHandler.handle(Future.failedFuture("The stored relationships page is not valid."));

        } else {

          searchHandler.handle(Future.succeededFuture(page));
        }
      }
    });

  }

  /**
   * Retrieve a page with some relationships.
   *
   * @param query   to obtain the required relationships.
   * @param sort    describe how has to be ordered the obtained relationships.
   * @param offset  the index of the first social network relationship to return.
   * @param limit   the number maximum of relationships to return.
   * @param handler to inform of the found relationships.
   */
  void retrieveSocialNetworkRelationshipsPageObject(JsonObject query, JsonObject sort, int offset, int limit,
      Handler<AsyncResult<JsonObject>> handler);

}
