package com.kevindubois.cameldemo;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.search.TwitterSearchComponent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ConsumeFromTwitterRoute extends RouteBuilder {
    @ConfigProperty(name = "twitter.apikey")
    String twitterApiKey;

    @ConfigProperty(name = "twitter.secret")
    String twitterSecret;

    @ConfigProperty(name = "twitter.accesstoken")
    String twitterAccessToken;

    @ConfigProperty(name = "twitter.accesstokensecret")
    String twitterAccessTokenSecret;

    @ConfigProperty(name="searchterm", defaultValue = "@micronautfw")
    String searchTerm;
    
    @Override
    public void configure() throws Exception {
        // setTwitterConfig();

        // fromF("twitter-search://%s?", searchTerm)
        // .log("Search Result: ${body}")
        // .process(new TweetInfoProcessor())
        // .log("${body}");
        
    }

    private void setTwitterConfig() {
        // setup Twitter component
        TwitterSearchComponent tc = getContext().getComponent("twitter-search", TwitterSearchComponent.class);
        tc.setAccessToken(twitterAccessToken);
        tc.setAccessTokenSecret(twitterAccessTokenSecret);
        tc.setConsumerKey(twitterApiKey);
        tc.setConsumerSecret(twitterSecret);
    }

    
    
}
