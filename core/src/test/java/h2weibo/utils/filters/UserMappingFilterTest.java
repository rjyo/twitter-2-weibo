package h2weibo.utils.filters;

import h2weibo.model.RedisHelper;
import junit.framework.TestCase;

/**
 * Test case for UserMappingFilter
 */
public class UserMappingFilterTest extends TestCase {
    public void setUp() throws Exception {
        RedisHelper helper = RedisHelper.getInstance();
        helper.setWeiboId("xuzhe", "xu_zhe");
        helper.setWeiboId("eatdami", "大米大仙");
    }

    public void testFilter() throws Exception {
        String input = "@xuzhe Let's eat dinner with @eatdami tonight. /cc @xu_lele.";
        UserMappingFilter filter = new UserMappingFilter();
        String s = filter.filter(input);
        assertEquals(s, "@xu_zhe Let's eat dinner with @大米大仙 tonight. /cc @xu_lele.");
    }
}
