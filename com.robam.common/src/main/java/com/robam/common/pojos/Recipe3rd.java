package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;

/**
 * 第三方菜谱
 *
 * @author sylar
 */
public class Recipe3rd extends AbsRecipe {

    @DatabaseField
    @JsonProperty("imgUrl")
    public String imgUrl;

    @DatabaseField
    @JsonProperty("detailUrl")
    public String detailUrl;

}
