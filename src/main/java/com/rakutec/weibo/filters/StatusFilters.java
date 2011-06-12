package com.rakutec.weibo.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jyo
 * Date: 11/06/12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
public class StatusFilters {
    List<StatusFilter> filters = new ArrayList<StatusFilter>();

    public StatusFilters use(StatusFilter filter) {
        filters.add(filter);
        return this;
    }

    public StatusFilters remove(StatusFilter filter) {
        filters.remove(filter);
        return this;
    }

    public String filter(String input) {
        String output = input;
        for (StatusFilter filter : filters) {
            output = filter.filter(output);
        }
        return output;
    }
}
