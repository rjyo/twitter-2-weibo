package h2weibo.controllers;

import h2weibo.*;
import h2weibo.model.DBHelper;
import h2weibo.model.T2WUser;
import it.sauronsoftware.cron4j.Scheduler;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class SyncServlet extends InitServlet {
    private static final Logger log = Logger.getLogger(SyncServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRouter router = new HttpServletRouter(request);
        router.setPattern("/:cmd/:id");

        response.setContentType("text/plain; charset=UTF-8");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        
        final DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);

        if (router.is(":cmd", "dump")) {
            S3BackupTask task = new S3BackupTask();
            task.run();
            response.sendRedirect("/");
        } else if (router.is(":cmd", "restore")) {
            S3BackupTask task = new S3BackupTask();
            if (router.has(":id")) {
                task.restore(router.get(":id"));
                response.sendRedirect("/");
            } else {
                writer.println("Parameter error");
            }
        } else {
            if (router.is(":cmd", "users")) {
                Set ids = helper.getAuthorizedIds();

                writer.println("Syncing user list: (" + ids.size() + " users)");
                for (Object id : ids) {
                    writer.println("  " + id);
                }
            } else if (router.is(":cmd", "show_mapping")) {
                Map<String,String> mappings = helper.getMappings();
                for (String key : mappings.keySet()) {
                    writer.printf("%s = %s \n", key, mappings.get(key));
                }
            } else if (router.is(":cmd", "mapping")) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        helper.createUserMap();
                    }
                });
                t.start();
                response.sendRedirect("/");
            } else if (router.is(":cmd", "info")) {
                String info = helper.getJedis().info();
                writer.println(info);
            } else if (router.is(":cmd", "del")) {
                if (router.has(":id")) {
                    String user = router.get(":id");
                    T2WUser id = helper.findOneByUser(user);
                    id.delete();
                    response.sendRedirect("/u/" + user);
                }
            } else if (router.is(":cmd", "u")) {
                if (router.has(":id")) {
                    T2WUser u = helper.findOneByUser(router.get(":id"));
                    writer.println("Latest tweet ID is " + u.getLatestId());
                    writer.println("Twitter ID is " + router.get(":id"));
                    writer.println("Weibo ID is " + u.getWeiboId());
                }
            } else {
                response.sendRedirect("/");
            }
        }
        writer.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        log.info("Web started.");

        JedisPool jedisPool = getPool(getServletContext());
        DBHelper helper = new DBHelper(jedisPool.getResource());
        // clear the queue
        helper.clearQueue();

        Scheduler scheduler = new Scheduler();

        QueueTask task = new QueueTask();
        task.setHelper(new DBHelper(jedisPool.getResource()));
        scheduler.schedule("*/2 * * * *", task);

        S3BackupTask task2 = new S3BackupTask();
        task2.setHelper(new DBHelper(jedisPool.getResource()));
        scheduler.schedule("0 * * * *", task2);
        
        scheduler.start();

        log.info("Cron scheduler started.");

        jedisPool.returnResource(helper.getJedis());
    }
}
