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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.Routine;
import eu.internetofus.common.components.models.RoutineTest;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.List;

/**
 * Check the manipulation of the personal behaviors ({@link Routine}) in a
 * {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesPersonalBehaviorsIT extends AbstractProfileFieldResourcesIT<Routine, Integer> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Profiles.PERSONAL_BEHAVIORS_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Routine> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(new RoutineTest().createModelExample(index, vertx, testContext));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Routine createInvalidModelFieldElement() {

    final var element = new RoutineTest().createModelExample(0);
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Routine> fieldOf(final WeNetUserProfile model) {

    return model.personalBehaviors;
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
          profile.personalBehaviors = null;
          return StoreServices.storeProfile(profile, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final Routine source, final Routine target) {

    assertThat(source).isEqualTo(target);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Integer idOfElementIn(final WeNetUserProfile model, final Routine element) {

    if (model.personalBehaviors == null) {

      return -1;

    } else {

      return model.personalBehaviors.indexOf(element);

    }

  }

}
