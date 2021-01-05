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
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.common.components.profile_manager.MeaningTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
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
  protected void createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext, final Handler<AsyncResult<Meaning>> createHandler) {

    final var model = new MeaningTest().createModelExample(index);
    createHandler.handle(Future.succeededFuture(model));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Meaning createInvalidModelFieldElement() {

    final var element = new MeaningTest().createModelExample(0);
    element.name = ValidationsTest.STRING_256;
    return element;

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
  protected void storeValidExampleModelWithNullField(final int index, final Vertx vertx,
      final VertxTestContext testContext, final Handler<AsyncResult<WeNetUserProfile>> succeeding) {

    succeeding.handle(testContext
        .assertComplete(new WeNetUserProfileTest().createModelExample(index, vertx, testContext).compose(profile -> {
          profile.id = null;
          profile.meanings = null;
          return StoreServices.storeProfile(profile, vertx, testContext);
        })));

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

}
