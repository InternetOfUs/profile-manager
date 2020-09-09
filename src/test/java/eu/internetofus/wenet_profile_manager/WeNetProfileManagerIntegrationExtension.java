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

package eu.internetofus.wenet_profile_manager;

import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import eu.internetofus.common.Containers;
import eu.internetofus.common.components.service.WeNetServiceMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderMocker;
import eu.internetofus.common.vertx.AbstractMain;
import eu.internetofus.common.vertx.AbstractWeNetComponentIntegrationExtension;
import eu.internetofus.common.vertx.WeNetModuleContext;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Extension used to run integration tests over the WeNet profile manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileManagerIntegrationExtension extends AbstractWeNetComponentIntegrationExtension {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void afterStarted(final WeNetModuleContext context) {

    final var vertx = context.vertx;
    final var client = WebClient.create(vertx);
    final var conf = context.configuration.getJsonObject("wenetComponents", new JsonObject());
    WeNetServiceSimulator.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] createMainStartArguments() {

    final var network = Network.newNetwork();

    final var serviceApi = WeNetServiceMocker.start().getApiUrl();
    final var socialContextBuilderApi = WeNetSocialContextBuilderMocker.start().getApiUrl();

    final var profileManagerApiPort = Containers.nextFreePort();
    final var profileManagerApi = Containers.exposedApiFor(profileManagerApiPort);

    final var taskManagerApiPort = Containers.nextFreePort();
    var taskManagerApi = Containers.exposedApiFor(taskManagerApiPort);

    final var interactionProtocolEngineApiPort = Containers.nextFreePort();
    final var interactionProtocolEngineApi = Containers.exposedApiFor(interactionProtocolEngineApiPort);

    Testcontainers.exposeHostPorts(profileManagerApiPort, taskManagerApiPort, interactionProtocolEngineApiPort);

    taskManagerApi = Containers.createAndStartContainersForTaskManager(taskManagerApiPort, profileManagerApi, interactionProtocolEngineApi, serviceApi, network);

    final GenericContainer<?> persistenceContainer = Containers.createMongoContainerFor(Containers.WENET_PROFILE_MANAGER_DB_NAME, network);
    persistenceContainer.start();

    return new String[] { "-papi.port=" + profileManagerApiPort, "-ppersistence.host=host.docker.internal", "-ppersistence.port=" + persistenceContainer.getMappedPort(Containers.EXPORT_MONGODB_PORT),
        "-pwenetComponents.taskManager=\"" + taskManagerApi + "\"", "-pwenetComponents.service=\"" + serviceApi + "\"", "-pwenetComponents.socialContextBuilder=\"" + socialContextBuilderApi + "\"" };

  }

  /**
   * {@inheritDoc}
   *
   * @see Main
   */
  @Override
  protected AbstractMain createMain() {

    return new Main();
  }

}
