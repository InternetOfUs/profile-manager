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

package eu.internetofus.wenet_profile_manager.api.communities;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
 * The definition of the web services to manage the {@link CommunityProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Communities.PATH)
@Tag(name = "Communities")
@WebApiServiceGen
public interface Communities {

  /**
   * The path to the communities resources.
   */
  String PATH = "/communities";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.communities";

  /**
   * The path to the social practices of a profile.
   */
  String SOCIAL_PRACTICES_PATH = "/socialPractices";

  /**
   * Called when want to create a community.
   *
   * @param body          the new community to create.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Create a community", description = "Create a new  community")
  @RequestBody(description = "The new community to create", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "200", description = "The created community", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "400", description = "Bad community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void createCommunity(@Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a community.
   *
   * @param id            identifier of the community to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a community", description = "Allow to get the community with the specified identifier")
  @ApiResponse(responseCode = "200", description = "The community associated to the identifier", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void retrieveCommunity(@PathParam("id") @Parameter(description = "The identifier of the community to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to modify a community.
   *
   * @param id            identifier of the community to modify.
   * @param body          the new community.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Modify a community", description = "Change a community")
  @RequestBody(description = "The new community", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "200", description = "The updated community", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "400", description = "Bad community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void updateCommunity(@PathParam("id") @Parameter(description = "The identifier of the community to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to modify partially a community.
   *
   * @param id            identifier of the community to modify.
   * @param body          the new community attributes.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Modify partially a community", description = "Change some attributes of a community")
  @RequestBody(description = "The new values for the community", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "200", description = "The merged community", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "400", description = "Bad community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void mergeCommunity(@PathParam("id") @Parameter(description = "The identifier of the community to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id, @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to delete a community.
   *
   * @param id            identifier of the community to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Delete a community", description = "Allow to delete a community with an specific identifier")
  @ApiResponse(responseCode = "204", description = "The community was deleted successfully")
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void deleteCommunity(@PathParam("id") @Parameter(description = "The identifier of the community to delete") String id, @Parameter(hidden = true, required = false) OperationRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to add a social practice into a community.
   *
   * @param id            identifier of the user for the profile to add the social practice.
   * @param body          social practice to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{id}" + SOCIAL_PRACTICES_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a social practice into a community", description = "Insert a new social practice into a community")
  @RequestBody(description = "The new social practice", required = true, content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "201", description = "The added social practice into the profile", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "Bad social practice to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void addSocialPractice(@PathParam("id") @Parameter(description = "The identifier of the community to add the social practice", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get all the social practice from a community.
   *
   * @param id            identifier of the community where the social practices are defined.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}" + SOCIAL_PRACTICES_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the social practices from a community", description = "Allow to get all the social practices defined into a community")
  @ApiResponse(responseCode = "200", description = "The social practices defined into the community", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice"))))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void retrieveSocialPractices(@PathParam("id") @Parameter(description = "The identifier of community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to get a social practice from a community.
   *
   * @param id               identifier of the community where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to get.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @GET
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a social practice from a community", description = "Allow to get a social practice defined into a community")
  @ApiResponse(responseCode = "200", description = "The social practice defined into the community", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void retrieveSocialPractice(@PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to remove a social practice from a community.
   *
   * @param id               identifier of the community where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to remove.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @DELETE
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Remove a social practice from a community", description = "Allow to remove a social practice defined into a community")
  @ApiResponse(responseCode = "204", description = "The social practice has removed successfully from the community")
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void deleteSocialPractice(@PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to remove", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to update a social practice from a community.
   *
   * @param id               identifier of the community where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to update.
   * @param body             element with the values to update.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @PUT
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a social practice from a community", description = "Allow to update a social practice defined into a community")
  @ApiResponse(responseCode = "200", description = "The social practice that has been updated on the community", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "The social practice to update is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void updateSocialPractice(@PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

  /**
   * Called when want to merge a social practice from a community.
   *
   * @param id               identifier of the community where the social practice is defined.
   * @param socialPracticeId identifier of the social practice to merge.
   * @param body             element with the values to merge.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @PATCH
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Merge a social practice from a community", description = "Allow to merge a social practice defined into a community")
  @ApiResponse(responseCode = "200", description = "The social practice that has been merged on the community", content = @Content(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "The social practice to merge is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void mergeSocialPractice(@PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to merge", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) JsonObject body, @Parameter(hidden = true, required = false) OperationRequest request, @Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}
