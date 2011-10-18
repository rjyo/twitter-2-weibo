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

public class FlickrImageFilterTest extends TestCase {
    public void testFilter() throws Exception {
        FlickrImageFilter filter = new FlickrImageFilter();
        String s = filter.filter("hello http://www.flickr.com/photos/ccyann/6213229373/");

        assertEquals("hello http://www.flickr.com/photos/ccyann/6213229373/ http://farm7.static.flickr.com/6094/6213229373_4eb85490d4.jpg", s);
    }

    public void testFilterShort() throws Exception {
        FlickrImageFilter filter = new FlickrImageFilter();
        String s = filter.filter("flic.kr/p/at3qCn nice!");

        assertEquals("flic.kr/p/at3qCn nice! http://farm7.static.flickr.com/6094/6213229373_4eb85490d4.jpg", s);
    }
}
