#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not run the performance test inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	docker run --rm -p 8089:8089 -v ${PWD}/src/test/python:/mnt/locust locustio/locust -f /mnt/locust/locustfile.py --headless --users 100 --spawn-rate 10 -H https://ardid.iiia.csic.es/wenet/profile-manager --run-time 3m
fi