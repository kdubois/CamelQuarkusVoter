#!/bin/bash
trap 'kill 0' SIGINT; mvn -f ./ui clean quarkus:dev & mvn -f ./processor clean quarkus:dev & mvn -f ./ingester clean quarkus:dev ; wait