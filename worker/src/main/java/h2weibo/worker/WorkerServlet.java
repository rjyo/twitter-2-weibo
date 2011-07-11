package h2weibo.worker;

import h2weibo.Twitter2Weibo;
import h2weibo.model.RedisHelper;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author Rakuraku Jyo
 */
public class WorkerServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(WorkerServlet.class.getName());

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Key for Weibo App
        System.setProperty("weibo4j.oauth.consumerKey", "2440858351");
        System.setProperty("weibo4j.oauth.consumerSecret", "1faf4ed7b4af302907e25429a0b88dfc");

        System.setProperty("weibo4j.debug", "false");

        // Key for Twitter App
        System.setProperty("twitter4j.oauth.consumerKey", "Scwn2HbdT7v3yOEjkAQrfQ");
        System.setProperty("twitter4j.oauth.consumerSecret", "QIz4dbgb5ABzNMjfP1Sb0YdwKTY2oKQwhLoehk0ug");

        log.info("Worker started.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                RedisHelper helper = RedisHelper.getInstance();
                long size = helper.getQueueSize();
                log.info("Start running, queue size = " + size + ", user count = " + helper.getUserCount());

                while (true) {
                    T2WUser user = helper.pop();
                    if (user != null) {
                        String userId = user.getUserId();
                        Twitter2Weibo weibo = new Twitter2Weibo(user);
                        weibo.syncTwitter();
                        log.info("Syncing for " + userId);
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
        }).start();
    }
}
