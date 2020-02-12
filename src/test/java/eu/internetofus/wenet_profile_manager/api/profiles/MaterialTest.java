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

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link Material}.
 *
 * @see Material
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MaterialTest extends MaterialTestCase<Material> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Material createModelExample(int index) {

		final Material material = new Material();
		material.id = null;
		return material;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shouldNotBeEquals() {

		final Material model1 = new Material();
		model1.id = UUID.randomUUID().toString();
		final Material model2 = new Material();
		model2.id = UUID.randomUUID().toString();
		assertThat(model1).isNotEqualTo(model2);
		assertThat(model1.hashCode()).isNotEqualTo(model2.hashCode());
		assertThat(model1.toString()).isNotEqualTo(model2.toString());

	}

	/**
	 * Should copy a {@link Car}.
	 */
	@Test
	public void shouldCopyACar() {

		final Car car = new CarTest().createModelExample(1);
		assertThat(Material.copyOf(car)).isEqualTo(car).isNotSameAs(car);

	}

	/**
	 * Should not copy {@code null} value.
	 */
	@Test
	public void shouldNotCopyNull() {

		assertThat(Material.copyOf(null)).isNull();

	}

	/**
	 * Should copy a material.
	 */
	@Test
	public void shouldCopyMaterial() {

		final Material material = this.createModelExample(1);
		assertThat(Material.copyOf(material)).isEqualTo(material).isNotSameAs(material);

	}

	/**
	 * Check the copy of a model has to be equals to the original.
	 */
	@Test
	public void shouldCopyBeEqual() {

		final Material model1 = this.createModelExample(1);
		final Material model2 = new Material(model1);
		assertThat(model1).isEqualTo(model2);

	}

}
