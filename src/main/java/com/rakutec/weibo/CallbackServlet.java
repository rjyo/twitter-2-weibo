package com.rakutec.weibo;

import com.rakutec.weibo.utils.TweetID;
import org.apache.log4j.Logger;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.AccessToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Rakuraku Jyo
 */
public class CallbackServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(CallbackServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Weibo weibo = new Weibo();

        try {
            String token = (String) request.getSession().getAttribute("token");
            String tokenSecret = (String) request.getSession().getAttribute("tokenSecret");
            String twitterUser = (String) request.getSession().getAttribute("twitterUser");
            String oauthToken = request.getParameter("oauth_token");
            String oauthVerifier = request.getParameter("oauth_verifier");

            log.info("token = " + token);
            log.info("tokenSecret = " + tokenSecret);
            log.info("oauthToken = " + oauthToken);
            log.info("oauthVerifier = " + oauthVerifier);

            response.setContentType("text/plain");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            AccessToken accessToken = weibo.getOAuthAccessToken(token, tokenSecret, oauthVerifier);
            if (accessToken != null) {
                TweetID user = TweetID.findOneByUser(twitterUser);
                user.setToken(accessToken.getToken());
                user.setTokenSecret(accessToken.getTokenSecret());
                user.save();

                weibo.updateStatus("Hello from T2W Sync!");
                writer.write(twitterUser + " connected to Weibo.");
            } else {
                writer.write("Failed to get accesstoken.");
            }
            writer.close();
        } catch (WeiboException e) {
            e.printStackTrace();
        }
    }
}