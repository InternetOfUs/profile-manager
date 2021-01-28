# syntax=docker/dockerfile:experimental

# --- Build the code
FROM maven:3.6.3-jdk-11-slim AS build
LABEL stage=build
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml .
RUN --mount=type=cache,id=mvn-cache,target=/root/.m2 mvn dependency:go-offline
COPY src/ /build/src/
RUN --mount=type=cache,id=mvn-cache,target=/root/.m2 mvn -DfinalName=wenet-profile-manager -DskipTests install

# --- Run the code
FROM openjdk:11.0.5-jre
ARG DEFAULT_API_HOST=0.0.0.0
ARG DEFAULT_API_PORT=8080
ARG DEFAULT_DB_HOST=localhost
ARG DEFAULT_DB_PORT=27017
ARG DEFAULT_DB_NAME=wenetProfileManagerDB
ARG DEFAULT_DB_USER_NAME=wenetProfileManager
ARG DEFAULT_DB_USER_PASSWORD=password
ARG DEFAULT_WENET_TASK_MANAGER_API="https://wenet.u-hopper.com/prod/task_manager"
ARG DEFAULT_WENET_SERVICE_API="https://wenet.u-hopper.com/prod/service"
ARG DEFAULT_WENET_SOCIAL_CONTEXT_BUILDER_API="https://wenet.u-hopper.com/prod/social_context_builder"
ARG DEFAULT_COMP_AUTH_KEY=""
ENV API_HOST=${DEFAULT_API_HOST}
ENV API_PORT=${DEFAULT_API_PORT}
ENV DB_HOST=${DEFAULT_DB_HOST}
ENV DB_PORT=${DEFAULT_DB_PORT}
ENV DB_NAME=${DEFAULT_DB_NAME}
ENV DB_USER_NAME=${DEFAULT_DB_USER_NAME}
ENV DB_USER_PASSWORD=${DEFAULT_DB_USER_PASSWORD}
ENV WENET_TASK_MANAGER_API=${DEFAULT_WENET_TASK_MANAGER_API}
ENV WENET_SOCIAL_CONTEXT_BUILDER_API=${DEFAULT_WENET_SOCIAL_CONTEXT_BUILDER_API}
ENV RUN_ARGS=""
ENV COMP_AUTH_KEY=${DEFAULT_COMP_AUTH_KEY}

# Create running dierectories
RUN mkdir -p /usr/wenet/profile-manager/lib
RUN mkdir -p /usr/wenet/profile-manager/etc
RUN mkdir -p /usr/wenet/profile-manager/var/log

# Create configuration for api
RUN echo "{\"api\":{\"host\":\"${DEFAULT_API_HOST}\",\"port\":${DEFAULT_API_PORT}}}" > /usr/wenet/profile-manager/etc/api.json

# Create configuration for persistence
RUN echo "{\"persistence\":{\"db_name\":\"${DEFAULT_DB_NAME}\",\"host\":\"${DEFAULT_DB_HOST}\",\"port\":${DEFAULT_DB_PORT},\"username\":\"${DEFAULT_DB_USER_NAME}\",\"password\":\"${DEFAULT_DB_USER_PASSWORD}\"}}" > /usr/wenet/profile-manager/etc/persistence.json

# Create configuration for task manager component
RUN echo "{\"wenetComponents\":{\"taskManager\":\"${DEFAULT_WENET_TASK_MANAGER_API}\"}}" > /usr/wenet/profile-manager/etc/taskManager.json

# Create configuration for service component
RUN echo "{\"wenetComponents\":{\"service\":\"${DEFAULT_WENET_SERVICE_API}\"}}" > /usr/wenet/profile-manager/etc/service.json

# Create configuration for social context builder component
RUN echo "{\"wenetComponents\":{\"socialContextBuilder\":\"${DEFAULT_WENET_SOCIAL_CONTEXT_BUILDER_API}\"}}" > /usr/wenet/profile-manager/etc/socialContextBuilder.json

WORKDIR /usr/wenet/profile-manager
COPY --from=build /build/target/wenet-profile-manager.jar /usr/wenet/profile-manager/wenet-profile-manager.jar
COPY --from=build /build/target/lib/ /usr/wenet/profile-manager/lib/


## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.7.3/wait /wait
RUN chmod +x /wait

EXPOSE ${API_PORT}
CMD /wait && java -jar wenet-profile-manager.jar -c etc \
  -papi.host=${API_HOST} -papi.port=${API_PORT}\
  -ppersistence.host=${DB_HOST} -ppersistence.port=${DB_PORT} -ppersistence.db_name=${DB_NAME} -ppersistence.username=${DB_USER_NAME} -ppersistence.password=${DB_USER_PASSWORD}\
  -pwenetComponents.taskManager=${WENET_TASK_MANAGER_API}\
  -pwenetComponents.service=${WENET_SERVICE_API}\
  -pwenetComponents.socialContextBuilder=${WENET_SOCIAL_CONTEXT_BUILDER_API}\
  -pwebClient.wenetComponentApikey=${COMP_AUTH_KEY}\
  ${RUN_ARGS}
