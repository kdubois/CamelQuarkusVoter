package com.kevindubois.cameldemo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;


public class RestRoute extends RouteBuilder{

    @Override
    public void configure() throws Exception {

        restConfiguration()
        .enableCORS(true)
        .corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,CustomHeader1, CustomHeader2")
        .bindingMode(RestBindingMode.off);
        
        
        rest("/getresults")
        .get()
        .to("direct:getvotes");

        from("direct:getvotes")
        .streamCaching()
        .transacted()
        .bean("vote", "orderedList()")        
        .marshal().json()
        .log("${body}")
        .convertBodyTo(String.class);      
        
    }
    
}
