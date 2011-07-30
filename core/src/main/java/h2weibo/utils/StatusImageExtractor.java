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

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusImageExtractor {
    private static final Logger log = Logger.getLogger(StatusImageExtractor.class.getName());
    private HashMap<String, String> simplePatterns = new HashMap<String, String>();

    public StatusImageExtractor() {
        simplePatterns.put("http://instagr.am/p/(\\w+)/", "http://instagr.am/p/_KEY_/media/");
        simplePatterns.put("http://twitpic.com/(\\w+)", "http://twitpic.com/show/large/_KEY_");
        simplePatterns.put("http://img.ly/(\\w+)", "http://img.ly/show/large/_KEY_");
        simplePatterns.put("http://yfrog.com/(\\w+)", "http://yfrog.com/_KEY_:iphone");
        simplePatterns.put("http://campl.us/(\\w+)", "http://campl.us/_KEY_:iphone");
    }

    public byte[] extract(String input) {
        if (input == null) return null;

        for (String key : simplePatterns.keySet()) {
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(input);
            
            if (m.find()) {
                String mediaUrl = simplePatterns.get(key);
                mediaUrl = mediaUrl.replaceAll("_KEY_", m.group(1));

                try {
                    URL url = new URL(mediaUrl);
                    InputStream in = url.openStream();
                    ByteOutputStream out = new ByteOutputStream();
                    for (int b; (b = in.read()) != -1; ) {
                        out.write(b);
                    }
                    byte[] bytes = out.getBytes();
                    out.close();
                    in.close();

                    return bytes;
                } catch (IOException e) {
                    log.error("Not able to download image", e);
                }
            }
        }

        return null;
    }
}
