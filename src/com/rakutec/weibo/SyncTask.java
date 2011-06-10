package com.rakutec.weibo;

/**
 * Created by IntelliJ IDEA.
 * User: jyo
 * Date: 11/06/11
 * Time: 5:20
 * To change this template use File | Settings | File Templates.
 */
public class SyncTask implements Runnable {

	public void run() {
        T2Weibo t = new T2Weibo();
        t.syncTwitter("xu_lele");
	}

}