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

import java.util.function.Function;

import javax.ws.rs.core.Response.Status;

import eu.internetofus.common.vertx.OperationReponseHandlers;
import eu.internetofus.common.vertx.OperationRequests;
import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

/**
 * Generic component to manage the {@link Questionnaire} as resource.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface QuestionnaireResources {

	/**
	 * Called to retrieve a questionnaire from the resources.
	 *
	 * @param resourceNameGenerator the function used to generate the resource from
	 *                              the language.
	 * @param vertx                 environment to load the questionnaire.
	 * @param context               of the request.
	 * @param resultHandler         to inform of the response.
	 */
	static void retrieveQuestionnaire(Function<String, String> resourceNameGenerator, Vertx vertx,
			OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {

		vertx.<Questionnaire>executeBlocking(promise -> {

			final String lang = OperationRequests.acceptedLanguageIn(context, "en", "en", "es", "ca");
			final String resourceName = resourceNameGenerator.apply(lang);
			final Questionnaire questionnaire = Model.loadFromResource(resourceName, Questionnaire.class);
			if (questionnaire != null) {

				promise.complete(questionnaire);

			} else {
				// In theory never happens because the resources are right
				promise.fail("The resource '" + resourceName + "' does not contains a valid Questionnaire.");
			}

		}, false, load -> {

			if (load.failed()) {

				OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, load.cause());

			} else {

				final Questionnaire questionnaire = load.result();
				OperationReponseHandlers.responseOk(resultHandler, questionnaire);

			}

		});

	}

}
