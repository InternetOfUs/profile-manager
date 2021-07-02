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

package eu.internetofus.wenet_profile_manager.api.communities;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.queryParam;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.CommunityMemberTest;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.CommunityProfileTest;
import eu.internetofus.common.components.profile_manager.CommunityProfilesPage;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.vertx.AbstractModelResourcesIT;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepository;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepositoryIT;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The integration test over the {@link Communities}.
 *
 * @see Communities
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class CommunitiesIT extends AbstractModelResourcesIT<CommunityProfile, String> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String modelPath() {

    return Communities.PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CommunityProfile createInvalidModel() {

    return new CommunityProfileTest().createModelExample(1);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<CommunityProfile> createValidModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(new CommunityProfileTest().createModelExample(index, vertx, testContext))
        .compose(model -> {
          model.id = null;
          return Future.succeededFuture(model);
        });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<CommunityProfile> storeModel(final CommunityProfile source, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeCommunity(source, vertx, testContext);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertThatCreatedEquals(final CommunityProfile source, final CommunityProfile target) {

    source.id = target.id;
    source._creationTs = target._creationTs;
    source._lastUpdateTs = target._lastUpdateTs;
    if (source.socialPractices != null && target.socialPractices != null
        && source.socialPractices.size() == target.socialPractices.size()) {

      final var max = source.socialPractices.size();
      for (var i = 0; i < max; i++) {

        source.socialPractices.get(i).id = target.socialPractices.get(i).id;
      }

    }

    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOf(final CommunityProfile model) {

    return model.id;
  }

  /**
   * Should not retrieve a page because the order is not right.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudFailRetrieveCommunityProfilesPageWithBadOrderParameter(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, this.modelPath()).with(queryParam("order", "undefinedKey")).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);
  }

  /**
   * Should retrieve the expected communities with the specified application
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudRetrieveCommunityProfilesPageMatchingAppId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeCommunityExample(1, vertx, testContext)).onSuccess(community -> {

      testRequest(client, HttpMethod.GET, this.modelPath()).with(queryParam("appId", community.appId)).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var page = assertThatBodyIs(CommunityProfilesPage.class, res);
        assertThat(page).isNotNull();
        assertThat(page.total).isEqualTo(1);
        assertThat(page.communities).isNotEmpty().hasSize(1).contains(community);

      }).send(testContext);

    });

  }

  /**
   * Should retrieve the expected communities with the specified name.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudRetrieveCommunityProfilesPageMatchingName(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var name = UUID.randomUUID().toString();
    final List<CommunityProfile> communities = new ArrayList<>();
    CommunitiesRepositoryIT.storeSomeCommunityProfiles(vertx, testContext,
        community -> community.name = name + "_" + communities.size(), 10, communities,
        testContext.succeeding(empty -> {

          testRequest(client, HttpMethod.GET, this.modelPath()).with(queryParam("name", "/" + name + "_.*/"),
              queryParam("order", "-name"), queryParam("offset", "3"), queryParam("limit", "5")).expect(res -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final var page = assertThatBodyIs(CommunityProfilesPage.class, res);
                assertThat(page).isNotNull();
                assertThat(page.total).isEqualTo(communities.size());
                Collections.reverse(communities);
                assertThat(page.communities).isNotEmpty().hasSize(5).isEqualTo(communities.subList(3, 8));

              }).send(testContext);

        }));

  }

  /**
   * Should retrieve the expected communities with the specified description.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudRetrieveCommunityProfilesPageMatchingDescription(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var description = UUID.randomUUID().toString();
    final List<CommunityProfile> communities = new ArrayList<>();
    CommunitiesRepositoryIT.storeSomeCommunityProfiles(vertx, testContext,
        community -> community.description = description, 10, communities, testContext.succeeding(empty -> {

          testRequest(client, HttpMethod.GET, this.modelPath()).with(queryParam("description", description),
              queryParam("order", "-appId,+name"), queryParam("offset", "5"), queryParam("limit", "3")).expect(res -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final var page = assertThatBodyIs(CommunityProfilesPage.class, res);
                assertThat(page).isNotNull();
                assertThat(page.total).isEqualTo(communities.size());
                communities.sort((c1, c2) -> {

                  var r = c2.appId.compareTo(c1.appId);
                  if (r == 0) {

                    r = c1.name.compareTo(c2.name);
                  }

                  return r;

                });
                assertThat(page.communities).isNotEmpty().hasSize(3).isEqualTo(communities.subList(5, 8));

              }).send(testContext);

        }));

  }

  /**
   * Should retrieve the expected communities with the specified keywords.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudRetrieveCommunityProfilesPageMatchingKeywords(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var keyword = UUID.randomUUID().toString();
    final List<CommunityProfile> communities = new ArrayList<>();
    CommunitiesRepositoryIT.storeSomeCommunityProfiles(vertx, testContext, community -> {
      community.keywords.add(keyword + "_1");
      community.keywords.add(keyword + "_2");
      community.keywords.add(keyword + "_11");
    }, 10, communities, testContext.succeeding(empty -> {

      testRequest(client, HttpMethod.GET, this.modelPath())
          .with(queryParam("keywords", keyword + "_2,/" + keyword + "_1.*/"), queryParam("order", "+name,-description"),
              queryParam("limit", "3"))
          .expect(res -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var page = assertThatBodyIs(CommunityProfilesPage.class, res);
            assertThat(page).isNotNull();
            assertThat(page.total).isEqualTo(communities.size());
            communities.sort((c1, c2) -> {

              var r = c1.name.compareTo(c2.name);
              if (r == 0) {

                r = c2.description.compareTo(c1.description);
              }

              return r;

            });
            assertThat(page.communities).isNotEmpty().hasSize(3).isEqualTo(communities.subList(0, 3));

          }).send(testContext);

    }));

  }

  /**
   * Should retrieve the expected communities with the specified members.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shoudRetrieveCommunityProfilesPageMatchingMembers(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new CommunityMemberTest().createModelExample(1, vertx, testContext))
        .onSuccess(member1 -> {

          testContext.assertComplete(new CommunityMemberTest().createModelExample(11, vertx, testContext))
              .onSuccess(member11 -> {

                final List<CommunityProfile> communities = new ArrayList<>();
                CommunitiesRepositoryIT.storeSomeCommunityProfiles(vertx, testContext, community -> {
                  community.members.add(member11);
                  community.members.add(member1);
                }, 10, communities, testContext.succeeding(empty -> {

                  testRequest(client, HttpMethod.GET, this.modelPath())
                      .with(queryParam("members", member1.userId + "," + member11.userId),
                          queryParam("order", "+name,-description"), queryParam("limit", "5"))
                      .expect(res -> {

                        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                        final var page = assertThatBodyIs(CommunityProfilesPage.class, res);
                        assertThat(page).isNotNull();
                        assertThat(page.total).isEqualTo(communities.size());
                        communities.sort((c1, c2) -> {

                          var r = c1.name.compareTo(c2.name);
                          if (r == 0) {

                            r = c2.description.compareTo(c1.description);
                          }

                          return r;

                        });
                        assertThat(page.communities).isNotEmpty().hasSize(5).isEqualTo(communities.subList(0, 5));

                      }).send(testContext);

                }));

              });

        });

  }

  /**
   * Verify that store an empty profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#createCommunity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreEmptyProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var profile = new CommunityProfile();
    profile._creationTs = 0;
    profile._lastUpdateTs = 1;
    testRequest(client, HttpMethod.POST, Communities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var stored = assertThatBodyIs(CommunityProfile.class, res);
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.id = stored.id;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile._creationTs = stored._creationTs;
      profile._lastUpdateTs = stored._lastUpdateTs;
      assertThat(stored).isEqualTo(profile);
      testContext.assertComplete(CommunitiesRepository.createProxy(vertx).searchCommunity(stored.id))
          .onSuccess(foundProfile -> testContext.verify(() -> {

            assertThat(foundProfile).isEqualTo(stored);
            testContext.completeNow();

          }));

    }).sendJson(profile.toJsonObject(), testContext, testContext.checkpoint(2));

  }

}
