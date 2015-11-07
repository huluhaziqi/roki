package com.robam.common.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.legent.pojos.AbsStorePojo;

public class PreSubStep extends AbsStorePojo<Long> {

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	@JsonProperty("no")
	public long index;

	@DatabaseField
	@JsonProperty("imgUrl")
	public String imageUrl;

	// -------------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------------

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	protected PreStep preStep;

	@Override
	public Long getID() {
		return id;
	}

	@Override
	public String getName() {
		return String.valueOf(index);
	}


	public PreStep getParent() {
		return preStep;
	}

}
