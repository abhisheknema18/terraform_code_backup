package com.capita.fusionPortalPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;
import utility.CommonUtils;

public class MobileUsers_MobileUsersSearchPage extends Basepage{
	public MobileUsers_MobileUsersSearchPage(){
		PageFactory.initElements(driver, this);
	}

	@FindBy(id="searchUsers:searchForm:showWorkgroups")
	WebElement btn_SelectWorkGroup;
	
	@FindBy(id="searchUsers:searchForm:searchAdmin")
	WebElement btn_Search;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(id="searchUsers:searchForm:userName")
	WebElement userNameElement;
	
	@FindBy(xpath="//table[@id='searchUsers:searchForm:allWorkgroupsSelected']//div[contains(@class, 'ui-corner-all')]")
	WebElement allWorkGroups;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void selectWorkGroup(String workGroupToSelect) throws InterruptedException {
		this.btn_SelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		new MobileUsers_WorkGroupSelectionPage().EnterWorkGroupNameAndSelectForMobileUser(workGroupToSelect);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSearch() {
		this.btn_Search.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void enterUserName(String usrName) throws InterruptedException {
		new CommonUtils().enterText(this.userNameElement, usrName);
	}
	
	public void clickAllWorkGroupRadioButton() {
		this.allWorkGroups.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
