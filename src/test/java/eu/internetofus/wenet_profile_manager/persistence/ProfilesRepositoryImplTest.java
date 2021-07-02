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

package eu.internetofus.wenet_profile_manager.persistence;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link ProfilesRepositoryImpl}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProfilesRepositoryImplTest {

  /**
   * Should not retrieve profile user identifiers page object.
   *
   * @param testContext context that executes the test.
   */
  @Test
  public void shouldNotRetrieveProfileUserIdsPageObject(final VertxTestContext testContext) {

    final var repository = new ProfilesRepositoryImpl(null, null) {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Future<JsonObject> searchPageObject(final String collectionName, final JsonObject query,
          final FindOptions options, final String resultKey, final Consumer<JsonObject> map) {

        return Future.failedFuture("Not found");
      }

    };
    repository.retrieveProfileUserIdsPageObject(0, 100, testContext.failing(error -> testContext.completeNow()));

  }

}
