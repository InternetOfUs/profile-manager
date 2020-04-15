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

package eu.internetofus.wenet_profile_manager.api;

import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.api.models.wenet.PlannedActivity;
import eu.internetofus.common.api.models.wenet.WeNetUserProfile;
import eu.internetofus.common.api.models.wenet.WeNetUserProfileTestCase;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link PlannedActivity}.
 *
 * @see PlannedActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class WeNetUserProfileIT extends WeNetUserProfileTestCase<WeNetUserProfile> {

	/**
	 * {@inheritDoc}
	 *
	 * @see PlannedActivity#PlannedActivity()
	 */
	@Override
	public WeNetUserProfile createEmptyModel() {

		return new WeNetUserProfile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeProfile(WeNetUserProfile profile, Vertx vertx, VertxTestContext testContext,
			Handler<AsyncResult<WeNetUserProfile>> storeHandler) {

		ProfilesRepository.createProxy(vertx).storeProfile(profile, storeHandler);

	}

}
