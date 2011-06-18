package com.rakutec.weibo.utils;

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
    private String[] options;
    private boolean dropRTAndReply;
    private boolean dropMetions;
    private boolean withGeo;

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

    public void setOptions(String[] values) {
        this.options = values;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean isDropRTAndReply() {
        return dropRTAndReply;
    }

    public boolean isDropMetions() {
        return dropMetions;
    }

    public boolean isWithGeo() {
        return withGeo;
    }

    private T2WUser() {
    }

    public void updateLatestId(Long latestId) {
        this.latestId = latestId;
        this.save();
    }

    public void save() {
        RedisHelper instance = RedisHelper.getInstance();
        Jedis j = instance.getJedis();

        j.set("id:" + this.userId + ":latestId", String.valueOf(this.latestId));
        if (this.token != null) j.set("id:" + this.userId + ":token", this.token);
        if (this.tokenSecret != null) j.set("id:" + this.userId + ":tokenSecret", this.tokenSecret);
        if (this.twitterToken != null) j.set("id:" + this.userId + ":twitter_token", this.twitterToken);
        if (this.twitterTokenSecret != null)
            j.set("id:" + this.userId + ":twitter_tokenSecret", this.twitterTokenSecret);

        String optionsKey = "id:" + this.userId + ":options";
        j.del(optionsKey);
        if (this.options != null) {
            for (String option : options) {
                log.debug("Adding " + option + " to " + optionsKey);
                j.sadd(optionsKey, option);
            }
        }
        j.sadd("twitter:ids", this.userId);

        instance.releaseJedis(j);
    }

    public void delete() {
        RedisHelper instance = RedisHelper.getInstance();
        Jedis j = instance.getJedis();

        j.del("id:" + this.userId + ":latestId");
        j.del("id:" + this.userId + ":token");
        j.del("id:" + this.userId + ":tokenSecret");
        j.del("id:" + this.userId + ":twitter_token");
        j.del("id:" + this.userId + ":twitter_tokenSecret");
        j.del("id:" + this.userId + ":options");
        j.srem("twitter:ids", this.userId);

        instance.releaseJedis(j);
    }

    public static T2WUser findOneByUser(String userId) {
        RedisHelper instance = RedisHelper.getInstance();
        Jedis j = instance.getJedis();

        T2WUser tid = new T2WUser();
        tid.userId = userId;
        String latest = j.get("id:" + tid.userId + ":latestId");
        if (latest != null) {
            tid.latestId = Long.valueOf(latest);
            tid.token = j.get("id:" + tid.userId + ":token");
            tid.tokenSecret = j.get("id:" + tid.userId + ":tokenSecret");
            tid.twitterToken = j.get("id:" + tid.userId + ":twitter_token");
            tid.twitterTokenSecret = j.get("id:" + tid.userId + ":twitter_tokenSecret");
            Set<String> options = j.smembers("id:" + tid.userId + ":options");

            tid.options = options.toArray(new String[options.size()]);
            tid.dropRTAndReply = false;
            tid.dropMetions = false;
            tid.withGeo = false;
            for (String s : options) {
                if ("drop_rt".equals(s)) tid.dropRTAndReply = true;
                if ("drop_at".equals(s)) tid.dropMetions = true;
                if ("with_geo".equals(s)) tid.withGeo = true;
            }

            log.info("Found data for @" + userId + " = " + tid.latestId);
        } else {
            tid.latestId = (long) 0;

            log.info("Data not found for @" + userId);
        }
        instance.releaseJedis(j);

        return tid;
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
                ", dropRTAndReply=" + dropRTAndReply +
                ", dropMetions=" + dropMetions +
                ", withGeo=" + withGeo +
                '}';
    }

    public boolean ready() {
        return (token != null && twitterToken != null);
    }
}
