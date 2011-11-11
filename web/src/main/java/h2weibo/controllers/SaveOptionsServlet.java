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

package h2weibo.controllers;

import h2weibo.Keys;
import h2weibo.model.DBHelper;
import h2weibo.model.T2WUser;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Rakuraku Jyo
 */
public class SaveOptionsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SaveOptionsServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loginUser = (String) session.getAttribute(Keys.SESSION_LOGIN_USER);
        log.info("Saving options for @" + loginUser);

        DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);
        T2WUser user = helper.findOneByUser(loginUser);

        String[] values = request.getParameterValues("options");

        if (values != null) {
            List<String> list = Arrays.asList(values);
            user.setOptions(new HashSet<String>(list));
        } else {
            user.setOptions(null);
        }
        helper.saveUser(user);

        session.setAttribute(Keys.SESSION_MESSAGE, "User Options Saved.");
        response.setStatus(200);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loginUser = (String) session.getAttribute(Keys.SESSION_LOGIN_USER);
        log.info("Deleting user @" + loginUser);

        DBHelper helper = (DBHelper) request.getAttribute(Keys.REQUEST_DB_HELPER);
        T2WUser user = helper.findOneByUser(loginUser);
        helper.deleteUser(user);
        session.invalidate();
        response.setStatus(200);
    }
}