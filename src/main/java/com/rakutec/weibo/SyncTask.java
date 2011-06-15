package com.rakutec.weibo;

import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {
    private static final Logger log = Logger.getLogger(SyncTask.class.getName());
    
    public void run() {
        Set ids = TweetID.getAuthorizedIds();
        for (Object id : ids) {
            log.info("Start syncing task.");
            Twitter2Weibo t = new Twitter2Weibo((String) id);
            t.syncTwitter();
        }
    }
}