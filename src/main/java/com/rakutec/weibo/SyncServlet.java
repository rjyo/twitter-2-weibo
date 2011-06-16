package com.rakutec.weibo;

import com.rakutec.weibo.utils.HttpServletRouter;
import com.rakutec.weibo.utils.RedisHelper;
import com.rakutec.weibo.utils.TweetID;
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
        HttpServletRouter router = new HttpServletRouter(request);
        router.setPattern("/:cmd/:id");

        response.setContentType("text/plain");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();

        if (router.is(":cmd", "sync")) {
            SyncTask task = new SyncTask();
            task.run();
            writer.println("Run!");
        } else if (router.is(":cmd", "users")) {
            Set ids = RedisHelper.getInstance().getAuthorizedIds();
            writer.println("Syncing user list:");
            for (Object id : ids) {
                writer.println("  " + id);
            }
        } else if (router.is(":cmd", "del")) {
            String user = request.getParameter("u");
            if (user != null) {
                TweetID id = TweetID.findOneByUser(user);
                id.delete();
                writer.println("  Removed " + user);
            }
        } else if (router.is(":cmd", "u")) {
            if (router.has(":id")) {
                TweetID f = TweetID.findOneByUser(router.get("id"));
                writer.println("Latest tweet ID is " + f.getLatestId());
            } else {
                response.sendRedirect(request.getContextPath());
            }
        } else {
            response.sendRedirect(request.getContextPath());
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