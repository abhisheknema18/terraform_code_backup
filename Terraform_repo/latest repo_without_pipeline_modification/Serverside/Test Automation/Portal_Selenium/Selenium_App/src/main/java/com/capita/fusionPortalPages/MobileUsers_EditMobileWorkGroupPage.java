package com.capita.fusionPortalPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;
import utility.CommonUtils;

public class MobileUsers_EditMobileWorkGroupPage extends Basepage{
	public MobileUsers_EditMobileWorkGroupPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(id = "editMobWorkGroupFrom:mobileWGEdit")
	WebElement EditMobWorkGroupPage;
	
	@FindBy(xpath = "//button[contains(@id,'editMobWorkGroupFrom')]/span[text()='Delete']")
	WebElement btn_Delete;
	
	@FindBy(xpath = "//button[contains(@id,'editMobWorkGroupFrom')]/span[text()='Cancel']")
	WebElement btn_Cancel;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);

	public Boolean VerifyLabelDisplay(String lblToVerify) {
		try {
			return new CommonUtils().singleElementToReturn(this.EditMobWorkGroupPage.findElements(By.tagName("label")), lblToVerify).isDisplayed();
		}
		catch (Exception e){
			return false;
		}
	}

	public void clickDeleteButton() {
		btn_Delete.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickCancelButton() {
		btn_Cancel.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyDeleteValidationForAssociatedAccessGroups() {
		try {
			if(driver.findElement(By.xpath("//span[text()='System Access Groups are associated with this Work Group. Unable to delete']")).isDisplayed()) {
				Assert.assertTrue(true);
			}
			else {
				Assert.assertTrue(false);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
