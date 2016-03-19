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
 * 备菜步骤
 * 
 * @author sylar
 * 
 */
public class PreStep extends AbsStorePojo<Long> {

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	@JsonProperty("img")
	public String imageUrl;

	@DatabaseField
	@JsonProperty("desc")
	public String desc;

	// -------------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------------

	@ForeignCollectionField
	private ForeignCollection<PreSubStep> steps;

	@JsonProperty("steps")
	private List<PreSubStep> js_steps;

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

	public List<PreSubStep> getPreSubSteps() {
		return Lists.newArrayList(steps);
	}


	@Override
	public void save2db() {
		super.save2db();

		if (js_steps != null) {
			for (PreSubStep pst : js_steps) {
				pst.preStep = this;
				pst.save2db();
			}
		}

		DaoHelper.update(this);
		DaoHelper.refresh(this);
	}

}