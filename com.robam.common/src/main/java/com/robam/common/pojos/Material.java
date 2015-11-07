package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.j256.ormlite.field.DatabaseField;
import com.legent.dao.DaoHelper;
import com.legent.pojos.AbsStorePojo;

import java.util.List;

public class Material extends AbsStorePojo<Long> {

    static final public String FIELD_NAME_Materials_ID = "materials_id";
    static final public String FIELD_NAME_isMain = "isMain";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = FIELD_NAME_isMain)
    public boolean isMain;

    @JsonProperty("id")
    public long serviceId;

    @DatabaseField
    @JsonProperty("name")
    public String name;

    @DatabaseField
    @JsonProperty("standardUnit")
    public String u1;

    @DatabaseField
    @JsonProperty("standardWeight")
    public String w1;

    @DatabaseField
    @JsonProperty("popularUnit")
    public String u2;

    @DatabaseField
    @JsonProperty("popularWeight")
    public String w2;

    @JsonProperty("isRemove")
    public boolean isRemove;

    @DatabaseField(foreign = true, columnName = FIELD_NAME_Materials_ID)
    protected Materials materials;

    public Material() {
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUnitString() {
        return String.format("%s%s", w1, u1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (!Strings.isNullOrEmpty(w2) && !Strings.isNullOrEmpty(u2)) {
            sb.append(w2).append(u2);
        }
        if (!Strings.isNullOrEmpty(w1) && !Strings.isNullOrEmpty(u1)) {
            sb.append("(").append(w1).append(u1).append(")");
        }
        return sb.toString();
    }


    static public List<Material> getMaterialList(long materialsId, boolean isMain) {

        List<Material>  res = DaoHelper.getWhereEqAnd(Material.class,
                new String[]{FIELD_NAME_Materials_ID, FIELD_NAME_isMain},
                new Object[]{materialsId, isMain});

        return res;

//        Dao<Material, Long> daoMaterial = DaoService.getInstance().getDao(Material.class);
//        QueryBuilder<Material, Long> qb = daoMaterial.queryBuilder();
//        try {
//            qb.where().eq(FIELD_NAME_Materials_ID, materialsId).and()
//                    .eq(FIELD_NAME_isMain, isMain);
//            List<Material> list = qb.query();
//            return list;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return Lists.newArrayList();
//        }
    }

}
