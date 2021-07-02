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
import eu.internetofus.common.components.models.Competence;
import eu.internetofus.common.components.models.CompetenceTest;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.List;

/**
 * Check the manipulation of the personal behaviors ({@link Competence}) in a
 * {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesCompetencesIT extends AbstractProfileFieldResourcesIT<Competence, Integer> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Profiles.COMPETENCES_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Competence> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new CompetenceTest().createModelExample(index);
    return Future.succeededFuture(model);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Competence createInvalidModelFieldElement() {

    final var element = new CompetenceTest().createModelExample(0);
    element.level = 100d;
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Competence> fieldOf(final WeNetUserProfile model) {

    return model.competences;
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
          profile.competences = null;
          return StoreServices.storeProfile(profile, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final Competence source, final Competence target) {

    assertThat(source).isEqualTo(target);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Integer idOfElementIn(final WeNetUserProfile model, final Competence element) {

    if (model.competences == null) {

      return -1;

    } else {

      return model.competences.indexOf(element);

    }

  }

}
