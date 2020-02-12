###
# The 'profile-manager' stores and maintains the WeNet user profile.
# To build:
#   docker build -t wenet/profile-manager .
# To run:
#   docker run -t -i -p 8080:8080 wenet/profile-manager
###

# --- Build the code
FROM maven:3.6.3-jdk-11-slim AS build
LABEL stage=build
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /build/src/
RUN mvn -DfinalName=profile-manager install

# --- Run the code
FROM openjdk:11.0.5-jre
RUN mkdir -p /usr/profile-manager/lib
WORKDIR /usr/profile-manager
COPY --from=build /build/target/profile-manager.jar /usr/profile-manager/profile-manager.jar
COPY --from=build /build/target/lib/ /usr/profile-manager/lib/
EXPOSE 8080
CMD ["java","-jar","profile-manager.jar"]
