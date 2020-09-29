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
   * The path to the profiles resource.
   */
  String PATH = "/profiles";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.profiles";

  /**
   * An example of a {@link WeNetUserProfile} that can be created.
   */
  String PROFILE_TO_CREATE_EXAMPLE = "{\"name\":{\"prefix\":\"Dr.\",\"first\":\"John\",\"middle\":\"Fidgerald\",\"last\":\"Kenedy\",\"suffix\":\"Jr\"},\"dateOfBirth\":{\"year\":1973,\"month\":2,\"day\":24},\"gender\":\"M\",\"email\":\"jfk@president.gov\",\"phoneNumber\":\"+1202-456-1111\",\"locale\":\"en_US\",\"avatar\":\"https://upload.wikimedia.org/wikipedia/commons/1/1e/JFK_White_House_portrait_looking_up_lighting_corrected.jpg\",\"nationality\":\"American\",\"languages\":[{\"name\":\"English\",\"code\":\"en\",\"level\":\"C2\"}],\"occupation\":\"President\",\"norms\":[],\"plannedActivities\":[{\"startTime\":\"2017-07-21T17:32:03Z\",\"endTime\":\"2019-07-21T17:32:23Z\",\"description\":\"Visit Marilyn\",\"status\":\"cancelled\"},{\"startTime\":\"2017-07-21T17:32:03Z\",\"endTime\":\"2019-07-21T17:32:23Z\",\"description\":\"Go to Dallas\",\"status\":\"confirmed\"}],\"relevantLocations\":[{\"label\":\"White house\",\"latitude\":38.897957,\"longitude\":-77.03656}],\"relationships\":[],\"personalBehaviors\":[]}";

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
   * The path to the personal behaviors of a profile.
   */
  String PERSONAL_BEHAVIORS_PATH = "/personalBehaviors";

  /**
   * The path to the materials of a profile.
   */
  String MATERIALS_PATH = "/materials";

  /**
   * The path to the competences of a profile.
   */
  String COMPETENCES_PATH = "/competences";

  /**
   * The path to the meanings of a profile.
   */
  String MEANINGS_PATH = "/meanings";

  /**
   * The sub path to retrieve a task.
   */
  String USER_ID_PATH = "/{userId:^(?!userIds)}";

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
  @Operation(summary = "Return some profiles", description = "Allow to get some user profiles")
  @ApiResponse(responseCode = "200", description = "The page with the user profiles", content = @Content(schema = @Schema(implementation = WeNetUserProfilesPage.class)))
  @ApiResponse(responseCode = "400", description = "If any of the search pattern is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void retrieveProfilesPage(@DefaultValue("0") @QueryParam(value = "offset") @Parameter(description = "The index of the first user profile to return.", example = "4", required = false) int offset,
      @DefaultValue("10000") @QueryParam(value = "limit") @Parameter(description = "The number maximum of user profiles to return", example = "100", required = false) int limit,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to create an user profile.
   *
   * @param body          the new profile to create.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Create a profile", description = "Create a new WeNet user profile")
  @RequestBody(description = "The new profile to create", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(value = PROFILE_TO_CREATE_EXAMPLE) }))
  @ApiResponse(responseCode = "201", description = "The created profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(name = "CreatedProfile", value = PROFILE_EXAMPLE) }))
  @ApiResponse(responseCode = "400", description = "Bad profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void createProfile(@Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a profile.
   *
   * @param userId        identifier of the user to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a profile", description = "Allow to get the profile with the specified identifier")
  @ApiResponse(responseCode = "200", description = "The profile associated to the identifier", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile"), examples = {
      @ExampleObject(name = "FoundProfile", value = PROFILE_EXAMPLE) }))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void retrieveProfile(@PathParam("userId") @Parameter(description = "The identifier of the user to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to modify a profile.
   *
   * @param userId        identifier of the user to modify.
   * @param body          the new profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH)
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
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to modify partially a profile.
   *
   * @param userId        identifier of the user to modify.
   * @param body          the new profile attributes.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH)
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
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a profile.
   *
   * @param userId        identifier of the user to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Delete a profile", description = "Allow to delete a profile with an specific identifier")
  @ApiResponse(responseCode = "204", description = "The profile was deleted successfully")
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void deleteProfile(@PathParam("userId") @Parameter(description = "The identifier of the user to delete") String userId, @Parameter(hidden = true, required = false) OperationRequest request,
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
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + HISTORIC_PATH)
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
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a norm into a profile.
   *
   * @param userId        identifier of the user for the profile to add the norm.
   * @param body          norm to add to the user profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + NORMS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a norm into an user profile", description = "Insert a new norm into an user profile")
  @RequestBody(description = "The new norm", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "201", description = "The added norm into the user profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found user profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void addProfileNorm(@PathParam("userId") @Parameter(description = "The identifier of the user profile to add the norm", example = "ceb846439eba-645a-4a55-9aaf-15837028") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the norm from a profile.
   *
   * @param userId        identifier of the user for the profile to get all norms.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + NORMS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the norms from a profile", description = "Allow to get all the norms defined into a profile")
  @ApiResponse(responseCode = "200", description = "The norms defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void retrieveProfileNorms(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + NORMS_PATH + "/{normId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a norm from a profile", description = "Allow to get a norm defined into a profile")
  @ApiResponse(responseCode = "200", description = "The norm defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void retrieveProfileNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to update.
   * @param body          the new values for the norm.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + NORMS_PATH + "/{normId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a norm from a profile", description = "Allow to modify a norm defined into a profile")
  @RequestBody(description = "The new values to update the norm", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "200", description = "The updated norm", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void updateProfileNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to merge.
   * @param body          the new values for the norm.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + NORMS_PATH + "/{normId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a norm from a profile", description = "Allow to modify parts of a norm defined into a profile")
  @RequestBody(description = "The new values to merge the norm", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "200", description = "The current values of the norm after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void mergeProfileNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a norm from a profile.
   *
   * @param userId        identifier of the user for the profile where the norm is defined.
   * @param normId        identifier of the norm to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + NORMS_PATH + "/{normId}")
  @Operation(summary = "Delete a norm from a profile", description = "Allow to delete a defined norm from a profile")
  @ApiResponse(responseCode = "204", description = "The norm defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void deleteProfileNorm(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("normId") @Parameter(description = "The identifier of the norm to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String normId, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a planned activity into a profile.
   *
   * @param userId        identifier of the user for the profile to add the plannedActivity.
   * @param body          planned activity to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + PLANNED_ACTIVITIES_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a planned activity into a profile", description = "Insert a new planned activity into a profile")
  @RequestBody(description = "The new planned activity", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "201", description = "The added planned activity into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "400", description = "Bad planned activity to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void addProfilePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the planned activity", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the planned activity from a profile.
   *
   * @param userId        identifier of the user for the profile to get all plannedactivities.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + PLANNED_ACTIVITIES_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the planned activities from a profile", description = "Allow to get all the planned activities defined into a profile")
  @ApiResponse(responseCode = "200", description = "The planned activities defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void retrieveProfilePlannedActivities(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to get.
   * @param request           of the operation.
   * @param resultHandler     to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a planned activity from a profile", description = "Allow to get a planned activity defined into a profile")
  @ApiResponse(responseCode = "200", description = "The planned activity defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void retrieveProfilePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to update.
   * @param body              the new values for the planned activity.
   * @param request           of the operation.
   * @param resultHandler     to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a planned activity from a profile", description = "Allow to modify a planned activity defined into a profile")
  @RequestBody(description = "The new values to update the planned activity", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "200", description = "The updated planned activity", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "400", description = "Bad planned activity to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void updateProfilePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to merge.
   * @param body              the new values for the planned activity.
   * @param request           of the operation.
   * @param resultHandler     to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a planned activity from a profile", description = "Allow to modify parts of a planned activity defined into a profile")
  @RequestBody(description = "The new values to merge the planned activity", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "200", description = "The current values of the planned activity after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/PlannedActivity")))
  @ApiResponse(responseCode = "400", description = "Bad planned activity to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void mergeProfilePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a planned activity from a profile.
   *
   * @param userId            identifier of the user for the profile where the planned activity is defined.
   * @param plannedActivityId identifier of the planned activity to delete.
   * @param request           of the operation.
   * @param resultHandler     to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + PLANNED_ACTIVITIES_PATH + "/{plannedActivityId}")
  @Operation(summary = "Delete a planned activity from a profile", description = "Allow to delete a defined planned activity from a profile")
  @ApiResponse(responseCode = "204", description = "The planned activity defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or planned activity", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Planned activities")
  void deleteProfilePlannedActivity(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the planned activity is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("plannedActivityId") @Parameter(description = "The identifier of the planned activity to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String plannedActivityId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a relevant location into a profile.
   *
   * @param userId        identifier of the user for the profile to add the relevantLocation.
   * @param body          relevant location to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + RELEVANT_LOCATIONS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a relevant location into a profile", description = "Insert a new relevant location into a profile")
  @RequestBody(description = "The new relevant location", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "201", description = "The added relevant location into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "400", description = "Bad relevant location to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void addProfileRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the relevant location", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the relevant location from a profile.
   *
   * @param userId        identifier of the user for the profile to get all relevantlocations.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + RELEVANT_LOCATIONS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the relevant locations from a profile", description = "Allow to get all the relevant locations defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relevant locations defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void retrieveProfileRelevantLocations(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to get.
   * @param request            of the operation.
   * @param resultHandler      to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a relevant location from a profile", description = "Allow to get a relevant location defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relevant location defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void retrieveProfileRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to update.
   * @param body               the new values for the relevant location.
   * @param request            of the operation.
   * @param resultHandler      to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a relevant location from a profile", description = "Allow to modify a relevant location defined into a profile")
  @RequestBody(description = "The new values to update the relevant location", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "200", description = "The updated relevant location", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "400", description = "Bad relevant location to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void updateProfileRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to merge.
   * @param body               the new values for the relevant location.
   * @param request            of the operation.
   * @param resultHandler      to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a relevant location from a profile", description = "Allow to modify parts of a relevant location defined into a profile")
  @RequestBody(description = "The new values to merge the relevant location", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "200", description = "The current values of the relevant location after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/RelevantLocation")))
  @ApiResponse(responseCode = "400", description = "Bad relevant location to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void mergeProfileRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a relevant location from a profile.
   *
   * @param userId             identifier of the user for the profile where the relevant location is defined.
   * @param relevantLocationId identifier of the relevant location to delete.
   * @param request            of the operation.
   * @param resultHandler      to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + RELEVANT_LOCATIONS_PATH + "/{relevantLocationId}")
  @Operation(summary = "Delete a relevant location from a profile", description = "Allow to delete a defined relevant location from a profile")
  @ApiResponse(responseCode = "204", description = "The relevant location defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or relevant location", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relevant locations")
  void deleteProfileRelevantLocation(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relevant location is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("relevantLocationId") @Parameter(description = "The identifier of the relevant location to delete", example = "15837028-645a-4a55-9aaf-ceb846439eba") String relevantLocationId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a relationship into a profile.
   *
   * @param userId        identifier of the user for the profile to add the relationship.
   * @param body          relationship to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + RELATIONSHIPS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a relationship into a profile", description = "Insert a new relationship into a profile")
  @RequestBody(description = "The new relationship", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "201", description = "The added relationship into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void addProfileRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the relationship", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the relationship from a profile.
   *
   * @param userId        identifier of the user for the profile to get all relationships.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + RELATIONSHIPS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the relationships from a profile", description = "Allow to get all the relationships defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relationships defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void retrieveProfileRelationships(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a relationship from a profile", description = "Allow to get a relationship defined into a profile")
  @ApiResponse(responseCode = "200", description = "The relationship defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void retrieveProfileRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the relationship to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to update.
   * @param body          the new values for the relationship.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a relationship from a profile", description = "Allow to modify a relationship defined into a profile")
  @RequestBody(description = "The new values to update the relationship", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "200", description = "The updated relationship", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void updateProfileRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the relationship to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to merge.
   * @param body          the new values for the relationship.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a relationship from a profile", description = "Allow to modify parts of a relationship defined into a profile")
  @RequestBody(description = "The new values to merge the relationship", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "200", description = "The current values of the relationship after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void mergeProfileRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The identifier of the relationship to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a relationship from a profile.
   *
   * @param userId        identifier of the user for the profile where the relationship is defined.
   * @param index         of the relationship to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + RELATIONSHIPS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a relationship from a profile", description = "Allow to delete a defined relationship from a profile")
  @ApiResponse(responseCode = "204", description = "The relationship defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or relationship", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Relationships")
  void deleteProfileRelationship(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the relationship is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the relationship to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a personal behavior into a profile.
   *
   * @param userId        identifier of the user for the profile to add the personalBehavior.
   * @param body          personal behavior to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + PERSONAL_BEHAVIORS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a personal behavior into a profile", description = "Insert a new personal behavior into a profile")
  @RequestBody(description = "The new personal behavior", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "201", description = "The added personal behavior into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "400", description = "Bad personal behavior to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void addProfilePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the personal behavior", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile to get all personalbehaviors.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + PERSONAL_BEHAVIORS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the personal behaviors from a profile", description = "Allow to get all the personal behaviors defined into a profile")
  @ApiResponse(responseCode = "200", description = "The personal behaviors defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void retrieveProfilePersonalBehaviors(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a personal behavior from a profile", description = "Allow to get a personal behavior defined into a profile")
  @ApiResponse(responseCode = "200", description = "The personal behavior defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void retrieveProfilePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to update.
   * @param body          the new values for the personal behavior.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a personal behavior from a profile", description = "Allow to modify a personal behavior defined into a profile")
  @RequestBody(description = "The new values to update the personal behavior", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "200", description = "The updated personal behavior", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "400", description = "Bad personal behavior to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void updateProfilePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to merge.
   * @param body          the new values for the personal behavior.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a personal behavior from a profile", description = "Allow to modify parts of a personal behavior defined into a profile")
  @RequestBody(description = "The new values to merge the personal behavior", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "200", description = "The current values of the personal behavior after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine")))
  @ApiResponse(responseCode = "400", description = "Bad personal behavior to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void mergeProfilePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a personal behavior from a profile.
   *
   * @param userId        identifier of the user for the profile where the personal behavior is defined.
   * @param index         of the personal behavior to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + PERSONAL_BEHAVIORS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a personal behavior from a profile", description = "Allow to delete a defined personal behavior from a profile")
  @ApiResponse(responseCode = "204", description = "The personal behavior defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or personal behavior", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Personal behaviors")
  void deleteProfilePersonalBehavior(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the personal behavior is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the personal behavior on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a material into a profile.
   *
   * @param userId        identifier of the user for the profile to add the material.
   * @param body          material to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + MATERIALS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a material into a profile", description = "Insert a new material into a profile")
  @RequestBody(description = "The new material", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "201", description = "The added material into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "400", description = "Bad material to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void addProfileMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the material", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the material from a profile.
   *
   * @param userId        identifier of the user for the profile to get all materials.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + MATERIALS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the materials from a profile", description = "Allow to get all the materials defined into a profile")
  @ApiResponse(responseCode = "200", description = "The materials defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void retrieveProfileMaterials(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + MATERIALS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a material from a profile", description = "Allow to get a material defined into a profile")
  @ApiResponse(responseCode = "200", description = "The material defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void retrieveProfileMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to update.
   * @param body          the new values for the material.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + MATERIALS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a material from a profile", description = "Allow to modify a material defined into a profile")
  @RequestBody(description = "The new values to update the material", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "200", description = "The updated material", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "400", description = "Bad material to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void updateProfileMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to merge.
   * @param body          the new values for the material.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + MATERIALS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a material from a profile", description = "Allow to modify parts of a material defined into a profile")
  @RequestBody(description = "The new values to merge the material", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "200", description = "The current values of the material after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Material")))
  @ApiResponse(responseCode = "400", description = "Bad material to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void mergeProfileMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a material from a profile.
   *
   * @param userId        identifier of the user for the profile where the material is defined.
   * @param index         of the material to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + MATERIALS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a material from a profile", description = "Allow to delete a defined material from a profile")
  @ApiResponse(responseCode = "204", description = "The material defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or material", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Materials")
  void deleteProfileMaterial(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the material is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the material on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a competence into a profile.
   *
   * @param userId        identifier of the user for the profile to add the competence.
   * @param body          competence to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + COMPETENCES_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a competence into a profile", description = "Insert a new competence into a profile")
  @RequestBody(description = "The new competence", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "201", description = "The added competence into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "400", description = "Bad competence to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void addProfileCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the competence", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the competence from a profile.
   *
   * @param userId        identifier of the user for the profile to get all competences.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + COMPETENCES_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the competences from a profile", description = "Allow to get all the competences defined into a profile")
  @ApiResponse(responseCode = "200", description = "The competences defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void retrieveProfileCompetences(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + COMPETENCES_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a competence from a profile", description = "Allow to get a competence defined into a profile")
  @ApiResponse(responseCode = "200", description = "The competence defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void retrieveProfileCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to update.
   * @param body          the new values for the competence.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + COMPETENCES_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a competence from a profile", description = "Allow to modify a competence defined into a profile")
  @RequestBody(description = "The new values to update the competence", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "200", description = "The updated competence", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "400", description = "Bad competence to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void updateProfileCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to merge.
   * @param body          the new values for the competence.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + COMPETENCES_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a competence from a profile", description = "Allow to modify parts of a competence defined into a profile")
  @RequestBody(description = "The new values to merge the competence", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "200", description = "The current values of the competence after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Competence")))
  @ApiResponse(responseCode = "400", description = "Bad competence to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void mergeProfileCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a competence from a profile.
   *
   * @param userId        identifier of the user for the profile where the competence is defined.
   * @param index         of the competence to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + COMPETENCES_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a competence from a profile", description = "Allow to delete a defined competence from a profile")
  @ApiResponse(responseCode = "204", description = "The competence defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or competence", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Competences")
  void deleteProfileCompetence(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the competence is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the competence on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a meaning into a profile.
   *
   * @param userId        identifier of the user for the profile to add the meaning.
   * @param body          meaning to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path(USER_ID_PATH + MEANINGS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a meaning into a profile", description = "Insert a new meaning into a profile")
  @RequestBody(description = "The new meaning", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "201", description = "The added meaning into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "400", description = "Bad meaning to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void addProfileMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user to the profile to add the meaning", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the meaning from a profile.
   *
   * @param userId        identifier of the user for the profile to get all meanings.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + MEANINGS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the meanings from a profile", description = "Allow to get all the meanings defined into a profile")
  @ApiResponse(responseCode = "200", description = "The meanings defined into the profile", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning"))))
  @ApiResponse(responseCode = "404", description = "Not found profile", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void retrieveProfileMeanings(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path(USER_ID_PATH + MEANINGS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a meaning from a profile", description = "Allow to get a meaning defined into a profile")
  @ApiResponse(responseCode = "200", description = "The meaning defined into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void retrieveProfileMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning to get", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to update.
   * @param body          the new values for the meaning.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path(USER_ID_PATH + MEANINGS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a meaning from a profile", description = "Allow to modify a meaning defined into a profile")
  @RequestBody(description = "The new values to update the meaning", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "200", description = "The updated meaning", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "400", description = "Bad meaning to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void updateProfileMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning to update", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to partially modify a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to merge.
   * @param body          the new values for the meaning.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path(USER_ID_PATH + MEANINGS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Change a meaning from a profile", description = "Allow to modify parts of a meaning defined into a profile")
  @RequestBody(description = "The new values to merge the meaning", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "200", description = "The current values of the meaning after it has been merged", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Meaning")))
  @ApiResponse(responseCode = "400", description = "Bad meaning to merge", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void mergeProfileMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning on the profile to merge", example = "1") int index, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a meaning from a profile.
   *
   * @param userId        identifier of the user for the profile where the meaning is defined.
   * @param index         of the meaning to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path(USER_ID_PATH + MEANINGS_PATH + "/{index:0-9}")
  @Operation(summary = "Delete a meaning from a profile", description = "Allow to delete a defined meaning from a profile")
  @ApiResponse(responseCode = "204", description = "The meaning defined into the profile")
  @ApiResponse(responseCode = "404", description = "Not found profile or meaning", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Meanings")
  void deleteProfileMeaning(@PathParam("userId") @Parameter(description = "The identifier of the user for the profile where the meaning is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @PathParam("index") @Parameter(description = "The index of the meaning on the profile to delete", example = "1") int index, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
