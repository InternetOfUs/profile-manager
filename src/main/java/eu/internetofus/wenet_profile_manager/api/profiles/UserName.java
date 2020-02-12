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

import eu.internetofus.wenet_profile_manager.Model;
import eu.internetofus.wenet_profile_manager.Validable;
import eu.internetofus.wenet_profile_manager.ValidationErrorException;
import eu.internetofus.wenet_profile_manager.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The information of an user name.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The information of an user name.")
public class UserName extends Model implements Validable {

	/**
	 * The user name prefix.
	 */
	@Schema(description = "The prefix of the name such as Mr., Mrs., Ms., Miss, or Dr.", example = "Dr.")
	public String prefix;

	/**
	 * The user first name.
	 */
	@Schema(
			description = "The first name (also known as a given name, forename or	 Christian name) is a part of a person's personal name.",
			example = "Abbey")
	public String first;

	/**
	 * The user middle name.
	 */
	@Schema(
			description = "The portion of a personal name that is written between the person's first name (given) and their last names (surname).",
			example = "Fitzgerald")
	public String middle;

	/**
	 * The user last name.
	 */
	@Schema(
			description = "The last name (surname or family name) is the portion (in some cultures) of a personal name that indicates a person's family (or tribe or community, depending on the culture).",
			example = "Smith")
	public String last;

	/**
	 * The user name suffix.
	 */
	@Schema(
			description = "The suffix of the name such as Jr., Sr., I, II, III, IV, V, MD, DDS, PhD or DVM.",
			example = "Jr.")
	public String suffix;

	/**
	 * Create a new user name.
	 */
	public UserName() {

	}

	/**
	 * Create a user name with the value of another.
	 *
	 * @param name to copy.
	 */
	public UserName(UserName name) {

		this.prefix = name.prefix;
		this.first = name.first;
		this.middle = name.middle;
		this.last = name.last;
		this.suffix = name.suffix;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(String codePrefix) throws ValidationErrorException {

		this.prefix = Validations.validateNullableStringField(codePrefix, "prefix", 10, this.prefix);
		this.first = Validations.validateNullableStringField(codePrefix, "first", 255, this.first);
		this.middle = Validations.validateNullableStringField(codePrefix, "middle", 255, this.middle);
		this.last = Validations.validateNullableStringField(codePrefix, "last", 255, this.last);
		this.suffix = Validations.validateNullableStringField(codePrefix, "suffix", 10, this.suffix);

	}

}
