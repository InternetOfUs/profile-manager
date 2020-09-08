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

package eu.internetofus.wenet_profile_manager.api.communities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.common.vertx.AbstractModelResourcesIT;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * The integration test over the {@link Communities}.
 *
 * @see Communities
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class CommunitiesIT extends AbstractModelResourcesIT<CommunityProfile, String> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String modelPath() {

    return Communities.PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CommunityProfile createInvalidModel() {

    return new CommunityProfileTest().createModelExample(1);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createValidModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<CommunityProfile>> createHandler) {

    new CommunityProfileTest().createModelExample(index, vertx, testContext, testContext.succeeding(model -> {
      model.id = null;
      createHandler.handle(Future.succeededFuture(model));
    }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void storeModel(final CommunityProfile source, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<CommunityProfile>> succeeding) {

    StoreServices.storeCommunity(source, vertx, testContext, succeeding);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertThatCreatedEquals(final CommunityProfile source, final CommunityProfile target) {

    source.id = target.id;
    source._creationTs = target._creationTs;
    source._lastUpdateTs = target._lastUpdateTs;
    if (source.norms != null && target.norms != null && source.norms.size() == target.norms.size()) {

      final var max = source.norms.size();
      for (var i = 0; i < max; i++) {

        source.norms.get(i).id = target.norms.get(i).id;
      }

    }
    if (source.socialPractices != null && target.socialPractices != null && source.socialPractices.size() == target.socialPractices.size()) {

      final var max = source.socialPractices.size();
      for (var i = 0; i < max; i++) {

        source.socialPractices.get(i).id = target.socialPractices.get(i).id;
      }

    }

    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOf(final CommunityProfile model) {

    return model.id;
  }

}
