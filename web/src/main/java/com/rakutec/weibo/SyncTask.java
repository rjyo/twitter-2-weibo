package com.rakutec.weibo;

import com.rakutec.weibo.model.RedisHelper;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {
    private static final Logger log = Logger.getLogger(SyncTask.class.getName());

    public void run() {
        Set<String> ids = RedisHelper.getInstance().getAuthorizedIds();
        for (String id : ids) {
            log.info("Start syncing task for " + id);
            Twitter2Weibo t = new Twitter2Weibo(id);
            t.syncTwitter();
        }
    }
}