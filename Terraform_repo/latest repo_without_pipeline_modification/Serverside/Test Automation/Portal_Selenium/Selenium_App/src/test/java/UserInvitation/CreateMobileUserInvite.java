package UserInvitation;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.capita.fusionPortalPages.UserInvitation_CreateMobileUserInvitationPage;

import BasePackage.Basepage;

public class CreateMobileUserInvite extends Basepage{
	
	UserInvitation_CreateMobileUserInvitationPage UserInvitation_CreateMobileUserInvitation;

	@Test
	public void CreateNewInvitation() throws InterruptedException, UnsupportedFlavorException, IOException {
		UserInvitation_CreateMobileUserInvitation = new UserInvitation_CreateMobileUserInvitationPage();
		
		//This function navigates a user to the Create Invitation Screen
		UserInvitation_CreateMobileUserInvitation.navigateToCreateUserInvite();
		
		//This function Helps user enter Short description on Create Invitation Screen
		UserInvitation_CreateMobileUserInvitation.EnterShortDescription();
		
		//This function Helps to Select the expiry and validate the Expiry Date
		UserInvitation_CreateMobileUserInvitation.SelectAndVerifyExpiryDate();
		
		//This function Helps to enter token notes on Create Invitation Screen
		UserInvitation_CreateMobileUserInvitation.EnterInvitationNotes();
		
		//This function Helps to select the Workgroup on the basis of the access
	    UserInvitation_CreateMobileUserInvitation.ClickSelectButton();
				
		//This function Helps to select the Workgroup on the basis of the access
		UserInvitation_CreateMobileUserInvitation.selectWorkGroup();
		
		//This function clicks save button on Create Invitation Screen
		UserInvitation_CreateMobileUserInvitation.ClickSaveButton();
		
		//This function clicks Copy and Close button on Create Invitation Screen
		UserInvitation_CreateMobileUserInvitation.ClickCopyAndCloseButton();
	}

	@Test
	public void VerifyMandatoryFieldsValidations()
			throws InterruptedException, UnsupportedFlavorException, IOException {
		UserInvitation_CreateMobileUserInvitation = new UserInvitation_CreateMobileUserInvitationPage();

		//This function navigates a user to the Create Invitation Screen
		UserInvitation_CreateMobileUserInvitation.navigateToCreateUserInvite();
		
		//This function verified mandatory field validations on Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.VerifyMandatoryFieldsValidations();
		
		//This function clicks Cancel button on Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.ClickCancelButton();
		
	}

	@Test
	public void VerifyFieldProperties() throws InterruptedException, UnsupportedFlavorException, IOException {
		UserInvitation_CreateMobileUserInvitation = new UserInvitation_CreateMobileUserInvitationPage();

		//This function navigates a user to the Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.navigateToCreateUserInvite();
		
		//This function validates the field properties on Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.VerifyFieldProperties();
		
		//This function clicks Cancel button on Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.ClickCancelButton();

	}
	
	@Test
	public void VerifyVisibleWorkgroups() throws InterruptedException, UnsupportedFlavorException, IOException {
		UserInvitation_CreateMobileUserInvitation = new UserInvitation_CreateMobileUserInvitationPage();

		//This function navigates a user to the Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.navigateToCreateUserInvite();
		
		//This function Helps to select the Workgroup on the basis of the access
		UserInvitation_CreateMobileUserInvitation.ClickSelectButton();
				
		//This function validates the field properties on Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.verifyVisibilityOfWorkGroups();
		
		//This function clicks Cancel button on Create Invitation screen
		UserInvitation_CreateMobileUserInvitation.ClickCancelButton();

	}


}
