package com.amtsybex.fieldreach.fdm.user;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class UserMonitor extends PageCodebase implements Serializable {

	private List<HPCUsers> userActivityStatus;
	private HPCUsers selectedUser;
	private MapModel mapModel;
	private Marker selectedMarker;

	private int fromDateRange;
	private int fromDateRangeUnit;

	private static final long serialVersionUID = 6052914809528935109L;

	private static Logger _logger = LoggerFactory.getLogger(UserMonitor.class.getName());

	public String searchUserStatus(){

		List<String> workgroupCodes = null;
		this.selectedMarker = null;
		this.selectedUser = null;
		try {
			List<HPCWorkGroups> groups = this.getUserService().getFDMHPCWorkgroupList(null, this.getUsername(), true);

			workgroupCodes = new ArrayList<String>();

			for (HPCWorkGroups group : groups) {

				workgroupCodes.add(group.getWorkgroupCode());
			}

			userActivityStatus= this.getUserService().findHPCUsers(null, workgroupCodes);

			if(userActivityStatus!=null) {
				this.addMarkers();
			}

		} catch (FRInstanceException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("recallWorks Unknown error occurred" + e.getMessage());

		} catch (UserNotFoundException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("recallWorks Unknown error occurred" + e.getMessage());

		} catch (ParseException e) {

			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("recallWorks Unknown error occurred" + e.getMessage());
		}

		return "userMonitor";

	}

	public boolean showMap() throws ParseException {
		return mapModel == null ? false : mapModel.getMarkers().size() > 0;
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		selectedMarker = (Marker) event.getOverlay();
		selectedUser = (HPCUsers) selectedMarker.getData(); 
	}
	
	public String getSelectedMarkerInfoWindowData() {
		if(this.selectedMarker != null) {
			HPCUsers usr = ((HPCUsers)selectedMarker.getData());
			return " User Name: "+ usr.getUserName()
			+"\n\n Device Id: "+usr.getLastActivity().getId().getDeviceId() 
			+"\n\n Location Time: "+DateUtil.dateFormatter(Common.convertFieldreachDate(usr.getLastActivity().getActivityDate()))
			+" "+DateUtil.parseTime(usr.getLastActivity().getActivityTime());
		}
		
		return null;
	}

	public void addMarkers() throws ParseException {
		mapModel = new DefaultMapModel();

		for (HPCUsers usr : this.getUserActivityStatus()) {
			if(usr.getLastActivity()!=null && usr.getLastActivity().getUserLocationHistory()!=null) {
				LatLng coOrds = new LatLng(Double.valueOf(usr.getLastActivity().getUserLocationHistory().getLatitude()),
						Double.valueOf(usr.getLastActivity().getUserLocationHistory().getLongitude()));
				Marker m = null;
				if(usr.getMapIconColor().contains("green")) {
					m = new Marker(coOrds,"User Name : "+usr.getUserName(),"Device Id "+usr.getLastActivity().getId().getDeviceId(),"https://maps.google.com/mapfiles/ms/micons/green-dot.png");
				}
				else if(usr.getMapIconColor().contains("orange")){
					m = new Marker(coOrds,"User Name : "+usr.getUserName(),"Device Id "+usr.getLastActivity().getId().getDeviceId(),"https://maps.google.com/mapfiles/ms/micons/orange-dot.png");
				}
				else {
					continue;
				}
				
				m.setData(usr);
				
				mapModel.addOverlay(m);
			}
		}
	}

	public List<HPCUsers> getUserActivityStatus() {
		return userActivityStatus;
	}

	public void setUserActivityStatus(List<HPCUsers> userActivityStatus) {
		this.userActivityStatus = userActivityStatus;
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public HPCUsers getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(HPCUsers selectedUser) {
		this.selectedUser = selectedUser;
	    if(this.selectedUser != null) {
			for(Marker marker : this.mapModel.getMarkers()) {
				if(this.selectedUser.equals(marker.getData())) {
					this.selectedMarker = marker;
					PrimeFaces.current().ajax().addCallbackParam("lat", selectedMarker.getLatlng().getLat()); 
					PrimeFaces.current().ajax().addCallbackParam("lng", selectedMarker.getLatlng().getLng());
					break;
				}
			}
	    }
		  
	}

	public int getFromDateRange() {
		return fromDateRange;
	}

	public void setFromDateRange(int fromDateRange) {
		this.fromDateRange = fromDateRange;
	}

	public int getFromDateRangeUnit() {
		return fromDateRangeUnit;
	}

	public void setFromDateRangeUnit(int fromDateRangeUnit) {
		this.fromDateRangeUnit = fromDateRangeUnit;
	}
	
	public String getCenterLocation() {
		if(this.selectedMarker != null) {
			return this.selectedMarker.getLatlng().getLat() + " " + this.selectedMarker.getLatlng().getLng();
		}
		return "";
	}

	public String getTitle() {
		return "User Status Monitor";
	}
}
