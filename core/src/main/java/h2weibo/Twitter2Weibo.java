/*
 * (The MIT License)
 *
 * Copyright (c) 2011 Rakuraku Jyo <jyo.rakuraku@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package h2weibo;

import h2weibo.model.DBHelper;
import h2weibo.model.T2WUser;
import h2weibo.utils.StatusImageExtractor;
import h2weibo.utils.filters.*;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.http.ImageItem;
import weibo4j.model.WeiboException;

import java.util.List;

/**
 * @author Rakuraku Jyo
 */
public class Twitter2Weibo {
    private static final Logger log = Logger.getLogger(Twitter2Weibo.class.getName());
    private Weibo weibo;
    private DBHelper helper;


    public Twitter2Weibo(DBHelper helper) {
        this.helper = helper;
        weibo = new Weibo();
    }

    public void syncTwitter(String userId) {
        T2WUser user = helper.findOneByUser(userId);

        weibo.setToken(user.getToken());

        Twitter twitter = new TwitterFactory().getInstance();
        if (user.getTwitterToken() != null) {
            twitter.setOAuthAccessToken(new AccessToken(user.getTwitterToken(), user.getTwitterTokenSecret()));
            log.debug(String.format("Using OAuth for %s", user.getUserId()));
        }

        StatusFilters filters = new StatusFilters();
        filters.use(new NoSyncFilter()); // should be used first
        filters.use(new TcoStatusFilter()).use(new URLStatusFilter()).use(new TagStatusFilter()).use(new FlickrImageFilter());

        NoMentionFilter mentionFilter = new NoMentionFilter();
        UserMappingFilter mappingFilter = new UserMappingFilter(helper);

        if (!user.ready()) {
            log.debug(String.format("Skipping @%s ...", user.getUserId()));
            return;
        }

        // gets Twitter instance with default credentials
        String screenName = user.getUserId();
        long latestId = user.getLatestId();

        log.debug(String.format("Checking @%s's timeline, latest ID = %d.", userId, latestId));

        try {
            if (latestId == 0) {
                List<Status> statuses = twitter.getUserTimeline(screenName);
                if (statuses.size() > 0) {
                    user.setLatestId(statuses.get(0).getId()); // Record latestId, and sync next time
                }
                log.info(String.format("First time use for @%s. Set latest ID to %d.", userId, latestId));
            } else {
                Timeline tl = new Timeline();
                Paging paging = new Paging(latestId);
                List<Status> statuses = twitter.getUserTimeline(screenName, paging);

                // sync from the oldest one
                for (int i = statuses.size() - 1; i >= 0; i--) {
                    Status status = statuses.get(i);

                    if (status.getId() < user.getLatestId()) continue; // safe keeper

                    String name = status.getUser().getScreenName();
                    String statusText = status.getText();
                    log.info(String.format("@%s - %s", name, statusText));
                    try {
                        if (status.isRetweet()) {
                            if (user.isDropRetweets()) {
                                user.setLatestId(status.getId());
                                log.debug("Skipped " + statusText + " because status is a retweet.");
                                continue;
                            } else {
                                filters.remove(mentionFilter);
                                filters.use(mappingFilter);
                            }
                        } else {
                            if (user.isDropMentions()) {
                                filters.remove(mappingFilter);
                                filters.use(mentionFilter);
                            } else {
                                filters.remove(mentionFilter);
                                filters.use(mappingFilter);
                            }
                        }

                        statusText = filters.filter(statusText);
                        if (statusText == null) {
                            user.setLatestId(status.getId());
                            log.info(String.format("Skipped %s because of the filter.", statusText));
                            continue;
                        }

                        if (!user.isNoImage()) {
                            // add twitter images to status text
                            MediaEntity[] mediaEntities = status.getMediaEntities();
                            if (mediaEntities != null) {
                                for (MediaEntity entity : mediaEntities) {
                                    statusText += " " + entity.getMediaURL();
                                }
                                log.info("with media url: " + statusText);
                            }

                            StatusImageExtractor ex = new StatusImageExtractor();
                            StringBuffer buf = new StringBuffer(statusText);
                            byte[] image = ex.extract(buf);
                            if (image != null) {
                                user.setLatestId(status.getId());
                                try {
                                    statusText = buf.toString(); // with image urls removed
                                    tl.UploadStatus(statusText, new ImageItem(image));
                                    log.info(String.format("@%s - %s sent with image.", name, statusText));
                                } catch (WeiboException e) {
                                    log.error("Faile to update image.", e);
                                }
                                continue;
                            }
                        }

                        GeoLocation location = status.getGeoLocation();
                        if (user.isWithGeo() && location != null) {
                            tl.UpdateStatus(statusText, (float) location.getLatitude(), (float) location.getLongitude(), "");
                            log.info(String.format("@%s - %s sent with geo locations.", name, statusText));
                        } else {
                            tl.UpdateStatus(statusText);
                            log.info(String.format("@%s - %s sent.", name, statusText));
                        }
                    } catch (WeiboException e) {
                        if (e.getStatusCode() != 400) { // resending same tweet
                            log.warn("Failed to update Weibo");
                            break;
                        }
                    }
                    user.setLatestId(status.getId());
                }
            }
            helper.saveUser(user);
        } catch (Exception e) {
            if (!(e instanceof TwitterException)) {
                log.error("Failed to update.", e);
            }
        }
    }
}
