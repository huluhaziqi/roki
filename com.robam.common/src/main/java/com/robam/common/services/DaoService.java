package com.robam.common.services;

import com.legent.plat.Plat;
import com.legent.dao.AbsDaoService;
import com.legent.dao.AbsDbHelper;

/**
 * Created by sylar on 15/7/7.
 */
public class DaoService extends AbsDaoService {

    private static DaoService instance = new DaoService();

    public synchronized static DaoService getInstance() {
        return instance;
    }

    private DaoService() {
    }

    public void switchUser() {
        daos.clear();

        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }

        dbHelper = getDbHelper();
    }

    @Override
    public AbsDbHelper getDbHelper() {
        String dbName = getDbName();
        int dbVer = getDbVersion();

        DbHelper helper = DbHelper.newHelper(cx, dbName, dbVer);
        return helper;
    }


    @Override
    public String getDbName() {
        long curUserId = Plat.accountService.getCurrentUserId();
        return String.format("%s_%s", super.getDbName(), curUserId);
    }
}
