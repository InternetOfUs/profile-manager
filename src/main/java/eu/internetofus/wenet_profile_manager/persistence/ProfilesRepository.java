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

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfile;
import eu.internetofus.wenet_profile_manager.api.profiles.HistoricWeNetUserProfilesPage;
import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The service to manage the {@link WeNetUserProfile} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface ProfilesRepository {

	/**
	 * The address of this service.
	 */
	String ADDRESS = "wenet_profile_manager.persistence.profiles";

	/**
	 * Create a proxy of the {@link ProfilesRepository}.
	 *
	 * @param vertx where the service has to be used.
	 *
	 * @return the profile.
	 */
	static ProfilesRepository createProxy(Vertx vertx) {

		return new ProfilesRepositoryVertxEBProxy(vertx, ProfilesRepository.ADDRESS);
	}

	/**
	 * Search for the profile with the specified identifier.
	 *
	 * @param id            identifier of the profile to search.
	 * @param searchHandler handler to manage the search.
	 */
	@GenIgnore
	default void searchProfile(String id, Handler<AsyncResult<WeNetUserProfile>> searchHandler) {

		this.searchProfileObject(id, search -> {

			if (search.failed()) {

				searchHandler.handle(Future.failedFuture(search.cause()));

			} else {

				final JsonObject value = search.result();
				final WeNetUserProfile profile = Model.fromJsonObject(value, WeNetUserProfile.class);
				if (profile == null) {

					searchHandler.handle(Future.failedFuture("The stored profile is not valid."));

				} else {

					searchHandler.handle(Future.succeededFuture(profile));
				}
			}
		});
	}

	/**
	 * Search for the profile with the specified identifier.
	 *
	 * @param id            identifier of the profile to search.
	 * @param searchHandler handler to manage the search.
	 */
	void searchProfileObject(String id, Handler<AsyncResult<JsonObject>> searchHandler);

	/**
	 * Register this service.
	 *
	 * @param vertx that contains the event bus to use.
	 * @param pool  to create the database connections.
	 */
	static void register(Vertx vertx, MongoClient pool) {

		new ServiceBinder(vertx).setAddress(ProfilesRepository.ADDRESS).register(ProfilesRepository.class,
				new ProfilesRepositoryImpl(pool));

	}

	/**
	 * Store a profile.
	 *
	 * @param profile      to store.
	 * @param storeHandler handler to manage the store.
	 */
	@GenIgnore
	default void storeProfile(WeNetUserProfile profile, Handler<AsyncResult<WeNetUserProfile>> storeHandler) {

		final JsonObject object = profile.toJsonObject();
		if (object == null) {

			storeHandler.handle(Future.failedFuture("The profile can not converted to JSON."));

		} else {

			this.storeProfile(object, stored -> {
				if (stored.failed()) {

					storeHandler.handle(Future.failedFuture(stored.cause()));

				} else {

					final JsonObject value = stored.result();
					final WeNetUserProfile storedProfile = Model.fromJsonObject(value, WeNetUserProfile.class);
					if (storedProfile == null) {

						storeHandler.handle(Future.failedFuture("The stored profile is not valid."));

					} else {

						storeHandler.handle(Future.succeededFuture(storedProfile));
					}

				}
			});
		}
	}

	/**
	 * Store a profile.
	 *
	 * @param profile      to store.
	 * @param storeHandler handler to manage the search.
	 */
	void storeProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler);

	/**
	 * Update a profile.
	 *
	 * @param profile       to update.
	 * @param updateHandler handler to manage the update.
	 */
	@GenIgnore
	default void updateProfile(WeNetUserProfile profile, Handler<AsyncResult<WeNetUserProfile>> updateHandler) {

		final JsonObject object = profile.toJsonObject();
		if (object == null) {

			updateHandler.handle(Future.failedFuture("The profile can not converted to JSON."));

		} else {

			this.updateProfile(object, updated -> {
				if (updated.failed()) {

					updateHandler.handle(Future.failedFuture(updated.cause()));

				} else {

					final JsonObject value = updated.result();
					final WeNetUserProfile updatedProfile = Model.fromJsonObject(value, WeNetUserProfile.class);
					if (updatedProfile == null) {

						updateHandler.handle(Future.failedFuture("The updated profile is not valid."));

					} else {

						updateHandler.handle(Future.succeededFuture(updatedProfile));
					}

				}
			});
		}
	}

	/**
	 * Update a profile.
	 *
	 * @param profile       to update.
	 * @param updateHandler handler to manage the update result.
	 */
	void updateProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> updateHandler);

	/**
	 * Delete a profile.
	 *
	 * @param id            identifier of the profile to delete.
	 * @param deleteHandler handler to manage the delete result.
	 */
	void deleteProfile(String id, Handler<AsyncResult<Void>> deleteHandler);

	/**
	 * Store a historic profile.
	 *
	 * @param profile      to store.
	 * @param storeHandler handler to manage the store.
	 */
	@GenIgnore
	default void storeHistoricProfile(HistoricWeNetUserProfile profile,
			Handler<AsyncResult<HistoricWeNetUserProfile>> storeHandler) {

		final JsonObject object = profile.toJsonObject();
		if (object == null) {

			storeHandler.handle(Future.failedFuture("The profile can not converted to JSON."));

		} else {

			this.storeHistoricProfile(object, stored -> {
				if (stored.failed()) {

					storeHandler.handle(Future.failedFuture(stored.cause()));

				} else {

					final JsonObject value = stored.result();
					value.remove("_id");
					final HistoricWeNetUserProfile storedProfile = Model.fromJsonObject(value, HistoricWeNetUserProfile.class);
					if (storedProfile == null) {

						storeHandler.handle(Future.failedFuture("The stored profile is not valid."));

					} else {

						storeHandler.handle(Future.succeededFuture(storedProfile));
					}

				}
			});
		}
	}

	/**
	 * Store a historic profile.
	 *
	 * @param profile      to store.
	 * @param storeHandler handler to manage the search.
	 */
	void storeHistoricProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler);

	/**
	 * Search for some historic profiles.
	 *
	 *
	 * @param profileId     identifier of the profile to get the historic values.
	 * @param from          the date inclusive that mark the older limit in witch
	 *                      the profile has to be active. It is the difference,
	 *                      measured in milliseconds, between the time when the
	 *                      profile has to be valid and midnight, January 1, 1970
	 *                      UTC.
	 * @param to            the date inclusive that mark the newest limit in witch
	 *                      the profile has to be active. It is the difference,
	 *                      measured in milliseconds, between the time when the
	 *                      profile has not more valid and midnight, January 1, 1970
	 *                      UTC.
	 * @param ascending     this is {@code true} if it has to return in ascending
	 *                      order.
	 * @param offset        index of the first profile to return.
	 * @param limit         number maximum of profiles to return.
	 * @param searchHandler handler to manage the search.
	 */
	@GenIgnore
	default void searchHistoricProfilePage(String profileId, long from, long to, boolean ascending, int offset, int limit,
			Handler<AsyncResult<HistoricWeNetUserProfilesPage>> searchHandler) {

		this.searchHistoricProfilePageObject(profileId, from, to, ascending, offset, limit, search -> {

			if (search.failed()) {

				searchHandler.handle(Future.failedFuture(search.cause()));

			} else {

				final JsonObject value = search.result();
				final HistoricWeNetUserProfilesPage page = Model.fromJsonObject(value, HistoricWeNetUserProfilesPage.class);
				if (page == null) {

					searchHandler.handle(Future.failedFuture("The stored page is not valid."));

				} else {

					searchHandler.handle(Future.succeededFuture(page));
				}
			}
		});

	}

	/**
	 * Search for some historic profiles.
	 *
	 *
	 * @param profileId     identifier of the profile to get the historic values.
	 * @param from          the date inclusive that mark the older limit in witch
	 *                      the profile has to be active. It is the difference,
	 *                      measured in milliseconds, between the time when the
	 *                      profile has to be valid and midnight, January 1, 1970
	 *                      UTC.
	 * @param to            the date inclusive that mark the newest limit in witch
	 *                      the profile has to be active. It is the difference,
	 *                      measured in milliseconds, between the time when the
	 *                      profile has not more valid and midnight, January 1, 1970
	 *                      UTC.
	 * @param ascending     this is {@code true} if it has to return in ascending
	 *                      order.
	 * @param offset        index of the first profile to return.
	 * @param limit         number maximum of profiles to return.
	 * @param searchHandler handler to manage the search.
	 */
	void searchHistoricProfilePageObject(String profileId, long from, long to, boolean ascending, int offset, int limit,
			Handler<AsyncResult<JsonObject>> searchHandler);

}
