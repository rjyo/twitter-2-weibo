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

package h2weibo.model;

import java.util.Map;
import java.util.Set;

public interface DBHelper {
    public Set<String> getAuthorizedIds();
    public Long getUserCount();

    public boolean isUser(String userId);

    public T2WUser findOneByUser(String userId);

    public Map<String, String> getUserMap();

    public void createUserMap();

    public void setWeiboId(String twitterId, String weiboId);

    public String getWeiboId(String twitterId);

    public Map<String, String> getMappings();

    public String dump();

    public void restore(String data);

    public void saveUser(T2WUser u);

    public void deleteUser(T2WUser u);

    public void clearQueue();

    public void queue(T2WUser user);

    public T2WUser pop();

    public long getQueueSize();

    public String getJedisInfo();


}
