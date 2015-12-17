package com.legent.plat.pojos;

import com.legent.pojos.AbsKeyPojo;

public class PlatformUser extends AbsKeyPojo<String> {
	public String userId;
	public String nickname;
	public String figureUrl;
	public String token;

	public PlatformUser(String userId, String nickname, String figureUrl,
						String token) {
		this.userId = userId;
		this.nickname = nickname;
		this.figureUrl = figureUrl;
		this.token = token;
	}

	@Override
	public String getID() {
		return userId;
	}

	@Override
	public String getName() {
		return nickname;
	}
}