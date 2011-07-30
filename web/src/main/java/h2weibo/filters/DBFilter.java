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

package h2weibo.filters;

import h2weibo.Keys;
import h2weibo.model.DBHelper;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DBFilter implements Filter {
    private static final Logger log = Logger.getLogger(DBFilter.class.getName());
    private ServletContext context = null;

    @Override
    public void init(FilterConfig config) throws ServletException {
        context = config.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        log.debug("In DBFilter");

        JedisPool jedisPool = (JedisPool) context.getAttribute(Keys.CONTEXT_JEDIS_POOL);
        DBHelper helper = new DBHelper(jedisPool.getResource());
        request.setAttribute(Keys.REQUEST_DB_HELPER, helper);

        chain.doFilter(req, res);

        jedisPool.returnResource(helper.getJedis());
        request.removeAttribute(Keys.REQUEST_DB_HELPER);
    }

    @Override
    public void destroy() {
        context = null;
    }
}
