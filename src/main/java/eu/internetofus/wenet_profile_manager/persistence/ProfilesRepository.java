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

import eu.internetofus.wenet_profile_manager.api.profiles.WeNetUserProfile;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The service to manage the {@link WeNetUserProfile} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface ProfilesRepository {

	/**
	 * The address of this service.
	 */
	String ADDRESS = "wenet_profile_manager.persistence.profiles";

	/**
	 * Create a proxy of the {@link ProfilesRepository}.
	 *
	 * @param vertx where the service has to be used.
	 *
	 * @return the profile.
	 */
	static ProfilesRepository createProxy(Vertx vertx) {

		return new ProfilesRepositoryVertxEBProxy(vertx, ProfilesRepository.ADDRESS);
	}

	/**
	 * Search for the profile with the specified identifier.
	 *
	 * @param id            identifier of the profile to search.
	 * @param searchHandler handler to manage the search.
	 */
	void searchProfile(String id, Handler<AsyncResult<WeNetUserProfile>> searchHandler);

	/**
	 * Register this service.
	 *
	 * @param vertx that contains the event bus to use.
	 * @param pool  to create the database connections.
	 */
	static void register(Vertx vertx, PgPool pool) {

		new ServiceBinder(vertx).setAddress(ProfilesRepository.ADDRESS).register(ProfilesRepository.class,
				new ProfilesRepositoryImpl(pool));

	}

}
