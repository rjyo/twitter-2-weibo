package com.rakutec.weibo;

import com.rakutec.weibo.model.T2WUser;
import com.rakutec.weibo.utils.StatusImageExtractor;
import com.rakutec.weibo.utils.filters.NoMentionFilter;
import com.rakutec.weibo.utils.filters.StatusFilters;
import com.rakutec.weibo.utils.filters.TagStatusFilter;
import com.rakutec.weibo.utils.filters.URLStatusFilter;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.auth.AccessToken;
import weibo4j.*;
import weibo4j.http.ImageItem;

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

        filters.use(new URLStatusFilter()).use(new TagStatusFilter());

        if (user.isDropMentions()) {
            filters.use(new NoMentionFilter());
        }
    }

    public void syncTwitter() {
        if (!user.ready()) {
            log.info("Skipping @" + user.getUserId() + " ...");
            return;
        }

        // gets Twitter instance with default credentials
        String screenName = user.getUserId();
        long latestId = user.getLatestId();
        log.info("= TID: " + latestId + " = ");

        log.info("Checking @" + screenName + "'s userId timeline.");

        try {
            if (latestId == 0) {
                List<Status> statuses = twitter.getUserTimeline(screenName);
                if (statuses.size() > 0) {
                    user.setLatestId(statuses.get(0).getId()); // Record latestId, and sync next time
                }
                log.info("Updating @" + screenName + "'s latestId to " + user.getLatestId());
            } else {
                Paging paging = new Paging(latestId);
                List<Status> statuses = twitter.getUserTimeline(screenName, paging);

                for (int i = statuses.size() - 1; i >= 0; i--) {
                    twitter4j.Status status = statuses.get(i);
                    log.info("@" + status.getUser().getScreenName() + " - " + status.getText());
                    try {
                        if (user.isDropRTAndReply() && status.isRetweet()) {
                            user.setLatestId(status.getId());
                            log.info("Skipped " + status.getText() + " because status is a retweet.");
                            continue;
                        }

                        String filtered = filters.filter(status.getText());
                        if (filtered == null) {
                            user.setLatestId(status.getId());
                            log.info("Skipped " + status.getText() + " because of the filter.");
                            continue;
                        }


                        if (!user.isNoImage()) {
                            StatusImageExtractor ex = new StatusImageExtractor();
                            byte[] image = ex.extract(status.getText());
                            if (image != null) {
                                user.setLatestId(status.getId());
                                weibo.uploadStatus(status.getText(), new ImageItem(image));
                                log.info("@" + status.getUser().getScreenName() + " - " + status.getText() + " sent with image.");
                                continue;
                            }
                        }

                        GeoLocation location = status.getGeoLocation();
                        if (user.isWithGeo() && location != null) {
                            weibo.updateStatus(filtered, location.getLatitude(), location.getLongitude());
                            log.info("@" + status.getUser().getScreenName() + " - " + status.getText() + " sent with geo locations.");
                        } else {
                            weibo.updateStatus(filtered);
                            log.info("@" + status.getUser().getScreenName() + " - " + status.getText() + " sent.");
                        }

                        user.setLatestId(status.getId());
                    } catch (WeiboException e) {
                        if (e.getStatusCode() != 400) { // resending same tweet
                            log.warn("Failed to update Weibo");
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            user.save();
        } catch (Exception e) {
            log.error("Failed to get timeline: " + e.getMessage());
        }
    }
}
