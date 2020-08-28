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

package eu.internetofus.wenet_profile_manager.api.communities;

import eu.internetofus.common.components.profile_manager.SocialPractice;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;

/**
 * Check the manipulation of the {@link SocialPractice}s in a {@link WeNetUserProfile}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunitiesSocialPracticesIT {// extends AbstractCommunityFieldManipulationByIdentifierIT<SocialPractice> {

  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected String fieldPath() {
  //
  // return Communities.SOCIAL_PRACTICES_PATH;
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected Future<SocialPractice> createInvalidModel(final Vertx vertx, final VertxTestContext testContext) {
  //
  // final SocialPractice socialPractice = new SocialPractice();
  // socialPractice.label = ValidationsTest.STRING_1024;
  // return Future.succeededFuture(socialPractice);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected Future<SocialPractice> createValidModel(final int index, final Vertx vertx, final VertxTestContext
  // testContext) {
  //
  // final SocialPractice model = new SocialPracticeTest().createModelExample(index);
  // return Future.succeededFuture(model);
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected void updateIdsTo(final SocialPractice source, final SocialPractice target) {
  //
  // target.id = source.id;
  // for (int i = 0; i < target.norms.size(); i++) {
  //
  // target.norms.get(i).id = source.norms.get(i).id;
  // }
  //
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected List<SocialPractice> initModelsIn(final WeNetUserProfile profile) {
  //
  // profile.socialPractices = new ArrayList<>();
  // return profile.socialPractices;
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected List<SocialPractice> modelsIn(final WeNetUserProfile profile) {
  //
  // return profile.socialPractices;
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected Class<SocialPractice> modelClass() {
  //
  // return SocialPractice.class;
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // @Override
  // protected String idOf(final SocialPractice model) {
  //
  // return model.id;
  // }

}
