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

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.info.MigrationInfoDumper;
import org.tinylog.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * The verticle that provide the persistence services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PersistenceVerticle extends AbstractVerticle {

	/**
	 * The pool of database connections.
	 */
	protected PgPool pool;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		// create the pool
		final MongoClient client = MongoClient.createShared(this.vertx, config);

		final JsonObject persitenceConf = this.config().getJsonObject("persistence", new JsonObject());
		final PgConnectOptions connectOptions = new PgConnectOptions(persitenceConf);
		final PoolOptions poolOptions = new PoolOptions(persitenceConf);
		this.pool = PgPool.pool(this.vertx, connectOptions, poolOptions);

		// register services
		ProfilesRepository.register(this.vertx, this.pool);

		// initialize the data base
		this.vertx.executeBlocking(future -> {

			try {

				final JsonObject flywayConf = persitenceConf.getJsonObject("flyway", new JsonObject());
				final String url = flywayConf.getString("url", "jdbc:postgresql://" + connectOptions.getHost() + ":"
						+ connectOptions.getPort() + "/" + connectOptions.getDatabase());
				final String user = flywayConf.getString("user", connectOptions.getUser());
				final String password = flywayConf.getString("password", connectOptions.getPassword());
				final boolean baselineOnMigrate = flywayConf.getBoolean("baselineOnMigrate", true);
				final String baselineDescription = flywayConf.getString("baselineDescription", "WeNet profile manager");
				final String encoding = flywayConf.getString("encoding", "UTF-8");
				final Flyway flyway = Flyway.configure().baselineDescription(baselineDescription)
						.baselineOnMigrate(baselineOnMigrate).encoding(encoding).dataSource(url, user, password).load();
				flyway.migrate();
				Logger.info("Database has been migrated: {}\n {}", () -> url,
						() -> MigrationInfoDumper.dumpToAsciiTable(flyway.info().all()));
				future.complete();

			} catch (final Throwable throwable) {

				future.fail(throwable);
			}

		}, updateDb -> {

			if (updateDb.failed()) {

				startPromise.fail(updateDb.cause());

			} else {

				startPromise.complete();
			}

		});

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
