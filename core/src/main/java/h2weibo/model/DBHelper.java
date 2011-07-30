package h2weibo.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.lang.reflect.Type;
import java.util.*;


public class DBHelper {
    private static final Logger log = Logger.getLogger(DBHelper.class.getName());
    private static final String USER_SET_KEY = "twitter:ids";
    private static final String QUEUE_KEY = "t2w:queue";
    private static final String USER_MAP_KEY = "t2w:id_map";

    private Jedis jedis;
//    private JedisPool jedisPool;


    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public Set<String> getAuthorizedIds() {
        return jedis.smembers(USER_SET_KEY);
    }

    public Long getUserCount() {
        return jedis.scard(USER_SET_KEY);
    }

    public boolean isUser(String userId) {
        return jedis.sismember(USER_SET_KEY, userId);
    }

    public T2WUser findOneByUser(String userId) {
        T2WUser tid = new T2WUser();
        tid.setUserId(userId);
        String latest = jedis.get("id:" + tid.getUserId() + ":latestId");
        if (latest != null) {
            tid.setLatestId(Long.valueOf(latest));
            tid.setToken(jedis.get("id:" + tid.getUserId() + ":token"));
            tid.setTokenSecret(jedis.get("id:" + tid.getUserId() + ":tokenSecret"));
            tid.setTwitterToken(jedis.get("id:" + tid.getUserId() + ":twitter_token"));
            tid.setTwitterTokenSecret(jedis.get("id:" + tid.getUserId() + ":twitter_tokenSecret"));
            tid.setOptions(jedis.smembers("id:" + tid.getUserId() + ":options"));

            log.debug("Found data for @" + userId + " = " + tid.getLatestId());
        } else {
            tid.setLatestId(0l);

            log.error("Data not found for @" + userId);
        }

        tid.setHelper(this);

        return tid;
    }


    public Map<String, String> getUserMap() {
        Map<String, String> map = jedis.hgetAll(USER_MAP_KEY);
        if (map == null) {
            map = new HashMap<String, String>(0);
        }

        return map;
    }

    public void createUserMap() {
        Set<String> userIds = getAuthorizedIds();

        Weibo w = new Weibo();
        for (String userId : userIds) {
            T2WUser user = findOneByUser(userId);

            if (user.ready()) {
                w.setToken(user.getToken(), user.getTokenSecret());

                try {
                    User weiboUser = w.verifyCredentials();
                    this.setWeiboId(user.getUserId(), weiboUser.getName());
                    log.info("Get Weibo credentials for @" + userId + " is @" + weiboUser.getName());
                } catch (WeiboException e) {
                    if (e.getStatusCode() == 401) {
                        user.setToken(null);
                        user.setTokenSecret(null);
                    }
                    log.error("Failed to find Weibo ID for @" + user.getUserId() + ", removing weibo tokens.");
                } catch (Exception e) {
                    log.error("Failed to find Weibo ID for @" + user.getUserId(), e);
                }
            }

        }
    }

    public void setWeiboId(String twitterId, String weiboId) {
        jedis.hset(USER_MAP_KEY, twitterId.toLowerCase(), weiboId);
    }

    public String getWeiboId(String twitterId) {
        return jedis.hget(USER_MAP_KEY, twitterId);
    }

    public Map<String, String> getMappings() {
        return jedis.hgetAll(USER_MAP_KEY);
    }

    public String dump() {
        Set<String> users = getAuthorizedIds();

        Gson gson = new Gson();
        ArrayList<T2WUser> list = new ArrayList<T2WUser>(users.size());

        for (String user : users) {
            list.add(findOneByUser(user));
        }

        return gson.toJson(list);
    }

    public void restore(String data) {
        Gson gson = new Gson();
        Type listType = new TypeToken<Collection<T2WUser>>() {
        }.getType();
        ArrayList<T2WUser> list = gson.fromJson(data, listType);

        for (T2WUser user : list) {
            T2WUser u = findOneByUser(user.getUserId());
            if (!u.ready()) { // not existed
                user.save();
                log.info("Restoring " + user.getUserId() + " ...");
            }
        }
    }

    public void clearQueue() {
        jedis.del(QUEUE_KEY);
    }

    public void queue(T2WUser user) {
        String userId = user.getUserId();
        log.debug("Queue " + userId + " to sync.");
        jedis.lpush(QUEUE_KEY, userId);
    }

    public T2WUser pop() {
        String userId = jedis.rpop(QUEUE_KEY);

        T2WUser user;
        if (userId != null) {
            log.debug("Poped " + userId + " from queue.");
            user = findOneByUser(userId);
        } else {
            user = null;
        }

        return user;
    }

    public long getQueueSize() {
        return jedis.llen(QUEUE_KEY);
    }

    public DBHelper(Jedis jedis) {
        this.jedis = jedis;
    }
}
