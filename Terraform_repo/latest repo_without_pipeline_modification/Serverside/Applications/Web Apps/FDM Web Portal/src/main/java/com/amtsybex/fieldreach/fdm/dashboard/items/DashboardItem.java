package com.amtsybex.fieldreach.fdm.dashboard.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.util.SerializationUtils;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.DashboardModel;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.pk.DashboardModelId;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemChartValue;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardItemCriteriaObjects;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSCATEGORY;

public abstract class DashboardItem implements Serializable {

	private static final long serialVersionUID = -5951893116975018688L;

	public enum ChartType {
		COLUMN, PIE, DONUT, COUNT, LIST;
		
		public static ChartType fromInteger(int x) {
	        switch(x) {
	        case 0:
	            return COLUMN;
	        case 1:
	            return PIE;
	        case 2:
	            return DONUT;
	        case 3:
	            return COUNT;
	        case 4:
	            return LIST;
	        }
	        return COLUMN;
	    }
		
		public static int integerValue(ChartType type) {
	        switch(type) {
	        case COLUMN:
	            return 0;
	        case PIE:
	            return 1;
	        case DONUT:
	            return 2;
	        case COUNT:
	            return 3;
	        case LIST:
	            return 4;
	        }
	        return 0;
	    }
		
		public static ChartType fromString(String chartType) {
	        switch(chartType) {
	        case "COLUMN":
	            return COLUMN;
	        case "PIE":
	            return PIE;
	        case "DONUT":
	            return DONUT;
	        case "COUNT":
	            return COUNT;
	        case "LIST":
	            return LIST;
	        }
	        return COLUMN;
	    }
		
		public String toString() {
	        switch(this) {
	        	case COLUMN:
	        		return "COLUMN";
		        case PIE:
		            return "PIE";
		        case DONUT:
		            return "DONUT";
		        case COUNT:
		            return "COUNT";
		        case LIST:
		            return "LIST";
	        }
	        return "COLUMN";
	    }
		
	};
	
	public enum DashboardItemType {
		SCRIPT, WORK, EQUIPMENT, USER;
		
		public static DashboardItemType fromInteger(int x) {
	        switch(x) {
	        case 0:
	            return SCRIPT;
	        case 1:
	            return WORK;
	        case 2:
	            return EQUIPMENT;
	        case 3:
	            return USER;
	        }
	        return SCRIPT;
	    }
		
		public static int integerValue(DashboardItemType type) {
	        switch(type) {
	        case SCRIPT:
	            return 0;
	        case WORK:
	            return 1;
	        case EQUIPMENT:
	            return 2;
	        case USER:
	            return 3;
	        }
	        return 0;
	    }
		
		public String toString() {
	        switch(this) {
	        case SCRIPT:
	            return "Script";
	        case WORK:
	            return "Work";
	        case EQUIPMENT:
	            return "Equipment";
	        case USER:
	            return "User";
	        }
	        return "Script";
	    }
		
	};
	
	public enum DateRangeUnit {
		DAY, HOUR, MINUTE;
		
		public static DateRangeUnit fromInteger(int x) {
	        switch(x) {
	        case 0:
	            return DAY;
	        case 1:
	            return HOUR;
	        case 2:
	            return MINUTE;
	        }
	        return DAY;
	    }
		public static int integerValue(DateRangeUnit type) {
	        switch(type) {
	        case DAY:
	            return 0;
	        case HOUR:
	            return 1;
	        case MINUTE:
	            return 2;
	        }
	        return 0;
	    }
	};
	
	private transient DashboardServiceManager serviceManager;
	private transient String username;
	private transient int order;
	
	private Map<String, String> criteria;

	//loaded from criteria
	private transient DashboardItemCriteriaObjects criteriaObjs;//loaded from criteria
	
	//converted date range values
	private transient Date fromDate;
	private transient Date toDate;

	//returned results
	private transient DashboardItemChartValue[] results;
	private transient List<String> stackedItems;
	private transient int overallCount;
	
	
	//xAxis filter selection
	transient TreeNode xRoot;  
	transient TreeNode selectedXRoot;  
	transient TreeNode[] selectedXNodes; 
	transient TreeNode removeSelectedXNode;
	
	
	//xAxis second filter selection
	transient TreeNode xFilterRoot;  
	transient TreeNode selectedXFilterRoot;  
	transient TreeNode[] selectedXFilterNodes; 
	transient TreeNode removeSelectedXFilterNode;

	public DashboardItem(String username, int order, DashboardServiceManager serviceManager) {
		super();
		this.username = username;
		this.order = order;
		this.serviceManager = serviceManager;

	}
	
	public abstract String getMonitorTypeString();
	public abstract void loadAdditionalCriteria();
	public abstract void saveAdditionalCriteria();
	public abstract List<SelectItem> getxAxisStackedOptions();
	public abstract boolean isxAxisStacked();
	public abstract DashboardItemChartValue[] loadModelFromDB() throws Exception;
	public abstract String viewResultsList(List<DashboardItemChartValue> xAxisFilter) throws Exception;
	public abstract String getLocalisationTag();
	public abstract String getImageTag();
	
	public String getEditHeaderTitle() {
		return Properties.get("fdm_dashboard_header_" + getLocalisationTag());
	}
	
	public String getTotalValue() {
		int total = 0;
		if(this.getResults() != null) {
			for(DashboardItemChartValue val: this.getResults()) {
				total += val.getIntValue();
			}
		}
		return String.valueOf(total);
	}
	
	public String viewResults() throws Exception {
		
		if(this.xRoot == null) {
			this.loadSelectedX();
		}
		
		if(this.xFilterRoot == null) {
			this.loadSelectedXFilter();
		}
		

		return this.viewResultsList(null);
	}

	
	public String viewResults(DashboardItemChartValue itemChartValue) throws Exception {
		
		this.getServiceManager().getSearch().reset();
		
		List<DashboardItemChartValue> xAxisValueList = new ArrayList<DashboardItemChartValue>();
		xAxisValueList.add(itemChartValue);
		
		return this.viewResultsList(xAxisValueList);
	}
	
	
	public void refresh() throws Exception{
		//if not editing
		this.setResults(this.loadModelFromDB());
		this.overallCount = 0;
		if(this.getResults() != null) {
			for(DashboardItemChartValue value: this.getResults()) {
				this.overallCount += value.getIntValue();
			}
		}
	}
	
	public void loadFromCriteria() {

		this.criteriaObjs = new DashboardItemCriteriaObjects();
		
		this.criteriaObjs.setTitle(criteria.get("title"));
		this.criteriaObjs.setChartType(ChartType.fromString(criteria.get("chartType")));
		
		if(criteria.containsKey("fullWidth")) {
			this.criteriaObjs.setFullWidth(!criteria.get("fullWidth").equals("0"));
		}
		if(criteria.containsKey("threeD")) {
			this.criteriaObjs.setThreeD(!criteria.get("threeD").equals("0"));
		}

		if(criteria.containsKey("dateRange")) {
			this.criteriaObjs.setDateRange(Integer.valueOf(criteria.get("dateRange")));
			this.criteriaObjs.setDateRangeUnit(DateRangeUnit.fromInteger(Integer.valueOf(criteria.get("dateRangeUnit"))));
		}
		
		if(criteria.containsKey("xAxis")) {
			this.criteriaObjs.setxAxis(DashboardItemType.fromInteger(Integer.parseInt(criteria.get("xAxis"))));
		}

		if(criteria.get("xAxisValues") != null && criteria.get("xAxisValues").length() > 0) {
			this.criteriaObjs.setxAxisValues(criteria.get("xAxisValues").split(", "));
			this.criteriaObjs.setxAxisAllSelected(false);
		}else {
			this.criteriaObjs.setxAxisAllSelected(true);
		}
		
		
		if(criteria.get("xFilterValues") != null && criteria.get("xFilterValues").length() > 0) {
			this.criteriaObjs.setxFilterValues(criteria.get("xFilterValues").split(", "));
			this.criteriaObjs.setxFilterAllSelected(false);
		}else {
			this.criteriaObjs.setxFilterAllSelected(true);
		}

		//call to subclass to load any extra variables from criteria
		this.loadAdditionalCriteria();
	}
	
	public boolean saveEdit() throws Exception {
		
		boolean needToRefreshPage = false;
		
		if(this.criteriaObjs.getDateRangeUnit() == DateRangeUnit.HOUR && this.criteriaObjs.getDateRange() == 0) {
			MessageHelper.setErrorMessage("messages", "Time Window must be greater than 0 hours");
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return false;
		}
		
		if(this.criteriaObjs.getChartType() != ChartType.LIST) {
			if(this.criteriaObjs.isFullWidth() != this.getCriteria().get("fullWidth").equals("1") ? true : false) {
				needToRefreshPage = true;
			}else if(this.criteriaObjs.getChartType() == ChartType.COUNT && ChartType.fromString(criteria.get("chartType")) != ChartType.COUNT) {
				needToRefreshPage = true;
			}else if(this.criteriaObjs.getChartType() != ChartType.COUNT && ChartType.fromString(criteria.get("chartType")) == ChartType.COUNT) {
				needToRefreshPage = true;
			}
		}
		
		this.getCriteria().put("title", this.criteriaObjs.getTitle());
		this.getCriteria().put("chartType", this.criteriaObjs.getChartType().toString());
		
		if(this.criteriaObjs.getChartType() != ChartType.LIST) {
			
			this.getCriteria().put("fullWidth", this.criteriaObjs.isFullWidth() ? "1" : "0");
			this.getCriteria().put("threeD", this.criteriaObjs.isThreeD() ? "1" : "0");
			
			this.getCriteria().put("xAxis", String.valueOf(DashboardItemType.integerValue(this.criteriaObjs.getxAxis())));
			
			List<String> selectedXValues = new ArrayList<String>();
			
			if(selectedXNodes != null && selectedXNodes.length > 0) {
				List<TreeNode> selectedXNodeList = Arrays.asList(selectedXNodes);
				
				if(selectedXNodeList != null) {
					for (TreeNode tn : selectedXNodeList){
						// discard the parent nodes
						if (tn.getData() instanceof Script) {
							selectedXValues.add(((Script)tn.getData()).getScriptCode());
						}else if(tn.getData() instanceof HPCWorkGroups){
							selectedXValues.add(((HPCWorkGroups)tn.getData()).getId().getWorkgroupCode());
						}
							
					}
				}
				String[] xsv = new String[selectedXValues.size()];
				xsv = selectedXValues.toArray(xsv);
				this.criteriaObjs.setxAxisValues(xsv);
				
				this.criteriaObjs.setxAxisAllSelected(false);
			}else {
				this.criteriaObjs.setxAxisValues(null);
				this.criteriaObjs.setxAxisAllSelected(true);
			}
			
			String xAxisValues = null;
			if(this.criteriaObjs.getxAxisValues() != null) {
				xAxisValues = Arrays.toString(this.criteriaObjs.getxAxisValues());
				xAxisValues = xAxisValues.substring(1, xAxisValues.length()-1);
			}
			
			
			//THIS IS JAVA 1.8 ONLY BUT BETTER this.getCriteria().put("xAxisValues", this.criteriaObjs.getxAxisValues() == null ? null : String.join(",", this.criteriaObjs.getxAxisValues()) );
			
			this.getCriteria().put("xAxisValues", xAxisValues );
			

			List<String> selectedXFilterValues = new ArrayList<String>();
			
			if(selectedXFilterNodes != null && selectedXFilterNodes.length > 0) {
				List<TreeNode> selectedXFilterNodeList = Arrays.asList(selectedXFilterNodes);
				
				if(selectedXFilterNodeList != null) {
					for (TreeNode tn : selectedXFilterNodeList){
						selectedXFilterValues.add(tn.getData().toString());	
					}
				}
				String[] xsv = new String[selectedXFilterValues.size()];
				xsv = selectedXFilterValues.toArray(xsv);
				this.criteriaObjs.setxFilterValues(xsv);
				
				this.criteriaObjs.setxFilterAllSelected(false);
			}else {
				this.criteriaObjs.setxFilterValues(null);
				this.criteriaObjs.setxFilterAllSelected(true);
			}

			
			String xFilterValues = null;
			if(this.criteriaObjs.getxFilterValues() != null) {
				xFilterValues = Arrays.toString(this.criteriaObjs.getxFilterValues());
				xFilterValues = xFilterValues.substring(1, xFilterValues.length()-1);
			}
			
			
			//THIS IS JAVA 1.8 ONLY BUT BETTER this.getCriteria().put("xAxisValues", this.criteriaObjs.getxAxisValues() == null ? null : String.join(",", this.criteriaObjs.getxAxisValues()) );
			
			this.getCriteria().put("xFilterValues", xFilterValues );
		}
		
		
		if(this.criteriaObjs.getDateRangeUnit() != null) {
			this.getCriteria().put("dateRange", String.valueOf(this.criteriaObjs.getDateRange()));
			this.getCriteria().put("dateRangeUnit", String.valueOf(DateRangeUnit.integerValue(this.criteriaObjs.getDateRangeUnit())));
		}


		
		//call to subclass to add any extra critera objects before saving
		this.saveAdditionalCriteria();
		
		DashboardModel model = this.serviceManager.getUserService().findDashboardUserItemById(null, this.getOrder(), this.getUsername());
		
		if(model == null) {
			//if saving a newly added item then add it to the db
			model = new DashboardModel();
			model.setId(new DashboardModelId(this.getOrder(), this.getUsername()));
			needToRefreshPage = true;
		}
		
		model.setTitle(this.criteriaObjs.getTitle());
		model.setMonitorType(this.getMonitorTypeString());
		model.setDisplayType(this.criteriaObjs.getChartType().toString());
		
		model.setItem(SerializationUtils.serialize(this));
		
		this.serviceManager.getUserService().saveDashboardUserItem(null, model);

		this.loadFromCriteria();
		this.refresh();
		
		return needToRefreshPage;

	}
	
	public void cancelEdit() {
		this.loadFromCriteria();
	}
	
	public void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void setDateRangeValues() {

		if(this.criteriaObjs.getDateRangeUnit() == null) {
			//no date range in this dashboard item
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		toDate = cal.getTime();
		
		if (this.criteriaObjs.getDateRangeUnit() == DateRangeUnit.DAY) {
			cal.add(Calendar.DAY_OF_YEAR, -this.criteriaObjs.getDateRange());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		} else if (this.criteriaObjs.getDateRangeUnit() == DateRangeUnit.HOUR) {
			cal.add(Calendar.HOUR_OF_DAY, -this.criteriaObjs.getDateRange());
		} else if (this.criteriaObjs.getDateRangeUnit() == DateRangeUnit.MINUTE) {
			cal.add(Calendar.MINUTE, -this.criteriaObjs.getDateRange());
		}

		fromDate = cal.getTime();
	}
	
	public List<HPCWorkGroups> fetchGroups() throws UserNotFoundException, FRInstanceException {
		
		return serviceManager.getUserService().getFDMHPCWorkgroupList(null,getUsername(), true);
	}
	
	public List<Script> fetchScripts() throws UserNotFoundException, FRInstanceException {
		return serviceManager.getScriptService().getFDMScriptList(null,getUsername());
	}
	
	public List<String> fetchScriptStatuses() throws UserNotFoundException, FRInstanceException {
		return serviceManager.getScriptResultsService().getFDMResultStatusList(null, getUsername());
	}
	
	public String getUsername(){
		return ManagedBeanHelper.findBean("fdmenv", Environment.class).getUser().getId();
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Map<String, String> getCriteria() {
		return criteria;
	}

	public void setCriteria(Map<String, String> criteria) {
		this.criteria = criteria;
		this.loadFromCriteria();
	}

	public DashboardItemCriteriaObjects getCriteriaObjs() {
		return criteriaObjs;
	}

	public void setCriteriaObjs(DashboardItemCriteriaObjects criteriaObjs) {
		this.criteriaObjs = criteriaObjs;
	}

	public DashboardItemChartValue[] getResults() {
		return results;
	}

	public List<String> getStackedItems() {
		return stackedItems;
	}

	public void setStackedItems(List<String> stackedItems) {
		this.stackedItems = stackedItems;
	}

	public void setResults(DashboardItemChartValue[] results) {
		this.results = results;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public DashboardServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(DashboardServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public int getOverallCount() {
		return overallCount;
	}

	public void setOverallCount(int overallCount) {
		this.overallCount = overallCount;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isxAxisAllSelected() {
		return this.criteriaObjs.isxAxisAllSelected();
	}

	public void setxAxisAllSelected(boolean xAxisAllSelected) {
		this.criteriaObjs.setxAxisAllSelected(xAxisAllSelected);
		
		if(this.criteriaObjs.isxAxisAllSelected()) {

			selectedXNodes = null;
			
			if(this.getxRoot() != null) {
				for(TreeNode tn : this.getxRoot().getChildren()) {
					tn.setSelected(false);
					for(TreeNode xAxisNode : tn.getChildren()) {
						xAxisNode.setSelected(false);
					}
				}
			}
		}
	}
	
	public boolean isxFilterAllSelected() {
		return this.criteriaObjs.isxFilterAllSelected();
	}
	
	public void setxFilterAllSelected(boolean xFilterAllSelected) {
		this.criteriaObjs.setxFilterAllSelected(xFilterAllSelected);
		
		if(this.criteriaObjs.isxFilterAllSelected()) {

			selectedXFilterNodes = null;
			
			if(this.getxFilterRoot() != null) {
				for(TreeNode tn : this.getxFilterRoot().getChildren()) {
					tn.setSelected(false);
					for(TreeNode xFilterNode : tn.getChildren()) {
						xFilterNode.setSelected(false);
					}
				}
			}
		}
	}
	
	public void loadSelected() throws UserNotFoundException, FRInstanceException{
		
		this.loadFromCriteria();

		this.clearSelectedXNodes();
		this.clearSelectedXFilterNodes();

		this.loadSelectedX();
		this.loadSelectedXFilter();
	}

	public void loadSelectedX() throws UserNotFoundException, FRInstanceException {
		
		if(this.criteriaObjs.getxAxis() == DashboardItemType.SCRIPT) {
			this.loadSelectedXScripts();
		}else if(this.criteriaObjs.getxAxis() == DashboardItemType.WORK 
				|| this.criteriaObjs.getxAxis() == DashboardItemType.EQUIPMENT 
				|| this.criteriaObjs.getxAxis() == DashboardItemType.USER) {
			this.loadSelectedXWorkGroups();
		}
	}
	
	public void loadSelectedXWorkGroups() throws UserNotFoundException, FRInstanceException {

		xRoot = new CheckboxTreeNode("Root", null);
		
		String[] xAxisVals = this.criteriaObjs.getxAxisValues();
		
		// make a map for the categories
		Map<Integer, CheckboxTreeNode> cats = new TreeMap<Integer, CheckboxTreeNode>();

		List<HPCWorkGroups> wgs = this.fetchGroups();
		List<TreeNode> arrayNodes = new ArrayList<TreeNode>();
		
		if(wgs != null) {
			// for every script
			for (HPCWorkGroups w : wgs){

				// check if we have created a parent node for the current script's category
				CheckboxTreeNode cat = cats.get(w.getWgCatId());

				// if not, create a new parent node for the current script's category
				if (cat == null){
					cat = new CheckboxTreeNode(w.getHpcWgCat().getWgCatDesc(), xRoot);
					cat.setExpanded(true);
					cats.put(w.getWgCatId(), cat);
				}

				// create a node for the current script, parenting it to the node we just retrieved/created
				TreeNode node = new CheckboxTreeNode(w, cat); 

				
				if(xAxisVals != null && xAxisVals.length > 0) {
					for(String workGroup : xAxisVals){
						if(w.getWorkgroupCode().equals(workGroup)) {
							arrayNodes.add(node);
							node.setSelected(true);
						}
					}
				}
			}
			
			if(xAxisVals != null && xAxisVals.length > 0) {
				TreeNode selectedArray[] = new TreeNode[arrayNodes.size()];
				selectedArray = arrayNodes.toArray(selectedArray);
				this.setSelectedXNodes(selectedArray);
			}
		}
		
	}
	
	public void loadSelectedXScripts() throws UserNotFoundException, FRInstanceException {

		xRoot = new CheckboxTreeNode("Root", null);
		
		String[] xAxisVals = this.criteriaObjs.getxAxisValues();
		
		// make a map for the categories
		Map<Integer, CheckboxTreeNode> cats = new TreeMap<Integer, CheckboxTreeNode>();

		List<Script> userScripts = this.fetchScripts();
		List<TreeNode> arrayNodes = new ArrayList<TreeNode>();
		
		if(userScripts != null) {
			// for every script
			for (Script s : userScripts){

				// check if we have created a parent node for the current script's category
				CheckboxTreeNode cat = cats.get(s.getScriptCategory().getId());

				// if not, create a new parent node for the current script's category
				if (cat == null){
					cat = new CheckboxTreeNode(s.getScriptCategory(), xRoot);
					cat.setExpanded(true);
					cats.put(s.getScriptCategory().getId(), cat);
				}

				// create a node for the current script, parenting it to the node we just retrieved/created
				TreeNode node = new CheckboxTreeNode(s, cat); 

				
				if(xAxisVals != null && xAxisVals.length > 0) {
					for(String script : xAxisVals){
						if(s.getScriptCode().equals(script)) {
							arrayNodes.add(node);
							node.setSelected(true);
						}
					}
				}
			}
			
			if(xAxisVals != null && xAxisVals.length > 0) {
				TreeNode selectedArray[] = new TreeNode[arrayNodes.size()];
				selectedArray = arrayNodes.toArray(selectedArray);
				this.setSelectedXNodes(selectedArray);
			}
		}
		
	}
	
	public void loadSelectedXFilter() throws UserNotFoundException, FRInstanceException {
		
		if(this.criteriaObjs.getxAxis() == DashboardItemType.SCRIPT) {
			loadSelectedXFilterScriptStatuses();
		}else if(this.criteriaObjs.getxAxis() == DashboardItemType.WORK) {
			loadSelectedXFilterWorkStatuses();
		}
		
	}
	
	public void loadSelectedXFilterScriptStatuses() throws UserNotFoundException, FRInstanceException {
		
		List<String> filterableValues = this.fetchScriptStatuses();
		
		xFilterRoot = new CheckboxTreeNode("Root", null);
		
		String[] xAxisVals = this.criteriaObjs.getxFilterValues();
		
		List<TreeNode> arrayNodes = new ArrayList<TreeNode>();
		
		if(filterableValues != null) {
			// for every filterable value
			for (String s : filterableValues){

				// create a node for the current filterable value
				TreeNode node = new CheckboxTreeNode(s, xFilterRoot); 

				if(this.selectXFilterTreeNodeTreeNode(node)) {
					arrayNodes.add(node);
				}
				
			}
			
			if(arrayNodes.size() > 0) {
				TreeNode selectedArray[] = new TreeNode[arrayNodes.size()];
				selectedArray = arrayNodes.toArray(selectedArray);
				this.setSelectedXFilterNodes(selectedArray);
			}
		}
	}
	
	public void loadSelectedXFilterWorkStatuses() throws UserNotFoundException, FRInstanceException {

		List<TreeNode> arrayNodes = new ArrayList<TreeNode>();
		
		xFilterRoot = new CheckboxTreeNode("Root", null);

		this.addStatusGroupToList(arrayNodes, WORKSTATUSCATEGORY.PREDISPATCH, "PRE-DISPATCH CATEGORY");
		this.addStatusGroupToList(arrayNodes, WORKSTATUSCATEGORY.INFIELD, "IN-FIELD CATEGORY");
		//this.addStatusGroupToList(arrayNodes, WORKSTATUSCATEGORY.OPERATIONAL, "OPERATIONAL CATEGORY");
		this.addStatusGroupToList(arrayNodes, WORKSTATUSCATEGORY.PRECLOSE, "PRE-APPROVAL CATEGORY");
		this.addStatusGroupToList(arrayNodes, WORKSTATUSCATEGORY.CANCELLED, "CANCELLED CATEGORY");
		this.addStatusGroupToList(arrayNodes, WORKSTATUSCATEGORY.CLOSED, "CLOSED CATEGORY");

		if(arrayNodes != null && arrayNodes.size() > 0) {
			TreeNode selectedArray[] = new TreeNode[arrayNodes.size()];
			selectedArray = arrayNodes.toArray(selectedArray);
			this.setSelectedXFilterNodes(selectedArray);
		}
		
	}
	
	
	private void addStatusGroupToList(List<TreeNode> arrayNodes, WORKSTATUSCATEGORY category, String categoryStr) throws FRInstanceException {
		
		List<String> woStauses = this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusesByCategory(null, category);
		
		if(woStauses != null) {
			
			CheckboxTreeNode cat = new CheckboxTreeNode(categoryStr, xFilterRoot);
			cat.setExpanded(true);
			
			for(String woStatus: woStauses) {
				
				TreeNode node = new CheckboxTreeNode(woStatus, cat);
				
				if(selectXFilterTreeNodeTreeNode(node)) {
					arrayNodes.add(node);
				}
			}
			
		}
	}
	
	private boolean selectXFilterTreeNodeTreeNode(TreeNode node) {
		
		if(this.getSelectedXFilterNodes() != null && this.getSelectedXFilterNodes().length > 0) {
			for(TreeNode selectedNode : this.getSelectedXFilterNodes()) {
				if(node.getData().equals(selectedNode.getData())) {
					node.setSelected(true);
					return true;
				}
			}
		}else {
			String[] xAxisVals = this.criteriaObjs.getxFilterValues();
			if(xAxisVals != null) {
				for(String status : xAxisVals){
					if(node.getData().equals(status)) {
						node.setSelected(true);
						return true;
					}
				}
			}
		}

		return false;
	}
	
	
	
	public TreeNode getxRoot() {
		return xRoot;
	}

	public void setxRoot(TreeNode xRoot) {
		this.xRoot = xRoot;
	}

	public TreeNode getSelectedXRoot() {
		return selectedXRoot;
	}

	public void setSelectedXRoot(TreeNode selectedXRoot) {
		this.selectedXRoot = selectedXRoot;
	}

	public TreeNode[] getSelectedXNodes() {
		return selectedXNodes;
	}

	public void setSelectedXNodes(TreeNode[] selectedXNodes) {
		
		if(selectedXNodes == null && this.selectedXNodes != null) {
			for(TreeNode selectedNode: this.selectedXNodes) {
				selectedNode.setSelected(false);
			}
		}
		
		this.selectedXNodes = selectedXNodes;
		
		this.selectedXRoot = new CheckboxTreeNode("Root", null);
		if(selectedXNodes != null) {
			
			for(TreeNode selectedNode: selectedXNodes) {
				// discard the parent/category nodes
				if (selectedNode.getData() instanceof Script || selectedNode.getData() instanceof HPCWorkGroups) {
					TreeNode n = new DefaultTreeNode(selectedNode.getData(), selectedXRoot);
				}
			}
		}
	}

	public TreeNode getRemoveSelectedXNode() {
		return removeSelectedXNode;
	}

	public void setRemoveSelectedXNode(TreeNode removeSelectedXNode) {
		this.removeSelectedXNode = removeSelectedXNode;
		
		if(this.removeSelectedXNode != null) {
			
			List<TreeNode> selectedList = new ArrayList<TreeNode>();
			
			for(TreeNode selectedNode : selectedXNodes) {
				// discard the parent nodes
				if (selectedNode.getData() instanceof Script || selectedNode.getData() instanceof HPCWorkGroups) {
					if(selectedNode.getData().toString().equals(removeSelectedXNode.getData().toString())) {
						selectedNode.setSelected(false);
					}else {
						selectedList.add(selectedNode);
					}
				}else {
					if(selectedNode.isSelected()) {
						selectedList.add(selectedNode);
					}
				}
					
			}
			
			TreeNode selectedArray[] = new TreeNode[selectedList.size()];
			selectedArray = selectedList.toArray(selectedArray);
			
			this.setSelectedXNodes(selectedArray);
		}
	}

	public void clearSelectedXNodes(){
		
		this.setSelectedXNodes(null);
		this.setRemoveSelectedXNode(null);
	
	}
	
	public void cancelSelectedXNodes() throws UserNotFoundException, FRInstanceException{
		
		this.setSelectedXNodes(null);
		this.setRemoveSelectedXNode(null);
	
		this.loadSelectedX();
	}
	
	
	public void clearSelectedXFilterNodes(){
		
		this.setSelectedXFilterNodes(null);
	
	}
	
	public void cancelSelectedXFilterNodes() throws UserNotFoundException, FRInstanceException{
		
		this.setSelectedXFilterNodes(null);
	
		this.loadSelectedXFilter();
	}
	
	public TreeNode getxFilterRoot() {
		return xFilterRoot;
	}

	public void setxFilterRoot(TreeNode xFilterRoot) {
		this.xFilterRoot = xFilterRoot;
	}

	public TreeNode getSelectedXFilterRoot() {
		return selectedXFilterRoot;
	}

	public void setSelectedXFilterRoot(TreeNode selectedXFilterRoot) {
		this.selectedXFilterRoot = selectedXFilterRoot;
	}

	public TreeNode[] getSelectedXFilterNodes() {
		return selectedXFilterNodes;
	}

	public void setSelectedXFilterNodes(TreeNode[] selectedXFilterNodes) {
		
		if((selectedXFilterNodes == null || selectedXFilterNodes.length == 0) && this.selectedXFilterNodes != null) {
			for(TreeNode selectedNode: this.selectedXFilterNodes) {
				selectedNode.setSelected(false);
			}
		}
		
		this.selectedXFilterNodes = selectedXFilterNodes;
		
		this.selectedXFilterRoot = new CheckboxTreeNode("Root", null);
		if(selectedXFilterNodes != null) {
			
			for(TreeNode selectedNode: selectedXFilterNodes) {
				// discard the parent/category nodes
				if(selectedNode.getChildCount() == 0) {
					TreeNode n = new DefaultTreeNode(selectedNode.getData(), selectedXFilterRoot);
				}
			}
		}
	}

	public TreeNode getRemoveSelectedXFilterNode() {
		return removeSelectedXFilterNode;
	}

	public void setRemoveSelectedXFilterNode(TreeNode removeSelectedXFilterNode) {
		this.removeSelectedXFilterNode = removeSelectedXFilterNode;
		
		this.removeSelectedXFilterNode = removeSelectedXFilterNode;
		
		if(this.removeSelectedXFilterNode != null) {
			
			List<TreeNode> selectedList = new ArrayList<TreeNode>();
			
			int i=0;
			for(TreeNode selectedNode : selectedXNodes) {
				// discard the parent nodes
				if (selectedNode.getChildCount() == 0) {
					if(selectedNode.getData().toString().equals(removeSelectedXNode.getData().toString())) {
						selectedNode.setSelected(false);
					}else {
						selectedList.add(selectedNode);
					}
				}else {
					if(selectedNode.isSelected()) {
						selectedList.add(selectedNode);
					}
				}
				
			}
			
			TreeNode selectedArray[] = new TreeNode[selectedList.size()];
			selectedArray = selectedList.toArray(selectedArray);
			
			this.setSelectedXNodes(selectedArray);
		}
	}
	
	
	public boolean showSecondFilter() {
		//override to show xFilter.. second filter
		return false;
	}


}
