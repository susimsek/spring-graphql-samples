# Spring Graphql Samples

Spring Graphql Reactive Samples With Jwt Authentication

<img src="https://github.com/susimsek/spring-graphql-samples/blob/main/images/introduction.png" alt="Spring Boot Graphql Samples" width="100%" height="100%"/> 

# Graphql

GraphQL is a query language and server-side runtime for application programming interfaces (APIs) that prioritizes
giving clients exactly the data they request and no more.
GraphQL is designed to make APIs fast, flexible, and developer-friendly.
It can even be deployed within an integrated development environment (IDE) known as GraphiQL.
As an alternative to REST, GraphQL lets developers construct requests that pull data from multiple data sources in a
single API call.

## Prerequisites

* Java 17
* Kotlin
* Maven 3.x


## Build

You can install the dependencies and build by typing the following command

```sh
mvn clean install
```

To compile as native, run the following goal:

```
$ mvn native:compile -Pnative
```

Then, you can run the app as follows:

```
$ target/spring-graphql-samples
```

## Testing

You can run application's tests by typing the following command

```
mvn verify
```


## Code Quality

You can test code quality locally via sonarqube by typing the following command

```sh
mvn -Psonar compile initialize sonar:sonar
```

## Detekt

Detekt a static code analysis tool for the Kotlin programming language

You can run detekt by typing the following command

```sh
mvn antrun:run@detekt
```

## Docker

You can also fully dockerize  the sample applications. To achieve this, first build a docker image of your app.
The docker image of sample app can be built as follows:


```sh
mvn -Pjib verify jib:dockerBuild
```

to build native image
```sh
mvn -Pnative spring-boot:build-image
```

## Rsocket


```sh
rsc --stream --route=subscriptions --dataMimeType="application/graphql+json" --data='{"query":"subscription { postAdded { id, title, content } }" }' --debug tcp://localhost:8079 --authBearer ${TOKEN} 
```

```sh
rsc --request --route=graphql --dataMimeType="application/graphql+json" --data='{"query":"{ post(id: \"632c8028feb9e053546a88f2\") { id, title } }" }' --debug tcp://localhost:8079  --authBearer ${TOKEN} 
```
