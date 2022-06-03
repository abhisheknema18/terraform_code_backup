package com.capita.fusionPortalPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class MobileUsers_MobileSearchResultPage extends Basepage{
	public MobileUsers_MobileSearchResultPage(){
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath="//a[@id='adminSearchTabs:userAdminSearchListForm:userList:0:editUser']/i[contains(@class,'fa-edit')]")
	WebElement EditMobileUser;
	
	@FindBy(id="addUserForm:editWorkgroups")
	WebElement btn_SelectWorkGroup;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//button[contains(@id, 'addUserForm')]//span[contains(text(), 'Save')]")
	WebElement btn_saveMobileUser;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void clickEditMobileUser() {
		//wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		this.EditMobileUser.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickBtn_SelectWorkGroup() {
		this.btn_SelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSaveBtn() {
		this.btn_saveMobileUser.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
