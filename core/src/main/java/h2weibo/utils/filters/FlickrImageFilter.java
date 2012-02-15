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

import org.apache.log4j.Logger;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlickrImageFilter implements StatusFilter {
    private static final Logger log = Logger.getLogger(FlickrImageFilter.class.getName());
    private static final String API_URL = "http://api.flickr.com/services/rest/?photo_id=_KEY_" +
            "&method=flickr.photos.getSizes" +
            "&api_key=a35416797f713f45f444a76fc41b6792" +
            "&format=json";

    public String filter(String input) {
        String imageUrl = extraceFromFullURL(input);
        if (imageUrl == null) imageUrl = extraceFromShortURL(input);

        if (imageUrl != null) {
            // Add the last segment of input to the new String
            input += " " + imageUrl;
        }

        return input;
    }

    private String extraceFromShortURL(String input) {
        Pattern p = Pattern.compile("flic.kr/p/(\\w+)/?.*");
        Matcher m = p.matcher(input);
        if (m.find()) {
            input = "http://www.flickr.com/photo.gne?short=" + m.group(1);

            String imageUrl = null;
            try {
                URL url = new URL(input);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.connect();
                int code = conn.getResponseCode();
                if (code == 302) {
                    String loc = conn.getHeaderField("location");
                    imageUrl = extraceFromFullURL("http://www.flickr.com" + loc);
                }
                conn.disconnect();
            } catch (Exception e) {
                log.error("Not able to handle flickr's short url/", e);
            }
            return imageUrl;
        } else {
            return null;
        }
    }

    private String extraceFromFullURL(String input) {
        Pattern p = Pattern.compile("http://www.flickr.com/photos/[^/]+/(\\w+)/?.*");
        Matcher m = p.matcher(input);

        String imageUrl = null;

        if (m.find()) {
            String apiUrl = API_URL.replaceAll("_KEY_", m.group(1));

            try {
                String source = new String(downloadUrl(apiUrl));
                int start = source.indexOf("(");
                int end = source.lastIndexOf(")");
                JSONObject obj = new JSONObject(source.substring(start + 1, end));
                JSONObject results = obj.getJSONObject("sizes");
                if (results.getInt("candownload") == 1) {
                    JSONArray sizes = results.getJSONArray("size");
                    for (int i = 0; i < sizes.length(); i++) {
                        JSONObject img = sizes.getJSONObject(i);
                        if ("Medium".equals(img.get("label"))) {
                            imageUrl = img.getString("source");
                            break;
                        }
                    }
                }

            } catch (IOException e) {
                log.error("Not able to download image", e);
            } catch (JSONException e) {
                log.error("Not able to parse json", e);
            }
        }
        return imageUrl;
    }

    private byte[] downloadUrl(String mediaUrl) throws IOException {
        URL url = new URL(mediaUrl);
        InputStream in = url.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int b; (b = in.read()) != -1; ) {
            out.write(b);
        }
        byte[] bytes = out.toByteArray();
        out.close();
        in.close();

        return bytes;
    }
}
