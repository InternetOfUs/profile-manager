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
 * Test the {@link Routine}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RoutineTest extends ModelTestCase<Routine> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Routine createModelExample(int index) {

		final Routine routine = new Routine();
		routine.id = null;
		routine.label = "label_" + index;
		routine.proximity = "proximity_" + index;
		routine.from_time = "18:00:0" + index % 10;
		routine.to_time = "22:00:0" + index % 10;
		return routine;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @see Routine#validate(String)
	 */
	@Test
	public void shouldExampleBeValid() {

		final Routine model = this.createModelExample(1);
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see Routine#validate(String)
	 */
	@Test
	public void shouldFullModelBeValid() {

		final Routine model = new Routine();
		model.id = "      ";
		model.label = "    label    ";
		model.proximity = "    proximity   ";
		model.from_time = "   18:19    ";
		model.to_time = "  22:21:20   ";
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final Routine expected = new Routine();
		expected.id = model.id;
		expected.label = "label";
		expected.proximity = "proximity";
		expected.from_time = "18:19:00";
		expected.to_time = "22:21:20";
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see Routine#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithAnId() {

		final Routine model = new Routine();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

	/**
	 * Check that not accept routines with bad label.
	 *
	 * @see Routine#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLabel() {

		final Routine model = new Routine();
		model.label = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.label");
	}

	/**
	 * Check that not accept routines with bad proximity.
	 *
	 * @see Routine#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadProximity() {

		final Routine model = new Routine();
		model.proximity = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.proximity");
	}

	/**
	 * Check that not accept routines with bad from_time.
	 *
	 * @param badTime a bad time value.
	 *
	 * @see Routine#validate(String)
	 */
	@ParameterizedTest(name = "Should not be valid with from_time = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:0", "2019-02-02T00:00:00Z" })
	public void shouldNotBeValidWithABadFrom_time(String badTime) {

		final Routine model = new Routine();
		model.from_time = badTime;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.from_time");
	}

	/**
	 * Check that not accept routines with bad to_time.
	 *
	 * @param badTime a bad time value.
	 *
	 * @see Routine#validate(String)
	 */
	@ParameterizedTest(name = "Should not be valid with to_time = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:0", "2019-02-02T00:00:00Z" })
	public void shouldNotBeValidWithABadTo_time(String badTime) {

		final Routine model = new Routine();
		model.to_time = badTime;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.to_time");
	}

}
