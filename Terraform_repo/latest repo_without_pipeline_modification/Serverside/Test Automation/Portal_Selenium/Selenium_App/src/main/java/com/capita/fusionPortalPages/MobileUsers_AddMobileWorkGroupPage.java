package com.capita.fusionPortalPages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;
import utility.CommonUtils;

public class MobileUsers_AddMobileWorkGroupPage extends Basepage {
	public MobileUsers_AddMobileWorkGroupPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='Mobile Users']")
	WebElement Menu_MobileUsers;
	
	@FindBy(xpath="//a[text()='Add Mobile Work Group']")
	WebElement SubMenu_AddMobileWorkGroups;
	
	@FindBy(xpath="//button[contains(@id,'addMobWorkGroupForm')]/span[text()='Cancel']")
	WebElement btn_Cancel;
	
	@FindBy(xpath="(//input[contains(@id, 'addMobWorkGroupForm')][1]")
	WebElement dropdown_WorkGroupCategory;
	
	@FindBy(xpath="(//input[contains(@id, 'addMobWorkGroupForm')])[2]")
	WebElement WorkGroupCodeInputElement;
	
	@FindBy(xpath="(//input[contains(@id, 'addMobWorkGroupForm')])[3]")
	WebElement WorkGroupDesciptionInputElement;
	
	@FindBy(xpath="//button[contains(@id,'addMobWorkGroupForm')]/span[text()='Save']")
	WebElement btn_Save;
	
	@FindBy(xpath="//label[text()='Work Group Category']/parent::td/following-sibling::td/div[contains(@id, 'addMobWorkGroupForm')]/div[3][@class='ui-selectonemenu-trigger ui-state-default ui-corner-right']")
	WebElement icon_dropdownWorkGroupCategory;
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void navigateToAddMobileWorkGroups() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			
			if(Menu_MobileUsers.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_MobileUsers.click();

				SubMenu_AddMobileWorkGroups.click();
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
	
	public void verifyWorkGroupCategoryDropDownOptions() {
		
//		dropdown_WorkGroupCategory.click();
	
		Select WorkGroupCategoryDropDown=new Select(dropdown_WorkGroupCategory);
		
	
		List<WebElement> WorkGroupCategoryOptions=WorkGroupCategoryDropDown.getOptions();
		
		List<String> WorkGroupCategoryOptionsText=new ArrayList<String>();
		
		
		//getText method returned null, hence getattribute(textContect) method is used
		for(WebElement option:WorkGroupCategoryOptions) {
			WorkGroupCategoryOptionsText.add(option.getAttribute("textContent"));
		}
		
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String WorkGroupCategory=Basepage.readProperty("WorkGroupCategory");
		//verifying all the records belong to a workgroup assgned to the user
		for (String temp : WorkGroupCategoryOptionsText) {
			Assert.assertTrue(temp.contains(WorkGroupCategory));
		}
		
	}
	
	public void clickCancelButton() {
		btn_Cancel.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void EnterWorkGroupCode(String wrkGrpCodeVal) throws InterruptedException {
		new CommonUtils().enterText(this.WorkGroupCodeInputElement, wrkGrpCodeVal);
	}
	
	public void EnterWorkGroupDescription(String wrkGrpDescVal) throws InterruptedException {
		new CommonUtils().enterText(this.WorkGroupDesciptionInputElement, wrkGrpDescVal);
	}
	
	public void ClickSaveButton() {
		this.btn_Save.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void selectWorkGroupCategory(String WGCName) {
		//click drop down work group category
		icon_dropdownWorkGroupCategory.click();
		
		//select the work group category passed by parameter WGCName
		driver.findElement(By.xpath("//ul[contains(@id,'addMobWorkGroupForm')]/li[text()='"+WGCName+"']"));
	}
	
}
