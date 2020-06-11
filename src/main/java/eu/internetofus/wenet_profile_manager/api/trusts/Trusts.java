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

package eu.internetofus.wenet_profile_manager.api.trusts;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.components.ErrorMessage;
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
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

/**
 * Service used to manage the trust of an user respect another.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Trusts.PATH)
@Tag(name = "Trusts")
@WebApiServiceGen
public interface Trusts {

  /**
   * The path to the version resource.
   */
  String PATH = "/trusts";

  /**
   * The path to add new rating events.
   */
  String RATING_PATH = "/rating";

  /**
   * The path that is used to manipulate the trust between the source and target users.
   */
  String BETWEEN_USERS_TRUST_PATH = "/{sourceId}/with/{targetId}";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.trusts";

  /**
   * Called when want to add a new trust event.
   *
   * @param body          the new trust event to add.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(RATING_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Rate an user performance", description = "Store an event that rating the performance of an user over a task that it has done in WeNet")
  @RequestBody(description = "The event to store", required = true, content = @Content(schema = @Schema(implementation = UserPerformanceRatingEvent.class)))
  @ApiResponse(responseCode = "201", description = "The event has been stored", content = @Content(schema = @Schema(implementation = UserPerformanceRatingEvent.class)))
  @ApiResponse(responseCode = "400", description = "Bad event", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void addTrustEvent(@Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to calculate the trust of an user respect another.
   *
   * @param sourceId      identifier of the user that inform of the trust with another user.
   * @param targetId      identifier of the user that would obtain the trust.
   * @param appId         application identifier to match for the events to use on the calculus.
   * @param communityId   community identifier to match for the events to use on the calculus.
   * @param taskTypeId    task type identifier to match for the events to use on the calculus.
   * @param taskId        task identifier to match for the events to use on the calculus.
   * @param relationship  to match for the events to use on the calculus.
   * @param reportFrom    minimum report time that the events has to be reported to use on the calculus.
   * @param reportTo      maximum report time that the events has to be reported to use on the calculus.
   * @param aggregator    type of trust calculus.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(BETWEEN_USERS_TRUST_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Calculate the trust of an user respect another", description = "Allow to obtain the trust that an user have respect another in general, respect the relationship, on a community or by task type.")
  @ApiResponse(responseCode = "200", description = "The calculated trust", content = @Content(schema = @Schema(implementation = Trust.class)))
  @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void calculateTrust(@PathParam("sourceId") @Parameter(description = "The identifier as reference that want to obtain the trust respect its point of view") String sourceId,
      @PathParam("targetId") @Parameter(description = "The identifier of the user that want to have the trust") String targetId,
      @QueryParam(value = "appId") @Parameter(description = "An application identifier to be equals on the events to use on the calculus. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of the events to use on the calculus if you write between '/'. For example to use the events for the aplications '1' and '2' you must pass as 'appId' '/^[1|2]$/'.", example = "1", required = false) String appId,
      @QueryParam(value = "communityId") @Parameter(description = "An community identifier to be equals on the events to use on the calculus. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of events to use on the calculus if you write between '/'. For example to use the events for the communities '1' and '2' you must pass as 'communityId' '/^[1|2]$/'.", example = "1", required = false) String communityId,
      @QueryParam(value = "taskTypeId") @Parameter(description = "An task type identifier to be equals on the events to use on the calculus. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of events to use on the calculus if you write between '/'. For example to use the events for the task types '1' and '2' you must pass as 'taskTypeId' '/^[1|2]$/'.", example = "1", required = false) String taskTypeId,
      @QueryParam(value = "taskId") @Parameter(description = "An task identifier to be equals on the events to use on the calculus. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of the events to use on the calculus if you write between '/'. For example to use the events for the tasks '1' and '2' you must pass as 'taskId' '/^[1|2]$/'.", example = "1", required = false) String taskId,
      @QueryParam(value = "relationship") @Parameter(description = "A relationship to be equals on the events to use on the calculus. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of the tasks to return if you write between '/'. For example to use the events for the relationships 'friend' and 'family' you must pass as 'relationship' '/^[friend|family]$/'.", example = "1", required = false) String relationship,
      @QueryParam(value = "reportFrom") @Parameter(description = "The difference, measured in seconds, between the minimum report time stamp of the event and midnight, January 1, 1970 UTC.", example = "1457166440", required = false) Long reportFrom,
      @QueryParam(value = "reportTo") @Parameter(description = "The difference, measured in seconds, between the maximum report time stamp of the event and midnight, January 1, 1970 UTC.", example = "1571664406", required = false) Long reportTo,
      @QueryParam(value = "aggregator") @Parameter(description = "The type of aggregation that has to be used to calculate the trust.", required = false, schema = @Schema(implementation = TrustAggregator.class, example = "MAXIMUM")) @DefaultValue("RECENCY_BASED") TrustAggregator aggregator,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
