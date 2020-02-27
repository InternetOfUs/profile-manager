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

package eu.internetofus.wenet_profile_manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepository;
import eu.internetofus.wenet_profile_manager.persistence.ProfilesRepositoryImpl;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;

/**
 * Extension used to run integration tests over the WeNet profile manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileManagerIntegrationExtension implements ParameterResolver, BeforeTestExecutionCallback,
		AfterTestExecutionCallback, BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

	/**
	 * The common asserts that can be used on the integration tests.
	 */
	public interface Asserts {

		/**
		 * VErify that the body of the response is of the specified class type.
		 *
		 * @param <T>   type of the context,
		 * @param clazz of the content.
		 * @param res   response to get the body content.
		 *
		 * @return the content of the body.
		 */
		static <T> T assertThatBodyIs(Class<T> clazz, HttpResponse<Buffer> res) {

			try {

				assertThat(res.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
				return Json.decodeValue(res.body(), clazz);

			} catch (final Throwable throwable) {

				fail(throwable);
				return null;
			}

		}
	}

	/**
	 * The started WeNet profile manager for do the integration tests.
	 */
	private static WeNetProfileManagerContext context;

	/**
	 * The started mongodb container for do the integration tests.
	 */
	private static MongoContainer mongoContainer;

	/**
	 * Return the defined vertx.
	 *
	 * @return the started vertx.
	 */
	public static synchronized WeNetProfileManagerContext getContext() {

		if (context == null) {

			final Semaphore semaphore = new Semaphore(0);
			new Thread(() -> {

				MongoContainer.createAndStart().onComplete(createdMongo -> {

					if (createdMongo.failed()) {

						InternalLogger.log(Level.ERROR, createdMongo.cause(), "Cannot start the MongoDB container");
						semaphore.release();

					} else {

						mongoContainer = createdMongo.result();
						int port = 0;
						try {
							final ServerSocket server = new ServerSocket(0);
							port = server.getLocalPort();
							server.close();
						} catch (final Throwable ignored) {
						}
						new Main().startWith("-papi.port=" + port, "-ppersistence.host=" + mongoContainer.getContainerIpAddress(),
								"-ppersistence.port=" + mongoContainer.getMappedPort(27017)).onComplete(start -> {

									if (start.failed()) {

										InternalLogger.log(Level.ERROR, start.cause(), "Cannot start the WeNet profile manager");
										mongoContainer.stop();
										mongoContainer = null;

									} else {

										context = start.result();
									}

									semaphore.release();

								});

					}

				});

			}).start();
			try {
				semaphore.acquire();
			} catch (final InterruptedException ignored) {
			}

		}
		return context;

	}

	/**
	 * The extension that manage the vertx components.
	 */
	protected VertxExtension vertxExtension = new VertxExtension();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterAll(ExtensionContext context) throws Exception {

		this.vertxExtension.afterAll(context);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {

		if (getContext() == null) {

			fail("The WeNet profile manager is not started.");
		}
		this.vertxExtension.beforeAll(context);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterEach(ExtensionContext context) throws Exception {

		this.vertxExtension.afterEach(context);

		// close client and pool after the test context has been completed.
		final WebClient client = context.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
				.remove(WebClient.class.getName(), WebClient.class);
		if (client != null) {

			client.close();
		}

		final MongoClient pool = context.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
				.remove(MongoClient.class.getName(), MongoClient.class);
		if (pool != null) {

			pool.close();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {

		this.vertxExtension.beforeEach(context);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {

		this.vertxExtension.afterTestExecution(context);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {

		this.vertxExtension.beforeTestExecution(context);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		final Class<?> type = parameterContext.getParameter().getType();
		return type == WebClient.class || type == WeNetProfileManagerContext.class || type == MongoClient.class
				|| type == ProfilesRepository.class
				|| this.vertxExtension.supportsParameter(parameterContext, extensionContext);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		final Class<?> type = parameterContext.getParameter().getType();
		if (type == Vertx.class) {

			return getContext().vertx;

		} else if (type == WebClient.class) {

			return extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
					.getOrComputeIfAbsent(WebClient.class.getName(), key -> {

						final WeNetProfileManagerContext context = getContext();
						final WebClientOptions options = new WebClientOptions();
						options.setDefaultHost(context.configuration.getJsonObject("api").getString("host"));
						options.setDefaultPort(context.configuration.getJsonObject("api").getInteger("port"));
						return WebClient.create(context.vertx, options);
					}, WebClient.class);

		} else if (type == MongoClient.class || type == ProfilesRepository.class) {

			final MongoClient pool = extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
					.getOrComputeIfAbsent(MongoClient.class.getName(), key -> {

						final WeNetProfileManagerContext context = getContext();
						final JsonObject persitenceConf = context.configuration.getJsonObject("persistence", new JsonObject());
						return MongoClient.create(context.vertx, persitenceConf);
					}, MongoClient.class);

			if (type == ProfilesRepository.class) {

				return extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
						.getOrComputeIfAbsent(ProfilesRepository.class.getName(), key -> {

							return new ProfilesRepositoryImpl(pool);
						}, ProfilesRepository.class);

			} else {

				return pool;
			}

		} else if (type == WeNetProfileManagerContext.class) {

			return getContext();

		} else {

			return this.vertxExtension.resolveParameter(parameterContext, extensionContext);
		}
	}

}
