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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
	 * Check that a field can be null.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	@Tag("unit")
	public void shouldNullStringFieldBeValid() {

		assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, null)).isEqualTo(null);
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	@Tag("unit")
	public void shouldEmptyStringFieldBeValid() {

		assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "")).isEqualTo(null);
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	@Tag("unit")
	public void shouldWhiteStringFieldBeValid() {

		assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "       ")).isEqualTo(null);
	}

	/**
	 * Check that the value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	@Tag("unit")
	public void shouldStringWithWhiteFieldBeValid() {

		assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "   a b c    "))
				.isEqualTo("a b c");
	}

	/**
	 * Check that the value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableStringField(String, String, int,String)
	 */
	@Test
	@Tag("unit")
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
	@Tag("unit")
	public void shouldNullEmailFieldBeValid() {

		assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", null)).isEqualTo(null);
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	@Tag("unit")
	public void shouldEmptyEmailFieldBeValid() {

		assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "")).isEqualTo(null);
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	@Tag("unit")
	public void shouldWhiteEmailFieldBeValid() {

		assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "       ")).isEqualTo(null);
	}

	/**
	 * Check that the email value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	@Tag("unit")
	public void shouldEmailWithWhiteFieldBeValid() {

		assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "   a@b.com    "))
				.isEqualTo("a@b.com");
	}

	/**
	 * Check that the email value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableEmailField(String, String, String)
	 */
	@Test
	@Tag("unit")
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
	@Tag("unit")
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
	@Tag("unit")
	public void shouldNullLocaleFieldBeValid() {

		assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", null)).isEqualTo(null);
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	@Tag("unit")
	public void shouldEmptyLocaleFieldBeValid() {

		assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "")).isEqualTo(null);
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	@Tag("unit")
	public void shouldWhiteLocaleFieldBeValid() {

		assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "       ")).isEqualTo(null);
	}

	/**
	 * Check that the locale value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	@Tag("unit")
	public void shouldLocaleWithWhiteFieldBeValid() {

		assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "   en_US    ")).isEqualTo("en_US");
	}

	/**
	 * Check that the locale value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableLocaleField(String, String, String)
	 */
	@Test
	@Tag("unit")
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
	@Tag("unit")
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
	@Tag("unit")
	public void shouldNullTelephoneFieldBeValid() {

		assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, null)).isEqualTo(null);
	}

	/**
	 * Check that an empty is right but is changed to null.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	@Tag("unit")
	public void shouldEmptyTelephoneFieldBeValid() {

		assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "")).isEqualTo(null);
	}

	/**
	 * Check that an white value is right but is changed to null.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	@Tag("unit")
	public void shouldWhiteTelephoneFieldBeValid() {

		assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "       ")).isEqualTo(null);
	}

	/**
	 * Check that the telephone value is trimmed to be valid.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	@Tag("unit")
	public void shouldTelephoneWithWhiteFieldBeValid() {

		assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "   +34987654321    "))
				.isEqualTo("+34 987 65 43 21");
	}

	/**
	 * Check that the telephone value of the field is not valid if it is too large.
	 *
	 * @see Validations#validateNullableTelephoneField(String, String,
	 *      String,String)
	 */
	@Test
	@Tag("unit")
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
	@Tag("unit")
	public void shouldNotBeValidABadTelephoneValue() {

		assertThat(assertThrows(ValidationErrorException.class,
				() -> Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "bad telephone number"))
						.getCode()).isEqualTo("codePrefix.fieldName");
	}
}
