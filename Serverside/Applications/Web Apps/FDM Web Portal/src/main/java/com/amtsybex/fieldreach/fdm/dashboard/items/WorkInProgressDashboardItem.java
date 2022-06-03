package com.amtsybex.fieldreach.fdm.dashboard.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemStackedChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemStandardChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.fdm.work.WorkList;
import com.amtsybex.fieldreach.utils.impl.Common;

public class WorkInProgressDashboardItem extends DashboardItem implements Serializable  {

	private static final long serialVersionUID = 5814531508263717554L;

	transient boolean xAxisStacked;
	transient String xAxisOption;

	public WorkInProgressDashboardItem(String username,  int order, DashboardServiceManager serviceManager) {
		super(username, order, serviceManager);

		Map<String, String> criteria = new HashMap<String, String>();

		criteria.put("title", Properties.get("fdm_dashboard_title_" + getLocalisationTag()));
		criteria.put("chartType", ChartType.COLUMN.toString());
		criteria.put("xAxis", String.valueOf(DashboardItemType.integerValue(DashboardItemType.WORK)));
		criteria.put("fullWidth", "0");
		criteria.put("threeD", "0");
		criteria.put("dateRange", "28");
		criteria.put("dateRangeUnit",String.valueOf(DateRangeUnit.integerValue(DateRangeUnit.DAY)));

		criteria.put("xAxisValues", null);

		criteria.put("xAxisOption", "workgroup");

		criteria.put("xAxisStackable", "1");
		criteria.put("xAxisStacked", "1");
		criteria.put("xAxisStackedValue", "Work Order Type");

		this.setCriteria(criteria);

	}

	@Override
	public void loadAdditionalCriteria() {
		this.xAxisOption = this.getCriteria().get("xAxisOption");
		this.xAxisStacked = this.getCriteria().get("xAxisStacked").equals("1") ? true : false;
	}
	@Override
	public void saveAdditionalCriteria() {
		this.getCriteria().put("xAxisOption", xAxisOption);
		this.getCriteria().put("xAxisStacked", this.isxAxisStacked() ? "1" : "0");
	}

	@Override
	public List<SelectItem> getxAxisStackedOptions() {
		List<SelectItem> returnItems = new ArrayList<SelectItem>();

		returnItems.add(new SelectItem("workgroup","Work Group"));
		returnItems.add(new SelectItem("status","Status"));

		return returnItems;
	}

	@Override
	public boolean isxAxisStacked() {
		return ChartType.fromString(this.getCriteria().get("chartType")) == ChartType.COLUMN && xAxisStacked;
	}


	public void setxAxisStacked(boolean xAxisStacked) {
		this.xAxisStacked = xAxisStacked;
	}


	public String getxAxisOption() {
		return xAxisOption;
	}


	public void setxAxisOption(String xAxisOption) {
		this.xAxisOption = xAxisOption;
	}


	@Override
	public DashboardItemChartValue[] loadModelFromDB() throws Exception {

		this.setDateRangeValues();

		List<String> statuses = new ArrayList<String>();

		if(this.getCriteria().get("xFilterValues") != null && this.getCriteria().get("xFilterValues").length() > 0) {
			statuses = Arrays.asList(this.getCriteria().get("xFilterValues").split(", "));
		}

		List<String> workgroupCodes = null;

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

		// 32701 - KN - Added new parameter for Over due check Flag
		Map<String,List<String[]>> returnedWoCounts = this.getServiceManager().getWorkService().findWorkIssuedInDateRangeForWorkGroupsCounts(null, DateUtil.formatDateInt(this.getFromDate()), Common.FIELDREACH_TIME_FORMAT.format(this.getFromDate()), DateUtil.formatDateInt(this.getToDate()), workgroupCodes, null, null, statuses, null, false, this.isxAxisStacked(), this.xAxisOption, false);

		if(this.isxAxisStacked()) {
			return this.getStackedChartItems(returnedWoCounts, workgroupCodes);
		}else {
			return this.getChartItems(returnedWoCounts, workgroupCodes);
		}

	}

	private DashboardItemChartValue[] getChartItems(Map<String,List<String[]>> returnedWoCounts, List<String> workgroupCodes) {

		List<DashboardItemChartValue> returnValues = new ArrayList<DashboardItemChartValue>();

		if(returnedWoCounts!= null) {


			List<String> keys = new ArrayList<String>();
			if(this.xAxisOption.equals("workgroup") && workgroupCodes != null) {
				keys = workgroupCodes;
			}else {
				//status
				keys.addAll(returnedWoCounts.keySet());
			}

			List<String[]> nullWoValues = returnedWoCounts.get(null); 
			if(nullWoValues != null && nullWoValues.size() > 0) {

				DashboardItemStandardChartValue chartItem = new DashboardItemStandardChartValue(nullWoValues.get(0)[0], nullWoValues.get(0)[1], nullWoValues.get(0)[2]);
				returnValues.add(chartItem);
			}

			//sort alphabetically
			Collections.sort(keys);

			for(String key: keys) {
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

	private DashboardItemChartValue[] getStackedChartItems(Map<String,List<String[]>> returnedWoCounts, List<String> workgroupCodes){

		Map<String, DashboardItemChartValue> returnValues = new HashMap<String, DashboardItemChartValue>();

		if(returnedWoCounts!= null) {

			List<String> keys = new ArrayList<String>();
			if(this.xAxisOption.equals("workgroup") && workgroupCodes != null) {
				keys = workgroupCodes;
			}else {
				//status
				keys.addAll(returnedWoCounts.keySet());
			}

			List<String> stackItems = new ArrayList<String>();
			//If doing a full search then there might be wo's that have no workgroup so count those too
			List<String[]> nullWoValues = returnedWoCounts.get(null); 
			if(nullWoValues != null && nullWoValues.size() > 0) {

				if(!stackItems.contains("UNDEFINED")) {
					stackItems.add("UNDEFINED");
				}
				Map<String, String> chartVals = new HashMap<String, String>();
				DashboardItemStackedChartValue chartItem = new DashboardItemStackedChartValue(nullWoValues.get(0)[0], nullWoValues.get(0)[1], chartVals);

				for(String[] vals: nullWoValues) {

					chartVals.put("UNDEFINED", vals[3]);
				}

				returnValues.put("UNDEFINED", chartItem);

			}

			//loop the work group codes and create the chart values
			for(String key: keys) {

				if(this.getCriteria().get("xAxisValues") != null) {

					//doing a specific search so show null values
					if(returnedWoCounts.containsKey(key)){
						List<String[]> woValues = returnedWoCounts.get(key); 

						Map<String, String> chartVals = new HashMap<String, String>();

						for(String[] vals: woValues) {

							chartVals.put(vals[2], vals[3]);

							if(!stackItems.contains(vals[2])) {
								stackItems.add(vals[2]);
							}
						}

						DashboardItemStackedChartValue chartItem = new DashboardItemStackedChartValue(woValues.get(0)[0], woValues.get(0)[1], chartVals);

						returnValues.put(chartItem.getTitle(), chartItem);

					}else {
						DashboardItemStackedChartValue chartItem = new DashboardItemStackedChartValue(key, key, null);
						returnValues.put(chartItem.getTitle(), chartItem);
					}
				}else if(returnedWoCounts.containsKey(key)){

					//doing a full search so just include everything that gets returned
					List<String[]> woValues = returnedWoCounts.get(key); 

					Map<String, String> chartVals = new HashMap<String, String>();
					for(String[] vals: woValues) {

						chartVals.put(vals[2], vals[3]);

						if(!stackItems.contains(vals[2])) {
							stackItems.add(vals[2]);
						}
					}

					DashboardItemStackedChartValue chartItem = new DashboardItemStackedChartValue(woValues.get(0)[0], woValues.get(0)[1], chartVals);

					returnValues.put(chartItem.getTitle(), chartItem);
				}

			}

			this.setStackedItems(stackItems);
		}

		//sort alphabetically
		List<String> keys = new ArrayList<>(returnValues.keySet());
		Collections.sort(keys);

		List<DashboardItemChartValue> arrayChartValues = new ArrayList<DashboardItemChartValue>();
		for(String key : keys) {
			arrayChartValues.add(returnValues.get(key));
		}

		//not sorted
		//DashboardItemChartValue arrayChartValues[] = new DashboardItemChartValue[returnValues.size()];
		//arrayChartValues = returnValues.values().toArray(arrayChartValues);

		return arrayChartValues.toArray(new DashboardItemChartValue[arrayChartValues.size()]);

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

		if(this.xAxisOption.equals("status") && xAxisFilter != null && xAxisFilter.size() > 0) {
			for(DashboardItemChartValue xAxisF: xAxisFilter){
				statuses.add(xAxisF.getTitle());
			}
		}else if(this.getCriteria().get("xFilterValues") != null && this.getCriteria().get("xFilterValues").length() > 0) {
			statuses = Arrays.asList(this.getCriteria().get("xFilterValues").split(", "));
		}


		if(this.getFromDate() != null) {
			workList.getSearchCriteria().setFromDate(this.getFromDate());
		}else {
			workList.getSearchCriteria().setFromDate(null);
		}

		workList.setWorkGroupsFromStrings(stringCodes);

		workList.setOverrideWorkStatusOptions(statuses);

		workList.searchAndHideFilterOnDisplay(false);

		return "worklist";

	}

	public boolean showSecondFilter() {
		//override to show xFilter.. second filter
		return true;
	}

	@Override
	public String getMonitorTypeString() {
		return "WORK STATUS";
	}

	@Override
	public String getLocalisationTag() {
		return "work";
	}
	
	@Override
	public String getImageTag() {
		return "fa fa-clipboard-check";
	}
}
