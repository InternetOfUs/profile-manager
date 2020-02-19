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

import eu.internetofus.wenet_profile_manager.ModelTestCase;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.ValidationsTest;

/**
 * Test the {@link Language}.
 *
 * @see Language
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class LanguageTest extends ModelTestCase<Language> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Language createModelExample(int index) {

		final Language name = new Language();
		name.code = "ca";
		name.name = "name_" + index;
		name.level = LanguageLevel.A0;
		return name;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @see Language#validate(String)
	 */
	@Test
	public void shouldExample1BeValid() {

		final Language model = this.createModelExample(1);
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();
	}

	/**
	 * Check that the name is not valid if has a large code.
	 *
	 * @see Language#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargeCode() {

		final Language model = new Language();
		model.code = "cat";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.code");
	}

	/**
	 * Check that the name is not valid if has a large code.
	 *
	 * @see Language#validate(String)
	 */
	@Test
	public void shouldBeValidACodeWithSpaces() {

		final Language model = new Language();
		model.code = "   en   ";
		model.validate("codePrefix");
		assertThat(model.code).isEqualTo("en");

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @see Language#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithALargeName() {

		final Language model = new Language();
		model.name = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.name");
	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @see Language#validate(String)
	 */
	@Test
	public void shouldBeValidANameWithSpaces() {

		final Language model = new Language();
		model.name = "   English   ";
		model.validate("codePrefix");
		assertThat(model.name).isEqualTo("English");

	}

	/**
	 * Check that merge two models.
	 *
	 * @see Language#merge(Language,String)
	 */
	@Test
	public void shouldMerge() {

		final Language target = this.createModelExample(1);
		final Language source = this.createModelExample(23);
		final Language merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isEqualTo(source);

	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @see Language#merge(Language,String)
	 */
	@Test
	public void shouldMergeWithNull() {

		final Language target = this.createModelExample(1);
		final Language merged = target.merge(null, "codePrefix");
		assertThat(merged).isSameAs(target);

	}

	/**
	 * Check that merge only the name.
	 *
	 * @see Language#merge(Language,String)
	 */
	@Test
	public void shouldMergeOnlyName() {

		final Language target = this.createModelExample(1);
		final Language source = new Language();
		source.name = "NEW VALUE";
		final Language merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.name = "NEW VALUE";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only the code.
	 *
	 * @see Language#merge(Language,String)
	 */
	@Test
	public void shouldMergeOnlyCode() {

		final Language target = this.createModelExample(1);
		final Language source = new Language();
		source.code = "en";
		final Language merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.code = "en";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only the level.
	 *
	 * @see Language#merge(Language,String)
	 */
	@Test
	public void shouldMergeOnlyLevel() {

		final Language target = this.createModelExample(1);
		final Language source = new Language();
		source.level = LanguageLevel.C1;
		final Language merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.level = LanguageLevel.C1;
		assertThat(merged).isEqualTo(target);
	}

}
