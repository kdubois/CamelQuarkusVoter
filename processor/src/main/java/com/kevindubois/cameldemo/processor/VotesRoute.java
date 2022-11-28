package com.kevindubois.cameldemo.processor;

import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;


@ApplicationScoped
public class VotesRoute extends RouteBuilder{

    @Override
    public void configure() throws Exception {

        from("kafka:{{kafka.topic.name}}")
            .routeId("FromKafkaToDB")            
            .log("Received message from Kafka: ${body}")
            .unmarshal().json(JsonLibrary.Jackson, Response.class)     
            .transacted()           
            .log("body before bean is ${body.getShortname}")
            .to("bean:vote?method=updateCounter(${body.getShortname})");
    }
}

