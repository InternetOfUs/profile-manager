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
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustEvent;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustEventTest;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Integration test over the {@link TrustsRepository}.
 *
 * @see TrustsRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class TrustsRepositoryIT {

	/**
	 * Verify that can not store a trust that can not be an object.
	 *
	 * @param repository  to test.
	 * @param testContext context that executes the test.
	 *
	 * @see TrustsRepository#storeTrustEvent(TrustEvent, Handler)
	 */
	@Test
	public void shouldNotStoreATrustThatCanNotBeAnObject(TrustsRepository repository, VertxTestContext testContext) {

		final TrustEvent trust = new TrustEvent() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public JsonObject toJsonObject() {

				return null;
			}
		};
		repository.storeTrustEvent(trust, testContext.failing(failed -> {
			testContext.completeNow();
		}));

	}

	/**
	 * Verify that can store a trust event.
	 *
	 * @param repository  to test.
	 * @param testContext context that executes the test.
	 *
	 * @see TrustsRepository#storeTrustEvent(TrustEvent, Handler)
	 */
	@Test
	public void shouldStoreTrust(TrustsRepository repository, VertxTestContext testContext) {

		final TrustEvent trust = new TrustEventTest().createModelExample(1);
		repository.storeTrustEvent(trust, testContext.succeeding(empty -> {
			testContext.completeNow();
		}));

	}

}
