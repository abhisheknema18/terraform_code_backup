package com.capita.fusionPortalPages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class MobileUsers_SearchMobileWorkGroupPage extends Basepage{
	public MobileUsers_SearchMobileWorkGroupPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='Mobile Users']")
	WebElement Menu_MobileUsers;
	
	@FindBy(xpath="//a[text()='Search Mobile Work Groups']")
	WebElement SubMenu_SearchMobileWorkGroups;
	
	@FindBy(xpath="//a[text()='Add Mobile Work Group']")
	WebElement SubMenu_AddMobileWorkGroup;
	
	@FindBy(xpath="//button[contains(@id,'mobileWorkGroupSearchForm:searchWorkGroup')]/span[text()='Search']")
	WebElement btn_Search;
	
	@FindBy(xpath="//button[contains(@id,'searchUsers:searchForm:resetBtn')]/span[text()='Reset']")
	WebElement btn_Reset;
	
	@FindBy(id="mobileWorkGroupSearchForm:searchWorkGroup:showWorkgroups")
	WebElement btn_SelectWorkGroup;
	
	@FindBy(id="mobileWorkGroupSearchForm:searchWorkGroup:allWorkgroupsSelected:0")
	WebElement AllWorkGroup;
	
	@FindBy(id="mobileWorkGroupSearchForm:searchWorkGroup:allWorkgroupsSelected:1")
	WebElement SelectedWorkGroup;
	
	@FindBy(id="mobileWorkGroupSearchForm:searchWorkGroup:showWorkgroups")
	WebElement showWorkGroupsSelectButton;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToSearchMobileWorkGroups() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
			
			if(Menu_MobileUsers.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_MobileUsers.click();
				TimeUnit.SECONDS.sleep(1);
				SubMenu_SearchMobileWorkGroups.click();
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
	
	public void clickSearch() {
		btn_Search.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyWorkGroupCategoryColumnValue() {
		List<WebElement> WorkGroups=driver.findElements(By.xpath("//tbody[@id='mobileWGSearchTabs:mobileWGSearchListForm:workGroupList_data']/tr/td[3]"));
		
		List<String> WorkGroupColValues = new ArrayList<String>();
		
		for(WebElement temp:WorkGroups) {
			WorkGroupColValues.add(temp.getText());
		}
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String WorkGroupCategory=Basepage.readProperty("WorkGroupCategory");
		//verifying all the records belong to a workgroup assgned to the user
		for (String temp : WorkGroupColValues) {
			Assert.assertTrue(temp.contains(WorkGroupCategory));
		}
			
	}
	
	public void clickWorkGroups(int index) {
		if(index == 0) {
			this.AllWorkGroup.findElement(By.xpath("..")).findElement(By.xpath("following-sibling::div")).findElement(By.className("ui-radiobutton-icon")).click();
		}
		else {
			this.SelectedWorkGroup.findElement(By.xpath("..")).findElement(By.xpath("following-sibling::div")).findElement(By.className("ui-radiobutton-icon")).click();
		}
		
	}
	
	public void clickShowWorkGroupSelectButton() {
		this.showWorkGroupsSelectButton.findElement(By.className("ui-button-text")).click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void selectWorkGroup(String workGroupToSelect) throws InterruptedException {
		this.btn_SelectWorkGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		new MobileUsers_WorkGroupSelectionPage().EnterWorkGroupNameAndSelect(workGroupToSelect);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickWGEditIcon(String WGName) {
		if(driver.findElement(By.xpath("//tbody[@id='mobileWGSearchTabs:mobileWGSearchListForm:workGroupList_data']/tr/td[1]")).getText().equalsIgnoreCase(WGName)) {
			driver.findElement(By.xpath("//tbody[@id='mobileWGSearchTabs:mobileWGSearchListForm:workGroupList_data']/tr/td[8]/a")).click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
	}
}
