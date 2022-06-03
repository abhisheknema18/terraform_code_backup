package com.capita.fusionPortalPages;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;

public class Work_WorkOrderSearchPage extends Basepage{
	public Work_WorkOrderSearchPage () {
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath="//div[@id='workSearchTabs:searchForm:statusSelect']//span")
	List<WebElement> workStatusElement;
	
	@FindBy(id="workSearchTabs:searchForm:searchWO")
	WebElement workOrderSearchBtn;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//a[contains(@id,'workDetailViewBtn')]")
	List<WebElement> workDetailsViewBtnList;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void clickClosedCancelledWork() {
		if(this.workStatusElement.get(1).getAttribute("className").contains("ui-icon-blank")) {
			this.workStatusElement.get(1).click();
		}
	}
	
	public void clickworkOrderSearchBtn() {
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		this.workOrderSearchBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickViewDetailsBtn() {
		this.workDetailsViewBtnList.get(0).click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
