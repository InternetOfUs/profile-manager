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

package eu.internetofus.wenet_profile_manager.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfileTest;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfilesPage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Integration test over the {@link ProfilesRepository}.
 *
 * @see ProfilesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesRepositoryIT {

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotFoundUndefinedProfile(Vertx vertx, VertxTestContext testContext) {

		ProfilesRepository.createProxy(vertx).searchProfile("undefined user identifier", testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not found a profile object if it is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotFoundUndefinedProfileObject(Vertx vertx, VertxTestContext testContext) {

		ProfilesRepository.createProxy(vertx).searchProfileObject("undefined user identifier",
				testContext.failing(failed -> {
					testContext.completeNow();
				}));

	}

	/**
	 * Verify that can found a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundProfile(Vertx vertx, VertxTestContext testContext) {

		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(storedProfile -> {

			repository.searchProfile(storedProfile.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {
				assertThat(foundProfile).isEqualTo(storedProfile);
				testContext.completeNow();
			})));

		}));

	}

	/**
	 * Verify that can found a profile object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundProfileObject(Vertx vertx, VertxTestContext testContext) {

		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeProfile(new JsonObject(), testContext.succeeding(storedProfile -> {

			repository.searchProfileObject(storedProfile.getString("id"),
					testContext.succeeding(foundProfile -> testContext.verify(() -> {
						assertThat(foundProfile).isEqualTo(storedProfile);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can not store a profile that can not be an object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#storeProfile(WeNetUserProfile, Handler)
	 */
	@Test
	public void shouldNotStoreAProfileThatCanNotBeAnObject(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		profile.id = "undefined user identifier";
		ProfilesRepository.createProxy(vertx).storeProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can store a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#storeProfile(WeNetUserProfile, Handler)
	 */
	@Test
	public void shouldStoreProfile(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile._creationTs = 0;
		profile._lastUpdateTs = 1;
		final long now = TimeManager.now();
		ProfilesRepository.createProxy(vertx).storeProfile(profile,
				testContext.succeeding(storedProfile -> testContext.verify(() -> {

					assertThat(storedProfile).isNotNull();
					assertThat(storedProfile.id).isNotEmpty();
					assertThat(storedProfile._creationTs).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
					assertThat(storedProfile._lastUpdateTs).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can store a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#storeProfile(WeNetUserProfile, Handler)
	 */
	@Test
	public void shouldStoreProfileWithAnId(Vertx vertx, VertxTestContext testContext) {

		final String id = UUID.randomUUID().toString();
		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.id = id;
		profile._creationTs = 0;
		profile._lastUpdateTs = 1;
		final long now = TimeManager.now();
		ProfilesRepository.createProxy(vertx).storeProfile(profile,
				testContext.succeeding(storedProfile -> testContext.verify(() -> {

					assertThat(storedProfile.id).isEqualTo(id);
					assertThat(storedProfile._creationTs).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
					assertThat(storedProfile._lastUpdateTs).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can store a profile with an id of an stored profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#storeProfile(WeNetUserProfile, Handler)
	 */
	@Test
	public void shouldNotStoreTwoProfileWithTheSameId(Vertx vertx, VertxTestContext testContext) {

		final String id = UUID.randomUUID().toString();
		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.id = id;
		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeProfile(profile, testContext.succeeding(storedProfile -> testContext.verify(() -> {

			repository.storeProfile(profile, testContext.failing(error -> testContext.completeNow()));

		})));

	}

	/**
	 * Verify that can store a profile object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#storeProfile(WeNetUserProfile, Handler)
	 */
	@Test
	public void shouldStoreProfileObject(Vertx vertx, VertxTestContext testContext) {

		final long now = TimeManager.now();
		ProfilesRepository.createProxy(vertx).storeProfile(new JsonObject(),
				testContext.succeeding(storedProfile -> testContext.verify(() -> {

					assertThat(storedProfile).isNotNull();
					final String id = storedProfile.getString("id");
					assertThat(id).isNotEmpty();
					assertThat(storedProfile.getLong("_creationTs", 0l)).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
					assertThat(storedProfile.getLong("_lastUpdateTs", 1l)).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can not update a profile if it is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateUndefinedProfile(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.id = "undefined user identifier";
		ProfilesRepository.createProxy(vertx).updateProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not update a profile if it is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(JsonObject, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateUndefinedProfileObject(Vertx vertx, VertxTestContext testContext) {

		final JsonObject profile = new JsonObject().put("id", "undefined user identifier");
		ProfilesRepository.createProxy(vertx).updateProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not update a profile if it is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateAProfileThatCanNotBeAnObject(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		profile.id = "undefined user identifier";
		ProfilesRepository.createProxy(vertx).updateProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can update a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldUpdateProfile(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.occupation = "Doctor";
		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeProfile(profile, testContext.succeeding(stored -> testContext.verify(() -> {

			final long now = TimeManager.now();
			final WeNetUserProfile update = new WeNetUserProfileTest().createModelExample(23);
			update.id = stored.id;
			update._creationTs = stored._creationTs;
			update._lastUpdateTs = 1;
			repository.updateProfile(update, testContext.succeeding(empty -> testContext.verify(() -> {

				repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

					assertThat(stored).isNotNull();
					assertThat(foundProfile.id).isNotEmpty().isEqualTo(stored.id);
					assertThat(foundProfile._creationTs).isEqualTo(stored._creationTs);
					assertThat(foundProfile._lastUpdateTs).isGreaterThanOrEqualTo(now);
					update._lastUpdateTs = foundProfile._lastUpdateTs;
					assertThat(foundProfile).isEqualTo(update);
					testContext.completeNow();
				})));
			})));

		})));

	}

	/**
	 * Verify that update a defined profile object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(JsonObject, io.vertx.core.Handler)
	 */
	@Test
	public void shouldUpdateProfileObject(Vertx vertx, VertxTestContext testContext) {

		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeProfile(new JsonObject().put("nationality", "Italian"),
				testContext.succeeding(stored -> testContext.verify(() -> {

					final String id = stored.getString("id");
					final JsonObject update = new JsonObject().put("id", id).put("occupation", "Unemployed");
					repository.updateProfile(update, testContext.succeeding(empty -> testContext.verify(() -> {

						repository.searchProfileObject(id, testContext.succeeding(foundProfile -> testContext.verify(() -> {
							stored.put("_lastUpdateTs", foundProfile.getLong("_lastUpdateTs"));
							stored.put("occupation", "Unemployed");
							assertThat(foundProfile).isEqualTo(stored);
							testContext.completeNow();
						})));
					})));

				})));

	}

	/**
	 * Verify that can not delete a profile if it is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotDeleteUndefinedProfile(Vertx vertx, VertxTestContext testContext) {

		ProfilesRepository.createProxy(vertx).deleteProfile("undefined user identifier", testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can delete a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(JsonObject, io.vertx.core.Handler)
	 */
	@Test
	public void shouldDeleteProfile(Vertx vertx, VertxTestContext testContext) {

		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeProfile(new JsonObject(), testContext.succeeding(stored -> {

			final String id = stored.getString("id");
			repository.deleteProfile(id, testContext.succeeding(success -> {

				repository.searchProfileObject(id, testContext.failing(search -> {

					testContext.completeNow();

				}));

			}));

		}));

	}

	/**
	 * Verify that can not store a profile that can not be an object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotStoreAHistoricProfileThatCanNotBeAnObject(Vertx vertx, VertxTestContext testContext) {

		final HistoricWeNetUserProfile profile = new HistoricWeNetUserProfile() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		ProfilesRepository.createProxy(vertx).storeHistoricProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can store a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreHistoricProfile(Vertx vertx, VertxTestContext testContext) {

		final HistoricWeNetUserProfile profile = new HistoricWeNetUserProfile();
		ProfilesRepository.createProxy(vertx).storeHistoricProfile(profile,
				testContext.succeeding(storedProfile -> testContext.verify(() -> {

					assertThat(storedProfile).isNotNull();
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can store a profile object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreHistoricProfileObject(Vertx vertx, VertxTestContext testContext) {

		ProfilesRepository.createProxy(vertx).storeHistoricProfile(new JsonObject(),
				testContext.succeeding(storedProfile -> testContext.verify(() -> {

					assertThat(storedProfile).isNotNull();
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can not found any historic profiles that match to an undefined
	 * identifier.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchHistoricProfilePage(String, long, long,
	 *      boolean, int, int, Handler)
	 */
	@Test
	public void shouldNotFoundAnyHistoricProfileFromAnUdefinedId(Vertx vertx, VertxTestContext testContext) {

		ProfilesRepository.createProxy(vertx).searchHistoricProfilePage("undefined user identifier", 0, Long.MAX_VALUE,
				false, 0, 100, testContext.succeeding(found -> testContext.verify(() -> {
					assertThat(found.offset).isEqualTo(0);
					assertThat(found.total).isEqualTo(0);
					assertThat(found.profiles).isNull();
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can not found any profile object that match to an undefined
	 * identifier.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchHistoricProfilePageObject(String, long, long,
	 *      boolean, int, int, Handler)
	 */
	@Test
	public void shouldNotFoundAnyHistoricProfileObjectFromAnUdefinedId(Vertx vertx, VertxTestContext testContext) {

		ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject("undefined user identifier", 0,
				Long.MAX_VALUE, true, 0, 100, testContext.succeeding(found -> testContext.verify(() -> {
					assertThat(found.getLong("offset")).isEqualTo(0);
					assertThat(found.getLong("total")).isEqualTo(0);
					assertThat(found.getJsonArray("profiles")).isNull();
					testContext.completeNow();
				})));

	}

	/**
	 * Verify that can found a profile.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePage(Vertx vertx, VertxTestContext testContext) {

		final HistoricWeNetUserProfile historic = new HistoricWeNetUserProfile();
		historic.from = 10000;
		historic.to = 1000000;
		historic.profile = new WeNetUserProfileTest().createBasicExample(1);
		final String id = UUID.randomUUID().toString();
		historic.profile.id = id;
		final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
		repository.storeHistoricProfile(historic, testContext.succeeding(storedProfile -> {

			repository.searchHistoricProfilePage(id, 0, Long.MAX_VALUE, false, 0, 100,
					testContext.succeeding(foundProfile -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
						page.total = 1;
						page.offset = 0;
						page.profiles = new ArrayList<>();
						page.profiles.add(historic);
						assertThat(foundProfile).isEqualTo(page);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Create a profile page.
	 *
	 * @param vertx           event bus to use.
	 * @param userId          identifier of the profile to get the historic.
	 * @param page            that has to be created.
	 * @param testContext     context to test.
	 * @param creationHandler handler to apply when has been created the page.
	 */
	public static void createProfilePage(Vertx vertx, String userId, HistoricWeNetUserProfilesPage page,
			VertxTestContext testContext, Handler<AsyncResult<HistoricWeNetUserProfilesPage>> creationHandler) {

		final int numProfiles = page.profiles.size();
		if (page.total == numProfiles) {

			creationHandler.handle(Future.succeededFuture(page));

		} else {

			final HistoricWeNetUserProfile historic = new HistoricWeNetUserProfileTest()
					.createModelExample(page.profiles.size());
			historic.from = numProfiles * 10000;
			historic.to = (1 + numProfiles) * 10000;
			historic.profile.id = userId;
			ProfilesRepository.createProxy(vertx).storeHistoricProfile(historic, testContext.succeeding(store -> {

				page.profiles.add(store);
				createProfilePage(vertx, userId, page, testContext, creationHandler);

			}));

		}

	}

	/**
	 * Verify that can found a profile object.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObject(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 0, Long.MAX_VALUE, true, 0, 100,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can found a profile object from a date.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObjectWithFrom(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 70000, Long.MAX_VALUE, true, 0, 100,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						created.total = 13;
						created.profiles = created.profiles.subList(7, 20);
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can found a profile object to a date.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObjectWithTo(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 0, 70000, true, 0, 100,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						created.total = 7;
						created.profiles = created.profiles.subList(0, 7);
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can found a profile object on descending order.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchHistoricProfilePageObject(String, long, long,
	 *      boolean, int, int, Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObjectOnDescendingOrder(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 0, Long.MAX_VALUE, false, 0, 100,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						Collections.reverse(created.profiles);
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can found a profile object from an offset.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObjectWithOffset(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 0, Long.MAX_VALUE, true, 5, 100,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						created.profiles = created.profiles.subList(5, 20);
						created.offset = 5;
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that return empty page if the offset is greater than the total.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObjectWithOffsetBiggerThanTotal(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 0, Long.MAX_VALUE, true, 21, 100,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						created.profiles = null;
						created.offset = 21;
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can found a profile object with a limit.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundHistoricProfilePageObjectWithLimit(Vertx vertx, VertxTestContext testContext) {

		final String userId = UUID.randomUUID().toString();
		final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
		page.total = 20;
		page.profiles = new ArrayList<>();
		createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

			ProfilesRepository.createProxy(vertx).searchHistoricProfilePageObject(userId, 0, Long.MAX_VALUE, true, 0, 10,
					testContext.succeeding(found -> testContext.verify(() -> {

						final HistoricWeNetUserProfilesPage foundModel = Model.fromJsonObject(found,
								HistoricWeNetUserProfilesPage.class);
						created.profiles = created.profiles.subList(0, 10);
						assertThat(foundModel).isEqualTo(created);
						testContext.completeNow();
					})));

		}));

	}

}
