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

package eu.internetofus.wenet_profile_manager.api.operations;

import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.model.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The definition of the web services to manage the diversity between
 * {@link WeNetUserProfile}s.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Operations.PATH)
@Tag(name = "Operations")
@WebApiServiceGen
public interface Operations {

  /**
   * The path to the diversity resource.
   */
  String PATH = "/operations";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.operations";

  /**
   * Calculate the diversity between some users.
   *
   * @param body          the information necessary to calculate the diversity.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/diversity")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Calculate the diversity between some user profiles", description = "Measure how a set of users are diverse")
  @RequestBody(description = "The information to calculate the diversity", required = true, content = @Content(schema = @Schema(implementation = DiversityData.class)))
  @ApiResponse(responseCode = "200", description = "The diversity between the users", content = @Content(schema = @Schema(implementation = DiversityValue.class)))
  @ApiResponse(responseCode = "400", description = "Bad diversity data", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void diversity(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Calculate the similarity between the user attributes and a text.
   *
   * @param body          the information necessary to calculate the similarity.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/similarity")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Calculate the similarity between the attributes of a profile and a text", description = "Measure how the similar is a text to the different attributes of a profile")
  @RequestBody(description = "The information to similarity between the text and the profile", required = true, content = @Content(schema = @Schema(implementation = SimilarityData.class)))
  @ApiResponse(responseCode = "200", description = "The similarity between the profile attributes and a text", content = @Content(schema = @Schema(implementation = SimilarityResult.class)))
  @ApiResponse(responseCode = "400", description = "Bad similarity data", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void similarity(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
