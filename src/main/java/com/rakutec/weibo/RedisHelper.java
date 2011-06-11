package com.rakutec.weibo;

import redis.clients.jedis.Jedis;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import java.util.logging.Logger;

public class RedisHelper {
    private static final Logger log = Logger.getLogger(RedisHelper.class.getName());
    private static RedisHelper ourInstance = new RedisHelper();

    private Jedis jedis;

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
