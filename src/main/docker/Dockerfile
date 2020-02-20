# --- Build the code
FROM maven:3.6.3-jdk-11-slim AS build
LABEL stage=build
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /build/src/
RUN mvn -DfinalName=profile-manager -DskipTests install

# --- Run the code
FROM openjdk:11.0.5-jre
ARG DEFAULT_API_PORT=8080
ARG DEFAULT_DB_HOST=localhost
ARG DEFAULT_DB_PORT=27017
ARG DEFAULT_DB_NAME=wenetProfileManagerDB
ARG DEFAULT_DB_USER_NAME=wenetProfileManager
ARG DEFAULT_DB_USER_PASSWORD=password
ENV API_PORT=${DEFAULT_API_PORT}
ENV DB_HOST=${DEFAULT_DB_HOST}
ENV DB_PORT=${DEFAULT_DB_PORT}
ENV DB_NAME=${DEFAULT_DB_NAME}
ENV DB_USER_NAME=${DEFAULT_DB_USER_NAME}
ENV DB_USER_PASSWORD=${DEFAULT_DB_USER_PASSWORD}

# Create running dierectories
RUN mkdir -p /usr/profile-manager/lib
RUN mkdir -p /usr/profile-manager/etc
RUN mkdir -p /usr/profile-manager/var/log

# Create configuration for api
RUN echo "{\"api\":{\"port\":${DEFAULT_API_PORT}}}" > /usr/profile-manager/etc/persistence.json

# Create configuration for persistence
RUN echo "{\"persistence\":{\"db_name\":\"${DEFAULT_DB_NAME}\",\"host\":\"${DEFAULT_DB_HOST}\",\"port\":${DEFAULT_DB_PORT},\"username\":\"${DEFAULT_DB_USER_NAME}\",\"password\":\"${DEFAULT_DB_USER_PASSWORD}\"}}" > /usr/profile-manager/etc/persistence.json

WORKDIR /usr/profile-manager
COPY --from=build /build/target/profile-manager.jar /usr/profile-manager/profile-manager.jar
COPY --from=build /build/target/lib/ /usr/profile-manager/lib/
EXPOSE 8080
CMD ["java","-jar","profile-manager.jar","-cetc","-papi.port=${API_PORT}","-ppersistence.host=${DB_HOST}","-ppersistence.port=${DB_PORT}","-ppersistence.db_name=${DB_NAME}","-ppersistence.username=${DB_USER_NAME}","-ppersistence.password=${DB_USER_PASSWORD}"]
