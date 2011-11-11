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

package h2weibo.worker;

import h2weibo.InitServlet;
import h2weibo.Twitter2Weibo;
import h2weibo.model.DBHelper;
import h2weibo.model.DBHelperFactory;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author Rakuraku Jyo
 */
public class WorkerServlet extends InitServlet {
    private static final Logger log = Logger.getLogger(WorkerServlet.class.getName());

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        log.info("Worker started.");

        JedisPool pool = getPool(getServletContext());

        // 3 Threads to handle the sync job
        new Thread(new SyncWorkerRunnable(DBHelperFactory.createHelper(pool))).start();
        new Thread(new SyncWorkerRunnable(DBHelperFactory.createHelper(pool))).start();
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
