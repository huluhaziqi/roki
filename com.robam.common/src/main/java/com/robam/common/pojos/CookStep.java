package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.legent.dao.DaoHelper;
import com.legent.pojos.AbsStorePojo;

import java.util.List;

/**
 * 烧菜步骤
 * 
 * @author sylar
 * 
 */
public class CookStep extends AbsStorePojo<Long> {

	public final static String COLUMN_BOOK_ID = "book_id";

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	@JsonProperty("no")
	public int order;

	@DatabaseField()
	@JsonProperty("desc")
	public String desc;

	@DatabaseField
	@JsonProperty("image")
	public String imageUrl;

	@DatabaseField
	@JsonProperty("fanGear")
	public short fanLevel;

	@DatabaseField
	@JsonProperty("stoveGear")
	public short stoveLevel;

	/**
	 * 所需时间 (秒)
	 */
	@DatabaseField
	@JsonProperty("needTime")
	public int needTime;

	// -------------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------------

	@DatabaseField(foreign = true, columnName = COLUMN_BOOK_ID)
	protected Recipe cookbook;

	@ForeignCollectionField
	private ForeignCollection<CookStepTip> db_tips;

	/**
	 * 子步骤列表
	 */
	@JsonProperty("tips")
	private List<CookStepTip> js_tips;

	// -------------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------------




	@Override
	public Long getID() {
		return id;
	}

	@Override
	public String getName() {
		return desc;
	}

	public List<CookStepTip> getCookStepTips() {
		if (db_tips == null || db_tips.size() == 0)
			return Lists.newArrayList();

		List<CookStepTip> list = Lists.newArrayList(db_tips);
		return list;
	}


	@Override
	public void save2db() {
		super.save2db();

		DaoHelper.deleteWhereEq(CookStepTip.class, CookStepTip.COLUMN_STEP_ID, id);
		if (js_tips != null) {
			for (CookStepTip tip : js_tips) {
				tip.cookStep = this;
				tip.save2db();
			}
		}

		DaoHelper.update(this);
		DaoHelper.refresh(this);
	}
}