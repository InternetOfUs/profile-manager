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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * The verticle that provide the persistence services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PersistenceVerticle extends AbstractVerticle {

	/**
	 * The name of the pool of connections.
	 */
	private static final String PERSISTENCE_POOL_NAME = "WENET_PROFILE_MANAGER_POOL";

	/**
	 * The pool of database connections.
	 */
	protected MongoClient pool;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		try {
			// create the pool
			final JsonObject persitenceConf = this.config().getJsonObject("persistence", new JsonObject());
			this.pool = MongoClient.createShared(this.vertx, persitenceConf, PERSISTENCE_POOL_NAME);

			// register services
			ProfilesRepository.register(this.vertx, this.pool);

			startPromise.complete();

		} catch (final Throwable cause) {

			startPromise.fail(cause);
		}
	}

	/**
	 * Close the connections pool.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void stop() throws Exception {

		if (this.pool != null) {
			this.pool.close();
			this.pool = null;
		}

	}

}
