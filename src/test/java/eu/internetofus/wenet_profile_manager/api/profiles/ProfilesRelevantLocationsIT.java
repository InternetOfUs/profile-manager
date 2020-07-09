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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.RelevantLocation;
import eu.internetofus.common.components.profile_manager.RelevantLocationTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Check the manipulation of the {@link RelevantLocation}s in a {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfilesRelevantLocationsIT extends AbstractProfileFieldManipulationIT<RelevantLocation> {

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
  protected Future<RelevantLocation> createInvalidModel(final Vertx vertx, final VertxTestContext testContext) {

    final RelevantLocation relevantLocation = new RelevantLocation();
    relevantLocation.label = ValidationsTest.STRING_1024;
    return Future.succeededFuture(relevantLocation);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<RelevantLocation> createValidModel(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final RelevantLocation model = new RelevantLocationTest().createModelExample(index);
    return Future.succeededFuture(model);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void idFrom(final String id, final RelevantLocation model) {

    model.id = id;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<RelevantLocation> initiModelsIn(final WeNetUserProfile profile) {

    profile.relevantLocations = new ArrayList<>();
    return profile.relevantLocations;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<RelevantLocation> modelsIn(final WeNetUserProfile profile) {

    return profile.relevantLocations;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAddedModel(final RelevantLocation model, final RelevantLocation addedModel) {

    model.id = addedModel.id;
    assertThat(addedModel).isEqualTo(model);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Class<RelevantLocation> modelClass() {

    return RelevantLocation.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOf(final RelevantLocation model) {

    return model.id;
  }

}
