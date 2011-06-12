package com.rakutec.weibo;

import com.rakutec.weibo.filters.KVStatusFilter;
import com.rakutec.weibo.filters.StatusFilter;
import com.rakutec.weibo.filters.StatusFilters;
import com.rakutec.weibo.filters.URLStatusFilter;
import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Url;
import twitter4j.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.List;

import org.apache.log4j.Logger;

import javax.enterprise.inject.New;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rakuraku Jyo
 */
public class Twitter2Weibo {
    private static final Logger log = Logger.getLogger(Twitter2Weibo.class.getName());
    private Weibo user = null;
    private StatusFilters filters = new StatusFilters();

    public Twitter2Weibo(String token, String tokenSecret) {
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        user = new Weibo();
        user.setToken(token, tokenSecret);

        KVStatusFilter kvFilter = new KVStatusFilter();
        kvFilter.add("@xuzhe", "@徐哲-老徐");
        kvFilter.add("@xu_lele", "@乐库-老乐");
        filters.use(kvFilter).use(new URLStatusFilter());
    }
//
//    private String replace(String s, String orig, String repl) {
//        // Create a pattern to match cat
//        Pattern p = Pattern.compile(orig);
//        // Create a matcher with an input string
//        Matcher m = p.matcher(s);
//        StringBuffer sb = new StringBuffer();
//        boolean result = m.find();
//        // Loop through and create a new String with the replacements
//        while (result) {
//            m.appendReplacement(sb, repl);
//            result = m.find();
//        }
//        // Add the last segment of input to the new String
//        m.appendTail(sb);
//
//        return sb.toString();
//    }


    public void syncTwitter(String screenName) {
        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            TweetIDJedis tid = TweetIDJedis.loadTweetID(screenName);
            long latestId = tid.latestId;
            log.info("= TID: " + latestId + " = ");

            log.info("Checking @" + screenName + "'s userId timeline.");

            if (latestId == 0) {
                List<Status> statuses = twitter.getUserTimeline(screenName);
                if (statuses.size() > 0)
                tid.update(statuses.get(0).getId()); // Record latestId, and sync next time
            } else {
                Paging paging = new Paging(latestId);
                List<Status> statuses = twitter.getUserTimeline(screenName, paging);

                for (int i = statuses.size() - 1; i >= 0; i--) {
                    twitter4j.Status status = statuses.get(i);
                    log.info("@" + status.getUser().getScreenName() + " - " + status.getText());
                    try {
                        user.updateStatus(filters.filter(status.getText()));
                        tid.update(status.getId());
                    } catch (WeiboException e) {
                        if (e.getStatusCode() != 400) { // resending same tweet
                            log.warn("Failed to update Weibo");
                            throw new RuntimeException(e);
                        }
                    }
                    tid.update(status.getId()); // still update the latestId to skip
                    Thread.sleep(1000);
                }
            }
        } catch (TwitterException te) {
            log.warn("Failed to get timeline: " + te.getMessage());
            throw new RuntimeException(te);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Twitter2Weibo t = new Twitter2Weibo("d9ddc51b9f84f211206eb4124a74601b", "35d1ff8d00d9093a666fbc705acc8629");
        t.syncTwitter("xu_lele");
    }
}
