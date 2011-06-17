package com.rakutec.weibo;

import com.rakutec.weibo.utils.HttpServletRouter;
import com.rakutec.weibo.utils.T2WUser;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
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

        HttpSession session = request.getSession(false);
        String user = (String) session.getAttribute("user");
        String token = (String) session.getAttribute("token");
        String tokenSecret = (String) session.getAttribute("tokenSecret");
        String oauthVerifier = request.getParameter("oauth_verifier");

        if (r.is(":type", "weibo")) {
            try {
                Weibo weibo = new Weibo();

                AccessToken accessToken = weibo.getOAuthAccessToken(token, tokenSecret, oauthVerifier);
                if (accessToken != null) {
                    T2WUser tid = T2WUser.findOneByUser(user);
                    tid.setToken(accessToken.getToken());
                    tid.setTokenSecret(accessToken.getTokenSecret());
                    tid.save();

                    weibo.updateStatus("Hello from T2W Sync!");
                }
            } catch (WeiboException e) {
                log.error(e);
            }
        } else if (r.is(":type", "twitter")) {
            try {
                TwitterFactory factory = new TwitterFactory();
                Twitter t = factory.getInstance();

                twitter4j.auth.RequestToken req = (RequestToken) session.getAttribute("requestToken");
                twitter4j.auth.AccessToken accessToken = t.getOAuthAccessToken(req, oauthVerifier);
                session.removeAttribute("requestToken");

                if (accessToken != null) {
                    T2WUser tid = T2WUser.findOneByUser(user);
                    tid.setTwitterToken(accessToken.getToken());
                    tid.setTwitterTokenSecret(accessToken.getTokenSecret());
                    tid.save();
                    t.updateStatus("Hello from T2W Sync!");
                }
            } catch (TwitterException e) {
                log.error(e);
            }
        }
        //response.sendRedirect("/u/" + user);
    }
}