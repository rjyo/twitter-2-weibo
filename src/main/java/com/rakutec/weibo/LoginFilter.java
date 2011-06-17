package com.rakutec.weibo;

import com.rakutec.weibo.utils.HttpServletRouter;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    private static final Logger log = Logger.getLogger(LoginFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRouter r = new HttpServletRouter((HttpServletRequest) req);
        r.setPattern("/:id");

        HttpSession session = ((HttpServletRequest) req).getSession();
        String user = (String) session.getAttribute("login_user");


        if (user == null) {
            log.info("Not logged in. Redirect to twitter login.");
            ((HttpServletResponse) res).sendRedirect("/auth/twitter");
        } else {
            if (!r.is(":id", user)) {
                log.info("Different User. Redirect to twitter login.");
                ((HttpServletResponse) res).sendRedirect("/auth/twitter");
            } else {
                chain.doFilter(req, res);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
