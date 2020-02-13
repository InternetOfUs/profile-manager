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

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.Validable;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * An activity that an user do regularly.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(
		ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/5c0512480f89ae267d6fc0dcf42db0f3a50d01e8/sources/wenet-models.yaml#/components/schemas/Routine",
		description = "An activity that an user do regularly.")
public class Routine extends Model implements Validable {

	/**
	 * The identifier of the activity.
	 */
	@Schema(description = "The identifier of the routine", example = "oishd0godlkgj")
	public String id;

	/**
	 * The identifier of the activity.
	 */
	@Schema(description = "The label of the routine", example = "work")
	public String label;

	/**
	 * The identifier of the routine.
	 */
	@Schema(description = "The identifier of the routine", example = "oishd0godlkgj")
	public String proximity;

	/**
	 * The time when the routine starts.
	 */
	@Schema(description = "The time when the routine starts", example = "18:00")
	public String from_time;

	/**
	 * The time when the routine ends.
	 */
	@Schema(description = "The time when the routine ends", example = "22:00")
	public String to_time;

	/**
	 * Create an empty routine.
	 */
	public Routine() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
		if (this.id != null) {

			throw new ValidationErrorException(codePrefix + ".id",
					"You can not specify the identifier of the norm to create");

		} else {

			this.id = UUID.randomUUID().toString();
		}
		this.label = Validations.validateNullableStringField(codePrefix, "label", 255, this.label);
		this.proximity = Validations.validateNullableStringField(codePrefix, "proximity", 255, this.proximity);
		this.from_time = Validations.validateNullableDateField(codePrefix, "from_time", DateTimeFormatter.ISO_TIME,
				this.from_time);
		this.to_time = Validations.validateNullableDateField(codePrefix, "to_time", DateTimeFormatter.ISO_TIME,
				this.to_time);

	}
}
