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

package eu.internetofus.common.api.models.wenet;

import static eu.internetofus.common.api.models.MergesTest.assertCanMerge;
import static eu.internetofus.common.api.models.MergesTest.assertCannotMerge;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationsTest;
import eu.internetofus.common.services.WeNetTaskManagerService;
import eu.internetofus.common.services.WeNetTaskManagerServiceOnMemory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TaskType}.
 *
 * @see TaskType
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskTypeTest extends ModelTestCase<TaskType> {

	/**
	 * Register the necessary services before to test.
	 *
	 * @param vertx event bus to register the necessary services.
	 */
	@BeforeEach
	public void registerServices(Vertx vertx) {

		WeNetTaskManagerServiceOnMemory.register(vertx);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskType createModelExample(int index) {

		final TaskType model = new TaskType();
		model.name = "name_" + index;
		model.description = "description_" + index;
		model.keywords = new ArrayList<>();
		model.keywords.add("keyword_" + index);
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(index));
		model.attributes = new ArrayList<>();
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(index));
		model.constants = new ArrayList<>();
		model.constants.add(new TaskAttributeTest().createModelExample(index));
		return model;

	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index       to verify
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final TaskType model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnExistingId(Vertx vertx, VertxTestContext testContext) {

		WeNetTaskManagerService.createProxy(vertx).createTaskType(new JsonObject(), testContext.succeeding(created -> {

			final TaskType model = new TaskType();
			model.id = created.getString("id");
			assertIsNotValid(model, "id", vertx, testContext);

		}));

	}

	/**
	 * Check that the model with an undefined identifier.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidWithAnNotExistingId(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.id = UUID.randomUUID().toString();
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = this.createModelExample(1);
		final TaskType source = this.createModelExample(23);
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = this.createModelExample(1);
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeName(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.name = ValidationsTest.STRING_256;
		assertIsNotValid(model, "name", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidANameWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.name = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.name).isEqualTo("1234567890"));

	}

	/**
	 * Check that the description is not valid if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.description = ValidationsTest.STRING_1024;
		assertIsNotValid(model, "description", vertx, testContext);

	}

	/**
	 * Check that the description is not valid if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidADescriptionWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.description = "   " + ValidationsTest.STRING_256 + "   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.description).isEqualTo(ValidationsTest.STRING_256));

	}

	/**
	 * Check that the keyword is not valid if has a large keyword.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeKeyword(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.keywords = new ArrayList<>();
		model.keywords.add("    ");
		model.keywords.add(ValidationsTest.STRING_256);
		assertIsNotValid(model, "keywords[1]", vertx, testContext);

	}

	/**
	 * Check that the keyword is not valid if has a large keyword.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidAKeywordWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.keywords = new ArrayList<>();
		model.keywords.add(null);
		model.keywords.add("   1234567890   ");
		model.keywords.add(null);
		model.keywords.add("     ");
		model.keywords.add("\n\t");
		assertIsValid(model, vertx, testContext,
				() -> assertThat(model.keywords).isNotEmpty().hasSize(1).contains("1234567890", atIndex(0)));

	}

	/**
	 * Check that not accept profiles with bad norms.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadNorms(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.norms = new ArrayList<>();
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.get(1).attribute = ValidationsTest.STRING_256;
		assertIsNotValid(model, "norms[1].attribute", vertx, testContext);

	}

	/**
	 * Check that not accept profiles with bad attributes.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadAttributes(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.attributes = new ArrayList<>();
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		model.attributes.add(new TaskAttributeType());
		assertIsNotValid(model, "attributes[2].name", vertx, testContext);

	}

	/**
	 * Check that not accept profiles with bad constants.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadConstants(Vertx vertx, VertxTestContext testContext) {

		final TaskType model = new TaskType();
		model.constants = new ArrayList<>();
		model.constants.add(new TaskAttributeTest().createModelExample(1));
		model.constants.add(new TaskAttribute());
		model.constants.add(new TaskAttributeTest().createModelExample(2));
		assertIsNotValid(model, "constants[1].name", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeName(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		final TaskType source = new TaskType();
		source.name = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "name", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeANameWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		final TaskType source = new TaskType();
		source.name = "   1234567890   ";
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.name).isEqualTo("1234567890"));

	}

	/**
	 * Check that the description is not merge if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		target.name = "name";
		final TaskType source = new TaskType();
		source.description = ValidationsTest.STRING_1024;
		assertCannotMerge(target, source, "description", vertx, testContext);

	}

	/**
	 * Check that the description is not merge if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeADescriptionWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		target.name = "name";
		final TaskType source = new TaskType();
		source.description = "   " + ValidationsTest.STRING_256 + "   ";
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.description).isEqualTo(ValidationsTest.STRING_256));

	}

	/**
	 * Check that the keyword is not merge if has a large keyword.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeKeyword(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		final TaskType source = new TaskType();
		source.keywords = new ArrayList<>();
		source.keywords.add(null);
		source.keywords.add("");
		source.keywords.add(ValidationsTest.STRING_256);
		assertCannotMerge(target, source, "keywords[2]", vertx, testContext);

	}

	/**
	 * Check that the keyword is not merge if has a large keyword.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeAKeywordWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		final TaskType source = new TaskType();
		source.keywords = new ArrayList<>();
		source.keywords.add("");
		source.keywords.add("   1234567890   ");
		source.keywords.add(null);
		source.keywords.add("     ");
		source.keywords.add("\n\t");

		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.keywords).isNotEmpty().hasSize(1).contains("1234567890", atIndex(0)));

	}

	/**
	 * Check that not accept profiles with bad norms.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadNorms(Vertx vertx, VertxTestContext testContext) {

		final TaskType source = new TaskType();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).attribute = ValidationsTest.STRING_256;
		assertCannotMerge(new TaskType(), source, "norms[1].attribute", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated social practice identifiers.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedNormIds(Vertx vertx, VertxTestContext testContext) {

		final TaskType source = new TaskType();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		source.norms.get(2).id = "1";
		final TaskType target = new TaskType();
		target.norms = new ArrayList<>();
		target.norms.add(new Norm());
		target.norms.get(0).id = "1";
		assertCannotMerge(target, source, "norms[2].id", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with not defined social practice id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecNormId(Vertx vertx, VertxTestContext testContext) {

		final TaskType source = new TaskType();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		assertCannotMerge(new TaskType(), source, "norms[1].id", vertx, testContext);

	}

	/**
	 * Check merge social practices profiles.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNorms(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		target.norms = new ArrayList<>();
		target.norms.add(new Norm());
		target.norms.get(0).id = "1";
		final TaskType source = new TaskType();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged.norms).isNotEqualTo(target.norms).isEqualTo(source.norms);
			assertThat(merged.norms.get(0).id).isNotEmpty();
			assertThat(merged.norms.get(1).id).isEqualTo("1");
			assertThat(merged.norms.get(2).id).isNotEmpty();

		});

	}

	/**
	 * Check that not accept model with bad attributes.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadAttributes(Vertx vertx, VertxTestContext testContext) {

		final TaskType source = new TaskType();
		source.attributes = new ArrayList<>();
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		source.attributes.add(new TaskAttributeType());
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		assertCannotMerge(new TaskType(), source, "attributes[1].name", vertx, testContext);

	}

	/**
	 * Check merge attributes.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeWithAttributes(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		target.attributes = new ArrayList<>();
		target.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		final TaskType source = new TaskType();
		source.attributes = new ArrayList<>();
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		source.attributes.add(new TaskAttributeType());
		source.attributes.get(1).name = target.attributes.get(0).name;
		source.attributes.get(1).description = "NEW DESCRIPTION";
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(3));
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged.attributes).isNotEqualTo(target.attributes).isNotEqualTo(source.attributes).hasSize(3)
					.contains(source.attributes.get(0), atIndex(0)).contains(source.attributes.get(2), atIndex(2));
			target.attributes.get(0).description = "NEW DESCRIPTION";
			assertThat(merged.attributes.get(1)).isEqualTo(target.attributes.get(0));

		});

	}

	/**
	 * Check that not accept model with bad constants.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadConstants(Vertx vertx, VertxTestContext testContext) {

		final TaskType source = new TaskType();
		source.constants = new ArrayList<>();
		source.constants.add(new TaskAttributeTest().createModelExample(1));
		source.constants.add(new TaskAttribute());
		source.constants.add(new TaskAttributeTest().createModelExample(2));
		assertCannotMerge(new TaskType(), source, "constants[1].name", vertx, testContext);

	}

	/**
	 * Check merge constants.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskType#merge(TaskType, String, Vertx)
	 */
	@Test
	public void shouldMergeWithConstants(Vertx vertx, VertxTestContext testContext) {

		final TaskType target = new TaskType();
		target.constants = new ArrayList<>();
		target.constants.add(new TaskAttributeTest().createModelExample(1));
		final TaskType source = new TaskType();
		source.constants = new ArrayList<>();
		source.constants.add(new TaskAttributeTest().createModelExample(2));
		source.constants.add(new TaskAttribute());
		source.constants.get(1).name = target.constants.get(0).name;
		source.constants.get(1).value = "NEW VALUE";
		source.constants.add(new TaskAttributeTest().createModelExample(3));
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged.constants).isNotEqualTo(target.constants).isEqualTo(source.constants);

		});

	}

}