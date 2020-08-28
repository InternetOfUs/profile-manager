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

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.common.components.profile_manager.MeaningTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Check the manipulation of the personal behaviors ({@link Meaning}) in a {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesMeaningsIT extends AbstractProfileFieldManipulationByIndexIT<Meaning> {

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
  protected Future<Meaning> createInvalidModel(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new MeaningTest().createModelExample(1);
    model.name = ValidationsTest.STRING_256;
    return Future.succeededFuture(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Meaning> createValidModel(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final Promise<Meaning> promise = Promise.promise();
    final var model = new MeaningTest().createModelExample(index);
    promise.complete(model);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Meaning> initModelsIn(final WeNetUserProfile profile) {

    profile.meanings = new ArrayList<>();
    return profile.meanings;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Meaning> modelsIn(final WeNetUserProfile profile) {

    return profile.meanings;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Class<Meaning> modelClass() {

    return Meaning.class;
  }

}
