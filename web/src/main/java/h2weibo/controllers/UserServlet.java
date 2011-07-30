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

import h2weibo.HttpServletRouter;
import h2weibo.Keys;
import h2weibo.model.DBHelper;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityLayoutServlet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Rakuraku Jyo
 */

public class UserServlet extends VelocityLayoutServlet {
    private static final Logger log = Logger.getLogger(UserServlet.class.getName());

    @Override
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
        HttpServletRouter r = new HttpServletRouter(request);
        r.setPattern("/:id");

        HttpSession session = request.getSession(false);

        DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);

        // Service limit
        String uId = r.get(":id");
        if (!helper.isUser(uId) && helper.getUserCount() > 50) {
            return getTemplate("full.vm");
        }

        T2WUser t2wUser = helper.findOneByUser(uId);
        if (r.has(":id")) {
            log.info("Displaying user info for @" + uId);

            ctx.put("user_id", uId);
            ctx.put("user", helper.findOneByUser(uId));

            try {
                weibo4j.User user = (weibo4j.User) session.getAttribute(Keys.SESSION_WEIBO_USER);
                if (user == null) {
                    Weibo w = new Weibo();
                    w.setToken(t2wUser.getToken(), t2wUser.getTokenSecret());
                    user = w.verifyCredentials();

                    session.setAttribute(Keys.SESSION_WEIBO_USER, user);
                }

                ctx.put("weibo_user", user.getScreenName());
                ctx.put("weibo_user_image", user.getProfileImageURL().toString());
                ctx.put("weibo_login", 1);

                // save user ID mapping
                t2wUser.setWeiboId(user.getScreenName());
            } catch (Exception e) {
                // 401 = not logged in
                if (e instanceof WeiboException && ((WeiboException) e).getStatusCode() != 401) {
                    e.printStackTrace();
                }
            }

            try {
                twitter4j.User user = (twitter4j.User) session.getAttribute(Keys.SESSION_TWITTER_USER);
                if (user == null) {
                    TwitterFactory factory = new TwitterFactory();
                    Twitter t = factory.getInstance();
                    t.setOAuthAccessToken(new AccessToken(t2wUser.getTwitterToken(), t2wUser.getTwitterTokenSecret()));

                    user = t.verifyCredentials();
                    session.setAttribute(Keys.SESSION_TWITTER_USER, user);
                }

                ctx.put("twitter_user", user.getScreenName());
                ctx.put("twitter_user_image", user.getProfileImageURL().toString());
                ctx.put("twitter_login", 1);
            } catch (Exception e) {
                // 401 = not logged in
                if (e instanceof TwitterException && ((TwitterException) e).getStatusCode() != 401) {
                    e.printStackTrace();
                }
            }
        }

        Object message = session.getAttribute(Keys.SESSION_MESSAGE);
        if (message != null) {
            ctx.put("message", message);
            session.removeAttribute(Keys.SESSION_MESSAGE);
        }

        Object prompt = session.getAttribute(Keys.SESSION_PROMPT_TWEET);
        if (prompt != null) {
            ctx.put("prompt", prompt);
            session.removeAttribute(Keys.SESSION_PROMPT_TWEET);
        }

        return getTemplate("user.vm");
    }
}