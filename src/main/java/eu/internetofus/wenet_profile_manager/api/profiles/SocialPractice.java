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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.Validable;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A social practice of an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A social practice of an user.")
public class SocialPractice extends Model implements Validable {

	/**
	 * The identifier of the social practice.
	 */
	@Schema(description = "The identifier of the social practice", example = "f9dofgljdksdf")
	public String id;

	/**
	 * The descriptor of the social practice.
	 */
	@Schema(description = "The descriptor of the social practice", example = "commuter")
	public String label;

	/**
	 * The materials necessaries for the social practice.
	 */
	@Schema(description = "The materials necessaries for the social practice", anyOf = { Car.class })
	public Material materials;

	/**
	 * The competences necessaries for the social practice.
	 */
	@Schema(description = "The competences necessaries for the social practice", anyOf = { DrivingLicense.class })
	public Competence competences;

	/**
	 * The norms of the social practice.
	 */
	@ArraySchema(
			schema = @Schema(implementation = Norm.class),
			arraySchema = @Schema(description = "The norms of the social practice"))
	public List<Norm> norms = new ArrayList<Norm>();

	/**
	 * Create an empty practice.
	 */
	public SocialPractice() {

	}

	/**
	 * Create a practice with the values of another.
	 *
	 * @param practice to copy.
	 */
	public SocialPractice(SocialPractice practice) {

		this.id = practice.id;
		this.label = practice.label;
		this.materials = Material.copyOf(practice.materials);
		this.competences = Competence.copyOf(practice.competences);
		for (final Norm practiceNorm : practice.norms) {

			final Norm norm = new Norm(practiceNorm);
			this.norms.add(norm);

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
		if (this.id != null) {

			throw new ValidationErrorException(codePrefix + ".id",
					"You can not specify the identifier of the social practice to create");

		} else {

			this.id = UUID.randomUUID().toString();
		}
		this.label = Validations.validateNullableStringField(codePrefix, "label", 255, this.label);

		if (this.competences != null) {

			this.competences.validate(codePrefix + ".competences");
		}

		if (this.materials != null) {

			this.materials.validate(codePrefix + ".materials");
		}
		if (this.norms != null && !this.norms.isEmpty()) {

			final String codeNorms = codePrefix + ".norms";
			for (int index = 0; index < this.norms.size(); index++) {

				final Norm norm = this.norms.get(index);
				norm.validate(codeNorms + "[" + index + "]");
			}

		}

	}

}
