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

package eu.internetofus.wenet_profile_manager.api.personalities;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.wenet_profile_manager.api.Questionnaire;
import eu.internetofus.wenet_profile_manager.api.QuestionnaireAnswers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;

/**
 * The definition of the web services to manage the user personality.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Personalities.PATH)
@Tag(name = "Personalities")
@WebApiServiceGen
public interface Personalities {

  /**
   * The path to the personalities resource.
   */
  String PATH = "/personalities";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.personalities";

  /**
   * Called when want the personality questionnaire.
   *
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Obtain the personality test", description = "Return the questionnaire used to obtain the personality of a person")
  @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, description = "The preferred language for the text on the questionnaire. If it is not available the texts will be on English.", example = "en", schema = @Schema(type = "string", defaultValue = "en"))
  @ApiResponse(responseCode = "200", description = "The questionnaire to evaluate the personality of a person", content = @Content(schema = @Schema(implementation = Questionnaire.class)))
  void retrievePersonalityQuestionnaire(@Parameter(hidden = true, required = false) ServiceRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to evaluate the personality of a person.
   *
   * @param body          the selected answer values to the questionnaire.
   *
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Calculate the personality of a person", description = "Evaluate the answers to the personality test to obtain the personality of the person")
  @RequestBody(description = "The values of the answers that the person has selected on the personality questionnaire.", required = true, content = @Content(schema = @Schema(implementation = QuestionnaireAnswers.class)))
  @ApiResponse(responseCode = "200", description = "The personality of the person", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/7af902b41c0d088f33ba35efd095624aa8aa6a6a/sources/wenet-models-openapi.yaml#/components/schemas/Meaning"))))
  @ApiResponse(responseCode = "404", description = "If it can not calculate the personality", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void calculatePersonality(@Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) ServiceRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
