#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR >/dev/null
docker-compose -p wenet_profile_manager_services_dev -f src/dev/docker/docker-compose.yml up --remove-orphans -d
DOCKER_BUILDKIT=1 docker build -f src/dev/docker/Dockerfile -t wenet/profile-manager:dev .
docker run --name wenet_profile_manager_dev -v /var/run/docker.sock:/var/run/docker.sock -v ${HOME}/.m2/repository:/root/.m2/repository  -v ${PWD}:/app -p 5005:5005 -it wenet/profile-manager:dev /bin/bash
popd >/dev/null