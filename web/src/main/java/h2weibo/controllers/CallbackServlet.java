package h2weibo.controllers;

import h2weibo.HttpServletRouter;
import h2weibo.Keys;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.auth.RequestToken;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.AccessToken;

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
        String token = (String) session.getAttribute(Keys.SESSION_TOKEN);
        String tokenSecret = (String) session.getAttribute(Keys.SESSION_TOKEN_SECRET);
        String oauthVerifier = request.getParameter("oauth_verifier");

        if (r.is(":type", "weibo")) {
            try {
                Weibo weibo = new Weibo();

                AccessToken accessToken = weibo.getOAuthAccessToken(token, tokenSecret, oauthVerifier);
                if (accessToken != null) {
                    T2WUser tid = T2WUser.findOneByUser(loginUser);

                    if (tid.getToken() == null) { // send for the first time
                        session.setAttribute(Keys.SESSION_PROMPT_TWEET, "You are ready to go! Do you want to tweet about this service and share it with your friends?");
                    }

                    tid.setToken(accessToken.getToken());
                    tid.setTokenSecret(accessToken.getTokenSecret());
                    tid.save();
                } else {
                    log.error("Can't auth " + loginUser + " for Weibo. " + request.getQueryString());
                }
            } catch (WeiboException e) {
                log.error("Weibo Exception", e);
                throw new RuntimeException(e);
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

                    T2WUser tid = T2WUser.findOneByUser(loginUser);

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
                    tid.save();

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