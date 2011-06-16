package com.rakutec.weibo;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import java.util.Set;


public class RedisHelper {
    private static final Logger log = Logger.getLogger(RedisHelper.class.getName());

    private static RedisHelper ourInstance = new RedisHelper();

    private Jedis jedis;

    public static Set getAuthorizedIds() {
        Jedis j = getInstance().getJedis();
        return j.smembers("twitter:ids");
    }

    public static Long getUserCount() {
        Jedis j = getInstance().getJedis();
        return j.scard("twitter:ids");
    }

    public static void delete(String userId) {
        Jedis j = getInstance().getJedis();
        j.del("id:" + userId + ":latestId");
        j.del("id:" + userId + ":token");
        j.del("id:" + userId + ":tokenSecret");
        j.srem("twitter:ids", userId);
    }

    public Jedis getJedis() {
        return jedis;
    }

    public static RedisHelper getInstance() {
        return ourInstance;
    }

    private RedisHelper() {
        try {
            String services = System.getenv("VCAP_SERVICES");

            if (services != null) {
                JSONObject obj = new JSONObject(services);
                obj = obj.getJSONArray("redis-2.2").getJSONObject(0).getJSONObject("credentials");

                String hostname = obj.getString("hostname");
                int port = obj.getInt("port");
                String password = obj.getString("password");

                jedis = new Jedis(hostname, port);
                jedis.auth(password);
                log.info("Using Redis on " + hostname + ":" + port);
            } else {
                jedis = new Jedis("localhost");
                log.info("Using localhost Redis server");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
