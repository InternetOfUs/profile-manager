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
 * Test the {@link RelevantLocation}.
 *
 * @see RelevantLocation
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RelevantLocationTest extends ModelTestCase<RelevantLocation> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RelevantLocation createModelExample(int index) {

		final RelevantLocation location = new RelevantLocation();
		location.id = null;
		location.label = "label_" + index;
		location.latitude = (-1 - index) % 180;
		location.longitude = (1 + index) % 90;
		return location;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldExampleBeValid() {

		final RelevantLocation model = this.createModelExample(1);
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldFullModelBeValid() {

		final RelevantLocation model = new RelevantLocation();
		model.id = "      ";
		model.label = "    label    ";
		model.longitude = 10;
		model.latitude = -10;
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final RelevantLocation expected = new RelevantLocation();
		expected.id = model.id;
		expected.label = "label";
		expected.longitude = 10;
		expected.latitude = -10;
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithAnId() {

		final RelevantLocation model = new RelevantLocation();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

	/**
	 * Check that not accept model with bad label.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLabel() {

		final RelevantLocation model = new RelevantLocation();
		model.label = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.label");
	}

	/**
	 * Check that not accept model with bad longitude.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLongitudeLessThanMinimum() {

		final RelevantLocation model = new RelevantLocation();
		model.longitude = -180.0001;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.longitude");
	}

	/**
	 * Check that not accept model with bad longitude.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLongitudeMoreThanMaximum() {

		final RelevantLocation model = new RelevantLocation();
		model.longitude = 180.0001;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.longitude");
	}

	/**
	 * Check that not accept model with bad latitude.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLatitudeLessThanMinimum() {

		final RelevantLocation model = new RelevantLocation();
		model.latitude = -90.0001;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.latitude");
	}

	/**
	 * Check that not accept model with bad latitude.
	 *
	 * @see RelevantLocation#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLatitudeMoreThanMaximum() {

		final RelevantLocation model = new RelevantLocation();
		model.latitude = 90.0001;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.latitude");
	}

}
