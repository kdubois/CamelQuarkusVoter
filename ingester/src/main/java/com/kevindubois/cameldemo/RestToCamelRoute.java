package com.kevindubois.cameldemo;

import org.apache.camel.builder.RouteBuilder;

public class RestToCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // accept post requests (in json) from a /favstack endpoint
        rest("/favstack")
                .post()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:sendRequest");

        from("direct:sendRequest")
                .routeId("sendToKafka")
                .log("Sending message to kafka topic: {{kafka.topic.name}}")
                .to("kafka:{{kafka.topic.name}}");



        // accept post requests in x-www-form-urlencoded from a /favstackxform endpoint
        rest("/favstackxform")
                .post()
                .consumes("application/x-www-form-urlencoded")
                .produces("application/json")
                .to("direct:processRequest");

        // transform to json
        from("direct:processRequest")
                .routeId("marshallToJson")
                .log("The body is ${body}")
                .marshal().json()   
                .log("message marshalled and is now ${body}")             
                .to("direct:sendRequest");

                

        restConfiguration()
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Headers",
                        "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,CustomHeader1, CustomHeader2");

    }

}

// .log("After marshalling the body is now ${body}")