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

import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

/**
 * Implementation of the {@link ProfilesRepository}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesRepositoryImpl extends Repository implements ProfilesRepository {

	/**
	 * Create a new service.
	 *
	 * @param pool to create the connections.
	 */
	public ProfilesRepositoryImpl(PgPool pool) {

		super(pool);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchProfile(String id, Handler<AsyncResult<WeNetUserProfile>> searchHandler) {

		this.pool.preparedQuery("SELECT * FROM ", Tuple.of(id), query -> {

			if (query.failed()) {

				searchHandler.handle(Future.failedFuture(query.cause()));

			} else {

				final RowSet<Row> rowSet = query.result();
				if (rowSet.size() != 1) {

					searchHandler.handle(Future.failedFuture("Not found"));

				} else {

					final Row row = rowSet.iterator().next();
					final WeNetUserProfile profile = new WeNetUserProfile();
					profile.id = row.getString(0);
					searchHandler.handle(Future.succeededFuture(profile));
				}
			}

		});

	}

}
