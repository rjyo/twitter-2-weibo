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

import h2weibo.Keys;
import h2weibo.model.DBHelper;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Rakuraku Jyo
 */
public class TweetServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(TweetServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loginUser = (String) session.getAttribute(Keys.SESSION_LOGIN_USER);

        DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);
        T2WUser t2wUser = helper.findOneByUser(loginUser);

        TwitterFactory factory = new TwitterFactory();
        Twitter t = factory.getInstance();
        t.setOAuthAccessToken(new AccessToken(t2wUser.getTwitterToken(), t2wUser.getTwitterTokenSecret()));

        try {
            t.updateStatus("Twitter, say hello to Weibo! Now syncing Twitter 2 Weibo using http://h2weibo.cloudfoundry.com #t2w_sync");
        } catch (TwitterException e) {
            log.error("Failed to send tweets", e);
        }

        response.setStatus(200);
    }
}