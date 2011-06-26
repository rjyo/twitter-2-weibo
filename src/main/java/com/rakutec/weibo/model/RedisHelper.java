package com.rakutec.weibo.model;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twitter4j.internal.org.json.JSONArray;

import java.util.Set;


public class RedisHelper {
    private static final Logger log = Logger.getLogger(RedisHelper.class.getName());
    private static RedisHelper ourInstance = new RedisHelper();

    private JedisPool jedisPool;

    public Set<String> getAuthorizedIds() {
        Jedis jedis = getJedis();
        Set<String> set = jedis.smembers("twitter:ids");
        jedisPool.returnResource(jedis);
        return set;
    }

    public Long getUserCount() {
        Jedis jedis = getJedis();
        Long scard = jedis.scard("twitter:ids");
        jedisPool.returnResource(jedis);
        return scard;
    }

    public boolean isUser(String user) {
        Jedis jedis = getJedis();
        Boolean ismember = jedis.sismember("twitter:ids", user);
        jedisPool.returnResource(jedis);
        return ismember;
    }

    public String dump() {
        Jedis jedis = getJedis();

        Set<String> users = getAuthorizedIds();
        JSONArray list = new JSONArray();
        for (String user : users) {
            T2WUser u = T2WUser.findOneByUser(user);
            JSONObject obj = new JSONObject(u);
            list.put(obj);
        }

        jedisPool.returnResource(jedis);
        return list.toString();
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
}
