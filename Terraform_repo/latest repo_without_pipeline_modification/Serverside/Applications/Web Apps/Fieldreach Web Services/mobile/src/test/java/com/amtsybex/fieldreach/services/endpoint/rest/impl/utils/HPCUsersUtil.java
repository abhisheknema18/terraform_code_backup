package com.amtsybex.fieldreach.services.endpoint.rest.impl.utils;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCUsersAuth;
import com.amtsybex.fieldreach.backend.model.pk.HPCUserId;

public class HPCUsersUtil {
    
    public static HPCUsers getUserDetails() {
        HPCUsers hpcUsers = new HPCUsers();
        HPCUserId id = new HPCUserId();
        HPCUsersAuth auth = new HPCUsersAuth();
        
        id.setUserCode("USER");
        id.setWorkgroupCode("WorkGroup");
        
        hpcUsers.setId(id);
        hpcUsers.setDeviceid("");
        hpcUsers.setAltRef("");
        hpcUsers.setUserClass("");
        hpcUsers.setUserName("TEST");
        hpcUsers.setRevoked(0);
        hpcUsers.setHpcUsersAuth(auth);
        return hpcUsers;
    }

}
