package com.kevindubois.cameldemo.processor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.camel.builder.RouteBuilder;

import com.kevindubois.cameldemo.model.Vote;



@ApplicationScoped
public class VotesRoute extends RouteBuilder{

    @Override
    public void configure() throws Exception {

        // produces messages to kafka
        from("timer:foo?period={{timer.period}}&delay={{timer.delay}}")
                .routeId("FromTimer2Kafka")
                .setBody().simple("Quarkus")
                .to("kafka:{{kafka.topic.name}}")
                .log("Message sent correctly to the topic! : \"${body}\" "); 


        from("kafka:{{kafka.topic.name}}")
            .routeId("FromKafkaToDB")
            .log("Received message from Kafka: \"${body}\"")
            .transacted()
            .to("bean:vote?method=updateCounter(${body})");
    }
}

