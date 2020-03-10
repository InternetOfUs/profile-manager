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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TrustsRepositoryImpl}.
 *
 * @see TrustsRepositoryImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class TrustsRepositoryImplTest extends TrustsRepositoryTestCase<TrustsRepositoryImpl> {

	/**
	 * Create the repository to use in the tests.
	 *
	 * @param pool that create the mongo connections.
	 */
	@BeforeEach
	public void cerateRepository(MongoClient pool) {

		this.repository = new TrustsRepositoryImpl(pool);

	}

	/**
	 * Check store fail by mongo client.
	 *
	 * @param testContext test context.
	 */
	@Test
	public void shouldStoreFailedByMongoClient(VertxTestContext testContext) {

		this.repository.pool = mock(MongoClient.class);

		this.repository.storeTrustEventObject(new JsonObject(), testContext.failing(store -> {
			testContext.completeNow();
		}));
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<String>>> handler = ArgumentCaptor.forClass(Handler.class);
		verify(this.repository.pool, times(1)).save(any(), any(), handler.capture());
		handler.getValue().handle(Future.failedFuture("Internal error"));

	}

}
