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

package eu.internetofus.wenet_profile_manager.api.profiles;

import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.queryParam;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.AliveBirthDate;
import eu.internetofus.common.components.profile_manager.Gender;
import eu.internetofus.common.components.profile_manager.Language;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.PlannedActivity;
import eu.internetofus.common.components.profile_manager.RelevantLocation;
import eu.internetofus.common.components.profile_manager.RoutineTest;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationship;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipType;
import eu.internetofus.common.components.profile_manager.SocialPractice;
import eu.internetofus.common.components.profile_manager.UserName;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerService;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepositoryIT;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Profiles}.
 *
 * @see Profiles
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesIT {

  /**
   * Verify that return error when search an undefined profile.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotFoundProfileWithAnUndefinedProfileId(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefined-profile-identifier").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
      testContext.completeNow();

    }).send(testContext);
  }

  /**
   * Verify that return a defined profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldFoundProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final WeNetUserProfile found = assertThatBodyIs(WeNetUserProfile.class, res);
        assertThat(found).isEqualTo(profile);
        testContext.completeNow();

      })).send(testContext);

    }));

  }

  /**
   * Verify that can not store a bad profile.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreANonProfileObject(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty().isEqualTo("bad_profile");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
      testContext.completeNow();

    }).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
  }

  /**
   * Verify that can not store a bad profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotStoreProfileWithExistingId(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    WeNetProfileManagerService.createProxy(vertx).createProfile(new JsonObject(), testContext.succeeding(created -> {

      final WeNetUserProfile profile = new WeNetUserProfile();
      profile.id = created.getString("id");
      testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty().isEqualTo("bad_profile.id");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
        testContext.completeNow();

      }).sendJson(profile.toJsonObject(), testContext);

    }));
  }

  /**
   * Verify that store a profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(1, vertx, testContext, testContext.succeeding(profile -> {
      testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
        assertThat(stored).isNotNull().isNotEqualTo(profile);
        profile.id = stored.id;
        assertThat(stored).isNotEqualTo(profile);
        profile._creationTs = stored._creationTs;
        profile._lastUpdateTs = stored._lastUpdateTs;
        assertThat(stored).isNotEqualTo(profile);
        profile.norms.get(0).id = stored.norms.get(0).id;
        profile.plannedActivities.get(0).id = stored.plannedActivities.get(0).id;
        profile.plannedActivities.get(1).id = stored.plannedActivities.get(1).id;
        profile.relevantLocations.get(0).id = stored.relevantLocations.get(0).id;
        profile.socialPractices.get(0).id = stored.socialPractices.get(0).id;
        profile.socialPractices.get(0).materials.id = stored.socialPractices.get(0).materials.id;
        profile.socialPractices.get(0).competences.id = stored.socialPractices.get(0).competences.id;
        profile.socialPractices.get(0).norms.get(0).id = stored.socialPractices.get(0).norms.get(0).id;
        assertThat(stored).isEqualTo(profile);
        ProfilesRepository.createProxy(vertx).searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

          assertThat(foundProfile).isEqualTo(stored);
          testContext.completeNow();

        })));

      }).sendJson(profile.toJsonObject(), testContext);

    }));

  }

  /**
   * Verify that store an empty profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreEmptyProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final WeNetUserProfile profile = new WeNetUserProfile();
    profile._creationTs = 0;
    profile._lastUpdateTs = 1;
    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.id = stored.id;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile._creationTs = stored._creationTs;
      profile._lastUpdateTs = stored._lastUpdateTs;
      assertThat(stored).isEqualTo(profile);
      ProfilesRepository.createProxy(vertx).searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

        assertThat(foundProfile).isEqualTo(stored);
        testContext.completeNow();

      })));

    }).sendJson(profile.toJsonObject(), testContext);

  }

  /**
   * Verify that store a simple profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreSimpleProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final WeNetUserProfile profile = new WeNetUserProfileTest().createModelExample(1);
    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.id = stored.id;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile._creationTs = stored._creationTs;
      profile._lastUpdateTs = stored._lastUpdateTs;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.norms.get(0).id = stored.norms.get(0).id;
      profile.plannedActivities.get(0).id = stored.plannedActivities.get(0).id;
      profile.relevantLocations.get(0).id = stored.relevantLocations.get(0).id;
      profile.socialPractices.get(0).id = stored.socialPractices.get(0).id;
      profile.socialPractices.get(0).materials.id = stored.socialPractices.get(0).materials.id;
      profile.socialPractices.get(0).competences.id = stored.socialPractices.get(0).competences.id;
      profile.socialPractices.get(0).norms.get(0).id = stored.socialPractices.get(0).norms.get(0).id;
      assertThat(stored).isEqualTo(profile);
      ProfilesRepository.createProxy(vertx).searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

        assertThat(foundProfile).isEqualTo(stored);
        testContext.completeNow();

      })));

    }).sendJson(profile.toJsonObject(), testContext);

  }

  /**
   * Verify that return error when try to update an undefined profile.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateProfileThatIsNotDefined(final WebClient client, final VertxTestContext testContext) {

    final WeNetUserProfile profile = new WeNetUserProfileTest().createBasicExample(1);
    testRequest(client, HttpMethod.PUT, Profiles.PATH + "/undefined-profile-identifier").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
      testContext.completeNow();

    }).sendJson(profile.toJsonObject(), testContext);
  }

  /**
   * Verify that return error when try to update with a model that is not a profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateProfileWithANotProfileObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
        testContext.completeNow();

      }).sendJson(new JsonObject().put("udefinedKey", "value"), testContext);
    }));
  }

  /**
   * Verify that not update a profile if any change is done.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateProfileBecauseNotChangesHasDone(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
        testContext.completeNow();

      }).sendJson(new JsonObject(), testContext);
    }));

  }

  /**
   * Verify that not update a profile because the source is not valid.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(String, io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldNotUpdateProfileBecauseBadSource(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty().endsWith(".nationality");
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
        testContext.completeNow();

      }).sendJson(new JsonObject().put("nationality", ValidationsTest.STRING_256), testContext);
    }));

  }

  /**
   * Verify that can update a basic profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateBasicProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfileTest().createBasicExample(1), vertx, testContext, testContext.succeeding(storedProfile -> {

      final WeNetUserProfile newProfile = new WeNetUserProfileTest().createBasicExample(2);
      newProfile.id = UUID.randomUUID().toString();
      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
        assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
        newProfile.id = storedProfile.id;
        newProfile._creationTs = storedProfile._creationTs;
        newProfile._lastUpdateTs = updated._lastUpdateTs;
        newProfile.norms.get(0).id = updated.norms.get(0).id;
        assertThat(updated).isEqualTo(newProfile);
        testContext.completeNow();

      })).sendJson(newProfile.toJsonObject(), testContext);

    }));

  }

  /**
   * Verify that can update a complex profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(storedProfile -> {

      final WeNetUserProfile newProfile = new WeNetUserProfileTest().createModelExample(2);
      newProfile.id = UUID.randomUUID().toString();
      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
        assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
        newProfile.id = storedProfile.id;
        newProfile._creationTs = storedProfile._creationTs;
        newProfile._lastUpdateTs = updated._lastUpdateTs;
        newProfile.norms.get(0).id = updated.norms.get(0).id;
        newProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
        newProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
        newProfile.relationships = updated.relationships;
        newProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
        newProfile.socialPractices.get(0).materials.id = updated.socialPractices.get(0).materials.id;
        newProfile.socialPractices.get(0).competences.id = updated.socialPractices.get(0).competences.id;
        newProfile.socialPractices.get(0).norms.get(0).id = updated.socialPractices.get(0).norms.get(0).id;
        assertThat(updated).isEqualTo(newProfile);

        ProfilesRepository.createProxy(vertx).searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> {

          assertThat(page.profiles).hasSize(1);
          assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
          assertThat(page.profiles.get(0).to).isEqualTo(storedProfile._lastUpdateTs);
          assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);
          testContext.completeNow();
        }));

      })).sendJson(newProfile.toJsonObject(), testContext);
    }));

  }

  /**
   * Verify that return error when delete an undefined profile.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldNotDeleteProfileWithAnUndefinedProfileId(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/undefined-profile-identifier").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
      testContext.completeNow();

    }).send(testContext);
  }

  /**
   * Verify that can delete a profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldDeleteProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(storedProfile -> {

      testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
        testContext.completeNow();

      })).send(testContext);

    }));

  }

  /**
   * Verify that can not obtain a historic page of a non defined profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotFoundHistoricOfANUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefined-profile-identifier" + Profiles.HISTORIC_PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
      testContext.completeNow();

    }).send(testContext);
  }

  /**
   * Verify that can not obtain a historic page of a non defined profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotFoundHistoricOfNonUpdateProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(storedProfile -> {
      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
        testContext.completeNow();

      }).send(testContext);

    }));
  }

  /**
   * Verify that can obtain a historic profile page.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePage(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final String userId = UUID.randomUUID().toString();
    final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
    page.total = 20;
    page.profiles = new ArrayList<>();
    ProfilesRepositoryIT.createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + userId + Profiles.HISTORIC_PATH).with(queryParam("limit", "100")).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final HistoricWeNetUserProfilesPage found = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, res);
        assertThat(found).isEqualTo(created);
        testContext.completeNow();

      }).send(testContext);
    }));

  }

  /**
   * Verify that can obtain a historic profile page for a range of dates.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageForARange(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final String userId = UUID.randomUUID().toString();
    final HistoricWeNetUserProfilesPage page = new HistoricWeNetUserProfilesPage();
    page.total = 20;
    page.profiles = new ArrayList<>();
    ProfilesRepositoryIT.createProfilePage(vertx, userId, page, testContext, testContext.succeeding(created -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + userId + Profiles.HISTORIC_PATH).with(queryParam("from", "50000"), queryParam("to", "150000"), queryParam("order", "DESC"), queryParam("offset", "5"), queryParam("limit", "3"))
      .expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final HistoricWeNetUserProfilesPage found = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, res);
        final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
        expected.offset = 5;
        expected.total = 10;
        expected.profiles = new ArrayList<>();
        expected.profiles.add(created.profiles.get(9));
        expected.profiles.add(created.profiles.get(8));
        expected.profiles.add(created.profiles.get(7));
        assertThat(found).isEqualTo(expected);
        testContext.completeNow();

      }).send(testContext);
    }));

  }

  /**
   * Verify that only update the middle name of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileNameMiddle(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.name = new UserName();
          final String newMiddleName = "NEW middle name";
          newProfile.name.middle = newMiddleName;
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
            final long old_lastUpdateTs = storedProfile._lastUpdateTs;
            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            final String old_middle = storedProfile.name.middle;
            storedProfile.name.middle = newMiddleName;
            assertThat(updated).isEqualTo(storedProfile);

            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> {

              assertThat(page.profiles).hasSize(1);
              assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
              assertThat(page.profiles.get(0).to).isEqualTo(storedProfile._lastUpdateTs);
              storedProfile._lastUpdateTs = old_lastUpdateTs;
              storedProfile.name.middle = old_middle;
              assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);
              testContext.completeNow();
            }));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that only update the birth date day of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileBirthDateDay(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.dateOfBirth = new AliveBirthDate();
          newProfile.dateOfBirth.day = 1;
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
            final long old_lastUpdateTs = storedProfile._lastUpdateTs;
            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.dateOfBirth.day = 1;
            assertThat(updated).isEqualTo(storedProfile);

            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> {

              assertThat(page.profiles).hasSize(1);
              assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
              assertThat(page.profiles.get(0).to).isEqualTo(storedProfile._lastUpdateTs);
              storedProfile._lastUpdateTs = old_lastUpdateTs;
              storedProfile.dateOfBirth.day = 24;
              assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);
              testContext.completeNow();
            }));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that only update the gender of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileGender(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.gender = Gender.M;
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
            final long old_lastUpdateTs = storedProfile._lastUpdateTs;
            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.gender = Gender.M;
            assertThat(updated).isEqualTo(storedProfile);

            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> {

              assertThat(page.profiles).hasSize(1);
              assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
              assertThat(page.profiles.get(0).to).isEqualTo(storedProfile._lastUpdateTs);
              storedProfile._lastUpdateTs = old_lastUpdateTs;
              storedProfile.gender = Gender.F;
              assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);
              testContext.completeNow();
            }));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the languages of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileLanguage(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.languages = new ArrayList<>();
          newProfile.languages.add(new Language());
          newProfile.languages.get(0).code = "es";
          newProfile.languages.add(new Language());
          newProfile.languages.get(1).name = "English";
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.languages = new ArrayList<>();
            storedProfile.languages.add(new Language());
            storedProfile.languages.get(0).code = "es";
            storedProfile.languages.add(new Language());
            storedProfile.languages.get(1).name = "English";
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              newProfile.languages = new ArrayList<>();
              newProfile.languages.add(new Language());
              newProfile.languages.get(0).code = "en";
              newProfile.languages.get(0).code = "English";
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                expected.profiles.add(new HistoricWeNetUserProfile());
                expected.profiles.get(1).from = updated._lastUpdateTs;
                expected.profiles.get(1).to = updated2._lastUpdateTs;
                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
                expected.total++;

                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                storedProfile.languages = new ArrayList<>();
                storedProfile.languages.add(new Language());
                storedProfile.languages.get(0).code = "en";
                storedProfile.languages.get(0).name = "English";
                assertThat(updated2).isEqualTo(storedProfile);
                repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page2 -> testContext.verify(() -> {

                  assertThat(page2).isEqualTo(expected);
                  testContext.completeNow();
                })));

              })).sendJson(newProfile.toJsonObject(), testContext);
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the norms of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldUpdateProfileNorm(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.norms = new ArrayList<>();
          newProfile.norms.add(new Norm());
          newProfile.norms.add(new Norm());
          newProfile.norms.get(1).id = storedProfile.norms.get(0).id;
          newProfile.norms.get(1).attribute = "Attribute";
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.norms.add(0, new Norm());
            storedProfile.norms.get(0).id = updated.norms.get(0).id;
            storedProfile.norms.get(1).attribute = "Attribute";
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              newProfile.norms = new ArrayList<>();
              newProfile.norms.add(new Norm());
              newProfile.norms.get(0).id = updated.norms.get(0).id;
              newProfile.norms.get(0).attribute = "Attribute2";
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                expected.profiles.add(new HistoricWeNetUserProfile());
                expected.profiles.get(1).from = updated._lastUpdateTs;
                expected.profiles.get(1).to = updated2._lastUpdateTs;
                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
                expected.total++;

                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                storedProfile.norms = new ArrayList<>();
                storedProfile.norms.remove(1);
                storedProfile.norms.get(0).attribute = "Attribute2";
                assertThat(updated2).isEqualTo(storedProfile);
                repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page2 -> testContext.verify(() -> {

                  assertThat(page2).isEqualTo(expected);
                  testContext.completeNow();
                })));

              })).sendJson(newProfile.toJsonObject(), testContext);
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the planned activities of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfilePlannedActivity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.plannedActivities = new ArrayList<>();
          newProfile.plannedActivities.add(new PlannedActivity());
          newProfile.plannedActivities.add(new PlannedActivity());
          newProfile.plannedActivities.get(1).id = storedProfile.plannedActivities.get(0).id;
          newProfile.plannedActivities.get(1).description = "Description";
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.plannedActivities.remove(1);
            storedProfile.plannedActivities.add(0, new PlannedActivity());
            storedProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
            storedProfile.plannedActivities.get(1).description = "Description";
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              newProfile.plannedActivities = new ArrayList<>();
              newProfile.plannedActivities.add(new PlannedActivity());
              newProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
              newProfile.plannedActivities.get(0).description = "Description2";
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                expected.profiles.add(new HistoricWeNetUserProfile());
                expected.profiles.get(1).from = updated._lastUpdateTs;
                expected.profiles.get(1).to = updated2._lastUpdateTs;
                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
                expected.total++;

                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                storedProfile.plannedActivities = new ArrayList<>();
                storedProfile.plannedActivities.remove(1);
                storedProfile.plannedActivities.get(0).description = "Description2";
                assertThat(updated2).isEqualTo(storedProfile);
                repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page2 -> testContext.verify(() -> {

                  assertThat(page2).isEqualTo(expected);
                  testContext.completeNow();
                })));

              })).sendJson(newProfile.toJsonObject(), testContext);
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the relevant locations of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileRelevantLocation(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.relevantLocations = new ArrayList<>();
          newProfile.relevantLocations.add(new RelevantLocation());
          newProfile.relevantLocations.add(new RelevantLocation());
          newProfile.relevantLocations.get(1).id = storedProfile.relevantLocations.get(0).id;
          newProfile.relevantLocations.get(1).label = "Label";
          newProfile.relevantLocations.get(1).latitude = -24;
          newProfile.relevantLocations.get(1).longitude = 24;
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.relevantLocations.add(0, new RelevantLocation());
            storedProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
            storedProfile.relevantLocations.get(1).label = "Label";
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              newProfile.relevantLocations = new ArrayList<>();
              newProfile.relevantLocations.add(new RelevantLocation());
              newProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
              newProfile.relevantLocations.get(0).label = "Label2";
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                expected.profiles.add(new HistoricWeNetUserProfile());
                expected.profiles.get(1).from = updated._lastUpdateTs;
                expected.profiles.get(1).to = updated2._lastUpdateTs;
                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
                expected.total++;

                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                storedProfile.relevantLocations = new ArrayList<>();
                storedProfile.relevantLocations.remove(1);
                storedProfile.relevantLocations.get(0).label = "Label2";
                assertThat(updated2).isEqualTo(storedProfile);
                repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page2 -> testContext.verify(() -> {

                  assertThat(page2).isEqualTo(expected);
                  testContext.completeNow();
                })));

              })).sendJson(newProfile.toJsonObject(), testContext);
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the social practices of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileSocialPractice(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.socialPractices = new ArrayList<>();
          newProfile.socialPractices.add(new SocialPractice());
          newProfile.socialPractices.add(new SocialPractice());
          newProfile.socialPractices.get(1).id = storedProfile.socialPractices.get(0).id;
          newProfile.socialPractices.get(1).label = "Label";
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.socialPractices.add(0, new SocialPractice());
            storedProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
            storedProfile.socialPractices.get(1).label = "Label";
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              newProfile.socialPractices = new ArrayList<>();
              newProfile.socialPractices.add(new SocialPractice());
              newProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
              newProfile.socialPractices.get(0).label = "Label2";
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                expected.profiles.add(new HistoricWeNetUserProfile());
                expected.profiles.get(1).from = updated._lastUpdateTs;
                expected.profiles.get(1).to = updated2._lastUpdateTs;
                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
                expected.total++;

                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                storedProfile.socialPractices = new ArrayList<>();
                storedProfile.socialPractices.remove(1);
                storedProfile.socialPractices.get(0).label = "Label2";
                assertThat(updated2).isEqualTo(storedProfile);
                repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page2 -> testContext.verify(() -> {

                  assertThat(page2).isEqualTo(expected);
                  testContext.completeNow();
                })));

              })).sendJson(newProfile.toJsonObject(), testContext);
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the personal behaviors of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfilePersonalBehavior(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.personalBehaviors = new ArrayList<>();
          newProfile.personalBehaviors.add(new RoutineTest().createModelExample(1));
          newProfile.personalBehaviors.add(new RoutineTest().createModelExample(2));
          newProfile.personalBehaviors.get(0).user_id = storedProfile.id;
          newProfile.personalBehaviors.get(1).user_id = storedProfile.id;
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.personalBehaviors = new ArrayList<>();
            storedProfile.personalBehaviors.add(new RoutineTest().createModelExample(1));
            storedProfile.personalBehaviors.add(new RoutineTest().createModelExample(2));
            storedProfile.personalBehaviors.get(0).user_id = storedProfile.id;
            storedProfile.personalBehaviors.get(1).user_id = storedProfile.id;
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              testContext.completeNow();
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can update the relationships of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileRelationship(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);

        repository.storeProfile(created, testContext.succeeding(storedProfile -> {

          final WeNetUserProfile newProfile = new WeNetUserProfile();
          newProfile.relationships = new ArrayList<>();
          newProfile.relationships.add(new SocialNetworkRelationship());
          newProfile.relationships.get(0).userId = storedProfile.relationships.get(0).userId;
          newProfile.relationships.get(0).type = SocialNetworkRelationshipType.friend;
          newProfile.relationships.add(new SocialNetworkRelationship());
          newProfile.relationships.get(1).userId = storedProfile.relationships.get(0).userId;
          newProfile.relationships.get(1).type = SocialNetworkRelationshipType.acquaintance;
          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
            expected.profiles = new ArrayList<>();
            expected.profiles.add(new HistoricWeNetUserProfile());
            expected.profiles.get(0).from = storedProfile._creationTs;
            expected.profiles.get(0).to = updated._lastUpdateTs;
            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
            expected.total++;

            storedProfile._lastUpdateTs = updated._lastUpdateTs;
            storedProfile.relationships.add(0, new SocialNetworkRelationship());
            storedProfile.relationships.get(0).userId = storedProfile.relationships.get(1).userId;
            storedProfile.relationships.get(0).type = SocialNetworkRelationshipType.friend;
            assertThat(updated).isEqualTo(storedProfile);
            repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isEqualTo(expected);
              newProfile.relationships = new ArrayList<>();
              newProfile.relationships.add(new SocialNetworkRelationship());
              newProfile.relationships.get(0).userId = storedProfile.relationships.get(0).userId;
              newProfile.relationships.get(0).type = SocialNetworkRelationshipType.family;
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                expected.profiles.add(new HistoricWeNetUserProfile());
                expected.profiles.get(1).from = updated._lastUpdateTs;
                expected.profiles.get(1).to = updated2._lastUpdateTs;
                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
                expected.total++;

                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                storedProfile.relationships = new ArrayList<>();
                storedProfile.relationships.get(0).type = SocialNetworkRelationshipType.family;
                assertThat(updated2).isEqualTo(storedProfile);
                repository.searchHistoricProfilePage(storedProfile.id, 0, Long.MAX_VALUE, true, 0, 100, testContext.succeeding(page2 -> testContext.verify(() -> {

                  assertThat(page2).isEqualTo(expected);
                  testContext.completeNow();
                })));

              })).sendJson(newProfile.toJsonObject(), testContext);
            })));

          })).sendJson(newProfile.toJsonObject(), testContext);
        }));
      });
    }));

  }

  /**
   * Verify that can store a profile with only an identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreProfileWithOnlyAnId(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final long now = TimeManager.now();
    final String id = UUID.randomUUID().toString();
    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final WeNetUserProfile stored = assertThatBodyIs(WeNetUserProfile.class, res);
      assertThat(stored).isNotNull();
      assertThat(stored.id).isEqualTo(id);
      assertThat(stored._creationTs).isGreaterThanOrEqualTo(now);
      assertThat(stored._lastUpdateTs).isEqualTo(stored._creationTs);
      assertThat(stored.name).isNull();
      assertThat(stored.dateOfBirth).isNull();
      assertThat(stored.gender).isNull();
      assertThat(stored.email).isNull();
      assertThat(stored.phoneNumber).isNull();
      assertThat(stored.locale).isNull();
      assertThat(stored.avatar).isNull();
      assertThat(stored.nationality).isNull();
      assertThat(stored.languages).isNull();
      assertThat(stored.occupation).isNull();
      assertThat(stored.norms).isNull();
      assertThat(stored.plannedActivities).isNull();
      assertThat(stored.relevantLocations).isNull();
      assertThat(stored.relationships).isNull();
      assertThat(stored.socialPractices).isNull();
      assertThat(stored.personalBehaviors).isNull();
      ProfilesRepository.createProxy(vertx).searchProfile(stored.id, testContext.succeeding(foundProfile -> testContext.verify(() -> {

        assertThat(foundProfile).isEqualTo(stored);
        testContext.completeNow();

      })));

    }).sendJson(new JsonObject().put("id", id), testContext);

  }
}
