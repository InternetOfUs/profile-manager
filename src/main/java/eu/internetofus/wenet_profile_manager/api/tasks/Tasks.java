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

package eu.internetofus.wenet_profile_manager.api.tasks;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Service used to manage the tasks associated to a profile.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Tasks.PATH)
@Tag(name = "Tasks")
@WebApiServiceGen
public interface Tasks {

  /**
   * The path to the version resource.
   */
  String PATH = "/tasks";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_profile_manager.api.tasks";

  /**
   * Called when want to delete the information of a task.
   *
   * @param taskId        identifier of the task to delete.
   * @param request       of the operation.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Delete task information", description = "Allow to delete the information associated to a task")
  @ApiResponse(responseCode = "204", description = "The information of the task was deleted successfully")
  void taskDeleted(
      @PathParam("taskId") @Parameter(description = "The identifier of the task to delete its information") String taskId,
      @Parameter(hidden = true, required = false) ServiceRequest request,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
