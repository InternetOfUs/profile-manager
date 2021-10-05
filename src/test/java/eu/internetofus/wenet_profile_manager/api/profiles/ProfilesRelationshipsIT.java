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

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipTest;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

/**
 * Check the manipulation of the personal behaviors
 * ({@link SocialNetworkRelationship}) in a {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesRelationshipsIT extends AbstractProfileFieldResourcesIT<SocialNetworkRelationship, Integer> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Profiles.RELATIONSHIPS_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<SocialNetworkRelationship> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new SocialNetworkRelationshipTest().createModelExample(index, vertx, testContext));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SocialNetworkRelationship createInvalidModelFieldElement() {

    final var element = new SocialNetworkRelationshipTest().createModelExample(0);
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<SocialNetworkRelationship> fieldOf(final WeNetUserProfile model) {

    return model.relationships;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<WeNetUserProfile> storeValidExampleModelWithNullField(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new WeNetUserProfileTest().createModelExample(index, vertx, testContext).compose(profile -> {
          profile.id = null;
          profile.relationships = null;
          return StoreServices.storeProfile(profile, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final SocialNetworkRelationship source, final SocialNetworkRelationship target) {

    assertThat(source).isEqualTo(target);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Integer idOfElementIn(final WeNetUserProfile model, final SocialNetworkRelationship element) {

    if (model.relationships == null) {

      return -1;

    } else {

      return model.relationships.indexOf(element);

    }

  }

  /**
   * Should not add or update an element if the profile is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotAddOrUpdateRelationshipBecauseProfileIsUndefined(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelFieldElementExample(1, vertx, testContext)).onSuccess(source -> {

      final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath();
      testRequest(client, HttpMethod.PUT, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(source.toJsonObject(), testContext);

    });
  }

  /**
   * Should not add or update a bad JSON element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotAddOrUpdateWithBadJsonRelationship(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath();
      testRequest(client, HttpMethod.PUT, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    });

  }

  /**
   * Should add element when relationships are {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldAddOrUpdateElementOverNullField(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

        final var checkpoint = testContext.checkpoint(2);
        final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
        testRequest(client, HttpMethod.PUT, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
          final var created = assertThatBodyIs(element.getClass(), res);
          this.assertEqualsAdded(element, created);

          final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
          testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

            assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
            final var updatedModel = assertThatBodyIs(model.getClass(), resRetrieve);
            final var updatedField = this.fieldOf(updatedModel);
            assertThat(updatedField).isNotEmpty().contains(created);

          }).sendJson(element.toJsonObject(), testContext, checkpoint);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      });

    });

  }

  /**
   * Should update an existing relation.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldAddOrUpdateRelationWhenRelationExist(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var elementId = this.idOfElementIn(model, field.get(field.size() - 1));
      final var element = Model.fromJsonObject(field.get(elementId).toJsonObject(), SocialNetworkRelationship.class);
      element.weight += 0.02;
      final var checkpoint = testContext.checkpoint(2);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath();
      testRequest(client, HttpMethod.PUT, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var merged = assertThatBodyIs(element.getClass(), res);
        this.assertEqualsAdded(element, merged);
        final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
        testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

          assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final var mergedModel = assertThatBodyIs(model.getClass(), resRetrieve);
          final var mergedField = this.fieldOf(mergedModel);
          assertThat(mergedField).isNotEmpty().contains(merged);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }).sendJson(element.toJsonObject(), testContext, checkpoint);

    });
  }

  /**
   * Should add an existing relation.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldAddOrUpdateRelationWhenRelationNotExist(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(2, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(200, vertx, testContext))
          .onSuccess(element -> {

            final var checkpoint = testContext.checkpoint(2);
            final var postPath = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
            testRequest(client, HttpMethod.PUT, postPath).expect(res -> {

              assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
              final var created = assertThatBodyIs(element.getClass(), res);
              this.assertEqualsAdded(element, created);

              final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
              testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

                assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
                final var updatedModel = assertThatBodyIs(model.getClass(), resRetrieve);
                final var updatedField = this.fieldOf(updatedModel);
                assertThat(updatedField).isNotEmpty().contains(created);

              }).sendJson(element.toJsonObject(), testContext, checkpoint);

            }).sendJson(element.toJsonObject(), testContext, checkpoint);

          });

    });
  }

}
