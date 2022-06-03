package com.amtsybex.fieldreach.fdm.dashboard;

import java.util.Map;

public class DashboardItemStackedChartValue extends DashboardItemChartValue {

	private Map<String, String> value;
	private int intValue;

	public DashboardItemStackedChartValue() {
		super();
	}
	
	public DashboardItemStackedChartValue(String title, String description, Map<String, String> value) {
		super(title, description);
		this.setValue(value);
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
	
	public Map<String, String> getValue() {
		return value;
	}

	public void setValue(Map<String, String> value) {
		this.value = value;
		
		this.intValue = 0;
		if(this.value != null && this.value.size() > 0) {
			for(String val: this.value.values()) {
				intValue += Integer.parseInt(val);
			}
		}

	}
	
	public String getValueForStackItem(String key) {
		
		if(value == null) {
			return "0";
		}
		if(value.containsKey(key)) {
			return value.get(key);
		}
		return "0";
	}

}
