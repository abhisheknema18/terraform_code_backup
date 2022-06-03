package com.capita.fusionPortalPages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class MobileUsers_EditMobileUserPage extends Basepage {
	
	public MobileUsers_EditMobileUserPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='Mobile Users']")
	WebElement Menu_MobileUsers;
	
	@FindBy(xpath="//a[text()='Shared Access Tokens']")
	WebElement Menu_SharedAccessTokens;
	
	@FindBy(xpath="//a[text()='System Users']")
	WebElement Menu_SystemUsers;
	
	@FindBy(xpath="//a[text()='System Access']")
	WebElement Menu_SystemAccess;
	
	@FindBy(xpath="//a[text()='Search Mobile Users']")
	WebElement SubMenu_SearchMobileUsers;
	
	@FindBy(xpath="//a[text()='Search Mobile Work Groups']")
	WebElement SubMenu_SearchMobileWorkGroups;
	
	@FindBy(xpath="//a[text()='Add Mobile User']")
	WebElement SubMenu_AddMobileUser;
	
	@FindBy(xpath="//a[text()='Add Mobile Work Group']")
	WebElement SubMenu_AddMobileWorkGroup;
	
	
	
	
	@FindBy(xpath="//input[@id='searchUsers:searchForm:userCode']")
	WebElement txtbox_SearchUsercode;
	
	@FindBy(xpath="//input[@id='searchUsers:searchForm:userName']")
	WebElement txtbox_SearchUserName;
	
	@FindBy(xpath="//button[contains(@id,'searchUsers:searchForm:searchAdmin')]/span[text()='Search']")
	WebElement btn_Search;
	
	@FindBy(xpath="//button[contains(@id,'searchUsers:searchForm:resetBtn')]/span[text()='Reset']")
	WebElement btn_Reset;
	
	
	@FindBy(xpath="//input[@id='addUserForm:userCodeAdd']")
	WebElement txtbox_EditUserCode;
	
	@FindBy(xpath="//input[@id='addUserForm:userName']")
	WebElement txtbox_EditUserName;
	
	@FindBy(xpath="//button[@id='addUserForm:editWorkgroups']")
	WebElement btn_EditSelectWorkGroup;
	
	@FindBy(xpath="//li[@id='SingleWorkgroupSelectionFormUserAdmin:workgroupSelectTree:0_1']/span/span[3]")
	WebElement EditselectSecondworkGroup;
	
	@FindBy(xpath="//button[contains(@id,'SingleWorkgroupSelectionFormUserAdmin')]/span[text()='OK']")
	WebElement btn_EditOkWorkgroupSelection;
	
	@FindBy(xpath="//button[contains(@id,'addUserForm')]/span[text()='Save']")
	WebElement btn_Save;
	
	@FindBy(xpath="//button[contains(@id,'searchUsers:searchForm:cancelAdmin')]/span[text()='Cancel']")
	WebElement btn_Cancel;
	
	@FindBy(xpath="//button[contains(@id,'searchUsers:searchForm:showWorkgroups')]/span[text()='Select']")
	WebElement btn_Select;
	
	@FindBy(xpath="//div[@id='WorkgroupSelectionFormUserAdmin:workgroupSelectionP_footer']/button[contains(@id,'WorkgroupSelectionFormUserAdmin')]/span[text()='Cancel']")
	WebElement btn_CancelWorkGroupSelection;
	
	
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToEditMobileUsers() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			
			if(Menu_MobileUsers.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_MobileUsers.click();

				SubMenu_SearchMobileUsers.click();
				wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
				logger.info("click View Access Token sub menu");
				TimeUnit.SECONDS.sleep(2);
			}
			else {
				logger.info("User has no access to Module Shared Access Tokens");
			}
			
		} else {
			logger.info("Logged In User is not an Admin User");
			
		}
	}
	
	public void enterUserCode(String usercode) {
		btn_Reset.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		txtbox_SearchUsercode.sendKeys(usercode);
		logger.info("enetered User Code");
	}

	public void enterUserName(String username) {
		txtbox_SearchUserName.sendKeys(username);
		logger.info("Entered user name");
	}

	public void clickSearchButton() {
		btn_Search.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Search Button");
	}
	
	private List<WebElement> mobileUsersRecords;
	public void verifyMobileUserCreated() {
		mobileUsersRecords=driver.findElements(By.xpath("//tbody[@id='adminSearchTabs:userAdminSearchListForm:userList_data']/tr"));
		Assert.assertFalse(mobileUsersRecords.isEmpty());
		logger.info("Mobile User Created Successfully");
	}
	
	public void clickEditUserIcon() {
		if(!mobileUsersRecords.isEmpty()) {
			WebElement icon_EditUser=driver.findElement(By.xpath("//tbody[@id='adminSearchTabs:userAdminSearchListForm:userList_data']/tr/td[6]/a"));
			icon_EditUser.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
		else
			logger.info("No records for the mobile user available");
		
	}
	private String editedUserName;
	public void enterUpdatedUserName() {
		editedUserName=RandomStringUtils.randomAlphabetic(8);
		txtbox_EditUserName.clear();
		txtbox_EditUserName.sendKeys(editedUserName);		
	}
	public void selectUpdatedWorkGroup() {
		btn_EditSelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		EditselectSecondworkGroup.click();
		btn_EditOkWorkgroupSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSaveButton() {
		btn_Save.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyMobileUserUpdated(String usercode) throws InterruptedException {
		navigateToEditMobileUsers();
		enterUserCode(usercode);
		enterUserName(editedUserName);
		btn_Search.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		mobileUsersRecords=driver.findElements(By.xpath("//tbody[@id='adminSearchTabs:userAdminSearchListForm:userList_data']/tr"));
		Assert.assertFalse(mobileUsersRecords.isEmpty());
		logger.info("Mobile User Updated Successfully");
		
	}

	public void verifyAdminMenuMobileUserDisplayed() {
		if (Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			Assert.assertTrue(Menu_MobileUsers.isDisplayed());
		}

	}

	public void verifyMobileUserSubMenuDisplayed() {
		if (Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();

			if (Menu_MobileUsers.isDisplayed()) {
				logger.info("User has access to Mobile Users Sub Menu");
				Menu_MobileUsers.click();

				Assert.assertTrue(SubMenu_AddMobileUser.isDisplayed());
				Assert.assertTrue(SubMenu_SearchMobileUsers.isDisplayed());
				Assert.assertTrue(SubMenu_AddMobileWorkGroup.isDisplayed());
				Assert.assertTrue(SubMenu_SearchMobileWorkGroups.isDisplayed());

			}
		}

	}
	
	public void verifyAdminMenuSystemUsersNotDisplayed() {
		if (Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			Assert.assertFalse(Menu_SystemUsers.isDisplayed());
		}
			
	}
	
	public void verifyAdminMenuSystemAccessNotDisplayed() {
		if (Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			Assert.assertFalse(Menu_SystemAccess.isDisplayed());
		}
	}
	
	public void verifyAdminMenuSharedAccessTokensNotDisplayed() {
		if (Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			Assert.assertFalse(Menu_SharedAccessTokens.isDisplayed());
		}
	}
	
	public void clickSelectButton() {
		btn_Select.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyVisibleWorkGroups() {
		List<WebElement> visibleWorkGroups=driver.findElements(By.xpath("//div[@id='WorkgroupSelectionFormUserAdmin:workgroupSelectTree']/ul/li/span/span[3]"));
		List<String> visibleWorkGroupsText=new ArrayList<String>();
		for(WebElement temp:visibleWorkGroups) {
			visibleWorkGroupsText.add(temp.getText());
		}
		
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String WorkGroupCategory=Basepage.readProperty("WorkGroupCategory");
		
		for(String temp:visibleWorkGroupsText) {
			Assert.assertTrue(temp.contains(WorkGroupCategory));
		}
		
	}
	
	public void clickCancelButton() {
		btn_Cancel.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickCancelButtonWorkGroupSelection() {
		btn_CancelWorkGroupSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	
}

