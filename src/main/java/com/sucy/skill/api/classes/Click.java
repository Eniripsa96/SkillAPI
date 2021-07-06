package com.sucy.skill.api.classes;

import com.sucy.skill.api.event.KeyPressEvent.Key;

public class Click {
	private long time;
	private Key key;
	
	public Click (long time, Key key) {
		this.time = time;
		this.key = key;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
}
