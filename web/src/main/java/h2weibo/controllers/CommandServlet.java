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
import h2weibo.model.DBHelperFactory;
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
public class CommandServlet extends InitServlet {
    private static final Logger log = Logger.getLogger(CommandServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRouter router = new HttpServletRouter(request);
        router.setPattern("/:cmd/:id");

        response.setContentType("text/plain; charset=UTF-8");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();

        final DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);

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
            String info = helper.getJedisInfo();
            writer.println(info);
        } else if (router.is(":cmd", "del")) {
            if (router.has(":id")) {
                String user = router.get(":id");
                helper.deleteUser(helper.findOneByUser(user));
                response.sendRedirect("/u/" + user);
            }
        } else if (router.is(":cmd", "u")) {
            if (router.has(":id")) {
                T2WUser u = helper.findOneByUser(router.get(":id"));
                writer.println(String.format("Latest tweet ID is %d", u.getLatestId()));
                writer.println(String.format("Twitter ID is %s", router.get(":id")));
                writer.println(String.format("Weibo ID is %s", helper.getWeiboId(u.getUserId())));
                writer.println(String.format("Twitter Token %s", u.getTwitterToken()));
                writer.println(String.format("Twitter Secret %s", u.getTwitterTokenSecret()));
                writer.println(String.format("Weibo Token %s", u.getToken()));
                writer.println(String.format("Weibo User ID %s", u.getWeiboUserId()));
            }
        } else {
            response.sendRedirect("/");
        }
        writer.close();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        log.info("Web started.");

        JedisPool pool = getPool(getServletContext());
        DBHelper helper = DBHelperFactory.createHelper(pool);
        // clear the queue
        helper.clearQueue();

        Scheduler scheduler = new Scheduler();

        QueueTask task = new QueueTask();
        task.setHelper(DBHelperFactory.createHelper(pool));
        scheduler.schedule("*/2 * * * *", task);

        scheduler.start();

        log.info("Cron scheduler started.");


        log.info("Worker started.");
        // 1 Threads to handle the sync job
        new Thread(new SyncWorkerRunnable(DBHelperFactory.createHelper(pool))).start();
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

            int multiper = 1;
            while (true) {
                T2WUser user = helper.pop();
                if (user != null) {
                    multiper = 1;
                    String userId = user.getUserId();
                    Twitter2Weibo weibo = new Twitter2Weibo(helper);
                    weibo.syncTwitter(userId);
                    log.debug("Syncing for " + userId);
                } else {
                    try {
                        log.info("No task found, sleeping");
                        Thread.sleep(4000 * multiper);
                        multiper *= 2;
                    } catch (InterruptedException e) {
                        log.error("Error when try to sleep thread.", e);
                    }
                }
            }

        }
    }

}
