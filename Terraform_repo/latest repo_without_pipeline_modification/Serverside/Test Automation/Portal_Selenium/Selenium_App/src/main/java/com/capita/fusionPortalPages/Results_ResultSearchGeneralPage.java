package com.capita.fusionPortalPages;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import BasePackage.Basepage;
import utility.CommonUtils;

public class Results_ResultSearchGeneralPage extends Basepage {
	
	public Results_ResultSearchGeneralPage() {
		PageFactory.initElements(driver, this);
	}
	
	
	@FindBy(xpath="//span[text()='Results']")
	WebElement resultsTab;
	
	@FindBy(xpath="//span[text()='Results']/ancestor::li/ul/li/a[@id='menuForm:viewResultsBtn']")
	WebElement searchMenuOption;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:saveBtn']/span[text()='Search']")
	WebElement searchButton;
//	---------------------------------------------------------------------------------
	//Radio buttons
	@FindBy(xpath="//table[@id='resultSearchTabs:searchForm:allScriptsSelected']//span[@class='ui-radiobutton-icon ui-icon ui-icon-bullet ui-c']")
	WebElement radioButtonSelectedScriptTypes;
	
	@FindBy(xpath="//table[@id='resultSearchTabs:searchForm:allWorkgroupsSelected']//span[@class='ui-radiobutton-icon ui-icon ui-icon-blank ui-c']")
	WebElement radioButtonSelectedWorkGroups;
	
	@FindBy(xpath="//table[@id='resultSearchTabs:searchForm:allUsersSelected']//span[@class='ui-radiobutton-icon ui-icon ui-icon-bullet ui-c']")
	WebElement radioButtonSelectedUsers;
	
	@FindBy(xpath="//table[@id='resultSearchTabs:searchForm:allResultsSelected']//span[@class='ui-radiobutton-icon ui-icon ui-icon-blank ui-c']")
	WebElement radioButtonSelectedStatuses;
	
	
//	---------------------------------------------------------------------------------
	//Select buttons
	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:showScripts']/span[text()='Select']")
	WebElement scriptTypesSelectButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:showWorkgroups']/span[text()='Select']")
	WebElement workGroupsSelectButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:showUsers']/span[text()='Select']")
	WebElement usersSelectButton;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:showResults']/span[text()='Select']")
	WebElement scriptResultStatusSelectButton;
	
	
//	---------------------------------------------------------------------------------
	//CHeck Boxes
	@FindBy(xpath="//span[text()='AMT-SYBEX']/ancestor::span/following-sibling::ul/li/span/span[text()='Geofield Test']")
	WebElement scriptSelectionCheckBoxGeofieldTest;
	
	@FindBy(xpath="//span[text()='AddOns Conference 2019']/ancestor::span/following-sibling::ul/li/span/span[text()='Andy Clark']")
	WebElement userSelectionCheckBoxAndyClarke;
	
	@FindBy(xpath="//span[text()='ACCEPTED']")
	WebElement scripStatusSelectionCheckBoxAccepted;
	
	@FindBy(xpath="//span[text()='UK IMPORT']/ancestor::span/following-sibling::ul/li/span/span[text()='TMP - Cant Do']")
	WebElement scriptTypeCheckBoxTMP;
	
	@FindBy(xpath="//span[text()='ERG']")
	WebElement workGroupCheckBoxERG;
	
	@FindBy(xpath="//span[text()='Andy Clark (ERG)']")
	WebElement userCheckBoxAndyClarkERG;
	
	@FindBy(xpath="//span[text()='UNCHECKED']")
	WebElement resultStatusCheckBoxUnchecked;
	
	@FindBy(xpath="//li[@id='resultWorkgroupSelectionForm:workgroupSelectTree:3_0']//span[text()='Fusion Project']")
	WebElement workgroupCheckBoxFusionProject;
	
//	---------------------------------------------------------------------------------
	//Search Result Column Values
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[text()='Geofield Test']")
	WebElement searchResultScriptDescriptionColumnValue;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[text()='fusion project']")
	WebElement searchResultWorkgroupColumnValue;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[text()='Andy Clark']")
	WebElement searchResultScriptDescriptionColumnValueByUser;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[text()='ACCEPTED']")
	WebElement searchResultStatusColumnValue;
	
//	---------------------------------------------------------------------------------
	
//	Ok Button upon selection is made
	@FindBy(xpath="//div[@id='scriptSelectionForm:scriptSelectionP_footer']/button/span[text()='OK']")
	WebElement scriptSelectionOkButton;
	
	@FindBy(xpath="//div[@id='resultWorkgroupSelectionForm:userSelectionP_footer']/button/span[text()='OK']")
	WebElement workGroupSelectionOkButton;
	
	@FindBy(xpath="//div[@id='resultUserSelectionForm:userSelectionP_footer']/button/span[text()='OK']")
	WebElement usersSelectionOkButton;
	
	@FindBy(xpath="//div[@id='resultStatusSelectionForm:statusSelectionP_footer']/button/span[text()='OK']")
	WebElement scriptStatusSelectionOkButton;
	
	@FindBy(xpath="//button[@id='resultWorkgroupSelectionForm:j_idt436']/span[text()='OK']")
	WebElement workgroupSelectionOkButton;
	
	
//	---------------------------------------------------------------------------------

	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:resetBtn']/span[text()='Reset']")
	WebElement searchResultResetButton;
	
	@FindBy(xpath="//table[@id='resultSearchTabs:searchForm:scriptSelect']//ul/li")
	WebElement listOfSelectedScriptTypes;
//	---------------------------------------------------------------------------------
	
	@FindBy(xpath="//input[@id='resultSearchTabs:searchForm:fromDate_input']")
	WebElement fromDateField;
	
	@FindBy(xpath="//input[@id='resultSearchTabs:searchForm:toDate_input']")
	WebElement toDateField;
	
	@FindBy(xpath="//span[contains(text(),'results found')]")
	WebElement searchResultFoundLabel;
	
//	---------------------------------------------------------------------------------
	//when no records found in search result
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[1]")
	WebElement noRecordsFound;

	
	public void navigateToResultSearch() throws InterruptedException {
		logger.info("Click results option tab");
		resultsTab.click();
		TimeUnit.SECONDS.sleep(3);
		logger.info("click Search menu option");
		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(3);

	}
	
	public void scriptResultSearch() throws InterruptedException {
		TimeUnit.SECONDS.sleep(10);
		logger.info("Click results option tab");
		resultsTab.click();
		TimeUnit.SECONDS.sleep(3);
		logger.info("click Search menu option");
		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(3);
		logger.info("click Search button");
		searchButton.click();
		TimeUnit.SECONDS.sleep(10);
	}
	
	public void scriptResultSearchByScriptTypes(String scriptType) throws InterruptedException {	
		TimeUnit.SECONDS.sleep(10);
		resultsTab.click();
		TimeUnit.SECONDS.sleep(6);
		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(6);
		searchResultResetButton.click();
		logger.info("click Reset button");
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedScriptTypes.click();
		logger.info("Select script type radio button");
		TimeUnit.SECONDS.sleep(3);
		scriptTypesSelectButton.click();
		logger.info("click Select script type button");
		TimeUnit.SECONDS.sleep(3);
		
		//Select scrip dynamically as per the parameter
		WebElement scriptTypeCheckBx=driver.findElement(By.xpath("//div[@id='scriptSelectionForm:scriptSelectionTree']//span[text()='"+scriptType+"']"));
		scriptTypeCheckBx.click();
		
		logger.info("Select the script checkbox");
		TimeUnit.SECONDS.sleep(3);
		scriptSelectionOkButton.click();
		logger.info("click Ok button after scrupt selection");
		TimeUnit.SECONDS.sleep(3);
		searchButton.click();
		logger.info("click Search button");
		TimeUnit.SECONDS.sleep(3);
		
		String noRecords=noRecordsFound.getText();
		//Checking if there are any records for selected script code
		if(!noRecords.equalsIgnoreCase("No records found.")) {
			logger.info("Records displayed as per the selected script type");
		}
		else {
			logger.info("No Records available as per the selected script type");
		}
		
				
	}
	
	public void scriptResultSearchByWorkGroups() throws InterruptedException {
		resultsTab.click();
		TimeUnit.SECONDS.sleep(3);
		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(3);	
		searchResultResetButton.click();
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedWorkGroups.click();
		logger.info("Select Work Group Radio button");
		TimeUnit.SECONDS.sleep(3);
		workGroupsSelectButton.click();
		logger.info("Click Select button");
		TimeUnit.SECONDS.sleep(3);
		workgroupCheckBoxFusionProject.click();
		logger.info("Select workgroup checkbox");
		TimeUnit.SECONDS.sleep(3);
		workgroupSelectionOkButton.click();
		logger.info("click ok button after workgroup selection");
		TimeUnit.SECONDS.sleep(3);

		
		
		List<WebElement> scriptList=driver.findElements(By.xpath("//div[@id='resultSearchTabs:searchForm:j_idt242']//span[text()='Fusion Project']"));
		for(WebElement script:scriptList) {
			Assert.assertEquals(script.getText(), "Geofield Test");
		}
		searchButton.click();
		logger.info("click Search button");
		TimeUnit.SECONDS.sleep(3);
		Assert.assertEquals(searchResultWorkgroupColumnValue.getText(), "fusion project", "Script Searched with respect to script type"+searchResultWorkgroupColumnValue.getText());
		logger.info("Validate the search with selected Work Group");
				
	}
	
	public void scriptResultSearchByUsers() throws InterruptedException {
		resultsTab.click();
		TimeUnit.SECONDS.sleep(3);
		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(3);	
		searchResultResetButton.click();
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedUsers.click();
		TimeUnit.SECONDS.sleep(3);
		usersSelectButton.click();
		TimeUnit.SECONDS.sleep(3);		
		userSelectionCheckBoxAndyClarke.click();
		TimeUnit.SECONDS.sleep(3);
		usersSelectionOkButton.click();
		TimeUnit.SECONDS.sleep(3);
		
		List<WebElement> userList=driver.findElements(By.xpath("//table[@id='resultSearchTabs:searchForm:userSelect']//ul/li/span"));
		for(WebElement user:userList) {
			Assert.assertEquals(user.getText(), "Andy Clark");
		}
		searchButton.click();
		TimeUnit.SECONDS.sleep(3);
		Assert.assertEquals("Andy Clark", searchResultScriptDescriptionColumnValueByUser.getText(), "Script Searched with respect to Selected USer"+searchResultScriptDescriptionColumnValueByUser.getText());
				
	}
	
	public void scriptResultSearchByResultStatus() throws InterruptedException {
		resultsTab.click();
		TimeUnit.SECONDS.sleep(3);

		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(3);

		searchResultResetButton.click();
		TimeUnit.SECONDS.sleep(3);

		radioButtonSelectedStatuses.click();
		TimeUnit.SECONDS.sleep(3);
		
		scriptResultStatusSelectButton.click();
		TimeUnit.SECONDS.sleep(3);
		
		scripStatusSelectionCheckBoxAccepted.click();
		TimeUnit.SECONDS.sleep(3);
		
		scriptStatusSelectionOkButton.click();
		TimeUnit.SECONDS.sleep(3);
		
		
		List<WebElement> scriptResultStatusList=driver.findElements(By.xpath("//table[@id='resultSearchTabs:searchForm:resultSelect']//ul/li/span"));
		for(WebElement status:scriptResultStatusList) {
			Assert.assertEquals(status.getText(), "ACCEPTED");
		}
		searchButton.click();
		TimeUnit.SECONDS.sleep(3);
		
		Assert.assertEquals("ACCEPTED", searchResultStatusColumnValue.getText(), "Script Searched with respect to Selected Status"+searchResultStatusColumnValue.getText());
				
		
	}
	
	public void scriptResultSearchByAllFields() throws InterruptedException {
		resultsTab.click();
		TimeUnit.SECONDS.sleep(3);
		searchMenuOption.click();
		TimeUnit.SECONDS.sleep(3);	
		searchResultResetButton.click();
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedScriptTypes.click();
		TimeUnit.SECONDS.sleep(3);
		scriptTypesSelectButton.click();
		TimeUnit.SECONDS.sleep(3);
		scriptTypeCheckBoxTMP.click();
		TimeUnit.SECONDS.sleep(3);
		scriptSelectionOkButton.click();
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedWorkGroups.click();
		TimeUnit.SECONDS.sleep(3);
		workGroupsSelectButton.click();
		TimeUnit.SECONDS.sleep(3);
		workGroupCheckBoxERG.click();
		TimeUnit.SECONDS.sleep(3);
		workGroupSelectionOkButton.click();
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedUsers.click();
		TimeUnit.SECONDS.sleep(3);
		usersSelectButton.click();
		TimeUnit.SECONDS.sleep(3);
		userCheckBoxAndyClarkERG.click();
		TimeUnit.SECONDS.sleep(3);
		usersSelectionOkButton.click();
		TimeUnit.SECONDS.sleep(3);
		radioButtonSelectedStatuses.click();
		TimeUnit.SECONDS.sleep(3);
		scriptResultStatusSelectButton.click();
		TimeUnit.SECONDS.sleep(3);
		resultStatusCheckBoxUnchecked.click();
		TimeUnit.SECONDS.sleep(3);
		scriptStatusSelectionOkButton.click();
		TimeUnit.SECONDS.sleep(3);
		fromDateField.sendKeys("03/27/2019");
		TimeUnit.SECONDS.sleep(3);
		toDateField.sendKeys("03/27/2019");
		TimeUnit.SECONDS.sleep(3);
		searchButton.click();
		TimeUnit.SECONDS.sleep(3);
		Assert.assertEquals(searchResultFoundLabel.isDisplayed(), true);

		
	}
	

}
