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

import static eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension.Asserts.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.ErrorMessage;
import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Trusts}.
 *
 * @see Trusts
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class TrustsIT {

	/**
	 * Verify that return error when try to add an event over an undefined user.
	 *
	 * @param profileRepository service to manage the profiles.
	 * @param client            to connect to the server.
	 * @param testContext       context to test.
	 *
	 * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotAddEventOverAnUdefinedSourceAndTargetUsers(ProfilesRepository profileRepository,
			WebClient client, VertxTestContext testContext) {

		testRequest(client, HttpMethod.POST, Trusts.PATH + "/undefined-source-identifier/with/undefined-target-identifier")
				.expect(res -> {

					assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
					final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
					assertThat(error.code).isNotEmpty();
					assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
					testContext.completeNow();

				}).sendJson(new JsonObject(), testContext);
	}

	/**
	 * Verify that return error when try to add an event over an undefined user.
	 *
	 * @param profileRepository service to manage the profiles.
	 * @param client            to connect to the server.
	 * @param testContext       context to test.
	 *
	 * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotAddEventOverAnUdefinedSourceUser(ProfilesRepository profileRepository, WebClient client,
			VertxTestContext testContext) {

		profileRepository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			testRequest(client, HttpMethod.POST, Trusts.PATH + "/undefined-source-identifier/with/" + stored.id)
					.expect(res -> {

						assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
						final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
						assertThat(error.code).isNotEmpty();
						assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
						testContext.completeNow();

					}).sendJson(new JsonObject(), testContext);

		}));
	}

	/**
	 * Verify that return error when try to add an event over an undefined user.
	 *
	 * @param profileRepository service to manage the profiles.
	 * @param client            to connect to the server.
	 * @param testContext       context to test.
	 *
	 * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotAddEventOverAnUdefinedTargetUser(ProfilesRepository profileRepository, WebClient client,
			VertxTestContext testContext) {

		profileRepository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			testRequest(client, HttpMethod.POST, Trusts.PATH + "/" + stored.id + "/with/undefined-target-identifier")
					.expect(res -> {

						assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
						final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
						assertThat(error.code).isNotEmpty();
						assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
						testContext.completeNow();

					}).sendJson(new JsonObject(), testContext);

		}));
	}

	/**
	 * Verify that return error when try to add an event over an undefined user.
	 *
	 * @param profileRepository service to manage the profiles.
	 * @param client            to connect to the server.
	 * @param testContext       context to test.
	 *
	 * @see Trusts#addTrustEvent( JsonObject, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotAddEventOverTheSameUser(ProfilesRepository profileRepository, WebClient client,
			VertxTestContext testContext) {

		profileRepository.storeProfile(new WeNetUserProfile(), testContext.succeeding(stored -> {

			testRequest(client, HttpMethod.POST, Trusts.PATH + "/" + stored.id + "/with/" + stored.id).expect(res -> {

				assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
				final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
				assertThat(error.code).isNotEmpty();
				assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
				testContext.completeNow();

			}).sendJson(new JsonObject(), testContext);

		}));
	}

}
