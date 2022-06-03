package com.capita.fusionPortalPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class SystemAccess_AddAccessGroupsPage extends Basepage{
	public SystemAccess_AddAccessGroupsPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//table[contains(@id,'accessGroupAddForm')]//label[text()='Access Group Code']/following::td[1]/input")
	WebElement txtBox_AccessGroupCode;
	
	@FindBy(xpath="//table[contains(@id,'accessGroupAddForm')]//label[text()='Access Group Description']/following::td[1]/input")
	WebElement txtBox_AccessGroupDescription;
	
	@FindBy(xpath="//a[text()='Work Groups']")
	WebElement tab_WorkGroups;
	
	@FindBy(xpath="//a[text()='Access']")
	WebElement tab_Access;
	
	@FindBy(xpath="//div[@id='manageAccessGroupTabPanel:accessGroupWorkGroups:wgCatSelected']/div[2]")
	WebElement chkBox_WorkGroupAdministration;
	
	@FindBy(xpath="//button[@id='manageAccessGroupTabPanel:accessGroupWorkGroups:showworkGroupStatus']/span")
	WebElement btn_Select;
	
	@FindBy(xpath="//button[contains(@id,'workgroupCatSelectionForm')]/span[text()='Clear']")
	WebElement btn_ClearWGCSelection;
	
	@FindBy(xpath="//button[contains(@id,'workgroupCatSelectionForm')]/span[text()='OK']")
	WebElement btn_OKWGCSelection;
	
	@FindBy(xpath="//button[contains(@id,'workgroupCatSelectionForm')]/span[text()='Cancel']")
	WebElement btn_CancelWGCSelection;
	
	@FindBy(xpath="//button[contains(@id,'saveAccessGroupForm')]/span[text()='Cancel']")
	WebElement btn_CancelWorkGroupsTab;
	
	@FindBy(xpath="//button[contains(@id,'saveAccessGroupForm')]/span[text()='Save']")
	WebElement btn_SaveWorkGroupsTab;
	
	@FindBy(xpath="//button[contains(@id,'accessWorkgroupSelectionForm')]/span[text()='OK']")
	WebElement btn_OKAccessTabWGSelection;
	
	@FindBy(xpath="//button[contains(@id,'accessWorkgroupSelectionForm')]/span[text()='Clear']")
	WebElement btn_ClearAccessTabWGSelection;
	
	
	@FindBy(xpath="//button[@id='manageAccessGroupTabPanel:accessGroupAccessForm:showWorkgroups']/span[text()='Select']")
	WebElement btn_AccessTabWorkGroupSelect;
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	String AccessGroupCodeName, AccessGroupCodeDescription;
	
	
	public void enterAccessGroupCode(String AGCName) {
		AccessGroupCodeName=AGCName;
		txtBox_AccessGroupCode.sendKeys(AccessGroupCodeName);
	}

	public void enterAccessGroupDescription(String AGCDescription) {
		AccessGroupCodeDescription=AGCDescription;
		txtBox_AccessGroupDescription.sendKeys(AccessGroupCodeDescription);
	}
	
	public void clickTabWorkGroups() {
		tab_WorkGroups.click();
		
	}
	
	public void clickTabAccess() {
		tab_Access.click();
	}
	
	public void selectWorkGroupAdministrationCheckBox() {
		if(chkBox_WorkGroupAdministration.getAttribute("class").contains("active"))
		{
			logger.info("Check Box already checked");
		}
		else {
			chkBox_WorkGroupAdministration.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
		
	}
	
	public void deselectWorkGroupAdministrationCheckBox() {
		if(chkBox_WorkGroupAdministration.getAttribute("class").contains("active"))
		{
			chkBox_WorkGroupAdministration.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			
		}
		else {
			logger.info("Check Box already unchecked");
		}
		
	}

	public void clickSelectButton() {
		if (chkBox_WorkGroupAdministration.getAttribute("class").contains("active")) {
			btn_Select.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
	}
	
	public void selectWorkGroupCategory(String WGC) {
		driver.findElement(By.xpath("//div[@id='workgroupCatSelectionForm:workGroupCatSelector']//li[text()='"+WGC+"']")).click();
	}
	
	public void clickWorkGroupSelectionClearButton() {
		btn_ClearWGCSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickWorkGroupSelectionOkButton() {
		btn_OKWGCSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSaveButton() {
		btn_SaveWorkGroupsTab.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSelectButtonAccessTabWorkGroups() {
		btn_AccessTabWorkGroupSelect.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void selectWorkGroupAccessTab(String WGName) {
		driver.findElement(By.xpath("//span[text()='"+WGName+"']")).click();;
	}
	
	public void clickBtnClearAccessTabWGSelection() {
		btn_ClearAccessTabWGSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickBtnOKAccessTabWGSelection() {
		btn_OKAccessTabWGSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
