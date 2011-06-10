package org.cloudfoundry.samples;


import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rakuraku Jyo
 */
public class HelloWeibo {
    private static String ID_FILE = "latest_id.txt";
    Weibo user = null;

    protected HelloWeibo() {
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        user = new Weibo();
        user.setToken("d9ddc51b9f84f211206eb4124a74601b", "35d1ff8d00d9093a666fbc705acc8629");
    }

    private String replace(String s, String orig, String repl) {
       // Create a pattern to match cat
        Pattern p = Pattern.compile(orig);
        // Create a matcher with an input string
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        // Loop through and create a new String with the replacements
        while(result) {
            m.appendReplacement(sb, repl);
            result = m.find();
        }
        // Add the last segment of input to the new String
        m.appendTail(sb);

        return sb.toString();
    }

    private String filterTwitterStatus(String status) {
        status = replace(status, "@xuzhe", "@徐哲-老徐");
        status = replace(status, "@xu_lele", "@乐库-乐乐");
        return status;
    }

    private void runTwitter() {
        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            long latestId = readLatestId();

            List<twitter4j.Status> statuses;
            String screenName = "xu_lele";
            if (latestId == 0) {
                statuses = twitter.getUserTimeline(screenName);
            } else {
                Paging paging = new Paging(latestId);
                statuses = twitter.getUserTimeline(screenName, paging);
            }

            System.out.println("Showing @" + screenName + "'s user timeline.");

            for (int i = statuses.size() - 1; i >= 0 ; i --) {
                twitter4j.Status status = statuses.get(i);
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                user.updateStatus(filterTwitterStatus(status.getText()), 35.8617292, 139.6454822);
                writeLatestId(String.valueOf(status.getId()));

                Thread.sleep(1000);
            }
        } catch (TwitterException te) {
            System.out.println("Failed to get timeline: " + te.getMessage());
        } catch (WeiboException e) {
            System.out.println("Failed to sendto Weibo: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeLatestId(String id) {
        FileWriter outFile;
        try {
            outFile = new FileWriter(ID_FILE, false);
            PrintWriter out = new PrintWriter(outFile);
            out.println(id);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long readLatestId() {
        FileReader inputFileReader;
        try {
            inputFileReader = new FileReader(ID_FILE);
            BufferedReader input = new BufferedReader(inputFileReader);

            String id = input.readLine();
            input.close();
            return Long.valueOf(id);
        } catch (FileNotFoundException e) {
            System.out.println("not found latest id, first time usage.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        HelloWeibo t = new HelloWeibo();
//        t.runTwitter();

        String s = t.replace("这个看起来真的不错 http://bit.ly/ij2bnX", "http:\\/\\/bit.ly\\/\\w+", "hello!");
        System.out.println(s);
    }
}
