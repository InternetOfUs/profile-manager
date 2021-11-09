#
# -----------------------------------------------------------------------------
#
# Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#
# -----------------------------------------------------------------------------
#

from locust import HttpUser, task


class ProfileManagerPerformance(HttpUser):
    
    @task
    def crud_profile(self):

        profile = {
            "name":{
                    "first":"Test"
            }
        }
        
        with self.client.post("/profiles", json=profile,name="/profiles") as response:
            if response.status_code == 201:
                profile = response.json()
            else:
                response.failure("Profile not created")
        
        profile_id = profile["id"]
        with self.client.get(f"/profiles/{profile_id}",name="/profiles") as response:
            if response.status_code == 200 and profile == response.json():
                profile = response.json()
            else:
                response.failure("Profile not found")

        with self.client.delete(f"/profiles/{profile_id}",name="/profiles") as response:
            if response.status_code != 204:
                response.failure("Profile not deleted")

        with self.client.get(f"/profiles/{profile_id}",name="/profiles", catch_response=True) as response:
            if response.status_code == 404:
                response.success()
            else:
                response.failure("Found deleted profile")
