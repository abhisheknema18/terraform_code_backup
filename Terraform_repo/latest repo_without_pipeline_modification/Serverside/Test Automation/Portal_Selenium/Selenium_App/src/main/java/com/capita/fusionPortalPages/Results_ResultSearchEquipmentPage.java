package com.capita.fusionPortalPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import BasePackage.Basepage;

public class Results_ResultSearchEquipmentPage extends Basepage {
	
	public Results_ResultSearchEquipmentPage(){
		PageFactory.initElements(driver, this);
	}
	
	
	
	@FindBy(xpath="//a[text()='Equipment']")
	WebElement equipmentTab;
	
	@FindBy(xpath="//input[@id='resultSearchTabs:searchReferenceForm:equipmentReferenceNumber']")
	WebElement equipmentTextBox;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchReferenceForm:saveBtn']")
	WebElement equipmentSearchButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchReferenceForm:j_idt300']")
	WebElement equipmentCancelButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchReferenceForm:j_idt301']")
	WebElement equipmentResetButton;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[text()='Installed 2014']")
	WebElement equipmentReferenceSearchResult;
	
	@FindBy(xpath="//div[@id='resultSearchTabs:searchReferenceForm:messages']//span[text()='No results found']")
	WebElement equipmentReferenceInvalidMessage;
	
	
	
	public void validEquipmentSearch() {
		logger.info("Click Equipment Tab");
		equipmentTab.click();
		logger.info("Entered Equipment number to search");
		equipmentTextBox.sendKeys("Installed 2014");
		logger.info("Click Search button");
		equipmentSearchButton.click();
		logger.info("Validate the search result");
		Assert.assertTrue(equipmentReferenceSearchResult.isDisplayed());

	}

	public void invalidEquipmentSearch() {
		logger.info("Click Equipment Tab");
		equipmentTab.click();
		logger.info("Enter Invalid equipment number to search");
		equipmentTextBox.sendKeys("PrInstalled 2014");
		logger.info("Click Search button");
		equipmentSearchButton.click();
		logger.info("Verify the Invalid equipment number error message displays");
		Assert.assertTrue(equipmentReferenceInvalidMessage.isDisplayed());

	}
}
