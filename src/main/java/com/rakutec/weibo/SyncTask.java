package com.rakutec.weibo;

import com.rakutec.weibo.utils.RedisHelper;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {
    private static final Logger log = Logger.getLogger(SyncTask.class.getName());

    public void run() {
        Set ids = RedisHelper.getInstance().getAuthorizedIds();
        for (Object id : ids) {
            log.info("Start syncing task.");
            Twitter2Weibo t = new Twitter2Weibo((String) id);
            t.syncTwitter();
        }
    }
}