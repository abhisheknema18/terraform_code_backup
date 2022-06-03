package com.capita.fusionPortalPages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class SystemAccess_SearchAccessGroupPage extends Basepage {

	public SystemAccess_SearchAccessGroupPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement Tab_admin;
	
	@FindBy(xpath="//a[text()='System Access']")
	WebElement Menu_SystemAccess;
	
	@FindBy(xpath="//a[text()='View All Access Groups']")
	WebElement SubMenu_ViewAllAccessGroups;
	
	@FindBy(xpath="//a[text()='Search Access Groups']")
	WebElement SubMenu_SearchAccessGroups;
	
	
	@FindBy(xpath="//td[text()='FDMPRIMARY']/following::td[3]/a[contains(@id,'editAccessGroup')][1]")
	WebElement Icon_EditAccessGroup;
	
	@FindBy(xpath="//li/a[text()='Edit Results']")
	WebElement Tab_EditResults;
	
	@FindBy(xpath="//label[text()='Edit Accessible Results Only']")
	WebElement RdoBtn_EditAccessibleResultsOnly;
	
	@FindBy(xpath="//label[text()='Edit Own Results Only']")
	WebElement RdoBtn_EditOwnResultsOnly;
	
	@FindBy(xpath="//button[contains(@id,'saveAccessGroupForm')]/span[text()='Save']")
	WebElement btn_Save;
	
	@FindBy(xpath="//input[@id='accessGroupSearchForm:accessGroupSearchTabPanel:accessGroupCode']")
	WebElement txtBox_SearchAccessGroupCode;
	
	@FindBy(xpath="//button[contains(@id,'accessGroupSearchForm')]/span[text()='Reset']")
	WebElement btn_Reset;
	
	@FindBy(xpath="//button[contains(@id,'accessGroupSearchForm')]/span[text()='Search']")
	WebElement btn_Search;
	
	@FindBy(xpath="//tbody[@id='accessSearchTabs:accessGroupSearchListForm:accessGroupList_data']/tr/td[5]/a")
	WebElement btn_EditFirstAccessGroup;
	
	@FindBy(xpath="//a[text()='Work Groups']")
	WebElement tab_WorkGroups;
	
	@FindBy(xpath="//div[@id='manageAccessGroupTabPanel:accessGroupWorkGroups:wgCatSelected']/div[2]")
	WebElement chkBox_WorkGroupAdministration;
	
	@FindBy(xpath="//button[@id='manageAccessGroupTabPanel:accessGroupWorkGroups:showworkGroupStatus']/span")
	WebElement btn_SelectWorkGroupCategories;
	
	@FindBy(xpath="//button[contains(@id,'workgroupCatSelectionForm')]/span[text()='OK']")
	WebElement btn_OKWorkGroupCategorySelection;
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToVIewAllAccessGroups() throws InterruptedException {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
//			TimeUnit.SECONDS.sleep(3);
			
			if(Menu_SystemAccess.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_SystemAccess.click();
//				TimeUnit.SECONDS.sleep(3);
				SubMenu_ViewAllAccessGroups.click();
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
	
	public void ClickEditAccessGroupIcon(){
		Icon_EditAccessGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Edit Access group icon");
	}
	
	public void ClickEditResultsTab(){
		Tab_EditResults.click();
		logger.info("Clicked tab edit results");
	}
	
	public void SelectRadioButtonEditAccessibleResultsOnly(){
		RdoBtn_EditAccessibleResultsOnly.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Selected Radio button EditAccessibleResultsOnly");
	}
	
	public void SelectRadioButtonEditOwnResultsOnly(){
		RdoBtn_EditOwnResultsOnly.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Selected Radio button EditOwnResultsOnly");
	}
	
	public void ClickSave(){
		btn_Save.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Save button");
//		wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//div[@role='alert']"))));
	}
	
	public void clickSearchSystemAccessGroup() {
		if(Tab_admin.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_admin.click();
//			TimeUnit.SECONDS.sleep(3);
			
			if(Menu_SystemAccess.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_SystemAccess.click();
//				TimeUnit.SECONDS.sleep(3);
				SubMenu_SearchAccessGroups.click();
				wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
				logger.info("Clicked Search System Access Group SubMenu");
			}
		}
	}
	
	public void enterAccessGroupCodeForSearch(String accessgroupcode) {
		
		btn_Reset.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		txtBox_SearchAccessGroupCode.sendKeys(accessgroupcode);
		logger.info("entered access group code");
	}
	
	public void clickAccessGroupSearchButton() {
		
		btn_Search.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Search System Access Group SubMenu");
	}
	
	public void clickEditAccessGroup(){
		btn_EditFirstAccessGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Edit System Access Group");
	}
	
	public void selectTabWorkGroups() {
		tab_WorkGroups.click();
	}
	
	public void selectChkBoxWorkGroupAdmin() {
		if(driver.findElement(By.xpath("//button[@id='manageAccessGroupTabPanel:accessGroupWorkGroups:showworkGroupStatus']")).getAttribute("aria-disabled").equalsIgnoreCase("false")) {
			
			chkBox_WorkGroupAdministration.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			
			chkBox_WorkGroupAdministration.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
		else {
			chkBox_WorkGroupAdministration.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
		
		
	}
	
	public void deSelectChkBoxWorkGroupAdmin() {
		if (driver.findElement(By.xpath("//button[@id='manageAccessGroupTabPanel:accessGroupWorkGroups:showworkGroupStatus']"))
				.getAttribute("aria-disabled").equalsIgnoreCase("false")){

			chkBox_WorkGroupAdministration.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));

		} 
		
	}
	
	public void clickWorkGroupCategorySelectButton() {
		btn_SelectWorkGroupCategories.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void SelectWorkGroupCategoryCheckBox() {
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//config//data.properties");
		driver.findElement(By.xpath("//li[text()='"+Basepage.readProperty("WorkGroupCategory")+"']")).click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void ClickOkButton() {
		btn_OKWorkGroupCategorySelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
}

