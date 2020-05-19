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

package eu.internetofus.wenet_profile_manager.api;

import eu.internetofus.common.components.profile_manager.WeNetProfileManagerService;
import eu.internetofus.common.vertx.AbstractAPIVerticle;
import eu.internetofus.wenet_profile_manager.api.intelligences.Intelligences;
import eu.internetofus.wenet_profile_manager.api.intelligences.IntelligencesResource;
import eu.internetofus.wenet_profile_manager.api.personalities.Personalities;
import eu.internetofus.wenet_profile_manager.api.personalities.PersonalitiesResource;
import eu.internetofus.wenet_profile_manager.api.profiles.Profiles;
import eu.internetofus.wenet_profile_manager.api.profiles.ProfilesResource;
import eu.internetofus.wenet_profile_manager.api.trusts.Trusts;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustsResource;
import eu.internetofus.wenet_profile_manager.api.versions.Versions;
import eu.internetofus.wenet_profile_manager.api.versions.VersionsResource;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The verticle that provide the manage the WeNet profile manager API.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class APIVerticle extends AbstractAPIVerticle {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getOpenAPIResourcePath() {

		return "wenet-profile_manager-openapi.yaml";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void mountServiceInterfaces(OpenAPI3RouterFactory routerFactory) {

		routerFactory.mountServiceInterface(Versions.class, Versions.ADDRESS);
		new ServiceBinder(this.vertx).setAddress(Versions.ADDRESS).register(Versions.class, new VersionsResource(this));

		routerFactory.mountServiceInterface(Profiles.class, Profiles.ADDRESS);
		new ServiceBinder(this.vertx).setAddress(Profiles.ADDRESS).register(Profiles.class,
				new ProfilesResource(this.vertx));

		routerFactory.mountServiceInterface(Personalities.class, Personalities.ADDRESS);
		new ServiceBinder(this.vertx).setAddress(Personalities.ADDRESS).register(Personalities.class,
				new PersonalitiesResource(this.vertx));

		routerFactory.mountServiceInterface(Intelligences.class, Intelligences.ADDRESS);
		new ServiceBinder(this.vertx).setAddress(Intelligences.ADDRESS).register(Intelligences.class,
				new IntelligencesResource(this.vertx));

		routerFactory.mountServiceInterface(Trusts.class, Trusts.ADDRESS);
		new ServiceBinder(this.vertx).setAddress(Trusts.ADDRESS).register(Trusts.class, new TrustsResource(this.vertx));

	}

	/**
	 * Register the services provided by the API.
	 *
	 * {@inheritDoc}
	 *
	 * @see WeNetProfileManagerService
	 */
	@Override
	protected void startedServerAt(String host, int port) {

		final JsonObject conf = new JsonObject();
		conf.put("profileManager", "http://" + host + ":" + port);
		final WebClient client = WebClient.create(this.vertx);
		WeNetProfileManagerService.register(this.vertx, client, conf);

	}

}