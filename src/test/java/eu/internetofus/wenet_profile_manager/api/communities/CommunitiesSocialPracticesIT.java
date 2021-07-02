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
import eu.internetofus.common.components.models.SocialPractice;
import eu.internetofus.common.components.models.SocialPracticeTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Check the manipulation of the {@link SocialPractice}s in a
 * {@link CommunityProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesSocialPracticesIT extends AbstractCommunityFieldResourcesIT<SocialPractice, String> {

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
  protected Future<SocialPractice> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final var element = new SocialPracticeTest().createModelExample(index);
    return Future.succeededFuture(element);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SocialPractice createInvalidModelFieldElement() {

    final var element = new SocialPracticeTest().createModelExample(2);
    element.norms.get(0).whenever = element.norms.get(0).thenceforth;
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
  protected Future<CommunityProfile> storeValidExampleModelWithFieldElements(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new CommunityProfileTest().createModelExample(index, vertx, testContext).compose(community -> {
          community.id = null;
          community.socialPractices = new ArrayList<>();
          community.socialPractices.add(new SocialPracticeTest().createModelExample(index - 1));
          community.socialPractices.add(new SocialPracticeTest().createModelExample(index));
          community.socialPractices.add(new SocialPracticeTest().createModelExample(index + 1));
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
          community.socialPractices = null;
          return StoreServices.storeCommunity(community, vertx, testContext);

        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final SocialPractice source, final SocialPractice target) {

    source.id = target.id;
    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOfElementIn(final CommunityProfile model, final SocialPractice element) {

    return element.id;
  }

}
