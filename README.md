# Votes Processor

This demo project illustrates how Camel Quarkus applications can leverage Quarkus native builds and interact seamlessly with Apache Kafka and Knative Serving

Watch the demo in action during my talk at Devoxx: <https://youtu.be/FBWgbhp8FG8>

## Application Architecture

The application is composed of 4 applications that communicate through Rest and Kafka and consume a database

![Architecture](architecture.png)

The UI application features a, well, UI, that shows the results of a poll and a form to vote for your favorite Java stack.
When you vote, a REST POST event gets sent to the 'ingester' app, which will translate the result and add it to a Kafka topic.  This app is designed to scale out rapidly with Knative so that it can handle bursts of requests.  
The 3rd application (processor) consumes the Kafka messages at its own pace and updates the database accordingly. (eg. if someone voted 'quarkus', the counter for quarkus would be incremented by 1)
The 'retriever' application (currently embedded in the processor app) has a REST GET endpoint to get the results from the DB.  

## Running the Application locally in Dev Mode

You can run the entire application on your local machine in Quarkus dev mode.  As long as you have Docker/Podman running on your machine, Quarkus will take care of spinning up Dev Services (basically a containerized mock) for your database and Kafka, so all you need to worry about is running the applications.  Nice huh? :)

Either start each process in a separate terminal as shown below; or if you want to just start the entire thing in one go, you can run `./devmode.sh`

```bash
mvn -f ui quarkus:dev
```

and in another terminal:

```bash
mvn -f ingester quarkus:dev
```

and in yet another terminal:

```bash
mvn -f processor quarkus:dev
```

Then, open your browser at `http://localhost:8080`.
You can send requests and observe the votes table changing (asynchronously).

## Anatomy

The application is composed of the following components:

### UI

The _ui_ application displays a list of java stacks/frameworks that you can vote for by clicking the respective button next to it.  This action calls the _ingester_ app.  The page also displays a bar chart of the results so far.  The app is built with Quarkus Qute templating and some crappy javascript/jquery code :P

### Ingester

The _ingester_ Camel Quarkus application receives requests from the user (via HTTP) and forwards the requests to the Kafka broker.
The main component of the application:

* `RestToCamelRoute` : Camel route that receives a rest call and forwards it on to a Kafka broker

### Processor

The _processor_ Camel Quarkus application receives the vote requests from Kafka, processes them, and writes results into the `votesdb` Postgres DB table.  As of right now it also returns the results through a /getresults endpoint.
The application has the following Camel Routes:

* `processor/VotesRoute` consumes messages from a kafka topic (in json format), extracts the value of 'shortname' and increments the counter of the java stack that matches with this shortname.
* `RestRoute` returns data from the votes table in json format

## Compiling to native binary

You can compile the respective applications into a native binary using:

```bash
mvn package -Dnative
```

## Deploy to Openshift

### Prepare the environment

NOTE: if you don't have an Openshift environment available, you can also get a
developer sandbox at https://developers.openshift.com/sandbox.
This environment already has Openshift Serverless installed.
You won't have access to the Kafka operator, but instead you can install an
ephemeral kafka cluster running in a single pod by applying the kafka-no-keeper.yaml file (`oc apply -f kubefiles/kafka-no-keeper.yaml`). After that you can proceed with step 6.

1. Create a new openshift project 'cameldemo' (if you use a different name, make sure to update the respective application.properties files)
1. Install the Openshift Serverless (Knative) Operator
1. Install Openshift Serverless "Knative Serving" component
1. Install AMQ Streams (Kafka) Operator
1. In the AMQ Streams operator, install the kafka cluster component. You can use the default 'my-cluster' name for the cluster.
1. Install a Postgresql Database (you can use the built in Openshift template, or the kubefiles/postgresql.yaml file).  Name the db 'votedb'.
1. Using the `kubefiles/config/configmap-example.yaml` an example, modify it to match your environment and apply the yaml (eg. `kubectl apply -f kubefiles/config/configmap-example.yaml`). The ingester.url and processor.url should be set to the route of the components you will be deploying in the next step. It's a bit of a chicken-and-egg problem, but feel free to update these values and re-apply the yaml after you've deployed the services below and restart the UI pod if needed.
1. If you did not use the Openshift template to deploy the database, you may need to create a postgresql secret containing the DB credentials. In that case use the kubefiles/config/secrets-example.yaml as an example the yaml (eg. `kubectl apply -f kubefiles/config/secrets-example.yaml -n cameldemo`).

## Option 1 (easiest): Deploy existing images

Simply run the following command with the `oc` or `kubectl` cli:

```bash
kubectl apply -f kubefiles/processor.knative.yaml -f kubefiles/ingester.knative.yaml -f kubefiles/ui.knative.yaml -n cameldemo
```

## Option 2: Build the application locally and deploy with Quarkus

1. Build and deploy the applications.  If you're logged in to Openshift in your terminal, you can run:

```bash
quarkus build -Dquarkus.openshift.deploy
```

and Quarkus will take care of building the application and deploying it to Openshift and it will even configure the wiring to use the secrets and configmaps for you.
If you want to deploy a native binary, you can add the -Dnative flag to build a native binary
(If you're not on Linux, you will also likely need to add ` --no-tests -Dquarkus.native.container-build=true`)

## Option 3: (Linux Only) Compile to native binaries, build container images and push to registry, then deploy to Openshift/Kubernetes

You can also let Quarkus build & push native container images using the Quarkus CLI (Make sure to update the --group value with your Quay user!).

```bash
quarkus image push --also-build --native --registry=quay.io --group=yourquayuser
```

Then apply the yaml files (make sure to update the images in your yamls from `kevindubois` to YOUR quay user!)

```bash
    kubectl apply -f kubefiles/processor.knative.yaml -f kubefiles/ingester.knative.yaml -f kubefiles/ui.knative.yaml -n cameldemo
```

## Option 4: Deploy images with kn service:

```bash
kn service create cameldemo-processor --env-from cm:appconfig --env-from secret:postgresql --image=quay.io/kevindubois/cameldemo-processor --force
kn service create cameldemo-ingester --env-from cm:appconfig --env-from secret:postgresql --image=quay.io/kevindubois/cameldemo-ingester --force
kn service create cameldemo-ui --env-from cm:appconfig --env-from secret:postgresql --image=quay.io/kevindubois/cameldemo-ui --force
```

