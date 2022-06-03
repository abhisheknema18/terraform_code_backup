package com.amtsybex.fieldreach.services.endpoint.rest.impl.utils;

import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;

public class HPCWorkGroupsUtil {

    public static HPCWorkGroups getHPCWorkGroups(String workGroupCode) {
        
        HPCWorkGroups hPCWorkGroups = new HPCWorkGroups(workGroupCode);
        return hPCWorkGroups;
    }
}
