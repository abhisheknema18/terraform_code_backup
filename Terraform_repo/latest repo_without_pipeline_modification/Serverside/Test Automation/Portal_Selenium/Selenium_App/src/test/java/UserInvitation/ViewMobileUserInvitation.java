package UserInvitation;

import java.text.ParseException;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.UserInvitation_ViewMobileUserInvitationPage;

import BasePackage.Basepage;

public class ViewMobileUserInvitation extends Basepage {
	UserInvitation_ViewMobileUserInvitationPage userInvitation_ViewMobileUserInvitationPage;

	@Test
	public void VerifyCountOfTotalToken() throws InterruptedException {

		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();

		// This function verifies total tokens in the list with the total count displayed
		userInvitation_ViewMobileUserInvitationPage.VerifyInvitationsCount();
	}
	
	@Test
	public void VerifyFilterByInvitationName() throws InterruptedException {

		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();

		// This function verifies total tokens in the list with the total count
		// displayed
		userInvitation_ViewMobileUserInvitationPage.verifyFilterByInvitationName();
	}
	
	@Test
	public void VerifyFilterByWorkGroupCode() throws InterruptedException {

		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();

		// This function verifies total tokens in the list with the total count
		// displayed
		userInvitation_ViewMobileUserInvitationPage.verifyFilterByWorkGroupCode();
	}
	
	@Test
	public void VerifyColumnSorting() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();

		// This function validates the sort by Invitation Name
		userInvitation_ViewMobileUserInvitationPage.verifySortByInvitationName();
		
		// This function validates the sort by Work Group Code
		userInvitation_ViewMobileUserInvitationPage.verifySortByWorkGroupCode();
		
		// This function validates the sort by Invitation Name
		userInvitation_ViewMobileUserInvitationPage.VerifySortingByIssueDate();
		
		userInvitation_ViewMobileUserInvitationPage.VerifySortingByExpiryDate();
		
	}
	
	@Test
	public void VerifyLegendIndicatorsPerExpiryDate() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables the Active toggle
		userInvitation_ViewMobileUserInvitationPage.disableActiveToggle();
		
		userInvitation_ViewMobileUserInvitationPage.VerifyColourCodesAsPerExpiryDate();
	}
	
	@Test
	public void VerifyActiveInvitations() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables the Active toggle
		userInvitation_ViewMobileUserInvitationPage.enableActiveToggle();
		
		userInvitation_ViewMobileUserInvitationPage.verifyActiveInvitationsToggle();
	}
	
	@Test
	public void VerifyActiveInvitationExpanderFields() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();
		
		// This function verifies the default state of the Active toggle
		userInvitation_ViewMobileUserInvitationPage.verifyDefaultTActiveToggleState();
		
		// This function clicks Active invitation's notes icon
		userInvitation_ViewMobileUserInvitationPage.clickActiveInvitationNotesIcon();
		
		// This function verifies the expander fields for active invitations
		userInvitation_ViewMobileUserInvitationPage.verifyActiveInvitationExpanderFields();
		
	}
	
	@Test
	public void VerifyRevokeInvitation() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables Active toggle
		userInvitation_ViewMobileUserInvitationPage.disableActiveToggle();
		
		// This function clicks on Revoke Icon
		userInvitation_ViewMobileUserInvitationPage.clickRevokeInvitationIcon();
		
		//This function inputs revoke notes text
		userInvitation_ViewMobileUserInvitationPage.enterRevokeNotes();
		
		//This function clicks on Revoke Button on Revoke slide in
		userInvitation_ViewMobileUserInvitationPage.clickRevokeButton();
		
		//This function verifies the revoked invitation
		userInvitation_ViewMobileUserInvitationPage.verifyInvitationRevoked();
		
		//This function verifies the revoked invitation expander fields
		userInvitation_ViewMobileUserInvitationPage.verifyRevokedInvitationExpanderFields();
		
	}
	
	@Test
	public void VerifyRevokeMandatoryValidation() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables Active toggle
		userInvitation_ViewMobileUserInvitationPage.disableActiveToggle();
		
		// This function clicks on Revoke Icon
		userInvitation_ViewMobileUserInvitationPage.clickRevokeInvitationIcon();
		
		// This function clicks on Revoke Button
		userInvitation_ViewMobileUserInvitationPage.clickRevokeButton();
		
		// This function verifies the mandatory validation for Revoke Notes
		userInvitation_ViewMobileUserInvitationPage.VerifyRevokeValidation();
		
		// This function clicks on Cancel Button
		userInvitation_ViewMobileUserInvitationPage.clickCancelButton();
		
	}
	
	@Test
	public void VerifyVisibleInvitationsForWGAdminUSer() throws InterruptedException, ParseException {
		userInvitation_ViewMobileUserInvitationPage = new UserInvitation_ViewMobileUserInvitationPage();

		// This function navigates a user to the User Invitations SCreen
		userInvitation_ViewMobileUserInvitationPage.navigateToViewUserInvitations();
		
		// This function disables Active toggle
		userInvitation_ViewMobileUserInvitationPage.disableActiveToggle();
		
		//This method verifies if a WG Admin can see the invitations with only the work groups assigned to it
		userInvitation_ViewMobileUserInvitationPage.verifyInvitationsVisibleForWorkGroupAdminUser();
		
	}
	
}
