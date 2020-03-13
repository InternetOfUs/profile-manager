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

import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.ValidationsTest;

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
	public void shouldNotBeValidWithABadDrivingLicenseId() {

		final DrivingLicense model = new DrivingLicense();
		model.drivingLicenseId = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.drivingLicenseId");
	}

	/**
	 * Check that not merge with bad driving license id.
	 *
	 * @see DrivingLicense#merge(DrivingLicense, String)
	 */
	@Test
	public void shouldNotMergeWithABadDrivingLicenseId() {

		final DrivingLicense target = this.createModelExample(1);
		final DrivingLicense source = new DrivingLicense();
		source.drivingLicenseId = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.drivingLicenseId");
	}

	/**
	 * Check that merge two models.
	 *
	 * @see DrivingLicense#merge(DrivingLicense, String)
	 */
	@Test
	public void shouldMerge() {

		final DrivingLicense target = this.createModelExample(1);
		target.id = "1";
		final DrivingLicense source = this.createModelExample(2);
		final DrivingLicense merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		source.id = "1";
		assertThat(merged).isEqualTo(source);
	}

	/**
	 * Check that merge with {@code null} source.
	 *
	 * @see DrivingLicense#merge(DrivingLicense, String)
	 */
	@Test
	public void shouldMergeWithNull() {

		final DrivingLicense target = this.createModelExample(1);
		final DrivingLicense merged = target.merge(null, "codePrefix");
		assertThat(merged).isSameAs(target);
	}

	/**
	 * Check that merge only driving license id.
	 *
	 * @see DrivingLicense#merge(DrivingLicense, String)
	 */
	@Test
	public void shouldMergeOnlyDrivingLicenseId() {

		final DrivingLicense target = this.createModelExample(1);
		target.id = "1";
		final DrivingLicense source = new DrivingLicense();
		source.drivingLicenseId = "NEW DRIVINGLICENSE TYPE";
		final DrivingLicense merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.drivingLicenseId = "NEW DRIVINGLICENSE TYPE";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge only id.
	 *
	 * @see DrivingLicense#merge(DrivingLicense, String)
	 */
	@Test
	public void shouldMergeOnlyId() {

		final DrivingLicense target = this.createModelExample(1);
		target.id = "1";
		final DrivingLicense source = new DrivingLicense();
		final DrivingLicense merged = target.merge(source, "codePrefix");
		assertThat(merged).isEqualTo(target).isNotSameAs(target).isNotEqualTo(source);
	}

}
