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
 * Test the {@link Car}.
 *
 * @see Car
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CarTest extends MaterialTestCase<Car> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Car createModelExample(int index) {

		final Car car = new Car();
		car.id = null;
		car.carPlate = "car_plate_" + index;
		car.carType = "car_type_" + index;
		return car;
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see Car#validate(String)
	 */
	@Test
	public void shouldFullModelBeValid() {

		final Car model = new Car();
		model.id = "      ";
		model.carType = "    car type    ";
		model.carPlate = "    car plate    ";
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final Car expected = new Car();
		expected.id = model.id;
		expected.carType = "car type";
		expected.carPlate = "car plate";
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see Car#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithAnId() {

		final Car model = new Car();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

	/**
	 * Check that not accept cars with bad car type.
	 *
	 * @see Car#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadCarType() {

		final Car model = new Car();
		model.carType = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.carType");
	}

	/**
	 * Check that not accept cars with bad car plate.
	 *
	 * @see Car#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadCarPlate() {

		final Car model = new Car();
		model.carPlate = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.carPlate");
	}

}
