package com.robam.common.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.collect.Lists;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.legent.dao.AbsDbHelper;
import com.robam.common.pojos.Advert.MobAdvert;
import com.robam.common.pojos.Advert.PadAdvert;
import com.robam.common.pojos.AdvertImage;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.pojos.CookStep;
import com.robam.common.pojos.CookStepTip;
import com.robam.common.pojos.CookStepTipMaterial;
import com.robam.common.pojos.Group;
import com.robam.common.pojos.Material;
import com.robam.common.pojos.Materials;
import com.robam.common.pojos.PreStep;
import com.robam.common.pojos.PreSubStep;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.pojos.RecipeProvider;
import com.robam.common.pojos.SysCfg;
import com.robam.common.pojos.Tag;
import com.robam.common.pojos.Tag_Recipe;
import com.robam.common.pojos.Tag_Recipe3rd;

import java.sql.SQLException;
import java.util.List;

public class DbHelper extends AbsDbHelper {


    static List<Class<?>> tables = Lists.newLinkedList();

    static public DbHelper newHelper(Context cx, String dbName, int dbVersion) {

        // ---------------------------------------------
        tables.add(SysCfg.class);
        // ---------------------------------------------
        tables.add(MobAdvert.class);
        tables.add(PadAdvert.class);
        tables.add(AdvertImage.class);
        tables.add(CookAlbum.class);
        tables.add(RecipeProvider.class);
        // ---------------------------------------------
        tables.add(Group.class);
        tables.add(Tag.class);
        // ---------------------------------------------
        tables.add(Recipe.class);
        tables.add(PreStep.class);
        tables.add(PreSubStep.class);
        tables.add(CookStep.class);
        tables.add(CookStepTip.class);
        tables.add(CookStepTipMaterial.class);
        tables.add(Materials.class);
        tables.add(Material.class);
        // ---------------------------------------------
        tables.add(Recipe3rd.class);
        tables.add(Tag_Recipe.class);
        tables.add(Tag_Recipe3rd.class);
        // ---------------------------------------------
        return new DbHelper(cx, dbName, dbVersion);
    }

    private DbHelper(Context cx, String dbName, int dbVersion) {
        super(cx, dbName, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sd, ConnectionSource cs) {
        super.onCreate(sd, cs);

        try {

            for (Class<?> clazz : tables) {
                TableUtils.createTable(cs, clazz);
                Log.d(TAG, "table created :" + clazz.getSimpleName());
            }

            Log.d(TAG, String.format("DB created:%s table count:%s",
                    getDatabaseName(), tables.size()));
        } catch (Exception e) {
            Log.e(TAG, "DB created error:" + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sd, ConnectionSource cs,
                          int oldVersion, int newVersion) {

        super.onUpgrade(sd,cs,oldVersion,newVersion);

        dropAll(cs);
        onCreate(sd, cs);
    }

    private void dropAll(ConnectionSource cs) {

        try {
            for (Class<?> clazz : tables) {
                TableUtils.dropTable(cs, clazz, true);
            }

            Log.d(TAG, "DB droped");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}