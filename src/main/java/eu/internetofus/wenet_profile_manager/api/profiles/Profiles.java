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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.profiles";

  /**
   * An example of a {@link WeNetUserProfile} that can be created.
   */
  String PROFILE_TO_CREATE_EXAMPLE = "{\"name\":{\"prefix\":\"Dr.\",\"first\":\"John\",\"middle\":\"Fidgerald\",\"last\":\"Kenedy\",\"suffix\":\"Jr\"},\"dateOfBirth\":{\"year\":1973,\"month\":2,\"day\":24},\"gender\":\"M\",\"email\":\"jfk@president.gov\",\"phoneNumber\":\"+1202-456-1111\",\"locale\":\"en_US\",\"avatar\":\"https://upload.wikimedia.org/wikipedia/commons/1/1e/JFK_White_House_portrait_looking_up_lighting_corrected.jpg\",\"nationality\":\"American\",\"languages\":[{\"name\":\"English\",\"code\":\"en\",\"level\":\"C2\"}],\"occupation\":\"President\",\"norms\":[],\"plannedActivities\":[{\"startTime\":\"2017-07-21T17:32:03Z\",\"endTime\":\"2019-07-21T17:32:23Z\",\"description\":\"Visit Marilyn\",\"status\":\"cancelled\"},{\"startTime\":\"2017-07-21T17:32:03Z\",\"endTime\":\"2019-07-21T17:32:23Z\",\"description\":\"Go to Dallas\",\"status\":\"confirmed\"}],\"relevantLocations\":[{\"label\":\"White house\",\"latitude\":38.897957,\"longitude\":-77.03656}],\"relationships\":[],\"socialPractices\":[],\"personalBehaviors\":[]}";

  /**
   * An example of a {@link WeNetUserProfile} that can be used.
   */
  String PROFILE_EXAMPLE = "{\"id\":\"5e4ced814e1dc208a5f7bd25\",\"name\":{\"prefix\":\"Dr.\",\"first\":\"John\",\"middle\":\"Fidgerald\",\"last\":\"Kenedy\",\"suffix\":\"Jr\"},\"dateOfBirth\":{\"year\":1973,\"month\":2,\"day\":24},\"gender\":\"M\",\"email\":\"jfk@president.gov\",\"phoneNumber\":\"+1 202-456-1111\",\"locale\":\"en_US\",\"avatar\":\"https://upload.wikimedia.org/wikipedia/commons/1/1e/JFK_White_House_portrait_looking_up_lighting_corrected.jpg\",\"nationality\":\"American\",\"languages\":[{\"name\":\"English\",\"code\":\"en\",\"level\":\"C2\"}],\"occupation\":\"President\",\"plannedActivities\":[{\"id\":\"ef06fe75-e8bd-41ca-9624-f60289c11de4\",\"startTime\":\"2017-07-21T17:32:03Z\",\"endTime\":\"2019-07-21T17:32:23Z\",\"description\":\"Visit Marilyn\",\"status\":\"cancelled\"},{\"id\":\"b6c9a9da-1832-45ba-9580-a6fef6daa62a\",\"startTime\":\"2017-07-21T17:32:03Z\",\"endTime\":\"2019-07-21T17:32:23Z\",\"description\":\"Go to Dallas\",\"status\":\"confirmed\"}],\"relevantLocations\":[{\"id\":\"f7c4eccc-a654-4b6f-ab82-fd01cd2d47e3\",\"label\":\"White house\",\"latitude\":38.897957,\"longitude\":-77.03656}],\"_creationTs\":1582099841815,\"_lastUpdateTs\":1582099841815}";

  /**
   * An example of a {@link WeNetUserProfile} that can be used to update.
   */
  String PROFILE_TO_UPDATE_EXAMPLE = "{\"name\":{\"prefix\":\"\",\"first\":null,\"middle\":null,\"last\":null,\"suffix\":\"III\"},\"dateOfBirth\":{\"year\":1979,\"month\":6,\"day\":4},\"email\":\"jfk3@ex.president.gov\",\"languages\":[{\"name\":\"English\",\"code\":\"en\",\"level\":\"C2\"},{\"name\":\"French\",\"code\":\"fr\",\"level\":\"B2\"}],\"occupation\":\"Conferenciant\"}";

  /**
   * The path to the profile past attributes resource.
   */
  String HISTORIC_PATH = "/historic";

  /**
   * The path to edit the norms of a profile.
   */
  String NORMS_PATH = "/norms";

  /**
   * The path to the planned activities of a profile.
   */
  String PLANNED_ACTIVITIES_PATH = "/plannedActivities";

  /**
   * The path to the relevant locations of a profile.
   */
  String RELEVANT_LOCATIONS_PATH = "/relevantLocations";

  /**
   * The path to the relationships of a profile.
   */
  String RELATIONSHIPS_PATH = "/relationships";

  /**
   * The path to the social practices of a profile.
   */
  String SOCIAL_PRACTICES_PATH = "/socialPractices";

  /**
   * The path to the personal behaviors of a profile.
   */
  String PERSONAL_BEHAVIORS_PATH = "/personalBehaviors";

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
  @Operation(summary = "Create a profile", description = "Create a new WeNet user profile")
  @RequestBody(description = "The new profile to create", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(value = PROFILE_TO_CREATE_EXAMPLE) }))
  @ApiResponse(responseCode = "200", description = "The created profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(name = "CreatedProfile", value = PROFILE_EXAMPLE) }))
  @ApiResponse(responseCode = "400", description = "Bad profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void createProfile(@Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a profile.
   *
   * @param userId        identifier of the user to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a profile", description = "Allow to get the profile with the specified identifier")
  @ApiResponse(responseCode = "200", description = "The profile associated to the identifier", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(name = "FoundProfile", value = PROFILE_EXAMPLE) }))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void retrieveProfile(@PathParam("userId") @Parameter(description = "The identifier of the user to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to modify a profile.
   *
   * @param userId        identifier of the user to modify.
   * @param body          the new profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Modify a profile", description = "Change a profile")
  @RequestBody(description = "The new profile", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(value = PROFILE_TO_UPDATE_EXAMPLE) }))
  @ApiResponse(responseCode = "200", description = "The updated profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(name = "UpdatedProfile", value = PROFILE_EXAMPLE) }))
  @ApiResponse(responseCode = "400", description = "Bad profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void updateProfile(@PathParam("userId") @Parameter(description = "The identifier of the user to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to modify partially a profile.
   *
   * @param userId        identifier of the user to modify.
   * @param body          the new profile attributes.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Modify partially a profile", description = "Change some attributes of a profile")
  @RequestBody(description = "The new values for the profile", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(value = PROFILE_TO_UPDATE_EXAMPLE) }))
  @ApiResponse(responseCode = "200", description = "The merged profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(name = "UpdatedProfile", value = PROFILE_EXAMPLE) }))
  @ApiResponse(responseCode = "400", description = "Bad profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void mergeProfile(@PathParam("userId") @Parameter(description = "The identifier of the user to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a profile.
   *
   * @param userId        identifier of the user to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Delete a profiler", description = "Allow to delete a profile with an specific identifier")
  @ApiResponse(responseCode = "204", description = "The profile was deleted successfully")
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void deleteProfile(@PathParam("userId") @Parameter(description = "The identifier of the user to delete") String userId, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get the historic values of the profile.
   *
   * @param userId        identifier of the user to get the historic values.
   * @param from          the minimum time stamp that define the range the profile is active.
   * @param to            the maximum time stamp that define the range the profile is active.
   * @param order         of the profiles to return.
   * @param offset        index of the first task to return.
   * @param limit         number maximum of tasks to return.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + HISTORIC_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get the status of the profile in specific time period", description = "Allow to obtain the historic of profile changes")
  @ApiResponse(responseCode = "200", description = "The found profiles", content = @Content(schema = @Schema(implementation = HistoricWeNetUserProfilesPage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Historic")
  void retrieveProfileHistoricPage(@PathParam("userId") @Parameter(description = "The identifier of the user to get the historic profiles") String userId,
      @QueryParam(value = "from") @Parameter(description = "The difference, measured in seconds, between the minimum time stamp when the profile was enabled and midnight, January 1, 1970 UTC.", example = "1457166440", required = false) Long from,
      @QueryParam(value = "to") @Parameter(description = "The difference, measured in seconds, between the maximum time stamp when the profile was enabled and midnight, January 1, 1970 UTC.", example = "1571664406", required = false) Long to,
      @QueryParam(value = "order") @Parameter(description = "The order in witch has to return the profiles. From the newest to the oldest (descendant '-') or from the oldest to the newest (ascendant '+').", required = false, schema = @Schema(allowableValues = {
          "-", "+" }, defaultValue = "+", example = "-")) String order,
      @DefaultValue("0") @QueryParam(value = "offset") @Parameter(description = "The index of the first task type to return.", example = "4", required = false) int offset,
      @DefaultValue("10") @QueryParam(value = "limit") @Parameter(description = "The number maximum of task types to return", example = "100", required = false) int limit,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a norm into a profile.
   *
   * @param userId        identifier of the user for the profile to add the norm.
   * @param body          norm to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + NORMS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a norm into a profile", description = "Insert a new norm into a profile")
  @RequestBody(description = "The new norm", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "201", description = "The added norm into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void addNorm(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the norm", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the norm from a profile.
   *
   * @param userId        identifier of the user for the profile to get all norms.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + NORMS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the norms from a profile", description = "Allow to get all the norms defined into a profile")
  @ApiResponse(responseCode = "200", description = "The norms defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void retrieveNorms(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + NORMS_PATH + "/{normId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a norm from a profile", description = "Allow to get a norm defined into a profile")
  @ApiResponse(responseCode = "200", description = "The norm defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void retrieveNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to update.
   * @param body          the new values for the norm.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}" + NORMS_PATH + "/{normId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a norm from a profile", description = "Allow to modify a norm defined into a profile")
  @RequestBody(description = "The new values to update the norm", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "200", description = "The updated norm", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void updateNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to merge.
   * @param body          the new values for the norm.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + NORMS_PATH + "/{normId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a norm from a profile", description = "Allow to modify parts of a norm defined into a profile")
  @RequestBody(description = "The new values to merge the norm", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "200", description = "The current values of the norm after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void mergeNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + NORMS_PATH + "/{normId}")
  @Operation(summary = "Delete a norm from a profile", description = "Allow to delete a defined norm from a profile")
  @ApiResponse(responseCode = "204", description = "The norm defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void deleteNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a planned activity into a profile.
   *
   * @param userId        identifier of the user for the profile to add the plannedActivity.
   * @param body          planned activity to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + PLANNED_ACTIVITIES_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a planned activity into a profile", description = "Insert a new planned activity into a profile")
  @RequestBody(description = "The new planned activity", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "201", description = "The added planned activity into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "400", description = "Bad planned activity to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void addPlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the planned activity", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the planned activity from a profile.
   *
   * @param userId        identifier of the user for the profile to get all plannedactivities.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PLANNED_ACTIVITIES_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the planned activities from a profile", description = "Allow to get all the planned activities defined into a profile")
  @ApiResponse(responseCode = "200", description = "The planned activities defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void retrievePlannedActivities(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to get.
   * @param context           of the request.
   * @param resultHandler     to inform of the response.
   */
  @GET
  @Path("/{userId}" + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a planned activity from a profile", description = "Allow to get a planned activity defined into a profile")
  @ApiResponse(responseCode = "200", description = "The planned activity defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void retrievePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to update.
   * @param body              the new values for the planned activity.
   * @param context           of the request.
   * @param resultHandler     to inform of the response.
   */
  @PUT
  @Path("/{userId}" + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a planned activity from a profile", description = "Allow to modify a planned activity defined into a profile")
  @RequestBody(description = "The new values to update the planned activity", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "200", description = "The updated planned activity", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "400", description = "Bad planned activity to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void updatePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to merge.
   * @param body              the new values for the planned activity.
   * @param context           of the request.
   * @param resultHandler     to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a planned activity from a profile", description = "Allow to modify parts of a planned activity defined into a profile")
  @RequestBody(description = "The new values to merge the planned activity", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "200", description = "The current values of the planned activity after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "400", description = "Bad planned activity to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void mergePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to delete.
   * @param context           of the request.
   * @param resultHandler     to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Operation(summary = "Delete a planned activity from a profile", description = "Allow to delete a defined planned activity from a profile")
  @ApiResponse(responseCode = "204", description = "The planned activity defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void deletePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a relevant location into a profile.
   *
   * @param userId        identifier of the user for the profile to add the relevantLocation.
   * @param body          relevant location to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + RELEVANT_LOCATIONS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a relevant location into a profile", description = "Insert a new relevant location into a profile")
  @RequestBody(description = "The new relevant location", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "201", description = "The added relevant location into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "400", description = "Bad relevant location to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void addRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the relevant location", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the relevant location from a profile.
   *
   * @param userId        identifier of the user for the profile to get all relevantlocations.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + RELEVANT_LOCATIONS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the relevant locations from a profile", description = "Allow to get all the relevant locations defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relevant locations defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void retrieveRelevantLocations(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to get.
   * @param context            of the request.
   * @param resultHandler      to inform of the response.
   */
  @GET
  @Path("/{userId}" + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a relevant location from a profile", description = "Allow to get a relevant location defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relevant location defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void retrieveRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to update.
   * @param body               the new values for the relevant location.
   * @param context            of the request.
   * @param resultHandler      to inform of the response.
   */
  @PUT
  @Path("/{userId}" + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a relevant location from a profile", description = "Allow to modify a relevant location defined into a profile")
  @RequestBody(description = "The new values to update the relevant location", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "200", description = "The updated relevant location", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "400", description = "Bad relevant location to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void updateRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to merge.
   * @param body               the new values for the relevant location.
   * @param context            of the request.
   * @param resultHandler      to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a relevant location from a profile", description = "Allow to modify parts of a relevant location defined into a profile")
  @RequestBody(description = "The new values to merge the relevant location", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "200", description = "The current values of the relevant location after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "400", description = "Bad relevant location to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void mergeRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to delete.
   * @param context            of the request.
   * @param resultHandler      to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Operation(summary = "Delete a relevant location from a profile", description = "Allow to delete a defined relevant location from a profile")
  @ApiResponse(responseCode = "204", description = "The relevant location defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void deleteRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a relationship into a profile.
   *
   * @param userId        identifier of the user for the profile to add the relationship.
   * @param body          relationship to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + RELATIONSHIPS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a relationship into a profile", description = "Insert a new relationship into a profile")
  @RequestBody(description = "The new relationship", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "201", description = "The added relationship into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void addRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the relationship", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the relationship from a profile.
   *
   * @param userId        identifier of the user for the profile to get all relationships.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + RELATIONSHIPS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the relationships from a profile", description = "Allow to get all the relationships defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relationships defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void retrieveRelationships(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a relationship from a profile", description = "Allow to get a relationship defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relationship defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void retrieveRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the relationship to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to update.
   * @param body          the new values for the relationship.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}" + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a relationship from a profile", description = "Allow to modify a relationship defined into a profile")
  @RequestBody(description = "The new values to update the relationship", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "200", description = "The updated relationship", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void updateRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the relationship to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to merge.
   * @param body          the new values for the relationship.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a relationship from a profile", description = "Allow to modify parts of a relationship defined into a profile")
  @RequestBody(description = "The new values to merge the relationship", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "200", description = "The current values of the relationship after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void mergeRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The identifier of the relationship to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a relationship from a profile", description = "Allow to delete a defined relationship from a profile")
  @ApiResponse(responseCode = "204", description = "The relationship defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void deleteRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the relationship to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a social practice into a profile.
   *
   * @param userId        identifier of the user for the profile to add the social practice.
   * @param body          social practice to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + SOCIAL_PRACTICES_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a social practice into a profile", description = "Insert a new social practice into a profile")
  @RequestBody(description = "The new social practice", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "201", description = "The added social practice into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "Bad social practice to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void addSocialPractice(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the social practice", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the social practice from a profile.
   *
   * @param userId        identifier of the user for the profile to get all social practices.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + SOCIAL_PRACTICES_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the social practices from a profile", description = "Allow to get all the social practices defined into a profile")
  @ApiResponse(responseCode = "200", description = "The social practices defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void retrieveSocialPractices(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a social practice from a profile.
   *
   * @param userId           identifier of the user for the profile where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to get.
   * @param context          of the request.
   * @param resultHandler    to inform of the response.
   */
  @GET
  @Path("/{userId}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a social practice from a profile", description = "Allow to get a social practice defined into a profile")
  @ApiResponse(responseCode = "200", description = "The social practice defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "404", description = "Not found profile or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void retrieveSocialPractice(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a social practice from a profile.
   *
   * @param userId           identifier of the user for the profile where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to update.
   * @param body             the new values for the social practice.
   * @param context          of the request.
   * @param resultHandler    to inform of the response.
   */
  @PUT
  @Path("/{userId}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a social practice from a profile", description = "Allow to modify a social practice defined into a profile")
  @RequestBody(description = "The new values to update the social practice", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "200", description = "The updated social practice", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "Bad social practice to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void updateSocialPractice(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a social practice from a profile.
   *
   * @param userId           identifier of the user for the profile where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to merge.
   * @param body             the new values for the social practice.
   * @param context          of the request.
   * @param resultHandler    to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a social practice from a profile", description = "Allow to modify parts of a social practice defined into a profile")
  @RequestBody(description = "The new values to merge the social practice", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "200", description = "The current values of the social practice after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "Bad social practice to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void mergeSocialPractice(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to merge", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a social practice from a profile.
   *
   * @param userId           identifier of the user for the profile where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to delete.
   * @param context          of the request.
   * @param resultHandler    to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Operation(summary = "Delete a social practice from a profile", description = "Allow to delete a defined social practice from a profile")
  @ApiResponse(responseCode = "204", description = "The social practice defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void deleteSocialPractice(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a personal behavior into a profile.
   *
   * @param userId        identifier of the user for the profile to add the personalBehavior.
   * @param body          personal behavior to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a personal behavior into a profile", description = "Insert a new personal behavior into a profile")
  @RequestBody(description = "The new personal behavior", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "201", description = "The added personal behavior into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "400", description = "Bad personal behavior to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void addPersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the personal behavior", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile to get all personalbehaviors.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the personal behaviors from a profile", description = "Allow to get all the personal behaviors defined into a profile")
  @ApiResponse(responseCode = "200", description = "The personal behaviors defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void retrievePersonalBehaviors(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a personal behavior from a profile", description = "Allow to get a personal behavior defined into a profile")
  @ApiResponse(responseCode = "200", description = "The personal behavior defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void retrievePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to update.
   * @param body          the new values for the personal behavior.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a personal behavior from a profile", description = "Allow to modify a personal behavior defined into a profile")
  @RequestBody(description = "The new values to update the personal behavior", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "200", description = "The updated personal behavior", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "400", description = "Bad personal behavior to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void updatePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to merge.
   * @param body          the new values for the personal behavior.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a personal behavior from a profile", description = "Allow to modify parts of a personal behavior defined into a profile")
  @RequestBody(description = "The new values to merge the personal behavior", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "200", description = "The current values of the personal behavior after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "400", description = "Bad personal behavior to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void mergePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a personal behavior from a profile", description = "Allow to delete a defined personal behavior from a profile")
  @ApiResponse(responseCode = "204", description = "The personal behavior defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void deletePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);


  /**
   * Called when want to add a material into a profile.
   *
   * @param userId        identifier of the user for the profile to add the material.
   * @param body          material to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a material into a profile", description = "Insert a new material into a profile")
  @RequestBody(description = "The new material", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "201", description = "The added material into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "400", description = "Bad material to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void addMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the material", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the material from a profile.
   *
   * @param userId        identifier of the user for the profile to get all materials.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the materials from a profile", description = "Allow to get all the materials defined into a profile")
  @ApiResponse(responseCode = "200", description = "The materials defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void retrieveMaterials(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a material from a profile", description = "Allow to get a material defined into a profile")
  @ApiResponse(responseCode = "200", description = "The material defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void retrieveMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to update.
   * @param body          the new values for the material.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a material from a profile", description = "Allow to modify a material defined into a profile")
  @RequestBody(description = "The new values to update the material", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "200", description = "The updated material", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "400", description = "Bad material to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void updateMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to merge.
   * @param body          the new values for the material.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a material from a profile", description = "Allow to modify parts of a material defined into a profile")
  @RequestBody(description = "The new values to merge the material", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "200", description = "The current values of the material after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "400", description = "Bad material to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void mergeMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a material from a profile", description = "Allow to delete a defined material from a profile")
  @ApiResponse(responseCode = "204", description = "The material defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void deleteMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a competence into a profile.
   *
   * @param userId        identifier of the user for the profile to add the competence.
   * @param body          competence to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a competence into a profile", description = "Insert a new competence into a profile")
  @RequestBody(description = "The new competence", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "201", description = "The added competence into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "400", description = "Bad competence to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void addCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the competence", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the competence from a profile.
   *
   * @param userId        identifier of the user for the profile to get all competences.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the competences from a profile", description = "Allow to get all the competences defined into a profile")
  @ApiResponse(responseCode = "200", description = "The competences defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void retrieveCompetences(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a competence from a profile", description = "Allow to get a competence defined into a profile")
  @ApiResponse(responseCode = "200", description = "The competence defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void retrieveCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to update.
   * @param body          the new values for the competence.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a competence from a profile", description = "Allow to modify a competence defined into a profile")
  @RequestBody(description = "The new values to update the competence", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "200", description = "The updated competence", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "400", description = "Bad competence to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void updateCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to merge.
   * @param body          the new values for the competence.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a competence from a profile", description = "Allow to modify parts of a competence defined into a profile")
  @RequestBody(description = "The new values to merge the competence", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "200", description = "The current values of the competence after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "400", description = "Bad competence to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void mergeCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a competence from a profile", description = "Allow to delete a defined competence from a profile")
  @ApiResponse(responseCode = "204", description = "The competence defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void deleteCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);


  /**
   * Called when want to add a meaning into a profile.
   *
   * @param userId        identifier of the user for the profile to add the meaning.
   * @param body          meaning to add to the profile.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a meaning into a profile", description = "Insert a new meaning into a profile")
  @RequestBody(description = "The new meaning", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "201", description = "The added meaning into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "400", description = "Bad meaning to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void addMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the meaning", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the meaning from a profile.
   *
   * @param userId        identifier of the user for the profile to get all meanings.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the meanings from a profile", description = "Allow to get all the meanings defined into a profile")
  @ApiResponse(responseCode = "200", description = "The meanings defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void retrieveMeanings(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to get.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a meaning from a profile", description = "Allow to get a meaning defined into a profile")
  @ApiResponse(responseCode = "200", description = "The meaning defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void retrieveMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to update.
   * @param body          the new values for the meaning.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a meaning from a profile", description = "Allow to modify a meaning defined into a profile")
  @RequestBody(description = "The new values to update the meaning", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "200", description = "The updated meaning", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "400", description = "Bad meaning to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void updateMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to merge.
   * @param body          the new values for the meaning.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a meaning from a profile", description = "Allow to modify parts of a meaning defined into a profile")
  @RequestBody(description = "The new values to merge the meaning", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "200", description = "The current values of the meaning after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "400", description = "Bad meaning to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void mergeMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest context, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to delete.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}" + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a meaning from a profile", description = "Allow to delete a defined meaning from a profile")
  @ApiResponse(responseCode = "204", description = "The meaning defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void deleteMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
