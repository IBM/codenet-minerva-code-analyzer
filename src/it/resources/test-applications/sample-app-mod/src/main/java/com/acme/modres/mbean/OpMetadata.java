package com.acme.modres.mbean;

public class OpMetadata {
	
	public OpMetadata() {		
	}
	
	public OpMetadata(String name, String description, String type, int impact) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.impact = impact;
	}
	private String name;
	private String description;
	//TODO signature, assume empty for now
	private String type;
	private int impact;

	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getType() {
		return type;
	}
	public int getImpact() {
		return impact;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setImpact(int impact) {
		this.impact = impact;
	}
}
