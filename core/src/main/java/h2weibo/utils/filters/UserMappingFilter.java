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

import h2weibo.model.DBHelper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMappingFilter extends DBStatusFilter {
    public UserMappingFilter(DBHelper helper) {
        setHelper(helper);
    }

    public String filter(String input) {
        Pattern p = Pattern.compile("@(\\w+)");
        Matcher m = p.matcher(input);

        Map<String, String> userMap = getHelper().getUserMap();

        while (m.find()) {
            String userId = m.group(1);
            String weiboId = userMap.get(userId.toLowerCase());

            if (weiboId != null) {
                input = input.replaceAll("@" + userId, "@" + weiboId);
            }
        }

        return input;
    }
}
