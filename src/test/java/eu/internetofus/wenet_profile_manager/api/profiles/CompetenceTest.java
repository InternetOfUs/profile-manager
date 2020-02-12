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

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.ValidationErrorException;

/**
 * Test the {@link Competence}.
 *
 * @see Competence
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CompetenceTest extends CompetenceTestCase<Competence> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shouldNotBeEquals() {

		final Competence model1 = new Competence();
		model1.id = UUID.randomUUID().toString();
		final Competence model2 = new Competence();
		model2.id = UUID.randomUUID().toString();
		assertThat(model1).isNotEqualTo(model2);
		assertThat(model1.hashCode()).isNotEqualTo(model2.hashCode());
		assertThat(model1.toString()).isNotEqualTo(model2.toString());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Competence createModelExample(int index) {

		final Competence competence = new Competence();
		competence.id = null;
		return competence;
	}

	/**
	 * Should copy a {@link DrivingLicense}.
	 */
	@Test
	@Tag("unit")
	public void shouldCopyADrivingLicense() {

		final DrivingLicense drivinglicense = new DrivingLicenseTest().createModelExample(1);
		assertThat(Competence.copyOf(drivinglicense)).isEqualTo(drivinglicense).isNotSameAs(drivinglicense);

	}

	/**
	 * Should not copy {@code null} value.
	 */
	@Test
	public void shouldNotCopyNull() {

		assertThat(Competence.copyOf(null)).isNull();

	}

	/**
	 * Should copy a competence.
	 */
	@Test
	@Tag("unit")
	public void shouldCopyCompetence() {

		final Competence competence = this.createModelExample(1);
		assertThat(Competence.copyOf(competence)).isEqualTo(competence).isNotSameAs(competence);

	}

	/**
	 * Check the copy of a model has to be equals to the original.
	 */
	@Test
	@Tag("unit")
	public void shouldCopyBeEqual() {

		final Competence model1 = this.createModelExample(1);
		final Competence model2 = new Competence(model1);
		assertThat(model1).isEqualTo(model2);

	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see Competence#validate(String)
	 */
	@Test
	@Tag("unit")
	public void shouldFullModelBeValid() {

		final Competence model = new Competence();
		model.id = "      ";
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final Competence expected = new Competence();
		expected.id = model.id;
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see Competence#validate(String)
	 */
	@Test
	@Tag("unit")
	public void shouldNotBeValidWithAnId() {

		final Competence model = new Competence();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

}
