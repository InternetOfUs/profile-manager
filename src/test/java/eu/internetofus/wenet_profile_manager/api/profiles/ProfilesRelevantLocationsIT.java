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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.RelevantLocation;
import eu.internetofus.common.components.profile_manager.RelevantLocationTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.List;

/**
 * Check the manipulation of the {@link RelevantLocation}s in a
 * {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesRelevantLocationsIT extends AbstractProfileFieldResourcesIT<RelevantLocation, String> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Profiles.RELEVANT_LOCATIONS_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext, final Handler<AsyncResult<RelevantLocation>> createHandler) {

    final var element = new RelevantLocationTest().createModelExample(index);
    element.id = null;
    createHandler.handle(Future.succeededFuture(element));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RelevantLocation createInvalidModelFieldElement() {

    final var element = new RelevantLocationTest().createModelExample(0);
    element.label = ValidationsTest.STRING_256;
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<RelevantLocation> fieldOf(final WeNetUserProfile model) {

    return model.relevantLocations;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void storeValidExampleModelWithNullField(final int index, final Vertx vertx,
      final VertxTestContext testContext, final Handler<AsyncResult<WeNetUserProfile>> succeeding) {

    succeeding.handle(testContext
        .assertComplete(new WeNetUserProfileTest().createModelExample(index, vertx, testContext).compose(profile -> {
          profile.id = null;
          profile.relevantLocations = null;
          return StoreServices.storeProfile(profile, vertx, testContext);
        })));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final RelevantLocation source, final RelevantLocation target) {

    source.id = target.id;
    assertThat(source).isEqualTo(target);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOfElementIn(final WeNetUserProfile model, final RelevantLocation element) {

    return element.id;

  }

}
