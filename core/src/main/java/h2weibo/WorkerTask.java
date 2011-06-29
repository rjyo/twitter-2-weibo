package h2weibo;

import h2weibo.model.RedisHelper;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class WorkerTask implements Runnable {
    private static final Logger log = Logger.getLogger(WorkerTask.class.getName());

    public void run() {
        Set<String> ids = RedisHelper.getInstance().getAuthorizedIds();
        for (String id : ids) {
            log.info("Start syncing task for " + id);
            Twitter2Weibo t = new Twitter2Weibo(id);
            t.syncTwitter();
        }
    }
}