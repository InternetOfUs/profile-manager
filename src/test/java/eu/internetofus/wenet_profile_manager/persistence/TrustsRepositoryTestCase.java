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

import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.api.trusts.TrustEvent;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustEventTest;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic test over the {@link TrustsRepository}.
 *
 * @param <T> the repository to test.
 *
 * @see TrustsRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class TrustsRepositoryTestCase<T extends TrustsRepository> {

	/**
	 * The repository to do the tests.
	 */
	protected T repository;

	/**
	 * Verify that can not store a trust that can not be an object.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see TrustsRepository#storeTrustEvent(TrustEvent, Handler)
	 */
	@Test
	public void shouldNotStoreATrustThatCanNotBeAnObject(VertxTestContext testContext) {

		final TrustEvent trust = new TrustEvent() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		this.repository.storeTrustEvent(trust, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can store a trust event.
	 *
	 * @param testContext context that executes the test.
	 *
	 * @see TrustsRepository#storeTrustEvent(TrustEvent, Handler)
	 */
	@Test
	public void shouldStoreTrust(VertxTestContext testContext) {

		final TrustEvent trust = new TrustEventTest().createModelExample(1);
		this.repository.storeTrustEvent(trust, testContext.succeeding(empty -> {
			testContext.completeNow();
		}));

	}

}
