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

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic integration test to test the manipulation of a filed in a profile.
 *
 * @param <T> type of model of the field to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractProfileFieldManipulationByIdentifierIT<T extends Model & Validable> extends AbstractProfileFieldManipulationIT<T> {

  /**
   * Should not add a model if the model identifier is duplicated.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailAddModelWithDuplicatedIdentifier(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      this.createValidModel(1, vertx, testContext).onComplete(testContext.succeeding(newModel -> {

        final var models = this.modelsIn(profile);
        final var model = models.get(0);
        this.updateIdsTo(model, newModel);
        testRequest(client, HttpMethod.POST, Profiles.PATH + "/" + profile.id + this.fieldPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(newModel.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final T addedModel, final T model) {

    this.updateIdsTo(addedModel, model);
    assertThat(addedModel).isEqualTo(model);

  }

  /**
   * Update the identifiers of a model with the identifiers of another.
   *
   * @param source to get the identifiers.
   * @param target to set the identifiers.
   */
  protected abstract void updateIdsTo(T source, T target);

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
  public void shouldFailRetrieveModelForUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Profiles.PATH + "/undefinedProfileIdentifier" + this.fieldPath() + "/undefinedModelId").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Should not retrieve model from undefined model identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailRetrieveModelForUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/undefinedModelId").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

  }

  /**
   * Should not retrieve model from empty models.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailRetrieveModelForEmptyModels(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(profile -> {

      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/1").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

  }

  /**
   * Should retrieve model by identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldRetrieveModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      final var models = this.modelsIn(profile);
      final var model = models.get(0);
      testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + this.idOf(model)).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var retrievedmodel = assertThatBodyIs(this.modelClass(), res);
        assertThat(retrievedmodel).isEqualTo(model);

      }).send(testContext);

    }));

  }

  /**
   * Return the identifier associated to a model.
   *
   * @param model to get the identifier.
   *
   * @return the identifier of the model.
   */
  protected abstract String idOf(T model);

  /**
   * Should not update a bad coded json model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldFailUpdateBadCodedJson(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      final var models = this.modelsIn(profile);
      final var model = models.get(0);
      final var modelId = this.idOf(model);
      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject().put("undefinedKey", "undefinedValue"), testContext);

    }));

  }

  /**
   * Should not update an invalid model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldFailUpdateInvalidModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createInvalidModel(vertx, testContext).onComplete(testContext.succeeding(invalidModel -> {

      StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

        final var models = this.modelsIn(profile);
        final var model = models.get(0);
        final var modelId = this.idOf(model);
        testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(invalidModel.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not update a model if the profile is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailUpdateModelToUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModel(1, vertx, testContext).onComplete(testContext.succeeding(model -> {

      testRequest(client, HttpMethod.PUT, Profiles.PATH + "/undefinedProfileIdentifier" + this.fieldPath() + "/1").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(model.toJsonObject(), testContext);

    }));

  }

  /**
   * Should not update a model if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailUpdateModelToUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModel(2, vertx, testContext).onComplete(testContext.succeeding(model -> {

      StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {
        testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/undefinedModelId").expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(model.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should update a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldUpdateModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      this.createValidModel(2, vertx, testContext).onComplete(testContext.succeeding(target -> {

        final var models = this.modelsIn(profile);
        final var source = models.get(0);
        final var modelId = this.idOf(source);
        final var checkpoint = testContext.checkpoint(2);
        testRequest(client, HttpMethod.PUT, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final var updatedModel = assertThatBodyIs(this.modelClass(), res);
          this.updateIdsTo(updatedModel, target);
          assertThat(updatedModel).isEqualTo(target);
          ProfilesRepository.createProxy(vertx).searchProfile(profile.id, testContext.succeeding(updatedProfile -> testContext.verify(() -> {

            final var updatedModels = this.modelsIn(updatedProfile);
            assertThat(updatedModels).contains(updatedModel).doesNotContain(source);
            testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

              assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
              final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
              assertThat(page).isNotNull();
              final var historicProfile = page.profiles.get(page.profiles.size() - 1).profile;
              assertThat(historicProfile).isEqualTo(profile);

            }).send(testContext, checkpoint);

          })));

        }).sendJson(target.toJsonObject(), testContext, checkpoint);

      }));

    }));

  }

  /**
   * Should not merge a model if the profile is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailMergeModelToUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModel(1, vertx, testContext).onComplete(testContext.succeeding(model -> {

      testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/undefinedProfileIdentifier" + this.fieldPath() + "/1").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(model.toJsonObject(), testContext);

    }));

  }

  /**
   * Should not merge a model if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailMergeModelToUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModel(2, vertx, testContext).onComplete(testContext.succeeding(model -> {

      StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {
        testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/undefinedModelId").expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(model.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not merge a model if the models are empty.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailMergeModelToEmptyModels(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModel(2, vertx, testContext).onComplete(testContext.succeeding(model -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(profile -> {
        testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/1").expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(model.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not merge a bad coded json model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldFailMergeBadCodedJson(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      final var models = this.modelsIn(profile);
      final var model = models.get(0);
      final var modelId = this.idOf(model);
      testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(new JsonObject().put("undefinedKey", "undefinedValue"), testContext);

    }));

  }

  /**
   * Should not merge an invalid model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldFailMergeInvalidModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createInvalidModel(vertx, testContext).onComplete(testContext.succeeding(invalidModel -> {

      StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

        final var models = this.modelsIn(profile);
        final var model = models.get(0);
        final var modelId = this.idOf(model);
        testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(invalidModel.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should merge a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldMergeModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      this.createValidModel(2, vertx, testContext).onComplete(testContext.succeeding(target -> {

        final var models = this.modelsIn(profile);
        final var source = models.get(0);
        final var modelId = this.idOf(source);
        final var checkpoint = testContext.checkpoint(2);
        testRequest(client, HttpMethod.PATCH, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final var mergedModel = assertThatBodyIs(this.modelClass(), res);
          this.updateIdsTo(mergedModel, target);
          assertThat(mergedModel).isEqualTo(target);
          ProfilesRepository.createProxy(vertx).searchProfile(profile.id, testContext.succeeding(mergedProfile -> testContext.verify(() -> {

            final var mergedModels = this.modelsIn(mergedProfile);
            assertThat(mergedModels).contains(mergedModel).doesNotContain(source);
            testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

              assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
              final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
              assertThat(page).isNotNull();
              final var historicProfile = page.profiles.get(page.profiles.size() - 1).profile;
              assertThat(historicProfile).isEqualTo(profile);

            }).send(testContext, checkpoint);

          })));

        }).sendJson(target.toJsonObject(), testContext, checkpoint);

      }));

    }));

  }

  /**
   * Should not delete a model if the profile is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailDeleteModelToUndefinedProfile(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/undefinedProfileIdentifier" + this.fieldPath() + "/1").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Should not delete a model if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailDeleteModelToUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {
      testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/undefinedModelId").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

  }

  /**
   * Should not delete a model if the models are empty.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldFailDeleteModelToEmptyModels(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(profile -> {
      testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/1").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

  }

  /**
   * Should delete a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldDeleteModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      final var models = this.modelsIn(profile);
      final var source = models.get(0);
      final var modelId = this.idOf(source);
      final var checkpoint = testContext.checkpoint(2);
      testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
        ProfilesRepository.createProxy(vertx).searchProfile(profile.id, testContext.succeeding(deletedProfile -> {

          final var deletedModels = this.modelsIn(deletedProfile);
          assertThat(deletedModels).doesNotContain(source);
          testRequest(client, HttpMethod.GET, Profiles.PATH + "/" + profile.id + Profiles.HISTORIC_PATH).expect(resPage -> {

            assertThat(resPage.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var page = assertThatBodyIs(HistoricWeNetUserProfilesPage.class, resPage);
            assertThat(page).isNotNull();
            final var historicProfile = page.profiles.get(page.profiles.size() - 1).profile;
            assertThat(historicProfile).isEqualTo(profile);

          }).send(testContext, checkpoint);

        }));

      }).send(testContext, checkpoint);

    }));

  }

  /**
   * Should delete a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Profiles#addRelevantLocation(String, JsonObject, io.vertx.ext.web.api.OperationRequest, Handler)
   */
  @Test
  public void shouldNotDeleteModelWithSameIdTwice(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext, testContext.succeeding(profile -> {

      final var models = this.modelsIn(profile);
      final var source = models.get(0);
      final var modelId = this.idOf(source);
      final var checkpoint = testContext.checkpoint(2);
      testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
        testRequest(client, HttpMethod.DELETE, Profiles.PATH + "/" + profile.id + this.fieldPath() + "/" + modelId).expect(res2 -> {

          assertThat(res2.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res2);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext, checkpoint);

      }).send(testContext, checkpoint);

    }));

  }

}
