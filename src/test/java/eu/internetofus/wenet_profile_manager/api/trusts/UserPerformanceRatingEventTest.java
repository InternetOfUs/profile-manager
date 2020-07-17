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

package eu.internetofus.wenet_profile_manager.api.trusts;

import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationship;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipType;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.components.task_manager.WeNetTaskManagerMocker;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link UserPerformanceRatingEvent}
 *
 * @see UserPerformanceRatingEvent
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class UserPerformanceRatingEventTest extends ModelTestCase<UserPerformanceRatingEvent> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The task manager mocked server.
   */
  protected static WeNetTaskManagerMocker taskManagerMocker;

  /**
   * The service mocked server.
   */
  protected static WeNetServiceMocker serviceMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMockers() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
    taskManagerMocker = WeNetTaskManagerMocker.start();
    serviceMocker = WeNetServiceMocker.start();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final WebClient client = WebClient.create(vertx);
    final JsonObject profileConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileConf);

    final JsonObject taskConf = taskManagerMocker.getComponentConfiguration();
    WeNetTaskManager.register(vertx, client, taskConf);

    final JsonObject conf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, conf);
    WeNetServiceSimulator.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPerformanceRatingEvent createModelExample(final int index) {

    final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
    model.sourceId = "SourceId_" + index;
    model.targetId = "TargetId_" + index;
    model.communityId = "CommunityId_" + index;
    model.taskTypeId = "TaskTypeId_" + index;
    model.taskId = "TaskId_" + index;
    model.reportTime = index;
    model.rating = 1.0 / Math.max(1, index + 2);
    return model;
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index         to use in the example.
   * @param vertx         event bus to use.
   * @param testContext   test context to use.
   * @param createHandler the component that will manage the created model.
   */
  public void createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<UserPerformanceRatingEvent>> createHandler) {

    StoreServices.storeTaskExample(index, vertx, testContext, testContext.succeeding(task -> {

      final WeNetUserProfile profile = new WeNetUserProfile();
      profile.relationships = new ArrayList<>();
      profile.relationships.add(new SocialNetworkRelationship());
      profile.relationships.get(0).userId = task.requesterId;
      final SocialNetworkRelationshipType relationship = SocialNetworkRelationshipType.values()[index % SocialNetworkRelationshipType.values().length];
      profile.relationships.get(0).type = relationship;
      StoreServices.storeProfile(profile, vertx, testContext, testContext.succeeding(stored -> {

        final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
        model.sourceId = stored.id;
        model.targetId = task.requesterId;
        model.relationship = relationship;
        model.appId = task.appId;
        model.communityId = "CommunityId" + index;
        model.taskTypeId = task.taskTypeId;
        model.taskId = task.id;
        model.rating = 1.0 / Math.max(1, index + 2);
        createHandler.handle(Future.succeededFuture(model));

      }));
    }));

  }

  /**
   * Check that an empty event is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyEventNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new UserPerformanceRatingEvent(), "rating", vertx, testContext);

  }

  /**
   * Check that a {@link #createModelExample(int)} is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldBasicExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final UserPerformanceRatingEvent event = this.createModelExample(1);
    assertIsNotValid(event, "sourceId", vertx, testContext);

  }

  /**
   * Check that an event with source, target and rating be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceTargetAndRatingBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = "   " + created.sourceId + "   ";
      model.targetId = "   " + created.targetId + "   ";
      model.rating = created.rating;
      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.sourceId).isEqualTo(created.sourceId);
        assertThat(model.targetId).isEqualTo(created.targetId);

      });

    }));

  }

  /**
   * Check that an event with source equals to target is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceEqualsToTargetNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.targetId = model.sourceId;
      assertIsNotValid(model, "targetId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with source, target, rating and application identifier be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndAppBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.appId = "   " + created.appId + "   ";
      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.appId).isEqualTo(created.appId);

      });

    }));

  }

  /**
   * Check that an event with source, target, rating and community identifier be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndCommunityBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.communityId = "   " + created.communityId + "   ";
      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.communityId).isEqualTo(created.communityId);

      });

    }));

  }

  /**
   * Check that an event with source, target, rating and task type identifier be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndTaskTypeBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.taskTypeId = "   " + created.taskTypeId + "   ";
      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.taskTypeId).isEqualTo(created.taskTypeId);

      });

    }));

  }

  /**
   * Check that an event with source, target, rating and task identifier be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndTaskBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.taskId = "   " + created.taskId + "   ";
      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.taskId).isEqualTo(created.taskId);

      });

    }));

  }

  /**
   * Check that an event with source, target, rating and relationship be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndRelationshipBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.relationship = created.relationship;
      assertIsValid(model, vertx, testContext);

    }));

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext, testContext.succeeding(model -> assertIsValid(model, vertx, testContext)));

  }

  /**
   * Check that a model with a bad rating is not valid.
   *
   * @param rating      that is wrong.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with a rating {0} has not to be valid")
  @ValueSource(doubles = { -0.0001, -0.1, 1.1, 1.000001 })
  public void shouldEventWithBadRatingNotBeValid(final double rating, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.rating = rating;
      assertIsNotValid(model, "rating", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a bad sourceId is not valid.
   *
   * @param sourceId    invalid source identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with the sourceId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000", ValidationsTest.STRING_256 })
  public void shouldEventWithBadSourceIdNotBeValid(final String sourceId, final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = sourceId;
      model.targetId = stored.id;
      model.rating = Math.random();
      assertIsNotValid(model, "sourceId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a bad targetId is not valid.
   *
   * @param targetId    invalid target identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with the targetId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000", ValidationsTest.STRING_256 })
  public void shouldEventWithBadTargetIdNotBeValid(final String targetId, final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

      final UserPerformanceRatingEvent model = new UserPerformanceRatingEvent();
      model.sourceId = stored.id;
      model.targetId = targetId;
      model.rating = Math.random();
      assertIsNotValid(model, "targetId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a bad appId is not valid.
   *
   * @param appId       invalid application identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with the appId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000", ValidationsTest.STRING_256 })
  public void shouldEventWithBadAppIdNotBeValid(final String appId, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.appId = appId;
      assertIsNotValid(model, "appId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a appId that is not equals to the appId of the task.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithAppIdDiferentTotehAppIdOfTheTaskNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeAppExample(2, vertx, testContext, testContext.succeeding(stored -> {
      this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

        model.appId = stored.appId;
        assertIsNotValid(model, "appId", vertx, testContext);

      }));
    }));

  }

  /**
   * Check that an event with a bad communityId is not valid.
   *
   * @param communityId invalid community identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with the communityId {0} has not to be valid")
  // @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000", ValidationsTest.STRING_256 })
  @ValueSource(strings = { ValidationsTest.STRING_256 })
  public void shouldEventWithBadCommunityIdNotBeValid(final String communityId, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.communityId = communityId;
      assertIsNotValid(model, "communityId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a bad taskTypeId is not valid.
   *
   * @param taskTypeId  invalid task type identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with the taskTypeId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000", ValidationsTest.STRING_256 })
  public void shouldEventWithBadTaskTypeIdNotBeValid(final String taskTypeId, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.taskTypeId = taskTypeId;
      assertIsNotValid(model, "taskTypeId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a taskTypeId that is not equals to the taskTypeId of the task.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithTaskTypeIdDiferentTotehTaskTypeIdOfTheTaskNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskTypeExample(2, vertx, testContext, testContext.succeeding(stored -> {
      this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

        model.taskTypeId = stored.id;
        assertIsNotValid(model, "taskTypeId", vertx, testContext);

      }));
    }));

  }

  /**
   * Check that an event with a bad taskId is not valid.
   *
   * @param taskId      invalid task identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The event with the taskId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000", ValidationsTest.STRING_256 })
  public void shouldEventWithBadTaskIdNotBeValid(final String taskId, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.taskId = taskId;
      assertIsNotValid(model, "taskId", vertx, testContext);

    }));

  }

  /**
   * Check that an event with a bad relationship is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEventWithBadRelationshipNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.relationship = SocialNetworkRelationshipType.acquaintance;
      assertIsNotValid(model, "relationship", vertx, testContext);

    }));

  }

}
