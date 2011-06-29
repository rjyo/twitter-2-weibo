package h2weibo.utils;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: jyo
 * Date: 11/06/29
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
public class StatusImageExtractorTest extends TestCase {
    StatusImageExtractor extractor;
    private byte[] extract;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new StatusImageExtractor();
    }

    public void testInstagram() throws Exception {
        extract = extractor.extract("hello http://instagr.am/p/Ga_kl/ nice");
        assertNotNull(extract);
    }

    public void testTwitpic() throws Exception {
        extract = extractor.extract("hello http://twitpic.com/5h7gkh testing!");
        assertNotNull(extract);
    }

    public void testImgly() throws Exception {
        extract = extractor.extract("hello http://img.ly/5wsQ testing!");
        assertNotNull(extract);
    }

    public void testYfrog() throws Exception {
        extract = extractor.extract("hello http://yfrog.com/kl9w0yj testing!");
        assertNotNull(extract);
    }
}
