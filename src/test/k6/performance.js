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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import http from 'k6/http';
import { group, check } from 'k6';

/**
 * Test to check the performance of the profile manager.
 */
export default function() {

	var profileManagerApi = 'https://wenet.u-hopper.com/dev/profile_manager';
	if (typeof __ENV.PROFILE_MANAGER_API === 'string') {

		profileManagerApi = __ENV.PROFILE_MANAGER_API;
	}
	group('profile manager performance', function() {

		let profile;
		group('create profile', function() {

			var profileToCreate = {
				name: {
					prefix: 'k6',
					first: 'performance',
					last: 'test'
				}
			};
			var createPayload = JSON.stringify(profileToCreate);
			var createParams = {
				headers: {
					'Content-Type': 'application/json',
				}
			};
			var createResponse = http.post(profileManagerApi + '/profiles', createPayload, createParams);
			check(createResponse, {
				'created profile': (r) => r.status === 201,
				'obtain created profile': (r) => {
					profile = r.json();
					return profile !== undefined;
				}
			});
		});

		group('retrieve profile', function() {

			var retrieveResponse = http.get(profileManagerApi + '/profiles/' + profile.id);
			check(retrieveResponse, {
				'retrieved profile': (r) => r.status === 200,
				'validate profile': (r) => {
					var received = JSON.stringify(r.json()).split('').sort().join('');
					var expected = JSON.stringify(profile).split('').sort().join('');
					return received == expected;
				}
			});
			var retrievePageResponse = http.get(profileManagerApi + '/profiles');
			check(retrievePageResponse, {
				'retrieved page': (r) => r.status === 200,
				'validate page': (r) => {

					var page = r.json();
					if (page.total == 0) {

						return false;

					} else if ( page.total > page.profiles.length){

						return true;

					} else {

						var expected = JSON.stringify(profile).split('').sort().join('');
						for (var i = page.profiles.length - 1; i >= 0; i--) {

							var received = JSON.stringify(page.profiles[i]).split('').sort().join('');
							if (received == expected) {

								return true;
							}

						}

						return false;
					}
				}
			});

		});

		group('delete profile', function() {

			var deleteResponse = http.del(profileManagerApi + '/profiles/' + profile.id);
			check(deleteResponse, {
				'deleted profile': (r) => r.status === 204
			});
		});
	});

}