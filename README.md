# wenet-profile-manager

This project will be used to provide the API to storing and maintaining the WeNet user profile.

 - [License](LICENSE)
 - [Changes](CHANGELOG)
 - [API specification](https://bitbucket.org/wenet/wenet-components-documentation/src/master/sources/wenet-profile_manager-openapi.yaml) ( [Swagger UI](http://swagger.u-hopper.com/?url=https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-profile_manager-openapi.yaml) )
 - [API Examples with Postman](https://bitbucket.org/wenet/wenet-components-documentation/raw/b529541301d7dcc1f6b3fbf6afae3a18a18037f7/Postman_collections/wenet-profile_manager_api/wenet-profile-manager.postman_collection.json)
 - [Repository](https://rosell@bitbucket.org/wenet/wenet-profile-manager.git)
 - [Servers](#servers)
 - [Deploy with docker](#deploy-with-docker)
 - [Developing](#developing)
 - [Contact](#contact)

## Servers

  - **Sever for the latest API** [http://ardid.iiia.csic.es/wenet-profile-manager/latest/](http://ardid.iiia.csic.es/wenet-profile-manager/latest/)
 - **Sever for the API 0.8.0 (Dummy)** [http://ardid.iiia.csic.es/dummy-wenet-profile-manager/](http://ardid.iiia.csic.es/dummy-wenet-profile-manager/)
  - **Sever for the API 0.9.0** [http://ardid.iiia.csic.es/dev-wenet-profile-manager/](http://ardid.iiia.csic.es/dev-wenet-profile-manager/)
  - **Sever for the API 0.10.0** [http://ardid.iiia.csic.es/wenet-profile-manager/0.10.0/](http://ardid.iiia.csic.es/wenet-profile-manager/0.10.0/)
- **Sever for the API 0.11.0** [http://ardid.iiia.csic.es/wenet-profile-manager/0.11.0/](http://ardid.iiia.csic.es/wenet-profile-manager/0.11.0/)
- **Sever for the API 0.12.0** [http://ardid.iiia.csic.es/wenet-profile-manager/0.12.0/](http://ardid.iiia.csic.es/wenet-profile-manager/0.12.0/)

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

 - **API_HOST** to define the host where the API has to bind. By default is **0.0.0.0**.
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

After that you can compile the source, pass the tests and calculate the test coverage with:

```
./mvnw clean install
```

This process generate the next files:

 - The OpenAPI description of the web services at **target/classes/wenet-profile_manager-openapi.yml**
 - The execution java package at **target/wenet-profile-manager-VERSION.jar** where **VERSION** is the version of the software.
 - The necessary execution dependencies at **target/lib**.


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

 - [Nardine Osman](http://www.iiia.csic.es/~nardine/) ( [IIIA-CSIC](http://www.iiia.csic.es) ) nardine (at) iiia.csic.es
 - [Carles Sierra](http://www.iiia.csic.es/~sierra/) ( [IIIA-CSIC](http://www.iiia.csic.es) ) sierra (at) iiia.csic.es

## Developers

 - Joan Jené ( [UDT-IA, IIIA-CSIC](http://www.iiia.csic.es) ) jjene (at) iiia.csic.es
 - Bruno Rosell i Gui ( [UDT-IA, IIIA-CSIC](http://www.iiia.csic.es) ) rosell (at) iiia.csic.es
