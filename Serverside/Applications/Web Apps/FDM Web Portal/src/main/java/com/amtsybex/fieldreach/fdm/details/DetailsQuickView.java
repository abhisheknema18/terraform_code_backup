package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;

import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

@Named
@WindowScoped
public class DetailsQuickView extends DetailsBase implements Serializable {

	private static final long serialVersionUID = 7801644476092924996L;

	@Override
	public void initialiseAdditionalInformation() {
		// no additional information to load here for now
	}

	@Override
	public void setAdditionalResponseInfo(ResultSet resposne) {
		// no additional information to load here for now
	}

}
