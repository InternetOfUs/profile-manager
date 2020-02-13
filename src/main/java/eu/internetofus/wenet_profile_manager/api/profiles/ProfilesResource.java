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

package eu.internetofus.wenet_profile_manager.api.profiles;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.wenet_profile_manager.api.ErrorMessage;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

/**
 * Resource that provide the methods for the {@link Profiles}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesResource implements Profiles {

	/**
	 * The repository to manage the profiles.
	 */
	protected ProfilesRepository repository;

	/**
	 * Create a new instance to provide the services of the {@link Profiles}.
	 *
	 * @param vertx where resource is defined.
	 */
	public ProfilesResource(Vertx vertx) {

		this.repository = ProfilesRepository.createProxy(vertx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveProfile(String profileId, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		this.repository.searchProfileObject(profileId, search -> {

			final JsonObject profile = search.result();
			if (profile == null) {

				Logger.debug(search.cause(), "Not found profile for {}", profileId);
				resultHandler
						.handle(Future.succeededFuture(new OperationResponse().setStatusCode(Status.NOT_FOUND.getStatusCode())
								.putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
								.setPayload(Buffer.buffer(
										new ErrorMessage("not_found_profile", "Does not exist a profile associated to '" + profileId + "'.")
												.toJsonString()))));

			} else {

				resultHandler.handle(Future.succeededFuture(new OperationResponse().setStatusCode(Status.OK.getStatusCode())
						.putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
						.setPayload(Buffer.buffer(profile.encode()))));

			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createProfile(JsonObject body, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProfile(String profileId, JsonObject body, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {

		// TODO Auto-generated method stub

	}

}
