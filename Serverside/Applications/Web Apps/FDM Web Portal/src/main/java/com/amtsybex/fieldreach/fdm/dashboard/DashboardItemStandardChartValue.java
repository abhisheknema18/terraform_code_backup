package com.amtsybex.fieldreach.fdm.dashboard;

public class DashboardItemStandardChartValue extends DashboardItemChartValue {

	private String value;

	public DashboardItemStandardChartValue() {
		super();
	}
	
	public DashboardItemStandardChartValue(String title, String description, String value) {
		super(title, description);
		this.value = value;
	}

	@Override
	public int getIntValue() {
		return Integer.parseInt(value);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
