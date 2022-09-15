package com.kevindubois.cameldemo;
import org.apache.camel.builder.RouteBuilder;

public class VoteConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // accept post requests from a /favstack endpoint
        rest("/favstackxform")  
            .post()    
            .consumes("application/x-www-form-urlencoded")                     
            .produces("application/json")                  
            .to("direct:processRequest");
        
         // accept post requests (in json) from a /favstack endpoint
         rest("/favstack")  
         .post()    
         .consumes("application/json")                     
         .produces("application/json")                  
         .to("direct:sendRequest");

        // transform to json and send to kafka
        from("direct:processRequest")
            .log("The body is ${body}")
            .marshal().json()
            .log("After Marshalling the body is now ${body}")
            .to("direct:sendRequest");
        
        from("direct:sendRequest")
            .to("kafka:{{kafka.topic.name}}");

        // optional, just reads the message back from the kafka topic. 
        from("kafka:{{kafka.topic.name}}")
             .routeId("ReadKafka")
             .log("Received message from Kafka: \"${body}\"");
    }

}
