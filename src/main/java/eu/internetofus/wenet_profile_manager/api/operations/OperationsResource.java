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
package eu.internetofus.wenet_profile_manager.api.operations;

import eu.internetofus.common.vertx.ModelContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.wenet_profile_manager.api.profiles.Profiles;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

/**
 * Resource that provide the methods for the {@link Operations}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OperationsResource implements Operations {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * Create a new instance to provide the services of the {@link Profiles}.
   *
   * @param vertx with the event bus to use.
   */
  public OperationsResource(final Vertx vertx) {

    this.vertx = vertx;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void diversity(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = new ModelContext<DiversityData, Void>();
    model.name = "diversityData";
    model.type = DiversityData.class;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.toModel(body, model, context, () -> {

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void similarity(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = new ModelContext<SimilarityData, Void>();
    model.name = "similarityData";
    model.type = SimilarityData.class;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.toModel(body, model, context, () -> {

    });

  }

}
