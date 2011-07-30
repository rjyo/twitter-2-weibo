package h2weibo.filters;

import h2weibo.Keys;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminFilter implements Filter {
    private static final Logger log = Logger.getLogger(AdminFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) req).getSession();
        String user = (String) session.getAttribute(Keys.SESSION_LOGIN_USER);

        if (!"xu_lele".equals(user)) {
            log.info("Not logged in. Redirect to twitter login.");
            session.setAttribute(Keys.SESSION_REQUEST_URL, ((HttpServletRequest) req).getRequestURL().toString());
            ((HttpServletResponse) res).sendRedirect("/auth/twitter");
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }
}
