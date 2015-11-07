package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.legent.dao.DaoHelper;
import com.legent.pojos.AbsStorePojo;

abstract public class AbsRecipe extends AbsStorePojo<Long> {

    public final static String COLUMN_ID = "id";
    static final public String COLUMN_isToday = "isToday";
    static final public String COLUMN_isFavority = "isFavority";
    static final public String COLUMN_isRecommend = "isRecommend";

    @DatabaseField(id = true, columnName = COLUMN_ID)
    @JsonProperty("id")
    public long id;

    @DatabaseField
    @JsonProperty("name")
    public String name;

    /**
     * 供应商ID
     */
    @DatabaseField
    @JsonProperty("sourceId")
    public long providerId;

    /**
     * 收藏次数
     */
    @DatabaseField
    @JsonProperty("collectCount")
    public int collectCount;

    /**
     * 是否在今日菜单
     */
    @DatabaseField(columnName = COLUMN_isToday)
    public boolean isToday;

    /**
     * 是否是收藏菜谱
     */
    @DatabaseField(columnName = COLUMN_isFavority)
    public boolean isFavority;

    /**
     * 是否推荐菜谱
     */
    @DatabaseField(columnName = COLUMN_isRecommend)
    public boolean isRecommend;

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

    /**
     * 是否Roki内置菜谱
     */
    public boolean isRoki() {
        return providerId <= 0;
    }

    public RecipeProvider getProvider() {
        if (providerId > 0)
            return DaoHelper.getById(RecipeProvider.class, providerId);

        else
            return null;
    }

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------


    @Override
    public void save2db() {
        AbsRecipe book = DaoHelper.getById(getClass(), id);
        boolean isToday = book != null && book.isToday;
        boolean isFavority = book != null && book.isFavority;
        boolean isRecommend = book != null && book.isRecommend;
        DaoHelper.deleteWhereEq(getClass(), COLUMN_ID, id);

        this.isToday = isToday;
        this.isFavority = isFavority;
        this.isRecommend = isRecommend;
        DaoHelper.create(this);

//        DaoHelper.refresh(this);
    }

    public void setIsToday(boolean value) {
        if (DaoHelper.isExists(getClass(), id)) {
            DaoHelper.setFieldWhereEq(getClass(), COLUMN_isToday, value, COLUMN_ID, id);
            DaoHelper.refresh(this);
        } else {
            this.isToday = value;
            save2db();
        }
    }

    public void setIsFavority(boolean value) {
        if (DaoHelper.isExists(getClass(), id)) {
            DaoHelper.setFieldWhereEq(getClass(), COLUMN_isFavority, value, COLUMN_ID, id);
            DaoHelper.refresh(this);
        } else {
            this.isFavority = value;
            save2db();
        }

    }

    public void setIsRecommend(boolean value) {
        if (DaoHelper.isExists(getClass(), id)) {
            DaoHelper.setFieldWhereEq(getClass(), COLUMN_isRecommend, value, COLUMN_ID, id);
            DaoHelper.refresh(this);
        } else {
            this.isRecommend = value;
            save2db();
        }
    }


    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------
    static public void setIsToday(long recipeId, boolean value) {
        Recipe recipe = DaoHelper.getById(Recipe.class, recipeId);
        if (recipe != null) {
            recipe.setIsToday(value);
        }
        Recipe3rd recipe3rd = DaoHelper.getById(Recipe3rd.class, recipeId);
        if (recipe3rd != null) {
            recipe3rd.setIsToday(value);
        }
    }

    static public void setIsFavority(long recipeId, boolean value) {
        Recipe recipe = DaoHelper.getById(Recipe.class, recipeId);
        if (recipe != null) {
            recipe.setIsFavority(value);
            recipe.collectCount = value ? recipe.collectCount + 1 : recipe.collectCount - 1;
            DaoHelper.update(recipe);
        }

        Recipe3rd recipe3rd = DaoHelper.getById(Recipe3rd.class, recipeId);
        if (recipe3rd != null) {
            recipe3rd.setIsFavority(value);
            recipe3rd.collectCount = value ? recipe3rd.collectCount + 1 : recipe3rd.collectCount - 1;
            DaoHelper.update(recipe3rd);
        }
    }


    static public void setIsRecommend(long recipeId, boolean value) {
        Recipe recipe = DaoHelper.getById(Recipe.class, recipeId);
        if (recipe != null) {
            recipe.setIsRecommend(value);
        }
        Recipe3rd recipe3rd = DaoHelper.getById(Recipe3rd.class, recipeId);
        if (recipe3rd != null) {
            recipe3rd.setIsRecommend(value);
        }
    }

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------


}
