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

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.Validable;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A location of interest for the user, may be the home or work location. This
 * information is generated by the platform AI.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(
		description = "A location of interest for the user - may be the home or work location -. This information is generated by the platform AI.")
public class RelevantLocation extends Model implements Validable {

	/**
	 * The identifier of the relevant location.
	 */
	@Schema(description = "The identifier of the location", example = "kdjfghd8hikdfg")
	public String id;

	/**
	 * The descriptor of the location.
	 */
	@Schema(description = "The descriptor of the location", example = "Home")
	public String label;

	/**
	 * The latitude of the location.
	 */
	@Schema(description = "The latitude of the location", example = "40.388756")
	public double latitude;

	/**
	 * The longitude of the location.
	 */
	@Schema(description = "The longitude of the location", example = "-3.588622")
	public double longitude;

	/**
	 * Create an empty relation.
	 */
	public RelevantLocation() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
		if (this.id != null) {

			throw new ValidationErrorException(codePrefix + ".id",
					"You can not specify the identifier of the relevant location to create");

		} else {

			this.id = UUID.randomUUID().toString();
		}
		this.label = Validations.validateNullableStringField(codePrefix, "label", 255, this.label);
		if (this.latitude < -90 || this.latitude > 90) {

			throw new ValidationErrorException(codePrefix + ".latitude", "The latitude has to be on the range [-90,90]");
		}

		if (this.longitude < -180 || this.longitude > 180) {

			throw new ValidationErrorException(codePrefix + ".longitude", "The longitude has to be on the range [-180,180]");
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
	 * @throws ValidationErrorException if the model is not right.
	 */
	public RelevantLocation merge(RelevantLocation source, String codePrefix) throws ValidationErrorException {

		if (source != null) {

			final RelevantLocation merged = new RelevantLocation();
			merged.label = source.label;
			if (merged.label == null) {

				merged.label = this.label;
			}

			merged.latitude = source.latitude;
			merged.longitude = source.longitude;

			merged.validate(codePrefix);
			merged.id = this.id;
			return merged;

		} else {

			return this;
		}
	}

}
