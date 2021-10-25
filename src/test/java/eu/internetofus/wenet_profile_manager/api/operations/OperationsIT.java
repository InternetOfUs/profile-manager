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

package eu.internetofus.wenet_profile_manager.api.operations;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.HashSet;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The integration test over the {@link Operations}.
 *
 * @see Operations
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class OperationsIT {

  /**
   * Verify that fail calculate diversity with bad data.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithBadData(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new JsonObject().put("undefined", true), testContext);

  }

  /**
   * Verify that fail calculate similarity with bad data.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailSimilarityWithBadData(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(new JsonObject().put("undefined", true), testContext);

  }

  /**
   * Verify that fail calculate diversity without attributes.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithoutAttributes(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.attributes = null;
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate diversity with empty attributes.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithEmptyAttributes(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.attributes.clear();
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate diversity without user identifiers.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithoutUserIds(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.userIds = null;
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate diversity with empty user Identifiers.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithEmptyUserIds(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.userIds.clear();
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate diversity with only one user Identifiers.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithOnlyOneUserId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      final var profileId = data.userIds.iterator().next();
      data.userIds.clear();
      data.userIds.add(profileId);
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate diversity with bad user identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithBadUserId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.userIds.add(UUID.randomUUID().toString());
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate diversity with bad attribute name.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailDiversityWithBadAttributeName(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.attributes.add(UUID.randomUUID().toString());
      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that calculate diversity.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldCalculateDiversity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new DiversityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var diversityValue = assertThatBodyIs(DiversityValue.class, res);
        assertThat(diversityValue.diversity).isGreaterThan(0d).isLessThan(1d);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that calculate diversity over the same user is {@code 0} diversity.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#diversity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldCalculateDiversityOverSameUser(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var data = new DiversityData();
    data.attributes = new HashSet<>();
    data.attributes.add("gender");
    data.attributes.add("locale");
    data.attributes.add("nationality");
    data.attributes.add("occupation");
    data.userIds = new HashSet<>();
    StoreServices.storeProfileExample(0, vertx, testContext).onSuccess(profile1 -> {

      data.userIds.add(profile1.id);
      profile1.id = null;
      StoreServices.storeProfile(profile1, vertx, testContext).onSuccess(profile2 -> {

        data.userIds.add(profile2.id);
        testRequest(client, HttpMethod.POST, Operations.PATH + "/diversity").expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          final var diversityValue = assertThatBodyIs(DiversityValue.class, res);
          assertThat(diversityValue.diversity).isEqualTo(0d);

        }).sendJson(data.toJsonObject(), testContext);

      });

    });

  }

  /**
   * Verify that fail calculate similarity without source.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailSimilarityWithoutSource(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new SimilarityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.source = null;
      testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate similarity with empty source.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailSimilarityWithEmptySource(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    new SimilarityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      data.source = "    ";
      testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(data.toJsonObject(), testContext);

    });

  }

  /**
   * Verify that fail calculate similarity without userId.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailSimilarityWithoutUserId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var data = new SimilarityData();
    data.source = "Who is the best bond?";
    data.userId = null;
    testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(data.toJsonObject(), testContext);

  }

  /**
   * Verify that fail calculate similarity with empty userId.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailSimilarityWithEmptyUserId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var data = new SimilarityData();
    data.source = "Who is the best bond?";
    data.userId = "";
    testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(data.toJsonObject(), testContext);

  }

  /**
   * Verify that fail calculate similarity with undefined userId.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldFailSimilarityWithUndefinedUserId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var data = new SimilarityData();
    data.source = "Who is the best bond?";
    data.userId = UUID.randomUUID().toString();
    testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(data.toJsonObject(), testContext);

  }

  /**
   * Verify that calculate similarity.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   *
   * @see Operations#similarity(JsonObject,
   *      io.vertx.ext.web.api.service.ServiceRequest, Handler)
   */
  @Test
  public void shouldCalculateSimilarity(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    new SimilarityDataTest().createModelExample(0, vertx, testContext).onSuccess(data -> {

      testRequest(client, HttpMethod.POST, Operations.PATH + "/similarity").expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var result = assertThatBodyIs(SimilarityResult.class, res);
        assertThat(result.attributes).isNotNull().isNotEmpty();

      }).sendJson(data.toJsonObject(), testContext);

    });

  }
}
