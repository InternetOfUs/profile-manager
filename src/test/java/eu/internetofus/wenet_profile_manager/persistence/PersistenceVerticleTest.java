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
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Test the {@link PersistenceVerticle}.
 *
 * @see PersistenceVerticle
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PersistenceVerticleTest {

	/**
	 * Check that not stop the server if it is not started.
	 */
	@Test
	public void shouldNotStopIfServerNotStarted() {

		final PersistenceVerticle persistence = new PersistenceVerticle();
		assertThatCode(() -> persistence.stop()).doesNotThrowAnyException();

	}

	/**
	 * Check that not stop the server if it is not started.
	 */
	@Test
	public void shouldStopIfServerStarted() {

		final PersistenceVerticle persistence = new PersistenceVerticle();
		persistence.pool = MongoClient.create(Vertx.vertx(), new JsonObject());
		assertThatCode(() -> persistence.stop()).doesNotThrowAnyException();
		assertThat(persistence.pool).isNull();

	}

}
