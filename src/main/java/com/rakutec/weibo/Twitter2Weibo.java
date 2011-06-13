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

    public Twitter2Weibo(String token, String tokenSecret) {
        user = new Weibo();
        user.setToken(token, tokenSecret);

        filters.use(new NoReplyFilter()).use(new URLStatusFilter());
    }

    public void syncTwitter(String screenName) {
        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            TweetIDJedis tid = TweetIDJedis.loadUser(screenName);
            long latestId = tid.getLatestId();
            log.info("= TID: " + latestId + " = ");

            log.info("Checking @" + screenName + "'s userId timeline.");

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
                        }
                        tid.updateLatestId(status.getId());
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
        Twitter2Weibo t = new Twitter2Weibo("d9ddc51b9f84f211206eb4124a74601b", "35d1ff8d00d9093a666fbc705acc8629");
        t.syncTwitter("xu_lele");
    }
}
