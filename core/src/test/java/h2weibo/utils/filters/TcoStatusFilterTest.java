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

public class TcoStatusFilterTest extends TestCase {

    public void testFilter() throws Exception {
        TcoStatusFilter tcoStatusFilter = new TcoStatusFilter();
        String result = tcoStatusFilter.filter("http://t.co/cjUHlXF");
        assertEquals("http://twitpic.com/659uz4", result);
    }
}
