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

package h2weibo.filters;

import h2weibo.HttpServletRouter;
import h2weibo.Keys;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginUserFilter implements Filter {
    private static final Logger log = Logger.getLogger(LoginUserFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRouter r = new HttpServletRouter((HttpServletRequest) req);
        r.setPattern("/:id");

        HttpSession session = ((HttpServletRequest) req).getSession();
        String user = (String) session.getAttribute(Keys.SESSION_LOGIN_USER);

        String uri = ((HttpServletRequest) req).getRequestURI();
        log.info("Filtering " + uri);

        if ("/u/save".equals(uri) || r.is(":id", user)) {
            chain.doFilter(req, res);
        } else {
            if ("/u/logout".equals(uri)) {
                log.info("Logged @" + user + " out");
                session.removeAttribute(Keys.SESSION_LOGIN_USER);
                ((HttpServletResponse) res).sendRedirect("/");
            } else {
                log.info("Not logged in. Redirect to twitter login.");
                ((HttpServletResponse) res).sendRedirect("/auth/twitter");
            }
        }
    }

    @Override
    public void destroy() {

    }
}
