package com.rakutec.weibo.utils;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusImageExtractor {
    private static final Logger log = Logger.getLogger(StatusImageExtractor.class.getName());

    public byte[] extract(String input) {
        if (input == null) return null;

        // Create a pattern to match cat
        Pattern p = Pattern.compile("http://instagr.am/p/\\w+/");
        // Create a matcher with an input string
        Matcher m = p.matcher(input);
        // Loop through and create a new String with the replacements
        if (m.find()) {
            String imgUrl = m.group();
            String mediaUrl = imgUrl + "media/";

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

        return null;
    }

    public static void main(String[] args) {
        StatusImageExtractor extractor = new StatusImageExtractor();
        byte[] extract = extractor.extract("hello http://instagr.am/p/Ga_kl/ nice");
        if (extract != null) {
            log.info("nice!");
        }
    }
}
