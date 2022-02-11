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

package eu.internetofus.wenet_profile_manager.api.communities;

import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfilesPage;
import eu.internetofus.common.model.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
   * The path to the social practices of a community.
   */
  String SOCIAL_PRACTICES_PATH = "/socialPractices";

  /**
   * The path to the norms of a community.
   */
  String NORMS_PATH = "/norms";

  /**
   * The path to the social members of a community.
   */
  String COMMUNITY_MEMBERS_PATH = "/members";

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
  @RequestBody(description = "The new community to create", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "200", description = "The created community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "400", description = "Bad community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void createCommunity(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

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
  @ApiResponse(responseCode = "200", description = "The community associated to the identifier", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void retrieveCommunity(
      @PathParam("id") @Parameter(description = "The identifier of the community to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get some communities.
   *
   * @param appId         application identifier to match for the communities to
   *                      return.
   * @param name          to match for the communities to return.
   * @param description   to match for the communities to return.
   * @param keywords      to match for the communities to return.
   * @param members       to match for the communities to return.
   * @param order         in with the communities has to be sort.
   * @param offset        index of the first community to return.
   * @param limit         number maximum of communities to return.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return some communities", description = "Allow to search for some communities")
  @ApiResponse(responseCode = "200", description = "The page with the communities that satisfy the search parameters", content = @Content(schema = @Schema(implementation = CommunityProfilesPage.class)))
  @ApiResponse(responseCode = "400", description = "If any search parameter is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void retrieveCommunityProfilesPage(
      @QueryParam(value = "appId") @Parameter(description = "An application identifier to be equals on the communities to return. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of the communities to return, if you write between '/'. For example to get the communitites for the aplications '1' and '2' you must pass as 'appId' '/^[1|2]$/'.", example = "1", required = false) String appId,
      @QueryParam(value = "name") @Parameter(description = "A name to be equals on the communities to return. You can use a Perl compatible regular expressions (PCRE) that has to match the name of the communities to return if you write between '/'. For example to get the communities with a name with the word 'eat' you must pass as 'name' '/.*eat.*/'", example = "/.*eat.*/", required = false) String name,
      @QueryParam(value = "description") @Parameter(description = "A description to be equals on the communities to return. You can use a Perl compatible regular expressions (PCRE) that has to match the description of the communities to return if you write between '/'. For example to get the communities with a description with the word 'eat' you must pass as 'description' '/.*eat.*/'", example = "/.*eat.*/", required = false) String description,
      @QueryParam(value = "keywords") @Parameter(description = "A set of keywords to be defined on the communities to be returned. For each keyword is separated by a ',' and each field keyword can be between '/' to use a Perl compatible regular expressions (PCRE) instead the exact value.", example = "key1,/.*eat.*/,key3", required = false, style = ParameterStyle.FORM, explode = Explode.FALSE) String keywords,
      @QueryParam(value = "members") @Parameter(description = "A set of user identifiers to be a member of the communities to be returned. For each member is separated by a ',' and each field user identifier can be between '/' to use a Perl compatible regular expressions (PCRE) instead the exact value.", example = "1,/.*2.*/,3", required = false, style = ParameterStyle.FORM, explode = Explode.FALSE) String members,
      @QueryParam(value = "order") @Parameter(description = "The order in witch the communities has to be returned. For each field it has be separated by a ',' and each field can start with '+' (or without it) to order on ascending order, or with the prefix '-' to do on descendant order.", example = "name,-description,+members", required = false, style = ParameterStyle.FORM, explode = Explode.FALSE) String order,
      @DefaultValue("0") @QueryParam(value = "offset") @Parameter(description = "The index of the first community to return.", example = "4", required = false) int offset,
      @DefaultValue("10") @QueryParam(value = "limit") @Parameter(description = "The number maximum of communities to return", example = "100", required = false) int limit,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

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
  @RequestBody(description = "The new community", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "200", description = "The updated community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "400", description = "Bad community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void updateCommunity(
      @PathParam("id") @Parameter(description = "The identifier of the community to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

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
  @RequestBody(description = "The new values for the community", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "200", description = "The merged community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityProfile")))
  @ApiResponse(responseCode = "400", description = "Bad community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void mergeCommunity(
      @PathParam("id") @Parameter(description = "The identifier of the community to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

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
  void deleteCommunity(@PathParam("id") @Parameter(description = "The identifier of the community to delete") String id,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to add a social practice into a community.
   *
   * @param id            identifier of the user for the profile to add the social
   *                      practice.
   * @param body          social practice to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{id}" + SOCIAL_PRACTICES_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Insert a new social practice into a community")
  @RequestBody(description = "The new social practice", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "201", description = "The added social practice into the profile", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "Bad social practice to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void addCommunitySocialPractice(
      @PathParam("id") @Parameter(description = "The identifier of the community to add the social practice", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get all the social practice from a community.
   *
   * @param id            identifier of the community where the social practices
   *                      are defined.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}" + SOCIAL_PRACTICES_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to get all the social practices defined into a community")
  @ApiResponse(responseCode = "200", description = "The social practices defined into the community", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice"))))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void retrieveCommunitySocialPractices(
      @PathParam("id") @Parameter(description = "The identifier of community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get a social practice from a community.
   *
   * @param id               identifier of the community where the social practice
   *                         is defined.
   * @param socialPracticeId identifier of the social practice to get.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @GET
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to get a social practice defined into a community")
  @ApiResponse(responseCode = "200", description = "The social practice defined into the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void retrieveCommunitySocialPractice(
      @PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to remove a social practice from a community.
   *
   * @param id               identifier of the community where the social practice
   *                         is defined.
   * @param socialPracticeId identifier of the social practice to remove.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @DELETE
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to remove a social practice defined into a community")
  @ApiResponse(responseCode = "204", description = "The social practice has removed successfully from the community")
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void deleteCommunitySocialPractice(
      @PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to remove", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to update a social practice from a community.
   *
   * @param id               identifier of the community where the social practice
   *                         is defined.
   * @param socialPracticeId identifier of the social practice to update.
   * @param body             element with the values to update.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @PUT
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to update a social practice defined into a community")
  @RequestBody(description = "The update values for the social practice", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "200", description = "The social practice that has been updated on the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "The social practice to update is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void updateCommunitySocialPractice(
      @PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to merge a social practice from a community.
   *
   * @param id               identifier of the community where the social practice
   *                         is defined.
   * @param socialPracticeId identifier of the social practice to merge.
   * @param body             element with the values to merge.
   * @param request          of the operation.
   * @param resultHandler    to inform of the response.
   */
  @PATCH
  @Path("/{id}" + SOCIAL_PRACTICES_PATH + "/{socialPracticeId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to merge a social practice defined into a community")
  @RequestBody(description = "The merge values for the social practice", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "200", description = "The social practice that has been merged on the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialPractice")))
  @ApiResponse(responseCode = "400", description = "The social practice to merge is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or social practice", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Social practices")
  void mergeCommunitySocialPractice(
      @PathParam("id") @Parameter(description = "The identifier of the community where the social practice is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("socialPracticeId") @Parameter(description = "The identifier of the social practice to merge", example = "15837028-645a-4a55-9aaf-ceb846439eba") String socialPracticeId,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to add a norm into a community.
   *
   * @param id            identifier of the community to add the norm.
   * @param body          norm to add to the community.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{id}" + NORMS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Insert a new norm into a community")
  @RequestBody(description = "The new norm", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "201", description = "The added norm into the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "400", description = "Bad norm to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void addCommunityNorm(
      @PathParam("id") @Parameter(description = "The identifier of the community to add the norm", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get all the norm from a community.
   *
   * @param id            identifier of the community where the norms are defined.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}" + NORMS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to get all the norms defined into a community")
  @ApiResponse(responseCode = "200", description = "The norms defined into the community", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm"))))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void retrieveCommunityNorms(
      @PathParam("id") @Parameter(description = "The identifier of community where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get a norm from a community.
   *
   * @param id            identifier of the community where the norm is defined.
   * @param index         of the norm to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}" + NORMS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to get a norm defined into a community")
  @ApiResponse(responseCode = "200", description = "The norm defined into the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "404", description = "Not found community or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void retrieveCommunityNorm(
      @PathParam("id") @Parameter(description = "The identifier of the community where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("index") @Parameter(description = "The index of the norm to get", example = "1") int index,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to remove a norm from a community.
   *
   * @param id            identifier of the community where the norm is defined.
   * @param index         of the norm to remove.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{id}" + NORMS_PATH + "/{index:0-9}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to remove a norm defined into a community")
  @ApiResponse(responseCode = "204", description = "The norm has removed successfully from the community")
  @ApiResponse(responseCode = "404", description = "Not found community or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void deleteCommunityNorm(
      @PathParam("id") @Parameter(description = "The identifier of the community where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("index") @Parameter(description = "The index of the norm to remove", example = "1") int index,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to update a norm from a community.
   *
   * @param id            identifier of the community where the norm is defined.
   * @param index         of the norm to update.
   * @param body          element with the values to update.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{id}" + NORMS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(description = "Allow to update a norm defined into a community")
  @RequestBody(description = "The update values for the norm", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "200", description = "The norm that has been updated on the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "400", description = "The norm to update is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void updateCommunityNorm(
      @PathParam("id") @Parameter(description = "The identifier of the community where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("index") @Parameter(description = "The index of the norm to update", example = "1") int index,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to merge a norm from a community.
   *
   * @param id            identifier of the community where the norm is defined.
   * @param index         of the norm to merge.
   * @param body          element with the values to merge.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{id}" + NORMS_PATH + "/{index:0-9}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Merge a norm from a community", description = "Allow to merge a norm defined into a community")
  @RequestBody(description = "The merge values for the norm", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "200", description = "The norm that has been merged on the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/ProtocolNorm")))
  @ApiResponse(responseCode = "400", description = "The norm to merge is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or norm", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Norms")
  void mergeCommunityNorm(
      @PathParam("id") @Parameter(description = "The identifier of the community where the norm is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("index") @Parameter(description = "The index of the norm to merge", example = "1") int index,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to add a community member into a community.
   *
   * @param id            identifier of the user for the profile to add the
   *                      community member.
   * @param body          community member to add to the profile.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Path("/{id}" + COMMUNITY_MEMBERS_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add a community member into a community", description = "Insert a new community member into a community")
  @RequestBody(description = "The new community member", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "201", description = "The added community member into the profile", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "400", description = "Bad community member to add", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Community members")
  void addCommunityMember(
      @PathParam("id") @Parameter(description = "The identifier of the community to add the community member", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get all the community member from a community.
   *
   * @param id            identifier of the community where the community members
   *                      are defined.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}" + COMMUNITY_MEMBERS_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the community members from a community", description = "Allow to get all the community members defined into a community")
  @ApiResponse(responseCode = "200", description = "The community members defined into the community", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember"))))
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Community members")
  void retrieveCommunityMembers(
      @PathParam("id") @Parameter(description = "The identifier of community where the community member is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get a community member from a community.
   *
   * @param id            identifier of the community where the community member
   *                      is defined.
   * @param userId        identifier of the community member to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{id}" + COMMUNITY_MEMBERS_PATH + "/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return a community member from a community", description = "Allow to get a community member defined into a community")
  @ApiResponse(responseCode = "200", description = "The community member defined into the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "404", description = "Not found community or community member", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Community members")
  void retrieveCommunityMember(
      @PathParam("id") @Parameter(description = "The identifier of the community where the community member is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("userId") @Parameter(description = "The identifier of the community member to get", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to remove a community member from a community.
   *
   * @param id            identifier of the community where the community member
   *                      is defined.
   * @param userId        identifier of the community member to remove.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{id}" + COMMUNITY_MEMBERS_PATH + "/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Remove a community member from a community", description = "Allow to remove a community member defined into a community")
  @ApiResponse(responseCode = "204", description = "The community member has removed successfully from the community")
  @ApiResponse(responseCode = "404", description = "Not found community or community member", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Community members")
  void deleteCommunityMember(
      @PathParam("id") @Parameter(description = "The identifier of the community where the community member is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("userId") @Parameter(description = "The identifier of the community member to remove", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to update a community member from a community.
   *
   * @param id            identifier of the community where the community member
   *                      is defined.
   * @param userId        identifier of the community member to update.
   * @param body          element with the values to update.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{id}" + COMMUNITY_MEMBERS_PATH + "/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update a community member from a community", description = "Allow to update a community member defined into a community")
  @RequestBody(description = "The update values for the community member", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "200", description = "The community member that has been updated on the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "400", description = "The community member to update is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or community member", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Community members")
  void updateCommunityMember(
      @PathParam("id") @Parameter(description = "The identifier of the community where the community member is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("userId") @Parameter(description = "The identifier of the community member to update", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to merge a community member from a community.
   *
   * @param id            identifier of the community where the community member
   *                      is defined.
   * @param userId        identifier of the community member to merge.
   * @param body          element with the values to merge.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{id}" + COMMUNITY_MEMBERS_PATH + "/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Merge a community member from a community", description = "Allow to merge a community member defined into a community")
  @RequestBody(description = "The merge values for the community member", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "200", description = "The community member that has been merged on the community", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/CommunityMember")))
  @ApiResponse(responseCode = "400", description = "The community member to merge is not valid", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(responseCode = "404", description = "Not found community or community member", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @Tag(name = "Community members")
  void mergeCommunityMember(
      @PathParam("id") @Parameter(description = "The identifier of the community where the community member is defined", example = "15837028-645a-4a55-9aaf-ceb846439eba") String id,
      @PathParam("userId") @Parameter(description = "The identifier of the community member to merge", example = "15837028-645a-4a55-9aaf-ceb846439eba") String userId,
      @Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to check if a community exist.
   *
   * @param communityId   identifier of the community to get.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @HEAD
  @Path("/{communityId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Check if exist a community with an identifier", description = "Allow to check if an  identifier is associated to a community")
  @ApiResponse(responseCode = "204", description = "The community exist")
  @ApiResponse(responseCode = "404", description = "Not found community", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void isCommunityDefined(
      @PathParam("communityId") @Parameter(description = "The identifier of the community to check if exist", example = "15837028-645a-4a55-9aaf-ceb846439eba") String communityId,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
