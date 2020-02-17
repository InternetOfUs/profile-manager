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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

/**
 * Implementation of the {@link ProfilesRepository}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesRepositoryImpl extends Repository implements ProfilesRepository {

	/**
	 * The name of the collection that contains the profiles.
	 */
	public static final String PROFILES_COLLECTION = "profiles";

	/**
	 * The name of the collection that contains the historic profiles.
	 */
	public static final String HISTORIC_PROFILES_COLLECTION = "historicProfiles";

	/**
	 * Create a new service.
	 *
	 * @param pool to create the connections.
	 */
	public ProfilesRepositoryImpl(MongoClient pool) {

		super(pool);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchProfileObject(String id, Handler<AsyncResult<JsonObject>> searchHandler) {

		final JsonObject query = new JsonObject().put("_id", id);
		this.pool.findOne(PROFILES_COLLECTION, query, null, search -> {

			if (search.failed()) {

				searchHandler.handle(Future.failedFuture(search.cause()));

			} else {

				final JsonObject profile = search.result();
				if (profile == null) {

					searchHandler.handle(Future.failedFuture("Does not exist a profile with the identifier '" + id + "'."));

				} else {

					profile.put("id", profile.remove("_id"));
					searchHandler.handle(Future.succeededFuture(profile));
				}
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler) {

		final long now = System.currentTimeMillis();
		profile.put("_creationTs", now);
		profile.put("_lastUpdateTs", now);
		this.pool.save(PROFILES_COLLECTION, profile, store -> {

			if (store.failed()) {

				storeHandler.handle(Future.failedFuture(store.cause()));

			} else {

				final String id = store.result();
				profile.put("id", id);
				profile.remove("_id");
				storeHandler.handle(Future.succeededFuture(profile));
			}

		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> updateHandler) {

		final Object id = profile.remove("id");
		final JsonObject query = new JsonObject().put("_id", id);
		final long now = System.currentTimeMillis();
		profile.put("_lastUpdateTs", now);
		final JsonObject updateProfile = new JsonObject().put("$set", profile);
		final UpdateOptions options = new UpdateOptions().setMulti(false);
		this.pool.updateCollectionWithOptions(PROFILES_COLLECTION, query, updateProfile, options, update -> {

			if (update.failed()) {

				updateHandler.handle(Future.failedFuture(update.cause()));

			} else if (update.result().getDocModified() != 1) {

				updateHandler.handle(Future.failedFuture("Not Found profile to update"));

			} else {

				profile.put("id", id);
				profile.remove("_id");
				updateHandler.handle(Future.succeededFuture(profile));
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteProfile(String id, Handler<AsyncResult<Void>> deleteHandler) {

		final JsonObject query = new JsonObject().put("_id", id);
		this.pool.removeDocument(PROFILES_COLLECTION, query, remove -> {

			if (remove.failed()) {

				deleteHandler.handle(Future.failedFuture(remove.cause()));

			} else if (remove.result().getRemovedCount() != 1) {

				deleteHandler.handle(Future.failedFuture("Not Found profile to delete"));

			} else {

				deleteHandler.handle(Future.succeededFuture());
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeHistoricProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler) {

		this.pool.save(HISTORIC_PROFILES_COLLECTION, profile, store -> {

			if (store.failed()) {

				storeHandler.handle(Future.failedFuture(store.cause()));

			} else {

				storeHandler.handle(Future.succeededFuture(profile));
			}

		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchHistoricProfilePageObject(String profileId, long from, long to, boolean ascending, int offset,
			int limit, Handler<AsyncResult<JsonObject>> searchHandler) {

		final JsonObject query = new JsonObject().put("profile.id", profileId)
				.put("from", new JsonObject().put("$gte", from)).put("to", new JsonObject().put("$lte", to));
		this.pool.count(HISTORIC_PROFILES_COLLECTION, query, count -> {

			if (count.failed()) {

				searchHandler.handle(Future.failedFuture(count.cause()));

			} else {

				final long total = count.result().longValue();
				final JsonObject page = new JsonObject().put("offset", offset).put("total", total);
				if (total == 0) {

					searchHandler.handle(Future.failedFuture("Not found profiles with the identifier"));

				} else if (offset >= total) {

					searchHandler.handle(Future.succeededFuture(page));

				} else {

					int order = 1;
					if (!ascending) {

						order = -1;
					}

					final FindOptions options = new FindOptions();
					options.setLimit(limit);
					options.setSort(new JsonObject().put("from", order));
					options.setSkip(offset);
					options.setFields(new JsonObject().put("_id", 0));
					this.pool.findWithOptions(HISTORIC_PROFILES_COLLECTION, query, options, find -> {

						if (find.failed()) {

							searchHandler.handle(Future.failedFuture(find.cause()));

						} else {

							page.put("profiles", find.result());
							searchHandler.handle(Future.succeededFuture(page));
						}

					});

				}

			}
		});

	}
}
