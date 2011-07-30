package h2weibo;

import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class QueueTask extends DBTask {
    private static final Logger log = Logger.getLogger(QueueTask.class.getName());

    public void run() {
        log.info("Start to queue syncing tasks.");
        Set<String> ids = getHelper().getAuthorizedIds();
        for (String userId : ids) {
            T2WUser user = getHelper().findOneByUser(userId);
            if (user.ready()) {
                getHelper().queue(user);
            }
        }
    }
}