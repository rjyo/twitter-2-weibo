package com.rakutec.weibo.filters;

import javax.enterprise.inject.New;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KVStatusFilter implements StatusFilter {
    Map<String, String> repls = new HashMap<String, String>();

    public void add(String from, String to) {
        repls.put(from, to);
    }

    @Override
    public String filter(String input) {
        for (String key : repls.keySet()) {
            input.replaceAll(key, repls.get(key));
        }
        return input;
    }
}
