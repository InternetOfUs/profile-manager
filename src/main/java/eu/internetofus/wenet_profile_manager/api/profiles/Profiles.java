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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.internetofus.wenet_profile_manager.api.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
 * The definition of the web services to manage the {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Profiles.PATH)
@Tag(name = "Profiles")
@WebApiServiceGen
public interface Profiles {

	/**
	 * The path to the version resource.
	 */
	String PATH = "/profiles";

	/**
	 * The sub path to retrieve a profile.
	 */
	String PROFILE_ID_PATH = "/{profileId}";

	/**
	 * An example of a {@link WeNetUserProfile} that can be created.
	 */
	String PROFILE_TO_CREATE_EXAMPLE = "{\"name\":{\"prefix\":null,\"first\":\"User\",\"middle\":null,\"last\":\"1\",\"suffix\":null},\"dateOfBirth\":{\"year\":1976,\"month\":4,\"day\":1},\"gender\":\"F\",\"email\":\"user1@internetofus.eu\",\"phoneNumber\":\"+34987654321\",\"locale\":\"es_ES\",\"avatar\":\"avatar_1\",\"nationality\":\"Spanish\",\"occupation\":null,\"personalBehaviors\":[],\"_creationTs\":0,\"_lastUpdateTs\":1234567992,\"languages\":[],\"norms\":[],\"plannedActivities\":[],\"relevantLocations\":[],\"relationships\":[],\"socialPractices\":[]}";

	/**
	 * An example of a {@link WeNetUserProfile} that can be used.
	 */
	String PROFILE_EXAMPLE = "{\"id\":\"1\",\"name\":{\"prefix\":null,\"first\":\"User\",\"middle\":null,\"last\":\"1\",\"suffix\":null},\"dateOfBirth\":{\"year\":1976,\"month\":4,\"day\":1},\"gender\":\"F\",\"email\":\"user1@internetofus.eu\",\"phoneNumber\":\"+34987654321\",\"locale\":\"es_ES\",\"avatar\":\"avatar_1\",\"nationality\":\"Spanish\",\"occupation\":null,\"personalBehaviors\":[],\"_creationTs\":0,\"_lastUpdateTs\":1234567992,\"languages\":[],\"norms\":[],\"plannedActivities\":[],\"relevantLocations\":[],\"relationships\":[],\"socialPractices\":[]}";

	/**
	 * An example of a {@link WeNetUserProfile} that can be used to update.
	 */
	String PROFILE_TO_UPDATE_EXAMPLE = "{\"name\":{\"prefix\":null,\"first\":\"User\",\"middle\":null,\"last\":\"1\",\"suffix\":null},\"dateOfBirth\":{\"year\":1976,\"month\":4,\"day\":1},\"gender\":\"F\",\"email\":\"user1@internetofus.eu\",\"phoneNumber\":\"+34987654321\",\"locale\":\"es_ES\",\"avatar\":\"avatar_1\",\"nationality\":\"Spanish\",\"occupation\":null,\"personalBehaviors\":[],\"_creationTs\":0,\"_lastUpdateTs\":1234567992,\"languages\":[],\"norms\":[],\"plannedActivities\":[],\"relevantLocations\":[],\"relationships\":[],\"socialPractices\":[]}";

	/**
	 * The address of this service.
	 */
	String ADDRESS = "wenet_profile_manager.api.profiles";

	/**
	 * Called when want to create an user profile.
	 *
	 * @param body          the new profile to create.
	 * @param context       of the request.
	 * @param resultHandler to inform of the response.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "createProfile",
			summary = "Create a profile",
			description = "Create a new WeNet user profile")
	@RequestBody(
			description = "The new profile to create",
			required = true,
			content = @Content(
					schema = @Schema(
							ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/WeNetUserProfile"),
					examples = { @ExampleObject(value = PROFILE_TO_CREATE_EXAMPLE) }))
	@ApiResponse(
			responseCode = "200",
			description = "The created profile",
			content = @Content(
					schema = @Schema(
							ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/WeNetUserProfile"),
					examples = { @ExampleObject(name = "CreatedProfile", value = PROFILE_EXAMPLE) }))
	@ApiResponse(
			responseCode = "400",
			description = "Bad profile",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	void createProfile(@Parameter(hidden = true, required = false) JsonObject body,
			@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

	/**
	 * Called when want to get a profile.
	 *
	 * @param profileId     identifier of the profile to get.
	 * @param context       of the request.
	 * @param resultHandler to inform of the response.
	 */
	@GET
	@Path(PROFILE_ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "retrieveProfile",
			summary = "Return a profile associated to the identifier",
			description = "Allow to get a profile associated to an identifier")
	@ApiResponse(
			responseCode = "200",
			description = "The profile associated to the identifier",
			content = @Content(
					schema = @Schema(
							ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/WeNetUserProfile"),
					examples = { @ExampleObject(name = "FoundProfile", value = PROFILE_EXAMPLE) }))
	@ApiResponse(
			responseCode = "404",
			description = "Not found profile",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	void retrieveProfile(
			@PathParam("profileId") @Parameter(
					description = "The identifier of the profile to get",
					example = "15837028-645a-4a55-9aaf-ceb846439eba") String profileId,
			@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

	/**
	 * Called when want to modify a profile.
	 *
	 * @param profileId     identifier of the profile to modify.
	 * @param body          the new profile attributes.
	 * @param context       of the request.
	 * @param resultHandler to inform of the response.
	 */
	@PUT
	@Path(PROFILE_ID_PATH)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "updateProfile",
			summary = "Modify a profile",
			description = "Change the attributes of a profile")
	@RequestBody(
			description = "The new values for the profile",
			required = true,
			content = @Content(
					schema = @Schema(
							ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/WeNetUserProfile"),
					examples = { @ExampleObject(value = PROFILE_TO_UPDATE_EXAMPLE) }))
	@ApiResponse(
			responseCode = "200",
			description = "The updated profile",
			content = @Content(
					schema = @Schema(
							ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/WeNetUserProfile"),
					examples = { @ExampleObject(name = "UpdatedProfile", value = PROFILE_EXAMPLE) }))
	@ApiResponse(
			responseCode = "400",
			description = "Bad profile",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	@ApiResponse(
			responseCode = "404",
			description = "Not found profile",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	void updateProfile(
			@PathParam("profileId") @Parameter(
					description = "The identifier of the profile to update",
					example = "15837028-645a-4a55-9aaf-ceb846439eba") String profileId,
			@Parameter(hidden = true, required = false) JsonObject body,
			@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

	/**
	 * Called when want to delete a profile.
	 *
	 * @param profileId     identifier of the profile to delete.
	 * @param context       of the request.
	 * @param resultHandler to inform of the response.
	 */
	@DELETE
	@Path("/{profileId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Delete the profile associated to the identifier",
			description = "Allow to delete a profile associated to an identifier")
	@ApiResponse(
			responseCode = "200",
			description = "The deleted profile associated to the identifier",
			content = @Content(
					schema = @Schema(
							ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/WeNetUserProfile")))
	@ApiResponse(
			responseCode = "404",
			description = "Not found profile",
			content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
	void deleteProfile(
			@PathParam("profileId") @Parameter(description = "The identifier of the profile to delete") String profileId,
			@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
