package h2weibo.utils;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusImageExtractor {
    private static final Logger log = Logger.getLogger(StatusImageExtractor.class.getName());
    private HashMap<String, String> simplePatterns = new HashMap<String, String>();

    public StatusImageExtractor() {
        simplePatterns.put("http://instagr.am/p/(\\w+)/", "http://instagr.am/p/_KEY_/media/");
        simplePatterns.put("http://twitpic.com/(\\w+)", "http://twitpic.com/show/large/_KEY_");
        simplePatterns.put("http://img.ly/(\\w+)", "http://img.ly/show/large/_KEY_");
        simplePatterns.put("http://yfrog.com/(\\w+)", "http://yfrog.com/_KEY_:iphone");
        simplePatterns.put("http://campl.us/(\\w+)", "http://campl.us/_KEY_:iphone");
    }

    public byte[] extract(String input) {
        if (input == null) return null;

        for (String key : simplePatterns.keySet()) {
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(input);
            
            if (m.find()) {
                String mediaUrl = simplePatterns.get(key);
                mediaUrl = mediaUrl.replaceAll("_KEY_", m.group(1));

                try {
                    URL url = new URL(mediaUrl);
                    InputStream in = url.openStream();
                    ByteOutputStream out = new ByteOutputStream();
                    for (int b; (b = in.read()) != -1; ) {
                        out.write(b);
                    }
                    byte[] bytes = out.getBytes();
                    out.close();
                    in.close();

                    return bytes;
                } catch (IOException e) {
                    log.error("Not able to download image", e);
                }
            }
        }

        return null;
    }
}
