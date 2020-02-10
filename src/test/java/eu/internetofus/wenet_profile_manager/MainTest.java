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

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemErrGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.Level;

/**
 * Test the {@link Main}
 *
 * @see Main
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MainTest {

	/**
	 * Verify the options are localized.
	 *
	 * @param lang language to load the options.
	 */
	@ParameterizedTest(name = "Should create options for locale {0}")
	@ValueSource(strings = { "en", "es", "ca" })
	public void shouldCreateOptionForLocale(String lang) {

		final Locale locale = Locale.getDefault();
		try {

			final Locale newLocale = new Locale(lang);
			Locale.setDefault(newLocale);
			final Main main = new Main();
			final Options options = main.createOptions();
			assertThat(options.hasOption(Main.HELP_OPTION)).isTrue();
			assertThat(options.hasOption(Main.VERSION_OPTION)).isTrue();
			assertThat(options.hasOption(Main.CONF_DIR_OPTION)).isTrue();
			assertThat(options.hasOption(Main.PROPERTY_OPTION)).isTrue();

		} finally {

			Locale.setDefault(locale);
		}
	}

	/**
	 * Verify show help message.
	 *
	 * @param stream captured system output stream.
	 */
	@ExtendWith(SystemOutGuard.class)
	@Test
	public void shouldShowHelpMessage(final Capturable stream) {

		stream.capture();
		Main.main("-" + Main.HELP_OPTION);
		final String data = stream.getCapturedData();
		assertThat(data).contains("-" + Main.HELP_OPTION, "-" + Main.VERSION_OPTION, "-" + Main.CONF_DIR_OPTION,
				"-" + Main.PROPERTY_OPTION);

	}

	/**
	 * Verify show version.
	 *
	 * @param stream captured system err stream.
	 */
	@ExtendWith(SystemErrGuard.class)
	@Test
	public void shouldShowVersion(final Capturable stream) {

		stream.capture();
		Main.main("-" + Main.VERSION_OPTION);
		final String data = stream.getCapturedData();
		assertThat(data).contains(Level.INFO.name());

	}

	/**
	 * Verify undefined argument provokes an error.
	 *
	 * @param stream captured system err stream.
	 */
	@Test
	@ExtendWith(SystemErrGuard.class)
	public void shouldCaptureUndefinedArgument(final Capturable stream) {

		stream.capture();
		Main.main("-undefined");
		final String data = stream.getCapturedData();
		assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());

	}

	/**
	 * Verify error happens when the property parameter is wrong.
	 *
	 * @param stream captured system err stream.
	 */
	@Test
	@ExtendWith(SystemErrGuard.class)
	public void shouldCaptureBadPropertyArgument(final Capturable stream) {

		stream.capture();
		Main.main("-" + Main.PROPERTY_OPTION, "propertyName");
		final String data = stream.getCapturedData();
		assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());

	}

	/**
	 * Verify error happens when the configuration directory parameter is wrong.
	 *
	 * @param stream captured system err stream.
	 */
	@Test
	@ExtendWith(SystemErrGuard.class)
	public void shouldCaptureBadConfDirArgument(final Capturable stream) {

		stream.capture();
		Main.main("-" + Main.CONF_DIR_OPTION);
		final String data = stream.getCapturedData();
		assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());

	}

	/**
	 * Verify can not start server because the port is already binded.
	 *
	 * @param stream captured system err stream.
	 * @param tmpDir temporal directory.
	 *
	 *
	 * @throws Throwable if can not bind a port.
	 */
	@Test
	@ExtendWith(SystemErrGuard.class)
	public void shouldNotStartServerBecausePortIsBidded(final Capturable stream, @TempDir File tmpDir) throws Throwable {

		try (final Socket socket = new Socket()) {

			socket.bind(new InetSocketAddress("localhost", 0));
			final int port = socket.getLocalPort();

			final File confDir = new File(tmpDir, "etc");
			confDir.mkdirs();
			Files.writeString(new File(confDir, "host.json").toPath(),
					"{\"api\":{\"host\":\"localhost\",\"port\":" + port + "}}");

			stream.capture();
			final Thread thread = new Thread(() -> Main.main("-" + Main.CONF_DIR_OPTION, confDir.getAbsolutePath()));
			thread.start();

			String data = stream.getCapturedData();
			for (int i = 0; i < 1000 && !data.contains("Check the Logs to known why."); i++) {

				Thread.sleep(100);
				data = stream.getCapturedData();
			}
			assertThat(data).contains(Level.ERROR.name(), "Check the Logs to known why.");

		}
	}

	/**
	 * Verify can not start server because exist bad configuration files.
	 *
	 * @param stream captured system err stream.
	 * @param tmpDir temporal directory.
	 *
	 *
	 * @throws Throwable if can not create temporal files.
	 */
	@Test
	@ExtendWith(SystemErrGuard.class)
	public void shouldNotStartServerBecauseConfigurationFilesAreWrong(final Capturable stream, @TempDir File tmpDir)
			throws Throwable {

		final File confDir = new File(tmpDir, "etc");
		confDir.mkdirs();
		new File(confDir, "Z").mkdirs();
		final File unreadable = new File(confDir, "x.json");
		unreadable.createNewFile();
		unreadable.setReadable(false);
		Files.writeString(new File(confDir, "bad_yaml.yml").toPath(), "{\"api\":{\"port\":0}}");
		Files.writeString(new File(confDir, "bad_json.json").toPath(), "port:0");

		stream.capture();
		final Thread thread = new Thread(() -> Main.main("-" + Main.CONF_DIR_OPTION, confDir.getAbsolutePath()));
		thread.start();

		String data = stream.getCapturedData();
		for (int i = 0; i < 1000 && !data.contains("Check the Logs to known why."); i++) {

			Thread.sleep(100);
			data = stream.getCapturedData();
		}
		assertThat(data).contains(Level.ERROR.name(), "Check the Logs to known why.");
	}

	/**
	 * Verify capture exception when configure the configuration directory.
	 */
	@Test
	public void shouldCaptureExceptionWhenConfigureDirectory() {

		final Main main = new Main();
		final CommandLine cmd = main.parse("-" + Main.CONF_DIR_OPTION, "undefined://bad/path/to/conf/dir");
		main.retrieveOptions.getStores().clear();
		main.configure(cmd);
		assertThat(main.retrieveOptions.getStores()).isEmpty();
	}

	/**
	 * Verify can not start server because the port has a bad value.
	 *
	 * @param stream captured system err stream.
	 * @param tmpDir temporal directory.
	 *
	 *
	 * @throws Throwable if can not bind a port.
	 */
	@Test
	@ExtendWith(SystemErrGuard.class)
	public void shouldNotStartServerBecausePortIsBad(final Capturable stream, @TempDir File tmpDir) throws Throwable {

		stream.capture();
		final Thread thread = new Thread(() -> Main.main("-" + Main.PROPERTY_OPTION, "api.port=\"zero\""));
		thread.start();

		String data = stream.getCapturedData();
		for (int i = 0; i < 1000 && !data.contains("Check the Logs to known why."); i++) {

			Thread.sleep(100);
			data = stream.getCapturedData();
		}
		assertThat(data).contains(Level.ERROR.name(), "Check the Logs to known why.");

	}

}
