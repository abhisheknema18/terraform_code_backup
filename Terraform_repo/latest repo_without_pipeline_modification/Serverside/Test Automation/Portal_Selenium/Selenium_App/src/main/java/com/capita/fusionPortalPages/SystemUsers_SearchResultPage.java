package com.capita.fusionPortalPages;

import java.util.ArrayList;
import java.util.Collections;
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

public class SystemUsers_SearchResultPage extends Basepage {

	public SystemUsers_SearchResultPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='System Users']")
	WebElement Menu_SystemUsers;
	
	@FindBy(xpath="//a[text()='Search System Users']")
	WebElement SubMenu_SearchSystemUsers;
	
	@FindBy(xpath="//button[@id='searchSytemUsers:searchForm:searchAdmin']")
	WebElement btn_SearchSystemUser;
	
	@FindBy(xpath="//button[@id='searchSytemUsers:searchForm:resetBtn']")
	WebElement btn_SearchReset;
	
	
	@FindBy(xpath="//label[@id='addSystemUserForm:sbAccessGroup_label']")
	WebElement dropDown_SBAccessGroup;
	
	@FindBy(xpath="//label[@id='addSystemUserForm:fdmAccessGroup_label']")
	WebElement dropDown_PortalAccessGroup;
	
	
	@FindBy(xpath="//button[contains(@id,'addSystemUserForm')]/span[text()='Save']")
	WebElement btn_Save;
	
	@FindBy(xpath="//button[contains(@id,'addSystemUserForm')]/span[text()='Cancel']")
	WebElement btn_Cancel;
	
	@FindBy(xpath="//input[@id='addSystemUserForm:windowsLoginInput']")
	WebElement txtBox_WindowsLogin;
	
	@FindBy(xpath="//th/span[text()='Windows User']/following::input[1]")
	WebElement txtBox_FilterWindowsUser;
	
	@FindBy(xpath="//th/span[text()='Windows User']")
	WebElement colHeader_WindowsUser;
	
	@FindBy(xpath="//input[@id='searchSytemUsers:searchForm:userCode']")
	WebElement txtBox_Search_UserCode;
	
	@FindBy(xpath="//input[@id='addSystemUserForm:userName']")
	WebElement txtBox_UserName;
	
	
	
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToSearchSystemUser() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
//			TimeUnit.SECONDS.sleep(3);
			
			if(Menu_SystemUsers.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_SystemUsers.click();
//				TimeUnit.SECONDS.sleep(3);
				SubMenu_SearchSystemUsers.click();
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
	
	public void clickSearchReset() {
		btn_SearchReset.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void enterUserCodeForSearch(String UserCode) {
		txtBox_Search_UserCode.sendKeys(UserCode);
	}
	
	public void ClickSearchSystemUsers(){
		btn_SearchSystemUser.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked search system user button");
	}
	
	
	
	public void ClickEditUserIcon() {
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String usercode=Basepage.readProperty("usercode");
		WebElement IconEditUser=driver.findElement(By.xpath("//td[text()='"+usercode+"']/following::td[7]"));
		IconEditUser.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked edit user icon");
	}
	
	public void SelectSBAccessGroup(int index) {
		
		dropDown_SBAccessGroup.click();
		driver.findElement(By.xpath("//ul[@id='addSystemUserForm:sbAccessGroup_items']/li["+index+"]")).click();
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Selected SB Access value");

	}
	
	public void clearWindowsLogin() {
		txtBox_WindowsLogin.clear();
	}
	
	private String UserName;
	public void enterUserName() {
		txtBox_UserName.clear();
		UserName=RandomStringUtils.randomAlphabetic(8);
		txtBox_UserName.sendKeys(UserName);
	}
	
	public void verifyUpdatedUserName() {
		
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String usercode=Basepage.readProperty("usercode");
		String UpdatedUserName=driver.findElement(By.xpath("//td[text()='"+usercode+"']/following::td[1]")).getText();
		
		//verification of expected and actual windows login
		Assert.assertEquals(UpdatedUserName, UserName);
		logger.info("Updated windows Login values is verified successfully");
	}
	
	public void verifyWindowsLoginMandatoryValidation() {
		WebElement validationMessage=driver.findElement(By.xpath("//span[text()='Windows Login must be supplied for a System User belonging to an SB Access Group.']"));
		Assert.assertTrue(validationMessage.isDisplayed());
		logger.info("Mandatory Validation Message displyas successfully");
	}
	
	private String WindowsLogin;
	
	public void EnterWindowsLogin() {
		WindowsLogin=RandomStringUtils.randomAlphabetic(8);
		
		txtBox_WindowsLogin.sendKeys(WindowsLogin);
		
		logger.info("Entered windows login");
	}
	
	public void clickSaveButton() {
		btn_Save.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Save button");
	}
	
	public void clickCancelButton() {
		btn_Cancel.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Cancel button");
	}
	
	public void verifyUpdatedWindowsLogin() {
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String usercode=Basepage.readProperty("usercode");
		String UpdatedWindowsLogin=driver.findElement(By.xpath("//td[text()='"+usercode+"']/following::td[5]")).getText();
		
		//verification of expected and actual windows login
		Assert.assertEquals(UpdatedWindowsLogin, WindowsLogin);
		logger.info("Updated windows Login values is verified successfully");
	}
	
	public void verifyWindowsLoginFieldLength() {
		
		txtBox_WindowsLogin.sendKeys(RandomStringUtils.randomAlphabetic(34));
		Assert.assertTrue(txtBox_WindowsLogin.getAttribute("maxlength").equalsIgnoreCase("32"));
		logger.info("Windows Login Field length verified successfully");

	}
	
	public void verifyWindowsLoginEnabledorDisabled() {
		//if enabled else disabled. freshly fetch the WebElement and check for the attribute. This method should run after the SB access seletion
		if(txtBox_WindowsLogin.isEnabled()) {
			logger.info("Windows Login is enabled");
		}
		else {
			logger.info("Windows Login is disabled");
		}
		
	}
	
	public void verifyFilterByWindowsUser() {
		txtBox_FilterWindowsUser.sendKeys("a");
		logger.info("Entered a string for Windows User Filter");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		// TimeUnit.SECONDS.sleep(5);

		List<WebElement> NoRecordsMessage = driver.findElements(By.xpath(
				"//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']//td[text()='No records found.']"));

		if (NoRecordsMessage.size() == 0) {
			// Fetching the list of records when input in entered for filter
			List<WebElement> WindowsUserColumnEachRow = driver.findElements(
					By.xpath("//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']/tr/td[6]"));

			// checking if entered filter characters present in all the records windows user column
			for (WebElement counter : WindowsUserColumnEachRow) {
				Assert.assertTrue(counter.getText().toLowerCase().contains("a"));
			}
			logger.info("Filter Windows User verified Successfully");

		} else {
			logger.info("No Records displayed for entered filter");
		}
	}
	
	public void VerifySortingByWindowsUser() {
		// Fetching the default list of tokens
		List<WebElement> WindowsUsersObtained = driver.findElements(
				By.xpath("//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']/tr/td[6]"));
		List<String> WindowsUsersObtainedList = new ArrayList<>();
		for (WebElement AppName : WindowsUsersObtained) {
			WindowsUsersObtainedList.add(AppName.getText());
		}

		colHeader_WindowsUser.click();
		logger.info("Clicked Application Name Header");
		// TimeUnit.SECONDS.sleep(5);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));

		// Fetching the list of tokens sorted in ascending order with application name
		List<WebElement> WindowsUserGrid = driver.findElements(
				By.xpath("//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']/tr/td[6]"));
		List<String> WindowsUserSortedList = new ArrayList<>();

		for (WebElement WinUserName : WindowsUserGrid) {
			WindowsUserSortedList.add(WinUserName.getText());
		}

		Collections.sort(WindowsUsersObtainedList);
		Collections.sort(WindowsUserSortedList);
		Assert.assertTrue(WindowsUsersObtainedList.equals(WindowsUserSortedList));
		logger.info("Sorted successfully by Windows User Ascending");
		// TimeUnit.SECONDS.sleep(3);

		// Now Reverse Sorting by Application Name
		colHeader_WindowsUser.click();
		logger.info("Clicked Application Name Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		// TimeUnit.SECONDS.sleep(5);

		// Fetching the list of tokens sorted in descending order with application name
		List<WebElement> WindowsUserObtainedDescending = driver.findElements(
				By.xpath("//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']/tr/td[6]"));
		List<String> WindowsUserObtainedListDescending = new ArrayList<>();
		for (WebElement WinUser : WindowsUserObtainedDescending) {
			WindowsUserObtainedListDescending.add(WinUser.getText());
		}

		Collections.sort(WindowsUsersObtainedList, Collections.reverseOrder());
		Collections.sort(WindowsUserObtainedListDescending, Collections.reverseOrder());

		Assert.assertTrue(WindowsUsersObtainedList.equals(WindowsUserObtainedListDescending));
		logger.info("Sorted by Application Name Descending");
		// TimeUnit.SECONDS.sleep(3);

	}
	
	

	public void SelectPortalAccessGroup(int index) {

		dropDown_PortalAccessGroup.click();
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.findElement(By.xpath("//ul[@id='addSystemUserForm:fdmAccessGroup_items']/li["+index+"]")).click();
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Selected Portal Access value");

	}
	
	public void verifySearchRecord() {
		List<WebElement> searchRecord=driver.findElements(By.xpath("//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']/tr"));
		Assert.assertTrue(searchRecord.size()>0);
	}
	
	private String portalAccessGrpColVal;
	public void readPortalAccessGroupColumnValue() {
		portalAccessGrpColVal=driver.findElement(By.xpath("//tbody[@id='adminSearchTabs:systemUserAdminSearchListForm:userList_data']/tr/td[3]")).getText();
	}
	
	public String getPortalAccessGrpColVal() {

		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portalAccessGrpColVal;
		
	}

}

