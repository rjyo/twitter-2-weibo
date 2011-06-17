package com.rakutec.weibo;

import com.rakutec.weibo.utils.HttpServletRouter;
import com.rakutec.weibo.utils.RedisHelper;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.RequestToken;

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

        HttpSession session = request.getSession(false);

        if (RedisHelper.getInstance().getUserCount() < 10) {
            if (r.is(":type", "weibo")) {
                try {
                    Weibo weibo = new Weibo();
                    RequestToken requestToken = weibo.getOAuthRequestToken(serverPath + "/callback/weibo");

                    response.setStatus(302);
                    response.setHeader("Location", requestToken.getAuthenticationURL());
                    session.setAttribute("token", requestToken.getToken());
                    session.setAttribute("tokenSecret", requestToken.getTokenSecret());

                    log.info("Redirecting Weibo...");
                } catch (WeiboException e) {
                    log.error(e);
                }
            } else if (r.is(":type", "twitter")) {
                try {
                    TwitterFactory factory = new TwitterFactory();
                    Twitter t = factory.getInstance();
                    twitter4j.auth.RequestToken requestToken = t.getOAuthRequestToken(serverPath + "/callback/twitter");

                    response.setStatus(302);
                    response.setHeader("Location", requestToken.getAuthenticationURL());
                    session.setAttribute("requestToken", requestToken);

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
        } else {
            response.setStatus(200);
            writer.println("Server reached max number of users. Please try again later.");
            writer.close();
        }
    }
}