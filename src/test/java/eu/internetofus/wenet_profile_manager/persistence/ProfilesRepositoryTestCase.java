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

import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfileTest;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic test over the {@link ProfilesRepository}.
 *
 * @param <T> the repository to test.
 *
 * @see ProfilesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class ProfilesRepositoryTestCase<T extends ProfilesRepository> {

	/**
	 * The repository to do the tests.
	 */
	protected T repository;

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotFoundUndefinedProfile(VertxTestContext testContext) {

		this.repository.searchProfile("undefined profile identifier", testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotFoundUndefinedProfileObject(VertxTestContext testContext) {

		this.repository.searchProfileObject("undefined profile identifier", testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundProfile(VertxTestContext testContext) {

		this.repository.storeProfile(new WeNetUserProfile(), testContext.succeeding(storedProfile -> {

			this.repository.searchProfile(storedProfile.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {
				assertThat(foundProfile).isEqualTo(storedProfile);
				testContext.completeNow();
			})));

		}));

	}

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfileObject(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldFoundProfileObject(VertxTestContext testContext) {

		this.repository.storeProfile(new JsonObject(), testContext.succeeding(storedProfile -> {

			this.repository.searchProfileObject(storedProfile.getString("id"),
					testContext.succeeding(foundProfile -> testContext.verify(() -> {
						assertThat(foundProfile).isEqualTo(storedProfile);
						testContext.completeNow();
					})));

		}));

	}

	/**
	 * Verify that can not store a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotStoreAProfileThatCanNotBeAnObject(VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		profile.id = "undefined profile identifier";
		this.repository.storeProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not store a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreProfile(VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile._creationTs = 0;
		profile._lastUpdateTs = 1;
		final long now = System.currentTimeMillis();
		this.repository.storeProfile(profile, testContext.succeeding(storedProfile -> testContext.verify(() -> {

			assertThat(storedProfile).isNotNull();
			assertThat(storedProfile.id).isNotEmpty();
			assertThat(storedProfile._creationTs).isNotEqualTo(0).isGreaterThanOrEqualTo(now);
			assertThat(storedProfile._lastUpdateTs).isNotEqualTo(1).isGreaterThanOrEqualTo(now);
			testContext.completeNow();
		})));

	}

	/**
	 * Verify that can not store a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#searchProfile(String, io.vertx.core.Handler)
	 */
	@Test
	public void shouldStoreProfileObject(VertxTestContext testContext) {

		final long now = System.currentTimeMillis();
		this.repository.storeProfile(new JsonObject(), testContext.succeeding(storedProfile -> testContext.verify(() -> {

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
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateUndefinedProfile(VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.id = "undefined profile identifier";
		this.repository.updateProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not update a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(JsonObject, io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateUndefinedProfileObject(VertxTestContext testContext) {

		final JsonObject profile = new JsonObject().put("id", "undefined profile identifier");
		this.repository.updateProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not update a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldNotUpdateAProfileThatCanNotBeAnObject(VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		profile.id = "undefined profile identifier";
		this.repository.updateProfile(profile, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(WeNetUserProfile,
	 *      io.vertx.core.Handler)
	 */
	@Test
	public void shouldUpdateProfile(VertxTestContext testContext) {

		final WeNetUserProfile profile = new WeNetUserProfile();
		profile.occupation = "Doctor";
		this.repository.storeProfile(profile, testContext.succeeding(stored -> testContext.verify(() -> {

			final long now = System.currentTimeMillis();
			final WeNetUserProfile update = new WeNetUserProfileTest().createModelExample(23);
			update.id = stored.id;
			update._creationTs = stored._creationTs;
			update._lastUpdateTs = 1;
			this.repository.updateProfile(update, testContext.succeeding(updatedProfile -> testContext.verify(() -> {

				assertThat(updatedProfile).isNotNull();
				assertThat(updatedProfile.id).isNotEmpty().isEqualTo(stored.id);
				assertThat(updatedProfile._creationTs).isEqualTo(stored._creationTs);
				assertThat(updatedProfile._lastUpdateTs).isGreaterThanOrEqualTo(now);
				update._lastUpdateTs = updatedProfile._lastUpdateTs;
				assertThat(updatedProfile).isEqualTo(update);
				this.repository.searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {
					assertThat(foundProfile).isEqualTo(updatedProfile);
					testContext.completeNow();
				})));
			})));

		})));

	}

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see ProfilesRepository#updateProfile(JsonObject, io.vertx.core.Handler)
	 */
	@Test
	public void shouldUpdateProfileObject(VertxTestContext testContext) {

		this.repository.storeProfile(new JsonObject().put("nationality", "Italian"),
				testContext.succeeding(stored -> testContext.verify(() -> {

					final String id = stored.getString("id");
					final JsonObject update = new JsonObject().put("id", id).put("occupation", "Unemployed");
					this.repository.updateProfile(update, testContext.succeeding(updatedProfile -> testContext.verify(() -> {

						assertThat(updatedProfile).isNotNull();
						update.put("_lastUpdateTs", updatedProfile.getLong("_lastUpdateTs"));
						assertThat(updatedProfile).isEqualTo(update);
						this.repository.searchProfileObject(id, testContext.succeeding(foundProfile -> testContext.verify(() -> {
							stored.put("_lastUpdateTs", updatedProfile.getLong("_lastUpdateTs"));
							stored.put("occupation", "Unemployed");
							assertThat(foundProfile).isEqualTo(stored);
							testContext.completeNow();
						})));
					})));

				})));

	}
}
