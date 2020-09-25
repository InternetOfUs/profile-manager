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

package eu.internetofus.wenet_profile_manager.api.profiles;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.components.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

/**
 * The definition of the web services to manage the user identifiers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(UserIds.PATH)
@Tag(name = "User identifiers")
@WebApiServiceGen
public interface UserIds {

  /**
   * The path to the user identifiers resource.
   */
  String PATH = "/userIds";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.userIds";

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
  void retrieveProfileUserIdsPage(@DefaultValue("0") @QueryParam(value = "offset") @Parameter(description = "The index of the first user identifier to return.", example = "4", required = false) int offset,
      @DefaultValue("10000") @QueryParam(value = "limit") @Parameter(description = "The number maximum of user identifiers to return", example = "100", required = false) int limit,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
