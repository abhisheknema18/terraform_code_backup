package com.capita.fusionPortalPages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPath;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import BasePackage.Basepage;
import utility.CommonUtils;

public class Results_ScriptResultSearchPage extends Basepage{
	public Results_ScriptResultSearchPage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//th[@id='searchTabs:searchForm:resultsTable:j_idt925']//span[@class='ui-chkbox-icon ui-icon ui-icon-blank ui-c']")
	WebElement selectAllCheckBox;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]//span[@class='ui-chkbox-icon ui-icon ui-icon-blank ui-c']")
	WebElement firstRowCheckBoxUnchecked;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]//span[@class='ui-chkbox-icon ui-icon ui-c ui-icon-check']")
	WebElement firstRowCheckBoxChecked;
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[10]")
	WebElement firstRowScriptStatus;
	
	@FindBy(xpath="//select[@id='statusUpdateForm:nextStatus_input']")
	WebElement radioButtonNextStatus;
	
	@FindBy(xpath="//button[@id='statusUpdateForm:j_idt1026']/span[text()='OK']")
	WebElement statusUpdateOkButton;
	
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[1]")
	WebElement noRecordsFound;
	
	@FindBy(xpath="//a[text()='Multi View']")
	WebElement multiViewOptionEnabled;
	
	@FindBy(xpath="//span[text()='Multi View']")
	WebElement multiViewOptionDisabled;
	
	
	//List of result rows
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr")
	List<WebElement> resultRows;
	
	//List of same Script COde Records
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr/td[7]")
	List<WebElement> resultColumnScriptCode;
	
	//List of same Script code version records
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr/td[7]/span")
	List<WebElement> resultColumnScriptCodeVersion;
	
	//List of script statuses
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr/td[10]")
	List<WebElement> resultColumnStatus;
	
	
	@FindBy(xpath="//label[@id='detailsForm:resultSetTable:selectAllLabel']")
	WebElement selectedScriptsOnMultiviewScreen;
	
	@FindBy(xpath="//span[text()='Status Update']")
	WebElement statusUpdateDisabled;
	
	@FindBy(xpath="//a[text()='Status Update']")
	WebElement statusUpdateEnabled;
	
	
	//quick view option for specific record hardcoded static xpath
	@FindBy(xpath="//a[@id='searchTabs:searchForm:resultsTable:0:resultQuickViewBtn']/i")
	WebElement quickViewOption;
	
	//Sinature image for script response for specific record hardcoded
	@FindBy(xpath="//div[@id='resultQuickViewForm:resultSetTable:29:j_idt973_content']/img")
	WebElement quickViewScriptResponseSignature;
	
	//SEarch result screen filter input on column Work Order
	@FindBy(xpath="//span[text()='Script Code']/following-sibling::input")
	WebElement searchResultScriptCodeFilterInputBox;
	
	
	
	
	public void multiViewOptionDisabledWhenAllRecordsSelected() throws InterruptedException {
		logger.info("Select all reords");
		selectAllCheckBox.click();
		TimeUnit.SECONDS.sleep(3);
		logger.info("Verify multiview option is disabled");
		Assert.assertTrue(multiViewOptionDisabled.isDisplayed());
		TimeUnit.SECONDS.sleep(3);
		logger.info("Deselect first row reord");
		firstRowCheckBoxChecked.click();
		TimeUnit.SECONDS.sleep(3);
		logger.info("Verify multiview option is disabled");
		Assert.assertTrue(multiViewOptionDisabled.isDisplayed());
	}
	
	public void multiViewOptionDisabledForDifferentScriptVersion(String script1, String script2) throws InterruptedException {
		int s1=0;
		int s2=0;
		
		for (int i = 1; i <= resultRows.size(); i++) {

			String ScriptCode = driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[7]")).getText();
			if (ScriptCode.equalsIgnoreCase(script1) && s1<1) {
				// clicking the rows select checkbox for 1st version of script code
				logger.info("Select a script with first version");
				driver.findElement(
						By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[" +i+ "]/td[1]//span")).click();
				TimeUnit.SECONDS.sleep(3);
				s1++;
			}
			else if(ScriptCode.equalsIgnoreCase(script2) && s2<1) {
				logger.info("Select a script with second version");
				driver.findElement(
						By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[" +i+ "]/td[1]//span")).click();
				TimeUnit.SECONDS.sleep(3);
				s2++;			
			}
			else if(s1>=1 && s2>=1) {
				break;
			}
			
		}			
		//Validating the multi view option After Selecting different script code versions
		logger.info("Verify multiview option is disabled");
		Assert.assertTrue(multiViewOptionDisabled.isDisplayed());
		
	}
	
	public void multiViewOptionEnabledForSameScriptVersion(String script1) throws InterruptedException {
		
		int s1=0;
		for (int i = 1; i <= resultRows.size(); i++) {

			String ScriptCode = driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[7]")).getText();
			if (ScriptCode.equalsIgnoreCase(script1) && s1<2) {
				// clicking the rows select checkbox for 1st version of script code
				logger.info("Selecting the two records with same script code and version");
				driver.findElement(
						By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[" +i+ "]/td[1]//span")).click();
				TimeUnit.SECONDS.sleep(3);
				s1++;
			}
			else if(s1>=2)
			{
				break;
			}
			
		}
		Assert.assertTrue(multiViewOptionEnabled.isDisplayed());
		multiViewOptionEnabled.click();
		logger.info("Verify multiview option enabled for scripts with same script code and version");
		Assert.assertEquals(selectedScriptsOnMultiviewScreen.getText().substring(6, 7), "2", "Multiview Screen Displays selected Scripts");
		
	}
	
	public void verifyQuickViewOption() {
		Assert.assertEquals(quickViewOption.isDisplayed(), true);	
		quickViewOption.click();
		try {
			TimeUnit.SECONDS.sleep(6);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(quickViewScriptResponseSignature.isDisplayed());
//		CommonUtils.isImageDisplayed(quickViewScriptResponseSignature);
		
	}
	
	public void statusUpdateDisabledWhenAllRecordsSelected() {
		selectAllCheckBox.click();
		Assert.assertTrue(statusUpdateDisabled.isDisplayed());
		firstRowCheckBoxChecked.click();
		Assert.assertTrue(statusUpdateDisabled.isDisplayed());
	}
	
	public void statusUpdateDisabledForDifferentScriptVersion(String script1, String script2) {
		int s1=0;
		int s2=0;
		
		for (int i = 1; i <= resultRows.size(); i++) {

			String ScriptCode = driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[7]")).getText();
			if (ScriptCode.equalsIgnoreCase(script1) && s1<1) {
				// clicking the rows select checkbox for 1st version of script code
				driver.findElement(
						By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[" +i+ "]/td[1]//span")).click();
				s1++;
			}
			else if(ScriptCode.equalsIgnoreCase(script2) && s2<1) {
				driver.findElement(
						By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[" +i+ "]/td[1]//span")).click();
				s2++;			
			}
			else if(s1>=1 && s2>=1) {
				break;
			}
			
		}			
		//Validating the multi view option After Selecting different script code versions
		Assert.assertTrue(statusUpdateDisabled.isDisplayed());
		
	}
	
	public void statusUpdateForSingleScript() throws InterruptedException {
		TimeUnit.SECONDS.sleep(6);
		String originalStatus=firstRowScriptStatus.getText();
		String selectedStatus="";
		
		firstRowCheckBoxUnchecked.click();
		TimeUnit.SECONDS.sleep(6);
		statusUpdateEnabled.click();
		TimeUnit.SECONDS.sleep(6);
		
		Select nextStatusDropDown=new Select(radioButtonNextStatus);
		
		List<WebElement> dropDownOptions=nextStatusDropDown.getOptions();
		for(WebElement option:dropDownOptions) {
			selectedStatus=option.getText();
			if(!selectedStatus.equalsIgnoreCase(originalStatus)){
				nextStatusDropDown.selectByVisibleText(selectedStatus);
				
				break;
			}
			
		}
		
		TimeUnit.SECONDS.sleep(6);
		statusUpdateOkButton.click();
		TimeUnit.SECONDS.sleep(20);
		String updatedStatus=firstRowScriptStatus.getText();		
		Assert.assertEquals(updatedStatus, selectedStatus);
		
	}
	
	
	//Verify status update functionality for multiple scripts at once
	public void statusUpdateForMultipleRecords(String scriptcode) throws InterruptedException {
		TimeUnit.SECONDS.sleep(6);
		searchResultScriptCodeFilterInputBox.sendKeys(scriptcode);
		TimeUnit.SECONDS.sleep(10);
		String noRecords=noRecordsFound.getText();
		//Checking if there are any records for selected script code
		if(!noRecords.equalsIgnoreCase("No records found.")) {
			
			firstRowCheckBoxUnchecked.click();
			String Script1Status=driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[10]")).getText();
			
			//int record2 is to get the row of the second selected script which needs to be used in the end for status verification
			int record2=0;
			for(int i=2;i<=resultColumnStatus.size();i++) {
				String script2Status=driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[10]")).getText();
				if(script2Status.equalsIgnoreCase(Script1Status)) {
					driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[1]")).click();
					record2=i;
					break;
				}
				else {
					System.out.println("No records found for given Script code");
				}
			}
			
			String selectedStatus="";
			statusUpdateEnabled.click();
			TimeUnit.SECONDS.sleep(6);
			
			Select nextStatusDropDown=new Select(radioButtonNextStatus);		
			List<WebElement> dropDownOptions=nextStatusDropDown.getOptions();
			for(WebElement option:dropDownOptions) {
				selectedStatus=option.getText();
				if(!selectedStatus.equalsIgnoreCase(Script1Status)){
					nextStatusDropDown.selectByVisibleText(selectedStatus);				
					break;
				}
				
			}
			
			TimeUnit.SECONDS.sleep(6);
			statusUpdateOkButton.click();
			TimeUnit.SECONDS.sleep(20);
			String updatedStatusScrip1=driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[10]")).getText();
			String updatedStatusScrip2=driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+record2+"]/td[10]")).getText();
			if(updatedStatusScrip1.equalsIgnoreCase(updatedStatusScrip2)) {
				Assert.assertEquals(updatedStatusScrip1, selectedStatus);
			}
			else {
				System.out.println("Status does not match");
			}
			
		}
		
		else {
			System.out.println("No Records with the selected Script code");
		}	
		
	}
}
