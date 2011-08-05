/*
 * (The MIT License)
 *
 * Copyright (c) 2011 Rakuraku Jyo <jyo.rakuraku@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

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
                Set<String> ids = helper.getAuthorizedIds();
                Map<String, String> mappings = helper.getMappings();

                writer.println(String.format("Syncing user list: (%d users)", ids.size()));
                for (String id : ids) {
                    writer.println(String.format("  %s => %s", id, mappings.get(id)));
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
                    writer.println(String.format("Latest tweet ID is %d", u.getLatestId()));
                    writer.println(String.format("Twitter ID is %s", router.get(":id")));
                    writer.println(String.format("Weibo ID is %s", u.getWeiboId()));
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
