package com.capita.fusionPortalPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class Work_WorkAssignment extends Basepage{
	public Work_WorkAssignment () {
		PageFactory.initElements(driver, this);
	}

	@FindBy(id="searchTab:workListResultsForm:multiWorkListTable_data")
	WebElement workListResultTableElement;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(css="a[onclick^='assignWorkForm']")
	WebElement assignWorkBtn;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void selectWorkOrder(int countOfWorkOrdersToSelect) {
		for(int i = 0; i < countOfWorkOrdersToSelect; i++) {
			this.workListResultTableElement.findElements(By.cssSelector("data-ri")).get(i).click();
			this.wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
	}
	
	public void clickAssignWorkBtn() {
		this.assignWorkBtn.click();
		this.wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
