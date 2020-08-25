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

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.CommunitiesRepository;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Communities}.
 *
 * @see Communities
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class CommunitiesIT {

  /**
   * Verify that return error when search an undefined community.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#retrieveCommunity(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundCommunityWithAnUndefinedCommunityId(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Communities.PATH + "/undefined-community-identifier").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);
  }

  /**
   * Verify that return a defined community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#retrieveCommunity(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.GET, Communities.PATH + "/" + community.id).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var found = assertThatBodyIs(CommunityProfile.class, res);
        assertThat(found).isEqualTo(community);

      })).send(testContext);

    }));

  }

  /**
   * Verify that can not store a bad community.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#createCommunity(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreANonCommunityObject(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Communities.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty().isEqualTo("bad_community_profile");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
  }

  /**
   * Verify that can not store a bad community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#createCommunity(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreCommunityWithExistingId(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.POST, Communities.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty().isEqualTo("bad_community_profile.id");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(community.toJsonObject(), testContext);

    }));
  }

  /**
   * Verify that store a community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#createCommunity(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new CommunityProfileTest().createModelExample(1, vertx, testContext, testContext.succeeding(community -> {
      community.id = null;
      testRequest(client, HttpMethod.POST, Communities.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var stored = assertThatBodyIs(CommunityProfile.class, res);
        assertThat(stored).isNotNull().isNotEqualTo(community);
        community.id = stored.id;
        assertThat(stored).isNotEqualTo(community);
        community._creationTs = stored._creationTs;
        community._lastUpdateTs = stored._lastUpdateTs;
        assertThat(stored).isNotEqualTo(community);
        community.norms.get(0).id = stored.norms.get(0).id;
        community.socialPractices.get(0).id = stored.socialPractices.get(0).id;
        community.socialPractices.get(0).norms.get(0).id = stored.socialPractices.get(0).norms.get(0).id;
        assertThat(stored).isEqualTo(community);
        CommunitiesRepository.createProxy(vertx).searchCommunity(stored.id, testContext.succeeding(foundCommunity -> testContext.verify(() -> {

          assertThat(foundCommunity).isEqualTo(stored);
          testContext.completeNow();

        })));

      }).sendJson(community.toJsonObject(), testContext, testContext.checkpoint(2));

    }));

  }

  /**
   * Verify that return error when try to update an undefined community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#updateCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateCommunityThatIsNotDefined(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new CommunityProfileTest().createModelExample(1, vertx, testContext, testContext.succeeding(community -> {
      community.id = null;
      testRequest(client, HttpMethod.PUT, Communities.PATH + "/undefined-community-identifier").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(community.toJsonObject(), testContext);
    }));

  }

  /**
   * Verify that return error when try to update with a model that is not a community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#updateCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateCommunityWithANotCommunityObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.PUT, Communities.PATH + "/" + community.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
    }));
  }

  /**
   * Verify that return error when try to update with a community that is not valid.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#updateCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateCommunityWithABadCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final CommunityProfile newCommunity = new CommunityProfile();
      newCommunity.appId = community.appId;
      newCommunity.name = ValidationsTest.STRING_256;
      testRequest(client, HttpMethod.PUT, Communities.PATH + "/" + community.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty().contains("name");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(newCommunity.toJsonObject(), testContext);
    }));
  }

  /**
   * Verify that return error when try to update with a community that has the same vlaues that the original.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#updateCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateCommunityWithSameCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.PUT, Communities.PATH + "/" + community.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isEqualTo("community_to_update_equal_to_original");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(community.toJsonObject(), testContext);
    }));
  }

  /**
   * Verify that can update a community with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#retrieveCommunity(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(storedCommunity -> {

      new CommunityProfileTest().createModelExample(2, vertx, testContext, testContext.succeeding(newCommunity -> {

        newCommunity.id = null;
        testRequest(client, HttpMethod.PUT, Communities.PATH + "/" + storedCommunity.id).expect(res -> testContext.verify(() -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final CommunityProfile updated = assertThatBodyIs(CommunityProfile.class, res);
          assertThat(updated).isNotEqualTo(storedCommunity).isNotEqualTo(newCommunity);
          newCommunity.id = storedCommunity.id;
          newCommunity._creationTs = storedCommunity._creationTs;
          newCommunity._lastUpdateTs = updated._lastUpdateTs;
          newCommunity.norms.get(0).id = updated.norms.get(0).id;
          newCommunity.socialPractices.get(0).id = updated.socialPractices.get(0).id;
          newCommunity.socialPractices.get(0).norms.get(0).id = updated.socialPractices.get(0).norms.get(0).id;
          assertThat(updated).isEqualTo(newCommunity);

        })).sendJson(newCommunity.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Verify that return error when try to merge an undefined community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#mergeCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotMergeCommunityThatIsNotDefined(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new CommunityProfileTest().createModelExample(2, vertx, testContext, testContext.succeeding(community -> {

      community.id = null;

      testRequest(client, HttpMethod.PATCH, Communities.PATH + "/undefined-community-identifier").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(community.toJsonObject(), testContext);
    }));
  }

  /**
   * Verify that return error when try to merge with a model that is not a community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#mergeCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotMergeCommunityWithANotCommunityObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.PATCH, Communities.PATH + "/" + community.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
    }));
  }

  /**
   * Verify that not merge a community if any change is done.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#mergeCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotMergeCommunityBecauseNotChangesHasDone(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.PATCH, Communities.PATH + "/" + community.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject(), testContext);
    }));

  }

  /**
   * Verify that not merge a community because the source is not valid.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#mergeCommunity(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotMergeCommunityBecauseBadSource(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      testRequest(client, HttpMethod.PATCH, Communities.PATH + "/" + community.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty().endsWith(".name");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject().put("name", ValidationsTest.STRING_256), testContext);
    }));

  }

  /**
   * Verify that can merge a complex community with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#retrieveCommunity(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldMergeCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(target -> {

      new CommunityProfileTest().createModelExample(2, vertx, testContext, testContext.succeeding(source -> {
        source.id = UUID.randomUUID().toString();
        testRequest(client, HttpMethod.PATCH, Communities.PATH + "/" + target.id).expect(res -> testContext.verify(() -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final CommunityProfile merged = assertThatBodyIs(CommunityProfile.class, res);
          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          source.id = target.id;
          source._creationTs = target._creationTs;
          source._lastUpdateTs = merged._lastUpdateTs;
          source.norms.get(0).id = merged.norms.get(0).id;
          source.socialPractices.get(0).id = merged.socialPractices.get(0).id;
          source.socialPractices.get(0).norms.get(0).id = merged.socialPractices.get(0).norms.get(0).id;
          assertThat(merged).isEqualTo(source);

        })).sendJson(source.toJsonObject(), testContext);

      }));
    }));

  }

  /**
   * Verify that return error when delete an undefined community.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#retrieveCommunity(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotDeleteCommunityWithAnUndefinedCommunityId(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.DELETE, Communities.PATH + "/undefined-community-identifier").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);
  }

  /**
   * Verify that can delete a community.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Communities#retrieveCommunity(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldDeleteCommunity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(storedCommunity -> {

      testRequest(client, HttpMethod.DELETE, Communities.PATH + "/" + storedCommunity.id).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

      })).send(testContext);

    }));

  }

  // /**
  // * Verify that can update the social practices of an user.
  // *
  // * @param vertx event bus to use.
  // * @param client to connect to the server.
  // * @param testContext context to test.
  // *
  // * @see Communities#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
  // */
  // @Test
  // public void shouldUpdateCommunitiesocialPractice(final Vertx vertx, final WebClient client, final VertxTestContext
  // testContext) {
  //
  // new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {
  //
  // assertIsValid(created, vertx, testContext, () -> {
  //
  // final CommunitiesRepository repository = CommunitiesRepository.createProxy(vertx);
  //
  // repository.storeProfile(created, testContext.succeeding(storedProfile -> {
  //
  // final WeNetUserProfile newProfile = new WeNetUserProfile();
  // newProfile.socialPractices = new ArrayList<>();
  // newProfile.socialPractices.add(new SocialPractice());
  // newProfile.socialPractices.add(new SocialPractice());
  // newProfile.socialPractices.get(1).id = storedProfile.socialPractices.get(0).id;
  // newProfile.socialPractices.get(1).label = "Label";
  // final Checkpoint checkpoint = testContext.checkpoint(4);
  // testRequest(client, HttpMethod.PUT, Communities.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() ->
  // {
  //
  // assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
  // final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
  // assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
  //
  // final HistoricWeNetUserCommunitiesPage expected = new HistoricWeNetUserCommunitiesPage();
  // expected.communities = new ArrayList<>();
  // expected.communities.add(new HistoricWeNetUserProfile());
  // expected.communities.get(0).from = storedProfile._creationTs;
  // expected.communities.get(0).to = updated._lastUpdateTs;
  // expected.communities.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
  // expected.total++;
  //
  // storedProfile._lastUpdateTs = updated._lastUpdateTs;
  // storedProfile.socialPractices.add(0, new SocialPractice());
  // storedProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
  // storedProfile.socialPractices.get(1).label = "Label";
  // assertThat(updated).isEqualTo(storedProfile);
  // testRequest(client, HttpMethod.GET, Communities.PATH + "/" + storedProfile.id +
  // Communities.HISTORIC_PATH).expect(resPage
  // -> {
  //
  // assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
  // final HistoricWeNetUserCommunitiesPage page = assertThatBodyIs(HistoricWeNetUserCommunitiesPage.class, resPage);
  // assertThat(page).isEqualTo(expected);
  // newProfile.socialPractices = new ArrayList<>();
  // newProfile.socialPractices.add(new SocialPractice());
  // newProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
  // newProfile.socialPractices.get(0).label = "Label2";
  // testRequest(client, HttpMethod.PUT, Communities.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(()
  // -> {
  //
  // assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
  // final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
  // assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
  //
  // expected.communities.add(new HistoricWeNetUserProfile());
  // expected.communities.get(1).from = updated._lastUpdateTs;
  // expected.communities.get(1).to = updated2._lastUpdateTs;
  // expected.communities.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
  // expected.total++;
  //
  // storedProfile._lastUpdateTs = updated2._lastUpdateTs;
  // storedProfile.socialPractices = new ArrayList<>();
  // storedProfile.socialPractices.remove(1);
  // storedProfile.socialPractices.get(0).label = "Label2";
  // assertThat(updated2).isEqualTo(storedProfile);
  // testRequest(client, HttpMethod.GET, Communities.PATH + "/" + storedProfile.id +
  // Communities.HISTORIC_PATH).expect(resPage2
  // -> {
  //
  // assertThat(resPage2.statusCode()).isEqualTo(Status.OK.getStatusCode());
  // final HistoricWeNetUserCommunitiesPage page2 = assertThatBodyIs(HistoricWeNetUserCommunitiesPage.class, resPage2);
  // assertThat(page2).isEqualTo(expected);
  //
  // }).send(testContext, checkpoint);
  //
  // })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
  //
  // }).send(testContext, checkpoint);
  //
  // })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
  // }));
  // });
  // }));
  //
  // }

}
