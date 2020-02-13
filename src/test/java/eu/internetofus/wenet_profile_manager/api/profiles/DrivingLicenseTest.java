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

import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.ValidationsTest;

/**
 * Test the {@link DrivingLicense}.
 *
 * @see DrivingLicense
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DrivingLicenseTest extends CompetenceTestCase<DrivingLicense> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DrivingLicense createModelExample(int index) {

		final DrivingLicense drivinglicense = new DrivingLicense();
		drivinglicense.id = null;
		drivinglicense.drivingLicenseId = "driving_license_id_" + index;
		return drivinglicense;
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see DrivingLicense#validate(String)
	 */
	@Test
	public void shouldFullModelBeValid() {

		final DrivingLicense model = new DrivingLicense();
		model.id = "      ";
		model.drivingLicenseId = "    driving license id    ";
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final DrivingLicense expected = new DrivingLicense();
		expected.id = model.id;
		expected.drivingLicenseId = "driving license id";
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see DrivingLicense#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithAnId() {

		final DrivingLicense model = new DrivingLicense();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

	/**
	 * Check that not accept driving licenses with bad driving license id.
	 *
	 * @see DrivingLicense#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadDrivingLicenseType() {

		final DrivingLicense model = new DrivingLicense();
		model.drivingLicenseId = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.drivingLicenseId");
	}
}
