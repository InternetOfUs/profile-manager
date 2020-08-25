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

package eu.internetofus.wenet_profile_manager.api.communities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link CommunitiesResource}.
 *
 * @see CommunitiesResource
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class CommunitiesResourceTest {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The profile manager mocked server.
   */
  protected static WeNetServiceMocker serviceMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMocker() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
    serviceMocker = WeNetServiceMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stop();
    serviceMocker.stop();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final WebClient client = WebClient.create(vertx);
    final JsonObject profileManagerConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileManagerConf);
    final JsonObject serviceConf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, serviceConf);
    WeNetServiceSimulator.register(vertx, client, serviceConf);

  }

  /**
   * Create a resource where the repository is a mocked class.
   *
   * @param vertx event bus to use.
   *
   * @return the created class with the mocked repository.
   */
  public static CommunitiesResource createCommunitiesResource(final Vertx vertx) {

    final CommunitiesResource resource = new CommunitiesResource();
    resource.vertx = vertx;
    resource.repository = mock(CommunitiesRepository.class);
    return resource;

  }

  /**
   * Check fail create community because repository can not store it.
   *
   * @param vertx       event bus to use.
   * @param testContext test context.
   */
  @Test
  public void shouldFailCreateCommunityBecasueRepositoryFailsToStore(final Vertx vertx, final VertxTestContext testContext) {

    final CommunitiesResource resource = createCommunitiesResource(vertx);

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {
      community.id = null;
      final OperationRequest context = mock(OperationRequest.class);
      resource.createCommunity(community.toJsonObject(), context, testContext.succeeding(create -> {

        assertThat(create.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        testContext.completeNow();
      }));
    }));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<CommunityProfile>>> storeHandler = ArgumentCaptor.forClass(Handler.class);
    verify(resource.repository, timeout(30000).times(1)).storeCommunity(any(), storeHandler.capture());
    storeHandler.getValue().handle(Future.failedFuture("Store community error"));

  }

  /**
   * Check fail update community because repository can not update it.
   *
   * @param vertx       event bus to use.
   * @param testContext test context.
   */
  @Test
  public void shouldFailUpdateCommunityBecasueRepositoryFailsToUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final CommunitiesResource resource = createCommunitiesResource(vertx);
    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final OperationRequest context = mock(OperationRequest.class);
      resource.updateCommunity(community.id, new JsonObject().put("name", "NEW NAME").put("appId", community.appId), context, testContext.succeeding(update -> {

        assertThat(update.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        testContext.completeNow();
      }));
    }));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<CommunityProfile>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(resource.repository, timeout(30000).times(1)).searchCommunity(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(new CommunityProfile()));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> updateHandler = ArgumentCaptor.forClass(Handler.class);
    verify(resource.repository, timeout(30000).times(1)).updateCommunity(any(CommunityProfile.class), updateHandler.capture());
    updateHandler.getValue().handle(Future.failedFuture("Update community error"));
  }

  /**
   * Check fail merge community because repository can not update it.
   *
   * @param vertx       event bus to use.
   * @param testContext test context.
   */
  @Test
  public void shouldFailMergeCommunityBecasueRepositoryFailsToUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final CommunitiesResource resource = createCommunitiesResource(vertx);
    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final OperationRequest context = mock(OperationRequest.class);
      resource.mergeCommunity(community.id, new JsonObject().put("name", "NEW NAME"), context, testContext.succeeding(update -> {

        assertThat(update.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        testContext.completeNow();
      }));

      @SuppressWarnings("unchecked")
      final ArgumentCaptor<Handler<AsyncResult<CommunityProfile>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
      verify(resource.repository, timeout(30000).times(1)).searchCommunity(any(), searchHandler.capture());
      searchHandler.getValue().handle(Future.succeededFuture(community));

    }));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> updateHandler = ArgumentCaptor.forClass(Handler.class);
    verify(resource.repository, timeout(30000).times(1)).updateCommunity(any(CommunityProfile.class), updateHandler.capture());
    updateHandler.getValue().handle(Future.failedFuture("Update community error"));

  }

}
