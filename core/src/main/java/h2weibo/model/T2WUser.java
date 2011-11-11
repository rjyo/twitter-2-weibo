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
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Should rewrite this class to seperate model and static db methods
 *
 * @author Rakuraku Jyo
 */
public class T2WUser {
    private static final Logger log = Logger.getLogger(T2WUser.class.getName());

    private String userId;
    private Long latestId;
    private String token;
    private String tokenSecret;
    private String twitterTokenSecret;
    private String twitterToken;
    private Set<String> options;

    private transient DBHelper helper;

    public DBHelper getHelper() {
        return helper;
    }

    public void setHelper(DBHelper helper) {
        this.helper = helper;
    }

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

    public String getWeiboId() {
        return helper.getWeiboId(this.userId);
    }

    public void setWeiboId(String weiboId) {
        helper.setWeiboId(this.userId, weiboId);
    }

    public T2WUser() {
    }

    public void save() {
        Jedis j = helper.getJedis();

        j.set("id:" + this.userId + ":latestId", String.valueOf(this.latestId));
        if (this.token != null) j.set("id:" + this.userId + ":token", this.token);
        if (this.tokenSecret != null) j.set("id:" + this.userId + ":tokenSecret", this.tokenSecret);
        if (this.twitterToken != null) j.set("id:" + this.userId + ":twitter_token", this.twitterToken);
        if (this.twitterTokenSecret != null)
            j.set("id:" + this.userId + ":twitter_tokenSecret", this.twitterTokenSecret);

        String optionsKey = "id:" + this.userId + ":options";
        j.del(optionsKey);

        if (options != null) {
            for (String option : options) {
                log.debug("Adding " + option + " to " + optionsKey);
                j.sadd(optionsKey, option);
            }
        }
        j.sadd("twitter:ids", this.userId);
    }

    public void delete() {
        Jedis j = helper.getJedis();

        j.del("id:" + this.userId + ":latestId");
        j.del("id:" + this.userId + ":token");
        j.del("id:" + this.userId + ":tokenSecret");
        j.del("id:" + this.userId + ":twitter_token");
        j.del("id:" + this.userId + ":twitter_tokenSecret");
        j.del("id:" + this.userId + ":options");
        j.srem("twitter:ids", this.userId);
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
