package h2weibo.filters;

import h2weibo.Keys;
import h2weibo.model.DBHelper;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DBFilter implements Filter {
    private static final Logger log = Logger.getLogger(DBFilter.class.getName());
    private ServletContext context = null;

    @Override
    public void init(FilterConfig config) throws ServletException {
        context = config.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        log.debug("In DBFilter");

        JedisPool jedisPool = (JedisPool) context.getAttribute(Keys.CONTEXT_JEDIS_POOL);
        DBHelper helper = new DBHelper(jedisPool.getResource());
        request.setAttribute(Keys.REQUEST_DB_HELPER, helper);

        chain.doFilter(req, res);

        jedisPool.returnResource(helper.getJedis());
        request.removeAttribute(Keys.REQUEST_DB_HELPER);
    }

    @Override
    public void destroy() {
        context = null;
    }
}
