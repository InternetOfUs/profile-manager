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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.persitences.Repository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Implementation of the {@link ProfilesRepository}.
 *
 * @see ProfilesRepository
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
	 * The name of the collection that contains the trusts.
	 */
	public static final String TRUSTS_COLLECTION = "trusts";

	/**
	 * Create a new repository.
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
		this.findOneDocument(PROFILES_COLLECTION, query, null, found -> found.put("id", found.remove("_id")),
				searchHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler) {

		final long now = TimeManager.now();
		profile.put("_creationTs", now);
		profile.put("_lastUpdateTs", now);
		this.storeOneDocument(PROFILES_COLLECTION, profile, "id", storeHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProfile(JsonObject profile, Handler<AsyncResult<Void>> updateHandler) {

		final Object id = profile.remove("id");
		final JsonObject query = new JsonObject().put("_id", id);
		final long now = TimeManager.now();
		profile.put("_lastUpdateTs", now);
		this.updateOneDocument(PROFILES_COLLECTION, query, profile, updateHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteProfile(String id, Handler<AsyncResult<Void>> deleteHandler) {

		final JsonObject query = new JsonObject().put("_id", id);
		this.deleteOneDocument(PROFILES_COLLECTION, query, deleteHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeHistoricProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> storeHandler) {

		this.storeOneDocument(HISTORIC_PROFILES_COLLECTION, profile, null, storeHandler, "_id");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchHistoricProfilePageObject(String profileId, long from, long to, boolean ascending, int offset,
			int limit, Handler<AsyncResult<JsonObject>> searchHandler) {

		final JsonObject query = new JsonObject().put("profile.id", profileId)
				.put("from", new JsonObject().put("$gte", from)).put("to", new JsonObject().put("$lte", to));
		this.searchPageObject(HISTORIC_PROFILES_COLLECTION, query, new JsonObject().put("_id", 0), offset, limit,
				"profiles", searchHandler);

	}

}
