package h2weibo.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.lang.reflect.Type;
import java.util.*;


public class RedisHelper {
    private static final Logger log = Logger.getLogger(RedisHelper.class.getName());
    private static final String USER_SET_KEY = "twitter:ids";
    private static final String QUEUE_KEY = "t2w:queue";
    private static final String USER_MAP_KEY = "t2w:id_map";

    private static RedisHelper ourInstance = new RedisHelper();
    private JedisPool jedisPool;

    public Set<String> getAuthorizedIds() {
        Jedis jedis = getJedis();
        Set<String> set = jedis.smembers(USER_SET_KEY);
        jedisPool.returnResource(jedis);
        return set;
    }

    public Long getUserCount() {
        Jedis jedis = getJedis();
        Long scard = jedis.scard(USER_SET_KEY);
        jedisPool.returnResource(jedis);
        return scard;
    }

    public boolean isUser(String user) {
        Jedis jedis = getJedis();
        Boolean ismember = jedis.sismember(USER_SET_KEY, user);
        jedisPool.returnResource(jedis);
        return ismember;
    }

    public Map<String, String> getUserMap() {
        Jedis jedis = getJedis();

        Map<String, String> map = jedis.hgetAll(USER_MAP_KEY);
        if (map == null) {
            map = new HashMap<String, String>(0);
        }

        jedisPool.returnResource(jedis);

        return map;
    }

    public void createUserMap() {
        Set<String> userIds = getAuthorizedIds();

        Weibo w = new Weibo();
        for (String userId : userIds) {
            T2WUser user = T2WUser.findOneByUser(userId);

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
        Jedis jedis = getJedis();
        jedis.hset(USER_MAP_KEY, twitterId, weiboId);
        jedisPool.returnResource(jedis);
    }

    public String getWeiboId(String twitterId) {
        Jedis jedis = getJedis();
        String weiboId = jedis.hget(USER_MAP_KEY, twitterId);
        jedisPool.returnResource(jedis);
        return weiboId;
    }

    public Map<String, String> getMappings() {
        Jedis jedis = getJedis();
        Map<String, String> map = jedis.hgetAll(USER_MAP_KEY);
        jedisPool.returnResource(jedis);
        return map;
    }

    public String dump() {
        Jedis jedis = getJedis();

        Set<String> users = getAuthorizedIds();

        Gson gson = new Gson();
        ArrayList<T2WUser> list = new ArrayList<T2WUser>(users.size());

        for (String user : users) {
            list.add(T2WUser.findOneByUser(user));
        }

        jedisPool.returnResource(jedis);
        return gson.toJson(list);
    }

    public void restore(String data) {
        Gson gson = new Gson();
        Type listType = new TypeToken<Collection<T2WUser>>() {
        }.getType();
        ArrayList<T2WUser> list = gson.fromJson(data, listType);

        for (T2WUser user : list) {
            T2WUser u = T2WUser.findOneByUser(user.getUserId());
            if (!u.ready()) { // not existed
                user.save();
                log.info("Restoring " + user.getUserId() + " ...");
            }
        }
    }

    public void queue(T2WUser user) {
        Jedis jedis = getJedis();
        String userId = user.getUserId();
        log.debug("Queue " + userId + " to sync.");
        jedis.lpush(QUEUE_KEY, userId);

        jedisPool.returnResource(jedis);
    }

    public T2WUser pop() {
        Jedis jedis = getJedis();
        String userId = jedis.rpop(QUEUE_KEY);

        T2WUser user;
        if (userId != null) {
            log.debug("Poped " + userId + " from queue.");
            user = T2WUser.findOneByUser(userId);
        } else {
            user = null;
        }

        jedisPool.returnResource(jedis);
        return user;
    }

    public long getQueueSize() {
        Jedis jedis = getJedis();
        Long llen = jedis.llen(QUEUE_KEY);
        jedisPool.returnResource(jedis);
        return llen;
    }

    protected Jedis getJedis() {
        return jedisPool.getResource();
    }

    protected void releaseJedis(Jedis j) {
        jedisPool.returnResource(j);
    }

    public static RedisHelper getInstance() {
        return ourInstance;
    }

    private RedisHelper() {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.testOnBorrow = true;
        config.maxActive = 20;
        config.maxIdle = 5;
        config.minIdle = 1;

        try {
            String services = System.getenv("VCAP_SERVICES");

            if (services != null) {
                JSONObject obj = new JSONObject(services);
                obj = obj.getJSONArray("redis-2.2").getJSONObject(0).getJSONObject("credentials");

                String hostname = obj.getString("hostname");
                int port = obj.getInt("port");
                String password = obj.getString("password");

                jedisPool = new JedisPool(config, hostname, port, 0, password);
            } else {
                jedisPool = new JedisPool(config, "localhost");
                log.info("Using localhost Redis server");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        RedisHelper instance = RedisHelper.getInstance();
        T2WUser pop = instance.pop();
        System.out.println(pop);
    }

}
