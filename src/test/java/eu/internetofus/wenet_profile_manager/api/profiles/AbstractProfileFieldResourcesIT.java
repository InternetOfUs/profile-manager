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

package eu.internetofus.wenet_profile_manager.api.profiles;

import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import eu.internetofus.common.vertx.AbstractModelFieldResourcesIT;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Check the manipulation of a field in a {@link WeNetUserProfile}.
 *
 * @param <T> type of the elements in the field.
 * @param <I> type of identifier of the element.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public abstract class AbstractProfileFieldResourcesIT<T extends Model, I> extends AbstractModelFieldResourcesIT<WeNetUserProfile, String, T, I> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String modelPath() {

    return Profiles.PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOfModel(final WeNetUserProfile model) {

    return model.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void storeValidExampleModelWithFieldElements(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<WeNetUserProfile>> succeeding) {

    new WeNetUserProfileTest().createModelExample(index, vertx, testContext, testContext.succeeding(profile -> {
      profile.id = null;
      StoreServices.storeProfile(profile, vertx, testContext, succeeding);
    }));

  }

}