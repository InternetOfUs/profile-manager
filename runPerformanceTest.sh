#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not start the performance test inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	docker run --rm --name wenet_profile_manager_performance_test -i loadimpact/k6:latest run $@ - <$DIR/src/test/k6/performance.js
fi