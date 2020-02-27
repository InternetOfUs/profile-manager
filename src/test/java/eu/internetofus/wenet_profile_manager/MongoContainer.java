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

package eu.internetofus.wenet_profile_manager;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * A container to start a mongodb.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MongoContainer extends GenericContainer<MongoContainer> {

	/**
	 * The name of the mongo docker container to use.
	 */
	private static final String MONGO_DOCKER_NAME = "mongo:4.2.3";

	/**
	 * Create a new mongo container.
	 */
	public MongoContainer() {

		super(MONGO_DOCKER_NAME);

		this.withEnv("MONGO_INITDB_ROOT_USERNAME", "root");
		this.withEnv("MONGO_INITDB_ROOT_PASSWORD", "password");
		this.withEnv("MONGO_INITDB_DATABASE", "wenetProfileManagerDB");
		this.withCopyFileToContainer(
				MountableFile.forClasspathResource("eu/internetofus/wenet_profile_manager/initialize-test-mongodb.js"),
				"/docker-entrypoint-initdb.d/init-mongo.js");

		this.withExposedPorts(27017);
		this.waitingFor(Wait.forListeningPort());

	}

	/**
	 * Return the host where the mongo is running.
	 *
	 * @return the name of the host where the mongodb is binded.
	 */
	public String getMongodbHost() {

		return this.getContainerIpAddress();
	}

	/**
	 * Return the port where the mongo is running.
	 *
	 * @return the name of the port where the mongodb is binded.
	 */
	public int getMongodbPort() {

		return this.getMappedPort(27017);
	}

	/**
	 * Create and start the MongoDB container.
	 *
	 * @return the created mongo container.
	 */
	public static Future<MongoContainer> createAndStart() {

		final Promise<MongoContainer> promise = Promise.promise();
		try {

			final MongoContainer container = new MongoContainer();
			container.start();

			// verify is ready to get info from the database
			final JsonObject conf = new JsonObject().put("host", container.getMongodbHost())
					.put("port", container.getMongodbPort()).put("user", "wenetProfileManager")
					.put("db_name", "wenetProfileManagerDB").put("username", "wenetProfileManager").put("password", "password");
			final MongoClient client = MongoClient.create(Vertx.vertx(), conf);
			client.getCollections(collections -> {

				if (collections.failed()) {

					promise.fail(collections.cause());

				} else {

					promise.complete(container);
				}

			});

		} catch (final Throwable throwable) {

			promise.fail(throwable);
		}

		return promise.future();

	}

}
