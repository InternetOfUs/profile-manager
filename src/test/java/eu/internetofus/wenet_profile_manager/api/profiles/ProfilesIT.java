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

import static eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension.Asserts.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.ErrorMessage;
import eu.internetofus.wenet_profile_manager.api.profiles.Profiles;
import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Profiles}.
 *
 * @see Profiles
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesIT {

	/**
	 * Verify that return error when the profile is not defined.
	 *
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotFoundProfileWithAnUndefinedProfileId(WebClient client, VertxTestContext testContext) {

		testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefined-profile-identifier").expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
			assertThat(error.code).isNotEmpty();
			assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
			testContext.completeNow();

		}).send(testContext);
	}

	/**
	 * Verify that return a defined profile.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfileTest().createModelExample(1), testContext.succeeding(profile -> {

			testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id).expect(res -> {

				assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
				final WeNetUserProfile found = assertThatBodyIs(WeNetUserProfile.class, res);
				assertThat(found).isEqualTo(profile);
				testContext.completeNow();

			}).send(testContext);

		}));

	}

	/**
	 * Verify that can not store a bad profile.
	 *
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#createProfile(io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotStoreBadProfile(WebClient client, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.id = UUID.randomUUID().toString();
		testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
			assertThat(error.code).isNotEmpty().isEqualTo("bad_profile.id");
			assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
			testContext.completeNow();

		}).sendJson(profile, testContext);
	}

	/**
	 * Verify that store a profile.
	 *
	 * @param repository  that manage the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#createProfile(io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		testContext.assertComplete(new WeNetUserProfileTest().createModelExample(1, repository)).setHandler(created -> {

			final WeNetUserProfile profile = created.result();
			testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

				assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
				final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
				assertThat(stored).isNotNull().isNotEqualTo(profile);
				profile.id = stored.id;
				assertThat(stored).isNotEqualTo(profile);
				profile._creationTs = stored._creationTs;
				profile._lastUpdateTs = stored._lastUpdateTs;
				assertThat(stored).isNotEqualTo(profile);
				profile.norms.get(0).id = stored.norms.get(0).id;
				profile.plannedActivities.get(0).id = stored.plannedActivities.get(0).id;
				profile.plannedActivities.get(1).id = stored.plannedActivities.get(1).id;
				profile.relevantLocations.get(0).id = stored.relevantLocations.get(0).id;
				profile.socialPractices.get(0).id = stored.socialPractices.get(0).id;
				profile.socialPractices.get(0).materials.id = stored.socialPractices.get(0).materials.id;
				profile.socialPractices.get(0).competences.id = stored.socialPractices.get(0).competences.id;
				profile.socialPractices.get(0).norms.get(0).id = stored.socialPractices.get(0).norms.get(0).id;
				profile.personalBehaviors.get(0).id = stored.personalBehaviors.get(0).id;
				assertThat(stored).isEqualTo(profile);
				repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> {

					assertThat(foundProfile).isEqualTo(stored);
					testContext.completeNow();

				}));

			}).sendJson(profile.toJsonObject(), testContext);

		});
	}

	/**
	 * Verify that store an empty profile.
	 *
	 * @param repository  that manage the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#createProfile(io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreEmptyProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
			final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile.id = stored.id;
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile._creationTs = stored._creationTs;
			profile._lastUpdateTs = stored._lastUpdateTs;
			assertThat(stored).isEqualTo(profile);
			repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> {

				assertThat(foundProfile).isEqualTo(stored);
				testContext.completeNow();

			}));

		}).sendJson(profile.toJsonObject(), testContext);

	}

	/**
	 * Verify that store a simple profile.
	 *
	 * @param repository  that manage the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#createProfile(io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreSimpleProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfileTest().createModelExample(1);
		testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
			final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile.id = stored.id;
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile._creationTs = stored._creationTs;
			profile._lastUpdateTs = stored._lastUpdateTs;
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile.norms.get(0).id = stored.norms.get(0).id;
			profile.plannedActivities.get(0).id = stored.plannedActivities.get(0).id;
			profile.relevantLocations.get(0).id = stored.relevantLocations.get(0).id;
			profile.socialPractices.get(0).id = stored.socialPractices.get(0).id;
			profile.socialPractices.get(0).materials.id = stored.socialPractices.get(0).materials.id;
			profile.socialPractices.get(0).competences.id = stored.socialPractices.get(0).competences.id;
			profile.socialPractices.get(0).norms.get(0).id = stored.socialPractices.get(0).norms.get(0).id;
			profile.personalBehaviors.get(0).id = stored.personalBehaviors.get(0).id;
			assertThat(stored).isEqualTo(profile);
			repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> {

				assertThat(foundProfile).isEqualTo(stored);
				testContext.completeNow();

			}));

		}).sendJson(profile.toJsonObject(), testContext);

	}

}
