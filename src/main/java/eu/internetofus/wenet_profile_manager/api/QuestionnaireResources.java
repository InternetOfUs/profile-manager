/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.wenet_profile_manager.api;

import java.util.function.Function;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.vertx.ServiceRequests;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import javax.ws.rs.core.Response.Status;

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
  static void retrieveQuestionnaire(final Function<String, String> resourceNameGenerator, final Vertx vertx,
      final ServiceRequest context, final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    vertx.<Questionnaire>executeBlocking(promise -> {

      final var lang = ServiceRequests.acceptedLanguageIn(context, "en", "ca", "da", "de", "el", "en", "es", "fr", "he",
          "it");
      final var resourceName = resourceNameGenerator.apply(lang);
      final var questionnaire = Model.loadFromResource(resourceName, Questionnaire.class);
      if (questionnaire != null) {

        promise.complete(questionnaire);

      } else {
        // In theory never happens because the resources are right
        promise.fail("The resource '" + resourceName + "' does not contains a valid Questionnaire.");
      }

    }, false, load -> {

      if (load.failed()) {

        ServiceResponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, load.cause());

      } else {

        final Questionnaire questionnaire = load.result();
        ServiceResponseHandlers.responseOk(resultHandler, questionnaire);

      }

    });

  }

}
