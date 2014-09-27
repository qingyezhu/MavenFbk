package com.gwssi.model;

public class TreeNode {

	private String id;
	private String name;
	private String state;
	private Boolean isParent;
	private String kind;
	private Boolean nocheck;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		if (null != state) {
			this.setIsParent("closed".equals(state) ? true : false);
		}
		this.state = state;
	}

	public Boolean getIsParent() {
		return this.isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public String getKind() {
		return this.kind;
	}

	public void setKind(String kind) {
		if (null != kind) {
			this.setNocheck("FBZD".equals(kind) ? true : false);
		}
		this.kind = kind;
	}

	public Boolean getNocheck() {
		return this.nocheck;
	}

	public void setNocheck(Boolean nocheck) {
		this.nocheck = nocheck;
	}

	@Override
	public String toString() {
		return "{id=" + this.id + ", name=" + this.name + ", state="
				+ this.state + ", isParent=" + this.isParent + ", kind="
				+ this.kind + ", nocheck=" + this.nocheck + "}";
	}
}
