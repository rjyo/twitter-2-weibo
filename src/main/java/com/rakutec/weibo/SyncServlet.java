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
import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SyncServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();

        String cmd = request.getParameter("cmd");
        if ("sync".equals(cmd)) {
            SyncTask task = new SyncTask();
            task.run();
            writer.println("Run!");
        } else if ("users".equals(cmd)) {
            Set ids = TweetID.getAuthorizedIds();
            writer.println("Syncing user list:");
            for (Object id : ids) {
                writer.println("  " + id);
            }
        } else if ("del".equals(cmd)) {
            String user = request.getParameter("u");
            if (user != null) {
                TweetID.delete(user);
                writer.println("  Removed " + user);
            }
        } else { // not a command
            String user = request.getParameter("u");
            if (user != null) {
                TweetID f = TweetID.getUser(user);
                writer.println("Latest tweet ID is " + f.getLatestId());
            } else {
                writer.println("Welcome!");
                writer.println("");
                writer.println("Features:");
                writer.println("  - Use oauth to connect to Weibo, no need for user/password");
                writer.println("  - Sync in less than 5 minutes");
                writer.println("  - Auto drop replies and RTs with @somebody in the tweet");
                writer.println("  - Auto expand bit.ly URL");
                writer.println("  - Auto translate twitter style #tag to weibo style #tag#");
            }
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