package com.kevindubois.cameldemo.twitter;

import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.search.TwitterSearchComponent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TwitterRoute extends RouteBuilder {

    @ConfigProperty(name = "searchterm", defaultValue = "@kevindubois")
    String searchTerm;

    int count = 1;

    @Override
    public void configure() throws Exception {

        setTwitterConfig();

        fromF("twitter-search://%s?", searchTerm, count)
            .log(LoggingLevel.INFO, "Twitter Search Result: ${body}")
            .process(new TweetInfoProcessor())            
            .choice()                
                .when(simple("${body} ~~ 'intellij'")).setBody(simple("{\"shortname\":\"intellij\"}")).to("direct:sendToKafka")
                .when(simple("${body} ~~ 'vscode'")).setBody(simple("{\"shortname\":\"vscode\"}")).to("direct:sendToKafka")
                .when(simple("${body} ~~ 'eclipse'")).setBody(simple("{\"shortname\":\"eclipse\"}")).to("direct:sendToKafka")
                .when(simple("${body} ~~ 'openshiftdevspaces'")).setBody(simple("{\"shortname\":\"openshiftdevspaces\"}")).to("direct:sendToKafka")
                .when(simple("${body} ~~ 'vim'")).setBody(simple("{\"shortname\":\"vim\"}")).to("direct:sendToKafka")
                .otherwise().log("no match");

        from("direct:sendToKafka")
            .routeId("sendToKafka")
            .log("Sending message '${body}' to kafka topic {{kafka.topic.name}}")
            .to("kafka:{{kafka.topic.name}}");
    }

    @ConfigProperty(name = "twitter.apikey")
    String twitterApiKey;

    @ConfigProperty(name = "twitter.secret")
    String twitterSecret;

    @ConfigProperty(name = "twitter.accesstoken")
    String twitterAccessToken;

    @ConfigProperty(name = "twitter.accesstokensecret")
    String twitterAccessTokenSecret;

    private void setTwitterConfig() {
        // setup Twitter component
        TwitterSearchComponent tc = getContext().getComponent("twitter-search", TwitterSearchComponent.class);
        tc.setAccessToken(twitterAccessToken);
        tc.setAccessTokenSecret(twitterAccessTokenSecret);
        tc.setConsumerKey(twitterApiKey);
        tc.setConsumerSecret(twitterSecret);
    }

}
