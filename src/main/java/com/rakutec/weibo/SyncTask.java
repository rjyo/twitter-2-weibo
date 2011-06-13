package com.rakutec.weibo;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {
    public void run() {
        Set ids = TweetIDJedis.getAuthorizedIds();
        for (Object id : ids) {
            TweetIDJedis tj = TweetIDJedis.loadUser((String) id);
            Twitter2Weibo t = new Twitter2Weibo(tj.getToken(), tj.getTokenSecret());
            t.syncTwitter(tj.getUserId());
        }
    }
}