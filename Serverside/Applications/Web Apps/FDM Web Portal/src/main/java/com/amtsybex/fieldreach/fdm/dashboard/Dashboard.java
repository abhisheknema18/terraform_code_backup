package com.amtsybex.fieldreach.fdm.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.DragDropEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.DashboardModel;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.pk.DashboardModelId;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.dashboard.items.ActiveUserDashboardItem;
import com.amtsybex.fieldreach.fdm.dashboard.items.DashboardItem;
import com.amtsybex.fieldreach.fdm.dashboard.items.DashboardItem.ChartType;
import com.amtsybex.fieldreach.fdm.dashboard.items.ScriptResultsDashboardItem;
import com.amtsybex.fieldreach.fdm.dashboard.items.WorkAtRiskDashboardItem;
import com.amtsybex.fieldreach.fdm.dashboard.items.WorkInProgressDashboardItem;
import com.amtsybex.fieldreach.fdm.dashboard.items.WorkOverDueDashboardItem;

@Named
@WindowScoped
public class Dashboard extends PageCodebase implements Serializable{

	private static final long serialVersionUID = -3548858656175190509L;

	private static Logger _logger = LoggerFactory.getLogger(Dashboard.class.getName());
	
	private List<DashboardItem> dashboardItems;
	private DashboardItem dashboardAddItem;
	
	private List<DashboardItem> dashboardCountItems;
	private List<DashboardItem> dashboardFullItems;
	private List<DashboardItem> dashboardSmallItems;
	@Inject
	transient DashboardServiceManager serviceManager;

	private int refreshRate;
	
	public Dashboard() {
	}
	
	
	public void onload(){

		for(DashboardItem item: this.dashboardItems) {
			
			try {
				item.refresh();
			} catch (Exception e) {
				_logger.error("FRException occured loading dashboard items ", e);
			}
			
		}
	}
	
	@PostConstruct
	public void init() {

		refreshRate = serviceManager.getPortalPropertyUtil().props().getRefresh().getInterval();

		//get dashboard items for this user (key/id, type/classname?)
		try {
			dashboardItems = new ArrayList<DashboardItem>();
			
			List<DashboardModel> models = serviceManager.getUserService().findDashboardUserItemsByUserCode(null, this.getUsername());
			
			if(models != null) {
				//create new dashboard items from db retrieved items
				for(DashboardModel model: models) {
					
					DashboardItem item = (DashboardItem)SerializationUtils.deserialize(model.getItem());
					item.setOrder(model.getId().getItemId());
					item.getCriteria().put("title", model.getTitle());
					item.getCriteria().put("chartType", model.getDisplayType());

					item.setUsername(this.getUsername());
					item.setServiceManager(serviceManager);
					item.loadFromCriteria();
					
					//item.refresh();
					dashboardItems.add(item);
					
				}
				
			}
			
			this.buildLists();
			
		} catch (FRInstanceException e) {
			_logger.error("FRException occured loading dashboard items ", e);
		} catch (Exception e) {
			_logger.error("Exception occured loading dashboard items ", e);
		}
	}
	
	
	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	
	public void onDashboardItemDrop(DragDropEvent ddEvent) throws Exception {
		
		int dragId = Integer.parseInt(ddEvent.getDragId().split("dashItem")[2]);
		int dropId = Integer.parseInt(ddEvent.getDropId().split("dashItem")[2]);
		
		DashboardModel modelItem = null;
		
		DashboardItem fromItem = null;
		DashboardItem toItem = null;
		
		for(DashboardItem item: this.dashboardItems) {

			if(item.getOrder() == dragId) {
				fromItem = item;
			}else if(item.getOrder() == dropId) {
				toItem = item;
			}
			
			if(fromItem != null && toItem != null) {
				break;
			}
			
		}
		
		this.dashboardItems.remove(fromItem);
		
		if(dragId < dropId) {
			//drag forward then add after
			this.dashboardItems.add(this.dashboardItems.indexOf(toItem)+1, fromItem);
		}else {
			//drag back then add before/inplace
			this.dashboardItems.add(this.dashboardItems.indexOf(toItem), fromItem);
		}
		
		List<DashboardItem> tempItems = new ArrayList<DashboardItem>();
		
		for(int i=0; i < this.dashboardItems.size(); i++) {
			
			DashboardItem item = this.dashboardItems.get(i);
			
			item.setOrder(i+1);
			
			modelItem = serviceManager.getUserService().findDashboardUserItemById(null, i+1, this.getUsername());
			if(modelItem != null) {

				serviceManager.getUserService().removeDashboardUserItem(null, modelItem);

			}
			
			modelItem = new DashboardModel(new DashboardModelId(i+1, this.getUsername()),SerializationUtils.serialize(item));
			
			modelItem.setTitle(item.getCriteria().get("title"));
			modelItem.setMonitorType(item.getMonitorTypeString());
			modelItem.setDisplayType(item.getCriteria().get("chartType"));
			
			serviceManager.getUserService().saveDashboardUserItem(null, modelItem);
			
			tempItems.add(item);
		}
		
		this.dashboardItems = tempItems;
		this.buildLists();

    }

	
	@Transient
	public List<HPCWorkGroups> fetchGroups() throws UserNotFoundException, FRInstanceException {

		return serviceManager.getUserService().getFDMHPCWorkgroupList(null,getUsername(), true);

	}
	
	
	public void handleAddItem(String itemType) {

		DashboardItem item;
		try {

			int id=1;
			if(this.getDashboardItems() != null) {
				for(DashboardItem dbItem : this.getDashboardItems()) {
					id = Math.max(id, dbItem.getOrder()) + 1;
				}
			}
			
			if(itemType.equals("ReturnedScript")) {
				item = new ScriptResultsDashboardItem(this.getUsername(), id, serviceManager);
			}else if(itemType.equals("WorkInProgress")) {
				item = new WorkInProgressDashboardItem(this.getUsername(), id, serviceManager);
			}else if(itemType.equals("ActiveUser")) {
				item = new ActiveUserDashboardItem(this.getUsername(), id, serviceManager);
			}else if(itemType.equals("WorkOverDue")) {
				// 32701 - KN - Creating new Over Due Dashboard Object
				item = new WorkOverDueDashboardItem(this.getUsername(), id, serviceManager);
			}else if(itemType.equals("AtRisk")) {
				// 33353 - KN - Creating new At Risk Dashboard Object
				item = new WorkAtRiskDashboardItem(this.getUsername(), id, serviceManager);
			}else {
				item = new ScriptResultsDashboardItem(this.getUsername(), id, serviceManager);
			}
			
			item.loadSelected();

			//item.refresh();
			
			this.dashboardAddItem = item;
			
		} catch (Exception e) {
			this.addMessage(new FacesMessage("Error occured adding item"));
		}
		
		
	}
	
	public String saveEdit(DashboardItem dItem) {
		
		try {
			
			if(!this.dashboardItems.contains(dItem)) {
				this.dashboardItems.add(dItem);
			}

			if(dItem.saveEdit()) {
				PrimeFaces.current().ajax().addCallbackParam("refreshingLists", true);
				this.buildLists();
				return "dashboard";
			}
			
		} catch (Exception e) {
			this.addMessage(new FacesMessage("Error adding new item"));
		}
		
		return null;
		
	}
	
	public void cancelEdit(DashboardItem dItem) {
		
		try {
			if(this.dashboardAddItem != null) {
				this.dashboardAddItem = null;
			}
			dItem.cancelEdit();
		} catch (Exception e) {
			this.addMessage(new FacesMessage("Error canceling save"));
		}
	}
	
	
	public String removeItem(DashboardItem item) {

		try {
			
			DashboardModel model = this.serviceManager.getUserService().findDashboardUserItemById(null, item.getOrder(), this.getUsername());
			
			if(model == null) {
				throw new Exception();
			}
			
			model.setItem(SerializationUtils.serialize(item));
			
			this.serviceManager.getUserService().removeDashboardUserItem(null, model);

			this.init();
			
			return "dashboard";
			
		} catch (Exception e) {
			this.addMessage(new FacesMessage("Error removing item"));
		}
		
		return null;
	}

	private void buildLists() {
		
		this.dashboardCountItems = new ArrayList<DashboardItem>();
		this.dashboardSmallItems = new ArrayList<DashboardItem>();
		this.dashboardFullItems = new ArrayList<DashboardItem>();
		
		if(this.dashboardItems != null) {
			for(DashboardItem item: this.dashboardItems) {
				
				if(ChartType.fromString(item.getCriteria().get("chartType")) == ChartType.COUNT) {
					this.dashboardCountItems.add(item);
				}
				
				if(ChartType.fromString(item.getCriteria().get("chartType")) != ChartType.COUNT && !item.getCriteriaObjs().isFullWidth()) {
					this.dashboardSmallItems.add(item);
				}
				
				
				if(ChartType.fromString(item.getCriteria().get("chartType")) != ChartType.COUNT && item.getCriteriaObjs().isFullWidth()) {
					this.dashboardFullItems.add(item);
				}

			}
		}

	}
	public List<DashboardItem> getDashboardCountItems() {
		
		return this.dashboardCountItems;
	}
	
	public List<DashboardItem> getDashboardSmallItems() {
		
		return this.dashboardSmallItems;
	}
	public List<DashboardItem> getDashboardFullItems() {
		
		return this.dashboardFullItems;
	}
	
	
	public List<DashboardItem> getDashboardItems() {
		return dashboardItems;
	}

	public void setDashboardItems(List<DashboardItem> dashboardItems) {
		this.dashboardItems = dashboardItems;
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	public DashboardItem getDashboardAddItem() {
		return dashboardAddItem;
	}

	public void setDashboardAddItem(DashboardItem dashboardAddItem) {
		this.dashboardAddItem = dashboardAddItem;
	}
	
}

