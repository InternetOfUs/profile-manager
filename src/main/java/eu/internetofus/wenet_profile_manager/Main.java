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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.tinylog.Level;
import org.tinylog.Logger;
import org.tinylog.Supplier;
import org.tinylog.jul.JulTinylogBridge;
import org.tinylog.provider.InternalLogger;

import eu.internetofus.wenet_profile_manager.api.APIVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * Start the vert.x and run the API verticle.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Main {

	/**
	 * The name of the option to obtain help information.
	 */
	public static final String HELP_OPTION = "h";

	/**
	 * The name of the option to show the version of the software.
	 */
	public static final String VERSION_OPTION = "v";

	/**
	 * The name of the option to define a directory where are the configuration
	 * files.
	 */
	public static final String CONF_DIR_OPTION = "c";

	/**
	 * The name of the option to define a property value.
	 */
	public static final String PROPERTY_OPTION = "p";

	/**
	 * The configurations to start the vertx verticles.
	 */
	protected ConfigRetrieverOptions retrieveOptions;

	/**
	 * The configuration property that define if has to store the effective
	 * configuration.
	 */
	public static final String STORE_EFFECTIVE_CONFIGURATION = "store_effective_configuration";

	/**
	 * The configuration property that contains the path where the effective
	 * configuration has to be stored.
	 */
	public static final String EFFECTIVE_CONFIGURATION_PATH = "effective_configuration_path";

	/**
	 * The maximum milliseconds that the system has to be open. If it is {0} or less
	 * the system is available for ever.
	 */
	protected long delay;

	/**
	 * Create the component to start the server.
	 */
	public Main() {

		this.retrieveOptions = new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("file")
				.setFormat("json").setConfig(new JsonObject().put("path", "wenet-profile-manager.configuration.json")));
		this.delay = -1l;

	}

	/**
	 * Start the verticles to manage the HTTP request to the profile-manager API.
	 *
	 * @return the promise of the started Vert.x if it can be started.
	 */
	public Future<WeNetProfileManagerContext> startVertx() {

		final Promise<WeNetProfileManagerContext> promise = Promise.promise();
		final Vertx vertx = Vertx.vertx();
		final ConfigRetriever retriever = ConfigRetriever.create(vertx, this.retrieveOptions);
		retriever.getConfig(confResult -> {

			if (confResult.succeeded()) {

				final JsonObject conf = confResult.result();
				vertx.close();

				Logger.info("Loaded configuration: {}", conf);
				if (conf.getBoolean(STORE_EFFECTIVE_CONFIGURATION, Boolean.TRUE)) {
					try {

						final Path effectiveConf = FileSystems.getDefault()
								.getPath(conf.getString(EFFECTIVE_CONFIGURATION_PATH, "var/effective-conf.json"));
						Files.write(effectiveConf, conf.encodePrettily().getBytes());
						Logger.info("Stored effective configuration at '{}'", effectiveConf);

					} catch (final Throwable throwable) {

						Logger.error(throwable, "Cannot store the effective configuration");
					}

				}

				// Create a new Vert.x instance using the retrieve configuration
				final VertxOptions options = new VertxOptions(conf);
				final Vertx newVertx = Vertx.vertx(options);

				// deploy the verticles
				final DeploymentOptions deployOptions = new DeploymentOptions().setConfig(conf);
				newVertx.deployVerticle(new MainVerticle(), deployOptions, deploy -> {
					if (deploy.succeeded()) {

						promise.complete(new WeNetProfileManagerContext(newVertx, conf));

					} else {

						promise.fail(deploy.cause());
						newVertx.close();
					}
				});

			} else {

				promise.fail(confResult.cause());
			}
		});
		return promise.future();

	}

	/**
	 * Set up the logging system.
	 */
	protected void startLoggingSystems() {

		JulTinylogBridge.activate();

	}

	/**
	 * Create the options for the command line.
	 *
	 * @return the options that can be used on the command line.
	 */
	protected Options createOptions() {

		final ResourceBundle l10n = ResourceBundle.getBundle(Main.class.getName().replaceAll("\\.", "/"));
		final Options options = new Options();
		options.addOption(HELP_OPTION, l10n.getString(HELP_OPTION + "_large"), false,
				l10n.getString(HELP_OPTION + "_description"));
		options.addOption(VERSION_OPTION, l10n.getString(VERSION_OPTION + "_large"), false,
				l10n.getString(VERSION_OPTION + "_description"));
		options.addOption(Option.builder(CONF_DIR_OPTION).longOpt(l10n.getString(CONF_DIR_OPTION + "_large"))
				.numberOfArgs(1).argName(l10n.getString(CONF_DIR_OPTION + "_argName"))
				.desc(l10n.getString(CONF_DIR_OPTION + "_description")).build());
		options.addOption(Option.builder(PROPERTY_OPTION).longOpt(l10n.getString(PROPERTY_OPTION + "_large"))
				.numberOfArgs(2).argName(l10n.getString(PROPERTY_OPTION + "_argName")).valueSeparator()
				.desc(l10n.getString(CONF_DIR_OPTION + "_description")).build());
		return options;

	}

	/**
	 * Configure the main with the parameters.
	 *
	 * @param args arguments to configure the server.
	 *
	 * @return the parsed command line, or {@code null} if the arguments are wrong.
	 */
	protected CommandLine parse(String... args) {

		try {

			final CommandLineParser parser = new DefaultParser();
			final Options options = this.createOptions();
			final CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption(HELP_OPTION)) {

				this.printHelpMessage(options);

			} else if (cmd.hasOption(VERSION_OPTION)) {

				this.printVersion();

			} else {

				Logger.debug("Start Main with: {}", (Supplier<String>) () -> Arrays.toString(args));
				return cmd;
			}

		} catch (final Throwable throwable) {

			InternalLogger.log(Level.ERROR, throwable.getLocalizedMessage());
			InternalLogger.log(Level.INFO, "Call with -h to obtain help information");
		}

		return null;

	}

	/**
	 * Print the help message.
	 *
	 * @param options used to create the command parser.
	 */
	protected void printHelpMessage(Options options) {

		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("profile-manager", options);

	}

	/**
	 * Print the software version.
	 */
	protected void printVersion() {

		String version = "Unknown";
		try {

			final Package currentPackage = this.getClass().getPackage();
			version = currentPackage.getImplementationVersion();

		} catch (final Throwable ignored) {
		}

		InternalLogger.log(Level.INFO, version);

	}

	/**
	 * Configure the retriever to get the options.
	 *
	 * @param cmd command line options.
	 */
	protected void configure(CommandLine cmd) {

		if (cmd.hasOption(PROPERTY_OPTION)) {

			final JsonObject userProperties = new JsonObject();
			final Properties properties = cmd.getOptionProperties(Main.PROPERTY_OPTION);
			for (final String key : properties.stringPropertyNames()) {

				JsonObject property = userProperties;
				final String value = properties.getProperty(key);
				final String[] sections = key.split("\\.");
				for (int i = 0; i < sections.length - 1; i++) {

					if (property.containsKey(sections[i])) {

						property = property.getJsonObject(sections[i]);

					} else {

						final JsonObject sectionProperty = new JsonObject();
						property.put(sections[i], sectionProperty);
						property = sectionProperty;

					}

				}
				final Object content = Json.decodeValue(value);
				property.put(sections[sections.length - 1], content);

			}

			final ConfigStoreOptions userPropertiesConf = new ConfigStoreOptions().setType("json").setConfig(userProperties);
			this.retrieveOptions = this.retrieveOptions.addStore(userPropertiesConf);

		}

		if (cmd.hasOption(CONF_DIR_OPTION)) {

			final String confDirValue = cmd.getOptionValue(CONF_DIR_OPTION);

			try {

				final Path confPath = Path.of(confDirValue);
				Files.list(confPath).filter(confFilePath -> {

					final File file = confFilePath.toFile();
					return file.isFile() && file.canRead();

				}).sorted((a, b) -> b.getFileName().compareTo(a.getFileName())).forEach(confFilePath -> {

					String format = "json";
					final String fileName = confFilePath.getFileName().toString();
					if (fileName.endsWith("yml")) {

						format = "yaml";
					}
					final ConfigStoreOptions confFileOptions = new ConfigStoreOptions().setType("file").setFormat(format)
							.setConfig(new JsonObject().put("path", confFilePath.toFile().getAbsolutePath()));
					this.retrieveOptions = this.retrieveOptions.addStore(confFileOptions);

				});

			} catch (final Throwable throwable) {

				Logger.error(throwable, "Cannot load all the configuration files from {}", confDirValue);
			}

		}

	}

	/**
	 * Satrt the server with the specified arguments.
	 *
	 * @param args arguments to configure the main process.
	 *
	 * @return the component that will called when the server has started or not.
	 */
	public Future<WeNetProfileManagerContext> startWith(String... args) {

		this.startLoggingSystems();
		final CommandLine cmd = this.parse(args);
		if (cmd != null) {

			this.configure(cmd);
			return this.startVertx();

		} else {

			return Future.failedFuture("Bad arguments");
		}

	}

	/**
	 * Start the verticles.
	 *
	 * @param args arguments to configure the main process.
	 *
	 * @see APIVerticle
	 */
	public static void main(String... args) {

		final Main main = new Main();
		main.startWith(args).onComplete(result -> {

			if (!result.succeeded()) {

				InternalLogger.log(Level.ERROR, result.cause(), "Can not start the Vert.x !\n Check the Logs to known why.");
			}

		});

	}

}