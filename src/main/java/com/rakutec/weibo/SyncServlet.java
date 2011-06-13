package com.rakutec.weibo;

import it.sauronsoftware.cron4j.Scheduler;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Rakuraku Jyo
 */
public class SyncServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SyncServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();

        TweetIDJedis f = TweetIDJedis.loadUser("xu_lele");
        String latestId = request.getParameter("id");
        if (latestId != null) {
            f.updateLatestId(Long.valueOf(latestId));
            f.save();
            writer.println("Latest tweet ID updated to " + latestId);
        } else {
            writer.println("Latest tweet ID is " + f.getLatestId());
        }

        writer.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Key for Weibo App
        System.setProperty("weibo4j.oauth.consumerKey", "2917100994");
        System.setProperty("weibo4j.oauth.consumerSecret", "331e188966be6384c6722b1d3944c89e");

        SyncTask task = new SyncTask();
        Scheduler scheduler = new Scheduler();
        scheduler.schedule("*/5 * * * *", task);
        scheduler.start();

        log.info("Cron scheduler started.");
    }
}