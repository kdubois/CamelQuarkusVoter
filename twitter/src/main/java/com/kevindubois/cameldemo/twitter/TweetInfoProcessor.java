package com.kevindubois.cameldemo.twitter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.kevindubois.cameldemo.model.TweetInfo;
import twitter4j.Status;

public class TweetInfoProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Status bodyIn = (Status) exchange.getIn().getBody();
		
		TweetInfo tweetInfo = new TweetInfo();
		
		tweetInfo.setTweetId(bodyIn.getId());
		tweetInfo.setText(bodyIn.getText());
		
		if (bodyIn.getUser() != null) {
			tweetInfo.setUsername(bodyIn.getUser().getName());
			tweetInfo.setLanguage(bodyIn.getUser().getLang());
			tweetInfo.setLocation(bodyIn.getUser().getLocation());
		}

		tweetInfo.setFavouriteCount(bodyIn.getFavoriteCount());
		tweetInfo.setCreationDate(bodyIn.getCreatedAt());
		
		exchange.getIn().setBody(tweetInfo.getText());
	}

}