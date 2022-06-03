package com.capita.fusionPortalPages;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class MobileUsers_ManageMobileWorkGroupCategoryPage extends Basepage {
	
	public MobileUsers_ManageMobileWorkGroupCategoryPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//thead[@id='mobileWorkGroupCategoryForm:workGroupCatTable_head']//a")
	WebElement icon_AddWorkGroupCategory;
	
	@FindBy(id="addMobileWorkGroupCategoryForm:wgCatId")
	WebElement txtBox_WorkGroupCategoryName;
	
	@FindBy(xpath="//button[contains(@id,'addMobileWorkGroupCategoryForm')]/span[text()='Save']")
	WebElement btn_Save;
	
	@FindBy(xpath="//button[contains(@id,'addMobileWorkGroupCategoryForm')]/span[text()='Cancel']")
	WebElement btn_Cancel;
	
	@FindBy(xpath="//div[@id='mobileWorkGroupCategoryForm:WGCatPanel_content']//span[@class='fa fa-angle-right']")
	WebElement icon_CloseSlideIn;

	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void clickAddWorkGroupCategoryIcon() {
		icon_AddWorkGroupCategory.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	private String workGroupCategoryName;
	
	public void setWorkGroupCategoryName(String WGCName) {
		workGroupCategoryName=WGCName;
	}
	
	public String getWorkGroupCategoryName() {
		return workGroupCategoryName;
	}
	
	public void enterWorkGroupCategoryName(String WGCName) {
		workGroupCategoryName=WGCName;
		txtBox_WorkGroupCategoryName.sendKeys(workGroupCategoryName);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSaveButton() {
		btn_Save.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void closeSlideIn() {
		icon_CloseSlideIn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickDeleteIcon(String WGCName) {
		driver.findElement(By.xpath("//td[text()='"+WGCName+"']/following-sibling::td//a[2]")).click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyValidationForAssociatedSystemAccessGroups() {
		try {
			if(driver.findElement(By.xpath("//span[text()='System Access Groups are associated with this Category. Unable to delete.']")).isDisplayed()) {
				Assert.assertTrue(true);
			}
			else {
				Assert.assertTrue(false);
			}
			
		} catch (Exception e) {
			e.getMessage();
		}
		
	}
	

}
