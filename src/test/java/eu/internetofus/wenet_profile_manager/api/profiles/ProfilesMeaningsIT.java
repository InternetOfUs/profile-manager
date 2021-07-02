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
import eu.internetofus.common.components.models.Meaning;
import eu.internetofus.common.components.models.MeaningTest;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.List;

/**
 * Check the manipulation of the personal behaviors ({@link Meaning}) in a
 * {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesMeaningsIT extends AbstractProfileFieldResourcesIT<Meaning, Integer> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Profiles.MEANINGS_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Meaning> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new MeaningTest().createModelExample(index);
    return Future.succeededFuture(model);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Meaning createInvalidModelFieldElement() {

    final var model = new MeaningTest().createModelExample(2);
    model.name = null;
    return model;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Meaning> fieldOf(final WeNetUserProfile model) {

    return model.meanings;
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
          profile.meanings = null;
          return StoreServices.storeProfile(profile, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final Meaning source, final Meaning target) {

    assertThat(source).isEqualTo(target);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Integer idOfElementIn(final WeNetUserProfile model, final Meaning element) {

    if (model.meanings == null) {

      return -1;

    } else {

      return model.meanings.indexOf(element);

    }

  }

  /**
   * Merge can not fail never because to be invalid has to set a {@code null} a
   * field, but in merge any {@code null} field is ignored.
   *
   * {@inheritDoc}
   */
  @Override
  public void shouldNotMergeWithInvalidElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    // Disable because the merge every time works
  }

}
