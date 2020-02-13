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

package eu.internetofus.wenet_profile_manager.api.profiles;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.wenet_profile_manager.ModelTestCase;
import eu.internetofus.wenet_profile_manager.ValidationsTest;
import eu.internetofus.wenet_profile_manager.WeNetProfileManagerIntegrationExtension;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link SocialNetworkRelationship}
 *
 * @see SocialNetworkRelationship
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetProfileManagerIntegrationExtension.class)
public class SocialNetworkRelationshipTest extends ModelTestCase<SocialNetworkRelationship> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocialNetworkRelationship createModelExample(int index) {

		final SocialNetworkRelationship model = new SocialNetworkRelationship();
		model.userId = String.valueOf(index);
		model.type = SocialNetworkRelationshipType.acquaintance;
		return model;

	}

	/**
	 * Create an example model that has the specified index.
	 *
	 * @param index      to use in the example.
	 * @param repository to use to create the model.
	 *
	 * @return the example.
	 */
	public Future<SocialNetworkRelationship> createModelExample(int index, ProfilesRepository repository) {

		final Promise<SocialNetworkRelationship> promise = Promise.promise();
		final Future<SocialNetworkRelationship> future = promise.future();

		final SocialNetworkRelationship relation = new SocialNetworkRelationship();
		relation.type = SocialNetworkRelationshipType.acquaintance;
		repository.storeProfile(new WeNetUserProfile(), stored -> {

			if (stored.failed()) {

				promise.fail(stored.cause());

			} else {

				final WeNetUserProfile profile = stored.result();
				relation.userId = profile.id;
				promise.complete(relation);
			}

		});

		return future;
	}

	/**
	 * Check that the {@link #createModelExample(int,ProfilesRepository)} is valid.
	 *
	 * @param index       to verify
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String,
	 *      eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleFromRepositoryBeValid(int index, ProfilesRepository repository,
			VertxTestContext testContext) {

		this.createModelExample(index, repository).onComplete(created -> {

			if (created.failed()) {

				testContext.failNow(created.cause());

			} else {

				final SocialNetworkRelationship model = created.result();
				testContext.assertComplete(model.validate("codePrefix", repository))
						.setHandler(result -> testContext.completeNow());
			}

		});
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldFullModelBeValid(ProfilesRepository repository, VertxTestContext testContext) {

		this.createModelExample(1, repository).onComplete(created -> {

			if (created.failed()) {

				testContext.failNow(created.cause());

			} else {

				final SocialNetworkRelationship model = created.result();
				final SocialNetworkRelationship expected = new SocialNetworkRelationship();
				expected.userId = model.userId;
				expected.type = model.type;
				model.userId = "     " + model.userId + "    ";
				testContext.assertComplete(model.validate("codePrefix", repository)).setHandler(result -> {

					assertThat(model).isEqualTo(expected);
					testContext.completeNow();
				});
			}

		});

	}

	/**
	 * Check that a model with a bad user id is not valid.
	 *
	 * @param userId      that is not valid.
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String,ProfilesRepository)
	 */
	@ParameterizedTest(name = "Should not be valid  a SocialNetworkRelationship with an userId = {0}")
	@NullAndEmptySource
	@ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d", ValidationsTest.STRING_256 })
	public void shouldNotBeValidWithBadUserIdentifier(String userId, ProfilesRepository repository,
			VertxTestContext testContext) {

		final SocialNetworkRelationship model = new SocialNetworkRelationship();
		model.userId = userId;
		model.type = SocialNetworkRelationshipType.colleague;
		testContext.assertFailure(model.validate("codePrefix", repository)).setHandler(result -> {

			testContext.completeNow();
		});

	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param repository  to create profiles to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String,ProfilesRepository)
	 */
	@Test
	public void shouldNotBeValidAmodelWithoutAType(ProfilesRepository repository, VertxTestContext testContext) {

		repository.storeProfile(new WeNetUserProfile(), stored -> {

			if (stored.failed()) {

				testContext.failNow(stored.cause());

			} else {

				final WeNetUserProfile profile = stored.result();
				final SocialNetworkRelationship model = new SocialNetworkRelationship();
				model.type = null;
				model.userId = profile.id;
				testContext.assertFailure(model.validate("codePrefix", repository)).setHandler(result -> {

					testContext.completeNow();
				});
			}

		});

	}

}
