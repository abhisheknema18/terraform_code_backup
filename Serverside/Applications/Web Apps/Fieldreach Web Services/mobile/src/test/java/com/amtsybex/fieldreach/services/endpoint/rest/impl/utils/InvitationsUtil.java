package com.amtsybex.fieldreach.services.endpoint.rest.impl.utils;

import com.amtsybex.fieldreach.backend.model.Invitations;

public class InvitationsUtil {
  
  public static Invitations getActiveInvitation() {
    
    return getInvitatioByStatus("ACTIVE");
    
  }
  
  public static Invitations getRevokedInvitation() {
    
    return getInvitatioByStatus("REVOKED");
    
  }
  
  public static Invitations getInvitatioByStatus(String status) {
    Invitations userInvitation = prepareInvitation();
    userInvitation.setInvitationStatus(status);
    return userInvitation;
  }
  
  public static Invitations prepareInvitation() {
    return  new Invitations();
  }

}
