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

import eu.internetofus.common.model.Model;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.CommunityProfileTest;
import eu.internetofus.common.vertx.AbstractModelFieldResourcesIT;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Check the manipulation of a field in a {@link CommunityProfile}.
 *
 * @param <T> type of the elements in the field.
 * @param <I> type of identifier of the element.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public abstract class AbstractCommunityFieldResourcesIT<T extends Model, I>
    extends AbstractModelFieldResourcesIT<CommunityProfile, String, T, I> {

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
  protected String idOfModel(final CommunityProfile model) {

    return model.id;
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
          return StoreServices.storeCommunity(community, vertx, testContext);
        }));

  }

}
