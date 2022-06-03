package com.amtsybex.fieldreach.fdm.web.jsf;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger.ACTIVITYTYPE;

@Named
@WindowScoped
public class Menu extends PageCodebase implements Serializable{

	private static final long serialVersionUID = -3956524567739695206L;
	
	//FDE056 add activity logging for logout
	@Inject
	transient SystemActivityLogger systemActivityLogger;
	

	/**
	 * called when the user clicks the logout button in the navigation header
	 * @return	- the name of the view to navigate to
	 */
	public String logout(){
		//FDP1398 - MC - shouldnt need this. let spring security take care of the session
		/*HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.invalidate();*/
		systemActivityLogger.recordActivityLog(null, ACTIVITYTYPE.LOGOUT, this.getUsername(), "", "");
		
		return "login";
	}
	
}
