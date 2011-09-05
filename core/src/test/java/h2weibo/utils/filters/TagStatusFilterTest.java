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

import junit.framework.TestCase;

public class TagStatusFilterTest extends TestCase {
    public void testFilter() throws Exception {
        TagStatusFilter tagStatusFilter = new TagStatusFilter();
        String result = tagStatusFilter.filter("http://t.co/cjUHlXF #a2bc #t2w_日本語");
        assertEquals("http://t.co/cjUHlXF #a2bc# #t2w_日本語#", result);
    }
}
