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
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemStandardChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.user.UserList;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

public class ActiveUserDashboardItem extends DashboardItem implements Serializable{

	private static final long serialVersionUID = 6494321557681291280L;


	public ActiveUserDashboardItem(String username, int order, DashboardServiceManager serviceManager) {
		super(username, order, serviceManager);
		
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("title", Properties.get("fdm_dashboard_title_" + getLocalisationTag()));
		criteria.put("chartType", ChartType.COLUMN.toString());
		criteria.put("xAxis", String.valueOf(DashboardItemType.integerValue(DashboardItemType.USER)));
		criteria.put("fullWidth", "0");
		criteria.put("threeD", "0");
		criteria.put("dateRange", "24");
		criteria.put("dateRangeUnit",String.valueOf(DateRangeUnit.integerValue(DateRangeUnit.HOUR)));
		
		criteria.put("xAxisValues", null);

		this.setCriteria(criteria);
	}

	@Override
	public void loadAdditionalCriteria() {

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
		
		if(this.getCriteria().get("xAxisValues") != null && this.getCriteria().get("xAxisValues").length() > 0) {
			workgroupCodes = Arrays.asList(this.getCriteria().get("xAxisValues").split(", "));
		}
		
		if(workgroupCodes == null || workgroupCodes.size() == 0) {
			List<HPCWorkGroups> groups = this.fetchGroups();
			workgroupCodes = new ArrayList<String>();
			for(HPCWorkGroups group: groups) {
				workgroupCodes.add(group.getWorkgroupCode());
			}
		}
	
		Map<String,String[]> returnedUsrWGCounts = this.getServiceManager().getUserService().getActiveUsersForWorkGroups(null, workgroupCodes, DateUtil.formatDateInt(this.getFromDate()), Common.FIELDREACH_TIME_FORMAT.format(this.getFromDate()));
		
		Map<String, DashboardItemChartValue> returnedScripts = new HashMap<String, DashboardItemChartValue>();
		
		if(returnedUsrWGCounts!= null) {
			for(String wgCode: workgroupCodes) {
				
				if(this.getCriteria().get("xAxisValues") != null) {
					if(returnedUsrWGCounts.containsKey(wgCode)){
						DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(wgCode, returnedUsrWGCounts.get(wgCode)[0], returnedUsrWGCounts.get(wgCode)[1]);
						returnedScripts.put(wgCode, chartVal);
					}else {
						DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(wgCode, wgCode, "0");
						returnedScripts.put(wgCode, chartVal);
					}
				}else if(returnedUsrWGCounts.containsKey(wgCode)){
					DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(wgCode, returnedUsrWGCounts.get(wgCode)[0], returnedUsrWGCounts.get(wgCode)[1]);
					returnedScripts.put(wgCode, chartVal);
				}
				
			}
		}
		
		
		//sort alphabetically
		List<String> keys = new ArrayList<>(returnedScripts.keySet());
		Collections.sort(keys);
		
		List<DashboardItemChartValue> arrayChartValues = new ArrayList<DashboardItemChartValue>();
		for(String key : keys) {
			arrayChartValues.add(returnedScripts.get(key));
		}
		
		//not sorted
		//DashboardItemChartValue arrayChartValues[] = new DashboardItemChartValue[returnedScripts.size()];
		//arrayChartValues = returnedScripts.values().toArray(arrayChartValues);
		
		return arrayChartValues.toArray(new DashboardItemChartValue[arrayChartValues.size()]);
		
	}

	@Override
	public String viewResultsList(List<DashboardItemChartValue> xAxisFilter) throws Exception {
		
		UserList userList = this.getServiceManager().getUserList();
		
		userList.reset();
		
		List<String> stringCodes = new ArrayList<String>();
		
		if(xAxisFilter != null && xAxisFilter.size() > 0) {
			for(DashboardItemChartValue xAxisF: xAxisFilter){
				stringCodes.add(xAxisF.getTitle());
			}
		}else if(this.getCriteria().get("xAxisValues") != null && this.getCriteria().get("xAxisValues").length() > 0) {
			stringCodes = Arrays.asList(this.getCriteria().get("xAxisValues").split(", "));
		}else {
			List<HPCWorkGroups> hpcGroups = this.fetchGroups();
			for(HPCWorkGroups group : hpcGroups) {
				stringCodes.add(group.getWorkgroupCode());
			}
		}

		userList.setFromDateRange(Integer.valueOf(this.getCriteria().get("dateRange")));
		userList.setFromDateRangeUnit(Integer.valueOf(this.getCriteria().get("dateRangeUnit")));
		
		userList.setWorkGroupsFromStrings(stringCodes);
		
		userList.search();
		
		return "userlist";
	}

	@Override
	public String getMonitorTypeString() {
		return "USER ACCESS";
	}

	@Override
	public String getLocalisationTag() {
		return "active_users";
	}

	@Override
	public String getImageTag() {
		return "fa fa-users";
	}

}
