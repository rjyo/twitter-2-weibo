package com.rakutec.weibo.controllers;

import com.rakutec.weibo.utils.HttpServletRouter;
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