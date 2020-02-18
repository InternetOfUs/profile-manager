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

package eu.internetofus.wenet_profile_manager.api.intelligences;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.internetofus.wenet_profile_manager.api.Questionnaire;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
 * The definition of the web services to manage the user intelligences.
 *
 * @see GardnerIntelligences
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Intelligences.PATH)
@Tag(name = "Intelligences")
@WebApiServiceGen
public interface Intelligences {

	/**
	 * The path to the intelligences resource.
	 */
	String PATH = "/intelligences";

	/**
	 * The address of this service.
	 */
	String ADDRESS = "wenet_profile_manager.api.intelligences";

	/**
	 * Called when want the intelligences questionnaire.
	 *
	 * @param context       of the request.
	 * @param resultHandler to inform of the response.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Obtain the intelligences test",
			description = "Return the questionnaire used to obtain the intelligences of a person")
	@Parameter(
			in = ParameterIn.HEADER,
			name = HttpHeaders.ACCEPT_LANGUAGE,
			description = "The preferred language for the text on the questionnaire. If it is not available the texts will be on English.",
			example = "en",
			schema = @Schema(type = "string", defaultValue = "en"))
	@ApiResponse(
			responseCode = "200",
			description = "The questionnaire to evaluate the intelligences of a person",
			content = @Content(schema = @Schema(implementation = Questionnaire.class)))
	void retrieveIntelligencesQuestionnaire(@Parameter(hidden = true, required = false) OperationRequest context,
			@Parameter(hidden = true, required = false) Handler<AsyncResult<OperationResponse>> resultHandler);

}