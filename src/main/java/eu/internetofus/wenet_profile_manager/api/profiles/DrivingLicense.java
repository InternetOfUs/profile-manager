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

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A competence to drive a vehicle.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A competence to drive a vehicle.")
@JsonDeserialize(using = JsonDeserializer.None.class)
public class DrivingLicense extends Competence {

	/**
	 * The identifier of the driving license.
	 */
	@Schema(description = "The driving license if", example = "ESdfg09dofgk")
	public String drivingLicenseId;

	/**
	 * Create an empty driving license.
	 */
	public DrivingLicense() {

	}

	/**
	 * Create a driving license with the value of another.
	 *
	 * @param drivinglicense to copy.
	 */
	public DrivingLicense(DrivingLicense drivinglicense) {

		super(drivinglicense);
		this.drivingLicenseId = drivinglicense.drivingLicenseId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		super.validate(codePrefix);
		this.drivingLicenseId = Validations.validateNullableStringField(codePrefix, "drivingLicenseId", 255,
				this.drivingLicenseId);

	}

}