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

package eu.internetofus.wenet_profile_manager.api.trusts;

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.profile_manager.Trust;
import eu.internetofus.common.components.profile_manager.TrustAggregator;
import eu.internetofus.common.components.profile_manager.UserPerformanceRatingEvent;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.TimeManager;
import eu.internetofus.common.vertx.QueryBuilder;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.persistence.TrustsRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

/**
 * Resource that implements the web services defined at {@link Trusts}.
 *
 * @see Trusts
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class TrustsResource implements Trusts {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * The repository to manage the trusts events.
   */
  protected TrustsRepository repository;

  /**
   * Create a new instance to provide the services of the {@link Trusts}.
   *
   * @param vertx with the event bus to use.
   */
  public TrustsResource(final Vertx vertx) {

    this.vertx = vertx;
    this.repository = TrustsRepository.createProxy(vertx);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTrustEvent(final JsonObject body, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var event = Model.fromJsonObject(body, UserPerformanceRatingEvent.class);
    if (event == null) {

      Logger.debug("The {} is not a valid TrustEvent.", body);
      ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_trust_event",
          "The trust event is not right.");

    } else {

      event.validate(new WeNetValidateContext("bad_event", this.vertx)).onComplete(validation -> {

        if (validation.failed()) {

          final var cause = validation.cause();
          Logger.debug(cause, "The {} is not valid.", event);
          ServiceResponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          this.repository.storeTrustEvent(event).onComplete(store -> {

            if (store.failed()) {

              final var cause = store.cause();
              Logger.debug(cause, "Cannot store {}.", event);
              ServiceResponseHandlers.responseFailedWith(resultHandler, Status.INTERNAL_SERVER_ERROR, cause);

            } else {

              final var storedEvent = store.result();
              ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, storedEvent);
            }
          });
        }

      });
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateTrust(final String sourceId, final String targetId, final String appId, final String communityId,
      final String taskTypeId, final String taskId, final String relationship, final Long reportFrom,
      final Long reportTo, final TrustAggregator aggregator, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var query = new QueryBuilder().with("sourceId", sourceId).with("targetId", targetId)
        .withEqOrRegex("appId", appId).withEqOrRegex("communityId", communityId).withEqOrRegex("taskTypeId", taskTypeId)
        .withEqOrRegex("taskId", taskId).withRange("reportTime", reportFrom, reportTo).build();
    this.repository.calculateTrustBy(aggregator, query).onComplete(calculation -> {

      if (calculation.failed()) {

        final var cause = calculation.cause();
        Logger.debug(cause, "Cannot calculate the trust {} for {}.", aggregator, query);
        ServiceResponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

      } else {

        final var trust = new Trust();
        trust.value = calculation.result();
        trust.calculatedTime = TimeManager.now();
        ServiceResponseHandlers.responseOk(resultHandler, trust);

      }

    });

  }

}
