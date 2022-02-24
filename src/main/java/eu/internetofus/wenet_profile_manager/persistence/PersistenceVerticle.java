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

import eu.internetofus.common.vertx.AbstractPersistenceVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * The verticle that provide the persistence services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PersistenceVerticle extends AbstractPersistenceVerticle {

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Void> registerRepositoriesFor(final String schemaVersion) {

    final var conf = this.config().getJsonObject("profileManager", new JsonObject());
    return CompositeFuture.all(
        ProfilesRepository.register(this.vertx, this.pool, schemaVersion,
            conf.getBoolean("migrateProfilesInBackground", true)),
        TrustsRepository.register(this.vertx, this.config(), this.pool, schemaVersion,
            conf.getBoolean("migrateTrustsInBackground", true)),
        CommunitiesRepository.register(this.vertx, this.pool, schemaVersion,
            conf.getBoolean("migrateCommunitiesInBackground", true)),
        RelationshipsRepository.register(this.vertx, this.pool, schemaVersion,
            conf.getBoolean("migrateRelationshipsInBackground", true)))
        .map(any -> null);

  }

}
