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

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.vertx.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic integration test to test the manipulation of a filed in a profile.
 *
 * @param <T> type of model of the field to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public abstract class AbstractProfileFieldManipulationIT<T extends Model & Validable> {

  /**
   * Should not add a bad coded json model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldFailAddBadCodedJson(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.POST, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject().put("undefinedKey", "undefinedValue"), testContext);

    }));

  }

  /**
   * Return the path to the field.
   *
   * @return the path to the field.
   */
  protected abstract String fieldPath();

  /**
   * Should not add an invalid model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldFailAddInvalidModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createInvalidModel(vertx, testContext).onComplete(testContext.succeeding(model -> {

      StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

        testRequest(client, HttpMethod.POST, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(model.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Create an invalid model.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future that will return the invalid model.
   */
  protected abstract Future<T> createInvalidModel(Vertx vertx, VertxTestContext testContext);

  /**
   * Create a valid model.
   *
   * @param index       for the example to create.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future that will return the invalid model.
   */
  protected abstract Future<T> createValidModel(int index, Vertx vertx, VertxTestContext testContext);

  /**
   * Should not add a model if the profile is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailAddModelToUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModel(1, vertx, testContext).onComplete(testContext.succeeding(model -> {

      testRequest(client, HttpMethod.POST, Profiles.PATH + "/undefinedProfileIdentifier" + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(model.toJsonObject(), testContext);

    }));

  }

  /**
   * Should add some models.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldAddModels(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(profile -> {

      this.createValidModel(1, vertx, testContext).onComplete(testContext.succeeding(model1 -> {

        this.createValidModel(2, vertx, testContext).onComplete(testContext.succeeding(model2 -> {

          final Checkpoint checkpoint = testContext.checkpoint(3);
          testRequest(client, HttpMethod.POST, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final T addedModel1 = assertThatBodyIs(this.modelClass(), res);
            this.assertEqualsAdded(addedModel1, model1);
            testRequest(client, HttpMethod.POST, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res2 -> {

              assertThat(res2.statusCode()).isEqualTo(Status.OK.getStatusCode());
              final T addedModel2 = assertThatBodyIs(this.modelClass(), res2);
              this.assertEqualsAdded(addedModel2, model2);
              ProfilesRepository.createProxy(vertx).searchProfile(profile.id, testContext.succeeding(newProfile -> {

                final List<T> models = this.modelsIn(newProfile);
                assertThat(models).hasSize(2).containsExactly(addedModel1, addedModel2);
                testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

                  assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
                  final HistoricWeNetUserProfilesPage page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
                  assertThat(page).isNotNull();
                  assertThat(page.total).isEqualTo(2);
                  profile._lastUpdateTs = page.profiles.get(0).profile._lastUpdateTs;
                  assertThat(page.profiles.get(0).profile).isEqualTo(profile);
                  final List<T> profileModels = this.initiModelsIn(profile);
                  profileModels.add(addedModel1);
                  profile._lastUpdateTs = page.profiles.get(1).profile._lastUpdateTs;
                  assertThat(page.profiles.get(1).profile).isEqualTo(profile);

                }).send(testContext, checkpoint);

              }));

            }).sendJson(model2.toJsonObject(), testContext, checkpoint);

          }).sendJson(model1.toJsonObject(), testContext, checkpoint);

        }));

      }));

    }));

  }

  /**
   * Check if the added model is equals to the original.
   *
   * @param addedModel the model that has added.
   * @param model      the model that is try to add.
   */
  protected abstract void assertEqualsAdded(T addedModel, T model);

  /**
   * Initialize the models on a profile.
   *
   * @param profile to initialize the models.
   *
   * @return the list of the initialized models.
   */
  protected abstract List<T> initiModelsIn(WeNetUserProfile profile);

  /**
   * Return the models defined on a profile.
   *
   * @param profile to get the models for the
   *
   * @return the models associated to the profile.
   */
  protected abstract List<T> modelsIn(WeNetUserProfile profile);

  /**
   * REturn the class of the model that is testing.
   *
   * @return the class of the model that is testing.
   */
  protected abstract Class<T> modelClass();

  /**
   * Should not retrieve models from undefined profile.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailRetrieveModelsForUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefinedProfileIdentifier" + this.fieldPath()).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final ErrorMessage error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Should retrieve empty models if they are not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldRetrieveEmptyModels(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final JsonArray array = res.bodyAsJsonArray();
        assertThat(array).isEqualTo(new JsonArray());

      }).send(testContext);

    }));

  }

  /**
   * Should retrieve models.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldRetrieveModels(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final JsonArray array = res.bodyAsJsonArray();
        final List<T> models = Model.fromJsonArray(array, this.modelClass());
        assertThat(models).isEqualTo(this.modelsIn(profile));

      }).send(testContext);

    }));

  }

}
