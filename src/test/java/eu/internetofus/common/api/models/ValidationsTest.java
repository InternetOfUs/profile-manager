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

package eu.internetofus.common.api.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Validations}.
 *
 * @see Validations
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ValidationsTest {

	/**
	 * A string with 256 characters.
	 */
	public static final String STRING_256 = "0Ncu2eQI7boSct2Ga6VHViEPJn0HqffPajWKyL3TmgUyLG4ZjVLbaZSx7DZXuY0EAWGqWnWOB35Uql92cV2zTbBrSi4gVR0y9jJ3a5zsHnnXNFucmHRyplXw0v98l7BD4d8jvKro7QBIuZM4A4fARUol9gSrRAIoZ7PpUxtbNfteFkVhxfRUhGAkHKfRUsMulmgui5bRQaCM8ivevTJm8N4jXXUlgfkPepeMsQeQPzktJRnZDR3PxDrKLtKjoE24";

	/**
	 * Assert that a model is not valid.
	 *
	 * @param model       to validate.
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 */
	public static void assertIsNotValid(Validable model, Vertx vertx, VertxTestContext testContext) {

		assertIsNotValid(model, null, vertx, testContext);

	}

	/**
	 * Assert that a model is not valid because a field is wrong.
	 *
	 * @param model       to validate.
	 * @param fieldName   name of the field that is not valid.
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 */
	public static void assertIsNotValid(Validable model, String fieldName, Vertx vertx, VertxTestContext testContext) {

		model.validate("codePrefix", vertx).onComplete(testContext.failing(error -> testContext.verify(() -> {

			assertThat(error).isInstanceOf(ValidationErrorException.class);
			String expectedCode = "codePrefix";
			if (fieldName != null) {

				expectedCode += "." + fieldName;

			}
			assertThat(((ValidationErrorException) error).getCode()).isEqualTo(expectedCode);

			testContext.completeNow();

		})));
	}

	/**
	 * Assert that a model is valid.
	 *
	 * @param model       to validate.
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 * @param <T>         model to test.
	 */
	public static <T extends Validable> void assertIsValid(T model, Vertx vertx, VertxTestContext testContext) {

		assertIsValid(model, vertx, testContext, null);

	}

	/**
	 * Assert that a model is valid.
	 *
	 * @param model       to validate.
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 * @param expected    function to check the validation result.
	 * @param <T>         model to test.
	 */
	public static <T extends Validable> void assertIsValid(T model, Vertx vertx, VertxTestContext testContext,
			Runnable expected) {

		model.validate("codePrefix", vertx).onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

			if (expected != null) {

				expected.run();
			}

			testContext.completeNow();

		})));

	}

	/**
	 * Check that a field can be null.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	public void shouldNullStringFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, null)).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	public void shouldEmptyStringFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "")).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	public void shouldWhiteStringFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "       "))
				.isEqualTo(null)).doesNotThrowAnyException();
	}

	/**
	 * Check that the value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	public void shouldStringWithWhiteFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "   a b c    "))
						.isEqualTo("a b c")).doesNotThrowAnyException();
	}

	/**
	 * Check that the value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	public void shouldNotBeValidIfStringIsTooLarge() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableStringField("codePrefix", "fieldName", 255, ValidationsTest.STRING_256))
						.getCode()).isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that a field can be null.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	public void shouldNullEmailFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", null)).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	public void shouldEmptyEmailFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "")).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	public void shouldWhiteEmailFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "       ")).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that the email value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	public void shouldEmailWithWhiteFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "   a@b.com    "))
				.isEqualTo("a@b.com")).doesNotThrowAnyException();
	}

	/**
	 * Check that the email value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	public void shouldNotBeValidIfEmailIsTooLarge() {

		assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableEmailField("codePrefix",
				"fieldName", ValidationsTest.STRING_256.substring(0, 250) + "@b.com")).getCode())
						.isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that the email value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	public void shouldNotBeValidABadEmailValue() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableEmailField("codePrefix", "fieldName", "bad email(at)host.com")).getCode())
						.isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that a field can be null.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	public void shouldNullLocaleFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", null)).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	public void shouldEmptyLocaleFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "")).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	public void shouldWhiteLocaleFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "       ")).isEqualTo(null))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that the locale value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	public void shouldLocaleWithWhiteFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "   en_US    "))
				.isEqualTo("en_US")).doesNotThrowAnyException();
	}

	/**
	 * Check that the locale value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	public void shouldNotBeValidIfLocaleIsTooLarge() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableLocaleField("codePrefix", "fieldName", "to_la_ge_")).getCode())
						.isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that the locale value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	public void shouldNotBeValidABadLocaleValue() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableLocaleField("codePrefix", "fieldName", "de-Gr")).getCode())
						.isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that a field can be null.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	public void shouldNullTelephoneFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, null))
				.isEqualTo(null)).doesNotThrowAnyException();
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	public void shouldEmptyTelephoneFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, ""))
				.isEqualTo(null)).doesNotThrowAnyException();
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	public void shouldWhiteTelephoneFieldBeValid() {

		assertThatCode(
				() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "       "))
						.isEqualTo(null)).doesNotThrowAnyException();
	}

	/**
	 * Check that the telephone value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	public void shouldTelephoneWithWhiteFieldBeValid() {

		assertThatCode(() -> assertThat(
				Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "   +34987654321    "))
						.isEqualTo("+34 987 65 43 21")).doesNotThrowAnyException();
	}

	/**
	 * Check that the telephone value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	public void shouldNotBeValidIfTelephoneIsTooLarge() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "+349876543211")).getCode())
						.isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that the telephone value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	public void shouldNotBeValidABadTelephoneValue() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "bad telephone number"))
						.getCode()).isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableStringDateField(String, String,
	 *      DateTimeFormatter,String)
	 */
	@Test
	public void shouldEmptyDateFieldBeValid() {

		assertThatCode(() -> assertThat(
				Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, ""))
						.isEqualTo(null)).doesNotThrowAnyException();
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableStringDateField(String, String,
	 *      DateTimeFormatter,String)
	 */
	@Test
	public void shouldWhiteDateFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableStringDateField("codePrefix", "fieldName",
				DateTimeFormatter.ISO_INSTANT, "       ")).isEqualTo(null)).doesNotThrowAnyException();
	}

	/**
	 * Check that the date value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableStringDateField(String, String,
	 *      DateTimeFormatter,String)
	 */
	@Test
	public void shouldDateWithWhiteFieldBeValid() {

		assertThatCode(() -> assertThat(Validations.validateNullableStringDateField("codePrefix", "fieldName",
				DateTimeFormatter.ISO_INSTANT, "   2011-12-03t10:15:30z    ")).isEqualTo("2011-12-03T10:15:30Z"))
						.doesNotThrowAnyException();
	}

	/**
	 * Check that the date value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableStringDateField(String, String,
	 *      DateTimeFormatter,String)
	 */
	@Test
	public void shouldNotBeValidABadDateValue() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableStringDateField("codePrefix", "fieldName", null, "bad date")).getCode())
						.isEqualTo("codePrefix.fieldName");
	}

	/**
	 * Check that the date value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableStringDateField(String, String,
	 *      DateTimeFormatter,String)
	 */
	@Test
	public void shouldNotBeValidABadIsoinstanceValue() {

		assertThat(
				assertThrows(ValidationErrorException.class, () -> Validations.validateNullableStringDateField("codePrefix",
						"fieldName", DateTimeFormatter.ISO_INSTANT, "bad date")).getCode()).isEqualTo("codePrefix.fieldName");
	}

}
