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

import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;

/**
 * The integration test over the {@link Communities}.
 *
 * @see Communities
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class CommunitiesIT {

  //  /**
  //   * Verify that can update the social practices of an user.
  //   *
  //   * @param vertx       event bus to use.
  //   * @param client      to connect to the server.
  //   * @param testContext context to test.
  //   *
  //   * @see Profiles#retrieveProfile(String, io.vertx.ext.web.api.OperationRequest, io.vertx.core.Handler)
  //   */
  //  @Test
  //  public void shouldUpdateProfileSocialPractice(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {
  //
  //    new WeNetUserProfileTest().createModelExample(23, vertx, testContext, testContext.succeeding(created -> {
  //
  //      assertIsValid(created, vertx, testContext, () -> {
  //
  //        final ProfilesRepository repository = ProfilesRepository.createProxy(vertx);
  //
  //        repository.storeProfile(created, testContext.succeeding(storedProfile -> {
  //
  //          final WeNetUserProfile newProfile = new WeNetUserProfile();
  //          newProfile.socialPractices = new ArrayList<>();
  //          newProfile.socialPractices.add(new SocialPractice());
  //          newProfile.socialPractices.add(new SocialPractice());
  //          newProfile.socialPractices.get(1).id = storedProfile.socialPractices.get(0).id;
  //          newProfile.socialPractices.get(1).label = "Label";
  //          final Checkpoint checkpoint = testContext.checkpoint(4);
  //          testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res -> testContext.verify(() -> {
  //
  //            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
  //            final WeNetUserProfile updated = assertThatBodyIs(WeNetUserProfile.class, res);
  //            assertThat(updated).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
  //
  //            final HistoricWeNetUserProfilesPage expected = new HistoricWeNetUserProfilesPage();
  //            expected.profiles = new ArrayList<>();
  //            expected.profiles.add(new HistoricWeNetUserProfile());
  //            expected.profiles.get(0).from = storedProfile._creationTs;
  //            expected.profiles.get(0).to = updated._lastUpdateTs;
  //            expected.profiles.get(0).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
  //            expected.total++;
  //
  //            storedProfile._lastUpdateTs = updated._lastUpdateTs;
  //            storedProfile.socialPractices.add(0, new SocialPractice());
  //            storedProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
  //            storedProfile.socialPractices.get(1).label = "Label";
  //            assertThat(updated).isEqualTo(storedProfile);
  //            testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH).expect(resPage -> {
  //
  //              assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
  //              final HistoricWeNetUserProfilesPage page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
  //              assertThat(page).isEqualTo(expected);
  //              newProfile.socialPractices = new ArrayList<>();
  //              newProfile.socialPractices.add(new SocialPractice());
  //              newProfile.socialPractices.get(0).id = updated.socialPractices.get(0).id;
  //              newProfile.socialPractices.get(0).label = "Label2";
  //              testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + storedProfile.id).expect(res2 -> testContext.verify(() -> {
  //
  //                assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
  //                final WeNetUserProfile updated2 = assertThatBodyIs(WeNetUserProfile.class, res2);
  //                assertThat(updated2).isNotEqualTo(storedProfile).isNotEqualTo(newProfile);
  //
  //                expected.profiles.add(new HistoricWeNetUserProfile());
  //                expected.profiles.get(1).from = updated._lastUpdateTs;
  //                expected.profiles.get(1).to = updated2._lastUpdateTs;
  //                expected.profiles.get(1).profile = Model.fromJsonObject(storedProfile.toJsonObject(), WeNetUserProfile.class);
  //                expected.total++;
  //
  //                storedProfile._lastUpdateTs = updated2._lastUpdateTs;
  //                storedProfile.socialPractices = new ArrayList<>();
  //                storedProfile.socialPractices.remove(1);
  //                storedProfile.socialPractices.get(0).label = "Label2";
  //                assertThat(updated2).isEqualTo(storedProfile);
  //                testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + storedProfile.id + Profiles.HISTORIC_PATH).expect(resPage2 -> {
  //
  //                  assertThat(resPage2.statusCode()).isEqualTo(Status.OK.getStatusCode());
  //                  final HistoricWeNetUserProfilesPage page2 = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage2);
  //                  assertThat(page2).isEqualTo(expected);
  //
  //                }).send(testContext, checkpoint);
  //
  //              })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
  //
  //            }).send(testContext, checkpoint);
  //
  //          })).sendJson(newProfile.toJsonObject(), testContext, checkpoint);
  //        }));
  //      });
  //    }));
  //
  //  }

}
