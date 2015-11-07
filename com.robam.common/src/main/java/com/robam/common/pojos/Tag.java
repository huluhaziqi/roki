package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.legent.dao.DaoHelper;
import com.legent.pojos.AbsStorePojo;
import com.robam.common.services.SysCfgManager;

import java.util.List;

public class Tag extends AbsStorePojo<Long> {

    @DatabaseField(id = true)
    @JsonProperty("id")
    public long id;

    @DatabaseField
    @JsonProperty("name")
    public String name;

    @DatabaseField
    @JsonProperty("imgUrl")
    public String imageUrl;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    protected Group group;

    /**
     * 本地存储的库版本号
     */
    @DatabaseField()
    public int version;

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isNewest() {
        int mainVer = SysCfgManager.getInstance().getLocalVersion();
        return version >= mainVer;
    }

    public Group getParent() {
        return group;
    }


    public void save2db(List<Recipe> books, List<Recipe3rd> thirdBooks) {

        if (books != null) {
            DaoHelper.deleteWhereEq(Tag_Recipe.class, Tag_Recipe.COLUMN_TAG_ID, id);
            Tag_Recipe tb;
            for (Recipe book : books) {
                book.save2db();

                tb = new Tag_Recipe(this, book);
                tb.save2db();
            }
        }

        // --------------------------------------------------------------------
        if (thirdBooks != null) {
            DaoHelper.deleteWhereEq(Tag_Recipe3rd.class,
                    Tag_Recipe3rd.COLUMN_TAG_ID, id);

            Tag_Recipe3rd tb;
            for (Recipe3rd book : thirdBooks) {
                book.save2db();

                tb = new Tag_Recipe3rd(this, book);
                tb.save2db();
            }
        }

        // --------------------------------------------------------------------
        version = SysCfgManager.getInstance().getCloudVersion();
        DaoHelper.update(this);
    }

}