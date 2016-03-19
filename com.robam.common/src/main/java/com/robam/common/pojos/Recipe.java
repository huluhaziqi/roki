package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.legent.Callback;
import com.legent.dao.DaoHelper;
import com.legent.plat.Plat;
import com.robam.common.services.CookbookManager;
import com.robam.common.services.SysCfgManager;

import java.util.Calendar;
import java.util.List;

/**
 * 菜谱
 *
 * @author sylar
 */
public class Recipe extends AbsRecipe {

    /**
     * 菜谱描述
     */
    @DatabaseField
    @JsonProperty("introduction")
    public String desc;

    /**
     * 所需时间 （秒）
     */
    @DatabaseField
    @JsonProperty("needTime")
    public int needTime;

    /**
     * 难度系数
     */
    @DatabaseField
    @JsonProperty("difficulty")
    public int difficulty;

    /**
     * 小图
     */
    @DatabaseField
    @JsonProperty("imgSmall")
    public String imgSmall;

    /**
     * 中图
     */
    @DatabaseField
    @JsonProperty("imgMedium")
    public String imgMedium;

    /**
     * 大图
     */
    @DatabaseField
    @JsonProperty("imgLarge")
    public String imgLarge;

    /**
     * 海报图
     */
    @DatabaseField
    @JsonProperty("imgPoster")
    public String imgPoster;

    /**
     * 本地存储的库版本号
     */
    @DatabaseField()
    public int version;

    /**
     * 是否有明细数据
     */
    @DatabaseField()
    protected boolean hasDetail;

    /**
     * 最近的明细数据更新时间
     */
    @DatabaseField()
    protected long lastUpgradeTime;

    /**
     * 食材清单
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "materials_id")
    @JsonProperty("materials")
    public Materials materials;

    /**
     * 备菜步骤
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    @JsonProperty("prepareSteps")
    public PreStep preStep;

    // ------------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------------------------

    @ForeignCollectionField()
    private ForeignCollection<CookStep> db_cookSteps;

    @JsonProperty("steps")
    public List<CookStep> js_cookSteps;

    // ----------------------------------------------------------------------------------------------------

    /**
     * 菜谱分享查看 的url链接
     *
     * @return
     */
    public String getViewUrl() {
        String url = String.format("%s/rest/api/cookbook/view?cookbookId=%s",
                Plat.serverOpt.getRestfulBaseUrl(), id);
        return url;
    }

    public boolean isNewest() {
        int mainVer = SysCfgManager.getInstance().getCloudVersion();
        //是否新版本
        boolean isNewest = version >= mainVer;

        //是否10分钟内更新
        boolean isNearest = Calendar.getInstance().getTimeInMillis() - lastUpgradeTime <= CookbookManager.UpdatePeriod;

        return isNewest && hasDetail && isNearest;
    }

    public void getDetail(Callback<Recipe> callback) {
        CookbookManager.getInstance().getCookbookById(id, callback);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // json2db
    // ------------------------------------------------------------------------------------------------------------------

    public List<CookStep> getCookSteps() {
        if (db_cookSteps == null || db_cookSteps.size() == 0)
            return Lists.newArrayList();

        List<CookStep> list = Lists.newArrayList(db_cookSteps);
        return list;
    }

    public void setDetailFalg(boolean falg) {
        hasDetail = falg;
        version = falg ? SysCfgManager.getInstance().getLocalVersion() : -1;
    }

    @Override
    public void save2db() {

        super.save2db();

        if (preStep != null) {
            preStep.save2db();
        }

        if (materials != null) {
            materials.save2db();
        }

        if (js_cookSteps != null) {
            DaoHelper.deleteWhereEq(CookStep.class, CookStep.COLUMN_BOOK_ID, id);
            for (CookStep cs : js_cookSteps) {
                cs.cookbook = this;
                cs.save2db();
            }
        }

        lastUpgradeTime = hasDetail ? Calendar.getInstance().getTimeInMillis() : 0;
        DaoHelper.update(this);
        DaoHelper.refresh(this);
    }


}