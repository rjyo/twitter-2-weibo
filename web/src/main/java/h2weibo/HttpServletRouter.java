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

package h2weibo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class HttpServletRouter {
    private String[] paths;
    private HashMap<String, String> maps;

    public HttpServletRouter(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        this.paths = pathInfo.split("/");
        this.maps = new HashMap<String, String>(this.paths.length);
    }

    /**
     * A pattern for URL
     * For example: /:cmd/:id
     *
     * @param pathPattern path patterns
     */
    public void setPattern(String pathPattern) {
        String[] ps = pathPattern.split("/");
        for (int i = 0; i < ps.length; i++) {
            String p = ps[i];
            if (p.length() == 0) continue;
            if (paths.length > i) {
                maps.put(p, paths[i]);
            }
        }
    }

    public String get(String key) {
        return maps.get(key);
    }

    public boolean is(String key, String value) {
        if (value == null) return false;
        String s = maps.get(key);
        return value.equals(s);
    }

    public boolean has(String key) {
        return maps.containsKey(key);
    }
}
