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

package h2weibo.model;

import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DBHelperFactory {
    private static final Logger log = Logger.getLogger(DBHelperFactory.class.getName());


    private static class JedisInvocationHandler implements InvocationHandler {
        private DBHelperImpl helper;

        public JedisInvocationHandler(DBHelperImpl helper) {
            this.helper = helper;
        }

        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            try {
                helper.prepareJedis();
                Object result = method.invoke(helper, args);
                helper.returnJedis();
                return result;
            } catch (JedisException e) {
                log.error(e);
                log.info("Trying to recover Jedis connection");
                helper.returnBrokenJedis();
                Object result = method.invoke(helper, args);
                helper.returnJedis();
                return result;
            }
        }
    }

    public static DBHelper createHelper(JedisPool pool) {
        ClassLoader classLoader = DBHelper.class.getClassLoader();
        Class[] classes = {DBHelper.class};
        JedisInvocationHandler handler = new JedisInvocationHandler(new DBHelperImpl(pool));
        return (DBHelper) Proxy.newProxyInstance(classLoader, classes, handler);
    }
}
