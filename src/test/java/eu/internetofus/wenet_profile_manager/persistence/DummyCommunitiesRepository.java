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

package eu.internetofus.wenet_profile_manager.persistence;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * A dummy implementation of the {@link CommunitiesRepository}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyCommunitiesRepository implements CommunitiesRepository {

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchCommunityObject(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeCommunity(final JsonObject community, final Handler<AsyncResult<JsonObject>> storeHandler) {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(final JsonObject community, final Handler<AsyncResult<Void>> updateHandler) {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityProfilesPageObject(final JsonObject query, final JsonObject sort, final int offset, final int limit, final Handler<AsyncResult<JsonObject>> handler) {

  }

}