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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.wenet_profile_manager.ModelTestCase;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.ValidationsTest;

/**
 * Test the {@link UserName}.
 *
 * @see UserName
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UserNameTest extends ModelTestCase<UserName> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserName createModelExample(int index) {

		final UserName name = new UserName();
		name.prefix = "prefix_" + index;
		name.first = "firts_" + index;
		name.middle = "middle_" + index;
		name.last = "last_" + index;
		name.suffix = "suffix_" + index;
		return name;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index to verify
	 *
	 * @see UserName#validate(String)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index) {

		final UserName model = this.createModelExample(index);
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();
	}

	/**
	 * Check that the name is not valid if has a large prefix.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargePrefix() {

		final UserName model = new UserName();
		model.prefix = "12345678901";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.prefix");
	}

	/**
	 * Check that the name is not valid if has a large prefix.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldBeValidAPrefixWithSpaces() {

		final UserName model = new UserName();
		model.prefix = "   1234567890   ";
		model.validate("codePrefix");
		assertThat(model.prefix).isEqualTo("1234567890");

	}

	/**
	 * Check that the name is not valid if has a large first.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargeFirst() {

		final UserName model = new UserName();
		model.first = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.first");
	}

	/**
	 * Check that the name is not valid if has a large first.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldBeValidAFirstWithSpaces() {

		final UserName model = new UserName();
		model.first = "   First name 1234567890   ";
		model.validate("codePrefix");
		assertThat(model.first).isEqualTo("First name 1234567890");

	}

	/**
	 * Check that the name is not valid if has a large middle.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargeMiddle() {

		final UserName model = new UserName();
		model.middle = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.middle");
	}

	/**
	 * Check that the name is not valid if has a large middle.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldBeValidAMiddleWithSpaces() {

		final UserName model = new UserName();
		model.middle = "   Middle name 1234567890   ";
		model.validate("codePrefix");
		assertThat(model.middle).isEqualTo("Middle name 1234567890");

	}

	/**
	 * Check that the name is not valid if has a large last.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargeLast() {

		final UserName model = new UserName();
		model.last = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.last");
	}

	/**
	 * Check that the name is not valid if has a large last.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldBeValidALastWithSpaces() {

		final UserName model = new UserName();
		model.last = "   Last name 1234567890   ";
		model.validate("codePrefix");
		assertThat(model.last).isEqualTo("Last name 1234567890");

	}

	/**
	 * Check that the name is not valid if has a large suffix.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargeSuffix() {

		final UserName model = new UserName();
		model.suffix = "12345678901";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.suffix");
	}

	/**
	 * Check that the name is not valid if has a large suffix.
	 *
	 * @see UserName#validate(String)
	 */
	@Test
	public void shouldBeValidASuffixWithSpaces() {

		final UserName model = new UserName();
		model.suffix = "   1234567890   ";
		model.validate("codePrefix");
		assertThat(model.suffix).isEqualTo("1234567890");

	}

}
