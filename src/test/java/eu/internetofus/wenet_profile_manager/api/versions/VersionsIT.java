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

package eu.internetofus.wenet_profile_manager.api.versions;

import static eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension.Asserts.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Versions}.
 *
 * @see Versions
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class VersionsIT {

	/**
	 * Verify that return the version.
	 *
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 */
	@Test
	public void shouldReturnVersion(WebClient client, VertxTestContext testContext) {

		testRequest(client, HttpMethod.GET, Versions.PATH).expect(res -> {
			final Version version = assertThatBodyIs(Version.class, res);
			assertThat(version.api).isNotEmpty();
			assertThat(version.software).isNotEmpty();
			assertThat(version.vendor).isNotEmpty();
		}).send(testContext);
	}
}
