package SystemUsers;

import java.util.Random;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ScriptResultsSearchPage;
import com.capita.fusionPortalPages.SystemAccess_SearchAccessGroupPage;
import com.capita.fusionPortalPages.SystemUsers_AddSystemUserPage;
import com.capita.fusionPortalPages.SystemUsers_SearchResultPage;

import BasePackage.Basepage;

public class EditSystemUser extends Basepage {
	
	SystemUsers_SearchResultPage systemUsers_SearchResultPage;
	Results_ScriptResultsSearchPage results_ScriptResultsSearchPage;
	SystemAccess_SearchAccessGroupPage systemAccess_SearchAccessGroupPage;
	SystemUsers_AddSystemUserPage systemUsers_AddSystemUserPage;
	
	@Test
	public void verifyWindowsLoginEnabled() throws InterruptedException {
		
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_SearchResultPage.ClickEditUserIcon();
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(2);
		
		//This function verifies if the windows login is enabled/disabled
		systemUsers_SearchResultPage.verifyWindowsLoginEnabledorDisabled();
		
		//This function Clicks on cancel Button
		systemUsers_SearchResultPage.clickCancelButton();
		
	}
	
	@Test
	public void verifyWindowsLoginDisabled() throws InterruptedException {
		
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_SearchResultPage.ClickEditUserIcon();
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(1);
		
		//This function verifies if the windows login is enabled/disabled
		systemUsers_SearchResultPage.verifyWindowsLoginEnabledorDisabled();
		
		//This function Clicks on cancel Button
		systemUsers_SearchResultPage.clickCancelButton();
		
	}
	
	@Test
	public void verifyWindowsLoginFieldLength() throws InterruptedException {
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_SearchResultPage.ClickEditUserIcon();
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(2);
		
		//This function Removes the existing value from Windows login field
		systemUsers_SearchResultPage.clearWindowsLogin();
		
		//This function verifies the max length of the field Windows login
		systemUsers_SearchResultPage.verifyWindowsLoginFieldLength();
		
		//This function Clicks on cancel Button
		systemUsers_SearchResultPage.clickCancelButton();
		
	}

	
	@Test
	public void verifyUpdatedWindowsLogin() throws InterruptedException {
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_SearchResultPage.ClickEditUserIcon();
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(2);
		
		//This function Removes the existing value from Windows login field
		systemUsers_SearchResultPage.clearWindowsLogin();
		
		//This function enters value from Windows login field
		systemUsers_SearchResultPage.EnterWindowsLogin();
		
		//This function clicks on Save Button
		systemUsers_SearchResultPage.clickSaveButton();
		
		//This function clicks on Save Button
		systemUsers_SearchResultPage.verifyUpdatedWindowsLogin();
		
	}
	
	@Test
	public void verifyWindowsLoginMandatoryValidation() throws InterruptedException {
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_SearchResultPage.ClickEditUserIcon();
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(2);
		
		//This function Removes the existing value from Windows login field
		systemUsers_SearchResultPage.clearWindowsLogin();
				
		//This function clicks on Save Button
		systemUsers_SearchResultPage.clickSaveButton();
		
		//This function clicks on Save Button
		systemUsers_SearchResultPage.verifyWindowsLoginMandatoryValidation();
		
		//This function Clicks on cancel Button
		systemUsers_SearchResultPage.clickCancelButton();
		
	}
	
	@Test
	public void verifyFilterByWindowsUser() throws InterruptedException {
		
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function verifies the filter for windows userusers
		systemUsers_SearchResultPage.verifyFilterByWindowsUser();
	}
	
	@Test
	public void verifySortByWindowsUser() throws InterruptedException {
		
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function verifies the filter for windows userusers
		systemUsers_SearchResultPage.VerifySortingByWindowsUser();
	}
	
	
	@Test
	public void verifyEditOwnResultsOnly() throws InterruptedException {
		results_ScriptResultsSearchPage=new Results_ScriptResultsSearchPage();
		systemAccess_SearchAccessGroupPage=new SystemAccess_SearchAccessGroupPage();
		
		//This function navigates a user to the View All Access Groups screen
		systemAccess_SearchAccessGroupPage.navigateToVIewAllAccessGroups();
		
		//This function clicks on Edit Access Group icon
		systemAccess_SearchAccessGroupPage.ClickEditAccessGroupIcon();
		
		//This function Clicks on Edit Results tab
		systemAccess_SearchAccessGroupPage.ClickEditResultsTab();
		
		//This function Selects radio button Edit Own Results Only
		systemAccess_SearchAccessGroupPage.SelectRadioButtonEditOwnResultsOnly();
		
		//This function clciks save button
		systemAccess_SearchAccessGroupPage.ClickSave();
		
		//This function navigates user to Results Search screen
		results_ScriptResultsSearchPage.navigateToResultsSearchPage();
		
		//This function performs Results Search 
		results_ScriptResultsSearchPage.SearchResult();
		
		//This function opens results detailed screen
		results_ScriptResultsSearchPage.clickDetailedScreenForOwnResults();
		
		//This function checks for the availability of the Edit response icon
		results_ScriptResultsSearchPage.verifyEditResponseIconAvailablity();
		
		//This function navigates user back to the results list
		results_ScriptResultsSearchPage.clickReturnToListIcon();
		
		//This function opens results detailed screen
		results_ScriptResultsSearchPage.clickDetailedScreenForOtherResults();
		
		//This function checks for the availability of the Edit response icon
		results_ScriptResultsSearchPage.verifyEditResponseIconAvailablity();
	}
	
	@Test
	public void verifyEditAccessibleResultsOnly() throws InterruptedException {
		results_ScriptResultsSearchPage=new Results_ScriptResultsSearchPage();
		systemAccess_SearchAccessGroupPage=new SystemAccess_SearchAccessGroupPage();
		
		//This function navigates a user to the View All Access Groups screen
		systemAccess_SearchAccessGroupPage.navigateToVIewAllAccessGroups();
		
		//This function clicks on Edit Access Group icon
		systemAccess_SearchAccessGroupPage.ClickEditAccessGroupIcon();
		
		//This function Clicks on Edit Results tab
		systemAccess_SearchAccessGroupPage.ClickEditResultsTab();
		
		//This function Selects radio button Edit Accessible Results Only
		systemAccess_SearchAccessGroupPage.SelectRadioButtonEditAccessibleResultsOnly();
		
		//This function clicks Save button
		systemAccess_SearchAccessGroupPage.ClickSave();
		
		
		//This function navigates user to Results Search screen
		results_ScriptResultsSearchPage.navigateToResultsSearchPage();
		
		//This function performs Results Search 
		results_ScriptResultsSearchPage.SearchResult();
		
		//This function opens results detailed screen
		results_ScriptResultsSearchPage.clickDetailedScreenForOtherResults();
		
		//This function checks for the availability of the Edit response icon
		results_ScriptResultsSearchPage.verifyEditResponseIconAvailablity();
		
		//This function navigates user back to the results list
		results_ScriptResultsSearchPage.clickReturnToListIcon();
		
		//This function opens results detailed screen
		results_ScriptResultsSearchPage.clickDetailedScreenForOwnResults();
		
		//This function checks for the availability of the Edit response icon
		results_ScriptResultsSearchPage.verifyEditResponseIconAvailablity();
	}
	
	//Regression Case for PBI #113135
	@Test
	public void verifyUpdateSystemUser() throws InterruptedException {
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		//This function navigates a user to the System user Search screen
		systemUsers_SearchResultPage.navigateToSearchSystemUser();
		
		systemUsers_SearchResultPage.clickSearchReset();
		
		//This function Clicked search for system users
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		//This function clicks Edit user icon on system user list screen
		systemUsers_SearchResultPage.ClickEditUserIcon();
		
		systemUsers_SearchResultPage.enterUserName();
		
		systemUsers_SearchResultPage.SelectPortalAccessGroup(4);
		
		//This function Selects a value from SBAccessGroup drop down
		systemUsers_SearchResultPage.SelectSBAccessGroup(2);
		
		//This function Removes the existing value from Windows login field
		systemUsers_SearchResultPage.clearWindowsLogin();
		
		//This function enters value from Windows login field
		systemUsers_SearchResultPage.EnterWindowsLogin();
		
		//This function clicks on Save Button
		systemUsers_SearchResultPage.clickSaveButton();
		
		//This function clicks on Save Button
		systemUsers_SearchResultPage.verifyUpdatedWindowsLogin();
		
		systemUsers_SearchResultPage.verifyUpdatedUserName(); 
		
		
	}
	

}
