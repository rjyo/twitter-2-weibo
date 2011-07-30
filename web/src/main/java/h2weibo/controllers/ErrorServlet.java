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

import h2weibo.HttpServletRouter;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityLayoutServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Rakuraku Jyo
 */
public class ErrorServlet extends VelocityLayoutServlet {
    private static final Logger log = Logger.getLogger(ErrorServlet.class.getName());

    @Override
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
        HttpServletRouter r = new HttpServletRouter(request);
        r.setPattern("/:errid");
        String errorId = r.get(":errid");

        if (r.is(":errid", "404")) {
            Object url = request.getAttribute("javax.servlet.forward.request_uri");
            ctx.put("url", url);
            log.error("404 Error: " + url);
        } else {
            Exception ex = (Exception) request.getAttribute("javax.servlet.error.exception");
            log.error(errorId + " Error", ex);
            errorId = "500";
        }

        log.info("Redirect to " + errorId);
        return getTemplate(errorId + ".vm");
    }
}