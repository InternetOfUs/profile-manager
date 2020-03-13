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

import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.ValidationsTest;

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

	/**
	 * Check that the name is not merge if has a large prefix.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldNotMergeWithALargePrefix() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.prefix = "12345678901";
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.prefix");
	}

	/**
	 * Check that the name is not merge if has a large prefix.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeAPrefixWithSpaces() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.prefix = "   1234567890   ";
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged.prefix).isEqualTo("1234567890");

	}

	/**
	 * Check that the name is not merge if has a large first.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldNotMergeWithALargeFirst() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.first = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.first");
	}

	/**
	 * Check that the name is not merge if has a large first.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeAFirstWithSpaces() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.first = "   First name 1234567890   ";
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged.first).isEqualTo("First name 1234567890");

	}

	/**
	 * Check that the name is not merge if has a large middle.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldNotMergeWithALargeMiddle() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.middle = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.middle");
	}

	/**
	 * Check that the name is not merge if has a large middle.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeAMiddleWithSpaces() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.middle = "   Middle name 1234567890   ";
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged.middle).isEqualTo("Middle name 1234567890");

	}

	/**
	 * Check that the name is not merge if has a large last.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldNotMergeWithALargeLast() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.last = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.last");
	}

	/**
	 * Check that the name is not merge if has a large last.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeALastWithSpaces() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.last = "   Last name 1234567890   ";
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged.last).isEqualTo("Last name 1234567890");

	}

	/**
	 * Check that the name is not merge if has a large suffix.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldNotMergeWithALargeSuffix() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.suffix = "12345678901";
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.suffix");
	}

	/**
	 * Check that the name is not merge if has a large suffix.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeASuffixWithSpaces() {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.suffix = "   1234567890   ";
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged.suffix).isEqualTo("1234567890");

	}

	/**
	 * Check that merge two models.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMerge() {

		final UserName target = this.createModelExample(1);
		final UserName source = this.createModelExample(23);
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isEqualTo(source);

	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeWithNull() {

		final UserName target = this.createModelExample(1);
		final UserName merged = target.merge(null, "codePrefix");
		assertThat(merged).isSameAs(target);

	}

	/**
	 * Check that merge only the prefix.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeOnlyPrefix() {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.prefix = "NEW VALUE";
		final UserName merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.prefix = "NEW VALUE";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only the first.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeOnlyFirst() {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.first = "NEW VALUE";
		final UserName merged = target.merge(source, "codeFirst");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.first = "NEW VALUE";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only the middle.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeOnlyMiddle() {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.middle = "NEW VALUE";
		final UserName merged = target.merge(source, "codeMiddle");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.middle = "NEW VALUE";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only the last.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeOnlyLast() {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.last = "NEW VALUE";
		final UserName merged = target.merge(source, "codeLast");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.last = "NEW VALUE";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only the suffix.
	 *
	 * @see UserName#merge(UserName,String)
	 */
	@Test
	public void shouldMergeOnlySuffix() {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.suffix = "NEW VALUE";
		final UserName merged = target.merge(source, "codeSuffix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.suffix = "NEW VALUE";
		assertThat(merged).isEqualTo(target);
	}

}
