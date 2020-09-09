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

package eu.internetofus.wenet_profile_manager.api.trusts;

import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.common.vertx.QueryBuilder;
import eu.internetofus.wenet_profile_manager.persistence.TrustsRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

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
  public void addTrustEvent(final JsonObject body, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var event = Model.fromJsonObject(body, UserPerformanceRatingEvent.class);
    if (event == null) {

      Logger.debug("The {} is not a valid TrustEvent.", body);
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_trust_event", "The trust event is not right.");

    } else {

      event.validate("bad_event", this.vertx).onComplete(validation -> {

        if (validation.failed()) {

          final var cause = validation.cause();
          Logger.debug(cause, "The {} is not valid.", event);
          OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          this.repository.storeTrustEvent(event, store -> {

            if (store.failed()) {

              final var cause = store.cause();
              Logger.debug(cause, "Cannot store {}.", event);
              OperationReponseHandlers.responseFailedWith(resultHandler, Status.INTERNAL_SERVER_ERROR, cause);

            } else {

              final var storedEvent = store.result();
              OperationReponseHandlers.responseWith(resultHandler, Status.CREATED, storedEvent);
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
  public void calculateTrust(final String sourceId, final String targetId, final String appId, final String communityId, final String taskTypeId, final String taskId, final String relationship, final Long reportFrom, final Long reportTo,
      final TrustAggregator aggregator, final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var query = new QueryBuilder().with("sourceId", sourceId).with("targetId", targetId).withEqOrRegex("appId", appId).withEqOrRegex("communityId", communityId).withEqOrRegex("taskTypeId", taskTypeId).withEqOrRegex("taskId", taskId)
        .withRange("reportTime", reportFrom, reportTo).build();
    this.repository.calculateTrustBy(aggregator, query, calculation -> {

      if (calculation.failed()) {

        final var cause = calculation.cause();
        Logger.debug(cause, "Cannot calculate the trust {} for {}.", aggregator, query);
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

      } else {

        final var trust = new Trust();
        trust.value = calculation.result();
        trust.calculatedTime = TimeManager.now();
        OperationReponseHandlers.responseOk(resultHandler, trust);

      }

    });

  }

}
