package h2weibo.worker;

import h2weibo.Twitter2Weibo;
import h2weibo.controllers.InitServlet;
import h2weibo.model.DBHelper;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author Rakuraku Jyo
 */
public class WorkerServlet extends InitServlet {
    private static final Logger log = Logger.getLogger(WorkerServlet.class.getName());

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        log.info("Worker started.");

        JedisPool jedisPool = getPool(config);
        final DBHelper helper = new DBHelper(jedisPool.getResource());

        new Thread(new SyncWorkerRunnable(helper)).start();
    }

    private static class SyncWorkerRunnable implements Runnable {
        private DBHelper helper;

        public SyncWorkerRunnable(DBHelper helper) {
            this.helper = helper;
        }

        @Override
        public void run() {
            long size = helper.getQueueSize();
            log.info("Start running, queue size = " + size + ", user count = " + helper.getUserCount());

            while (true) {
                T2WUser user = helper.pop();
                if (user != null) {
                    String userId = user.getUserId();
                    Twitter2Weibo weibo = new Twitter2Weibo(user);
                    weibo.syncTwitter();
                    log.debug("Syncing for " + userId);
                } else {
                    try {
                        log.info("No task found, sleeping");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        log.error("Error when try to sleep thread.", e);
                    }
                }
            }

        }
    }
}
