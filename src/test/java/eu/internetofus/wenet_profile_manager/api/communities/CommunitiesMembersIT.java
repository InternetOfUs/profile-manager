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
import eu.internetofus.common.components.models.CommunityMember;
import eu.internetofus.common.components.models.CommunityMemberTest;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.CommunityProfileTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.List;

/**
 * Check the manipulation of the {@link CommunityMember}s in a
 * {@link CommunityProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesMembersIT extends AbstractCommunityFieldResourcesIT<CommunityMember, String> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String fieldPath() {

    return Communities.COMMUNITY_MEMBERS_PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<CommunityMember> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(new CommunityMemberTest().createModelExample(index, vertx, testContext));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CommunityMember createInvalidModelFieldElement() {

    final var model = new CommunityMemberTest().createModelExample(2);
    model.privileges.add("duplicated");
    model.privileges.add("duplicated");
    return model;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<CommunityMember> fieldOf(final CommunityProfile model) {

    return model.members;
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
          community.members = null;
          return StoreServices.storeCommunity(community, vertx, testContext);
        }));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertEqualsAdded(final CommunityMember source, final CommunityMember target) {

    source.userId = target.userId;
    source._creationTs = target._creationTs;
    source._lastUpdateTs = target._lastUpdateTs;
    assertThat(source).isEqualTo(target);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOfElementIn(final CommunityProfile model, final CommunityMember element) {

    return element.userId;
  }

}
