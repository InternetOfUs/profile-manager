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

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.Validable;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A competence necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A competence necessary for do a social practice.")
@JsonDeserialize(using = CompetenceDeserialize.class)
public class Competence extends Model implements Validable {

	/**
	 * The identifier of the competence.
	 */
	@Schema(description = "The identifier of the competence", example = "aisufh9sdokjnd")
	public String id;

	/**
	 * Create a new empty competence.
	 */
	public Competence() {

	}

	/**
	 * Create a competence with the values of another.
	 *
	 * @param competence to copy.
	 */
	public Competence(Competence competence) {

		this.id = competence.id;

	}

	/**
	 * Create a copy of a competence.
	 *
	 * @param competence to copy.
	 *
	 * @return the copy of the competence
	 */
	public static Competence copyOf(Competence competence) {

		if (competence instanceof DrivingLicense) {

			return new DrivingLicense((DrivingLicense) competence);

		} else if (competence != null) {

			return new Competence(competence);

		} else {

			return null;
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
					"You can not specify the identifier of the competence to create");

		} else {

			this.id = UUID.randomUUID().toString();
		}

	}

}