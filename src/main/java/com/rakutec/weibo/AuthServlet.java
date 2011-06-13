package com.rakutec.weibo;

import org.apache.log4j.Logger;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.RequestToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Rakuraku Jyo
 */
public class AuthServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AuthServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("u");
        request.getSession().setAttribute("twitterUser", user);

        if (user != null) {
            try {
                String server = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

                Weibo weibo = new Weibo();
                RequestToken requestToken = weibo.getOAuthRequestToken(server + "/callback");

                response.setContentType("text/html");
                response.setStatus(302);
                response.setHeader("Location", requestToken.getAuthenticationURL());
                PrintWriter writer = response.getWriter();
                request.getSession().setAttribute("token", requestToken.getToken());
                request.getSession().setAttribute("tokenSecret", requestToken.getTokenSecret());

                writer.write("<h3>Redirecting</h3>");
                writer.close();
            } catch (WeiboException e) {
                e.printStackTrace();
                log.error(e);
            }
        } else {
            response.setContentType("text/html");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            writer.write("<h3>Not working</h3>");
            writer.close();

        }
    }
}