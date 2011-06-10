package com.rakutec.weibo;

import it.sauronsoftware.cron4j.Scheduler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class SyncServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(T2Weibo.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();

        TweetIDFile f = TweetIDFile.loadTweetID("xu_lele");
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

        String latestId = config.getInitParameter("latestId");
        if (latestId != null) {
            TweetIDFile f = TweetIDFile.loadTweetID("xu_lele");
            f.update(Long.valueOf(latestId));
            log.info("Using init-param, latest tweet ID updated to " + latestId);
        }

        log.info("inited");
        // Prepares the task.
        SyncTask task = new SyncTask();
        // Creates the scheduler.
        Scheduler scheduler = new Scheduler();
        // Schedules the task, once every minute.
        scheduler.schedule("*/2 * * * *", task);
        // Starts the scheduler.
        scheduler.start();

        log.info("Cron scheduler started.");
    }
}