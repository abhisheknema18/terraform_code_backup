package com.amtsybex.fieldreach.fdm.web.jsf.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

public class PatchedDataTableRenderer extends DataTableRenderer {
    @Override
    public void decode(FacesContext context, UIComponent component) {
        ((DataTable) component).setSortByAsMap(null); 
        super.decode(context, component);
    }
}
