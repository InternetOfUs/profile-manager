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

import java.util.List;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.NormTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Check the manipulation of the {@link Norm}s in a {@link CommunityProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesNormsIT extends AbstractCommunityFieldResourcesIT<Norm, String> {

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
  protected void createValidModelFieldElementExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<Norm>> createHandler) {

    final var element = new NormTest().createModelExample(index);
    createHandler.handle(Future.succeededFuture(element));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Norm createInvalidModelFieldElement() {

    final var element = new NormTest().createModelExample(2);
    element.attribute = ValidationsTest.STRING_256;
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Norm> fieldOf(final CommunityProfile model) {

    return model.norms;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void storeValidExampleModelWithNullField(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<CommunityProfile>> succeeding) {

    new CommunityProfileTest().createModelExample(index, vertx, testContext, testContext.succeeding(community -> {
      community.id = null;
      community.norms = null;
      StoreServices.storeCommunity(community, vertx, testContext, succeeding);
    }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final Norm source, final Norm target) {

    source.id = target.id;
    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOfElementIn(final CommunityProfile model, final Norm element) {

    return element.id;
  }

}
