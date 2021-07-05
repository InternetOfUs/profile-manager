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
import eu.internetofus.common.components.profile_manager.CommunityProfilesPage;
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
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.List;

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
   * @param vertx   that contains the event bus to use.
   * @param pool    to create the database connections.
   * @param version of the schemas.
   *
   * @return the future that inform when the repository will be registered or not.
   */
  static Future<Void> register(final Vertx vertx, final MongoClient pool, final String version) {

    final var repository = new CommunitiesRepositoryImpl(vertx, pool, version);
    new ServiceBinder(vertx).setAddress(CommunitiesRepository.ADDRESS).register(CommunitiesRepository.class,
        repository);
    return repository.migrateDocumentsToCurrentVersions();

  }

  /**
   * Search for the community with the specified identifier.
   *
   * @param id identifier of the user to search.
   *
   * @return the future found community.
   */
  @GenIgnore
  default Future<CommunityProfile> searchCommunity(final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.searchCommunity(id, promise);
    return Model.fromFutureJsonObject(promise.future(), CommunityProfile.class);

  }

  /**
   * Search for the community with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  void searchCommunity(String id, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Store a community.
   *
   * @param community    to store.
   * @param storeHandler handler to manage the store.
   */
  @GenIgnore
  default void storeCommunity(final CommunityProfile community,
      final Handler<AsyncResult<CommunityProfile>> storeHandler) {

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

  /**
   * Create a query to obtain the communities that has the specified parameters.
   *
   * @param appId       application identifier to match for the communities to
   *                    return.
   * @param name        to match for the communities to return.
   * @param description to match for the communities to return.
   * @param keywords    to match for the communities to return.
   * @param members     to match for the communities to return.
   *
   * @return the query that will return the required communities.
   */
  static JsonObject createCommunityProfilesPageQuery(final String appId, final String name, final String description,
      final List<String> keywords, final List<String> members) {

    return new QueryBuilder().withEqOrRegex("appId", appId).withEqOrRegex("name", name)
        .withEqOrRegex("description", description).withEqOrRegex("keywords", keywords)
        .withElementEqOrRegex("members", "userId", members).build();

  }

  /**
   * Create the components used to sort the communities to return.
   *
   * @param order to use.
   *
   * @return the component that has to be used to sort the communities.
   *
   * @throws ValidationErrorException if the sort parameter is not valid.
   */
  static JsonObject createCommunityProfilesPageSort(final List<String> order) throws ValidationErrorException {

    final var sort = Repository.queryParamToSort(order, "bad_order", (value) -> {

      switch (value) {
      case "appId":
      case "name":
      case "description":
      case "keywords":
      case "_creationTs":
      case "_lastUpdateTs":
        return value;
      case "members":
        return "members.userId";
      default:
        return null;
      }

    });
    return sort;
  }

  /**
   * Retrieve the communities defined on the context.
   *
   * @param context that describe witch page want to obtain.
   * @param handler for the obtained page.
   */
  @GenIgnore
  default void retrieveCommunityProfilesPageObject(final ModelsPageContext context,
      final Handler<AsyncResult<JsonObject>> handler) {

    this.retrieveCommunityProfilesPageObject(context.query, context.sort, context.offset, context.limit, handler);
  }

  /**
   * Retrieve the communities defined on the context.
   *
   * @param context       that describe witch page want to obtain.
   * @param searchHandler for the obtained page.
   */
  @GenIgnore
  default void retrieveCommunityProfilesPage(final ModelsPageContext context,
      final Handler<AsyncResult<CommunityProfilesPage>> searchHandler) {

    this.retrieveCommunityProfilesPage(context.query, context.sort, context.offset, context.limit, searchHandler);

  }

  /**
   * Retrieve the communities defined on the context.
   *
   * @param query         to obtain the required communities.
   * @param sort          describe how has to be ordered the obtained communities.
   * @param offset        the index of the first community to return.
   * @param limit         the number maximum of communities to return.
   * @param searchHandler for the obtained page.
   */
  @GenIgnore
  default void retrieveCommunityProfilesPage(final JsonObject query, final JsonObject sort, final int offset,
      final int limit, final Handler<AsyncResult<CommunityProfilesPage>> searchHandler) {

    this.retrieveCommunityProfilesPageObject(query, sort, offset, limit, search -> {

      if (search.failed()) {

        searchHandler.handle(Future.failedFuture(search.cause()));

      } else {

        final var value = search.result();
        final var page = Model.fromJsonObject(value, CommunityProfilesPage.class);
        if (page == null) {

          searchHandler.handle(Future.failedFuture("The stored communities page is not valid."));

        } else {

          searchHandler.handle(Future.succeededFuture(page));
        }
      }
    });

  }

  /**
   * Retrieve a page with some communities.
   *
   * @param query   to obtain the required communities.
   * @param sort    describe how has to be ordered the obtained communities.
   * @param offset  the index of the first community to return.
   * @param limit   the number maximum of communities to return.
   * @param handler to inform of the found communities.
   */
  void retrieveCommunityProfilesPageObject(JsonObject query, JsonObject sort, int offset, int limit,
      Handler<AsyncResult<JsonObject>> handler);

}
