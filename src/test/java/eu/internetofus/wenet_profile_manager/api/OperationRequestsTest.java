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

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;

/**
 * Test the {@link OperationRequests}.
 *
 * @see OperationRequests
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OperationRequestsTest {

	/**
	 * Check return the default language.
	 *
	 * @param acceptLanguage header value.
	 */
	@ParameterizedTest(name = "Should return the default value for the Accept-Language= {0}")
	@NullAndEmptySource
	@ValueSource(strings = { "*", "es", "es-US,es;q=0.5" })
	public void shouldAcceptLanguageByTheDefault(String acceptLanguage) {

		final JsonObject headers = new JsonObject();
		if (acceptLanguage != null) {

			headers.put(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage);
		}
		final OperationRequest request = new OperationRequest(new JsonObject().put("headers", headers));
		assertThat(OperationRequests.acceptedLanguageIn(request, "en", "ca")).isEqualTo("en");

	}

}