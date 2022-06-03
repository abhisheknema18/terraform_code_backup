package com.amtsybex.fieldreach.fdm.dashboard;

import java.io.Serializable;

import com.amtsybex.fieldreach.fdm.dashboard.items.DashboardItem.ChartType;
import com.amtsybex.fieldreach.fdm.dashboard.items.DashboardItem.DashboardItemType;
import com.amtsybex.fieldreach.fdm.dashboard.items.DashboardItem.DateRangeUnit;

public class DashboardItemCriteriaObjects implements Serializable{

	private static final long serialVersionUID = -8134653281641053926L;

	private  String title;
	
	private  String dashType;
	private  ChartType chartType;
	private  boolean fullWidth;
	private  boolean threeD;
	private  int dateRange;
	private  DateRangeUnit dateRangeUnit;
	private  DashboardItemType xAxis;
	private  String[] xAxisValues;
	private  boolean xAxisAllSelected;
	
	private  boolean xFilterAllSelected;
	private  String[] xFilterValues;
	
	public DashboardItemCriteriaObjects() {
		
	}

	public DashboardItemCriteriaObjects(String title, ChartType chartType,
			boolean fullWidth, boolean threeD, int dateRange, DateRangeUnit dateRangeUnit, DashboardItemType xAxis,
			String[] xAxisValues, boolean xAxisAllSelected, String[] xFilterValues, boolean xFilterAllSelected) {
		super();
		this.title = title;
		this.setChartType(chartType);
		this.fullWidth = fullWidth;
		this.threeD = threeD;
		this.dateRange = dateRange;
		this.dateRangeUnit = dateRangeUnit;
		this.xAxis = xAxis;
		this.xAxisValues = xAxisValues;
		this.xAxisAllSelected = xAxisAllSelected;
		this.xFilterValues = xFilterValues;
		this.xFilterAllSelected = xFilterAllSelected;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ChartType getChartType() {
		return chartType;
	}
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
		if(this.chartType == ChartType.COUNT) {
			this.dashType = "count";
		}else {
			this.dashType = "chart";
		}
	}
	public boolean isFullWidth() {
		return this.getChartType() == ChartType.COLUMN &&  fullWidth;
	}
	public void setFullWidth(boolean fullWidth) {
		this.fullWidth = fullWidth;
	}
	public boolean isThreeD() {
		return threeD;
	}
	public void setThreeD(boolean threeD) {
		this.threeD = threeD;
	}
	public int getDateRange() {
		return dateRange;
	}
	public void setDateRange(int dateRange) {
		this.dateRange = dateRange;
	}
	public DateRangeUnit getDateRangeUnit() {
		return dateRangeUnit;
	}
	public void setDateRangeUnit(DateRangeUnit dateRangeUnit) {
		this.dateRangeUnit = dateRangeUnit;
	}
	public DashboardItemType getxAxis() {
		return xAxis;
	}
	public void setxAxis(DashboardItemType xAxis) {
		this.xAxis = xAxis;
	}
	public String[] getxAxisValues() {
		return xAxisValues;
	}
	public void setxAxisValues(String[] xAxisValues) {
		this.xAxisValues = xAxisValues;
	}
	public boolean isxAxisAllSelected() {
		return xAxisAllSelected;
	}
	public void setxAxisAllSelected(boolean xAxisAllSelected) {
		this.xAxisAllSelected = xAxisAllSelected;
	}
	public String getDashType() {
		return dashType;
	}
	public void setDashType(String dashType) {
		this.dashType = dashType;
		if(this.dashType.equals("count")) {
			this.chartType = ChartType.COUNT;
		}
	}

	public boolean isxFilterAllSelected() {
		return xFilterAllSelected;
	}

	public void setxFilterAllSelected(boolean xFilterAllSelected) {
		this.xFilterAllSelected = xFilterAllSelected;
	}

	public String[] getxFilterValues() {
		return xFilterValues;
	}

	public void setxFilterValues(String[] xFilterValues) {
		this.xFilterValues = xFilterValues;
	}
	
	
	
}
