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

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.internetofus.wenet_profile_manager.ModelTestCase;

/**
 * Test the {@link PlannedActivity}.
 *
 * @see PlannedActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PlannedActivityTest extends ModelTestCase<PlannedActivity> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlannedActivity createModelExample(int index) {

		final PlannedActivity activity = new PlannedActivity();
		activity.id = null;
		activity.startTime = "startTime_" + index;
		activity.endTime = "endTime_" + index;
		activity.description = "description_" + index;
		activity.attendees = new ArrayList<>();
		activity.status = PlannedActivityStatus.cancelled;
		return activity;
	}

	/**
	 * Check the copy of a model has to be equals to the original.
	 */
	@Test
	public void shouldCopyBeEqual() {

		final PlannedActivity model1 = this.createModelExample(1);
		final PlannedActivity model2 = new PlannedActivity(model1);
		assertThat(model1).isEqualTo(model2);

	}

	// /**
	// * Check that the {@link #createModelExample(int)} is valid.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldExample1BeValid() {
	//
	// final PlannedActivity model = this.createModelExample(1);
	// assertThat(catchThrowable(() ->
	// model.validate("codePrefix"))).doesNotThrowAnyException();
	// }
	//
	// /**
	// * Check that the {@link #createModelExample2()} is valid.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldExample2BeValid() {
	//
	// final PlannedActivity model = this.createModelExample2();
	// assertThat(catchThrowable(() ->
	// model.validate("codePrefix"))).doesNotThrowAnyException();
	// }
	//
	// /**
	// * Check that a model with all the values is valid.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldFullModelBeValid() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.id = " ";
	// model.startTime = " start time ";
	// model.endTime = " end time ";
	// model.description = " description ";
	// model.attendees = new ArrayList<>();
	// model.attendees.add("");
	// model.attendees.add(null);
	// model.attendees.add(" ");
	// model.status = PlannedActivityStatus.tentative;
	// assertThat(catchThrowable(() ->
	// model.validate("codePrefix"))).doesNotThrowAnyException();
	//
	// final PlannedActivity expected = new PlannedActivity();
	// expected.id = model.id;
	// expected.startTime = "start time";
	// expected.endTime = "end time";
	// expected.description = "description";
	// expected.attendees = new ArrayList<>();
	// expected.status = PlannedActivityStatus.tentative;
	// assertThat(model).isEqualTo(expected);
	// }
	//
	// /**
	// * Check that the model with id is not valid.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithAnId() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.id = "has_id";
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.id");
	// }
	//
	// /**
	// * Check that not accept planned activity with bad start time.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadStartTime() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.startTime = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.startTime");
	// }
	//
	// /**
	// * Check that not accept planned activity with bad end time.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadEndTime() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.endTime = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.endTime");
	// }
	//
	// /**
	// * Check that not accept planned activity with bad description.
	// *
	// * @see PlannedActivity#validate(String)
	// */
	// @Test
	// public void shouldNotBeValidWithABadDescription() {
	//
	// final PlannedActivity model = new PlannedActivity();
	// model.description = ValidationsTest.STRING_256;
	// assertThat(assertThrows(ValidationErrorException.class, () ->
	// model.validate("codePrefix")).getCode())
	// .isEqualTo("codePrefix.description");
	// }

}
