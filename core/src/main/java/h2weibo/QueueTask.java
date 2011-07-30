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

package h2weibo;

import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * @author Rakuraku Jyo
 */
public class QueueTask extends DBTask {
    private static final Logger log = Logger.getLogger(QueueTask.class.getName());

    public void run() {
        log.info("Start to queue syncing tasks.");
        Set<String> ids = getHelper().getAuthorizedIds();
        for (String userId : ids) {
            T2WUser user = getHelper().findOneByUser(userId);
            if (user.ready()) {
                getHelper().queue(user);
            }
        }
    }
}