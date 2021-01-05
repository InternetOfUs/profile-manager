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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.Model;
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
