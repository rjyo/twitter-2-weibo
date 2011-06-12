package com.rakutec.weibo;

import it.sauronsoftware.cron4j.Scheduler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * @author Rakuraku Jyo
 */
public class SyncServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(Twitter2Weibo.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();

        TweetIDJedis f = TweetIDJedis.loadTweetID("xu_lele");
        String latestId = request.getParameter("id");
        if (latestId != null) {
            f.update(Long.valueOf(latestId));
            writer.println("Latest tweet ID updated to " + latestId);
        } else {
            writer.println("Latest tweet ID is " + f.latestId);
        }

        writer.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
//        String latestId = config.getInitParameter("latestId");
//        if (latestId != null) {
//            TweetIDJedis f = TweetIDJedis.loadTweetID("xu_lele");
//            if (f.latestId == 0) {
//                f.update(Long.valueOf(latestId));
//                log.info("Using init-param, latest tweet ID updated to " + latestId);
//            } else {
//                log.info("Using current latest tweet ID: " + latestId);
//            }
//        }
        
        // Prepares the task.
        SyncTask task = new SyncTask("d9ddc51b9f84f211206eb4124a74601b", "35d1ff8d00d9093a666fbc705acc8629", "xu_lele");
        // Creates the scheduler.
        Scheduler scheduler = new Scheduler();
        // Schedules the task, once every minute.
        scheduler.schedule("*/2 * * * *", task);
        // Starts the scheduler.
        scheduler.start();

        log.info("Cron scheduler started.");
    }
}