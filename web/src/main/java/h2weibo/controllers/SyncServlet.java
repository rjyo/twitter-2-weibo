package h2weibo.controllers;

import h2weibo.HttpServletRouter;
import h2weibo.S3BackupTask;
import h2weibo.SyncTask;
import h2weibo.model.RedisHelper;
import h2weibo.model.T2WUser;
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
            response.sendRedirect("/");
        } else if (router.is(":cmd", "dump")) {
            S3BackupTask task = new S3BackupTask();
            task.run();
            response.sendRedirect("/");
        } else if (router.is(":cmd", "restore")) {
            S3BackupTask task = new S3BackupTask();
            if (router.has(":id")) {
                task.restore(router.get(":id"));
            }
            // response.sendRedirect("/");
        } else if (router.is(":cmd", "users")) {
            Set ids = RedisHelper.getInstance().getAuthorizedIds();

            writer.println("Syncing user list: (" + ids.size() + " users)");
            for (Object id : ids) {
                writer.println("  " + id);
            }
        } else if (router.is(":cmd", "del")) {
            if (router.has(":id")) {
                String user = router.get(":id");
                T2WUser id = T2WUser.findOneByUser(user);
                id.delete();
                response.sendRedirect("/u/" + user);
            }
        } else if (router.is(":cmd", "u")) {
            if (router.has(":id")) {
                T2WUser f = T2WUser.findOneByUser(router.get("id"));
                writer.println("Latest tweet ID is " + f.getLatestId());
            } else {
                response.sendRedirect(request.getContextPath());
            }
        } else {
            response.sendRedirect("/");
        }
        writer.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Key for Weibo App
        System.setProperty("weibo4j.oauth.consumerKey", "2440858351");
        System.setProperty("weibo4j.oauth.consumerSecret", "1faf4ed7b4af302907e25429a0b88dfc");

        // Key for Twitter App
        System.setProperty("twitter4j.oauth.consumerKey", "Scwn2HbdT7v3yOEjkAQrfQ");
        System.setProperty("twitter4j.oauth.consumerSecret", "QIz4dbgb5ABzNMjfP1Sb0YdwKTY2oKQwhLoehk0ug");

        // Disable weibo4j Debug outputs
        System.setProperty("weibo4j.debug", "false");

        Scheduler scheduler = new Scheduler();

        SyncTask task = new SyncTask();
        scheduler.schedule("*/5 * * * *", task);

        S3BackupTask task2 = new S3BackupTask();
        scheduler.schedule("0 */4 * * *", task2);
        scheduler.start();

        log.info("Cron scheduler started.");
    }
}
