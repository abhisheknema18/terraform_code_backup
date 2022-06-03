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

public class MobileUsers_AddMobileUserPage extends Basepage {
	
	public MobileUsers_AddMobileUserPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='Mobile Users']")
	WebElement Menu_MobileUsers;
	
	@FindBy(xpath="//a[text()='Add Mobile User']")
	WebElement SubMenu_AddMobileUser;
	
	@FindBy(xpath="//input[@id='addUserForm:userCodeAdd']")
	WebElement txtbox_UserCode;
	
	@FindBy(xpath="//input[@id='addUserForm:userName']")
	WebElement txtbox_UserName;
	
	@FindBy(xpath="//button[@id='addUserForm:editWorkgroups']")
	WebElement btn_SelectWorkGroup;
	
	@FindBy(xpath="//li[@id='SingleWorkgroupSelectionFormUserAdmin:workgroupSelectTree:0_0']/span/span[3]")
	WebElement selectFirstworkGroup;
	
	@FindBy(xpath="//button[contains(@id,'SingleWorkgroupSelectionFormUserAdmin')]/span[text()='OK']")
	WebElement btn_OkWorkgroupSelection;
	
	@FindBy(xpath="//button[contains(@id,'addUserForm')]/span[text()='Save']")
	WebElement btn_Save;
	
	@FindBy(xpath="//button[contains(@id,'addUserForm')]/span[text()='Cancel']")
	WebElement btn_Cancel;
	
	@FindBy(xpath="//button[contains(@id,'SingleWorkgroupSelectionFormUserAdmin')]/span[text()='Cancel']")
	WebElement btn_CancelWorkGroupSelection;
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToAddMobileUsers() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			
			if(Menu_MobileUsers.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_MobileUsers.click();

				SubMenu_AddMobileUser.click();
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
	
	private String UserCode;
	private String UserName;
	public void enterUserCode() {
		UserCode=RandomStringUtils.randomAlphabetic(8);
		txtbox_UserCode.sendKeys(UserCode);
		
	}
	public void enterUserName() {
		UserName=RandomStringUtils.randomAlphabetic(8);
		txtbox_UserName.sendKeys(UserName);
	}
	
	
	public void selectWorkGroup() {
		btn_SelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		selectFirstworkGroup.click();
		btn_OkWorkgroupSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void selectWorkGroup(String workGroupToSelect) throws InterruptedException {
		this.btn_SelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		new MobileUsers_WorkGroupSelectionPage().SelectSingleworkGroupForMobileUser(workGroupToSelect);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSaveButton() {
		btn_Save.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public String getUserCode() {
		return UserCode;
	}
	
	public String getUserName() {
		return UserName;
	}
	
	private String selectedWorkGroup;
	public String getWorkGroup() {
		selectedWorkGroup=selectFirstworkGroup.getText();
		return selectedWorkGroup;
	}
	
	public void clickWorkGroupSelectButton() {
		btn_SelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyVisibleWorkGroups() {
		List<WebElement> visibleWorkGroups=driver.findElements(By.xpath("//div[@id='SingleWorkgroupSelectionFormUserAdmin:workgroupSelectionSingle']/div[2]/div[2]/div/ul/li/span/span[3]"));
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

