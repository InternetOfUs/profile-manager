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
package eu.internetofus.wenet_profile_manager.api.relationships;

import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipsPage;
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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * The definition of the web services to manage the relationships between WeNet
 * users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Relationships.PATH)
@Tag(name = "Profiles")
@Tag(name = "Relationships")
@WebApiServiceGen
public interface Relationships {

  /**
   * The path to the relationships resource.
   */
  String PATH = "/relationships";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.relationships";

  /**
   * Called when want to get some relationships between users.
   *
   * @param appId         application identifier to match for the social network
   *                      relationships to return.
   * @param sourceId      user identifier to match for the relationships source to
   *                      return.
   * @param targetId      user identifier to match for the relationships target to
   *                      return.
   * @param type          for the relationships to return.
   * @param weightFrom    minimal weight, inclusive, of the relationships to
   *                      return.
   * @param weightTo      maximal weight, inclusive, of the relationships to
   *                      return.
   * @param order         in with the relationships has to be sort.
   * @param offset        index of the first social network relationship to
   *                      return.
   * @param limit         number maximum of social network relationships to
   *                      return.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the relationships that match the request parameters", description = "Allow to get all the relationships that match the request parameters")
  @ApiResponse(responseCode = "200", description = "The relationships defined into the profile", content = @Content(schema = @Schema(implementation = SocialNetworkRelationshipsPage.class)))
  void retrieveSocialNetworkRelationshipsPage(
      @QueryParam(value = "appId") @Parameter(description = "An application identifier to be equals on the social network relationships to return. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of the relationships if you write between '/'. For example to get the relationships for the applications '1' and '2' you must pass as 'appId' '/^[1|2]$/'.", example = "1", required = false) String appId,
      @QueryParam(value = "sourceId") @Parameter(description = "A user identifier to be equals on the relationships source to return. You can use a Perl compatible regular expressions (PCRE) that has to match the user identifier of the relationships source if you write between '/'. For example to get the relationships with the source users '1' and '2' you must pass as 'source' '/^[1|2]$/'.", example = "1e346fd440", required = false) String sourceId,
      @QueryParam(value = "targetId") @Parameter(description = "A user identifier to be equals on the relationships target to return. You can use a Perl compatible regular expressions (PCRE) that has to match the user identifier of the relationships target if you write between '/'. For example to get the relationships with the target users '1' and '2' you must pass as 'target' '/^[1|2]$/'.", example = "1e346fd440", required = false) String targetId,
      @QueryParam(value = "type") @Parameter(description = "The type for the relationships to return. You can use a Perl compatible regular expressions (PCRE) that has to match the type of the relationships if you write between '/'. For example to get the relationships with the types 'friend' and 'colleague' you must pass as 'type' '/^[friend|colleague]$/'.", example = "friend", required = false) String type,
      @QueryParam(value = "weightFrom") @Parameter(description = "The minimal weight, inclusive, of the relationships to return.", example = "0.3", required = false) Double weightFrom,
      @QueryParam(value = "weightTo") @Parameter(description = "The maximal weight, inclusive, of the relationships to return.", example = "0.8", required = false) Double weightTo,
      @QueryParam(value = "order") @Parameter(description = "The order in witch the relationships has to be returned. For each field it has be separated by a ',' and each field can start with '+' (or without it) to order on ascending order, or with the prefix '-' to do on descendant order.", example = "sourceId,-weight,+type", required = false, style = ParameterStyle.FORM, explode = Explode.FALSE) String order,
      @DefaultValue("0") @QueryParam(value = "offset") @Parameter(description = "The index of the first social network relationship to return.", example = "4", required = false) int offset,
      @DefaultValue("10") @QueryParam(value = "limit") @Parameter(description = "The number maximum of social network relationships to return", example = "100", required = false) int limit,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to delete some relationships between users.
   *
   * @param appId         application identifier to match for the social network
   *                      relationships to delete.
   * @param sourceId      user identifier to match for the relationships source to
   *                      delete.
   * @param targetId      user identifier to match for the relationships target to
   *                      delete.
   * @param type          for the relationships to delete.
   * @param weightFrom    minimal weight, inclusive, of the relationships to
   *                      return.
   * @param weightTo      maximal weight, inclusive, of the relationships to
   *                      return.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Return the relationships that match the request parameters", description = "Allow to get all the relationships that match the request parameters")
  @ApiResponse(responseCode = "204", description = "If the relationships has been removed")
  @ApiResponse(responseCode = "404", description = "If any relationships has not been removed")
  void deleteSocialNetworkRelationships(
      @QueryParam(value = "appId") @Parameter(description = "An application identifier to be equals on the social network relationships to delete. You can use a Perl compatible regular expressions (PCRE) that has to match the application identifier of the relationships if you write between '/'. For example to get the relationships for the applications '1' and '2' you must pass as 'appId' '/^[1|2]$/'.", example = "1", required = false) String appId,
      @QueryParam(value = "sourceId") @Parameter(description = "A user identifier to be equals on the relationships source to delete. You can use a Perl compatible regular expressions (PCRE) that has to match the user identifier of the relationships source if you write between '/'. For example to delete the relationships with the source users '1' and '2' you must pass as 'source' '/^[1|2]$/'.", example = "1e346fd440", required = false) String sourceId,
      @QueryParam(value = "targetId") @Parameter(description = "A user identifier to be equals on the relationships target to delete. You can use a Perl compatible regular expressions (PCRE) that has to match the user identifier of the relationships target if you write between '/'. For example to delete the relationships with the target users '1' and '2' you must pass as 'target' '/^[1|2]$/'.", example = "1e346fd440", required = false) String targetId,
      @QueryParam(value = "type") @Parameter(description = "The type for the relationships to delete. You can use a Perl compatible regular expressions (PCRE) that has to match the type of the relationships if you write between '/'. For example to delete the relationships with the types 'friend' and 'colleague' you must pass as 'type' '/^[friend|colleague]$/'.", example = "friend", required = false) String type,
      @QueryParam(value = "weightFrom") @Parameter(description = "The minimal weight, inclusive, of the relationships to return.", example = "0.3", required = false) Double weightFrom,
      @QueryParam(value = "weightTo") @Parameter(description = "The maximal weight, inclusive, of the relationships to return.", example = "0.8", required = false) Double weightTo,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to add or update a relationship between users.
   *
   * @param body          relationship to update.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Add or update a relationship between users", description = "Add or modify a relationship between WeNet users")
  @RequestBody(description = "The new values for the relationship", required = true, content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.4.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "200", description = "The added or updated relationship between users", content = @Content(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.4.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship")))
  @ApiResponse(responseCode = "400", description = "Bad relationship to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void addOrUpdateRelationship(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to update a set of relationships between users.
   *
   * @param body          relationships to update.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/batch")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Update some relationships between users", description = "Modify some relationships between WeNet users")
  @RequestBody(description = "The new values for the relationships", required = true, content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.4.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship"))))
  @ApiResponse(responseCode = "200", description = "The updated relationships between users", content = @Content(array = @ArraySchema(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.4.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship"))))
  @ApiResponse(responseCode = "400", description = "Bad relationships to update", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void addOrUpdateSomeRelationships(@Parameter(hidden = true, required = false) JsonArray body,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
