package com.capita.fusionPortalPages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;
import utility.CommonUtils;

public class Mobile_WorkGroupSearchPage extends Basepage{
	public Mobile_WorkGroupSearchPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(css="th[aria-label^='Work Group Category']")
	WebElement WorkGroupCategoryElement;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//a[@id='mobileWGSearchTabs:mobileWGSearchListForm:workGroupList:0:editMobWG']//i[contains(@class, \"fa-edit\")]")
	WebElement EditMobileWorkGroupBtn;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void EnterWorkGroupCategoryDetail(String textToEnter) throws InterruptedException {
		new CommonUtils().enterText(this.WorkGroupCategoryElement.findElement(By.tagName("input")), textToEnter);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	///Click the Edit button based on work group number
	public void clickEditMobileWorkGroupBtn() {
		//wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		this.EditMobileWorkGroupBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
