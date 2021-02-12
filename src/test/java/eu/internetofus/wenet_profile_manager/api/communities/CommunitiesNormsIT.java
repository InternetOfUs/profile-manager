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

package eu.internetofus.wenet_profile_manager.api.communities;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.task_manager.ProtocolNorm;
import eu.internetofus.common.components.task_manager.ProtocolNormTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Check the manipulation of the {@link Norm}s in a {@link CommunityProfile}.
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
