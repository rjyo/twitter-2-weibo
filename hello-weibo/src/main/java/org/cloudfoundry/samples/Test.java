package org.cloudfoundry.samples;


import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.List;

/**
 * @author Reilost
 */
public class Test {

    protected void printStatus(Status status, StringBuffer sb) {
        boolean isRT = false;
        if (status.getRetweeted_status() != null) {
            isRT = true;
        }
        sb.append(isRT ? "转发微博" : "原创微博").append(": ")
                .append(status.getUser().getScreenName()).append(" 在 ")
                .append(status.getCreatedAt()).append("时说:")
                .append(status.getText());
        if (isRT) {
            sb.append(" 转发原文 ").append(status.getRetweeted_status().getUser().getScreenName())
                    .append(" 说:").append(status.getRetweeted_status().getText());
        }
    }

    private void run() {
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        Weibo user = new Weibo();
        user.setToken("d9ddc51b9f84f211206eb4124a74601b", "35d1ff8d00d9093a666fbc705acc8629");

        Status updateStatus = null;
        try {
            updateStatus = user.updateStatus("进行update测试.....@Reilost "
                    + System.currentTimeMillis(), 20, 10);
            System.out.println("=======updateStatus=======");
            StringBuffer sb = new StringBuffer();
            printStatus(updateStatus, sb);
            System.out.println(sb);
        } catch (WeiboException e) {
            e.printStackTrace();
        }
    }

    private void runTwitter() {
        // gets Twitter instance with default credentials
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            List<twitter4j.Status> statuses;
            String user = "xu_lele";
            statuses = twitter.getUserTimeline(user);
            System.out.println("Showing @" + user + "'s user timeline.");
            for (twitter4j.Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
        }
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.runTwitter();
    }
}
