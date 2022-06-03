package SystemUsers;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ScriptResultsSearchPage;
import com.capita.fusionPortalPages.SystemAccess_SearchAccessGroupPage;
import com.capita.fusionPortalPages.SystemUsers_AddSystemUserPage;
import com.capita.fusionPortalPages.SystemUsers_SearchResultPage;

import BasePackage.Basepage;

public class AddSystemUser extends Basepage{
	
	SystemUsers_SearchResultPage systemUsers_SearchResultPage;
	SystemUsers_AddSystemUserPage systemUsers_AddSystemUserPage;
	
	//Regression Case for PBI #113135
	@Test
	public void AddNewSystemUser() throws InterruptedException {
		
		systemUsers_AddSystemUserPage=new SystemUsers_AddSystemUserPage();
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_AddSystemUserPage.navigateToAddSystemUser();
		
		//This function Clicked search for system users
		systemUsers_AddSystemUserPage.enterUserCode();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_AddSystemUserPage.enterUserName();
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectPortalAccessGroup(4);
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(2);
		
		//This function verifies if the windows login is enabled/disabled
		systemUsers_SearchResultPage.EnterWindowsLogin();
		
		//This function Clicks on cancel Button
		systemUsers_AddSystemUserPage.selectAdminCheckBox();
		
		systemUsers_SearchResultPage.clickSaveButton();
		
		
		//Now Verifying the added System User on System User Search Page
		
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		systemUsers_SearchResultPage.clickSearchReset();
		
		systemUsers_SearchResultPage.enterUserCodeForSearch(systemUsers_AddSystemUserPage.getUserCode());
		
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		systemUsers_SearchResultPage.verifySearchRecord();
		
	}

}
