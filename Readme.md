# LeoVegas Wallet

## What was done

1. Implemented a simple wallet microservice that manages credit/debit transactions on behalf of players.
2. The microservice is packaged in a docker container
3. Microservice can use postgresql or h2 as a database
4. Microservice covered by integration tests with testcontainers
5. Optimistic locking algorithm was used for debite operation

API can be viewed and tried at the link http://localhost:8080/swagger-ui/index.html

## How to run
### In docket container
This options run a docker container with  postgresql on 5432 port and after that run application on 8080 port
```
mvn clean package docker:build docker:start
```
For stop application and postgres use
```
mvn docker:stop
```
- To run postgres on a specific port use
  ```-Dpostgres.port=```
- To run application on a specific port use
  ```-Dservice.port=```

Example: 
```
mvn clean package docker:build docker:start -Dpostgres.port=9876 -Dservice.port=9870
```
### Without docker container
This options run application with H2 database. But this is required  JVM 17.
```
mvn clean package
java -jar tagrget/leovegas-wallet-0.0.1-SNAPSHOT.jar
```

## Implementation note
1. As you can see, no interfaces were used in the implementation. This is intentional, as there are no hierarchies and contracts with multiple implementations in the current simple implementation.
2. The wallet stores only whole numbers. It is assumed that the currency is defined on the player side.
3. API can be viewed and tried at the link http://localhost:8080/swagger-ui/index.html
