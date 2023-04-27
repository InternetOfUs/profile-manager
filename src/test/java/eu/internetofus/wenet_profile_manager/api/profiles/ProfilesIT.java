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

package eu.internetofus.wenet_profile_manager.api.profiles;

import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.queryParam;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.AliveBirthDate;
import eu.internetofus.common.components.models.CommunityMemberTest;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.PlannedActivity;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.RelevantLocation;
import eu.internetofus.common.components.models.RoutineTest;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.UserName;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import eu.internetofus.common.components.profile_manager.HistoricWeNetUserProfile;
import eu.internetofus.common.components.profile_manager.HistoricWeNetUserProfilesPage;
import eu.internetofus.common.components.profile_manager.TrustAggregator;
import eu.internetofus.common.components.profile_manager.UserPerformanceRatingEvent;
import eu.internetofus.common.components.profile_manager.UserPerformanceRatingEventTest;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderSimulator;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.TimeManager;
import eu.internetofus.common.vertx.AbstractModelResourcesIT;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepositoryIT;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.Response.Status;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The integration test over the {@link Profiles}.
 *
 * @see Profiles
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class ProfilesIT extends AbstractModelResourcesIT<WeNetUserProfile, String> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String modelPath() {

    return Profiles.PATH;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WeNetUserProfile createInvalidModel() {

    final var model = new WeNetUserProfile();
    model.phoneNumber = "+123456789012345678901234567";
    return model;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<WeNetUserProfile> createValidModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(new WeNetUserProfileTest().createModelExample(index, vertx, testContext));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertThatCreatedEquals(final WeNetUserProfile source, final WeNetUserProfile target) {

    source.id = target.id;
    source._creationTs = target._creationTs;
    source._lastUpdateTs = target._lastUpdateTs;
    if (source.plannedActivities != null && target.plannedActivities != null
        && source.plannedActivities.size() == target.plannedActivities.size()) {

      final var max = source.plannedActivities.size();
      for (var i = 0; i < max; i++) {

        source.plannedActivities.get(i).id = target.plannedActivities.get(i).id;
      }

    }
    if (source.relevantLocations != null && target.relevantLocations != null
        && source.relevantLocations.size() == target.relevantLocations.size()) {

      final var max = source.relevantLocations.size();
      for (var i = 0; i < max; i++) {

        source.relevantLocations.get(i).id = target.relevantLocations.get(i).id;
      }

    }
    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOf(final WeNetUserProfile model) {

    return model.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<WeNetUserProfile> storeModel(final WeNetUserProfile source, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(StoreServices.storeProfile(source, vertx, testContext));

  }

  /**
   * Verify that store an empty profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldStoreEmptyProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var profile = new WeNetUserProfile();
    profile._creationTs = 0;
    profile._lastUpdateTs = 1;
    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var stored = assertThatBodyIs(WeNetUserProfile.class, res);
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.id = stored.id;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile._creationTs = stored._creationTs;
      profile._lastUpdateTs = stored._lastUpdateTs;
      assertThat(stored).isEqualTo(profile);
      testContext.assertComplete(ProfilesRepository.createProxy(vertx).searchProfile(stored.id))
          .onSuccess(foundProfile -> testContext.verify(() -> {

            assertThat(foundProfile).isEqualTo(stored);
            testContext.completeNow();

          }));

    }).sendJson(profile.toJsonObject(), testContext, testContext.checkpoint(2));

  }

  /**
   * Verify that store a simple profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreSimpleProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var profile = new WeNetUserProfileTest().createModelExample(1);
    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var stored = assertThatBodyIs(WeNetUserProfile.class, res);
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.id = stored.id;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile._creationTs = stored._creationTs;
      profile._lastUpdateTs = stored._lastUpdateTs;
      assertThat(stored).isNotNull().isNotEqualTo(profile);
      profile.plannedActivities.get(0).id = stored.plannedActivities.get(0).id;
      profile.relevantLocations.get(0).id = stored.relevantLocations.get(0).id;
      assertThat(stored).isEqualTo(profile);
      testContext.assertComplete(ProfilesRepository.createProxy(vertx).searchProfile(stored.id))
          .onSuccess(foundProfile -> testContext.verify(() -> {

            assertThat(foundProfile).isEqualTo(stored);
            testContext.completeNow();

          }));

    }).sendJson(profile.toJsonObject(), testContext, testContext.checkpoint(2));

  }

  /**
   * Verify that can update a profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileAddingHistory(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfileExample(1, vertx, testContext)).onSuccess(storedProfile -> {

      testContext.assertComplete(new WeNetUserProfileTest().createModelExample(2, vertx, testContext))
          .onSuccess(newProfile -> {

            final var checkpoint = testContext.checkpoint(2);
            testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                .with(queryParam("storeProfileChangesInHistory", "true")).expect(res -> testContext.verify(() -> {

                  assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                  final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                  assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
                  newProfile.id = storedProfile.id;
                  newProfile._creationTs = storedProfile._creationTs;
                  newProfile._lastUpdateTs = updated._lastUpdateTs;
                  newProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
                  newProfile.plannedActivities.get(1).id = updated.plannedActivities.get(1).id;
                  newProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
                  assertThat(updated).isEqualTo(newProfile);

                  testContext.assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                      .getSocialNotificationProfileUpdate(storedProfile.id)).onSuccess(notification -> {

                        testContext.verify(() -> {

                          assertThat(notification.updatedFieldNames).isNotNull().contains("name", "dateOfBirth",
                              "gender", "email", "phoneNumber", "locale", "avatar", "nationality", "occupation",
                              "norms", "plannedActivities", "relevantLocations", "personalBehaviors", "materials",
                              "competences", "meanings");

                        });

                        vertx.setTimer(1000, any -> {

                          testRequest(client, HttpMethod.GET,
                              Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

                                assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);

                                assertThat(page.profiles).hasSize(1);
                                assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
                                assertThat(page.profiles.get(0).to).isCloseTo(storedProfile._lastUpdateTs,
                                    Offset.offset(1l));
                                assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);

                              }).send(testContext, checkpoint);

                        });

                      });

                })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);

          });

    });

  }

  /**
   * Verify that can update a profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileNoAddingHistory(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfileExample(1, vertx, testContext)).onSuccess(storedProfile -> {

      testContext.assertComplete(new WeNetUserProfileTest().createModelExample(2, vertx, testContext))
          .onSuccess(newProfile -> {

            final var checkpoint = testContext.checkpoint(2);
            testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                .expect(res -> testContext.verify(() -> {

                  assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                  final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                  assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
                  newProfile.id = storedProfile.id;
                  newProfile._creationTs = storedProfile._creationTs;
                  newProfile._lastUpdateTs = updated._lastUpdateTs;
                  newProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
                  newProfile.plannedActivities.get(1).id = updated.plannedActivities.get(1).id;
                  newProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
                  assertThat(updated).isEqualTo(newProfile);

                  testContext.assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                      .getSocialNotificationProfileUpdate(storedProfile.id)).onSuccess(notification -> {

                        testContext.verify(() -> {

                          assertThat(notification.updatedFieldNames).isNotNull().contains("name", "dateOfBirth",
                              "gender", "email", "phoneNumber", "locale", "avatar", "nationality", "occupation",
                              "norms", "plannedActivities", "relevantLocations", "personalBehaviors", "materials",
                              "competences", "meanings");

                        });

                        vertx.setTimer(1000, any -> {

                          testRequest(client, HttpMethod.GET,
                              Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

                                assertThat(resPage.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());

                              }).send(testContext, checkpoint);

                        });

                      });

                })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);

          });

    });

  }

  /**
   * Verify that can merge a basic profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldMergeBasicProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var basicExample = new WeNetUserProfileTest().createBasicExample(1);
    testContext.assertComplete(StoreServices.storeProfile(basicExample, vertx, testContext))
        .onSuccess(storedProfile -> {

          final var newProfile = new WeNetUserProfileTest().createBasicExample(2);
          newProfile.id = UUID.randomUUID().toString();
          testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + storedProfile.id)
              .expect(res -> testContext.verify(() -> {

                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final var merged = assertThatBodyIs(WeNetUserProfile.class, res);
                assertThat(merged).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
                newProfile.id = storedProfile.id;
                newProfile._creationTs = storedProfile._creationTs;
                newProfile._lastUpdateTs = merged._lastUpdateTs;
                assertThat(merged).isEqualTo(newProfile);

              })).sendJson(newProfile.toJsonObject(), testContext);

        });

  }

  /**
   * Verify that can merge a complex profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldMergeProfileAddingHistoric(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfileExample(1, vertx, testContext)).onSuccess(storedProfile -> {

      final var newProfile = new WeNetUserProfileTest().createModelExample(2);
      newProfile.id = UUID.randomUUID().toString();
      final var checkpoint = testContext.checkpoint(2);
      testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + storedProfile.id)
          .with(queryParam("storeProfileChangesInHistory", "true")).expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var merged = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(merged).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
            newProfile.id = storedProfile.id;
            newProfile._creationTs = storedProfile._creationTs;
            newProfile._lastUpdateTs = merged._lastUpdateTs;
            newProfile.plannedActivities.get(0).id = merged.plannedActivities.get(0).id;
            newProfile.relevantLocations.get(0).id = merged.relevantLocations.get(0).id;
            newProfile.personalBehaviors = storedProfile.personalBehaviors;
            assertThat(merged).isEqualTo(newProfile);

            testContext.assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                .getSocialNotificationProfileUpdate(storedProfile.id)).onSuccess(notification -> {

                  testContext.verify(() -> {

                    assertThat(notification.updatedFieldNames).isNotNull().contains("name", "dateOfBirth", "gender",
                        "email", "phoneNumber", "locale", "avatar", "nationality", "norms", "occupation",
                        "plannedActivities", "relevantLocations", "materials", "competences", "meanings");
                  });

                  vertx.setTimer(1000, any -> {
                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);

                          assertThat(page.profiles).hasSize(1);
                          assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
                          assertThat((Long) page.profiles.get(0).to).isCloseTo(storedProfile._lastUpdateTs,
                              Offset.offset((Long) 1L));
                          assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);

                        }).send(testContext, checkpoint);

                  });

                });

          })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
    });

  }

  /**
   * Verify that can merge a complex profile with another.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldMergeProfileNoAddingHistoric(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfileExample(1, vertx, testContext)).onSuccess(storedProfile -> {

      final var newProfile = new WeNetUserProfileTest().createModelExample(2);
      newProfile.id = UUID.randomUUID().toString();
      final var checkpoint = testContext.checkpoint(2);
      testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + storedProfile.id)
          .expect(res -> testContext.verify(() -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var merged = assertThatBodyIs(WeNetUserProfile.class, res);
            assertThat(merged).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
            newProfile.id = storedProfile.id;
            newProfile._creationTs = storedProfile._creationTs;
            newProfile._lastUpdateTs = merged._lastUpdateTs;
            newProfile.plannedActivities.get(0).id = merged.plannedActivities.get(0).id;
            newProfile.relevantLocations.get(0).id = merged.relevantLocations.get(0).id;
            newProfile.personalBehaviors = storedProfile.personalBehaviors;
            assertThat(merged).isEqualTo(newProfile);

            testContext.assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                .getSocialNotificationProfileUpdate(storedProfile.id)).onSuccess(notification -> {

                  testContext.verify(() -> {

                    assertThat(notification.updatedFieldNames).isNotNull().contains("name", "dateOfBirth", "gender",
                        "email", "phoneNumber", "locale", "avatar", "nationality", "norms", "occupation",
                        "plannedActivities", "relevantLocations", "materials", "competences", "meanings");
                  });

                  vertx.setTimer(1000, any -> {
                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());

                        }).send(testContext, checkpoint);

                  });

                });

          })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
    });

  }

  /**
   * Verify that can not obtain a historic page of a non defined profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, Long, Long, String, int,
   *      int, io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotFoundHistoricOfANUndefinedProfile(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefined-profile-identifier" + Profiles.HISTORIC_PATH)
        .expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext);
  }

  /**
   * Verify that can not obtain a historic page of a non defined profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, Long, Long, String, int,
   *      int, io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldNotFoundHistoricOfNonUpdateProfile(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
        .onSuccess(storedProfile -> {
          testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
              .expect(res -> {

                assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
                final var error = assertThatBodyIs(ErrorMessage.class, res);
                assertThat(error.code).isNotEmpty();
                assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

              }).send(testContext);

        });
  }

  /**
   * Verify that can obtain a historic profile page.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, Long, Long, String, int,
   *      int, io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePage(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    ProfilesRepositoryIT.createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + userId + Profiles.HISTORIC_PATH)
          .with(queryParam("limit", "100")).expect(res -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var found = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, res);
            assertThat(found).isEqualTo(created);

          }).send(testContext);
    });

  }

  /**
   * Verify that can obtain a historic profile page for a range of dates.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfileHistoricPage(String, Long, Long, String, int,
   *      int, io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFoundHistoricProfilePageForARange(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    ProfilesRepositoryIT.createProfilePage(20, userId, vertx, testContext).onSuccess(created -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + userId + Profiles.HISTORIC_PATH)
          .with(queryParam("from", "50000"), queryParam("to", "150000"), queryParam("order", "-"),
              queryParam("offset", "5"), queryParam("limit", "3"))
          .expect(res -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var found = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, res);
            final var expected = new HistoricWeNetUserProfilesPage();
            expected.offset = 5;
            expected.total = 10;
            expected.profiles = new ArrayList<>();
            expected.profiles.add(created.profiles.get(9));
            expected.profiles.add(created.profiles.get(8));
            expected.profiles.add(created.profiles.get(7));
            assertThat(found).isEqualTo(expected);

          }).send(testContext);
    });

  }

  /**
   * Verify that only update the middle name of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileNameMiddle(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new WeNetUserProfileTest().createModelExample(1, vertx, testContext))
        .onSuccess(created -> {

          assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

            final var repository = ProfilesRepository.createProxy(vertx);

            testContext.assertComplete(repository.storeProfile(created)).onSuccess(storedProfile -> {

              final var newProfile = new WeNetUserProfile();
              newProfile.name = new UserName();
              final var newMiddleName = "NEW middle name";
              newProfile.name.middle = newMiddleName;
              final var checkpoint = testContext.checkpoint(2);
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                  .expect(res -> testContext.verify(() -> {

                    assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                    final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                    assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
                    final var old_lastUpdateTs = storedProfile._lastUpdateTs;
                    storedProfile._lastUpdateTs = updated._lastUpdateTs;
                    final var old_middle = storedProfile.name.middle;
                    storedProfile.name.middle = newMiddleName;
                    assertThat(updated).isEqualTo(storedProfile);

                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);

                          assertThat(page.profiles).hasSize(1);
                          assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
                          assertThat(page.profiles.get(0).to).isCloseTo(storedProfile._lastUpdateTs, Offset.offset(1L));
                          storedProfile._lastUpdateTs = old_lastUpdateTs;
                          storedProfile.name.middle = old_middle;
                          assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);

                        }).send(testContext, checkpoint);

                  })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
            });
          });
        });

  }

  /**
   * Verify that only update the birth date day of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileBirthDateDay(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new WeNetUserProfileTest().createModelExample(1, vertx, testContext))
        .onSuccess(created -> {

          assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

            final var repository = ProfilesRepository.createProxy(vertx);

            testContext.assertComplete(repository.storeProfile(created)).onSuccess(storedProfile -> {

              final var newProfile = new WeNetUserProfile();
              newProfile.dateOfBirth = new AliveBirthDate();
              newProfile.dateOfBirth.day = 1;
              final var checkpoint = testContext.checkpoint(2);
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                  .expect(res -> testContext.verify(() -> {

                    assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                    final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                    assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
                    final var old_lastUpdateTs = storedProfile._lastUpdateTs;
                    storedProfile._lastUpdateTs = updated._lastUpdateTs;
                    storedProfile.dateOfBirth.day = 1;
                    assertThat(updated).isEqualTo(storedProfile);

                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);

                          assertThat(page.profiles).hasSize(1);
                          assertThat(page.profiles.get(0).from).isEqualTo(storedProfile._creationTs);
                          assertThat(page.profiles.get(0).to).isCloseTo(storedProfile._lastUpdateTs, Offset.offset(1L));
                          storedProfile._lastUpdateTs = old_lastUpdateTs;
                          storedProfile.dateOfBirth.day = 24;
                          assertThat(page.profiles.get(0).profile).isEqualTo(storedProfile);

                        }).send(testContext, checkpoint);

                  })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
            });
          });
        });

  }

  /**
   * Verify that only update the gender of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(Boolean, String, JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileGender(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(storedProfile -> {

      final var newProfile = new WeNetUserProfile();
      newProfile.gender = WeNetUserProfile.OTHER;
      testContext
          .assertComplete(WeNetProfileManager.createProxy(vertx).updateProfile(storedProfile.id, newProfile)
              .compose(ignored -> WeNetProfileManager.createProxy(vertx).retrieveProfile(storedProfile.id)))
          .onSuccess(updatedProfile -> testContext.verify(() -> {

            storedProfile._lastUpdateTs = updatedProfile._lastUpdateTs;
            storedProfile.gender = WeNetUserProfile.OTHER;
            assertThat(updatedProfile).isEqualTo(storedProfile);

            testContext
                .assertComplete(
                    WeNetProfileManager.createProxy(vertx).updateProfile(storedProfile.id, new WeNetUserProfile())
                        .compose(ignored -> WeNetProfileManager.createProxy(vertx).retrieveProfile(storedProfile.id)))
                .onSuccess(updatedProfile2 -> testContext.verify(() -> {

                  storedProfile._lastUpdateTs = updatedProfile2._lastUpdateTs;
                  storedProfile.gender = null;
                  assertThat(updatedProfile2).isEqualTo(storedProfile);
                  testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                      .expect(resPage -> {

                        assertThat(resPage.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());

                      }).send(testContext);

                }));

          }));

    });

  }

  /**
   * Verify that only update the gender of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(Boolean, String, JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldUpdateOnlyProfileGenderStoreChanges(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(storedProfile -> {

      final var newProfile = new WeNetUserProfile();
      newProfile.gender = WeNetUserProfile.OTHER;
      final var checkpoint = testContext.checkpoint(3);
      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
          .with(queryParam("storeProfileChangesInHistory", "true")).expect(updated1 -> {

            WeNetProfileManager.createProxy(vertx).retrieveProfile(storedProfile.id)
                .onSuccess(updatedProfile -> testContext.verify(() -> {

                  storedProfile._lastUpdateTs = updatedProfile._lastUpdateTs;
                  storedProfile.gender = WeNetUserProfile.OTHER;
                  assertThat(updatedProfile).isEqualTo(storedProfile);

                  testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                      .with(queryParam("storeProfileChangesInHistory", "true")).expect(updated2 -> {

                        WeNetProfileManager.createProxy(vertx).retrieveProfile(storedProfile.id)
                            .onSuccess(updatedProfile2 -> testContext.verify(() -> {

                              storedProfile._lastUpdateTs = updatedProfile2._lastUpdateTs;
                              storedProfile.gender = null;
                              assertThat(updatedProfile2).isEqualTo(storedProfile);
                              testRequest(client, HttpMethod.GET,
                                  Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

                                    assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                    final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);

                                    assertThat(page.profiles).hasSize(2);
                                    final var historic0 = page.profiles.get(0);
                                    assertThat(historic0.from).isEqualTo(storedProfile._creationTs);
                                    assertThat(historic0.to).isEqualTo(updatedProfile._lastUpdateTs);
                                    assertThat(historic0.profile.gender).isNull();

                                    final var historic1 = page.profiles.get(1);
                                    assertThat(historic1.from).isEqualTo(updatedProfile._lastUpdateTs);
                                    assertThat(historic1.to).isEqualTo(updatedProfile2._lastUpdateTs);
                                    assertThat(historic1.profile.gender).isEqualTo(WeNetUserProfile.OTHER);

                                  }).send(testContext, checkpoint);

                            }));

                      }).sendJson(new WeNetUserProfile().toJsonObject(), testContext, checkpoint);

                }));

          }).sendJson(newProfile.toJsonObject(), testContext, checkpoint);

    });

  }

  /**
   * Verify that can update the norms of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#updateProfile(Boolean, String, JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldUpdateProfileNorm(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(new WeNetUserProfileTest().createModelExample(23, vertx, testContext))
        .onSuccess(created -> {

          assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

            final var repository = ProfilesRepository.createProxy(vertx);

            testContext.assertComplete(repository.storeProfile(created)).onSuccess(storedProfile -> {

              final var newProfile = new WeNetUserProfile();
              newProfile.norms = new ArrayList<>();
              newProfile.norms.add(new ProtocolNorm());
              newProfile.norms.add(new ProtocolNorm());
              final var checkpoint = testContext.checkpoint(4);
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                  .expect(res -> testContext.verify(() -> {

                    assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                    final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                    assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                    final var expected = new HistoricWeNetUserProfilesPage();
                    expected.profiles = new ArrayList<>();
                    expected.profiles.add(new HistoricWeNetUserProfile());
                    expected.profiles.get(0).from = storedProfile._creationTs;
                    expected.profiles.get(0).to = updated._lastUpdateTs;
                    expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                        WeNetUserProfile.class);
                    expected.total++;

                    storedProfile._lastUpdateTs = updated._lastUpdateTs;
                    storedProfile.norms.add(0, new ProtocolNorm());
                    assertThat(updated).isEqualTo(storedProfile);
                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
                          assertThat(page).isEqualTo(expected);
                          newProfile.norms = new ArrayList<>();
                          newProfile.norms.add(new ProtocolNorm());
                          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                              .expect(res2 -> testContext.verify(() -> {

                                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                final var updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                                expected.profiles.add(new HistoricWeNetUserProfile());
                                expected.profiles.get(1).from = updated._lastUpdateTs;
                                expected.profiles.get(1).to = updated2._lastUpdateTs;
                                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                                    WeNetUserProfile.class);
                                expected.total++;

                                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                                storedProfile.norms = new ArrayList<>();
                                storedProfile.norms.remove(1);
                                assertThat(updated2).isEqualTo(storedProfile);
                                testRequest(client, HttpMethod.GET,
                                    Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                                        .expect(resPage2 -> {

                                          assertThat(resPage2.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                          final var page2 = assertThatBodyIs(HistoricWeNetUserProfilesPage.class,
                                              resPage2);
                                          assertThat(page2).isEqualTo(expected);

                                        }).send(testContext, checkpoint);

                              })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);

                        }).send(testContext, checkpoint);

                  })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
            });
          });
        });

  }

  /**
   * Verify that can update the planned activities of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfilePlannedActivity(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new WeNetUserProfileTest().createModelExample(23, vertx, testContext))
        .onSuccess(created -> {

          assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

            final var repository = ProfilesRepository.createProxy(vertx);

            testContext.assertComplete(repository.storeProfile(created)).onSuccess(storedProfile -> {

              final var newProfile = new WeNetUserProfile();
              newProfile.plannedActivities = new ArrayList<>();
              newProfile.plannedActivities.add(new PlannedActivity());
              newProfile.plannedActivities.add(new PlannedActivity());
              newProfile.plannedActivities.get(1).id = storedProfile.plannedActivities.get(0).id;
              newProfile.plannedActivities.get(1).description = "Description";
              final var checkpoint = testContext.checkpoint(4);
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                  .expect(res -> testContext.verify(() -> {

                    assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                    final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                    assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                    final var expected = new HistoricWeNetUserProfilesPage();
                    expected.profiles = new ArrayList<>();
                    expected.profiles.add(new HistoricWeNetUserProfile());
                    expected.profiles.get(0).from = storedProfile._creationTs;
                    expected.profiles.get(0).to = updated._lastUpdateTs;
                    expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                        WeNetUserProfile.class);
                    expected.total++;

                    storedProfile._lastUpdateTs = updated._lastUpdateTs;
                    storedProfile.plannedActivities.remove(1);
                    storedProfile.plannedActivities.add(0, new PlannedActivity());
                    storedProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
                    storedProfile.plannedActivities.get(1).description = "Description";
                    assertThat(updated).isEqualTo(storedProfile);
                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
                          assertThat(page).isEqualTo(expected);
                          newProfile.plannedActivities = new ArrayList<>();
                          newProfile.plannedActivities.add(new PlannedActivity());
                          newProfile.plannedActivities.get(0).id = updated.plannedActivities.get(0).id;
                          newProfile.plannedActivities.get(0).description = "Description2";
                          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                              .expect(res2 -> testContext.verify(() -> {

                                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                final var updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                                expected.profiles.add(new HistoricWeNetUserProfile());
                                expected.profiles.get(1).from = updated._lastUpdateTs;
                                expected.profiles.get(1).to = updated2._lastUpdateTs;
                                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                                    WeNetUserProfile.class);
                                expected.total++;

                                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                                storedProfile.plannedActivities = new ArrayList<>();
                                storedProfile.plannedActivities.remove(1);
                                storedProfile.plannedActivities.get(0).description = "Description2";
                                assertThat(updated2).isEqualTo(storedProfile);
                                testRequest(client, HttpMethod.GET,
                                    Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                                        .expect(resPage2 -> {

                                          assertThat(resPage2.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                          final var page2 = assertThatBodyIs(HistoricWeNetUserProfilesPage.class,
                                              resPage2);
                                          assertThat(page2).isEqualTo(expected);

                                        }).send(testContext, checkpoint);

                              })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);

                        }).send(testContext, checkpoint);

                  })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
            });
          });
        });

  }

  /**
   * Verify that can update the relevant locations of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfileRelevantLocation(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new WeNetUserProfileTest().createModelExample(23, vertx, testContext))
        .onSuccess(created -> {

          assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

            final var repository = ProfilesRepository.createProxy(vertx);

            testContext.assertComplete(repository.storeProfile(created)).onSuccess(storedProfile -> {

              final var newProfile = new WeNetUserProfile();
              newProfile.relevantLocations = new ArrayList<>();
              newProfile.relevantLocations.add(new RelevantLocation());
              newProfile.relevantLocations.add(new RelevantLocation());
              newProfile.relevantLocations.get(1).id = storedProfile.relevantLocations.get(0).id;
              newProfile.relevantLocations.get(1).label = "Label";
              newProfile.relevantLocations.get(1).latitude = -24d;
              newProfile.relevantLocations.get(1).longitude = 24d;
              final var checkpoint = testContext.checkpoint(4);
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                  .expect(res -> testContext.verify(() -> {

                    assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                    final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                    assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                    final var expected = new HistoricWeNetUserProfilesPage();
                    expected.profiles = new ArrayList<>();
                    expected.profiles.add(new HistoricWeNetUserProfile());
                    expected.profiles.get(0).from = storedProfile._creationTs;
                    expected.profiles.get(0).to = updated._lastUpdateTs;
                    expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                        WeNetUserProfile.class);
                    expected.total++;

                    storedProfile._lastUpdateTs = updated._lastUpdateTs;
                    storedProfile.relevantLocations.add(0, new RelevantLocation());
                    storedProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
                    storedProfile.relevantLocations.get(1).label = "Label";
                    assertThat(updated).isEqualTo(storedProfile);
                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);

                          assertThat(page).isEqualTo(expected);
                          newProfile.relevantLocations = new ArrayList<>();
                          newProfile.relevantLocations.add(new RelevantLocation());
                          newProfile.relevantLocations.get(0).id = updated.relevantLocations.get(0).id;
                          newProfile.relevantLocations.get(0).label = "Label2";
                          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                              .expect(res2 -> testContext.verify(() -> {

                                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                final var updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
                                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                                expected.profiles.add(new HistoricWeNetUserProfile());
                                expected.profiles.get(1).from = updated._lastUpdateTs;
                                expected.profiles.get(1).to = updated2._lastUpdateTs;
                                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                                    WeNetUserProfile.class);
                                expected.total++;

                                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
                                storedProfile.relevantLocations = new ArrayList<>();
                                storedProfile.relevantLocations.remove(1);
                                storedProfile.relevantLocations.get(0).label = "Label2";
                                assertThat(updated2).isEqualTo(storedProfile);
                                testRequest(client, HttpMethod.GET,
                                    Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                                        .expect(resPage2 -> {

                                          assertThat(resPage2.statusCode()).isEqualTo(Status.OK.getStatusCode());
                                          final var page2 = assertThatBodyIs(HistoricWeNetUserProfilesPage.class,
                                              resPage2);
                                          assertThat(page2).isEqualTo(expected);

                                        }).send(testContext, checkpoint);

                              })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);

                        }).send(testContext, checkpoint);

                  })).sendJson(newProfile.toJsonObject(), testContext);
            });
          });
        });

  }

  /**
   * Verify that can update the personal behaviors of an user.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfile(String,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldUpdateProfilePersonalBehavior(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(new WeNetUserProfileTest().createModelExample(23, vertx, testContext))
        .onSuccess(created -> {

          assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

            final var repository = ProfilesRepository.createProxy(vertx);

            testContext.assertComplete(repository.storeProfile(created)).onSuccess(storedProfile -> {

              final var newProfile = new WeNetUserProfile();
              newProfile.personalBehaviors = new ArrayList<>();
              newProfile.personalBehaviors.add(new RoutineTest().createModelExample(1));
              newProfile.personalBehaviors.add(new RoutineTest().createModelExample(2));
              newProfile.personalBehaviors.get(0).user_id = storedProfile.id;
              newProfile.personalBehaviors.get(1).user_id = storedProfile.id;
              final var checkpoint = testContext.checkpoint(2);
              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id)
                  .expect(res -> testContext.verify(() -> {

                    assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
                    final var updated = assertThatBodyIs(WeNetUserProfile.class, res);
                    assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);

                    final var expected = new HistoricWeNetUserProfilesPage();
                    expected.profiles = new ArrayList<>();
                    expected.profiles.add(new HistoricWeNetUserProfile());
                    expected.profiles.get(0).from = storedProfile._creationTs;
                    expected.profiles.get(0).to = updated._lastUpdateTs;
                    expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(),
                        WeNetUserProfile.class);
                    expected.total++;

                    storedProfile._lastUpdateTs = updated._lastUpdateTs;
                    storedProfile.personalBehaviors = new ArrayList<>();
                    storedProfile.personalBehaviors.add(new RoutineTest().createModelExample(1));
                    storedProfile.personalBehaviors.add(new RoutineTest().createModelExample(2));
                    storedProfile.personalBehaviors.get(0).user_id = storedProfile.id;
                    storedProfile.personalBehaviors.get(1).user_id = storedProfile.id;
                    assertThat(updated).isEqualTo(storedProfile);
                    testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH)
                        .expect(resPage -> {

                          assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                          final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
                          assertThat(page).isEqualTo(expected);

                        }).send(testContext, checkpoint);

                  })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
            });
          });
        });

  }

  /**
   * Verify that can store a profile with only an identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#createProfile(io.vertx.core.json.JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, io.vertx.core.Handler)
   */
  @Test
  public void shouldStoreProfileWithOnlyAnId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var now = TimeManager.now();
    final var id = UUID.randomUUID().toString();
    testRequest(client, HttpMethod.POST, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var stored = assertThatBodyIs(WeNetUserProfile.class, res);
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
      assertThat(stored.occupation).isNull();
      assertThat(stored.norms).isNull();
      assertThat(stored.plannedActivities).isNull();
      assertThat(stored.relevantLocations).isNull();
      assertThat(stored.personalBehaviors).isNull();
      assertThat(stored.materials).isNull();
      assertThat(stored.competences).isNull();
      assertThat(stored.meanings).isNull();
      testContext.assertComplete(ProfilesRepository.createProxy(vertx).searchProfile(stored.id))
          .onSuccess(foundProfile -> testContext.verify(() -> {

            assertThat(foundProfile).isEqualTo(stored);
            testContext.completeNow();

          }));

    }).sendJson(new JsonObject().put("id", id), testContext, testContext.checkpoint(2));

  }

  /**
   * Verify that can retrieve some profiles.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#retrieveProfilesPage(int, int,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldRetrieveProfilesPage(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var checkpoint = testContext.checkpoint(2);
    testRequest(client, HttpMethod.GET, Profiles.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var page = assertThatBodyIs(WeNetUserProfilesPage.class, res);
      assertThat(page).isNotNull();
      assertThat(page.profiles).isNotNull().hasSize((int) page.total);
      testRequest(client, HttpMethod.GET, Profiles.PATH).with(queryParam("offset", "5"), queryParam("limit", "3"))
          .expect(res2 -> {

            assertThat(res2.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var page2 = assertThatBodyIs(WeNetUserProfilesPage.class, res2);
            assertThat(page2).isNotNull();
            assertThat(page2.offset).isEqualTo(5);
            assertThat(page2.total).isEqualTo(page.total);
            assertThat(page2.profiles).isNotNull().hasSize(3).isEqualTo(page.profiles.subList(5, 8));
            testContext.completeNow();

          }).send(testContext, checkpoint);

    }).send(testContext, checkpoint);

  }

  /**
   * Should allow to update the profile with {@code null} values.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldAllowToUpdateProfileWithNulls(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext).onSuccess(storedProfile -> {

      final var newProfile = new WeNetUserProfile();
      final var value = newProfile.toJsonObjectWithEmptyValues();
      client.put("/profiles/" + storedProfile.id).sendJson(value).onComplete(updated -> testContext.verify(() -> {

        assertThat(updated.failed()).isFalse();
        final var res = updated.result();
        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var updatedProfile = assertThatBodyIs(WeNetUserProfile.class, res);
        newProfile.id = storedProfile.id;
        newProfile._creationTs = storedProfile._creationTs;
        newProfile._lastUpdateTs = updatedProfile._lastUpdateTs;
        assertThat(updatedProfile).isEqualTo(newProfile);
        testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveProfile(storedProfile.id))
            .onSuccess(retrivedProfile -> testContext.verify(() -> {

              assertThat(retrivedProfile).isEqualTo(newProfile);
              testContext.completeNow();

            }));

      }));

    });

  }

  /**
   * Should not delete undefined profile.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteUndefinedProfile(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + UUID.randomUUID().toString()).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      testContext.completeNow();

    }).send(testContext);

  }

  /**
   * Should delete profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   */
  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.MINUTES)
  public void shouldDeleteProfile(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(CompositeFuture.all(StoreServices.storeProfileExample(1, vertx, testContext),
        StoreServices.storeCommunityExample(345, vertx, testContext),
        StoreServices.storeSocialNetworkRelationshipExample(33, vertx, testContext),
        new UserPerformanceRatingEventTest().createModelExample(3, vertx, testContext))).onSuccess(added -> {

          final var profile = (WeNetUserProfile) added.resultAt(0);
          final var userId = profile.id;
          final var community = (CommunityProfile) added.resultAt(1);
          final var relationship = (SocialNetworkRelationship) added.resultAt(2);
          final var event = (UserPerformanceRatingEvent) added.resultAt(3);

          final var newProfile = new WeNetUserProfileTest().createBasicExample(253);
          event.relationship = null;
          final var event1 = Model.fromJsonObject(event.toJsonObject(), UserPerformanceRatingEvent.class);
          event1.sourceId = userId;
          final var event2 = Model.fromJsonObject(event.toJsonObject(), UserPerformanceRatingEvent.class);
          event2.targetId = userId;
          final var member = new CommunityMemberTest().createModelExample(256);
          member.userId = userId;
          community.members.add(member);
          final var relationship1 = Model.fromJsonObject(relationship.toJsonObject(), SocialNetworkRelationship.class);
          relationship1.sourceId = userId;
          final var relationship2 = Model.fromJsonObject(relationship.toJsonObject(), SocialNetworkRelationship.class);
          relationship2.targetId = userId;

          testContext.assertComplete(WeNetProfileManager.createProxy(vertx).updateProfile(userId, newProfile, true))
              .onSuccess(ignored1 -> {
                testContext.assertComplete(WeNetProfileManager.createProxy(vertx).addTrustEvent(event1))
                    .onSuccess(ignored2 -> {
                      testContext.assertComplete(WeNetProfileManager.createProxy(vertx).addTrustEvent(event2))
                          .onSuccess(ignored3 -> {
                            testContext
                                .assertComplete(WeNetProfileManager.createProxy(vertx).updateCommunity(community))
                                .onSuccess(ignored4 -> {
                                  testContext.assertComplete(
                                      StoreServices.storeSocialNetworkRelationship(relationship1, vertx, testContext))
                                      .onSuccess(ignored5 -> {
                                        testContext.assertComplete(StoreServices
                                            .storeSocialNetworkRelationship(relationship2, vertx, testContext))
                                            .onSuccess(ignored6 -> {
                                              testContext
                                                  .assertComplete(
                                                      WeNetProfileManager.createProxy(vertx).deleteProfile(userId))
                                                  .onSuccess(any -> {

                                                    vertx.setTimer(15000, anyTimer -> this.checkDeletedProfile(vertx,
                                                        testContext, userId, event1, event2, community.id));

                                                  });
                                            });
                                      });
                                });
                          });
                    });
              });
        });
  }

  /**
   * Check that the profile is deleted.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   * @param userId      identifier of the removed user.
   * @param event1      an added event that has been removed.
   * @param event2      an added event that has been removed.
   * @param communityId the identifier of the community to removed the userId.
   */
  private void checkDeletedProfile(final Vertx vertx, final VertxTestContext testContext, final String userId,
      final UserPerformanceRatingEvent event1, final UserPerformanceRatingEvent event2, final String communityId) {

    testContext.assertFailure(WeNetProfileManager.createProxy(vertx).retrieveProfile(userId)).onFailure(failed1 -> {

      testContext
          .assertFailure(
              WeNetProfileManager.createProxy(vertx).getProfileHistoricPage(userId, null, null, null, 0, 100))
          .onFailure(failed2 -> {

            testContext.assertFailure(WeNetProfileManager.createProxy(vertx).calculateTrust(event1.sourceId,
                event1.targetId, null, null, null, null, null, null, null, TrustAggregator.MAXIMUM))
                .onFailure(failed3 -> {

                  testContext
                      .assertFailure(WeNetProfileManager.createProxy(vertx).calculateTrust(event2.sourceId,
                          event2.targetId, null, null, null, null, null, null, null, TrustAggregator.MAXIMUM))
                      .onFailure(failed4 -> {

                        testContext
                            .assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunity(communityId))
                            .onSuccess(updatedCommunity -> testContext.verify(() -> {

                              if (updatedCommunity.members != null) {

                                for (final var member : updatedCommunity.members) {

                                  assertThat(member.userId).isNotEqualTo(userId);
                                }

                              }

                              testContext
                                  .assertComplete(
                                      WeNetProfileManager.createProxy(vertx).retrieveSocialNetworkRelationshipsPage(
                                          null, userId, null, null, null, null, null, 0, 1000))
                                  .onSuccess(updatedRelationships -> testContext.verify(() -> {

                                    assertThat(updatedRelationships.total).isEqualTo(0l);

                                    testContext
                                        .assertComplete(WeNetProfileManager.createProxy(vertx)
                                            .retrieveSocialNetworkRelationshipsPage(null, null, userId, null, null,
                                                null, null, 0, 1000))
                                        .onSuccess(updatedRelationships2 -> testContext.verify(() -> {

                                          assertThat(updatedRelationships2.total).isEqualTo(0l);
                                          testContext.completeNow();
                                        }));

                                  }));

                            }));
                      });
                });
          });
    });
  }

}
