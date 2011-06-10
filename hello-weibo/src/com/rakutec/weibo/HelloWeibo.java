package com.rakutec.weibo;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Url;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rakuraku Jyo
 */
public class HelloWeibo {
    private Weibo user = null;

    public HelloWeibo() {
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

    private String extendURL(String s) {
       // Create a pattern to match cat
        Pattern p = Pattern.compile("http://bit.ly/\\w+");
        // Create a matcher with an input string
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        // Loop through and create a new String with the replacements
        while(result) {
            String bitlyUrl = m.group();
            Url url = Bitly.as("rakuraku", "R_26c2081c74b8fd7fc4ce023738444187").call(Bitly.expand(bitlyUrl));
            m.appendReplacement(sb, url.getLongUrl());
            result = m.find();
        }
        // Add the last segment of input to the new String
        m.appendTail(sb);

        return sb.toString();
    }

    private String filterTwitterStatus(String status) {
        status = replace(status, "@xuzhe", "@徐哲-老徐");
        status = replace(status, "@xu_lele", "@乐库-老乐");
        
        return extendURL(status);
    }

    public void syncTwitter(String screenName) {
        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            TweetID tid = TweetID.loadTweetID(screenName);
            long latestId = tid.latestId;
            System.out.println("TID = " + latestId);

            List<twitter4j.Status> statuses;
            if (latestId == 0) {
                statuses = twitter.getUserTimeline(screenName);
            } else {
                Paging paging = new Paging(latestId);
                statuses = twitter.getUserTimeline(screenName, paging);
            }

            System.out.println("Showing @" + screenName + "'s userId timeline.");

            for (int i = statuses.size() - 1; i >= 0 ; i --) {
                twitter4j.Status status = statuses.get(i);
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                user.updateStatus(filterTwitterStatus(status.getText()));
                tid.update(status.getId());

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

    public static void main(String[] args) {
        HelloWeibo t = new HelloWeibo();
        t.syncTwitter("xu_lele");
    }
}
