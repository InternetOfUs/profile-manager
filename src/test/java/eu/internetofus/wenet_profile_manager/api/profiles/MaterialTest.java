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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.ValidationErrorException;

/**
 * Test the {@link Material}.
 *
 * @see Material
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MaterialTest {

	/**
	 * Check that merge two models.
	 *
	 * @see Material#merge(Material, String)
	 */
	@Test
	public void shouldMerge() {

		final Material target = new CarTest().createModelExample(1);
		target.id = "1";
		final Material source = new CarTest().createModelExample(2);
		final Material merged = target.merge(source, "codePrefix");
		assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
		source.id = "1";
		assertThat(merged).isEqualTo(source);
	}

	/**
	 * Check that merge with {@code null} source.
	 *
	 * @see Material#merge(Material, String)
	 */
	@Test
	public void shouldMergeWithNull() {

		final Material target = new Material();
		final Material merged = target.merge(null, "codePrefix");
		assertThat(merged).isSameAs(target);

	}

	/**
	 * Check that merge with two different classes.
	 *
	 * @see Material#merge(Material, String)
	 */
	@Test
	public void shouldNotMergeWithDiferentClasses() {

		final Material target = new Material();
		final Material source = new Car();
		final Material merged = target.merge(source, "codePrefix");
		assertThat(merged).isSameAs(source);
	}

	/**
	 * Check that not merge with bad material class.
	 *
	 * @see Material#merge(Material, String)
	 */
	@Test
	public void shouldNotMergeWithABadCarPlate() {

		final Material target = new Material();
		final Material source = new Material();
		assertThat(assertThrows(ValidationErrorException.class, () -> target.merge(source, "codePrefix")).getCode())
				.isEqualTo("codePrefix");
	}

}
