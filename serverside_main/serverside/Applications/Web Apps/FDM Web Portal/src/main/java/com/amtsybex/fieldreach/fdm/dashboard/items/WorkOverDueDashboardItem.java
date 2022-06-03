package com.amtsybex.fieldreach.fdm.dashboard.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemStandardChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.fdm.work.WorkList;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSCATEGORY;

public class WorkOverDueDashboardItem extends DashboardItem implements Serializable{


	private static final long serialVersionUID = 5616764619613336983L;
	
	transient String xAxisOption;

	public WorkOverDueDashboardItem(String username, int order, DashboardServiceManager serviceManager) {
		super(username, order, serviceManager);

		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("title", Properties.get("fdm_dashboard_title_" + getLocalisationTag()));
		criteria.put("chartType", ChartType.COLUMN.toString());
		criteria.put("xAxis", String.valueOf(DashboardItemType.integerValue(DashboardItemType.WORK)));
		criteria.put("fullWidth", "0");
		criteria.put("threeD", "0");
		criteria.put("dateRange", "0");
		criteria.put("dateRangeUnit",String.valueOf(DateRangeUnit.integerValue(DateRangeUnit.DAY)));
		criteria.put("dateRangeVisible","due");
		
		criteria.put("xAxisValues", null);

		this.setCriteria(criteria);
	}

	@Override
	public String getMonitorTypeString() {
		return "WORK OVERDUE";
	}

	@Override
	public void loadAdditionalCriteria() {
		this.xAxisOption = "workgroup";
	}

	@Override
	public void saveAdditionalCriteria() {

	}

	@Override
	public List<SelectItem> getxAxisStackedOptions() {
		return null;
	}

	@Override
	public boolean isxAxisStacked() {
		return false;
	}

	@Override
	public DashboardItemChartValue[] loadModelFromDB() throws Exception {

		this.setDateRangeValues();

		List<String> workgroupCodes = null;
		List<String> statuses = new ArrayList<String>();

		if(this.getCriteria().get("xAxisValues") != null && this.getCriteria().get("xAxisValues").length() > 0) {
			workgroupCodes = Arrays.asList(this.getCriteria().get("xAxisValues").split(", "));
		}

		if((workgroupCodes == null || workgroupCodes.size() == 0) && !this.getServiceManager().getUserService().hasUnlimitedAccessibleWorkgroups(null, this.getUsername())) {
			List<HPCWorkGroups> groups = this.fetchGroups();
			workgroupCodes = new ArrayList<String>();
			for(HPCWorkGroups group: groups) {
				workgroupCodes.add(group.getWorkgroupCode());
			}
		}
		
		statuses.addAll(this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.PREDISPATCH));
		statuses.addAll(this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.INFIELD));
		
		if (this.getCriteriaObjs().getDateRange() == 0) {
			this.setFromDate(null);
		}


		Map<String,List<String[]>> returnedWoCounts = this.getServiceManager().getWorkService().findWorkIssuedInDateRangeForWorkGroupsCounts(null, DateUtil.formatDateInt(this.getFromDate()), null, DateUtil.formatDateInt(this.getToDate()), workgroupCodes, null, null, statuses, null, false, this.isxAxisStacked(), this.xAxisOption, true);

		
		return this.getChartItems(returnedWoCounts, workgroupCodes);

	
	}
	
	private DashboardItemChartValue[] getChartItems(Map<String,List<String[]>> returnedWoCounts, List<String> workgroupCodes) {

		List<DashboardItemChartValue> returnValues = new ArrayList<DashboardItemChartValue>();

		if(returnedWoCounts!= null) {


			if(workgroupCodes == null) {
				workgroupCodes = new ArrayList<String>();
				workgroupCodes.addAll(returnedWoCounts.keySet());
			}

			List<String[]> nullWoValues = returnedWoCounts.get(null); 
			if(nullWoValues != null && nullWoValues.size() > 0) {

				DashboardItemStandardChartValue chartItem = new DashboardItemStandardChartValue(nullWoValues.get(0)[0], nullWoValues.get(0)[1], nullWoValues.get(0)[2]);
				returnValues.add(chartItem);
			}

			//sort alphabetically
			Collections.sort(workgroupCodes);

			for(String key: workgroupCodes) {
				if(this.getCriteria().get("xAxisValues") != null) {

					//doing a specific search so show null values
					if(returnedWoCounts.containsKey(key)){

						List<String[]> woValues = returnedWoCounts.get(key); 

						DashboardItemStandardChartValue chartItem = new DashboardItemStandardChartValue(woValues.get(0)[0], woValues.get(0)[1], woValues.get(0)[2]);

						returnValues.add(chartItem);

					}else {
						DashboardItemStandardChartValue chartItem = new DashboardItemStandardChartValue(key, key, "0");
						returnValues.add(chartItem);
					}
				}else if(returnedWoCounts.containsKey(key)){

					//doing a full search so just include everything that gets returned
					List<String[]> woValues = returnedWoCounts.get(key); 

					DashboardItemStandardChartValue chartItem = new DashboardItemStandardChartValue(woValues.get(0)[0], woValues.get(0)[1], woValues.get(0)[2]);

					returnValues.add(chartItem);
				}
			}


		}

		DashboardItemChartValue arrayChartValues[] = new DashboardItemChartValue[returnValues.size()];
		arrayChartValues = returnValues.toArray(arrayChartValues);

		return arrayChartValues;
	}

	@Override
	public String viewResultsList(List<DashboardItemChartValue> xAxisFilter) throws Exception {

		WorkList workList = this.getServiceManager().getWorkList();

		workList.reset();

		List<String> stringCodes = new ArrayList<String>();
		List<String> statuses = new ArrayList<String>();

		if(this.xAxisOption.equals("workgroup") && xAxisFilter != null && xAxisFilter.size() > 0) {
			for(DashboardItemChartValue xAxisF: xAxisFilter){
				stringCodes.add(xAxisF.getTitle());
			}
		}else if(this.getCriteria().get("xAxisValues") != null && this.getCriteria().get("xAxisValues").length() > 0) {
			stringCodes = Arrays.asList(this.getCriteria().get("xAxisValues").split(", "));
		}else if(!this.getServiceManager().getUserService().hasUnlimitedAccessibleWorkgroups(null, this.getUsername())) {
			List<HPCWorkGroups> hpcGroups = this.fetchGroups();
			for(HPCWorkGroups group : hpcGroups) {
				stringCodes.add(group.getWorkgroupCode());
			}
		}
		
		if (this.getCriteriaObjs().getDateRange() == 0) {
			this.setFromDate(null);
		}

		statuses.addAll(this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.PREDISPATCH));
		statuses.addAll(this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusesByCategory(null, WORKSTATUSCATEGORY.INFIELD));


		if(this.getFromDate() != null) {
			workList.getSearchCriteria().setFromDate(this.getFromDate());
		}else {
			workList.getSearchCriteria().setFromDate(null);
		}
		if(this.getToDate() != null) {
			workList.getSearchCriteria().setToDate(this.getToDate());
		}else {
			workList.getSearchCriteria().setToDate(null);
		}

		workList.setWorkGroupsFromStrings(stringCodes);

		workList.setOverrideWorkStatusOptions(statuses);

		workList.searchAndHideFilterOnDisplay(true);

		return "worklist";

	}

	@Override
	public String getLocalisationTag() {
		return "work_overdue";
	}

	public String getxAxisOption() {
		return xAxisOption;
	}

	public void setxAxisOption(String xAxisOption) {
		this.xAxisOption = xAxisOption;
	}

	@Override
	public String getImageTag() {
		return "far fa-clock";
	}	
	
	@Override
	public void setDateRangeValues() {

		if (this.getCriteriaObjs().getDateRangeUnit() == null) {
			// no date range in this dashboard item
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		//Required Finish Date < Today  
		cal.add(Calendar.DAY_OF_YEAR, -1);
		this.setToDate(cal.getTime());
		
		//Reset Calendar
		cal = Calendar.getInstance();		
		cal.add(Calendar.DAY_OF_YEAR, -this.getCriteriaObjs().getDateRange());
		this.setFromDate(cal.getTime());

	}
	
}
