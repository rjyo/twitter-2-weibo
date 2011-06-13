package com.rakutec.weibo;

import com.rakutec.weibo.filters.NoReplyFilter;
import com.rakutec.weibo.filters.StatusFilters;
import com.rakutec.weibo.filters.URLStatusFilter;
import org.apache.log4j.Logger;
import twitter4j.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.List;

/**
 * @author Rakuraku Jyo
 */
public class Twitter2Weibo {
    private static final Logger log = Logger.getLogger(Twitter2Weibo.class.getName());
    private Weibo user = null;
    private StatusFilters filters = new StatusFilters();
    private TweetIDJedis tid;

    public Twitter2Weibo(String twitterId) {
        tid = TweetIDJedis.getUser(twitterId);

        user = new Weibo();
        user.setToken(tid.getToken(), tid.getTokenSecret());

        filters.use(new NoReplyFilter()).use(new URLStatusFilter());
    }

    public void syncTwitter() {
        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory().getInstance();
        String screenName = tid.getUserId();
        long latestId = tid.getLatestId();
        log.info("= TID: " + latestId + " = ");

        log.info("Checking @" + screenName + "'s userId timeline.");

        try {
            if (latestId == 0) {
                List<Status> statuses = twitter.getUserTimeline(screenName);
                if (statuses.size() > 0)
                    tid.updateLatestId(statuses.get(0).getId()); // Record latestId, and sync next time
                log.info("Updating @" + screenName + "'s latestId to " + tid.getLatestId());
            } else {
                Paging paging = new Paging(latestId);
                List<Status> statuses = twitter.getUserTimeline(screenName, paging);

                for (int i = statuses.size() - 1; i >= 0; i--) {
                    twitter4j.Status status = statuses.get(i);
                    log.info("@" + status.getUser().getScreenName() + " - " + status.getText());
                    try {
                        String filtered = filters.filter(status.getText());
                        if (filtered != null) {
                            user.updateStatus(filtered);
                        } else {
                            log.info("Skipped " + status.getText() + " because of the filter.");
                        }
                    } catch (WeiboException e) {
                        if (e.getStatusCode() != 400) { // resending same tweet
                            log.warn("Failed to update Weibo");
                            throw new RuntimeException(e);
                        }
                    }
                    tid.updateLatestId(status.getId()); // still update the latestId to skip
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
        Twitter2Weibo t = new Twitter2Weibo("xu_lele");
        t.syncTwitter();
    }
}
