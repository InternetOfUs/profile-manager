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

package eu.internetofus.wenet_profile_manager;

import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import eu.internetofus.common.Containers;
import eu.internetofus.common.components.incentive_server.WeNetIncentiveServerMocker;
import eu.internetofus.common.components.service.WeNetServiceMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderMocker;
import eu.internetofus.common.vertx.AbstractMain;
import eu.internetofus.common.vertx.AbstractWeNetComponentIntegrationExtension;
import eu.internetofus.common.vertx.WeNetModuleContext;
import io.vertx.core.Vertx;
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

    final Vertx vertx = context.vertx;
    final WebClient client = WebClient.create(vertx);
    final JsonObject conf = context.configuration.getJsonObject("wenetComponents", new JsonObject());
    WeNetServiceSimulator.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] createMainStartArguments() {

    final Network network = Network.newNetwork();

    final int profileManagerApiPort = Containers.nextFreePort();
    final int taskManagerApiPort = Containers.nextFreePort();
    final int interactionProtocolEngineApiPort = Containers.nextFreePort();
    final int serviceApiPort = Containers.nextFreePort();
    final int socialContextBuilderApiPort = Containers.nextFreePort();
    final int incentiveServerApiPort = Containers.nextFreePort();
    Testcontainers.exposeHostPorts(profileManagerApiPort, taskManagerApiPort, interactionProtocolEngineApiPort, serviceApiPort, socialContextBuilderApiPort, incentiveServerApiPort);

    Containers.createAndStartContainersForTaskManager(taskManagerApiPort, profileManagerApiPort, interactionProtocolEngineApiPort, serviceApiPort, network);
    WeNetIncentiveServerMocker.start(interactionProtocolEngineApiPort);
    WeNetServiceMocker.start(serviceApiPort);
    WeNetSocialContextBuilderMocker.start(socialContextBuilderApiPort);
    WeNetIncentiveServerMocker.start(incentiveServerApiPort);

    final GenericContainer<?> persistenceContainer = Containers.createMongoContainerFor(Containers.WENET_PROFILE_MANAGER_DB_NAME, network);
    persistenceContainer.start();

    return new String[] { "-papi.port=" + profileManagerApiPort, "-ppersistence.host=localhost", "-ppersistence.port=" + persistenceContainer.getMappedPort(Containers.EXPORT_MONGODB_PORT),
        "-pwenetComponents.taskManager=\"http://localhost:" + taskManagerApiPort + "\"", "-pwenetComponents.interactionProtocolEngine=\"http://localhost:" + interactionProtocolEngineApiPort + "\"",
        "-pwenetComponents.service=\"http://localhost:" + serviceApiPort + "\"", "-pwenetComponents.socialContextBuilder=\"http://localhost:" + socialContextBuilderApiPort + "\"" };

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
