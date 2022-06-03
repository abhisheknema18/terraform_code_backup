package UserInvitation;

import java.text.ParseException;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;


import com.capita.fusionPortalPages.UserInvitation_ViewSystemUserInvitationPage;

import BasePackage.Basepage;

public class ViewSystemUserInvitation extends Basepage {
	UserInvitation_ViewSystemUserInvitationPage userInvitation_ViewSystemUserInvitationPage;

	@Test
	public void VerifyCountOfTotalToken() throws InterruptedException {

		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();

		// This function verifies total tokens in the list with the total count displayed
		userInvitation_ViewSystemUserInvitationPage.VerifyInvitationsCount();
	}
	
	@Test
	public void VerifyFilterByInvitationName() throws InterruptedException {

		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();

		// This function verifies total tokens in the list with the total count
		// displayed
		userInvitation_ViewSystemUserInvitationPage.verifyFilterByInvitationName("a");
	}
	
	@Test
	public void VerifyFilterBySystemUser() throws InterruptedException {

		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();

		// This function verifies total tokens in the list with the total count
		// displayed
		userInvitation_ViewSystemUserInvitationPage.verifyFilterBySystemUser("a");
	}
	
	@Test
	public void VerifyColumnSorting() throws InterruptedException, ParseException {
		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();

		// This function validates the sort by Invitation Name
		userInvitation_ViewSystemUserInvitationPage.verifySortByInvitationName();
		
		// This function validates the sort by Work Group Code
		userInvitation_ViewSystemUserInvitationPage.verifySortBySystemUser();
		
		// This function validates the sort by Invitation Name
		userInvitation_ViewSystemUserInvitationPage.VerifySortingByIssueDate();
		
		userInvitation_ViewSystemUserInvitationPage.VerifySortingByExpiryDate();
		
	}
	
	@Test
	public void VerifyLegendIndicatorsPerExpiryDate() throws InterruptedException, ParseException {
		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables the Active toggle
		userInvitation_ViewSystemUserInvitationPage.disableActiveToggle();
		
		userInvitation_ViewSystemUserInvitationPage.VerifyColourCodesAsPerExpiryDate();
	}
	
	@Test
	public void VerifyActiveInvitations() throws InterruptedException, ParseException {
		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables the Active toggle
		userInvitation_ViewSystemUserInvitationPage.enableActiveToggle();
		
		userInvitation_ViewSystemUserInvitationPage.verifyActiveInvitationsToggle();
	}
	
	@Test
	public void VerifyActiveInvitationExpanderFields() throws InterruptedException, ParseException {
		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();
		
		// This function verifies the default state of the Active toggle
		userInvitation_ViewSystemUserInvitationPage.verifyDefaultTActiveToggleState();
		
		// This function clicks Active invitation's notes icon
		userInvitation_ViewSystemUserInvitationPage.clickActiveInvitationNotesIcon();
		
		// This function verifies the expander fields for active invitations
		userInvitation_ViewSystemUserInvitationPage.verifyActiveInvitationExpanderFields();
		
	}
	
	@Test
	public void VerifyRevokeInvitation() throws InterruptedException, ParseException {
		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables Active toggle
		userInvitation_ViewSystemUserInvitationPage.disableActiveToggle();
		
		// This function clicks on Revoke Icon
		userInvitation_ViewSystemUserInvitationPage.clickRevokeInvitationIcon();
		
		String revokeNotes=RandomStringUtils.randomAlphabetic(5).toLowerCase();;
		//This function inputs revoke notes text
		userInvitation_ViewSystemUserInvitationPage.enterRevokeNotes(revokeNotes);
		
		//This function clicks on Revoke Button on Revoke slide in
		userInvitation_ViewSystemUserInvitationPage.clickRevokeButton();
		
		//This function verifies the revoked invitation
		userInvitation_ViewSystemUserInvitationPage.verifyInvitationRevoked();
		
		//This function verifies the revoked invitation expander fields
		userInvitation_ViewSystemUserInvitationPage.verifyRevokedInvitationExpanderFields(revokeNotes);
		
	}
	
	@Test
	public void VerifyRevokeMandatoryValidation() throws InterruptedException, ParseException {
		userInvitation_ViewSystemUserInvitationPage = new UserInvitation_ViewSystemUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewSystemUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables Active toggle
		userInvitation_ViewSystemUserInvitationPage.disableActiveToggle();
		
		// This function clicks on Revoke Icon
		userInvitation_ViewSystemUserInvitationPage.clickRevokeInvitationIcon();
		
		// This function clicks on Revoke Button
		userInvitation_ViewSystemUserInvitationPage.clickRevokeButton();
		
		// This function verifies the mandatory validation for Revoke Notes
		userInvitation_ViewSystemUserInvitationPage.VerifyRevokeValidation();
		
		// This function clicks on Cancel Button
		userInvitation_ViewSystemUserInvitationPage.clickCancelButton();
		
	}
	
}
