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

package eu.internetofus.wenet_profile_manager.api.communities;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.CommunityProfileTest;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.models.ProtocolNormTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Check the manipulation of the {@link ProtocolNorm}s in a
 * {@link CommunityProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesNormsIT extends AbstractCommunityFieldResourcesIT<ProtocolNorm, Integer> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Communities.NORMS_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<ProtocolNorm> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final var element = new ProtocolNormTest().createModelExample(index);
    return Future.succeededFuture(element);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ProtocolNorm createInvalidModelFieldElement() {

    final var element = new ProtocolNormTest().createModelExample(2);
    element.thenceforth = element.whenever;
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<ProtocolNorm> fieldOf(final CommunityProfile model) {

    return model.norms;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<CommunityProfile> storeValidExampleModelWithFieldElements(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new CommunityProfileTest().createModelExample(index, vertx, testContext).compose(community -> {
          community.id = null;
          community.norms = new ArrayList<>();
          community.norms.add(new ProtocolNormTest().createModelExample(index - 1));
          community.norms.add(new ProtocolNormTest().createModelExample(index));
          community.norms.add(new ProtocolNormTest().createModelExample(index + 1));
          return StoreServices.storeCommunity(community, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<CommunityProfile> storeValidExampleModelWithNullField(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new CommunityProfileTest().createModelExample(index, vertx, testContext).compose(community -> {
          community.id = null;
          community.norms = null;
          return StoreServices.storeCommunity(community, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final ProtocolNorm source, final ProtocolNorm target) {

    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Integer idOfElementIn(final CommunityProfile model, final ProtocolNorm element) {

    if (model.norms == null) {

      return -1;

    } else {

      return model.norms.indexOf(element);

    }

  }

}
