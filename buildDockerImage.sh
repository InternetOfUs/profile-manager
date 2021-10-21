#!/bin/bash
if ! docker stats --no-stream >/dev/null 2>&1; then
    echo "Docker does not seem to be running, run it first and retry"
    exit 1
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd "$DIR" > /dev/null
	COMPONENT_VERSION=$(grep -m1 '<version>' pom.xml |cut -d '<' -f2  |cut -d '>' -f2)
	COMPONENT_NAME="internetofus/profile-manager"
	DOCKER_TAG="$COMPONENT_NAME:$COMPONENT_VERSION"
	PROFILE=${1:-"gitlab"}
	DOCKER_BUILDKIT=1 docker build --build-arg DEFAULT_PROFILE=$PROFILE -f src/main/docker/Dockerfile -t $DOCKER_TAG .
	popd >/dev/null
fi