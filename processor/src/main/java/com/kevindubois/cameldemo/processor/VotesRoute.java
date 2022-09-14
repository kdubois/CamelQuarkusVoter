package com.kevindubois.cameldemo.processor;

import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;


@ApplicationScoped
public class VotesRoute extends RouteBuilder{

    @Override
    public void configure() throws Exception {

        // produces messages to kafka
        // from("timer:foo?period={{timer.period}}&delay={{timer.delay}}")
        //         .routeId("FromTimer2Kafka")
        //         .setBody().simple("Quarkus")
        //         .to("kafka:{{kafka.topic.name}}")
        //         .log("Message sent correctly to the topic! : \"${body}\" "); 


        from("kafka:{{kafka.topic.name}}")
            .routeId("FromKafkaToDB")            
            .log("Received message from Kafka: \"${body}\"")
            .unmarshal().json(JsonLibrary.Jackson, Response.class)     
            .transacted()            
            .to("bean:vote?method=updateCounter(${body.getStackname})");
    }    
}

