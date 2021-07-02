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

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.model.Model;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link QuestionnaireResources}.
 *
 * @see QuestionnaireResources
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class QuestionnaireResourcesTest {

  /**
   * Check fail retrieve questionnaire because it can not get it.
   *
   * @param vertx       environment to execute the actions.
   * @param testContext test context.
   */
  @Test
  public void shouldReplyWithErrorBecauseCannotObtainQuestionnaire(final Vertx vertx, final VertxTestContext testContext) {

    final var request = new ServiceRequest();
    QuestionnaireResources.retrieveQuestionnaire(lang -> lang, vertx, request, testContext.succeeding(retrieve -> {

      assertThat(retrieve.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      testContext.completeNow();
    }));

  }

  /**
   * Check fail retrieve questionnaire because it can not get it.
   *
   * @param vertx       environment to execute the actions.
   * @param testContext test context.
   */
  @Test
  public void shouldReplyQuestionnaire(final Vertx vertx, final VertxTestContext testContext) {

    final var request = new ServiceRequest();
    QuestionnaireResources.retrieveQuestionnaire(lang -> "eu/internetofus/wenet_profile_manager/api/Questionnaire.100.json", vertx, request, testContext.succeeding(retrieve -> {

      assertThat(retrieve.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
      final var questionnaire = Model.fromBuffer(retrieve.getPayload(), Questionnaire.class);
      assertThat(questionnaire).isEqualTo(new QuestionnaireTest().createModelExample(100));
      testContext.completeNow();
    }));

  }

}
