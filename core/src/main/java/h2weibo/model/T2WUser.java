/*
 * (The MIT License)
 *
 * Copyright (c) 2011 Rakuraku Jyo <jyo.rakuraku@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package h2weibo.model;

import org.apache.log4j.Logger;

import java.util.Set;

/**
 * Should rewrite this class to seperate model and static db methods
 *
 * @author Rakuraku Jyo
 */
public class T2WUser {
    private String userId;
    private Long latestId;
    private String token;
    private String tokenSecret;
    private String twitterTokenSecret;
    private String twitterToken;
    private Set<String> options;


    /**
     * Twitter user ID
     *
     * @return User ID in Twitter
     */
    public String getUserId() {
        return userId;
    }

    public Long getLatestId() {
        return latestId;
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLatestId(Long latestId) {
        this.latestId = latestId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public void setTwitterTokenSecret(String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    public String getTwitterTokenSecret() {
        return twitterTokenSecret;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public boolean isDropRetweets() {
        return options != null && options.contains("drop_rt");
    }

    public boolean isDropMentions() {
        return options != null && options.contains("drop_at");
    }

    public boolean isWithGeo() {
        return options != null && options.contains("with_geo");
    }

    public boolean isNoImage() {
        return options != null && options.contains("no_image");
    }

    public Set<String> getOptions() {
        return options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }

    public T2WUser() {
    }

    @Override
    public String toString() {
        return "T2WUser{" +
                "userId='" + userId + '\'' +
                ", latestId=" + latestId +
                ", token='" + token + '\'' +
                ", tokenSecret='" + tokenSecret + '\'' +
                ", twitterTokenSecret='" + twitterTokenSecret + '\'' +
                ", twitterToken='" + twitterToken + '\'' +
                ", options=" + options +
                '}';
    }

    public boolean ready() {
        return (token != null && twitterToken != null);
    }
}
