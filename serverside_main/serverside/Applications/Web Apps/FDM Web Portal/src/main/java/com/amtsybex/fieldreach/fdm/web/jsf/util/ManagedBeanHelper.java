package com.amtsybex.fieldreach.fdm.web.jsf.util;

import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public abstract class ManagedBeanHelper {

	@SuppressWarnings("unchecked")
	public static <T> T findBean(String beanName, Class<T> beanClass) {
		//FDP13979 get beans from faces rather than spring
		return (T) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{" + beanName + "}", Object.class);
	}

	public static Map<String, Object> getSessionMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	}

	public static Map<String, Object> getRequestMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
	}
	
	public static Map<String, String> getRequestParameterMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	}
	
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
	public static String getContextRoot() {
		return ((ServletContext) getFacesContext().getExternalContext().getContext()).getContextPath();
	}

	public static String getClientId(final String componentId) {
		UIViewRoot root = getFacesContext().getViewRoot();
		UIComponent c = findComponent(root, componentId);
		return c.getClientId(getFacesContext());
	}

	private static UIComponent findComponent(final UIComponent c, final String id) {
		if (id.equals(c.getId())) {
			return c;
		}
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), id);
			if (found != null) {
				return found;
			}
		}
		return null;
	}
	
	public static String findContextParamValue(final String paramName) {
		return ((ServletContext) getFacesContext().getExternalContext().getContext()).getInitParameter(paramName);
	}
}