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
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import weibo4j.Oauth;
import weibo4j.model.WeiboException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Rakuraku Jyo
 */
public class AuthServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AuthServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRouter r = new HttpServletRouter(request);
        r.setPattern("/:type");

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        String serverPath = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        HttpSession session = request.getSession();

        if (r.is(":type", "weibo")) {
            try {
                Oauth oauth = new Oauth();
                String redirectUrl = oauth.authorize("code");

                response.setStatus(302);
                response.setHeader("Location", redirectUrl);

                log.info("Redirecting Weibo...");
            } catch (WeiboException e) {
                log.error(e);
            }
        } else if (r.is(":type", "twitter")) {
            log.info("hello world~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            try {
                TwitterFactory factory = new TwitterFactory();
                Twitter t = factory.getInstance();
                twitter4j.auth.RequestToken requestToken = t.getOAuthRequestToken(serverPath + "/callback/twitter");

                response.setStatus(302);

                log.info(requestToken.getAuthenticationURL());
                response.setHeader("Location", requestToken.getAuthenticationURL());
                session.setAttribute(Keys.SESSION_REQUEST_TOKEN, requestToken);

                log.info("Redirecting Twitter...");
            } catch (TwitterException e) {
                log.error(e);
            }
            writer.close();
        } else {
            response.setStatus(200);
            writer.println("Wrong parameter, not working!");
            writer.close();
        }
    }
}