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

package h2weibo.utils;

import junit.framework.TestCase;

public class StatusImageExtractorTest extends TestCase {
    StatusImageExtractor extractor;
    private byte[] extract;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new StatusImageExtractor();
    }

    public void testInstagram() throws Exception {
        StringBuffer input = new StringBuffer("hello http://instagr.am/p/Ga_kl/ nice");
        extract = extractor.extract(input);
        assertEquals(input.toString(), "hello nice");
        assertNotNull(extract);
    }

    public void testTwitpic() throws Exception {
        extract = extractor.extract(new StringBuffer("hello http://twitpic.com/5h7gkh testing!"));
        assertNotNull(extract);
    }

    public void testImgly() throws Exception {
        StringBuffer buf;
        buf = new StringBuffer("hello http://img.ly/5wsQ testing!");
        extract = extractor.extract(buf);
        assertNotNull(extract);
        assertEquals("hello testing!", buf.toString());
        
        buf = new StringBuffer("http://img.ly/af4P 天冷了");
        extract = extractor.extract(buf);
        assertNotNull(extract);
        assertEquals(" 天冷了", buf.toString());
    }

    public void testYfrog() throws Exception {
        extract = extractor.extract(new StringBuffer("hello http://yfrog.com/kl9w0yj testing!"));
        assertNotNull(extract);
    }

    public void testCameraPlus() throws Exception {
        extract = extractor.extract(new StringBuffer("hello http://campl.us/b6Fi testing!"));
        assertNotNull(extract);
    }

    public void testDribbble() throws Exception {
        extract = extractor.extract(new StringBuffer("hello http://dribbble.com/shots/226476-Boat-on-Ocean-2 testing!"));
        assertNotNull(extract);
    }

    public void testDribbbleShort() throws Exception {
        extract = extractor.extract(new StringBuffer("hello http://drbl.in/bFDV testing!"));
        assertNotNull(extract);
    }

    public void testPlainImage() throws Exception {
        extract = extractor.extract(new StringBuffer("hello http://dribbble.com/system/users/17619/screenshots/176448/teelicht-icon.png?1310159107 testing!"));
        assertNotNull(extract);
    }

    public void testTwitterImage() throws Exception {
        extract = extractor.extract(new StringBuffer("http://twitter.com/xu_lele/status/125897828445855744/photo/1 http://p.twimg.com/Ab9HaG7CIAAfR6Y.jpg"));
        assertNotNull(extract);
    }
}
