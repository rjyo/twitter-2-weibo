package com.rakutec.weibo;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class TweetIDJedis {
    private static final Logger log = Logger.getLogger(TweetIDJedis.class.getName());

    private String userId;
    private Long latestId;
    private String token;
    private String tokenSecret;

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

    private TweetIDJedis() {
    }

    public void updateLatestId(Long latestId) {
        this.latestId = latestId;
        this.save();
    }

    public void save() {
        Jedis j = RedisHelper.getInstance().getJedis();
        j.set("id:" + this.userId + ":latestId", String.valueOf(this.latestId));
        j.set("id:" + this.userId + ":token", this.token);
        j.set("id:" + this.userId + ":tokenSecret", this.tokenSecret);

        j.sadd("twitter:ids", this.userId);
    }

    public static TweetIDJedis getUser(String userId) {
        Jedis j = RedisHelper.getInstance().getJedis();

        TweetIDJedis tid = new TweetIDJedis();
        tid.userId = userId;
        String latest = j.get("id:" + tid.userId + ":latestId");
        if (latest != null) {
            tid.latestId = Long.valueOf(latest);
            tid.token = j.get("id:" + tid.userId + ":token");
            tid.tokenSecret = j.get("id:" + tid.userId + ":tokenSecret");
            log.info("Found data for @" + userId + " = " + tid.latestId);
        } else {
            tid.latestId = (long) 0;
            log.info("Data not found for @" + userId);
        }
        return tid;
    }

    public static Set getAuthorizedIds() {
        Jedis j = RedisHelper.getInstance().getJedis();
        return j.smembers("twitter:ids");
    }
}
