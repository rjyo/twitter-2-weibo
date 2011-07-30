package h2weibo;

import h2weibo.model.DBHelper;

public abstract class DBTask implements Runnable, DBAccess {
    private DBHelper helper;

    public DBHelper getHelper() {
        return helper;
    }

    public void setHelper(DBHelper helper) {
        this.helper = helper;
    }
}
