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

package eu.internetofus.wenet_profile_manager.api.personalities;

import static eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension.Asserts.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.requestHeader;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.api.Questionnaire;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Personalities}.
 *
 * @see Personalities
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class PersonalitiesIT {

	/**
	 * Verify that retrieve the personality questionnaire.
	 *
	 * @param lang        language to obtain the questionnaire.
	 * @param client      to connect to the server.
	 * @param testContext context to test.
	 *
	 * @see Personalities#retrievePersonalityQuestionnaire(io.vertx.ext.web.api.OperationRequest,
	 *      Handler)
	 */
	@ParameterizedTest(name = "Should return personality questionnaire for language {0}")
	@EmptySource
	@ValueSource(strings = { "*", "en", "es", "ca", "es-US,es;q=0.5", "ca,es,en", "en-US,en,es", "it" })
	public void shouldRetrievePersonalityQuestionnaire(String lang, WebClient client, VertxTestContext testContext) {

		testRequest(client, HttpMethod.GET, Personalities.PATH).with(requestHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,es"))
				.expect(res -> {

					assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
					final Questionnaire questionnaire = assertThatBodyIs(Questionnaire.class, res);
					assertThat(questionnaire.questions).hasSize(20);
					testContext.completeNow();

				}).send(testContext);

	}
}
