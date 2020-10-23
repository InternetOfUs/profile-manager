#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR >/dev/null
COMPONENT_VERSION=$(grep -m1 '<version>' pom.xml |cut -d '<' -f2  |cut -d '>' -f2)
COMPONENT_NAME="internetofus/profile-manager"
DOCKER_TAG="$COMPONENT_NAME:$COMPONENT_VERSION"
DOCKER_BUILDKIT=1 docker build -f src/main/docker/Dockerfile -t $DOCKER_TAG .
popd >/dev/null