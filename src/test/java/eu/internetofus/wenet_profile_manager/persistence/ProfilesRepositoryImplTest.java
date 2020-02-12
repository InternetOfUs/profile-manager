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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link ProfilesRepositoryImpl}.
 *
 * @see ProfilesRepositoryImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesRepositoryImplTest {

	/**
	 * The repository to do the tests.
	 */
	protected ProfilesRepositoryImpl repository;

	/**
	 * Create the repository to use in the tests.
	 *
	 * @param pool that create the mongo connections.
	 */
	@BeforeEach
	public void cerateRepository(MongoClient pool) {

		this.repository = new ProfilesRepositoryImpl(pool);

	}

	/**
	 * Verify that can not found a profile if it is not defined.
	 *
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldNotFoundUndefinedProfile(VertxTestContext testContext) {

		this.repository.searchProfile("undefined profile identifier", search -> {

			if (search.failed()) {

				testContext.failNow(search.cause());

			} else {

				assertThat(search.result()).isNull();
				testContext.completeNow();
			}

		});
	}

}
