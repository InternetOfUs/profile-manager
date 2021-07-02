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

package eu.internetofus.wenet_profile_manager.api.user_identifiers;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.model.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;

/**
 * The definition of the web services to obtain the user identifiers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(UserIdentifiers.PATH)
@Tag(name = "User identifiers")
@WebApiServiceGen
public interface UserIdentifiers {

  /**
   * The path to the user identifiers resource.
   */
  String PATH = "/userIdentifiers";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.user_identifiers";

  /**
   * Called when want to get the user identifiers of the profiles.
   *
   * @param offset        index of the first user identifier to return.
   * @param limit         number maximum of user identifiers to return.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the identifiers of the users", description = "Allow to get some user identifiers")
  @ApiResponse(responseCode = "200", description = "The page with the user identifiers", content = @Content(schema = @Schema(implementation = UserIdentifiersPage.class)))
  @ApiResponse(responseCode = "400", description = "If any of the search pattern is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void getUserIdentifiersPage(@DefaultValue("0") @QueryParam("offset") @Parameter(description = "The index of the first task type to return") int offset,
      @DefaultValue("10000") @QueryParam("limit") @Parameter(description = "The number maximum of task types to return") int limit, @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
