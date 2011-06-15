package com.rakutec.weibo;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Should rewrite this class to seperate model and static db methods
 *
 * @author Rakuraku Jyo
 */
public class TweetID {
    private static final Logger log = Logger.getLogger(TweetID.class.getName());

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

    private TweetID() {
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

    public static TweetID getUser(String userId) {
        Jedis j = RedisHelper.getInstance().getJedis();

        TweetID tid = new TweetID();
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

    public static Long getUserCount() {
        Jedis j = RedisHelper.getInstance().getJedis();
        return j.scard("twitter:ids");
    }

    public static void delete(String userId) {
        Jedis j = RedisHelper.getInstance().getJedis();
        j.del("id:" + userId + ":latestId");
        j.del("id:" + userId + ":token");
        j.del("id:" + userId + ":tokenSecret");
        j.srem("twitter:ids", userId);
    }
}
