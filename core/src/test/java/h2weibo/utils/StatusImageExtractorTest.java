package h2weibo.utils;

import junit.framework.TestCase;

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

    public void testCameraPlus() throws Exception {
        extract = extractor.extract("hello http://campl.us/b6Fi testing!");
        assertNotNull(extract);
    }
}
