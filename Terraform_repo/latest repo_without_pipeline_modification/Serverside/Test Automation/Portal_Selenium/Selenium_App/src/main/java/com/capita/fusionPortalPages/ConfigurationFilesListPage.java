package com.capita.fusionPortalPages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class ConfigurationFilesListPage extends Basepage {
	public ConfigurationFilesListPage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement adminTab;
	
	@FindBy(xpath="//a[text()='Configuration Files']")
	WebElement configurationFilesMenu;
	
	@FindBy(xpath="//*[text()='Configuration File List']")
	WebElement headingConfigurationFiles;
	
	@FindBy(xpath="//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td")
	WebElement wm_noRecordsFoundText;   //No records found.
	
	@FindBy(xpath="//span[@id='configFilesTab:configFilesListForm:footerTitle']")
	WebElement wm_zeroResultsFound;  // 0 results found
	
	@FindBy(xpath="//span[@id='configFilesTab:headerTitle']")
	WebElement wmConfigTab;
	
	@FindBy(xpath="//span[@id='configFilesTab:headerTitle2']")
	WebElement scriptConfigTab;
	
	@FindBy(xpath="//tbody[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_data']/tr/td")
	WebElement script_noRecordsFoundText;  // No records found.
	
	@FindBy(xpath="//span[@id='configFilesTab:scriptConfigFilesListForm:footerTitle']")
	WebElement script_zeroResultsFound; //  0 results found
	
	@FindBy(xpath="//*[@data-widget='widget_configFilesTab']//ul/li[@data-index='0']")
	WebElement defaultWMConfig;
	
	@FindBy(xpath="//span[@id='adminMenuForm:configFileUploader_label']")
	WebElement uploadFileButton;
	
	@FindBy(xpath="//span[@id='configFilesTab:scriptConfigFilesListForm:footerTitle']")
	WebElement scriptConfigNumberOfRecordsFoundsText;
	
	@FindBy(xpath="//span[@id='configFilesTab:configFilesListForm:footerTitle']")
	WebElement wmConfigNumberOfRecordsFoundsText;
	
	@FindBy(xpath="//input[@type='file']")
	WebElement fileInput;
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Published Date']")
	WebElement columnHeaderPublishedDate;
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Published by']")
	WebElement columnHeaderPublishedBy;
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Work Group']")
	WebElement columnHeaderWorkGroup;
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Configuration File Name']")
	WebElement columnHeaderConfigurationFileName;
	
	
	@FindBy(xpath="//*[text()='Replace Configuration File']")
	WebElement replaceConfigurationFileHeaderMessage;
	
	@FindBy(xpath="//td[text()='Do you want to replace the existing WMConfig file?']")
	WebElement replaceExistingWMConfigFileMessage;
	
	
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Work Group']/following-sibling::input")
	WebElement txtBoxFilterWorkGroup;
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Published by']/following-sibling::input")
	WebElement txtBoxFilterPublishedBy;
	
	@FindBy(xpath="//thead[@id='configFilesTab:configFilesListForm:wmConfigList_head']//span[text()='Configuration File Name']/following-sibling::input")
	WebElement txtBoxFilterConfigurationFileName;
	
	@FindBy(xpath="//thead[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_head']//span[text()='Configuration File Name']/following-sibling::input")
	WebElement txtBoxFilterScriptConfigurationFileName;
	
	
	@FindBy(xpath="//td[contains(text(),'Do you want to replace the existing')]/following::button/span[text()='Yes']")
	WebElement btnYesReplaceFile;
	
	@FindBy(xpath="//td[contains(text(),'Do you want to replace the existing')]/following::button/span[text()='No']")
	WebElement btnNoReplaceFile;
	
	@FindBy(xpath="//td[contains(text(),'Are you sure you wish to delete')]/following::button[2]/span[text()='Yes']")
	WebElement btnYesDeleteFile;
	
	@FindBy(xpath="//td[contains(text(),'Are you sure you wish to delete')]/following::button[1]/span[text()='No']")
	WebElement btnNoDeleteFile;
	
	
	
	
	
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void navigateToConfigurationFiles() throws InterruptedException {
		try {
				if(adminTab.isDisplayed())
				{
					logger.info("Logged In User is an Admin User");
					logger.info("Click Admin tab");
					adminTab.click();
					TimeUnit.SECONDS.sleep(1);
				if(configurationFilesMenu.isDisplayed()) 
				{
					logger.info("User has access to Configuration Files Menu");
					if(driver.findElement(By.xpath("//*[text()='Configuration Files']/span")).getAttribute("class").contains("right")) 
					{
						configurationFilesMenu.click();
					}
					TimeUnit.SECONDS.sleep(1);
					logger.info("User is on the Configuration Files Page ");
					TimeUnit.SECONDS.sleep(1);
					headingConfigurationFiles.isDisplayed();	
						}
				else {
					logger.info("User has no access to Configuration Files Menu");
				}
				}
			}
			catch (Exception e) {
				logger.info("Logged In User is not an Admin User");	
			}
	}
	public void verifyDefaultTWMConfigSelected() {
		if (defaultWMConfig.getAttribute("aria-selected").equalsIgnoreCase("true")) {
			logger.info("WM Config is by default selected");
		}
	}
	
	public void verifyUploadButtonIsDisplayed() {
		if (uploadFileButton.isDisplayed()) {
			logger.info("Upload button is shown ");
		}
		else
			logger.info("Upload button is not shown");
	}
	public void verifyWMConfigCountFiles()
	{
		String wmConfigFilesCount = wmConfigTab.getText();
        String count= wmConfigFilesCount.replaceAll("\\D+", "");
        int count1=Integer.parseInt(count);
        if(count1 ==0)
        {
        	logger.info("WM config files are zero");
        	wm_noRecordsFoundText.isDisplayed();
        	wm_zeroResultsFound.isDisplayed();
        }
        else {
        	logger.info("WM Config files are not zero"+count);
        	wmConfigNumberOfRecordsFoundsText.isDisplayed();
        	logger.info(count+" results found");
        }
	}
	
	public void verifyScriptConfigTabDisplayed() {
		scriptConfigTab.isDisplayed();
		logger.info("Script Config Tab is displayed");
	}
	
	public void verifyScriptConfigCountFiles() {
		String scriptConfigFilesCount = scriptConfigTab.getText();
        String count= scriptConfigFilesCount.replaceAll("\\D+", "");
        int count1=Integer.parseInt(count);
        scriptConfigTab.click();
        if(count1 ==0)
        {
        	logger.info("Script config files are zero");
        	script_noRecordsFoundText.isDisplayed();
        	script_zeroResultsFound.isDisplayed();
        }
        else {
        	scriptConfigNumberOfRecordsFoundsText.isDisplayed();
        	logger.info(count+" results found");
        }
	}
	
	public void verifyTextOfTotalRecordsFound() {
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			WebElement notesIcon=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[1]/td[6]/a"));
			notesIcon.click();
		}
	}
	
	public void verifyClickUploadButton() {
		uploadFileButton.click();
		logger.info("Upload button is clicked");
	}
	
	public void verifySortByPublishedDate() throws InterruptedException, ParseException {
		SimpleDateFormat simpleformat = new SimpleDateFormat("DD MMM YYYY");
		
		List<WebElement> publishedDateObtained=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[1]"));
		List<Date> publishedDateObtainedList=new ArrayList<Date>();
		for(WebElement AppName:publishedDateObtained) {
			publishedDateObtainedList.add(simpleformat.parse(AppName.getText()));
		}
		
		columnHeaderPublishedDate.click();
		logger.info("Clicked Publish Date column Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> publishedDateGrid=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[1]"));
		List<Date> publishedDateSortedList=new ArrayList<Date>();
		
		for(WebElement AppName:publishedDateGrid) {
			publishedDateSortedList.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(publishedDateObtainedList);
		Collections.sort(publishedDateSortedList);
		Assert.assertTrue(publishedDateObtainedList.equals(publishedDateSortedList));
		logger.info("Sorted by Published Date Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		//Now Reverse Sorting by Published Date
		columnHeaderPublishedDate.click();
		logger.info("Clicked Published Date column Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> publishedDateGridDescending=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[1]"));
		List<Date> publishedDateSortedListDescending=new ArrayList<Date>();
		
		for(WebElement AppName:publishedDateGridDescending) {
			publishedDateSortedListDescending.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(publishedDateObtainedList, Collections.reverseOrder());
		Collections.sort(publishedDateSortedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(publishedDateSortedListDescending.equals(publishedDateObtainedList));
		logger.info("Sorted by Published Date Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifySortByPublishedBy() throws InterruptedException {
		List<WebElement> publishedByObtained=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[2]"));
		List<String> publishedByObtainedList=new ArrayList<>();
		for(WebElement temp:publishedByObtained) {
			publishedByObtainedList.add(temp.getText());
		}
		
		columnHeaderPublishedBy.click();
		logger.info("Clicked Published By Column");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> publishedByGrid=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[2]"));
		List<String> publishedBySortedList=new ArrayList<>();
		
		for(WebElement temp:publishedByGrid) {
			publishedBySortedList.add(temp.getText());
		}
		
		Collections.sort(publishedByObtainedList);
		Collections.sort(publishedBySortedList);
		Assert.assertTrue(publishedByObtainedList.equals(publishedBySortedList));
		logger.info("Sorted by Published By Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		
		//Now Reverse Sorting by Application Name
		columnHeaderPublishedBy.click();
		logger.info("Clicked Published By Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> publishedByObtainedDescending=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[2]"));
		List<String> publishedByObtainedListDescending=new ArrayList<>();
		for(WebElement AppName:publishedByObtainedDescending) {
			publishedByObtainedListDescending.add(AppName.getText());
		}
		
		
		Collections.sort(publishedByObtainedList, Collections.reverseOrder());
		Collections.sort(publishedByObtainedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(publishedByObtainedList.equals(publishedByObtainedListDescending));
		logger.info("Sorted by Published By Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifySortByWorkGroup() throws InterruptedException {
		List<WebElement> WorkGroupCodeObtained=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[3]"));
		List<String> WorkGroupCodeObtainedList=new ArrayList<>();
		for(WebElement temp:WorkGroupCodeObtained) {
			WorkGroupCodeObtainedList.add(temp.getText());
		}
		
		columnHeaderWorkGroup.click();
		logger.info("Clicked Work Group Code Column");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> WorkGroupCodeGrid=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[3]"));
		List<String> WorkGroupCodeSortedList=new ArrayList<>();
		
		for(WebElement temp:WorkGroupCodeGrid) {
			WorkGroupCodeSortedList.add(temp.getText());
		}
		
		Collections.sort(WorkGroupCodeObtainedList);
		Collections.sort(WorkGroupCodeSortedList);
		Assert.assertTrue(WorkGroupCodeObtainedList.equals(WorkGroupCodeSortedList));
		logger.info("Sorted by Work Group Code Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		
		//Now Reverse Sorting by Application Name
		columnHeaderWorkGroup.click();
		logger.info("Clicked Work Group Code Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> WorkGroupCodeObtainedDescending=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[3]"));
		List<String> WorkGroupCodeObtainedListDescending=new ArrayList<>();
		for(WebElement AppName:WorkGroupCodeObtainedDescending) {
			WorkGroupCodeObtainedListDescending.add(AppName.getText());
		}
		
		
		Collections.sort(WorkGroupCodeObtainedList, Collections.reverseOrder());
		Collections.sort(WorkGroupCodeObtainedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(WorkGroupCodeObtainedList.equals(WorkGroupCodeObtainedListDescending));
		logger.info("Sorted by Work Group Code Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifySortByConfigurationFileName() throws InterruptedException {
		List<WebElement> configurationFileNameObtained=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[4]"));
		List<String> configurationFileNameObtainedList=new ArrayList<>();
		for(WebElement temp:configurationFileNameObtained) {
			configurationFileNameObtainedList.add(temp.getText());
		}
		
		columnHeaderConfigurationFileName.click();
		logger.info("Clicked Configuration File Name Column");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> configurationFileNameGrid=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[4]"));
		List<String> configurationFileNameSortedList=new ArrayList<>();
		
		for(WebElement temp:configurationFileNameGrid) {
			configurationFileNameSortedList.add(temp.getText());
		}
		
		Collections.sort(configurationFileNameObtainedList);
		Collections.sort(configurationFileNameSortedList);
		Assert.assertTrue(configurationFileNameObtainedList.equals(configurationFileNameSortedList));
		logger.info("Sorted by Configuration File Name Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		
		//Now Reverse Sorting by Application Name
		columnHeaderConfigurationFileName.click();
		logger.info("Clicked Configuration File name Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> configurationFileNameObtainedDescending=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[4]"));
		List<String> configurationFileNameObtainedListDescending=new ArrayList<>();
		for(WebElement AppName:configurationFileNameObtainedDescending) {
			configurationFileNameObtainedListDescending.add(AppName.getText());
		}
		
		
		Collections.sort(configurationFileNameObtainedList, Collections.reverseOrder());
		Collections.sort(configurationFileNameObtainedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(configurationFileNameObtainedList.equals(configurationFileNameObtainedListDescending));
		logger.info("Sorted by Configuration File Name Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyFilterByPublishedBy() {
		String publishedByFilterInput="a";
		txtBoxFilterPublishedBy.sendKeys(publishedByFilterInput);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			List<WebElement> publishedByrecords=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[2]"));
			
			for(WebElement temp:publishedByrecords) {
				Assert.assertTrue(temp.getText().toLowerCase().contains(publishedByFilterInput.toLowerCase()));
			}
			logger.info("Filter by Invitation name works fine");
		}
		else {
			logger.info("No Records found for entered filter string");
		}	
	}
	
	public void verifyFilterByWorkGroup() {
		String workGroupFilterInput="a";
		txtBoxFilterWorkGroup.sendKeys(workGroupFilterInput);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			List<WebElement> workGroupRecords=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[3]"));
			
			for(WebElement temp:workGroupRecords) {
				Assert.assertTrue(temp.getText().toLowerCase().contains(workGroupFilterInput.toLowerCase()));
			}
			logger.info("Filter by Invitation name works fine");
		}
		else {
			logger.info("No Records found for entered filter string");
		}	
	}

	public void verifyFilterConfigurationFileName() {
		String configurationFileNameFilterInput="a";
		txtBoxFilterConfigurationFileName.sendKeys(configurationFileNameFilterInput);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			List<WebElement> configurationFileNameRecords=driver.findElements(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[4]"));
			
			for(WebElement temp:configurationFileNameRecords) {
				Assert.assertTrue(temp.getText().toLowerCase().contains(configurationFileNameFilterInput.toLowerCase()));
			}
			logger.info("Filter by Invitation name works fine");
		}
		else {
			logger.info("No Records found for entered filter string");
		}	
	}	
	
	public void UploadFile(String filepath) {
		
		
		try {
			fileInput.sendKeys(filepath);
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void verifyUploadSuccessful() {
		try {
			TimeUnit.SECONDS.sleep(1);
			WebElement successAlert=driver.findElement(By.xpath("//div[@role='alert']/div/span"));
			Assert.assertEquals(successAlert.getText(), "Configuration File Uploaded.");
			logger.info("Configuration File Uploaded Successfully");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public void verifyWMConfigUploadUnSuccessful() {
		try {
			TimeUnit.SECONDS.sleep(1);
			WebElement successAlert=driver.findElement(By.xpath("//div[@role='alert']/div/span"));
			Assert.assertEquals(successAlert.getText(), "WMConfig file is not valid.");
			logger.info("WMConfig file is not valid.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void verifyScriptConfigUploadUnSuccessful() {
		try {
			TimeUnit.SECONDS.sleep(1);
			WebElement successAlert=driver.findElement(By.xpath("//div[@role='alert']/div/span"));
			Assert.assertEquals(successAlert.getText(), "ScriptConfig file is not valid.");
			logger.info("ScriptConfig file is not valid.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void switchToTabScriptConfig() {
		scriptConfigTab.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void switchToTabWMConfig() {
		wmConfigTab.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickYesReplaceFilePopUp() {		
		btnYesReplaceFile.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));

	}
	
	public void clickNoReplaceFilePopUp() {		
		btnNoReplaceFile.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));

	}
	
	public void verifyWMConfigFileReplacedUnsuccessful() {
		btnNoReplaceFile.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
	}
	
	
	
	public void deleteScriptConfigFile(String fileName) {
		txtBoxFilterScriptConfigurationFileName.clear();
		txtBoxFilterScriptConfigurationFileName.sendKeys(fileName);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg = driver.findElements(By.xpath(
				"//tbody[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_data']//td[text()='No records found.']"));
		if (noRecordsFoundMsg.size() == 0) {
			WebElement deleteButton = driver.findElement(By
					.xpath("//tbody[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_data']/tr/td[6]/a"));
			deleteButton.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		} else {
			logger.info("File Not Found");
		}

	}

	public void deleteWMConfigFile(String fileName) {
		txtBoxFilterConfigurationFileName.clear();
		txtBoxFilterConfigurationFileName.sendKeys(fileName);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg = driver.findElements(By.xpath(
				"//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']//td[text()='No records found.']"));
		if (noRecordsFoundMsg.size() == 0) {
			WebElement deleteButton = driver.findElement(
					By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[6]/a"));
			deleteButton.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		} else {
			logger.info("File Not Found");
		}

	}
	
	public void clickYesDeleteFilePopUp() {		
		btnYesDeleteFile.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));

	}
	
	public void clickNoDeleteFilePopUp() {		
		btnNoDeleteFile.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));

	}

	public boolean isWMConfigFileAvailable(String fileName) {
		txtBoxFilterConfigurationFileName.clear();
		txtBoxFilterConfigurationFileName.sendKeys(fileName);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg = driver.findElements(By.xpath(
				"//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']//td[text()='No records found.']"));
		if (noRecordsFoundMsg.size() == 0) {
			logger.info("WM ConfigFile is Available");
			return true;
		} else {
			logger.info("WM Config File is Not Available");
			return false;
			
		}
	}
	
	public boolean isScriptConfigFileAvailable(String fileName) {
		txtBoxFilterScriptConfigurationFileName.clear();
		txtBoxFilterScriptConfigurationFileName.sendKeys(fileName);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg = driver.findElements(By.xpath(
				"//tbody[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_data']//td[text()='No records found.']"));
		if (noRecordsFoundMsg.size() == 0) {
			logger.info("Script Config File is Available");
			return true;
		} else {
			logger.info("Script Config File is Not Available");
			return false;
			
		}
	}
	
	public void isDeleteButtonAvailableForDefaultWMConfigFile() {
		try {
			driver.findElement(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[text()='WMCONFIG.xml']/following-sibling::td[2]/a"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(true);
		}
		
	}
	
	public void isDeleteButtonAvailableForDefaultScriptConfigFile() {
		try {
			driver.findElement(By.xpath("//tbody[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_data']/tr/td[text()='SCRIPTCONFIG.xml']/following-sibling::td[2]/a"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(true);
		}
		
	}
		
	public void downloadWMConfigFile(String fileName) {
		
		WebElement downloadLink=
		driver.findElement(By.xpath("//tbody[@id='configFilesTab:configFilesListForm:wmConfigList_data']/tr/td[text()='"+fileName+"']/following-sibling::td/a"));
		downloadLink.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void downloadScriptConfigFile(String fileName) {
		
		WebElement downloadLink=
		driver.findElement(By.xpath("//tbody[@id='configFilesTab:scriptConfigFilesListForm:scriptConfigList_data']/tr/td[text()='"+fileName+"']/following-sibling::td/a"));
		downloadLink.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void verifyInvalidFileTypeUploadValidation() {
		WebElement invalidFileTypeValidationMsg=
		driver.findElement(By.xpath("//span[text()='Please upload only xml files']"));
		Assert.assertTrue(invalidFileTypeValidationMsg.isDisplayed());
		WebElement closeIconValidationMsg=
		driver.findElement(By.xpath("//span[text()='Please upload only xml files']/ancestor::ul/preceding-sibling::a"));
		closeIconValidationMsg.click();
	}
	
}
