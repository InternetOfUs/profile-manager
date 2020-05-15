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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.components.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
	 * The path that is used to manipulate the trust between the source and target
	 * users.
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
	@Operation(
			summary = "Rate an user performance",
			description = "Store an event that rating the performance of an user over a task that it has done in WeNet")
	@RequestBody(
			description = "The event to store",
			required = true,
			content = @Content(schema = @Schema(implementation = UserPerformanceRatingEvent.class)))
	@ApiResponse(responseCode = "204", description = "The event has been stored")
	@ApiResponse(
			responseCode = "400",
			description = "Bad event",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	void addTrustEvent(@Parameter(hidden = true, required = false) JsonObject body,
			@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

	/**
	 * Called when want to calculate the trust of an user respect another.
	 *
	 * @param sourceId      identifier of the user that inform of the trust with
	 *                      another user.
	 * @param targetId      identifier of the user that would obtain the trust.
	 * @param context       of the request.
	 * @param resultHandler to inform of the response.
	 */
	@GET
	@Path(BETWEEN_USERS_TRUST_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Calculate the trust of an user respect another",
			description = "Allow to obtain the trust that an user have respect another in general, respect the relationship, on a community or by task type.")
	@Parameter(
			in = ParameterIn.QUERY,
			name = "membership",
			description = "The type of relationship with the user that is calculating the trust. It can be a Perl compatible regular expressions (PCRE) that has to match the relationships on the trust events to aggregate.",
			required = false,
			schema = @Schema(type = "string", example = "friend"))
	@Parameter(
			in = ParameterIn.QUERY,
			name = "communityId",
			description = "The identifier of the community to get the trust. It can be a Perl compatible regular expressions (PCRE) that has to match the community identifiers on the trust events to aggregate.",
			required = false,
			schema = @Schema(type = "string", example = "88cb96277edd"))
	@Parameter(
			in = ParameterIn.QUERY,
			name = "taskTypeId",
			description = "The identifier of the task type to get the trust. It can be a Perl compatible regular expressions (PCRE) that has to match the task type identifiers on the trust events to aggregate.",
			required = false,
			schema = @Schema(type = "string", example = "77edd88cb962"))
	@Parameter(
			in = ParameterIn.QUERY,
			name = "reportFrom",
			description = "The time stamp inclusive that mark the older limit in witch the trust event has reported. It is the difference, measured in seconds, between the time when the trust events were reported and midnight, January 1, 1970 UTC.",
			required = false,
			schema = @Schema(type = "integer", defaultValue = "0", example = "1457166440"))
	@Parameter(
			in = ParameterIn.QUERY,
			name = "reportTo",
			description = "The time stamp inclusive that mark the newest limit in witch the trust event has reported. It is the difference, measured in seconds, between the time when the trust events were reported and midnight, January 1, 1970 UTC.",
			required = false,
			schema = @Schema(type = "integer", defaultValue = "92233720368547757", example = "1571664406"))
	@Parameter(
			in = ParameterIn.QUERY,
			name = "aggregation",
			description = "The type of aggregation that has to be used to calculate the trust.",
			required = false,
			schema = @Schema(
					type = "string",
					defaultValue = "RECENCY_BASED",
					allowableValues = { "RECENCY_BASED", "AVERAGE", "MEDIAN", "MINIMUM", "MAXIMUM" },
					example = "1571664406"))
	@ApiResponse(
			responseCode = "200",
			description = "The calculated trust",
			content = @Content(schema = @Schema(implementation = Trust.class)))
	@ApiResponse(
			responseCode = "400",
			description = "Bad request",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	void calculateTrust(@PathParam("sourceId") @Parameter(
			description = "The identifier as reference that want to obtain the trust respect its point of view") String sourceId,
			@PathParam("targetId") @Parameter(
					description = "The identifier of the user that want to have the trust") String targetId,
			@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
