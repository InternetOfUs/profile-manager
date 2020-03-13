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

package eu.internetofus.wenet_profile_manager.services;

import eu.internetofus.common.services.AbstractServicesVerticle;
import eu.internetofus.common.services.WeNetInteractionProtocolEngineService;
import eu.internetofus.common.services.WeNetTaskManagerService;
import io.vertx.core.json.JsonObject;

/**
 * The verticle that provide the services to interact with the other WeNet
 * modules.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServicesVerticle extends AbstractServicesVerticle {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerServices(JsonObject serviceConf) throws Exception {

		// register the service to interact with the task manager
		final JsonObject taskManagerConf = serviceConf.getJsonObject("taskManager", new JsonObject());
		WeNetTaskManagerService.register(this.vertx, this.client, taskManagerConf);

		// register the service to interact with the interaction protocol engine
		final JsonObject interactionProtocolEngineConf = serviceConf.getJsonObject("interactionProtocolEngine",
				new JsonObject());
		WeNetInteractionProtocolEngineService.register(this.vertx, this.client, interactionProtocolEngineConf);

	}

}
