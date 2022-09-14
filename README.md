Votes Processor
========================

This project illustrates how Camel Quarkus applications can interact with Apache Kafka and Knative Serving

## Start the application

The application is composed of 3 applications communicating through Rest and Kafka and consuming a database

The first (UI) application features a form to submit your vote for your favorite Java stack.  
It sends the result to a second application with a rest Camel route which is designed to scale out rapidly using Knative and which will in turn forward the votes to a Kafka topic.
The 3rd application ingests the Kafka messages and updates the database accordingly. (eg. if someone voted 'quarkus', the counter for quarkus would be incremented by 1)
The first application also hosts the results page.

They can be started in dev mode using:

```bash
mvn -f results quarkus:dev
```

and in another terminal:

```bash
mvn -f producer quarkus:dev
```

and in yet another terminal:

```bash
mvn -f processor quarkus:dev
```

_NOTE_: Quarkus Dev Services starts a Kafka broker AND Postgresql database for you automatically.

Then, open your browser at `http://localhost:8080/result.html`.
You can send requests and observe the votes table changin (asynchronously).

## Anatomy

The application is composed of the following components:

#### Producer

The _producer_ application receives requests from the user (via HTTP) and forwards the requests to the Kafka broker.
The main component of the application:

* `VoteConsumerRoute` : Camel route that receives a rest call and forwards it on to a Kafka broker

#### Processor

The _processor_ application receives the vote requests from Kafka, processes them, and writes results into the `votes` Postgres DB table.
The application has one main Camel Route:

* `VotesRoute` consumes messages from a kafka topic (in json format), extracts the value of 'stackname' and increments the counter of the java stack that matches with this stackname.

The connection to Kafka is configured in the `src/main/resources/application.properties` file.

## Running the application in Docker

To run the application in Docker, first make sure that both services are built:

```bash
mvn package
```

Then launch Docker Compose:

```bash
docker-compose up
```

This will create a single-node Kafka cluster and launch both applications.

## Running in native

You can compile the application into a native binary using:

```bash
mvn package -Dnative
```

As you are running in _prod_ mode, you need a Kafka cluster.

If you have Docker installed, you can simply run:

```bash
export QUARKUS_MODE=native
docker-compose up --build
```

Alternatively, you can follow the instructions from the [Apache Kafka web site](https://kafka.apache.org/quickstart).

Then run both applications respectively with:

```bash
./producer/target/cameldemo-producer-1.0.0-SNAPSHOT-runner
```

and in another terminal:

```bash
./processor/target/cameldemo-processor-1.0.0-SNAPSHOT-runner
```


### Running On Openshift

Install Openshift Serverless Operator
Install Openshift Serverless "Serving"
