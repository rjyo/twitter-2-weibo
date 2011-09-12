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

package h2weibo.utils.filters;

import h2weibo.model.DBHelper;
import junit.framework.TestCase;
import org.apache.commons.pool.impl.GenericObjectPool;
import redis.clients.jedis.JedisPool;

/**
 * Test case for UserMappingFilter
 */
public class UserMappingFilterTest extends TestCase {

    private DBHelper helper;

    public void setUp() throws Exception {
        helper = new DBHelper(new JedisPool(new GenericObjectPool.Config(), "localhost"));
        helper.setWeiboId("Xuzhe", "xu_zhe");
        helper.setWeiboId("EatDami", "大米大仙");
    }

    public void testFilter() throws Exception {
        String input = "@xuzhe Let's eat dinner with @Eatdami tonight. /cc @xu_lele.";
        UserMappingFilter filter = new UserMappingFilter(helper);
        String s = filter.filter(input);
        assertEquals("@xu_zhe Let's eat dinner with @大米大仙 tonight. /cc @xu_lele.", s);
    }
}
