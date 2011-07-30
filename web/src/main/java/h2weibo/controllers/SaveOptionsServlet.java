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
        String actionType = request.getParameter("actionType");

        if ("delete".equals(actionType)) {
            user.delete();
            session.invalidate();
            response.sendRedirect("/");
        } else {
            String[] values = request.getParameterValues("options");

            if (values != null) {
                List<String> list = Arrays.asList(values);
                user.setOptions(new HashSet<String>(list));
            } else {
                user.setOptions(null);
            }
            user.save();

            session.setAttribute(Keys.SESSION_MESSAGE, "User Options Saved.");
            response.sendRedirect("/u/" + loginUser);
        }
    }
}