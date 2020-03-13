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

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationErrorException;

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
		date.month = (byte) (1 + index % 11);
		date.day = (byte) (1 + index % 27);
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
	public void shouldNotBeValidAnImposibleDate() {

		final ProfileDate date = new ProfileDate();
		date.year = 2020;
		date.month = 2;
		date.day = 31;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix");

	}

	/**
	 * Should not be valid to born in the future.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldNotBeValidToBornOnTheFuture() {

		final ProfileDate date = new ProfileDate();
		final LocalDate tomorrow = LocalDate.now().plusDays(1);
		date.year = tomorrow.getYear();
		date.month = (byte) tomorrow.getMonthValue();
		date.day = (byte) tomorrow.getDayOfMonth();
		assertThat(assertThrows(ValidationErrorException.class, () -> date.validateAsBirthDate("codePrefix")).getCode())
				.isEqualTo("codePrefix");

	}

	/**
	 * Should not be valid to born before the oldest person on the world.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldNotBeValidToBornBeforeTheOldestPersonOnTheWorld() {

		final ProfileDate date = new ProfileDate();
		date.year = 1903;
		date.month = 1;
		date.day = 1;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.validateAsBirthDate("codePrefix")).getCode())
				.isEqualTo("codePrefix");

	}

	/**
	 * Should not merge with a bad month.
	 *
	 * @param month that is not valid.
	 */
	@ParameterizedTest(name = "Should not merge a date with the value {0} as month.")
	@ValueSource(bytes = { 0, 13, -1, 100 })
	public void shouldNotMergeMonth(byte month) {

		final ProfileDate date = new ProfileDate();
		final ProfileDate source = new ProfileDate();
		source.year = 2020;
		source.month = month;
		source.day = 2;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.month");

	}

	/**
	 * Should not merge with a bad day.
	 *
	 * @param day that is not valid.
	 */
	@ParameterizedTest(name = "Should not merge a date with the value {0} as day.")
	@ValueSource(bytes = { 0, 32, -1, 100 })
	public void shouldNotMergeDay(byte day) {

		final ProfileDate date = new ProfileDate();
		final ProfileDate source = new ProfileDate();
		source.year = 2020;
		source.month = 4;
		source.day = day;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.day");

	}

	/**
	 * Should not merge with a bad day.
	 */
	@Test
	public void shouldNotMergeAnImposibleDate() {

		final ProfileDate date = new ProfileDate();
		final ProfileDate source = new ProfileDate();
		source.year = 2020;
		source.month = 2;
		source.day = 31;
		assertThat(assertThrows(ValidationErrorException.class, () -> date.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix");

	}

	/**
	 * Should not merge to born in the future.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldNotMergeToBornOnTheFuture() {

		final ProfileDate date = new ProfileDate();
		final ProfileDate source = new ProfileDate();
		final LocalDate tomorrow = LocalDate.now().plusDays(1);
		source.year = tomorrow.getYear();
		source.month = (byte) tomorrow.getMonthValue();
		source.day = (byte) tomorrow.getDayOfMonth();
		assertThat(
				assertThrows(ValidationErrorException.class, () -> date.mergeAsBirthDate(source, "codePrefix")).getCode())
						.isEqualTo("codePrefix");

	}

	/**
	 * Should not merge to born before the oldest person on the world.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldNotMergeToBornBeforeTheOldestPersonOnTheWorld() {

		final ProfileDate date = new ProfileDate();
		final ProfileDate source = new ProfileDate();
		source.year = 1903;
		source.month = 1;
		source.day = 1;
		assertThat(
				assertThrows(ValidationErrorException.class, () -> date.mergeAsBirthDate(source, "codePrefix")).getCode())
						.isEqualTo("codePrefix");

	}

	/**
	 * Should only merge the year.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldMergeOnlyYear() {

		final ProfileDate target = this.createModelExample(1);
		final ProfileDate source = new ProfileDate();
		source.year = target.year - 1;
		final ProfileDate merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target);
		target.year--;
		assertThat(merged).isEqualTo(target);

	}

	/**
	 * Should only merge the month.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldMergeOnlyMonth() {

		final ProfileDate target = this.createModelExample(1);
		final ProfileDate source = new ProfileDate();
		source.month = (byte) (target.month - 1);
		final ProfileDate merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target);
		target.month--;
		assertThat(merged).isEqualTo(target);

	}

	/**
	 * Should only merge the day.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldMergeOnlyDay() {

		final ProfileDate target = this.createModelExample(1);
		final ProfileDate source = new ProfileDate();
		source.day = (byte) (target.day - 1);
		final ProfileDate merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target);
		target.day--;
		assertThat(merged).isEqualTo(target);

	}

	/**
	 * Should only merge the year as birth date.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldMergeOnlyYearAsBirthDate() {

		final ProfileDate target = this.createModelExample(1);
		final ProfileDate source = new ProfileDate();
		source.year = target.year - 1;
		final ProfileDate merged = target.mergeAsBirthDate(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target);
		target.year--;
		assertThat(merged).isEqualTo(target);

	}

	/**
	 * Should only merge the month as birth date.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldMergeOnlyMonthAsBirthDate() {

		final ProfileDate target = this.createModelExample(1);
		final ProfileDate source = new ProfileDate();
		source.month = (byte) (target.month - 1);
		final ProfileDate merged = target.mergeAsBirthDate(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target);
		target.month--;
		assertThat(merged).isEqualTo(target);

	}

	/**
	 * Should only merge the day as birth date.
	 *
	 * @see ProfileDate#validateAsBirthDate(String)
	 */
	@Test
	public void shouldMergeOnlyDayAsBirthDate() {

		final ProfileDate target = this.createModelExample(1);
		final ProfileDate source = new ProfileDate();
		source.day = (byte) (target.day - 1);
		final ProfileDate merged = target.mergeAsBirthDate(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target);
		target.day--;
		assertThat(merged).isEqualTo(target);

	}

}
