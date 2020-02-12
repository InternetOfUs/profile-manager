# wenet-profile-manager

This project will be used to provide the API to storing and maintaining the WeNet user profile.

 - [License](LICENSE)
 - [Changes](CHANGELOG)
 - [Repository](git clone https://rosell@bitbucket.org/wenet/wenet-profile-manager.git)

## Servers

  - **Dummy server** (http://ardid.iiia.csic.es/dummy-wenet-profile-manager/)[http://ardid.iiia.csic.es/dummy-wenet-profile-manager/]
  - **Testing server** TO-DO


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

 - **PROFILE_MANAGER_DB_HOST** to define the database server host name. By default is **localhost**.
 - **PROFILE_MANAGER_DB_PORT** to define the database server port. By default is **27017**.
 - **PROFILE_MANAGER_DB_NAME** to define the database name. By default is **wenetProfileManagerDB**.
 - **PROFILE_MANAGER_DB_USER_NAME** to define the database user name. By default is **wenetProfileManager**.
 - **PROFILE_MANAGER_DB_USER_PASSWORD** to define the database user password. By default is **password**.

### Run docker image

To run a the created docker image, run the next command:

```
docker run -t -i -p 8080:8080 wenet/profile-manager
```

You can modify the database to use with the next environment properties:

 - **DATASOURCE_URL** to define the database server host name.
 - **DATASOURCE_USERNAME** to define the database server port.
 - **DATASOURCE_PASSWORD** to define the database user password.


If you want to start also a database and link both you can execute:

```
docker-compose -f src/main/docker/docker-compose.yml up -d
```

## Developing

You need to have installed Maven to run compile and run in development mode.
Also you need  a postgresql DB active, the easier way to enable it is:

```
docker-compose -f src/dev/docker/docker-compose.yml up -d
```

To launch your tests:
```
./mvnw clean test
```

To package your application:
```
./mvnw clean package
```

To run your application:
```
./mvnw clean compile exec:java
```
