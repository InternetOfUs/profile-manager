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

import java.util.List;

import eu.internetofus.common.api.models.Mergeable;
import eu.internetofus.common.api.models.Merges;
import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.Validable;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Describe a type of task that can be done by the users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskType", description = "Describe a type of task that can be done by the users.")
public class TaskType extends Model implements Validable, Mergeable<TaskType> {

	/**
	 * The identifier of the profile.
	 */
	@Schema(description = "The unique identifier of the task type.", example = "4a559aafceb8464")
	public String id;

	/**
	 * A name that identify the type.
	 */
	@Schema(description = "A name that identify the type.", example = "Eat together task")
	public String name;

	/**
	 * A name that identify the type.
	 */
	@Schema(
			description = "A human readable description of the task objective.",
			example = "A task for organizing social dinners")
	public String description;

	/**
	 * A name that identify the type.
	 */
	@ArraySchema(
			schema = @Schema(implementation = String.class),
			arraySchema = @Schema(
					description = "The keywords that describe the task type",
					example = "[\"social interaction\",\"eat\"]"))
	public List<String> keywords;

	/**
	 * The individual norms of the user
	 */
	@ArraySchema(
			schema = @Schema(implementation = Norm.class),
			arraySchema = @Schema(
					description = "The norms that describe the interaction of the users to do the tasks of this type."))
	public List<Norm> norms;

	/**
	 * The individual norms of the user
	 */
	@ArraySchema(
			schema = @Schema(implementation = TaskAttributeType.class),
			arraySchema = @Schema(
					description = "The attribute that has to be instantiated when create the task of this type."))
	public List<TaskAttributeType> attributes;

	/**
	 * The individual norms of the user
	 */
	@ArraySchema(
			schema = @Schema(implementation = TaskAttribute.class),
			arraySchema = @Schema(description = "The attribute with a fixed value."))
	public List<TaskAttribute> constants;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		final Promise<Void> promise = Promise.promise();
		Future<Void> future = promise.future();
		try {

			this.name = Validations.validateNullableStringField(codePrefix, "name", 255, this.name);
			this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
			this.keywords = Validations.validateNullableListStringField(codePrefix, "keywords", 255, this.keywords);
			future = future.compose(Validations.validate(this.norms, codePrefix + ".norms", vertx));
			future = future.compose(Validations.validate(this.attributes, codePrefix + ".attributes", vertx));
			future = future.compose(Validations.validate(this.constants, codePrefix + ".constants", vertx));
			promise.complete();

		} catch (final ValidationErrorException validationError) {

			promise.fail(validationError);
		}

		return future;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<TaskType> merge(TaskType source, String codePrefix, Vertx vertx) {

		final Promise<TaskType> promise = Promise.promise();
		Future<TaskType> future = promise.future();
		if (source != null) {

			final TaskType merged = new TaskType();
			merged.name = source.name;
			if (merged.name == null) {

				merged.name = this.name;
			}
			merged.description = source.description;
			if (merged.description == null) {

				merged.description = this.description;
			}

			merged.keywords = source.keywords;
			if (merged.keywords == null) {

				merged.keywords = this.keywords;
			}

			future = future.compose(Merges.validateMerged(codePrefix, vertx));
			future = future
					.compose(Merges.mergeNorms(this.norms, source.norms, codePrefix + ".norms", vertx, (model, mergedNorms) -> {
						model.norms = mergedNorms;
					}));
			future = future.compose(Merges.mergeTaskAttributeTypes(this.attributes, source.attributes,
					codePrefix + ".attributes", vertx, (model, mergedAttributes) -> {
						model.attributes = mergedAttributes;
					}));
			future = future.compose(Merges.mergeTaskAttributes(this.constants, source.constants, codePrefix + ".constants",
					vertx, (model, mergedConstants) -> {
						model.constants = mergedConstants;
					}));

			promise.complete(merged);
			future = future.map(mergedValidatedModel -> {

				mergedValidatedModel.id = this.id;
				return mergedValidatedModel;
			});

		} else {

			promise.complete(this);
		}
		return future;
	}

}
