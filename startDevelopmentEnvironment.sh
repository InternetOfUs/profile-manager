#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not start the development environment inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd "$DIR" >/dev/null
	DOCKER_BUILDKIT=1 docker build -f src/dev/docker/Dockerfile -t internetofus/profile-manager:dev .
	if [ $? -eq 0 ]; then
		DOCKER_PARAMS="--rm --name wenet_profile_manager_dev -v /var/run/docker.sock:/var/run/docker.sock -p 5551:5551 -it"
		if [[ "$OSTYPE" == "darwin"* ]]; then
			DOCKER_PARAMS="$DOCKER_PARAMS -e TESTCONTAINERS_HOST_OVERRIDE=docker.for.mac.host.internal"
		fi
		docker run $DOCKER_PARAMS -v "${HOME}/.m2/repository":/root/.m2/repository  -v "${PWD}":/app internetofus/profile-manager:dev /bin/bash
		./stopDevelopmentEnvironment.sh
	fi
	popd >/dev/null
fi