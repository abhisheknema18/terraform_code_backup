/**
 * This JSF converter is created such that it may be applied to
 * ANY JSF component requiring a converter, without regard to the class type
 * to which the component is value-binded.
 * 
 * Dont worry about cache, it just caches the components that use this converter.
 * <br /><br />
 */
package com.amtsybex.fieldreach.fdm.web.jsf.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpSession;

public class UniversalConverter implements Converter {
    
    private static final String objectCacheKey = "APP_OBJECT_CACHE";

    @Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
       if (string == null || string.length() == 0) {
            return null;
        }
       Object returnObject = getObjectCache(fc).get(string);
       return returnObject;
    }

    @Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o == null) {
            return "";
        }
        String returnString = null;
        Map<String, Object> objectCache = getObjectCache(fc);
        //search cacheMap to see if this has already been converted.
        for (Map.Entry<String, Object> cacheEntry : objectCache.entrySet()) {
            Object cachedObject = cacheEntry.getValue();
            if (cachedObject==null) continue;
            if ( o.equals(cachedObject) || o==cachedObject ) {
            	if (o.getClass().equals(cachedObject.getClass())){ // in case BaseEntities are used here and they have overlapping key ranges
                	returnString = cacheEntry.getKey();
            	}
            }
        }
        if (returnString==null) {
            returnString = o.getClass().getSimpleName()+UUID.randomUUID().toString();
            objectCache.put(returnString, o);
        }
        return returnString;
    }

    @SuppressWarnings("unchecked")
	private Map<String, Object> getObjectCache(FacesContext fc) {
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        Object object = session.getAttribute(objectCacheKey);
        if (object!=null && object instanceof Map) {
            return (Map<String, Object>) object;
        } else {
            Map<String, Object> objectCache = new HashMap<String, Object>();
            session.setAttribute(objectCacheKey, objectCache);
            return objectCache;
        }
    }

}
