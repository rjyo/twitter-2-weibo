package com.rakutec.weibo.utils;

import com.rakutec.weibo.filters.NoReplyFilter;
import com.rakutec.weibo.filters.StatusFilters;
import com.rakutec.weibo.filters.TagStatusFilter;
import com.rakutec.weibo.filters.URLStatusFilter;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.List;

/**
 * @author Rakuraku Jyo
 */
public class Twitter2Weibo {
    private static final Logger log = Logger.getLogger(Twitter2Weibo.class.getName());
    private Weibo weibo;
    private Twitter twitter;

    private StatusFilters filters = new StatusFilters();
    private T2WUser user;

    public Twitter2Weibo(String id) {
        user = T2WUser.findOneByUser(id);

        weibo = new Weibo();
        weibo.setToken(user.getToken(), user.getTokenSecret());

        twitter = new TwitterFactory().getInstance();
        if (user.getTwitterToken() != null) {
            twitter.setOAuthAccessToken(new AccessToken(user.getTwitterToken(), user.getTwitterTokenSecret()));
            log.info("Using OAuth for " + id);
        }

        filters.use(new NoReplyFilter()).use(new URLStatusFilter()).use(new TagStatusFilter());
    }

    public void syncTwitter() {
        // gets Twitter instance with default credentials
        String screenName = user.getUserId();
        long latestId = user.getLatestId();
        log.info("= TID: " + latestId + " = ");

        log.info("Checking @" + screenName + "'s userId timeline.");


        try {
            if (latestId == 0) {
                List<Status> statuses = twitter.getUserTimeline(screenName);
                if (statuses.size() > 0)
                    user.updateLatestId(statuses.get(0).getId()); // Record latestId, and sync next time
                log.info("Updating @" + screenName + "'s latestId to " + user.getLatestId());
            } else {
                Paging paging = new Paging(latestId);
                List<Status> statuses = twitter.getUserTimeline(screenName, paging);

                for (int i = statuses.size() - 1; i >= 0; i--) {
                    twitter4j.Status status = statuses.get(i);
                    log.info("@" + status.getUser().getScreenName() + " - " + status.getText());
                    try {
                        String filtered = filters.filter(status.getText());
                        if (filtered != null) {
                            weibo.updateStatus(filtered);
                        } else {
                            log.info("Skipped " + status.getText() + " because of the filter.");
                        }
                    } catch (WeiboException e) {
                        if (e.getStatusCode() != 400) { // resending same tweet
                            log.warn("Failed to update Weibo");
                            throw new RuntimeException(e);
                        }
                    }
                    user.updateLatestId(status.getId()); // still update the latestId to skip
                    Thread.sleep(500);
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
