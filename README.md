# GraphQL Fullstack Samples using Spring GraphQL & React

GraphQL Fullstack Samples using Spring GraphQL & React

<img src="https://github.com/susimsek/spring-graphql-samples/blob/main/images/introduction.png" alt="Spring Boot Graphql Samples" width="100%" height="100%"/> 

# GraphQL

GraphQL is a query language and server-side runtime for application programming interfaces (APIs) that prioritizes
giving clients exactly the data they request and no more.
GraphQL is designed to make APIs fast, flexible, and developer-friendly.
It can even be deployed within an integrated development environment (IDE) known as GraphiQL.
As an alternative to REST, GraphQL lets developers construct requests that pull data from multiple data sources in a
single API call.

## Prerequisites

* Java 17
* GraalVM 22.3+
* Kotlin
* Maven 3.x
* Kafka


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

## Prerequisites for Frontend

* Nodejs 16+

### Run the app

You can install the dependencies by typing the following command

```sh
npm run dev
```

You can run the react app(accessible on http://localhost:3000) by typing the following command

```sh
npm start
```

## Rsocket


```sh
rsc --stream --route=subscriptions --dataMimeType="application/graphql+json" --data='{"query":"subscription { postAdded { id, title, content } }" }' --debug tcp://localhost:8079 --authBearer ${TOKEN} 
```

## Deployment with Docker Compose

### Prerequisites for Docker Compose Deployment

* Docker
* Docker Compose

You can deploy app by running the following bash command


```sh
 sudo chmod +x deploy.sh
```

```sh
 ./deploy.sh -d
```

You can uninstall app the following bash command

```sh
 ./deploy.sh -d -r
```

The GraphQL App be accessed from the link below.  
http://127.0.0.1:9091


## Deployment Kubernetes with Helm

### Prerequisites for Kubernetes Deployment

* Kubernetes
* Helm

You can deploy app by running the following bash command

```sh
 sudo chmod +x deploy.sh
```

```sh
 ./deploy.sh -k
```

You can uninstall app the following bash command

```sh
 ./deploy.sh -k -r
```

You can upgrade the App (if you have made any changes to the generated manifests) by running the
following bash command

```sh
 ./deploy.sh -u
```

# Used Technologies
## Backend Side
* Java 17
* Kotlin 
* GraalVM
* Upx
* Docker
* Docker Compose
* Kubernetes
* Helm
* Sonarqube
* Snyk
* CircleCI
* Detekt
* Mongodb
* Kafka
* Spring Boot 3.x
* Spring Boot GraphQL
* Spring Boot Webflux
* Spring Boot Oauth2 Resource Server
* Spring Boot Security
* Spring Boot Data Mongodb Reactive
* Spring Boot Validation
* Spring Boot Actuator
* Spring Boot RSocket
* Spring Boot Configuration Processor
* Graphql Java Extended Scalars
* Graphql Java Extended Validation
* Mapstruct
* Recaptcha
* ChatGPT
* Jwt with Httponly Cookie
* Internalization(I18N)

## Frontend Side
* React
* Next.js
* Typescript
* Apollo Client
* Graphql
* Graphql Ws
* Graphql Codegen
* Next I18next
* React Google Recaptcha v3
* React Hook Form
* React Bootstrap
* Fontawesome
* React Cookie
* React Country Flag
* React Quill
* Yup