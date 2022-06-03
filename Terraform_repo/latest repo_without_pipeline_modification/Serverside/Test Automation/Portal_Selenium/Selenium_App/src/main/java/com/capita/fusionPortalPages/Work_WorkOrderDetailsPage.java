package com.capita.fusionPortalPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class Work_WorkOrderDetailsPage extends Basepage{
	public Work_WorkOrderDetailsPage () {
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath="//table[@id='sideMenuForm:leftMenu']//a[contains(@onclick,'editWorkForm')]")
	WebElement EditWorkFormBtn;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(id="editWorkForm:showUsers")
	WebElement assignWorkOrderBtn;
	
	@FindBy(id="editWorkForm:saveWorkBtn")
	WebElement saveBtn;

	@FindBy(id="menuForm:menuHomeBtn")
	WebElement HomePageMenuLink;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void clickHomePageMenuLink() {
		this.HomePageMenuLink.click();		
	}

	public void clickEditFormBtn() {
		this.EditWorkFormBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickAssignWorkOrderBtn() {
		this.assignWorkOrderBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickSaveBtn() {
		this.saveBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		try {
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
		finally {
			// Do Nothing
		}
	}
}
