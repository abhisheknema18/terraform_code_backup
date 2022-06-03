package com.capita.fusionPortalPages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import BasePackage.Basepage;

public class Results_ResultSearchUniqueReferencePage extends Basepage{
	public Results_ResultSearchUniqueReferencePage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//a[text()='Unique Reference']")
	WebElement uniqueReferenceTab;
	
	@FindBy(xpath="//input[@id='resultSearchTabs:searchUniqueReferenceForm:returnIdReferenceNumber']")
	WebElement uniqueReferenceTextBox;

	@FindBy(xpath="//button[@id='resultSearchTabs:searchUniqueReferenceForm:saveBtn']/span[text()='Search']")
	WebElement uniqueReferenceSearchButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchUniqueReferenceForm:j_idt317']/span[text()='Cancel']")
	WebElement uniqueReferenceCancelButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchUniqueReferenceForm:j_idt318']/span[text()='Reset']")
	WebElement uniqueReferenceResetButton;
	
	@FindBy(xpath="//div[@id='resultSearchTabs:searchUniqueReferenceForm:messages']//span[text()='Unique Reference number criteria invalid']")
	WebElement uniqueReferenceInvalidSearchValidationMessage;
	
	@FindBy(xpath="//div[@id='resultSearchTabs:searchUniqueReferenceForm:messages']//span[text()='Enter numeric value only']")
	WebElement uniqueReferenceOnlyNumericValueValidationMessage;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr/td/a[@id='searchTabs:searchForm:resultsTable:0:resultDetailedViewBtn']/i[@class='fa fa-external-link']")
	WebElement detailedViewIconSearchResult;
	
	@FindBy(xpath="//div[@id='detailsForm:returnIdPanel_content']//span")
	WebElement uniqueReferenceNumberOnSearchResultSetDetailsScreen;	
	
	
	public void invalidUniqueReferenceSearch() {
		uniqueReferenceTab.click();
		logger.info("Switch to Unique Reference Tab");
		uniqueReferenceSearchButton.click();
		logger.info("Click Unique Reference Search Button");
		Assert.assertEquals(uniqueReferenceInvalidSearchValidationMessage.getText(),
				"Unique Reference number criteria invalid");
		logger.info("Validate the message for invalid search");
	}

	public void invalidSearchInput() {
		uniqueReferenceTab.click();
		logger.info("Switch to Unique Reference Tab");
		uniqueReferenceTextBox.sendKeys("TEST");
		logger.info("Enter Invalid the Unique Reference to search");
		uniqueReferenceSearchButton.click();
		logger.info("Click Unique Reference Search Button");
		Assert.assertTrue(false);
		Assert.assertEquals(uniqueReferenceOnlyNumericValueValidationMessage.getText(), "Enter numeric value only");
		logger.info("Validate the error message for invalid unique reference");
	}

	public void validUniqueReferenceSearch(String uniqueRef) {
		String uniqueReferenceNumber = uniqueRef;
		uniqueReferenceTab.click();
		logger.info("Switch to Unique Reference Tab");
		uniqueReferenceTextBox.sendKeys(uniqueReferenceNumber);
		logger.info("Enter valid the Unique Reference to search");
		uniqueReferenceSearchButton.click();
		logger.info("Click Unique Reference Search Button");
		detailedViewIconSearchResult.click();
		logger.info("Click Detailed view icon on the Search results Screen");
		Assert.assertEquals(uniqueReferenceNumberOnSearchResultSetDetailsScreen.getText(), uniqueReferenceNumber);
		logger.info("Validate the unique reference");

	}
	
}
