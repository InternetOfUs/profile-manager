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

package eu.internetofus.wenet_profile_manager.api.trusts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import eu.internetofus.wenet_profile_manager.persistence.TrustsRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TrustsResource}.
 *
 * @see TrustsResource
 *
 * @author UDT-IA, IIIA-CSIC
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TrustsResourceTest {

	/**
	 * Create a resource where the repository is a mocked class.
	 *
	 * @return the created class with the mocked repository.
	 */
	public static TrustsResource createTrustsResource() {

		final TrustsResource resource = new TrustsResource();
		resource.profileRepository = mock(ProfilesRepository.class);
		resource.repository = mock(TrustsRepository.class);
		return resource;

	}

	/**
	 * Check fail store trust event because repository can not store it.
	 *
	 * @param testContext test context.
	 */
	@Test
	public void shouldFailCreateProfileBecasueRepositoryFailsToStore(VertxTestContext testContext) {

		final TrustsResource resource = createTrustsResource();
		final OperationRequest context = mock(OperationRequest.class);
		resource.addTrustEvent(new TrustEventTest().createModelExample(1).toJsonObject(), context,
				testContext.succeeding(create -> {

					assertThat(create.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
					testContext.completeNow();
				}));

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<WeNetUserProfile>>> searchProfileHandler = ArgumentCaptor
				.forClass(Handler.class);
		verify(resource.profileRepository, times(1)).searchProfile(any(), searchProfileHandler.capture());
		final WeNetUserProfile sourceProfile = new WeNetUserProfile();
		sourceProfile.id = "SourceId";
		searchProfileHandler.getValue().handle(Future.succeededFuture(sourceProfile));
		final WeNetUserProfile targetProfile = new WeNetUserProfile();
		targetProfile.id = "TargetId";
		searchProfileHandler.getValue().handle(Future.succeededFuture(targetProfile));

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<Void>>> storeHandler = ArgumentCaptor.forClass(Handler.class);
		verify(resource.repository, times(1)).storeTrustEvent(any(), storeHandler.capture());
		storeHandler.getValue().handle(Future.failedFuture("Store trust event"));

	}

}
