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

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.ModelTestCase;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.ValidationsTest;

/**
 * Test the {@link SocialPractice}
 *
 * @see SocialPractice
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class SocialPracticeTest extends ModelTestCase<SocialPractice> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocialPractice createModelExample(int index) {

		final SocialPractice model = new SocialPractice();
		model.id = null;
		model.label = "label_" + index;
		model.materials = new CarTest().createModelExample(index);
		model.competences = new DrivingLicenseTest().createModelExample(index);
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(index));
		return model;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldExampleBeValid() {

		final SocialPractice model = this.createModelExample(1);
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldFullModelBeValid() {

		final SocialPractice model = new SocialPractice();
		model.id = "      ";
		model.label = "    label    ";
		model.competences = new DrivingLicenseTest().createModelExample(1);
		model.materials = new CarTest().createModelExample(1);
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(1));
		assertThat(catchThrowable(() -> model.validate("codePrefix"))).doesNotThrowAnyException();

		final SocialPractice expected = new SocialPractice();
		expected.id = model.id;
		expected.label = "label";
		expected.competences = new DrivingLicenseTest().createModelExample(1);
		expected.competences.id = model.competences.id;
		expected.materials = new CarTest().createModelExample(1);
		expected.materials.id = model.materials.id;
		expected.norms = new ArrayList<>();
		expected.norms.add(new NormTest().createModelExample(1));
		expected.norms.get(0).id = model.norms.get(0).id;
		assertThat(model).isEqualTo(expected);
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithAnId() {

		final SocialPractice model = new SocialPractice();
		model.id = "has_id";
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.id");
	}

	/**
	 * Check that not accept social practices with bad label.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadLabel() {

		final SocialPractice model = new SocialPractice();
		model.label = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.label");
	}

	/**
	 * Check that not accept model with bad materials.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadMaterials() {

		final SocialPractice model = new SocialPractice();
		model.materials = new CarTest().createModelExample(1);
		((Car) model.materials).carType = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.materials.carType");
	}

	/**
	 * Check that not accept model with bad competences.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadCompetences() {

		final SocialPractice model = new SocialPractice();
		model.competences = new DrivingLicenseTest().createModelExample(1);
		((DrivingLicense) model.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.competences.drivingLicenseId");
	}

	/**
	 * Check that not accept model with bad norms.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotBeValidWithABadNorms() {

		final SocialPractice model = new SocialPractice();
		model.norms = new ArrayList<>();
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.get(1).attribute = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> model.validate("codePrefix")).getCode())
				.isEqualTo("codePrefix.norms[1].attribute");
	}

	/**
	 * Check that can not be decoded with a generic material.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotDecodeWithAGenericMaterial() {

		assertThat(Model.fromString("{\"materials\":{}}", SocialPractice.class)).isNull();

	}

	/**
	 * Check that can not be decoded with a generic material.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotDecodeWithAGenericCompetence() {

		assertThat(Model.fromString("{\"competences\":{}}", SocialPractice.class)).isNull();

	}

}
