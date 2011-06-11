package com.rakutec.weibo;

import redis.clients.jedis.Jedis;

import org.apache.log4j.Logger;

/**
 * @author Rakuraku Jyo
 */
public class TweetIDJedis {
    private static final Logger log = Logger.getLogger(TweetIDJedis.class.getName());

    public String userId;

    public Long latestId;

    public TweetIDJedis(String userId) {
        this.userId = "id:" + userId;
    }

    public void update(Long tweetId) {
        Jedis j = RedisHelper.getInstance().getJedis();
        j.set(this.userId, String.valueOf(tweetId));
        log.info("Updating latest id to " + tweetId);
    }

    public static TweetIDJedis loadTweetID(String user) {
        Jedis j = RedisHelper.getInstance().getJedis();

        TweetIDJedis tid = new TweetIDJedis(user);
        String latest = j.get(tid.userId);
        if (latest != null) {
            tid.latestId = Long.valueOf(latest);
            log.info("FOUND TID:" + tid.latestId);
        } else {
            tid.latestId = (long) 0;
            log.info("TID NOT FOUND");
        }
        return tid;
    }
}
