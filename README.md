# WeNet - Profile manager

## Introduction

The profile manager component is the one responsible for storing and maintaining the WeNet user profiles
and its historic. An user profile is a set of attributes that define the state of the user. Some of these
attributes are filled in by the user, such as name, email, telephone,..., or by  other components of
the platform, such as social relationships, routines,.... Every time a profile is modified a copy of
the previous profile values is stored in the historic. This allows to know, to the rest of components,
how the state of the user evolves in the time.

Another responsibility of the profile manager is to evaluate the trust of one user over another when it is
doing some action. The trust is dynamic and will be updated every time they collaborate to achieve a task.
When a user has received help it can rate the performance of the user that has helped it. For this it has to
post a performance rating event to the profile manager. These events are used by the profile manager when
it has to provide the trust that has an user that another does a certain action.When you want to calculate
he trust, you must specify some parameters that are used to select with events has to be aggregated to obtain
he trust. Also you must define the aggregation function, that can be:

 - RECENCY_BASED: the trust is the average of the last 'n' rating events. At the moment n=5.
 - AVERAGE: the trust is the average of all the rating events.
 - MEDIAN: the trust is the median of all the rating events.
 - MINIMUM: the trust is the minimum rating of all the events.
 - MAXIMUM: the trust is the maximum rating of all the events.

## Setup and configuration

### Installation

The profile manager component required [Java version 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or higher.

All required java packages will be automatic installed by the compilation tool (`./mvnw clean install`).

### Requirements

The profile manager component requires:

 - [MongoDB](https://docs.mongodb.com/manual/installation/)
 - [WeNet - Profile manager](https://bitbucket.org/wenet/profile-manager/)
 - [WeNet - Interaction protocol engine](https://bitbucket.org/wenet/wenet-interaction-protocol-engine/)
 - [WeNet - Service API](https://bitbucket.org/wenet/wenet-service-api/)


### Docker support

To use this feature you must to install the next software.

 - [docker](https://docs.docker.com/install/)
 - [docker compose](https://docs.docker.com/compose/install/)


#### Create docker image

If you want to create an image execute the next command.

```
docker build -f src/main/docker/Dockerfile -t wenet/profile-manager:latest .
```

You can use the next arguments:

 - **DEFAULT_API_HOST** to define the default host where API will be bind. By default is **0.0.0.0**.
 - **DEFAULT_API_PORT** to define the default port where API will be bind. By default is **8080**.
 - **DEFAULT_DB_HOST** to define the default mongo database server host name. By default is **localhost**.
 - **DEFAULT_DB_PORT** to define the default mongo database server port. By default is **27017**.
 - **DEFAULT_DB_NAME** to define the default mongo database name. By default is **wenetProfileManagerDB**.
 - **DEFAULT_DB_USER_NAME** to define the default mongo database user name. By default is **wenetProfileManager**.
 - **DEFAULT_DB_USER_PASSWORD** to define the default mongo database user password. By default is **password**.
 - **DEFAULT_WENET_TASK_MANAGER_API** to define the path to the task manager component to use. By default is **"https://wenet.u-hopper.com/prod/task_manager**.
 - **DEFAULT_WENET_SERVICE_API** to define the path to the service component to use. By default is **"https://wenet.u-hopper.com/prod/service**.
 - **DEFAULT_WENET_SOCIAL_CONTEXT_BUILDER_API** to define the path to the service component to use. By default is **"https://wenet.u-hopper.com/prod/social_context_builder**.

This arguments are used to create a configurations files at **/usr/wenet/profile-manager/etc**.
So you can mount a volume to this if you want to modify any configuration property at runtime.

#### Run docker image

To run a the created docker image, run the next command:

```
docker run -t -i -p 8080:8080 --name profile_manager wenet/profile-manager
```

You can modify use the next environment properties to modify some parameters of the server:

 - **API_HOST** to define the host where the API has to bind. By default is **0.0.0.0**.
 - **API_PORT** to define the port where the API has to bind. By default is **8080**.
 - **DB_HOST** to define the mongo database server host name. By default is **localhost**.
 - **DB_PORT** to define the mongo database server port. By default is **27017**.
 - **DB_NAME** to define the mongo database name. By default is **wenetProfileManagerDB**.
 - **DB_USER_NAME** to define the mongo database user name. By default is **wenetProfileManager**.
 - **DB_USER_PASSWORD** to define the mongo database user password. By default is **password**.
 - **WENET_TASK_MANAGER_API** to define the path to the task manager component to use. By default is **"https://wenet.u-hopper.com/prod/task_manager**.
 - **WENET_SERVICE_API** to define the path to the service component to use. By default is **"https://wenet.u-hopper.com/prod/service**.
 - **WENET_SOCIAL_CONTEXT_BUILDER_API** to define the path to the social context builder component to use. By default is **"https://wenet.u-hopper.com/prod/social_context_builder**.


Also you can define your own configuration that modify this properties and mount to  **/usr/wenet/profile-manager/etc**.

If you want to start also a database and link both you can use the docker compose (`docker-compose -f src/main/docker/docker-compose.yml up -d`).  To modify the component to links or the port to deploy use the next variables:

 - **INTERACTION_PROTOCOL_ENGINE_API_PORT** to define the port to listen for the API calls. By default is **8083**.
 - **MONGO_ROOT_USER** to define the root user for the MongoDB. By default is **root**.
 - **MONGO_ROOT_PASSWORD** to define the password of the root user for the MongoDB. By default is **password**.
 - **WENET_TASK_MANAGER_API** to define the path to the task manager component to use. By default is **"https://wenet.u-hopper.com/prod/task_manager**.
 - **WENET_SERVICE_API** to define the path to the service component to use. By default is **"https://wenet.u-hopper.com/prod/service**.
 - **WENET_SOCIAL_CONTEXT_BUILDER_API** to define the path to the social context builder component to use. By default is **"https://wenet.u-hopper.com/prod/social_context_builder**.

When the container is ready you can access the logs of the component, following the next steps:

 - Discover the identifier of the container of the component (`docker container ls`).
 - Open a shell to the container of the component (`docker exec -it c82f8f4a136c /bin/bash`).
 - The logs are on the directory **/usr/wenet/profile-manager/var/log**.


## Usage

The project use the [Apache maven](https://maven.apache.org/) tool to solve the dependencies,
generate the Open API documentation, compile the component and run the test.

 - Use `./mvnw dependency:list` to show the component dependencies.
 - Use `./mvnw compile` to compile and generate the Open API documentation (**target/classes/wenet-profile_manager-openapi.yml**).
 - Use `./mvnw tests` to run the test.
 - Use `./mvnw site` to generate a HTML page (**target/site/index.html**) with all the reports (test, javadoc, PMD,CPD and coverage).


### Run and configure

We encourage you to use the docker image of this component instead the next commands, because it is easier to use.

If you want to run this component you must to follow the next steps:

 - Compile the project (`./mvnw clean install`)
 - On the directory where you want to install the component (for example **~/profile-manager**) create the directories **etc** and **lib**.
 - Copy the compiled jar (`cp target/wenet-profile-manager-VERSION.jar ~/profile-manager/.`).
 - Copy the jar dependencies (`cp target/lib/* ~/profile-manager/lib/.`).
 - Copy the default logging configuration (`cp src/main/resources/tinylog.properties ~/profile-manager/etc/log_configuration.properties.`).
 - Copy the default component configuration (`cp src/main/resources/wenet-profile-manager.configuration.json ~/profile-manager/etc/configuration.conf.`).
 - Edit the component configuration to fix the URL of the other components and the database connection.
 - Go to the install directory and execute the command `java -jar -Dtinylog.configuration=etc/log_configuration.properties wenet-profile-manager-VERSION.jar -c etc`.


## Documentation

The latest APIs documentation is available [here](http://swagger.u-hopper.com/?url=https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-profile_manager-openapi.yaml).


## Instances

The profile manager has the next available instances:

 - WeNet production profile manager API is available at [https://wenet.u-hopper.com/prod/profile_manager/](https://wenet.u-hopper.com/prod/profile_manager/).
 - WeNet development profile manager API is available at [https://wenet.u-hopper.com/dev/profile_manager/](https://wenet.u-hopper.com/dev/profile_manager/).
 - The IIIA stable profile manager API is available at [http://ardid.iiia.csic.es/wenet/profile-manager/latest/](http://ardid.iiia.csic.es/wenet/profile-manager/latest/).
 - The IIIA development profile manager API is available at [http://ardid.iiia.csic.es/wenet/profile-manager/dev/](http://ardid.iiia.csic.es/wenet/profile-manager/dev/).
 - The profile manager API 0.12 is available at [http://ardid.iiia.csic.es/wenet/profile-manager/0.12/](http://ardid.iiia.csic.es/wenet/profile-manager/0.12/).
 - The profile manager API 0.11 is available at [http://ardid.iiia.csic.es/wenet/profile-manager/0.11/](http://ardid.iiia.csic.es/wenet/profile-manager/0.11.0/).
 - The profile manager API 0.10 is available at [http://ardid.iiia.csic.es/wenet/profile-manager/0.10/](http://ardid.iiia.csic.es/wenet/profile-manager/0.10.0/).


## License

This software is under the [MIT license](LICENSE)


## Interaction with other WeNet components

### Social context builder

 - Inform every time a new user profile is created. (GET {{social_context_builder_api}}/social/relations/{{userId}})


## Contact

### Researcher

 - [Nardine Osman](http://www.iiia.csic.es/~nardine/) ( [IIIA-CSIC](https://www.iiia.csic.es/~nardine/) ) nardine (at) iiia.csic.es
 - [Carles Sierra](http://www.iiia.csic.es/~sierra/) ( [IIIA-CSIC](https://www.iiia.csic.es/~sierra/) ) sierra (at) iiia.csic.es

### Developers

 - Joan Jené ( [UDT-IA, IIIA-CSIC](https://www.iiia.csic.es/people/person/?person_id=19) ) jjene (at) iiia.csic.es
 - Bruno Rosell i Gui ( [UDT-IA, IIIA-CSIC](https://www.iiia.csic.es/people/person/?person_id=27) ) rosell (at) iiia.csic.es
