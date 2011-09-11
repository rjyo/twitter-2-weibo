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

import org.apache.commons.httpclient.util.HttpURLConnection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TcoStatusFilter implements StatusFilter {

    public String filter(String input) {
        // Create a pattern to match cat
        Pattern p = Pattern.compile("http://t.co/\\w+");
        // Create a matcher with an input string
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        // Loop through and create a new String with the replacements
        while (result) {
            String tcoUrl = m.group();
            try {
                m.appendReplacement(sb, getRedirectUrl(tcoUrl));
            } catch (IOException e) {
                // do nothing, skip
            }
            result = m.find();
        }
        // Add the last segment of input to the new String
        m.appendTail(sb);

        return sb.toString();
    }

    private  String getRedirectUrl(String urlStr) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        String location = conn.getHeaderField("Location");
        HttpURLConnection.setFollowRedirects(true);
        return location;
    }
}
