package com.rakutec.weibo;

import com.rakutec.weibo.utils.HttpServletRouter;
import com.rakutec.weibo.utils.TweetID;
import org.apache.log4j.Logger;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Rakuraku Jyo
 */

public class UserServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UserServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRouter r = new HttpServletRouter(request);
        r.setPattern("/:id");

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        if (r.has(":id")) {
            TweetID tid= TweetID.findOneByUser(r.get(":id"));

            Weibo w = new Weibo();
            w.setToken(tid.getToken(), tid.getTokenSecret());

            try {
                User user = w.verifyCredentials();
                writer.println(user.getName());
            } catch (WeiboException e) {
                e.printStackTrace();
            }

        }

//        String user = request.get("u");
//        response.setContentType("text/plain");
//        PrintWriter writer = response.getWriter();
//
//        if (RedisHelper.getInstance().getUserCount() < 100) {
//            if (user != null && !"your_twitter_id".equals(user)) {
//                HttpSession session = request.getSession();
//                session.setAttribute("twitterUser", user);
//                try {
//                    String server = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
//
//                    Weibo weibo = new Weibo();
//                    RequestToken requestToken = weibo.getOAuthRequestToken(server + "/callback");
//
//                    response.setStatus(302);
//                    response.setHeader("Location", requestToken.getAuthenticationURL());
//                    session.setAttribute("token", requestToken.getToken());
//                    session.setAttribute("tokenSecret", requestToken.getTokenSecret());
//
//                    writer.println("Redirecting...");
//                    writer.close();
//                } catch (WeiboException e) {
//                    e.printStackTrace();
//                    log.error(e);
//                }
//            } else {
//                response.setStatus(200);
//                writer.println("Wrong parameter, not working!");
//                writer.close();
//            }
//        } else {
//            response.setStatus(200);
//            writer.println("Server reached max number of users. Please try again later.");
//            writer.close();
//        }
    }
}