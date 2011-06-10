package com.rakutec.weibo;

/**
 * @author Rakuraku Jyo
 */
public class SyncTask implements Runnable {

	public void run() {
        T2Weibo t = new T2Weibo();
        t.syncTwitter("xu_lele");
	}

}