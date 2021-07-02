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

package eu.internetofus.wenet_profile_manager.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.vertx.ModelsPageContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test {@link CommunitiesRepository}
 *
 * @see CommunitiesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ MockitoExtension.class, VertxExtension.class })
public class CommunitiesRepositoryTest {

  /**
   * Verify that can not create community profiles page sort.
   *
   * @see CommunitiesRepository#createCommunityProfilesPageSort(List)
   */
  @Test
  public void shouldFailCreateCommunityProfilesPageSort() {

    final List<String> order = new ArrayList<>();
    order.add("-undefinedKey");
    assertThatThrownBy(() -> {
      CommunitiesRepository.createCommunityProfilesPageSort(order);
    }).isInstanceOf(ValidationErrorException.class);

  }

  /**
   * Verify that can not create community profiles page sort.
   *
   * @see CommunitiesRepository#createCommunityProfilesPageSort(List)
   */
  @Test
  public void shouldCreateCommunityProfilesPageSort() {

    final List<String> order = new ArrayList<>();
    order.add("+description");
    order.add("-name");
    order.add("members");
    order.add("appId");
    order.add("-keywords");
    final var sort = CommunitiesRepository.createCommunityProfilesPageSort(order);
    assertThat(sort).isNotNull();
    assertThat(sort.getInteger("appId")).isNotNull().isEqualTo(1);
    assertThat(sort.getInteger("name")).isNotNull().isEqualTo(-1);
    assertThat(sort.getInteger("description")).isNotNull().isEqualTo(1);
    assertThat(sort.getInteger("keywords")).isNotNull().isEqualTo(-1);
    assertThat(sort.getInteger("members.userId")).isNotNull().isEqualTo(1);

  }

  /**
   * Should not obtain community if the obtainer object not match a
   * {@link CommunityProfile}.
   *
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#searchCommunity(String, io.vertx.core.Handler)
   */
  @Test
  public void shouldFailSearchCommunityWhenFoundObjectNotMatch(final VertxTestContext testContext) {

    final var repository = spy(new DummyCommunitiesRepository());
    repository.searchCommunity("id").onComplete(testContext.failing(error -> testContext.completeNow()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(repository, timeout(30000).times(1)).searchCommunity(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(new JsonObject().put("udefinedKey", "value")));

  }

  /**
   * Should not store community if the stored object not match a
   * {@link CommunityProfile}.
   *
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#storeCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldFailStoreCommunityWhenStoredObjectNotMatch(final VertxTestContext testContext) {

    final var repository = spy(new DummyCommunitiesRepository());
    repository.storeCommunity(new CommunityProfile(), testContext.failing(error -> testContext.completeNow()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> storeHandler = ArgumentCaptor.forClass(Handler.class);
    verify(repository, timeout(30000).times(1)).storeCommunity(any(JsonObject.class), storeHandler.capture());
    storeHandler.getValue().handle(Future.succeededFuture(new JsonObject().put("udefinedKey", "value")));

  }

  /**
   * Should not update community because can not convert to an object.
   *
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#updateCommunity(CommunityProfile, Handler)
   */
  @Test
  public void shouldFailUpdateCommunityBecauseNoObject(final VertxTestContext testContext) {

    final var community = new CommunityProfile() {
      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject toJsonObjectWithEmptyValues() {

        return null;

      }
    };
    final Handler<AsyncResult<Void>> handler = testContext.failing(error -> testContext.completeNow());
    final var repository = spy(new DummyCommunitiesRepository());
    repository.updateCommunity(community, handler);

  }

  /**
   * Should not update community because can not convert to an object.
   *
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPageObject(eu.internetofus.common.vertx.ModelsPageContext,
   *      Handler)
   */
  @Test
  public void shouldFailRetrieveCommunityProfilesPageObjectWhenSearchFail(final VertxTestContext testContext) {

    final var repository = spy(new DummyCommunitiesRepository());
    final var context = new ModelsPageContext();
    context.query = CommunitiesRepository.createCommunityProfilesPageQuery("appId", "name", "description", null, null);
    context.sort = CommunitiesRepository.createCommunityProfilesPageSort(Arrays.asList("name", "-description"));
    context.offset = 3;
    context.limit = 11;
    repository.retrieveCommunityProfilesPageObject(context, testContext.failing(error -> testContext.completeNow()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(repository, timeout(30000).times(1)).retrieveCommunityProfilesPageObject(eq(context.query), eq(context.sort),
        eq(context.offset), eq(context.limit), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

  }

  /**
   * Should not update community because the obtained object not match.
   *
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPage(ModelsPageContext,
   *      Handler)
   */
  @Test
  public void shouldFailRetrieveCommunityProfilesPageWhenObjectNotMatch(final VertxTestContext testContext) {

    final var repository = spy(new DummyCommunitiesRepository());
    final var context = new ModelsPageContext();
    context.query = CommunitiesRepository.createCommunityProfilesPageQuery("appId", "name", "description",
        Arrays.asList("keywords"), Arrays.asList("members"));
    context.sort = CommunitiesRepository.createCommunityProfilesPageSort(Arrays.asList("-name", "description"));
    context.offset = 23;
    context.limit = 100;
    repository.retrieveCommunityProfilesPage(context, testContext.failing(error -> testContext.completeNow()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(repository, timeout(30000).times(1)).retrieveCommunityProfilesPageObject(eq(context.query), eq(context.sort),
        eq(context.offset), eq(context.limit), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(new JsonObject().put("udefinedKey", "value")));

  }

  /**
   * Should not update community because the obtained object not found.
   *
   * @param testContext context that executes the test.
   *
   * @see CommunitiesRepository#retrieveCommunityProfilesPage(ModelsPageContext,
   *      Handler)
   */
  @Test
  public void shouldFailRetrieveCommunityProfilesPageWhenObjectNotFound(final VertxTestContext testContext) {

    final var repository = spy(new DummyCommunitiesRepository());
    final var context = new ModelsPageContext();
    context.query = CommunitiesRepository.createCommunityProfilesPageQuery("appId", "name", "description",
        Arrays.asList("keywords"), Arrays.asList("members"));
    context.sort = CommunitiesRepository.createCommunityProfilesPageSort(Arrays.asList("-name", "description"));
    context.offset = 23;
    context.limit = 100;
    repository.retrieveCommunityProfilesPage(context, testContext.failing(error -> testContext.completeNow()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(repository, timeout(30000).times(1)).retrieveCommunityProfilesPageObject(eq(context.query), eq(context.sort),
        eq(context.offset), eq(context.limit), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

  }

}
