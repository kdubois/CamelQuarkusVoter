package com.kevindubois.cameldemo.twitter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import twitter4j.Status;

public class TweetInfoProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Status bodyIn = (Status) exchange.getIn().getBody();
		
		exchange.getIn().setBody(bodyIn.getText());
	}

}