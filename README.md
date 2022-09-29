Votes Processor
========================

This project illustrates how Camel Quarkus applications can leverage Quarkus native builds and interact seamlessly with Apache Kafka and Knative Serving

## Start the application

The application is composed of 3 applications communicating through Rest and Kafka and consuming a database

The first (UI) application features a form to submit your vote for your favorite Java stack.  
It sends the result to a second application with a rest Camel route which is designed to scale out rapidly using Knative and which will in turn forward the votes to a Kafka topic.
The 3rd application ingests the Kafka messages and updates the database accordingly. (eg. if someone voted 'quarkus', the counter for quarkus would be incremented by 1)
The first application also hosts the results page.

They can be started in dev mode using:

```bash
mvn -f ui quarkus:dev
```

and in another terminal:

```bash
mvn -f consumer quarkus:dev
```

and in yet another terminal:

```bash
mvn -f processor quarkus:dev
```

_NOTE_: Quarkus Dev Services starts a Kafka broker AND Postgresql database for you automatically.

Then, open your browser at `http://localhost:8080/result.html`.
You can send requests and observe the votes table changing (asynchronously).

## Anatomy

The application is composed of the following components:

#### Consumer

The _consumer_ application receives requests from the user (via HTTP) and forwards the requests to the Kafka broker.
The main component of the application:

* `ConsumeFromRestRoute` : Camel route that receives a rest call and forwards it on to a Kafka broker

#### Processor

The _processor_ application receives the vote requests from Kafka, processes them, and writes results into the `votesdb` Postgres DB table.
The application has the following Camel Routes:

* `processor/VotesRoute` consumes messages from a kafka topic (in json format), extracts the value of 'stackname' and increments the counter of the java stack that matches with this stackname.
* `RestRoute` returns data from the votes table in json format

The connection to Kafka should be configured in the kubefiles/configmap.yaml file

## Running in native

You can compile the respective applications into a native binary using:

```bash
mvn package -Pnative
```

As you are running in _prod_ mode, you need a Kafka cluster.

## Running On Openshift

1. Create a new openshift project 'cameldemo' (if you use a different name, make sure to update the respective application.properties files)
1. Install the Openshift Serverless (Knative) Operator
1. Install Openshift Serverless "Knative Serving" component
1. Install AMQ Streams (Kafka) Operator
1. Using the kubefiles/configmap-example.yaml and kubefiles/secrets-example.yaml as an example, create a secrets.yaml and configmap.yaml and apply the yamls (eg. kubectl apply -f kubefiles/secrets.yaml -f kubefiles/configmap.yaml)
1. In the AMQ Streams operator, install the kafka cluster component.  Make sure the cluster name matches up with the cluster service name in the kubefiles/configmap.yaml file.
1. Install a Postgresql Database (you can use the built in Openshift template).  Name the db 'votedb' and set the username and password to what you have configured in the kubefiles/secrets.yaml.
1. Build and deploy the applications.  If you're logged in to Openshift in your terminal, you can run `mvn clean package -Pnative -Dquarkus.kubernetes.deploy` and Quarkus will take care of building native binaries and deploying them to Openshift and it will even configure the wiring to use the secrets and configmaps for you.   Otherwise you may also use the kubefiles/applications/* to deploy the applications (eg. using ArgoCD)
