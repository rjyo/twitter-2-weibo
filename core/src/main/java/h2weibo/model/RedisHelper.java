package h2weibo.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
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
