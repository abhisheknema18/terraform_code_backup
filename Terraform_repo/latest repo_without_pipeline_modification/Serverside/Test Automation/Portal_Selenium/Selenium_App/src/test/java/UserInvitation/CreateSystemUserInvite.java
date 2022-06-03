package UserInvitation;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import com.capita.fusionPortalPages.UserInvitation_CreateSystemUserInvitationPage;
import BasePackage.Basepage;

public class CreateSystemUserInvite extends Basepage{
	
	UserInvitation_CreateSystemUserInvitationPage UserInvitation_CreateSystemUserInvitation;
	 
	@Test
	public void createNewSystemUserInvitation() throws InterruptedException, UnsupportedFlavorException, IOException {
		String shortDescription = RandomStringUtils.randomAlphabetic(10);
		 String invitationNotes = RandomStringUtils.randomAlphabetic(4);
		 String userCode = RandomStringUtils.randomAlphabetic(4);
		 String userName = RandomStringUtils.randomAlphabetic(5);
		 String windowsLogin = RandomStringUtils.randomAlphabetic(32);
		 String userClass = RandomStringUtils.randomAlphabetic(37);
				
		UserInvitation_CreateSystemUserInvitation = new UserInvitation_CreateSystemUserInvitationPage();
		
		//This function navigates a user to the Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.navigateToCreateSystemUserInvite();
		
		//This function helps user enter Short description on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.enterShortDescription(shortDescription);
		
		//This function helps to Select the expiry and validate the Expiry Date.
		UserInvitation_CreateSystemUserInvitation.selectAndVerifyExpiryDate();
		
		//This function helps to enter invitation notes on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.enterInvitationNotes(invitationNotes);
		
		//This function helps to enter user code on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.enterUserCode(userCode);
		
		//This function Helps to enter user name on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.enterUserName(userName);
		
		//This function helps to select the Portal Access Group on the basis of the access.
		UserInvitation_CreateSystemUserInvitation.selectPortalAccessGroup();
		
		//This function helps to select the SB Access Group on the basis of the access.
		UserInvitation_CreateSystemUserInvitation.selectSbAccessGroup(2);
		
		//This function helps to enter Windows Login on the basis of the access.
		UserInvitation_CreateSystemUserInvitation.enterWindowsLogin(windowsLogin);

		//This function helps to enter User class on the basis of the access.
		UserInvitation_CreateSystemUserInvitation.enterUserClass(userClass);	
		
		//This function clicks Admin checkbox on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.clickAdminCheckbox();
		
		//This function clicks save button on Create Invitation Screen
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function clicks Copy and Close button on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.clickCopyAndCloseButton();
		
		//verifyPopUpMessage
		UserInvitation_CreateSystemUserInvitation.verifySystemUserCreationSuccessMessage();	
	}

	// Verify the field validation for Mandatory fields- Short Description,
	// Invitation Notes, User Code, User Name, Portal Access Group and Windows Login.
	@Test
	public void verifyMandatoryFieldsValidations()
			throws InterruptedException, UnsupportedFlavorException, IOException {
		 UserInvitation_CreateSystemUserInvitation = new UserInvitation_CreateSystemUserInvitationPage();
		 
		 String shortDescription = RandomStringUtils.randomAlphabetic(10);
		 String invitationNotes = RandomStringUtils.randomAlphabetic(4);
		 String userCode = RandomStringUtils.randomAlphabetic(4);
		 String userName = RandomStringUtils.randomAlphabetic(5);
		 String windowsLogin = RandomStringUtils.randomAlphabetic(32);

		//This function navigates a user to the System Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.navigateToCreateSystemUserInvite();
		
		//click save button
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function validates the error message for short Description field on left blank.
		UserInvitation_CreateSystemUserInvitation.validateShortDescriptionErrorMessage();
		
		//This function enters the random generated short Description in the field.
		UserInvitation_CreateSystemUserInvitation.enterShortDescription(shortDescription);
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function validates the error message for Invitation Notes field on left blank.
		UserInvitation_CreateSystemUserInvitation.validateInvitationNotesErrorMessage();
		
		//This function enters the random generated Invitation Notes in the field.
		UserInvitation_CreateSystemUserInvitation.enterInvitationNotes(invitationNotes);
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function validates the error message for User Code field on left blank.
		UserInvitation_CreateSystemUserInvitation.validateUserCodeErrorMessage();
		
		//This function enters the random generated User Code in the field.
		UserInvitation_CreateSystemUserInvitation.enterUserCode(userCode);
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function validates the error message for User Name field on left blank.
		UserInvitation_CreateSystemUserInvitation.validateUserNameErrorMessage();
		
		//This function enters the random generated User Name in the field.
		UserInvitation_CreateSystemUserInvitation.enterUserName(userName);
		
		//This function selects the Portal Access group from the list.
		UserInvitation_CreateSystemUserInvitation.selectPortalAccessGroup();
		
		//This function selects the SB Access group from the list.
		UserInvitation_CreateSystemUserInvitation.selectSbAccessGroup(2);
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function validates the error message for Windows Login field on left blank.
		UserInvitation_CreateSystemUserInvitation.validateWindowsLoginErrorMessage();
		
		//This function enters the random generated Windows Login in the field.
		UserInvitation_CreateSystemUserInvitation.enterWindowsLogin(windowsLogin);
		UserInvitation_CreateSystemUserInvitation.clickAdminCheckbox();
		
		//This function clicks Cancel button on Create Invitation screen.
		UserInvitation_CreateSystemUserInvitation.clickCancelButton();
	}

	// Verify the Field properties - ShortDescription, ExpiryDate, InvitationNotes,
	// User Code, User Name, Windows Login and User Class.
	@Test
	public void verifyFieldProperties() throws InterruptedException, UnsupportedFlavorException, IOException {
		UserInvitation_CreateSystemUserInvitation = new UserInvitation_CreateSystemUserInvitationPage();

		//This function navigates a user to the System Create Invitation screen.
		UserInvitation_CreateSystemUserInvitation.navigateToCreateSystemUserInvite();
		
		//This function verifies the max length of Short Description field.
		UserInvitation_CreateSystemUserInvitation.verifyShortDescriptionMaxLengthProperty();
		
		//This function verifies that the Expiry Date is Read only field.
		UserInvitation_CreateSystemUserInvitation.verifyExpiryDateReadOnlyProperty();
		
		//This function verifies the max length of Invitation Notes field.
		UserInvitation_CreateSystemUserInvitation.verifyInvitationNotesMaxLengthProperty();
		
		//This function verifies the max length of User Code field.
		UserInvitation_CreateSystemUserInvitation.verifyUserCodeMaxLengthProperty();
		
		//This function verifies the max length of User Name field.
		UserInvitation_CreateSystemUserInvitation.verifyUserNameMaxLengthProperty();
		
		//This function selects the SB Access Group from the dropdown list.
		UserInvitation_CreateSystemUserInvitation.selectSbAccessGroup(2);
		
		//This function verifies the max length of Windows Login field.
		UserInvitation_CreateSystemUserInvitation.verifyWindowsLoginMaxLengthProperty();
		
		//This function verifies the max length of User Class field.
		UserInvitation_CreateSystemUserInvitation.verifyUserClassTextMaxLengthProperty();
				
		//This function clicks Cancel button on Create Invitation screen
		UserInvitation_CreateSystemUserInvitation.clickCancelButton();
	}
	
	// Verify the field validation for SB Access Group 
	//If System user selects null the Windows Login is disabled.
	@Test
	public void verifySbAccessGroupNullFieldsValidations()
				throws InterruptedException, UnsupportedFlavorException, IOException {
		 UserInvitation_CreateSystemUserInvitation = new UserInvitation_CreateSystemUserInvitationPage();
		 
		 String shortDescription = RandomStringUtils.randomAlphabetic(10);
		 String invitationNotes = RandomStringUtils.randomAlphabetic(4);
		 String userCode = RandomStringUtils.randomAlphabetic(4);
		 String userName = RandomStringUtils.randomAlphabetic(5);

		//This function navigates a user to the System Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.navigateToCreateSystemUserInvite();
		
		//This function enters the random generated short Description in the field.
		UserInvitation_CreateSystemUserInvitation.enterShortDescription(shortDescription);
		
		//This function helps to Select the expiry and validate the Expiry Date.
		UserInvitation_CreateSystemUserInvitation.selectAndVerifyExpiryDate();
		
		//This function enters the random generated Invitation Notes in the field.
		UserInvitation_CreateSystemUserInvitation.enterInvitationNotes(invitationNotes);
		
		//This function enters the random generated User Code in the field.
		UserInvitation_CreateSystemUserInvitation.enterUserCode(userCode);
		
		//This function enters the random generated User Name in the field.
		UserInvitation_CreateSystemUserInvitation.enterUserName(userName);
		
		//This function selects the Portal Access group from the list.
		UserInvitation_CreateSystemUserInvitation.selectPortalAccessGroup();
		
		//This function selects the Null value of SB Access group from the list.
		UserInvitation_CreateSystemUserInvitation.selectSbAccessGroup(1);
		
		UserInvitation_CreateSystemUserInvitation.clickAdminCheckbox();
		
		//click save button
		UserInvitation_CreateSystemUserInvitation.clickSaveButton();
		
		//This function clicks Copy and Close button on Create Invitation Screen.
		UserInvitation_CreateSystemUserInvitation.clickCopyAndCloseButton();
				
		//verifyPopUpMessage
		UserInvitation_CreateSystemUserInvitation.verifySystemUserCreationSuccessMessage();	
	}
}
