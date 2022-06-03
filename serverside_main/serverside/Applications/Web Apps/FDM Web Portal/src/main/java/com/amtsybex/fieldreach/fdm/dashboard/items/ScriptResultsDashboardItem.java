package com.amtsybex.fieldreach.fdm.dashboard.items;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.primefaces.model.TreeNode;

import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemStandardChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

public class ScriptResultsDashboardItem extends DashboardItem implements Serializable {

	private static final long serialVersionUID = 2762254363123279718L;

	transient String xAxisOption;

	public ScriptResultsDashboardItem(String username,  int order, DashboardServiceManager serviceManager) {
		super(username, order, serviceManager);

		Map<String, String> criteria = new HashMap<String, String>();

		criteria.put("title", Properties.get("fdm_dashboard_title_" + getLocalisationTag()));
		criteria.put("chartType", ChartType.COLUMN.toString());
		criteria.put("xAxis", String.valueOf(DashboardItemType.integerValue(DashboardItemType.SCRIPT)));
		criteria.put("fullWidth", "0");
		criteria.put("threeD", "0");
		criteria.put("dateRange", "28");
		criteria.put("dateRangeUnit",String.valueOf(DateRangeUnit.integerValue(DateRangeUnit.DAY)));

		criteria.put("xAxisValues", null);

		criteria.put("xAxisOption", "script");

		this.setCriteria(criteria);
	}


	@Override
	public void loadAdditionalCriteria() {
		this.xAxisOption = this.getCriteria().get("xAxisOption");
	}



	public String getxAxisOption() {
		return xAxisOption;
	}


	public void setxAxisOption(String xAxisOption) {
		this.xAxisOption = xAxisOption;
	}


	@Override
	public void saveAdditionalCriteria() {
		this.getCriteria().put("xAxisOption", xAxisOption);
	}

	@Override
	public List<SelectItem> getxAxisStackedOptions(){
		List<SelectItem> returnItems = new ArrayList<SelectItem>();

		returnItems.add(new SelectItem("script","Script Type"));
		returnItems.add(new SelectItem("status","Result Status"));

		return returnItems;
	}

	@Override
	public boolean isxAxisStacked() {
		return false;
	}

	@Override
	public DashboardItemChartValue[] loadModelFromDB() throws Exception {

		this.setDateRangeValues();

		List<String> scriptCodes = null;

		if(this.getCriteria().get("xAxisValues") != null && this.getCriteria().get("xAxisValues").length() > 0) {
			scriptCodes = Arrays.asList(this.getCriteria().get("xAxisValues").split(", "));
		}

		if(scriptCodes == null || scriptCodes.size() == 0) {
			
			if(!this.getServiceManager().getUserService().hasUnlimitedAccessibleScripts(null, this.getUsername())) {
			
				List<Script> scripts = this.fetchScripts();
				scriptCodes = new ArrayList<String>();
				for(Script script: scripts) {
					scriptCodes.add(script.getScriptCode());
				}
				
			}

		}

		List<String> statusCodes = null;

		if(this.getCriteria().get("xFilterValues") != null && this.getCriteria().get("xFilterValues").length() > 0) {
			statusCodes = Arrays.asList(this.getCriteria().get("xFilterValues").split(", "));
		}

		
		List<String> workGroups = null;
		
		if(!this.getServiceManager().getUserService().hasUnlimitedAccessibleWorkgroups(null, this.getUsername())) {
			
			List<HPCWorkGroups> groups = this.fetchGroups();
			
			workGroups = new ArrayList<String>();
			for(HPCWorkGroups group: groups) {
				workGroups.add(group.getWorkgroupCode());
			}
		}

		

		Map<String,String[]> returnedScriptCounts = this.getServiceManager().getScriptResultsService().getScriptResultsCount(null, getUsername(), 
				null, 
				DateUtil.formatDateInt(this.getFromDate()), 
				Integer.valueOf(new SimpleDateFormat("HHmm").format(this.getFromDate())), 
				DateUtil.formatDateInt(this.getToDate()), 
				scriptCodes, statusCodes, null, workGroups, null, this.xAxisOption);

		Map<String, DashboardItemChartValue> returnedScripts = new HashMap<String, DashboardItemChartValue>();


		if(returnedScriptCounts!= null) {

			if(this.xAxisOption.equals("status")) {
				for(String key: returnedScriptCounts.keySet()) {
					DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(key, returnedScriptCounts.get(key)[0], returnedScriptCounts.get(key)[1]);
					returnedScripts.put(key, chartVal);
				}
			}else {
				
				if(scriptCodes != null && scriptCodes.size() == 0) {
					
					for(String scriptCode: scriptCodes) {

						if(returnedScriptCounts.containsKey(scriptCode)){
							DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(scriptCode, returnedScriptCounts.get(scriptCode)[0], returnedScriptCounts.get(scriptCode)[1]);
							returnedScripts.put(scriptCode, chartVal);
						}else {
							DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(scriptCode, scriptCode, "0");
							returnedScripts.put(scriptCode, chartVal);
						}

					}
				}else {
					
					List<String> scriptCodeKeys = new ArrayList<String>(returnedScriptCounts.keySet());
					Collections.sort(scriptCodeKeys);
					
					for(String key: returnedScriptCounts.keySet()) {
						DashboardItemStandardChartValue chartVal = new DashboardItemStandardChartValue(key, returnedScriptCounts.get(key)[0], returnedScriptCounts.get(key)[1]);
						returnedScripts.put(key, chartVal);
					}

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
	public String viewResultsList(List<DashboardItemChartValue> xAxisFilter) throws Exception{

		this.getServiceManager().getSearch().reset();



		List<String> stringCodes = new ArrayList<String>();
		List<String> statuses = new ArrayList<String>();
		

		if(this.xAxisOption.equals("script") && xAxisFilter != null && xAxisFilter.size() > 0) {
			for(DashboardItemChartValue xAxisF: xAxisFilter){
				stringCodes.add(xAxisF.getTitle());
			}
		}else if(this.getCriteria().get("xAxisValues") != null && this.getCriteria().get("xAxisValues").length() > 0) {
			stringCodes = Arrays.asList(this.getCriteria().get("xAxisValues").split(", "));
		}else{
			List<Script> scripts = this.fetchScripts();
			for(Script script : scripts) {
				stringCodes.add(script.getScriptCode());
			}
		}
		
		if(this.xAxisOption.equals("status") && xAxisFilter != null && xAxisFilter.size() > 0) {
			for(DashboardItemChartValue xAxisF: xAxisFilter){
				statuses.add(xAxisF.getTitle());
			}
		}else if(this.getCriteria().get("xFilterValues") != null && this.getCriteria().get("xFilterValues").length() > 0) {
			statuses = Arrays.asList(this.getCriteria().get("xFilterValues").split(", "));
		}

		this.getServiceManager().getSearch().getSearchCriteria().setFromDate(this.getFromDate());
		this.getServiceManager().getSearch().getSearchCriteria().setToDate(this.getToDate());
		/*List<Date> dates = new ArrayList<Date>();
		dates.add(this.getFromDate());
		dates.add(this.getToDate());
		this.getServiceManager().getSearch().getSearchCriteria().setDateRange(dates);*/

		List<Script> scripts = new ArrayList<Script>();

		if(this.getSelectedXNodes() != null) {

			for(TreeNode tn: this.getSelectedXNodes()) {
				if (tn.getData() instanceof Script && stringCodes.contains(((Script)tn.getData()).getScriptCode()))
					scripts.add((Script) tn.getData());
			}

			this.getServiceManager().getSearch().setSelectedScriptNodes(this.getSelectedXNodes());
		}else {

			this.loadSelectedX();
			List<TreeNode> arrayNodes = new ArrayList<TreeNode>();

			for(String xAxisFItem : stringCodes) {

				for(TreeNode tn : this.getxRoot().getChildren()) {
					for(TreeNode scriptNode : tn.getChildren()) {
						if(((Script)scriptNode.getData()).getScriptCode().equals(xAxisFItem)) {
							scripts.add((Script) scriptNode.getData());
							arrayNodes.add(scriptNode);
						}
					}
				}

			}

			TreeNode selectedArray[] = new TreeNode[arrayNodes.size()];
			selectedArray = arrayNodes.toArray(selectedArray);

			this.getServiceManager().getSearch().setSelectedScriptNodes(selectedArray);
		}

		this.getServiceManager().getSearch().getSearchCriteria().setScripts(scripts);


		this.getServiceManager().getSearch().getSearchCriteria().setResultStatuses(statuses);


		this.getServiceManager().getSearch().searchAndHideFilterOnDisplay();

		return "search";

	}

	public boolean showSecondFilter() {
		//override to show xFilter.. second filter
		return true;
	}

	@Override
	public String getMonitorTypeString() {
		return "SCRIPT RESULT";
	}


	@Override
	public String getLocalisationTag() {
		return "script_results";
	}


	@Override
	public String getImageTag() {
		return "fa fa-file";
	}
}
