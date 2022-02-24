# WeNet - Profile manager

## Introduction

The profile manager component is responsible for storing and maintaining the WeNet user profiles.
A user profile is a set of attributes that define the state of the user. Some of these
attributes are filled in by the user, such as name, email, telephone,..., or by  other components of
the platform, such routines.

The social relationships of a user are not stored on the profile, because a user can have a relationship
with all the playing applications of the user, and this could be a lot of relationships. For this reason,
the profile manager provides web services to add/update/delete/get these relationships.

The profile manager has the capability to maintain a historic with the profile changes. By default,
this behaviour is disabled and it can be enabled on the server configuration, or using a parameter
on the update profile requests. This allows knowing, to the rest of the components,
how the state of the user evolves in time.

Another responsibility of the profile manager is to evaluate the trust of one user over another when it is
doing some action. The trust is dynamic and will be updated every time they collaborate to achieve a task.
When a user has received some help it can rate the performance of the user that has helped it. For this, it has to
post a performance rating event to the profile manager. These events are used by the profile manager when
it has to provide the trust that has a user that another does a certain action. When you want to calculate
the trust, you must specify some parameters that are used to select events that have to be aggregated to obtain
the trust. Also, you must define the aggregation function, which can be:

 - RECENCY_BASED: the trust is the average of the last 'n' rating events. At the moment n=5.
 - AVERAGE: the trust is the average of all the rating events.
 - MEDIAN: the trust is the median of all the rating events.
 - MINIMUM: the trust is the minimum rating of all the events.
 - MAXIMUM: the trust is the maximum rating of all the events.


## Setup and configuration

First of all, you must install the next software.

 - [docker](https://docs.docker.com/install/)
 - [docker compose](https://docs.docker.com/compose/install/)

### Requirements

The profile manager component requires:

 - [MongoDB](https://docs.mongodb.com/manual/installation/)
 - [Social context builder](https://github.com/InternetOfUs/social-context-builder/)
 - [Service API](https://github.com/InternetOfUs/service-api/)
 - [Task manager API](https://github.com/InternetOfUs/task-manager/)

### Development

The development is done using a docker image that can be created and started with the script `./startDevelopmentEnvironment.sh`.
The scrip start the next services:

 - [Mongo express](http://localhost:8081)
 - [Swagger editor](http://localhost:8080)

And also start a bash console where you can compile and test the project. The project uses the [Apache maven](https://maven.apache.org/)
to solve the dependencies, generate the Open API documentation, compile the component and run the test.

 - Use `mvn dependency:list` to show the component dependencies.
 - Use `mvn compile` to compile and generate the Open API documentation (**target/classes/wenet-profile_manager-openapi.yml**).
 - Use `mvn test` to run the test.
 - Use `mvn -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:5005 -Xnoagent -Djava.compiler=NONE" test` to run the test on debug mode.
 - Use `mvn site` to generate a HTML page (**target/site/index.html**) with all the reports (test, javadoc, PMD,CPD and coverage).

Finally, you can stop the development exiting the bash and executing the script `./stopDevelopmentEnvironment.sh`.


### Create docker image

If you want to create an image execute the next command.

```
./buildDockerImage.sh
```

This creates the generic docker image, but you can create a different wit the **docker build** command and using the next arguments:

 - **DEFAULT_API_HOST** to define the host where the API has to bind. By default is **0.0.0.0**.
 - **DEFAULT_API_PORT** to define the port where the API has to bind. By default is **8080**.
 - **DEFAULT_DB_HOST** to define the mongo database server hostname. By default is **localhost**.
 - **DEFAULT_DB_PORT** to define the mongo database server port. By default is **27017**.
 - **DEFAULT_DB_NAME** to define the mongo database name. By default is **wenetProfileManagerDB**.
 - **DEFAULT_DB_USER_NAME** to define the mongo database user name. By default is **wenetProfileManager**.
 - **DEFAULT_DB_USER_PASSWORD** to define the mongo database user password. By default is **password**.
 - **DEFAULT_WENET_PROFILE_DIVERSITY_MANAGER_API** to define the path to the profile manager extension for the word net similarity component to use. By default is **https://wenet.u-hopper.com/prod/profile_diversity_manager**.
 - **DEFAULT_WENET_TASK_MANAGER_API** to define the path to the task manager component to use. By default is **https://wenet.u-hopper.com/prod/task_manager**.
 - **DEFAULT_WENET_SERVICE_API** to define the path to the service component to use. By default is **https://wenet.u-hopper.com/prod/service**.
 - **DEFAULT_WENET_SOCIAL_CONTEXT_BUILDER_API** to define the path to the social context builder component to use. By default is **https://wenet.u-hopper.com/prod/social_context_builder**.
 - **DEFAULT_CACHE_TIMEOUT** to define the time in seconds that a value can be on the cache. By default is **300**.
 - **DEFAULT_CACHE_SIZE** to define the maximum number of entries that can be on the cache. By default is **10000**.
 - **DEFAULT_AUTOSTORE_PROFILE_CHANGES_IN_HISTORY** this is **true** if every change on the profile has to provoke that the history is updated. By default is **false**.

Also, you can define your configuration that modifies these properties and mount to  **/usr/wenet/profile-manager/etc**.


### Run, configure and link with a MongoDB

You can start this component starting the [latest docker image upload to docker hub](https://hub.docker.com/r/internetofus/profile-manager).

```
docker run internetofus/profile-manager:latest
```

On this container, you can use the next environment variables:

 - **API_HOST** to define the host where the API has to bind. By default is **0.0.0.0**.
 - **API_PORT** to define the port where the API has to bind. By default is **8080**.
 - **DB_HOST** to define the mongo database server hostname. By default is **localhost**.
 - **DB_PORT** to define the mongo database server port. By default is **27017**.
 - **DB_NAME** to define the mongo database name. By default is **wenetProfileManagerDB**.
 - **DB_USER_NAME** to define the mongo database user name. By default is **wenetProfileManager**.
 - **DB_USER_PASSWORD** to define the mongo database user password. By default is **password**.
 - **WENET_PROFILE_DIVERSITY_MANAGER_API** to define the path to the profile manager extension for the word net similarity component to use. By default is **https://wenet.u-hopper.com/prod/profile_diversity_manager**.
 - **WENET_TASK_MANAGER_API** to define the path to the task manager component to use. By default is **https://wenet.u-hopper.com/prod/task_manager**.
 - **WENET_SERVICE_API** to define the path to the service component to use. By default is **https://wenet.u-hopper.com/prod/service**.
 - **WENET_SOCIAL_CONTEXT_BUILDER_API** to define the path to the social context builder component to use. By default is **https://wenet.u-hopper.com/prod/social_context_builder**.
 - **COMP_AUTH_KEY** to define the authentication key that the component has to use to interact with the other WeNet components.
 - **CACHE_TIMEOUT** to define the time in seconds that a value can be on the cache. By default is **300**.
 - **CACHE_SIZE** to define the maximum number of entries that can be on the cache. By default is **10000**.
 - **AUTOSTORE_PROFILE_CHANGES_IN_HISTORY** this is **true** if every change on the profile has to provoke that the history is updated. By default is **false**.

When the container is started, it stores the log messages at **/usr/wenet/profile-manager/var/log/profile-manager.log**. This file is limited
to 10 MB and rolled every day using the pattern **profile-manager.log.X** (where X is a number between 1 and 99).

If you want to start also a database and link both you can use the defined docker compose configuration.

```
docker-compose -f src/main/docker/docker-compose.yml up -d
```

This docker compose has the next variables:

 - **PROFILE_MANAGER_API_PORT** to define the port to listen for the API calls. By default is **8083**.
 - **MONGO_ROOT_USER** to define the root user for the MongoDB. By default is **root**.
 - **MONGO_ROOT_PASSWORD** to define the password of the root user for the MongoDB. By default is **password**.
 - **WENET_PROFILE_DIVERSITY_MANAGER_API** to define the path to the profile manager extension for the word net similarity component to use. By default is **https://wenet.u-hopper.com/prod/profile_diversity_manager**.
 - **WENET_TASK_MANAGER_API** to define the path to the profile manager component to use. By default is **https://wenet.u-hopper.com/prod/task_manager**.
 - **WENET_SERVICE_API** to define the path to the service component to use. By default is **https://wenet.u-hopper.com/prod/service**.
 - **WENET_SOCIAL_CONTEXT_BUILDER_API** to define the path to the social context builder component to use. By default is **https://wenet.u-hopper.com/prod/social_context_builder**.
 - **CACHE_TIMEOUT** to define the time in seconds that a value can be on the cache. By default is **300**.
 - **CACHE_SIZE** to define the maximum number of entries that can be on the cache. By default is **10000**.
 - **AUTOSTORE_PROFILE_CHANGES_IN_HISTORY** this is **true** if every change on the profile has to provoke that the history is updated. By default is **false**.

### Show running logs

When the container is ready you can access the logs of the component, following the next steps:

 - Discover the identifier of the container of the component (`docker container ls`).
 - Open a shell to the container of the component (`docker exec -it <CONTAINER_NAME> /bin/bash`).
 - The logs are on the directory **/usr/wenet/profile-manager/var/log**.

### Run performance test

This component provides a performance test using [K6](https://k6.io/). To run this test use the script `./runPerformanceTest.sh`.
By default, it is run over the development server, if you want to test another server pass the environment property **PROFILE_MANAGER_API**,
and also you can pass any parameter to configure **k6**. For example to run the test over the production one with 10 virtual users
during 30 seconds execute:

```
./runPerformanceTest.sh -e PROFILE_MANAGER_API="https://wenet.u-hopper.com/prod/profile_manager" --vus 10 --duration 30s
```

## Documentation

The latest APIs documentation is available [here](http://swagger.u-hopper.com/?url=https://github.com/InternetOfUs/components-documentation/raw/master/sources/wenet-profile_manager-openapi.yaml).


## Instances

The profile manager has the next available instances:

 - WeNet production profile manager API is available at [https://wenet.u-hopper.com/prod/profile_manager](https://wenet.u-hopper.com/prod/profile_manager/help/info).
 - WeNet development profile manager API is available at [https://wenet.u-hopper.com/dev/profile_manage](https://wenet.u-hopper.com/dev/profile_manager/help/info).
 - The IIIA stagging API is available at [https://ardid.iiia.csic.es/wenet/profile-manager](https://ardid.iiia.csic.es/wenet/profile-manager/help/info).


## License

This software is under the [Apache V2 license](LICENSE)


## Interaction with other WeNet components

### Social context builder

 - notify every time a new user profile is created. (POST {{social_context_builder_api}}/social/relations/initialize/{{userId}})
 - Notify every time a user profile is updated. (POST {{social_context_builder_api}}/social/notification/profileUpdate/{{userId}})

### [Task manager](https://hub.docker.com/r/internetofus/task-manager)

 - Used to validate that a task is defined (GET {{task_manager_api}}/tasks/{{appId}}).

### [Service](https://hub.docker.com/r/internetofus/service-api)

 - Used to validate that an application is defined (GET {{service_api}}/app/{{appId}}).


## Contact

### Researcher

 - [Nardine Osman](http://www.iiia.csic.es/~nardine/) ( [IIIA-CSIC](https://www.iiia.csic.es/~nardine/) ) nardine (at) iiia.csic.es
 - [Carles Sierra](http://www.iiia.csic.es/~sierra/) ( [IIIA-CSIC](https://www.iiia.csic.es/~sierra/) ) sierra (at) iiia.csic.es

### Developers

 - Joan Jené ( [UDT-IA, IIIA-CSIC](https://www.iiia.csic.es/people/person/?person_id=19) ) jjene (at) iiia.csic.es
 - Bruno Rosell i Gui ( [UDT-IA, IIIA-CSIC](https://www.iiia.csic.es/people/person/?person_id=27) ) rosell (at) iiia.csic.es
