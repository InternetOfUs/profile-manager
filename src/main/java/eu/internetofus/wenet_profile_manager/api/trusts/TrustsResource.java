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

package eu.internetofus.wenet_profile_manager.api.trusts;

import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.api.OperationReponseHandlers;
import eu.internetofus.common.api.models.Model;
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
	 * Create an empty resource. This is only used for unit tests.
	 */
	protected TrustsResource() {

	}

	/**
	 * Create a new instance to provide the services of the {@link Trusts}.
	 *
	 * @param vertx with the event bus to use.
	 */
	public TrustsResource(Vertx vertx) {

		this.vertx = vertx;
		this.repository = TrustsRepository.createProxy(vertx);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTrustEvent(JsonObject body, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		final UserPerformanceRatingEvent event = Model.fromJsonObject(body, UserPerformanceRatingEvent.class);
		if (event == null) {

			Logger.debug("The {} is not a valid TrustEvent.", body);
			OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_trust_event",
					"The trust event is not right.");

		} else {

			event.validate("bad_trust_event", this.vertx, validate -> {

				if (validate.failed()) {

					final Throwable cause = validate.cause();
					Logger.debug(cause, "The {} is not valid.", event);
					OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

				} else {

					this.repository.storeTrustEvent(event, store -> {

						if (store.failed()) {

							final Throwable cause = validate.cause();
							Logger.debug(cause, "Cannot store {}.", event);
							OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

						} else {

							OperationReponseHandlers.responseOk(resultHandler);
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
	public void calculateTrust(String sourceId, String targetId, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		final Trust trust = new Trust();
		trust.value = Math.random();
		trust.calculatedTime = TimeManager.now();
		OperationReponseHandlers.responseOk(resultHandler, trust);

	}

}
