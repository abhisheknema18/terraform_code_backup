package com.amtsybex.fieldreach.fdm.dashboard;

public abstract class DashboardItemChartValue {

	private String title;
	private String description;
	
	public DashboardItemChartValue() {
		
	}

	public DashboardItemChartValue(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}
	
	public abstract int getIntValue();

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
