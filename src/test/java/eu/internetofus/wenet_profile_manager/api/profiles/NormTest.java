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
 * Test the {@link Norm}
 *
 * @see Norm
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class NormTest extends ModelTestCase<Norm> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Norm createModelExample(int index) {

		final Norm norm = new Norm();
		norm.id = null;
		norm.attribute = "attribute_" + index;
		norm.operator = NormOperator.EQUALS;
		norm.comparison = "comparison_" + index;
		norm.negation = true;
		return norm;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @see Norm#validate(String)
	 */
	@Test
	public void shouldExample1BeValid() {

		final Norm model = this.createModelExample(1);
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see Norm#validate(String)
	 */
	@Test
	public void shouldFullModelBeValid() {

		final Norm model = new Norm();
		model.id = "      ";
		model.attribute = "    attribute    ";
		model.operator = NormOperator.GREATER_THAN;
		model.comparison = "   comparison    ";
		model.negation = false;
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final Norm expected = new Norm();
		expected.id = model.id;
		expected.attribute = "attribute";
		expected.operator = NormOperator.GREATER_THAN;
		expected.comparison = "comparison";
		expected.negation = false;
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see Norm#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithAnId() {

		final Norm model = new Norm();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

	/**
	 * Check that not accept norms with bad attribute.
	 *
	 * @see Norm#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadAttribute() {

		final Norm model = new Norm();
		model.attribute = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.attribute");
	}

	/**
	 * Check that not accept norms with bad comparison.
	 *
	 * @see Norm#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadComparison() {

		final Norm model = new Norm();
		model.comparison = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.comparison");
	}

}
