package AccessSharedToken;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.capita.fusionPortalPages.SharedAccessToken_AddAccessTokenPage;

import BasePackage.Basepage;

public class GenerateAccessToken extends Basepage{
	
	SharedAccessToken_AddAccessTokenPage sharedAccessToken_AddAccessTokenPage;

	@Test
	public void AddSharedAccessToken() throws InterruptedException, UnsupportedFlavorException, IOException {
		sharedAccessToken_AddAccessTokenPage = new SharedAccessToken_AddAccessTokenPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AddAccessTokenPage.navigateToAddAccessToken();
		
		//This function Helps user enter application name on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.EnterApplicationName();
		
		//This function Helps to select System user on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.SelectSystemUser();
		
		//This function Helps to enter token notes on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.EnterTokenNotes();
		
		//This function Helps to Select the expiry and validate the Expiry Date
		sharedAccessToken_AddAccessTokenPage.SelectAndVerifyExpiryDate();
		
		//This function clicks save button on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.ClickSaveButton();
		
		//This function clicks Copy and Close button on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.ClickCopyAndCloseButton();
	}

	@Test
	public void VerifyMandatoryFieldsValidations()
			throws InterruptedException, UnsupportedFlavorException, IOException {
		sharedAccessToken_AddAccessTokenPage = new SharedAccessToken_AddAccessTokenPage();

		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AddAccessTokenPage.navigateToAddAccessToken();
		
		//This function verified mandatory field validations on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.VerifyMandatoryFieldsValidations();
		
		//This function clicks Cancel button on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.ClickCancelButton();
		
	}

	@Test
	public void VerifyFieldProperties() throws InterruptedException, UnsupportedFlavorException, IOException {
		sharedAccessToken_AddAccessTokenPage = new SharedAccessToken_AddAccessTokenPage();

		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AddAccessTokenPage.navigateToAddAccessToken();
		
		//This function validates the field properties on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.VerifyFieldProperties();
		
		//This function clicks Cancel button on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.ClickCancelButton();

	}

	@Test
	public void VerifyExpiryDate() throws InterruptedException {
		sharedAccessToken_AddAccessTokenPage = new SharedAccessToken_AddAccessTokenPage();

		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AddAccessTokenPage.navigateToAddAccessToken();
		
		//This function Helps to Select the expiry and validate the Expiry Date
		sharedAccessToken_AddAccessTokenPage.SelectAndVerifyExpiryDate();
		
		//This function clicks Cancel button on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.ClickCancelButton();

	}

	//@Test Removed this test as the Create System User functionality is removed from portal
	public void VerifyNewlyCreatedSysUser() throws InterruptedException {
		sharedAccessToken_AddAccessTokenPage = new SharedAccessToken_AddAccessTokenPage();

		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AddAccessTokenPage.NavigateToCreateSysUser();
		
		//This function verifies the newly created System user is populated on Add Access Token screen
		sharedAccessToken_AddAccessTokenPage.ValidateSysUserInAddAccessToken();
		
		//This function clicks Cancel button on Add Access token screen
		sharedAccessToken_AddAccessTokenPage.ClickCancelButton();
	}
	
	@Test
	public void VerifyAccessForNonAdminUsers() throws InterruptedException {
		
		sharedAccessToken_AddAccessTokenPage = new SharedAccessToken_AddAccessTokenPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AddAccessTokenPage.navigateToAddAccessToken();
		
	}

}
