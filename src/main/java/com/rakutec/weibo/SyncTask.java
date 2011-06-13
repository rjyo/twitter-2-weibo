package com.rakutec.weibo;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {
    public void run() {
        Set ids = TweetIDJedis.getAuthorizedIds();
        for (Object id : ids) {
            Twitter2Weibo t = new Twitter2Weibo((String) id);
            t.syncTwitter();
        }
    }
}