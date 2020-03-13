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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import eu.internetofus.common.api.models.Model;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link ProfilesResource}.
 *
 * @see ProfilesResource
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProfilesResourceTest {

	/**
	 * Create a resource where the repository is a mocked class.
	 *
	 * @return the created class with the mocked repository.
	 */
	public static ProfilesResource createProfilesResource() {

		final ProfilesResource resource = new ProfilesResource();
		resource.repository = mock(ProfilesRepository.class);
		return resource;

	}

	/**
	 * Check fail create profile because repository can not store it.
	 *
	 * @param testContext test context.
	 */
	@Test
	public void shouldFailCreateProfileBecasueRepositoryFailsToStore(VertxTestContext testContext) {

		final ProfilesResource resource = createProfilesResource();
		final OperationRequest context = mock(OperationRequest.class);
		resource.createProfile(new JsonObject(), context, testContext.succeeding(create -> {

			assertThat(create.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			testContext.completeNow();
		}));

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<WeNetUserProfile>>> storeHandler = ArgumentCaptor.forClass(Handler.class);
		verify(resource.repository, times(1)).storeProfile(any(), storeHandler.capture());
		storeHandler.getValue().handle(Future.failedFuture("Store profile error"));

	}

	/**
	 * Check fail update profile because repository can not update it.
	 *
	 * @param testContext test context.
	 */
	@Test
	public void shouldFailUpdateProfileBecasueRepositoryFailsToUpdate(VertxTestContext testContext) {

		final ProfilesResource resource = createProfilesResource();
		final OperationRequest context = mock(OperationRequest.class);
		resource.updateProfile("profileId", new JsonObject().put("name", new JsonObject().put("first", "John")), context,
				testContext.succeeding(update -> {

					assertThat(update.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
					testContext.completeNow();
				}));

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<WeNetUserProfile>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
		verify(resource.repository, times(1)).searchProfile(any(), searchHandler.capture());
		searchHandler.getValue().handle(
				Future.succeededFuture(Model.fromJsonObject(new JsonObject().put("id", "profileId"), WeNetUserProfile.class)));
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<Void>>> updateHandler = ArgumentCaptor.forClass(Handler.class);
		verify(resource.repository, times(1)).updateProfile(any(JsonObject.class), updateHandler.capture());
		updateHandler.getValue().handle(Future.failedFuture("Update profile error"));

	}

	/**
	 * Check update profile but fail to store the historic.
	 *
	 * @param testContext test context.
	 */
	@Test
	public void shouldUpdateProfileButFailStoreHistoric(VertxTestContext testContext) {

		final ProfilesResource resource = createProfilesResource();
		final OperationRequest context = mock(OperationRequest.class);
		resource.updateProfile("profileId", new JsonObject().put("name", new JsonObject().put("first", "John")), context,
				testContext.succeeding(update -> {

					assertThat(update.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
					testContext.completeNow();
				}));

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<WeNetUserProfile>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
		verify(resource.repository, times(1)).searchProfile(any(), searchHandler.capture());
		searchHandler.getValue().handle(
				Future.succeededFuture(Model.fromJsonObject(new JsonObject().put("id", "profileId"), WeNetUserProfile.class)));
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<Void>>> updateHandler = ArgumentCaptor.forClass(Handler.class);
		verify(resource.repository, times(1)).updateProfile(any(JsonObject.class), updateHandler.capture());
		updateHandler.getValue().handle(Future.succeededFuture());
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Handler<AsyncResult<HistoricWeNetUserProfile>>> storeHandler = ArgumentCaptor
				.forClass(Handler.class);
		verify(resource.repository, times(1)).storeHistoricProfile(any(), storeHandler.capture());
		storeHandler.getValue().handle(Future.failedFuture("Store historic error"));

	}

}
