package h2weibo;

import h2weibo.model.DBHelper;

public interface DBAccess {
    DBHelper getHelper();

    void setHelper(DBHelper helper);
}
