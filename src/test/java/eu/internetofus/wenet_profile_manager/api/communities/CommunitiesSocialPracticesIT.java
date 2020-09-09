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

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.common.components.profile_manager.SocialPractice;
import eu.internetofus.common.components.profile_manager.SocialPracticeTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Check the manipulation of the {@link SocialPractice}s in a {@link CommunityProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class CommunitiesSocialPracticesIT extends AbstractCommunityFieldResourcesIT< SocialPractice, String> {


  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Communities.SOCIAL_PRACTICES_PATH;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createValidModelFieldElementExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<SocialPractice>> createHandler) {

    final var element = new SocialPracticeTest().createModelExample(index);
    createHandler.handle(Future.succeededFuture(element));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SocialPractice createInvalidModelFieldElement() {

    final var element = new SocialPracticeTest().createModelExample(2);
    element.label = ValidationsTest.STRING_256;
    return element;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<SocialPractice> fieldOf(final CommunityProfile model) {

    return model.socialPractices;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void storeValidExampleModelWithNullField(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<CommunityProfile>> succeeding) {

    new CommunityProfileTest().createModelExample(index, vertx, testContext, testContext.succeeding(community -> {
      community.id = null;
      community.socialPractices = null;
      StoreServices.storeCommunity(community, vertx, testContext, succeeding);
    }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final SocialPractice source, final SocialPractice target) {

    source.id = target.id;
    if (source.norms != null && target.norms != null && source.norms.size() == target.norms.size()) {

      final var max = source.norms.size();
      for (var i = 0; i < max; i++) {

        source.norms.get(i).id = target.norms.get(i).id;
      }

    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOfElementIn(final CommunityProfile model, final SocialPractice element) {

    return element.id;
  }

}
