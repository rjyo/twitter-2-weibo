package com.rakutec.weibo.utils;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import java.util.Set;


public class RedisHelper {
    private static final Logger log = Logger.getLogger(RedisHelper.class.getName());

    private static RedisHelper ourInstance = new RedisHelper();

    private Jedis jedis;
    private String hostname;
    private String password;
    private int port;

    public Set getAuthorizedIds() {
        return getJedis().smembers("twitter:ids");
    }

    public Long getUserCount() {
        return getJedis().scard("twitter:ids");
    }

    public boolean isUser(String user) {
        return getJedis().sismember("twitter:ids", user);
    }

    protected Jedis getJedis() {
        if (jedis == null || !jedis.isConnected()) {
            if (hostname != null) {
                jedis = new Jedis(hostname, port);
                jedis.auth(password);
                log.info("Using Redis on " + hostname + ":" + port);
            } else {
                jedis = new Jedis("localhost");
                log.info("Using localhost Redis server");
            }
        }
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

                hostname = obj.getString("hostname");
                port = obj.getInt("port");
                password = obj.getString("password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
