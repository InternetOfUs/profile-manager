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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.wenet_profile_manager.ModelTestCase;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;

/**
 * Test the {@link ProfileDate}.
 *
 * @see ProfileDate
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ProfileDateTest extends ModelTestCase<ProfileDate> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileDate createModelExample(int index) {

		final ProfileDate date = new ProfileDate();
		date.year = 1950 + index;
		date.month = (byte) ((1 + index) % 12);
		date.day = (byte) ((1 + index) % 28);
		return date;
	}

	/**
	 * Should not be valid with a bad month.
	 *
	 * @param month that is not valid.
	 */
	@ParameterizedTest(name = "Should not be valid a date with the value {0} as month.")
	@ValueSource(bytes = { 0, 13, -1, 100 })
	public void shouldNotBeValidMonth(byte month) {

		final ProfileDate date = new ProfileDate();
		date.year = 2020;
		date.month = month;
		date.day = 2;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.month");

	}

	/**
	 * Should not be valid with a bad day.
	 *
	 * @param day that is not valid.
	 */
	@ParameterizedTest(name = "Should not be valid a date with the value {0} as day.")
	@ValueSource(bytes = { 0, 32, -1, 100 })
	public void shouldNotBeValidDay(byte day) {

		final ProfileDate date = new ProfileDate();
		date.year = 2020;
		date.month = 4;
		date.day = day;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.day");

	}

	/**
	 * Should not be valid with a bad day.
	 */
	@Test
	public void shouldNotBeValidAImposibleDate() {

		final ProfileDate date = new ProfileDate();
		date.year = 2020;
		date.month = 2;
		date.day = 31;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix");

	}

}
