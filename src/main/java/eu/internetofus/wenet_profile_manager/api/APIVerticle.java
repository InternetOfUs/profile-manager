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

package eu.internetofus.wenet_profile_manager.api;

import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerClient;
import eu.internetofus.common.vertx.AbstractAPIVerticle;
import eu.internetofus.common.vertx.AbstractServicesVerticle;
import eu.internetofus.wenet_profile_manager.api.communities.Communities;
import eu.internetofus.wenet_profile_manager.api.communities.CommunitiesResource;
import eu.internetofus.wenet_profile_manager.api.help.Help;
import eu.internetofus.wenet_profile_manager.api.help.HelpResource;
import eu.internetofus.wenet_profile_manager.api.profiles.Profiles;
import eu.internetofus.wenet_profile_manager.api.profiles.ProfilesResource;
import eu.internetofus.wenet_profile_manager.api.trusts.Trusts;
import eu.internetofus.wenet_profile_manager.api.trusts.TrustsResource;
import eu.internetofus.wenet_profile_manager.api.user_identifiers.UserIdentifiers;
import eu.internetofus.wenet_profile_manager.api.user_identifiers.UserIdentifiersResource;
import io.vertx.core.json.JsonObject;
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
    conf.put(WeNetProfileManagerClient.PROFILE_MANAGER_CONF_KEY, "http://" + host + ":" + port);
    final var client = AbstractServicesVerticle.createWebClientSession(this.getVertx(), this.config());
    WeNetProfileManager.register(this.vertx, client, conf);

  }

}