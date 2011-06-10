package com.rakutec.weibo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class HelloServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(HelloServlet.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("Got Hello Message.");
        
		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("Say Hello to Twitter2Weibo");
		writer.close();
	}
}
