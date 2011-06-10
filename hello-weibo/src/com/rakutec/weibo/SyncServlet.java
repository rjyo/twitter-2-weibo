package com.rakutec.weibo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SyncServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setStatus(200);

        HelloWeibo t = new HelloWeibo();
        t.syncTwitter("xu_lele");

		PrintWriter writer = response.getWriter();
		writer.println("Done");
		writer.close();
	}
}
