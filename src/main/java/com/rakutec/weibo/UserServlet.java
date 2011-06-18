package com.rakutec.weibo;

import com.rakutec.weibo.utils.HttpServletRouter;
import com.rakutec.weibo.utils.RedisHelper;
import com.rakutec.weibo.utils.T2WUser;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityLayoutServlet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import weibo4j.User;
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

        // Service limit
        RedisHelper helper = RedisHelper.getInstance();
        String uId = r.get(":id");
        if (!helper.isUser(uId) && helper.getUserCount() > 50) {
            return getTemplate("full.vm");
        }

        T2WUser t2wUser = T2WUser.findOneByUser(uId);
        if (r.has(":id")) {
            log.info("Displaying user info for @" + uId);

            HttpSession session = request.getSession();
            session.setAttribute("user", uId);
            ctx.put("user_id", uId);

            Weibo w = new Weibo();
            w.setToken(t2wUser.getToken(), t2wUser.getTokenSecret());

            try {
                User user = w.verifyCredentials();
                ctx.put("weibo_user", user);
                ctx.put("weibo_user_image", user.getProfileImageURL().toString());
                ctx.put("weibo_login", 1);
            } catch (Exception e) {
                // 401 = not logged in
                if (e instanceof WeiboException && ((WeiboException) e).getStatusCode() != 401) {
                    e.printStackTrace();
                }
            }

            try {
                TwitterFactory factory = new TwitterFactory();
                Twitter t = factory.getInstance();
                t.setOAuthAccessToken(new AccessToken(t2wUser.getTwitterToken(), t2wUser.getTwitterTokenSecret()));

                twitter4j.User user = t.verifyCredentials();
                ctx.put("twitter_user", user);
                ctx.put("twitter_user_image", user.getProfileImageURL().toString());
                ctx.put("twitter_login", 1);
            } catch (Exception e) {
                // 401 = not logged in
                if (e instanceof TwitterException && ((TwitterException) e).getStatusCode() != 401) {
                    e.printStackTrace();
                }
            }
        }

        return getTemplate("user.vm");
    }
}