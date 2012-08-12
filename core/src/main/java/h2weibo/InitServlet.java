/*
 * (The MIT License)
 *
 * Copyright (c) 2011 Rakuraku Jyo <jyo.rakuraku@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package h2weibo;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Rakuraku Jyo
 */
public class InitServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(InitServlet.class.getName());
    public static final String CONTEXT_JEDIS_POOL = "JEDIS_POOL";

    public JedisPool createJedisPool() {
        JedisPool jedisPool;

        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.testOnBorrow = true;
        config.testWhileIdle = true;
        config.maxActive = 25;
        config.maxIdle = 0;
        config.minIdle = 0;

        log.debug("Jedis pool created.");

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
            return jedisPool;
        } catch (JSONException e) {
            log.error("Failed to init", e);
            return null;
        }
    }

    public JedisPool getPool(ServletContext context) {
        return (JedisPool) context.getAttribute(CONTEXT_JEDIS_POOL);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));

            System.setProperty("h2weibo.admin.user", properties.getProperty("admin", ""));

            // Key for Twitter App
            System.setProperty("twitter4j.oauth.consumerKey", properties.getProperty("twitter_key", ""));
            System.setProperty("twitter4j.oauth.consumerSecret", properties.getProperty("twitter_secret", ""));
        } catch (IOException e) {
            log.error(e);
        }

        System.setProperty("weibo4j.debug", "false");

        log.info("System initialized.");

        getServletContext().setAttribute(CONTEXT_JEDIS_POOL, createJedisPool());
    }

    @Override
    public void destroy() {
        JedisPool pool = getPool(getServletContext());
        pool.destroy();
    }
}
