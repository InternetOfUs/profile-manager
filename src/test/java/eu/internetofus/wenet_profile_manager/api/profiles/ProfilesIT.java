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
import static io.vertx.junit5.web.TestRequest.queryParam;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.ValidationsTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.ErrorMessage;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepositoryTestCase;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
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
	 * Verify that return error when search an undefined profile.
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

			testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id).expect(res -> testContext.verify(() -> {

				assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
				final WeNetUserProfile found = assertThatBodyIs(WeNetUserProfile.class, res);
				assertThat(found).isEqualTo(profile);
				testContext.completeNow();

			})).send(testContext);

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
	public void shouldNotStoreANonProfileObject(WebClient client, VertxTestContext testContext) {

		testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
			assertThat(error.code).isNotEmpty().isEqualTo("bad_profile");
			assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
			testContext.completeNow();

		}).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
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

		}).sendJson(profile.toJsonObject(), testContext);
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
				repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

					assertThat(foundProfile).isEqualTo(stored);
					testContext.completeNow();

				})));

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
		profile._creationTs = 0;
		profile._lastUpdateTs = 1;
		testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
			final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile.id = stored.id;
			assertThat(stored).isNotNull().isNotEqualTo(profile);
			profile._creationTs = stored._creationTs;
			profile._lastUpdateTs = stored._lastUpdateTs;
			assertThat(stored).isEqualTo(profile);
			repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

				assertThat(foundProfile).isEqualTo(stored);
				testContext.completeNow();

			})));

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
			repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

				assertThat(foundProfile).isEqualTo(stored);
				testContext.completeNow();

			})));

		}).sendJson(profile.toJsonObject(), testContext);

	}

	/**
	 * Verify that return error when try to update an undefined profile.
	 *
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateProfileThatIsNotDefined(WebClient client, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfileTest().createBasicExample(1);
		testRequest(client, HttpMethod.PUT, Profiles.PATH + "/undefined-profile-identifier").expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
			assertThat(error.code).isNotEmpty();
			assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
			testContext.completeNow();

		}).sendJson(profile.toJsonObject(), testContext);
	}

	/**
	 * Verify that return error when try to update with a model that is not a
	 * profile.
	 *
	 * @param repository  that manage the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateProfileWithANotProfileObject(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfileTest().createBasicExample(1), testContext.succeeding(profile -> {

			testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id).expect(res -> {

				assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
				final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
				assertThat(error.code).isNotEmpty();
				assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
				testContext.completeNow();

			}).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
		}));
	}

	/**
	 * Verify that not update a profile if any change is done.
	 *
	 * @param repository  that manage the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateProfileBecauseNotChangesHasDone(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfileTest().createBasicExample(1), testContext.succeeding(profile -> {

			testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id).expect(res -> {

				assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
				final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
				assertThat(error.code).isNotEmpty();
				assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
				testContext.completeNow();

			}).sendJson(new JsonObject(), testContext);
		}));

	}

	/**
	 * Verify that not update a profile because the source is not valid.
	 *
	 * @param repository  that manage the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject,
	 *      io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateProfileBecauseBadSource(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfileTest().createBasicExample(1), testContext.succeeding(profile -> {

			testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id).expect(res -> {

				assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
				final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
				assertThat(error.code).isNotEmpty().endsWith(".nationality");
				assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
				testContext.completeNow();

			}).sendJson(new JsonObject().put("nationality", ValidationsTest.STRING_256), testContext);
		}));

	}

	/**
	 * Verify that can update a basic profile with another.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldUpdateBasicProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfileTest().createBasicExample(1), testContext.succeeding(storedProfile -> {

			final WeNetUserProfile newProfile = new WeNetUserProfileTest().createBasicExample(2);
			newProfile.id = UUID.randomUUID().toString();
			testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
					.expect(res -> testContext.verify(() -> {

						assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
						final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
						assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
						newProfile.id = storedProfile.id;
						newProfile._creationTs = storedProfile._creationTs;
						newProfile._lastUpdateTs = updated._lastUpdateTs;
						assertThat(updated).isEqualTo(newProfile);
						testContext.completeNow();

					})).sendJson(newProfile.toJsonObject(), testContext);

		}));

	}

	/**
	 * Verify that can update a complex profile with another.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldUpdateProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		testContext.assertComplete(new WeNetUserProfileTest().createModelExample(23, repository))
				.setHandler(createdProfile -> {

					final WeNetUserProfile created = createdProfile.result();
					testContext.assertComplete(created.validate("codePrefix", repository)).setHandler(validation -> {

						repository.storeProfile(created, testContext.succeeding(storedProfile -> {

							final WeNetUserProfile newProfile = new WeNetUserProfileTest().createModelExample(2);
							newProfile.id = UUID.randomUUID().toString();
							testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
									.expect(res -> testContext.verify(() -> {

										assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
										final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
										assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
										newProfile.id = storedProfile.id;
										newProfile._creationTs = storedProfile._creationTs;
										newProfile._lastUpdateTs = updated._lastUpdateTs;
										newProfile.norms.get(0).id = updated.norms.get(0).id;
										newProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
										newProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
										newProfile.relationships = updated.relationships;
										newProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
										newProfile.socialPractices.get(0).materials.id = updated.socialPractices.get(0).materials.id;
										newProfile.socialPractices.get(0).competences.id = updated.socialPractices.get(0).competences.id;
										newProfile.socialPractices.get(0).norms.get(0).id = updated.socialPractices.get(0).norms.get(0).id;
										newProfile.personalBehaviors.get(0).id = updated.personalBehaviors.get(0).id;
										assertThat(updated).isEqualTo(newProfile);

										repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100,
												testContext.succeeding(page -> {

													assertThat(page.profiles).hasSize(1);
													assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
													assertThat(page.profiles.get(0).to).isEqualTo(storedProfile._lastUpdateTs);
													assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);
													testContext.completeNow();
												}));

									})).sendJson(newProfile.toJsonObject(), testContext);
						}));
					});
				});

	}

	/**
	 * Verify that return error when delete an undefined profile.
	 *
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotDeleteProfileWithAnUndefinedProfileId(WebClient client, VertxTestContext testContext) {

		testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/undefined-profile-identifier").expect(res -> {

			assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
			assertThat(error.code).isNotEmpty();
			assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
			testContext.completeNow();

		}).send(testContext);
	}

	/**
	 * Verify that can delete a profile.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldDeleteProfile(ProfilesRepository repository, WebClient client, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(storedProfile -> {

			testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + storedProfile.id)
					.expect(res -> testContext.verify(() -> {

						assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
						testContext.completeNow();

					})).send(testContext);

		}));

	}

	/**
	 * Verify that can not obtain a historic page of a non defined profile.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfileHistoricPage(String,
	 *      io.vertx.ext.web.api.OperationRequest, Handler)
	 */
	@Test
	public void shouldNotFoundHistoricOfANUndefinedProfile(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefined-profile-identifier" + Profiles.HISTORIC_PATH)
				.expect(res -> {

					assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
					final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
					assertThat(error.code).isNotEmpty();
					assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
					testContext.completeNow();

				}).send(testContext);
	}

	/**
	 * Verify that can not obtain a historic page of a non defined profile.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfileHistoricPage(String,
	 *      io.vertx.ext.web.api.OperationRequest, Handler)
	 */
	@Test
	public void shouldNotFoundHistoricOfNonUpdateProfile(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(storedProfile -> {
			testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
					.expect(res -> {

						assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
						final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
						assertThat(error.code).isNotEmpty();
						assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
						testContext.completeNow();

					}).send(testContext);

		}));
	}

	/**
	 * Verify that can obtain a historic profile page.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfileHistoricPage(String,
	 *      io.vertx.ext.web.api.OperationRequest, Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePage(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		final String profileId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		ProfilesRepositoryTestCase.createProfilePage(repository, profileId, page, testContext,
				testContext.succeeding(created -> {

					testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profileId + Profiles.HISTORIC_PATH)
							.with(queryParam("limit", "100")).expect(res -> {

								assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
								final HistoricWeNetUserProfilesPage found = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, res);
								assertThat(found).isEqualTo(created);
								testContext.completeNow();

							}).send(testContext);
				}));

	}

	/**
	 * Verify that can obtain a historic profile page for a range of dates.
	 *
	 * @param repository  to access the profiles.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Profiles#retrieveProfileHistoricPage(String,
	 *      io.vertx.ext.web.api.OperationRequest, Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageForARange(ProfilesRepository repository, WebClient client,
			VertxTestContext testContext) {

		final String profileId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		ProfilesRepositoryTestCase.createProfilePage(repository, profileId, page, testContext,
				testContext.succeeding(created -> {

					testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profileId + Profiles.HISTORIC_PATH)
							.with(queryParam("from", "50000"), queryParam("to", "150000"), queryParam("order", "DESC"),
									queryParam("offset", "5"), queryParam("limit", "3"))
							.expect(res -> {

								assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
								final HistoricWeNetUserProfilesPage found = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, res);
								final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
								expected.offset = 5;
								expected.total = 10;
								expected.profiles = new ArrayList<>();
								expected.profiles.add(created.profiles.get(9));
								expected.profiles.add(created.profiles.get(8));
								expected.profiles.add(created.profiles.get(7));
								assertThat(found).isEqualTo(expected);
								testContext.completeNow();

							}).send(testContext);
				}));

	}

}
