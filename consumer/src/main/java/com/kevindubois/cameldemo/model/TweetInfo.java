package com.kevindubois.cameldemo.model;

import java.util.Date;

public class TweetInfo {

	private Long tweetId;
	private String text;
	private String username;
	private String language;
	private String location;
	private Integer favouriteCount;
	private Date creationDate;
	
	public TweetInfo() {}

	public TweetInfo(Long id, Long tweetId, String text, String username, String language, String location,
			Integer favouriteCount, Date creationDate) {
		this.tweetId = tweetId;
		this.text = text;
		this.username = username;
		this.language = language;
		this.location = location;
		this.creationDate = creationDate;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getFavouriteCount() {
		return favouriteCount;
	}

	public void setFavouriteCount(Integer favouriteCount) {
		this.favouriteCount = favouriteCount;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "TweetInfo [tweetId=" + tweetId + ", text=" + text + ", username=" + username + ", language="
				+ language + ", location=" + location + ", favouriteCount=" + favouriteCount + ", creationDate="
				+ creationDate + "]";
	}
	
}