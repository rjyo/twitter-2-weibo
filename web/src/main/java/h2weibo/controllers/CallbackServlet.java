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
import twitter4j.*;
import twitter4j.auth.RequestToken;
import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Weibo;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Rakuraku Jyo
 */
public class CallbackServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(CallbackServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRouter r = new HttpServletRouter(request);
        r.setPattern("/:type");

        if (request.getParameter("denied") != null) {
            response.sendRedirect("/");
            return;
        }

        HttpSession session = request.getSession(false);
        String loginUser = (String) session.getAttribute(Keys.SESSION_LOGIN_USER);
        String oauthVerifier = request.getParameter("oauth_verifier");

        DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);

        if (r.is(":type", "weibo.jsp")) {
            String code = request.getParameter("code");

            if (code != null) {
                T2WUser tid = helper.findOneByUser(loginUser);

                if (tid.getToken() == null) { // send for the first time
                    session.setAttribute(Keys.SESSION_PROMPT_TWEET, "You are ready to go! Do you want to tweet about this service and share it with your friends?");
                }

                Oauth oauth = new Oauth();
                try {
                    AccessToken token = oauth.getAccessTokenByCode(code);
                    tid.setToken(token.getAccessToken());

                    Weibo weibo = new Weibo();
                    weibo.setToken(tid.getToken());
                    Account am = new Account();
                    try {
                        JSONObject obj = am.getUid();
                        String uid = obj.getString("uid");
                        tid.setWeiboUserId(uid);
                    } catch (WeiboException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    helper.saveUser(tid);
                } catch (WeiboException e) {
                    log.error(e);
                }

            } else {
                log.error("Can't auth " + loginUser + " for Weibo. " + request.getQueryString());
            }
        } else if (r.is(":type", "twitter")) {
            try {
                TwitterFactory factory = new TwitterFactory();
                Twitter t = factory.getInstance();

                twitter4j.auth.RequestToken req = (RequestToken) session.getAttribute(Keys.SESSION_REQUEST_TOKEN);
                twitter4j.auth.AccessToken accessToken = t.getOAuthAccessToken(req, oauthVerifier);
                session.removeAttribute(Keys.SESSION_REQUEST_TOKEN);

                if (accessToken != null) {
                    t.setOAuthAccessToken(accessToken);
                    User user = t.verifyCredentials();
                    loginUser = user.getScreenName();

                    T2WUser tid = helper.findOneByUser(loginUser);

                    if (tid.getTwitterToken() == null) {
                        // save latest id for the first time. sync from that tweet
                        ResponseList<Status> tl = t.getUserTimeline();
                        if (tl.size() > 0) {
                            Status s = tl.get(0);
                            tid.setLatestId(s.getId());
                        }
                    }

                    tid.setTwitterToken(accessToken.getToken());
                    tid.setTwitterTokenSecret(accessToken.getTokenSecret());
                    helper.saveUser(tid);

                    session.setAttribute(Keys.SESSION_LOGIN_USER, loginUser);
                }
            } catch (TwitterException e) {
                log.error("Twitter Exception", e);
                throw new RuntimeException(e);
            }
        }

        String requestUrl = (String) session.getAttribute(Keys.SESSION_REQUEST_URL);
        if (requestUrl != null) {
            session.removeAttribute(Keys.SESSION_REQUEST_URL);
            response.sendRedirect(requestUrl);
        } else {
            response.sendRedirect("/u/" + loginUser);
        }
    }
}