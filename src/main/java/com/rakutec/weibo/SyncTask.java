package com.rakutec.weibo;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {
    private String token;
    private String tokenSecret;
    private String screenName;

    public SyncTask(String token, String tokenSecret, String twitterScreenName) {
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.screenName = twitterScreenName;
    }

    public void run() {
        Twitter2Weibo t = new Twitter2Weibo(token, tokenSecret);
        t.syncTwitter(screenName);
	}
}