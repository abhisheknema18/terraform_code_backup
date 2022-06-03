package MobileUsers;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.capita.fusionPortalPages.FieldSmart_HomePage;
import com.capita.fusionPortalPages.MobileUsers_AddMobileUserPage;
import com.capita.fusionPortalPages.MobileUsers_ManageMobileWorkGroupCategoryPage;
import com.capita.fusionPortalPages.MobileUsers_AddMobileWorkGroupPage;
import com.capita.fusionPortalPages.MobileUsers_EditMobileUserPage;
import com.capita.fusionPortalPages.MobileUsers_EditMobileWorkGroupPage;
import com.capita.fusionPortalPages.MobileUsers_MobileSearchResultPage;
import com.capita.fusionPortalPages.MobileUsers_MobileUsersSearchPage;
import com.capita.fusionPortalPages.MobileUsers_SearchMobileWorkGroupPage;
import com.capita.fusionPortalPages.MobileUsers_WorkGroupSelectionPage;
import com.capita.fusionPortalPages.Mobile_WorkGroupSearchPage;
import com.capita.fusionPortalPages.SystemAccess_AddAccessGroupsPage;
import com.capita.fusionPortalPages.SystemAccess_SearchAccessGroupPage;
import com.capita.fusionPortalPages.SystemUsers_SearchResultPage;
import com.capita.fusionPortalPages.Work_AssignPage;
import com.capita.fusionPortalPages.Work_WorkAssignment;
import com.capita.fusionPortalPages.Work_WorkOrderDetailsPage;
import com.capita.fusionPortalPages.Work_WorkOrderSearchPage;

import BasePackage.Basepage;
import utility.CommonUtils;

public class MobileUserTests extends Basepage{

	MobileUsers_AddMobileUserPage mobileUsers_AddMobileUserPage;
	MobileUsers_EditMobileUserPage mobileUsers_EditMobileUserPage;
	SystemUsers_SearchResultPage systemUsers_SearchResultPage;
	SystemAccess_SearchAccessGroupPage systemAccess_SearchAccessGroupPage;
	MobileUsers_SearchMobileWorkGroupPage mobileUsers_SearchMobileWorkGroupPage;
	MobileUsers_AddMobileWorkGroupPage mobileUsers_AddMobileWorkGroupPage;
	Work_WorkAssignment work_workAssignment;
	Work_AssignPage work_assignPage;
	Mobile_WorkGroupSearchPage mobile_WorkGroupSearchPage;
	MobileUsers_WorkGroupSelectionPage mobileUsers_WorkGroupSelectionPage;
	MobileUsers_MobileUsersSearchPage mobileUsers_mobileUsersSearchPage;
	MobileUsers_MobileSearchResultPage mobileusers_mobileSearchResultPage;
	MobileUsers_ManageMobileWorkGroupCategoryPage mobileUsers_ManageMobileWorkGroupCategoryPage;
	SystemAccess_AddAccessGroupsPage systemAccess_AddAccessGroupsPage;
	MobileUsers_EditMobileWorkGroupPage mobileUsers_EditMobileWorkGroupPage;
	
	@Test
	public void addMobileUser() throws InterruptedException {
		mobileUsers_AddMobileUserPage=new MobileUsers_AddMobileUserPage();
		mobileUsers_EditMobileUserPage=new MobileUsers_EditMobileUserPage();
		
		mobileUsers_AddMobileUserPage.navigateToAddMobileUsers();
		
		mobileUsers_AddMobileUserPage.enterUserCode();
		
		mobileUsers_AddMobileUserPage.enterUserName();
		
		mobileUsers_AddMobileUserPage.selectWorkGroup();
		
		mobileUsers_AddMobileUserPage.clickSaveButton();
		
		//Now Verifying the created Mobile User
		
		mobileUsers_EditMobileUserPage.navigateToEditMobileUsers();
		
		mobileUsers_EditMobileUserPage.enterUserCode(mobileUsers_AddMobileUserPage.getUserCode());
		
		mobileUsers_EditMobileUserPage.enterUserName(mobileUsers_AddMobileUserPage.getUserName());
		
		mobileUsers_EditMobileUserPage.clickSearchButton();
		
		mobileUsers_EditMobileUserPage.verifyMobileUserCreated();
		
	}
	
	@Test(dependsOnMethods={ "addMobileUser" })
	public void editMobileUser() throws InterruptedException {
		//To Make use of previous object instance, not initialising the page objects again here.
		mobileUsers_AddMobileUserPage=new MobileUsers_AddMobileUserPage();
		mobileUsers_EditMobileUserPage=new MobileUsers_EditMobileUserPage();
		
		mobileUsers_EditMobileUserPage.navigateToEditMobileUsers();
		
		mobileUsers_EditMobileUserPage.enterUserCode(mobileUsers_AddMobileUserPage.getUserCode());
		
		mobileUsers_EditMobileUserPage.enterUserName(mobileUsers_AddMobileUserPage.getUserName());
		
		mobileUsers_EditMobileUserPage.clickSearchButton();
		
		mobileUsers_EditMobileUserPage.clickEditUserIcon();
		
		mobileUsers_EditMobileUserPage.enterUpdatedUserName();
		
		mobileUsers_EditMobileUserPage.selectUpdatedWorkGroup();
		
		mobileUsers_EditMobileUserPage.clickSaveButton();
		
		mobileUsers_EditMobileUserPage.verifyMobileUserUpdated(mobileUsers_AddMobileUserPage.getUserCode());
	}
	
	//Following test for setting up the sub admin user(QUINN) and access group by Super Admin (CRONIN)
	@Test
	public void enableWorkGroupAdministration() throws InterruptedException {
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		systemUsers_SearchResultPage.navigateToSearchSystemUser();		
		
		systemUsers_SearchResultPage.clickSearchReset();
		
		//Getting the user code for Trisha User as follows from data.properties
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		
		systemUsers_SearchResultPage.enterUserCodeForSearch(Basepage.readProperty("trishausercode"));
		
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		systemUsers_SearchResultPage.readPortalAccessGroupColumnValue();
		
		
		
		
		//Now editing the searched access group
		systemAccess_SearchAccessGroupPage=new SystemAccess_SearchAccessGroupPage();
		
		systemAccess_SearchAccessGroupPage.clickSearchSystemAccessGroup();
		
		systemAccess_SearchAccessGroupPage.enterAccessGroupCodeForSearch(systemUsers_SearchResultPage.getPortalAccessGrpColVal());
		
		systemAccess_SearchAccessGroupPage.clickAccessGroupSearchButton();
		
		systemAccess_SearchAccessGroupPage.clickEditAccessGroup();
		
		systemAccess_SearchAccessGroupPage.selectTabWorkGroups();
		
		systemAccess_SearchAccessGroupPage.selectChkBoxWorkGroupAdmin();
		
		systemAccess_SearchAccessGroupPage.clickWorkGroupCategorySelectButton();
		
		systemAccess_SearchAccessGroupPage.SelectWorkGroupCategoryCheckBox();
		
		systemAccess_SearchAccessGroupPage.ClickOkButton();
		
		systemAccess_SearchAccessGroupPage.ClickSave();
		
	}
	
	@Test
	public void disableWorkGroupAdministration() throws InterruptedException {
		systemUsers_SearchResultPage=new SystemUsers_SearchResultPage();
		
		systemUsers_SearchResultPage.navigateToSearchSystemUser();		
		
		systemUsers_SearchResultPage.clickSearchReset();
		
		//Getting the user code for Trisha User as follows from data.properties
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		
		systemUsers_SearchResultPage.enterUserCodeForSearch(Basepage.readProperty("abhishekusercode"));
		
		systemUsers_SearchResultPage.ClickSearchSystemUsers();
		
		systemUsers_SearchResultPage.readPortalAccessGroupColumnValue();
		
		
		
		
		//Now editing the searched access group
		systemAccess_SearchAccessGroupPage=new SystemAccess_SearchAccessGroupPage();
		
		systemAccess_SearchAccessGroupPage.clickSearchSystemAccessGroup();
		
		systemAccess_SearchAccessGroupPage.enterAccessGroupCodeForSearch(systemUsers_SearchResultPage.getPortalAccessGrpColVal());
		
		systemAccess_SearchAccessGroupPage.clickAccessGroupSearchButton();
		
		systemAccess_SearchAccessGroupPage.clickEditAccessGroup();
		
		systemAccess_SearchAccessGroupPage.selectTabWorkGroups();
		
		systemAccess_SearchAccessGroupPage.deSelectChkBoxWorkGroupAdmin();
		
		systemAccess_SearchAccessGroupPage.ClickSave();
		
	}
	
	//Following test for User with Workgroup administration rights (QUINN User)
	@Test
	public void verifyVisibilityOfAdminMenus() {
		mobileUsers_EditMobileUserPage=new MobileUsers_EditMobileUserPage();
		
		mobileUsers_EditMobileUserPage.verifyAdminMenuMobileUserDisplayed();
		mobileUsers_EditMobileUserPage.verifyAdminMenuSystemUsersNotDisplayed();
		mobileUsers_EditMobileUserPage.verifyAdminMenuSharedAccessTokensNotDisplayed();
		mobileUsers_EditMobileUserPage.verifyAdminMenuSystemAccessNotDisplayed();
		
	}
	
	@Test
	public void verifyVisibilityOfMobileUserSubMenus() {
		mobileUsers_EditMobileUserPage=new MobileUsers_EditMobileUserPage();
		
		mobileUsers_EditMobileUserPage.verifyMobileUserSubMenuDisplayed();
		
	}
	
	@Test
	public void verifyWorkGroupCategoryForMobileWorkGroupSearch() throws InterruptedException {
		mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
		
		mobileUsers_SearchMobileWorkGroupPage.navigateToSearchMobileWorkGroups();
		mobileUsers_SearchMobileWorkGroupPage.clickSearch();
		mobileUsers_SearchMobileWorkGroupPage.verifyWorkGroupCategoryColumnValue();
		
	}
	
	@Test
	public void verifyWorkGroupDropDownListValues() throws InterruptedException {
		mobileUsers_AddMobileWorkGroupPage=new MobileUsers_AddMobileWorkGroupPage();
		
		mobileUsers_AddMobileWorkGroupPage.navigateToAddMobileWorkGroups();
		
		mobileUsers_AddMobileWorkGroupPage.verifyWorkGroupCategoryDropDownOptions();
		
		mobileUsers_AddMobileWorkGroupPage.clickCancelButton();
	}
	
	@Test
	public void verifyWorkGroupsInAddMobileUser() throws InterruptedException {
		mobileUsers_AddMobileUserPage=new MobileUsers_AddMobileUserPage();
		
		mobileUsers_AddMobileUserPage.navigateToAddMobileUsers();
		
		mobileUsers_AddMobileUserPage.clickWorkGroupSelectButton();
		
		mobileUsers_AddMobileUserPage.verifyVisibleWorkGroups();
		
		mobileUsers_AddMobileUserPage.clickCancelButtonWorkGroupSelection();
		
		mobileUsers_AddMobileUserPage.clickCancelButton();
	}
	
	@Test
	public void verifyWorkGroupsInSearchMobileUser() throws InterruptedException {
		mobileUsers_EditMobileUserPage=new MobileUsers_EditMobileUserPage();
		
		mobileUsers_EditMobileUserPage.navigateToEditMobileUsers();
		
		mobileUsers_EditMobileUserPage.clickSelectButton();
		
		mobileUsers_EditMobileUserPage.verifyVisibleWorkGroups();
		
		mobileUsers_EditMobileUserPage.clickCancelButtonWorkGroupSelection();
		
		mobileUsers_EditMobileUserPage.clickCancelButton();
		
		
	}
	
	///Owner : Prathamesh
	/// TestID : 117839
	@Test
	public void VerifyWorkGroupCodeDisplayInSlidePanel() throws InterruptedException{
		try {
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			this.mobileUsers_SearchMobileWorkGroupPage.clickWorkGroups(0);
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			new Mobile_WorkGroupSearchPage().clickEditMobileWorkGroupBtn();
			
			Assert.assertTrue(new MobileUsers_EditMobileWorkGroupPage().VerifyLabelDisplay("Work Group Code"), "Work Group Code - Label is not displayed");
		}
		finally {
			CommonUtils.RefreshPage(Basepage.driver);
		}
		
	}
	
	///Owner : Prathamesh
	/// TestID : 117837
	@Test
	public void VerifyWorkGroupCanBeDeleted() throws InterruptedException {
		try {
			String workGroup = RandomStringUtils.randomAlphabetic(8);
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Add Mobile Work Group");
			
			this.mobileUsers_AddMobileWorkGroupPage = new MobileUsers_AddMobileWorkGroupPage();
			
			this.mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupCode(workGroup);
			this.mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupDescription(workGroup);
			this.mobileUsers_AddMobileWorkGroupPage.ClickSaveButton();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			this.mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup(workGroup);
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			this.mobile_WorkGroupSearchPage = new Mobile_WorkGroupSearchPage();
			this.mobile_WorkGroupSearchPage.clickEditMobileWorkGroupBtn();
			
			this.mobileUsers_WorkGroupSelectionPage = new MobileUsers_WorkGroupSelectionPage();
			this.mobileUsers_WorkGroupSelectionPage.clickDeleteButton();
			this.mobileUsers_WorkGroupSelectionPage.clickYesButton();
			
			//Thread.sleep(5000);
			Assert.assertTrue(new MobileUsers_WorkGroupSelectionPage().VerifyZeroRecordPresent(), "Record is not deleted");	
		}
		finally {
			CommonUtils.RefreshPage(Basepage.driver);
		}
	}
	
	///Owner : Prathamesh
	/// TestID : 117828, 117830
	@Test
	public void VerifyAssignedWorkGroupDeletion() throws InterruptedException{
		try {
			String workGroup = RandomStringUtils.randomAlphabetic(8);
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Add Mobile Work Group");
			
			this.mobileUsers_AddMobileWorkGroupPage = new MobileUsers_AddMobileWorkGroupPage();
			
			this.mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupCode(workGroup);
			this.mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupDescription(workGroup);
			this.mobileUsers_AddMobileWorkGroupPage.ClickSaveButton();
			
			new FieldSmart_HomePage().selectWorkOptionsElement("Search");
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickworkOrderSearchBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickViewDetailsBtn();
			
			new Work_WorkOrderDetailsPage().clickEditFormBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickAssignWorkOrderBtn();
			
			new Work_AssignPage().searchWorkGroup(workGroup, "OK");
			
			new Work_WorkOrderDetailsPage().clickSaveBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickHomePageMenuLink();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			this.mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup(workGroup);
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			this.mobile_WorkGroupSearchPage = new Mobile_WorkGroupSearchPage();
			this.mobile_WorkGroupSearchPage.clickEditMobileWorkGroupBtn();
			
			this.mobileUsers_WorkGroupSelectionPage = new MobileUsers_WorkGroupSelectionPage();
			this.mobileUsers_WorkGroupSelectionPage.clickDeleteButton();
			
			new MobileUsers_WorkGroupSelectionPage().VerifyWarningMessage("Work Orders are associated with this Work Group. Unable to delete");
			new MobileUsers_WorkGroupSelectionPage().clickCancelBtn();
			
			new FieldSmart_HomePage().selectWorkOptionsElement("Search");
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickworkOrderSearchBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickViewDetailsBtn();
			
			new Work_WorkOrderDetailsPage().clickEditFormBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickAssignWorkOrderBtn();
			
			new Work_AssignPage().searchWorkGroup("REGRESSION DATA SETUP", "OK");
			
			new Work_WorkOrderDetailsPage().clickSaveBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickHomePageMenuLink();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			TimeUnit.SECONDS.sleep(1);
			//this.mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup(workGroup);
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			this.mobile_WorkGroupSearchPage = new Mobile_WorkGroupSearchPage();
			this.mobile_WorkGroupSearchPage.clickEditMobileWorkGroupBtn();
			
			this.mobileUsers_WorkGroupSelectionPage = new MobileUsers_WorkGroupSelectionPage();
			this.mobileUsers_WorkGroupSelectionPage.clickDeleteButton();
			
			this.mobileUsers_WorkGroupSelectionPage.clickYesButton();
			
			Assert.assertTrue(new MobileUsers_WorkGroupSelectionPage().VerifyZeroRecordPresent(), "Record is not deleted");
		}
		finally {
			CommonUtils.RefreshPage(Basepage.driver);
		}	
	}
	
	/// Owner : Prathamesh
	/// TestID : 117827
	@Test
	public void VerifyWorkOrderCurrentFunctionality() throws InterruptedException{
		try {
			String workGroup = RandomStringUtils.randomAlphabetic(8);
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Add Mobile Work Group");
			
			this.mobileUsers_AddMobileWorkGroupPage = new MobileUsers_AddMobileWorkGroupPage();
			
			this.mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupCode(workGroup);
			this.mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupDescription(workGroup);
			this.mobileUsers_AddMobileWorkGroupPage.ClickSaveButton();
			
			new FieldSmart_HomePage().selectWorkOptionsElement("Search");
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickworkOrderSearchBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickViewDetailsBtn();
			
			new Work_WorkOrderDetailsPage().clickEditFormBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickAssignWorkOrderBtn();
			
			new Work_AssignPage().searchWorkGroup(workGroup, "OK");
			
			new Work_WorkOrderDetailsPage().clickSaveBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickHomePageMenuLink();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Add Mobile User");
			
			this.mobileUsers_AddMobileUserPage=new MobileUsers_AddMobileUserPage();
			
			this.mobileUsers_AddMobileUserPage.enterUserCode();
			
			this.mobileUsers_AddMobileUserPage.enterUserName();
			
			this.mobileUsers_AddMobileUserPage.selectWorkGroup(workGroup);
			
			this.mobileUsers_AddMobileUserPage.clickSaveButton();
			
			TimeUnit.SECONDS.sleep(2);
			
			new FieldSmart_HomePage().selectWorkOptionsElement("Search");
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickworkOrderSearchBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderSearchPage().clickViewDetailsBtn();
			
			new Work_WorkOrderDetailsPage().clickEditFormBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickAssignWorkOrderBtn();
			
			new Work_AssignPage().searchWorkGroup("REGRESSION DATA SETUP", "OK");
			
			new Work_WorkOrderDetailsPage().clickSaveBtn();
			TimeUnit.SECONDS.sleep(1);
			new Work_WorkOrderDetailsPage().clickHomePageMenuLink();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			this.mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup(workGroup);
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			this.mobile_WorkGroupSearchPage = new Mobile_WorkGroupSearchPage();
			this.mobile_WorkGroupSearchPage.clickEditMobileWorkGroupBtn();
			
			this.mobileUsers_WorkGroupSelectionPage = new MobileUsers_WorkGroupSelectionPage();
			this.mobileUsers_WorkGroupSelectionPage.clickDeleteButton();
			TimeUnit.SECONDS.sleep(1);
			
			new MobileUsers_WorkGroupSelectionPage().VerifyWarningMessage("Users are associated with this Work Group. Unable to delete");
			new MobileUsers_WorkGroupSelectionPage().clickCancelBtn();
		}
		finally {
			CommonUtils.RefreshPage(Basepage.driver);
		}
		
	}
	
	///Owner : Prathamesh
	///TestID : 117836
	@Test
	public void VerifyScriptResultCurrentFunctionality() throws InterruptedException{
		try {
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			this.mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup("AutoTest22");
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			this.mobile_WorkGroupSearchPage = new Mobile_WorkGroupSearchPage();
			this.mobile_WorkGroupSearchPage.clickEditMobileWorkGroupBtn();
			
			this.mobileUsers_WorkGroupSelectionPage = new MobileUsers_WorkGroupSelectionPage();
			this.mobileUsers_WorkGroupSelectionPage.clickDeleteButton();
			
			this.mobileUsers_WorkGroupSelectionPage.VerifyWarningMessage("Users are associated with this Work Group. Unable to delete");
			
			this.mobileUsers_WorkGroupSelectionPage.clickCancelBtn();
		}
		finally {
			CommonUtils.RefreshPage(Basepage.driver);
		}
	}
	
	///Owner : Prathamesh
	///TestID : 117833
	@Test
	public void VerifyScriptResultDeleteWorkGroupDueToExistingWO() throws InterruptedException{
		
		try {
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Users");
			
			this.mobileUsers_mobileUsersSearchPage = new MobileUsers_MobileUsersSearchPage();
			this.mobileUsers_mobileUsersSearchPage.selectWorkGroup("AutoTest22");
			this.mobileUsers_mobileUsersSearchPage.clickSearch();
			
			this.mobileusers_mobileSearchResultPage = new MobileUsers_MobileSearchResultPage();
			this.mobileusers_mobileSearchResultPage.clickEditMobileUser();
			this.mobileusers_mobileSearchResultPage.clickBtn_SelectWorkGroup();
			
			new MobileUsers_WorkGroupSelectionPage().SelectSingleworkGroupForMobileUser("DUB-STSE");
			
			this.mobileusers_mobileSearchResultPage.clickSaveBtn();
			
			TimeUnit.SECONDS.sleep(10);
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			
			this.mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			this.mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup("AutoTest22");
			this.mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			
			this.mobile_WorkGroupSearchPage = new Mobile_WorkGroupSearchPage();
			this.mobile_WorkGroupSearchPage.clickEditMobileWorkGroupBtn();
			
			this.mobileUsers_WorkGroupSelectionPage = new MobileUsers_WorkGroupSelectionPage();
			this.mobileUsers_WorkGroupSelectionPage.clickDeleteButton();
			
			this.mobileUsers_WorkGroupSelectionPage.VerifyWarningMessage("Script Results are associated with this Work Group. Unable to delete");
			
			this.mobileUsers_WorkGroupSelectionPage.clickCancelBtn();
		}
			finally {
				CommonUtils.RefreshPage(Basepage.driver);
				new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Users");
				
				this.mobileUsers_mobileUsersSearchPage = new MobileUsers_MobileUsersSearchPage();
				this.mobileUsers_mobileUsersSearchPage.enterUserName("PTest221285");
				this.mobileUsers_mobileUsersSearchPage.clickAllWorkGroupRadioButton();
				this.mobileUsers_mobileUsersSearchPage.clickSearch();
				
				this.mobileusers_mobileSearchResultPage = new MobileUsers_MobileSearchResultPage();
				this.mobileusers_mobileSearchResultPage.clickEditMobileUser();
				this.mobileusers_mobileSearchResultPage.clickBtn_SelectWorkGroup();
				
				new MobileUsers_WorkGroupSelectionPage().SelectSingleworkGroupForMobileUser("AutoTest22");
				
				this.mobileusers_mobileSearchResultPage.clickSaveBtn();
			}
	}
	
	//Owner: Abhishek	
	@Test
	public void VerifyAccessGroupAssociatedWorkGroupCategoryDeletion() throws InterruptedException{
		new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Manage Mobile Work Group Category");
		
		mobileUsers_ManageMobileWorkGroupCategoryPage=new MobileUsers_ManageMobileWorkGroupCategoryPage();
		
		mobileUsers_ManageMobileWorkGroupCategoryPage.clickAddWorkGroupCategoryIcon();
		mobileUsers_ManageMobileWorkGroupCategoryPage.enterWorkGroupCategoryName(RandomStringUtils.randomAlphabetic(8));
		mobileUsers_ManageMobileWorkGroupCategoryPage.clickSaveButton();
		mobileUsers_ManageMobileWorkGroupCategoryPage.closeSlideIn();
		
		new FieldSmart_HomePage().selectAdminOptionsElement("System Access", "Add Access Groups");
		
		systemAccess_AddAccessGroupsPage=new SystemAccess_AddAccessGroupsPage();
		
		systemAccess_AddAccessGroupsPage.enterAccessGroupCode(RandomStringUtils.randomAlphabetic(8));
		systemAccess_AddAccessGroupsPage.enterAccessGroupDescription(RandomStringUtils.randomAlphabetic(8));
		systemAccess_AddAccessGroupsPage.clickTabWorkGroups();
		systemAccess_AddAccessGroupsPage.selectWorkGroupAdministrationCheckBox();
		systemAccess_AddAccessGroupsPage.clickSelectButton();
		systemAccess_AddAccessGroupsPage.clickWorkGroupSelectionClearButton();
		systemAccess_AddAccessGroupsPage.selectWorkGroupCategory(mobileUsers_ManageMobileWorkGroupCategoryPage.getWorkGroupCategoryName());
		systemAccess_AddAccessGroupsPage.clickWorkGroupSelectionOkButton();
		systemAccess_AddAccessGroupsPage.clickSaveButton();
		
		new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Manage Mobile Work Group Category");
		
		mobileUsers_ManageMobileWorkGroupCategoryPage.clickDeleteIcon(mobileUsers_ManageMobileWorkGroupCategoryPage.getWorkGroupCategoryName());
		mobileUsers_ManageMobileWorkGroupCategoryPage.verifyValidationForAssociatedSystemAccessGroups();
		mobileUsers_ManageMobileWorkGroupCategoryPage.closeSlideIn();
	}
	
	//Owner: Abhishek	
		@Test
		public void VerifyAccessGroupAssociatedWorkGroupDeletion() throws InterruptedException{
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Add Mobile Work Group");
			
			mobileUsers_AddMobileWorkGroupPage=new MobileUsers_AddMobileWorkGroupPage();
			
			String workGroupCode=RandomStringUtils.randomAlphabetic(8);
			String workGroupDescription=RandomStringUtils.randomAlphabetic(8);
			mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupCode(workGroupCode);
			mobileUsers_AddMobileWorkGroupPage.EnterWorkGroupDescription(workGroupDescription);
			mobileUsers_AddMobileWorkGroupPage.ClickSaveButton();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("System Access", "Add Access Groups");
			systemAccess_AddAccessGroupsPage=new SystemAccess_AddAccessGroupsPage();
			systemAccess_AddAccessGroupsPage.enterAccessGroupCode(RandomStringUtils.randomAlphabetic(8));
			systemAccess_AddAccessGroupsPage.enterAccessGroupDescription(RandomStringUtils.randomAlphabetic(8));
			systemAccess_AddAccessGroupsPage.clickTabAccess();
			systemAccess_AddAccessGroupsPage.clickSelectButtonAccessTabWorkGroups();
			systemAccess_AddAccessGroupsPage.clickBtnClearAccessTabWGSelection();
			systemAccess_AddAccessGroupsPage.selectWorkGroupAccessTab(workGroupDescription);
			systemAccess_AddAccessGroupsPage.clickBtnOKAccessTabWGSelection();
			
			systemAccess_AddAccessGroupsPage.clickSaveButton();
			
			new FieldSmart_HomePage().selectAdminOptionsElement("Mobile Users", "Search Mobile Work Groups");
			mobileUsers_SearchMobileWorkGroupPage=new MobileUsers_SearchMobileWorkGroupPage();
			
			mobileUsers_SearchMobileWorkGroupPage.selectWorkGroup(workGroupCode);
			mobileUsers_SearchMobileWorkGroupPage.clickSearch();
			mobileUsers_SearchMobileWorkGroupPage.clickWGEditIcon(workGroupCode);
			
			mobileUsers_EditMobileWorkGroupPage = new MobileUsers_EditMobileWorkGroupPage();
			mobileUsers_EditMobileWorkGroupPage.clickDeleteButton();
			mobileUsers_EditMobileWorkGroupPage.verifyDeleteValidationForAssociatedAccessGroups();
			mobileUsers_EditMobileWorkGroupPage.clickCancelButton();
		}
	
}
