# wenet-profile-manager

This project will be used to provide the API to storing and maintaining the WeNet user profile.

 - [License](LICENSE)
 - [Changes](CHANGELOG)
 - [API specification](https://bitbucket.org/wenet/wenet-components-documentation/src/master/sources/wenet-profile-manager-api.yaml) ( [Swagger UI](http://swagger.u-hopper.com/?url=https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-profile-manager-api.yaml) )
 - [API Examples with Postman](https://bitbucket.org/wenet/wenet-components-documentation/raw/b529541301d7dcc1f6b3fbf6afae3a18a18037f7/Postman_collections/wenet-profile_manager_api/wenet-profile-manager.postman_collection.json)
 - [Repository](https://rosell@bitbucket.org/wenet/wenet-profile-manager.git)
 - [Servers](#servers)
 - [Deploy with docker](#deploy-with-docker)
 - [Developing](#developing)
 - [Contact](#contact)

## Servers

  - **Dummy server** (http://ardid.iiia.csic.es/dummy-wenet-profile-manager/)[http://ardid.iiia.csic.es/dummy-wenet-profile-manager/]
  - **Development server** (http://ardid.iiia.csic.es/dev-wenet-profile-manager/)[http://ardid.iiia.csic.es/dev-wenet-profile-manager/]


## Deploy with docker

  You must install [docker](https://docs.docker.com/install/) and
  [docker compose](https://docs.docker.com/compose/install/) to deploy
  the **wenet-profile-manager**.

### Create docker image

If you want to create an image execute the next command.

```
docker build -f src/main/docker/Dockerfile -t wenet/profile-manager .
```

You can use the next arguments:

 - **DEFAULT_API_HOST** to define the default host where API will be bind. By default is **0.0.0.0**.
 - **DEFAULT_API_PORT** to define the default port where API will be bind. By default is **8080**.
 - **DEFAULT_DB_HOST** to define the default mongo database server host name. By default is **localhost**.
 - **DEFAULT_DB_PORT** to define the default mongo database server port. By default is **27017**.
 - **DEFAULT_DB_NAME** to define the default mongo database name. By default is **wenetProfileManagerDB**.
 - **DEFAULT_DB_USER_NAME** to define the default mongo database user name. By default is **wenetProfileManager**.
 - **DEFAULT_DB_USER_PASSWORD** to define the default mongo database user password. By default is **password**.

This arguments are used to create a configurations files at **/usr/wenet/profile-manager/etc**.
So you can mount a volume to this if you want to modify any configuration property at runtime.

### Run docker image

To run a the created docker image, run the next command:

```
docker run -t -i -p 8080:8080 --name wenet_profile_manager_api wenet/profile-manager
```

You can modify use the next environment properties to modify some parameters of the server:

 - **API_PORT** to define the host where the API has to bind. By default is **0.0.0.0**.
 - **API_PORT** to define the port where the API has to bind. By default is **8080**.
 - **DB_HOST** to define the mongo database server host name. By default is **localhost**.
 - **DB_PORT** to define the mongo database server port. By default is **27017**.
 - **DB_NAME** to define the mongo database name. By default is **wenetProfileManagerDB**.
 - **DB_USER_NAME** to define the mongo database user name. By default is **wenetProfileManager**.
 - **DB_USER_PASSWORD** to define the mongo database user password. By default is **password**.

Also you can define your own configuration that modify this properties and mount to  **/usr/wenet/profile-manager/etc**.

If you want to start also a database and link both you can execute:

```
docker-compose -f src/main/docker/docker-compose.yml up -d
```

After that you can interact with the API at **http://localhost:80**. You can modify the listening port
with the next environment properties:

 - **API_PORT** to define the port where the API has to bind to the localhost. By default is **80**.


## Developing

To develop you need the next software:

 - [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
 - [docker](https://docs.docker.com/install/)
 - [docker compose](https://docs.docker.com/compose/install/)
 - [Postman](https://www.postman.com/downloads/)

When you have installed this software you can start the development environment with:

```
docker-compose -f src/dev/docker/docker-compose.yml up -d
```

This starts:

 - A database ( **MongoDB** ) at **localhost:27017**
 - A web to explore the database ( **mongo-express** ) at **http://localhost:8081/** with the credentials **admin:password**

After that you can compile the source, pass the tests and calculate the test coverage with:

```
./mvnw clean install
```

This process generate the next files:

 - The OpenAPI description of the web services at **target/classes/wenet-profile-manager-api.yml**
 - The execution java package at **target/wenet-profile-manager-VERSION.jar** where **VERSION** is the version of the software.
 - The java dependencies at **target/lib**.


If you go to the **target** directory you can run the application with:

```
java -jar wenet-profile-manager-VERSION.jar
```

With the **-h** option you can see the arguments.

```
user@host:~/git/wenet-profile-manager/target$ java -jar wenet-profile-manager-VERSION.jar -h
usage: wenet-profile-manager
 -c,--confDir <<etc>>         Define a directory where the configuration
                              files are defined.
 -h,--help                    Show this help message.
 -p,--property <name=value>   Define a directory where the configuration
                              files are defined.
 -v,--version                 Show the software version.
```

# Contact

## Researcher

 - [Nardine Osman](http://www.iiia.csic.es/~nardine/) ( [IIIA-CSIC]((http://www.iiia.csic.es) )
  ![mail](data:image/png;base64,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)
 - [Carles Sierra](http://www.iiia.csic.es/~sierra/) ( [IIIA-CSIC]((http://www.iiia.csic.es) )
  ![mail](data:image/png;base64,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)

## Developers

 - Joan Jené ( [UDT-IA, IIIA-CSIC]((http://www.iiia.csic.es) )
  ![mail](data:image/png;base64,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)
 - Bruno Rosell i Gui ( [UDT-IA, IIIA-CSIC]((http://www.iiia.csic.es) )
  ![mail](data:image/png;base64,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)
