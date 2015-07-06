package net.spookysquad.spookster.mod;

import net.spookysquad.spookster.Spookster;
import net.spookysquad.spookster.event.Listener;

public abstract class Module implements Listener {
	private String name;
	private Type type;
	private String desc;
	private int color;
	
	public Module(String name, String desc, Type type, int color) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.color = color;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public void setName(String moduleName) {
		this.name = moduleName;
	}
}