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

import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.profile_diversity_manager.AgentData;
import eu.internetofus.common.components.profile_diversity_manager.AgentsData;
import eu.internetofus.common.components.profile_diversity_manager.WeNetProfileDiversityManager;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.vertx.ModelContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_profile_manager.api.profiles.Profiles;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

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

      if (model.source.attributes == null || model.source.attributes.isEmpty()) {

        ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.BAD_REQUEST, "bad_" + model.name,
            "You must define at least one attribute to calculate the diversity");

      } else if (model.source.userIds == null || model.source.userIds.size() < 2) {

        ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.BAD_REQUEST, "bad_" + model.name,
            "You must define at least two users to obtain to calculate the diversity");

      } else {

        Future<AgentsData> future = Future.succeededFuture(this.createEmptyAgentsData());
        for (final var profileId : model.source.userIds) {

          future = future.compose(this.merge(profileId, model.source.attributes));

        }

        future.onComplete(search -> {

          if (search.failed()) {

            final var cause = search.cause();
            Logger.trace(cause, "Not found profile to calculate diversity");
            ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

          } else {

            final var data = search.result();
            WeNetProfileDiversityManager.createProxy(this.vertx).calculateDiversityOf(data).onComplete(calculus -> {

              final var result = new DiversityValue();
              if (calculus.failed()) {

                Logger.trace(calculus.cause(), "Cannot calculate diversity");
                result.diversity = 0d;

              } else {

                final var diversity = calculus.result();
                result.diversity = diversity.value;
              }

              ServiceResponseHandlers.responseOk(resultHandler, result);

            });
          }

        });
      }

    });

  }

  /**
   * Create an empty agents data.
   *
   * @return a data with empty collections.
   */
  private AgentsData createEmptyAgentsData() {

    final var data = new AgentsData();
    data.agents = new ArrayList<>();
    data.qualitativeAttributes = new HashMap<>();
    data.quantitativeAttributes = new HashSet<>();
    return data;

  }

  /**
   * Merge a profile into an agents data.
   *
   * @param profileId  identifier of the profile to merge.
   * @param attributes to get of the profile.
   *
   * @return the future agents data.
   */
  protected Function<AgentsData, Future<AgentsData>> merge(final String profileId, final Set<String> attributes) {

    return data -> {

      final Promise<AgentsData> promise = Promise.promise();
      ProfilesRepository.createProxy(this.vertx).searchProfile(profileId, search -> {

        if (search.failed()) {

          promise.fail(new ValidationErrorException("not_found_profile",
              "Cannot found a profile with the identifier '" + profileId + "'.", search.cause()));

        } else {

          final var profile = search.result();
          final var agent = new AgentData();
          agent.id = profileId;
          agent.qualitativeAttributes = new HashMap<>();
          agent.qualitativeAttributes = new HashMap<>();
          data.agents.add(agent);
          for (final var attributeName : attributes) {

            final var value = profile.getValue(attributeName);
            if (value instanceof String) {

              final var option = (String) value;
              agent.qualitativeAttributes.put(attributeName, option);

              var options = data.qualitativeAttributes.get(attributeName);
              if ("gender".equals(attributeName)) {

                if (options == null) {

                  options = new HashSet<>(Arrays.asList(WeNetUserProfile.GENDERS));
                  data.qualitativeAttributes.put(attributeName, options);
                }

              } else {

                if (options == null) {

                  options = new HashSet<>();
                  data.qualitativeAttributes.put(attributeName, options);
                }
                options.add(option);
              }

            } else if (value instanceof Number) {

              final var quantitativeValue = ((Number) value).doubleValue();
              if (quantitativeValue < 0d || quantitativeValue > 1d) {

                promise.fail(
                    new ValidationErrorException("bad_quantitative_profile_attribute_value", "The quantitative value '"
                        + attributeName + "' of the profile '" + profileId + "'is not on the range [0,1]."));
                return;
              }
              agent.quantitativeAttributes.put(attributeName, quantitativeValue);

            } else if (value == null) {

              promise.fail(new ValidationErrorException("bad_profile_attribute_value",
                  "The attribute '" + attributeName + "' is not defined on the profile '" + profileId + "'."));
              return;

            } else {

              promise.fail(new ValidationErrorException("bad_profile_attribute_value",
                  "Cannot calculate diversity for the attribute '" + attributeName + "' of the profile '" + profileId
                      + "'."));
              return;
            }

          } // End for attributeName

          promise.complete(data);
        }

      });

      return promise.future();
    };
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
