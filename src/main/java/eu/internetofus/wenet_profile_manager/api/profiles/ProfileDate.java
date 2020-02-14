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

import java.time.LocalDate;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.Validable;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a date.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "date", description = "The information of a date.")
public class ProfileDate extends Model implements Validable {

	/**
	 * The year of the date.
	 */
	@Schema(description = "The year of the date.", example = "1976")
	public int year;

	/**
	 * The year of the date.
	 */
	@Schema(description = "The month of the date,from 1 to 12 (1: Jan and 12: Dec).", example = "4")
	public byte month;

	/**
	 * The day of the date.
	 */
	@Schema(description = "The day of the date.", example = "23")
	public byte day;

	/**
	 * Create an empty date.
	 */
	public ProfileDate() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		if (this.month < 1 || this.month > 12) {

			throw new ValidationErrorException(codePrefix + ".month", "The month has to be on the range [1,12]");

		} else if (this.day < 1 || this.day > 31) {

			throw new ValidationErrorException(codePrefix + ".day", "The day has to be on the range [1,31]");

		} else {

			try {

				java.time.LocalDate.of(this.year, this.month, this.day);

			} catch (final Throwable exception) {

				throw new ValidationErrorException(codePrefix, exception);
			}
		}

	}

	/**
	 * Check if the date is a right birth date.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 *
	 * @throws ValidationErrorException If the model is not valid.
	 */
	public void validateAsBirthDate(String codePrefix) throws ValidationErrorException {

		this.validate(codePrefix);
		final LocalDate birthDate = LocalDate.of(this.year, this.month, this.day);
		if (birthDate.isAfter(LocalDate.now())) {

			throw new ValidationErrorException(codePrefix, "The birth date can not be on the future");
		}
		if (birthDate.isBefore(LocalDate.of(1903, 1, 2))) {

			throw new ValidationErrorException(codePrefix,
					"The user can not be born before Kane Tanake, the oldest living person on earth");
		}

	}
}
