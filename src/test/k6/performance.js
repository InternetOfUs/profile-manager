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
import { check } from 'k6';

/**
 * Test to check the performance of the profile manager.
 */
export default function() {

	var profileManagerApi = 'https://wenet.u-hopper.com/dev/profile_manager';
	if (typeof __ENV.PROFILE_MANAGER_API === 'string') {

		profileManagerApi = __ENV.PROFILE_MANAGER_API;
	}
	var createPayload = JSON.stringify({ name: { prefix: 'k6', first: 'performance', last: 'test' } });
	var createParams = {
		headers: {
			'Content-Type': 'application/json',
		},
	};
	var createResponse = http.post(profileManagerApi + '/profiles', createPayload, createParams);
	check(createResponse, {
		'created profile': (r) => r.status === 201,
	});
	var profile = createResponse.json();
	var retrieveResponse = http.get(profileManagerApi + '/profiles/' + profile.id);
	check(retrieveResponse, {
		'retrieved profile': (r) => r.status === 200,
		'expected profile': (r) => r.body == createResponse.body,
	});
	var retrievePageResponse = http.get(profileManagerApi + '/profiles');
	check(retrievePageResponse, {
		'retrieved page': (r) => r.status === 200
	});
	var deleteResponse = http.del(profileManagerApi + '/profiles/' + profile.id);
	check(deleteResponse, {
		'deleted profile': (r) => r.status === 204,
	});
	var noRetrieveResponse = http.get(profileManagerApi + '/profiles/' + profile.id);
	check(noRetrieveResponse, {
		'no retrieved profile': (r) => r.status === 404,
	});


}