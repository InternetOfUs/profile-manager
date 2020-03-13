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

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.Validable;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A material necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A material necessary for do a social practice.")
@JsonDeserialize(using = MaterialDeserialize.class)
public class Material extends Model implements Validable {

	/**
	 * The identifier of the material.
	 */
	@Schema(description = "The identifier of the material", example = "aisufh9sdokjnd")
	public String id;

	/**
	 * Create a new empty material.
	 */
	public Material() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
		if (this.id != null) {

			throw new ValidationErrorException(codePrefix + ".id",
					"You can not specify the identifier of the material to create");

		} else {

			this.id = UUID.randomUUID().toString();
		}

	}

	/**
	 * Merge this model with another.
	 *
	 * @param source     to merge.
	 * @param codePrefix the prefix of the code to use for the error message.
	 *
	 * @return the merged model.
	 *
	 * @throws ValidationErrorException if the merged model is not right.
	 */
	public Material merge(Material source, String codePrefix) throws ValidationErrorException {

		if (source != null) {

			final Class<? extends Material> sourceClass = source.getClass();
			if (sourceClass != this.getClass()) {

				source.validate(codePrefix);
				return source;

			} else if (this instanceof Car) {

				return ((Car) this).merge((Car) source, codePrefix);

			} else {

				throw new ValidationErrorException(codePrefix, "Unknown how to merge '" + sourceClass + "'.");
			}

		} else {

			return this;
		}
	}

}
