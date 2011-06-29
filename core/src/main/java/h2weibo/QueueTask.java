package h2weibo;

import h2weibo.model.RedisHelper;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class QueueTask implements Runnable {
    private static final Logger log = Logger.getLogger(QueueTask.class.getName());

    public void run() {
        log.info("Start to queue syncing tasks.");
        Set<String> ids = RedisHelper.getInstance().getAuthorizedIds();
        for (String userId : ids) {
            T2WUser user = T2WUser.findOneByUser(userId);
            if (user.ready()) {
                RedisHelper.getInstance().queue(user);
            }
        }
    }
}