package com.capita.fusionPortalPages;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class SystemUsers_AddSystemUserPage extends Basepage{
	
	public SystemUsers_AddSystemUserPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='System Users']")
	WebElement Menu_SystemUsers;
	
	@FindBy(xpath="//a[text()='Add System User']")
	WebElement SubMenu_AddSystemUser;
	
	@FindBy(xpath="//input[@id='addSystemUserForm:userCodeAdd']")
	WebElement txtBox_UserCode;
	
	@FindBy(xpath="//input[@id='addSystemUserForm:userName']")
	WebElement txtBox_UserName;
	
	@FindBy(xpath="//div[@id='addSystemUserForm:adminUser']/div/span")
	WebElement chkBox_Admin;
	
	
	
	
WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToAddSystemUser() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
//			TimeUnit.SECONDS.sleep(3);
			
			if(Menu_SystemUsers.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_SystemUsers.click();
//				TimeUnit.SECONDS.sleep(3);
				SubMenu_AddSystemUser.click();
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
		txtBox_UserCode.clear();
		UserCode=RandomStringUtils.randomAlphabetic(8);
		txtBox_UserCode.sendKeys(UserCode);
	}
	
	public void enterUserName() {
		txtBox_UserName.clear();
		UserName=RandomStringUtils.randomAlphabetic(8);
		txtBox_UserName.sendKeys(UserName);
	}
	
	public void selectAdminCheckBox() {		
			chkBox_Admin.click();
		
	}
	
	public String getUserCode() {
		return this.UserCode;
	}
	
	public String getUserName() {
		return this.UserName;
	}
}
