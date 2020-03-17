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

package eu.internetofus.common.api.models.wenet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.ValidationsTest;
import eu.internetofus.common.api.models.wenet.Car;
import eu.internetofus.common.api.models.wenet.DrivingLicense;
import eu.internetofus.common.api.models.wenet.Norm;
import eu.internetofus.common.api.models.wenet.SocialPractice;

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

	/**
	 * Check that not merge social practices with bad label.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotMergeWithABadLabel() {

		final SocialPractice target = this.createModelExample(1);
		final SocialPractice source = new SocialPractice();
		source.label = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.label");
	}

	/**
	 * Check that not merge model with bad materials.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotMergeWithABadMaterials() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.materials = new CarTest().createModelExample(1);
		((Car) source.materials).carType = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.materials.carType");
	}

	/**
	 * Check that not merge model with bad competences.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotMergeWithABadCompetences() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicenseTest().createModelExample(1);
		((DrivingLicense) source.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.competences.drivingLicenseId");
	}

	/**
	 * Check that not merge model with bad norms.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldNotMergeWithABadNorms() {

		final SocialPractice target = this.createModelExample(1);
		final SocialPractice source = new SocialPractice();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).attribute = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.norms[1].attribute");
	}

	/**
	 * Check that merge.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMerge() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = this.createModelExample(2);
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		source.id = target.id;
		source.competences.id = merged.competences.id;
		source.materials.id = merged.materials.id;
		source.norms.get(0).id = merged.norms.get(0).id;
		assertThat(merged).isEqualTo(source);
	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeWithNull() {

		final SocialPractice target = this.createModelExample(1);
		final SocialPractice merged = target.merge(null, "codePrefix");
		assertThat(merged).isSameAs(target);
	}

	/**
	 * Check that merge only label.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeOnlyLabel() {

		final SocialPractice target = this.createModelExample(1);
		target.id = "1";
		target.norms.get(0).id = "2";
		final SocialPractice source = new SocialPractice();
		source.label = "NEW LABEL";
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.label = "NEW LABEL";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge and add a new competence.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeNewCompetence() {

		final SocialPractice target = this.createModelExample(1);
		target.id = "1";
		target.competences.id = "2";
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicenseTest().createModelExample(2);
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.competences = new DrivingLicenseTest().createModelExample(2);
		target.competences.id = merged.competences.id;
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that fail merge a new competence.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldFailMergeNewCompetence() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicense();
		source.competences.id = "3";
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.competences.id");

	}

	/**
	 * Check that merge existing competence.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeExistingCompetence() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicense();
		source.competences.id = target.competences.id;
		((DrivingLicense) source.competences).drivingLicenseId = "New drivingLicense";
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		((DrivingLicense) target.competences).drivingLicenseId = "New drivingLicense";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that fail merge and existing competence.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldFailMergeExistingCompetence() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicense();
		source.competences.id = target.competences.id;
		((DrivingLicense) source.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.competences.drivingLicenseId");
	}

	/**
	 * Check that merge and add a new material.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeNewMaterial() {

		final SocialPractice target = this.createModelExample(1);
		target.id = "1";
		target.materials.id = "2";
		final SocialPractice source = new SocialPractice();
		source.materials = new CarTest().createModelExample(2);
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.materials = new CarTest().createModelExample(2);
		target.materials.id = merged.materials.id;
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that fail merge a new material.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldFailMergeNewMaterial() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.materials = new Car();
		source.materials.id = "3";
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.materials.id");

	}

	/**
	 * Check that merge existing material.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeExistingMaterial() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.materials = new Car();
		source.materials.id = target.materials.id;
		((Car) source.materials).carPlate = "New car plate";
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		((Car) target.materials).carPlate = "New car plate";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that fail merge and existing material.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldFailMergeExistingMaterial() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.materials = new Car();
		source.materials.id = target.materials.id;
		((Car) source.materials).carPlate = ValidationsTest.STRING_256;
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix.materials.carPlate");
	}

	/**
	 * Check that merge removing all norms.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeRemoveNorms() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.norms = new ArrayList<>();
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.norms = new ArrayList<>();
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge modify a norm.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeModifyANorm() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.get(0).id = target.norms.get(0).id;
		source.norms.get(0).attribute = "New attribute";
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.norms.get(0).attribute = "New attribute";
		assertThat(merged).isEqualTo(target);
	}

	/**
	 * Check that merge modify a norm and add another.
	 *
	 * @see SocialPractice#validate(String)
	 */
	@Test
	public void shouldMergeModifyANormAddOther() {

		final SocialPractice target = this.createModelExample(1);
		target.validate("codePrefix");
		final SocialPractice source = new SocialPractice();
		source.norms = new ArrayList<>();
		source.norms.add(new NormTest().createModelExample(3));
		source.norms.add(new Norm());
		source.norms.get(1).id = target.norms.get(0).id;
		source.norms.get(1).attribute = "New attribute";
		final SocialPractice merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		target.norms.add(0, new NormTest().createModelExample(3));
		target.norms.get(0).id = merged.norms.get(0).id;
		target.norms.get(1).attribute = "New attribute";
		assertThat(merged).isEqualTo(target);
	}

}
