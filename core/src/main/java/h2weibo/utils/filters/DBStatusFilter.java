package h2weibo.utils.filters;

import h2weibo.DBAccess;
import h2weibo.model.DBHelper;

public abstract class DBStatusFilter implements StatusFilter, DBAccess {
    private DBHelper helper;

    public DBHelper getHelper() {
        return this.helper;
    }

    public void setHelper(DBHelper helper) {
        this.helper = helper;
    }
}
