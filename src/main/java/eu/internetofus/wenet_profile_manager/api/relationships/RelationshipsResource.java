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
package eu.internetofus.wenet_profile_manager.api.relationships;

import eu.internetofus.common.components.WeNetModelContext;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.common.vertx.ServiceRequests;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.RelationshipsRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

/**
 * Resource that provide the methods for the {@link Relationships}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RelationshipsResource implements Relationships {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * The repository to manage the relationships.
   */
  protected RelationshipsRepository repository;

  /**
   * Create a new instance to provide the services of the {@link Relationships}.
   *
   * @param vertx with the event bus to use.
   */
  public RelationshipsResource(final Vertx vertx) {

    this.vertx = vertx;
    this.repository = RelationshipsRepository.createProxy(vertx);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveSocialNetworkRelationshipsPage(final String appId, final String sourceId, final String targetId,
      final String type, final Double weightFrom, final Double weightTo, final String orderValue, final int offset,
      final int limit, final ServiceRequest request, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var order = ServiceRequests.extractQueryArray(orderValue);
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModelsPage(offset, limit, (page, promise) -> {

      page.query = RelationshipsRepository.createSocialNetworkRelationshipsPageQuery(appId, sourceId, targetId, type,
          weightFrom, weightTo);
      page.sort = RelationshipsRepository.createSocialNetworkRelationshipsPageSort(order);
      this.repository.retrieveSocialNetworkRelationshipsPageObject(page, search -> promise.handle(search));

    }, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSocialNetworkRelationships(final String appId, final String sourceId, final String targetId,
      final String type, final Double weightFrom, final Double weightTo, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var query = RelationshipsRepository.createSocialNetworkRelationshipsPageQuery(appId, sourceId, targetId, type,
        weightFrom, weightTo);
    final var context = new ServiceContext(request, resultHandler);
    this.repository.deleteSocialNetworkRelationship(query, delete -> {

      if (delete.failed()) {

        final var cause = delete.cause();
        Logger.trace(cause, "Cannot delete {}.\n{}", query, context);
        ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.NOT_FOUND,
            "not_found_relationship", "Does not exist any 'relationship' associated to '" + query + "'.");

      } else {

        ServiceResponseHandlers.responseOk(context.resultHandler);
      }

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOrUpdateRelationship(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    final var model = WeNetModelContext.creteWeNetContext("relationship", SocialNetworkRelationship.class, this.vertx);
    ModelResources.toModel(body, model, context, () -> {

      ModelResources.validate(model, context, () -> {

        this.repository.storeOrUpdateSocialNetworkRelationship(model.value, result -> {

          if (result.failed()) {

            final var cause = result.cause();
            Logger.trace(cause, "The {} can not be added or updated with {}.\n{}", () -> model, () -> model.source,
                () -> context);
            ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

          } else {

            ServiceResponseHandlers.responseOk(context.resultHandler, model.value);
          }

        });

      });

    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOrUpdateSomeRelationships(final JsonArray body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var context = new ServiceContext(request, resultHandler);
    ModelResources.toModel(body, SocialNetworkRelationship.class, context, relationships -> {

      new WeNetValidateContext("bad_", this.vertx)
          .validateListField("body", relationships, SocialNetworkRelationship::compareIds).apply(null)
          .onComplete(valid -> {

            if (valid.failed()) {

              final var cause = valid.cause();
              Logger.trace(cause, "The {} is not valid.\n{}", body, context);
              ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

            } else {

              @SuppressWarnings("rawtypes")
              final List<Future> futures = new ArrayList<>();
              for (final var relationship : relationships) {

                final Promise<String> promise = Promise.promise();
                this.repository.storeOrUpdateSocialNetworkRelationship(relationship, promise);
                futures.add(promise.future());

              }

              CompositeFuture.all(futures).onComplete(stored -> {

                if (stored.failed()) {

                  final var cause = stored.cause();
                  Logger.trace(cause, "The {} can not be added or updated.\n{}", () -> relationships, () -> context);
                  ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

                } else {

                  ServiceResponseHandlers.responseOk(context.resultHandler, Model.toJsonArray(relationships));
                }

              });

            }

          });
    });

  }

}
