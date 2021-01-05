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

package eu.internetofus.wenet_profile_manager.api;

import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.vertx.AbstractAPIVerticle;
import eu.internetofus.wenet_profile_manager.api.communities.Communities;
import eu.internetofus.wenet_profile_manager.api.communities.CommunitiesResource;
import eu.internetofus.wenet_profile_manager.api.help.Help;
import eu.internetofus.wenet_profile_manager.api.help.HelpResource;
import eu.internetofus.wenet_profile_manager.api.intelligences.Intelligences;
import eu.internetofus.wenet_profile_manager.api.intelligences.IntelligencesResource;
import eu.internetofus.wenet_profile_manager.api.personalities.Personalities;
import eu.internetofus.wenet_profile_manager.api.personalities.PersonalitiesResource;
import eu.internetofus.wenet_profile_manager.api.profiles.Profiles;
import eu.internetofus.wenet_profile_manager.api.profiles.ProfilesResource;
import eu.internetofus.wenet_profile_manager.api.trusts.Trusts;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustsResource;
import eu.internetofus.wenet_profile_manager.api.user_identifiers.UserIdentifiers;
import eu.internetofus.wenet_profile_manager.api.user_identifiers.UserIdentifiersResource;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.openapi.RouterBuilder;
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
  protected void mountServiceInterfaces(final RouterBuilder routerFactory) {

    routerFactory.mountServiceInterface(Help.class, Help.ADDRESS);
    new ServiceBinder(this.vertx).setAddress(Help.ADDRESS).register(Help.class, new HelpResource(this));

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

    routerFactory.mountServiceInterface(Communities.class, Communities.ADDRESS);
    new ServiceBinder(this.vertx).setAddress(Communities.ADDRESS).register(Communities.class,
        new CommunitiesResource(this.vertx));

    routerFactory.mountServiceInterface(UserIdentifiers.class, UserIdentifiers.ADDRESS);
    new ServiceBinder(this.vertx).setAddress(UserIdentifiers.ADDRESS).register(UserIdentifiers.class,
        new UserIdentifiersResource(this.vertx));

  }

  /**
   * Register the services provided by the API.
   *
   * {@inheritDoc}
   *
   * @see WeNetProfileManager
   */
  @Override
  protected void startedServerAt(final String host, final int port) {

    final var conf = new JsonObject();
    conf.put("profileManager", "http://" + host + ":" + port);
    final var client = WebClient.create(this.vertx);
    WeNetProfileManager.register(this.vertx, client, conf);

  }

}