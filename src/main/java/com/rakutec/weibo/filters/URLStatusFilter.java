package com.rakutec.weibo.filters;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLStatusFilter implements StatusFilter {
    @Override
    public String filter(String input) {
        if (input == null) return input;

        // Create a pattern to match cat
        Pattern p = Pattern.compile("http://bit.ly/\\w+");
        // Create a matcher with an input string
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        // Loop through and create a new String with the replacements
        while (result) {
            String bitlyUrl = m.group();
            Url url = Bitly.as("rakuraku", "R_26c2081c74b8fd7fc4ce023738444187").call(Bitly.expand(bitlyUrl));
            m.appendReplacement(sb, url.getLongUrl());
            result = m.find();
        }
        // Add the last segment of input to the new String
        m.appendTail(sb);

        return sb.toString();
    }
}
