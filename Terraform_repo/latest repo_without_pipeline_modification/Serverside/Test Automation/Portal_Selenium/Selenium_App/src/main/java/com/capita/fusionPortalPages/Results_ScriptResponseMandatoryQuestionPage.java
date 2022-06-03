package com.capita.fusionPortalPages;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class Results_ScriptResponseMandatoryQuestionPage extends Basepage{
	
	public Results_ScriptResponseMandatoryQuestionPage(){
		PageFactory.initElements(driver, this);
	}
	
	
	@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[12]/a")
	WebElement detailedViewicon;
	
	@FindBy(xpath="//tbody[@id='detailsForm:resultSetTable_data']/tr/td[text()='Your system user name']/following-sibling::td/a[@id='detailsForm:resultSetTable:2:editResultResponsetBtn']")
	WebElement editResponseLinkForQuestionYourSystemUserName;
	
	@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_content']//div[@class='ui-toggleswitch-slider']/parent::div")
	WebElement toggleNA;
	
	@FindBy(xpath="//tbody[@id='detailsForm:resultSetTable_data']/tr/td[text()='Your system user name']/following-sibling::td[1]")
	WebElement updatedResponse;
	
	@FindBy(xpath="//td/textarea[@id='questionResponseForm:stringRes']")
	WebElement responseTextArea;
	
	
	@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
	WebElement responseSaveButton;
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//Photograph question elements
	
	@FindBy(xpath="//td[text()='Please take a Photograph']/following-sibling::td//img")
	WebElement photographQuestionImage;
	
	@FindBy(xpath="//body/img")
	WebElement photographQuestionImageOnNewTab;
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//Bitmap Image question elements
	
	@FindBy(xpath="//td[text()='Bitmap Questions']/parent::tr/following-sibling::tr[1]/td/a[contains(@id,'editResultResponsetBtn')]")
	List<WebElement> editResponseButtonForBitmapQuestion;
	
	@FindBy(xpath="//td[text()='Bitmap Questions']/parent::tr/following-sibling::tr/td[text()='Draw image']/following-sibling::td/a/img")
	WebElement bitmapImage;
	
	@FindBy(xpath="//body/img")
	WebElement bitmapImageOnNewTab;
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//User Signature question elements
	
	@FindBy(xpath="//td[text()='Signature Questions']/parent::tr/following-sibling::tr[1]/td/a[contains(@id,'editResultResponsetBtn')]")
	List<WebElement> editResponseButtonForUserSignatureQuestion;
	
	@FindBy(xpath="//td[text()='Signature Questions']/parent::tr/following-sibling::tr/td[text()='User Signature']/following-sibling::td/a/img")
	WebElement userSignatureImage;
	
	@FindBy(xpath="//body/img")
	WebElement userSignatureImageOnNewTab;
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//CSV question elements
		
		@FindBy(xpath="//td[text()='CSV QUESTION']/parent::tr/following-sibling::tr[3]/td/a[contains(@id,'editResultResponsetBtn')]")
		List<WebElement> editResponseButtonForCSVQuestion;
		
		@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[10]")
		WebElement detailedViewIconForCSVQuestionScript;

	
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//LEVEL question elements
		
		@FindBy(xpath="//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr[1]/td[10]")
		WebElement detailedViewIconForLevelQuestionScript;
		
		@FindBy(xpath="//td[text()='Points on gauge 8 increment 2 starting point 0']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseIconForLevelQuestion;
		
		@FindBy(xpath="//div[@class='ui-slider ui-corner-all ui-slider-horizontal ui-widget ui-widget-content']/span")
		WebElement LevelQuestionSlider;
		
		@FindBy(xpath="//span[@id='questionResponseForm:outputDisplay']")
		WebElement LevelQuestionSliderValue;
		
		@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
		WebElement LevelQuestionSaveButton;
		
		
		@FindBy(xpath="//td[text()='Points on gauge 8 increment 2 starting point 0']/parent::tr/td[2]")
		WebElement LevelQuestionResponseColumnValue;
		
		@FindBy(xpath="//td[text()='Points on gauge 8 increment 2 starting point 0']/parent::tr/td//a[@id='detailsForm:resultSetTable:1:editLogBtn']/span")
		WebElement LevelQuestionEditLogIcon;
		
		@FindBy(xpath="//tbody[@id='detailsForm:resultSetTable:1:editLog_data']/tr[1]/td[5]")
		WebElement LevelQuestionEditLogPreviousResponseValue;
		
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//GPS question elements
		
		@FindBy(xpath="//td[text()='GPS Location Question']/parent::tr/following-sibling::tr[1]/td/a[contains(@id,'editResultResponsetBtn')]")
		List<WebElement> editResponseButtonForGPSQuestion;
		
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//SinglePick question elements
		
		@FindBy(xpath="//td[text()='Select Colour']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseSelectColourQuestion;
		
		@FindBy(xpath="//td[text()='Select Colour']/parent::tr/td[2]")
		WebElement SelectColourResponseColumnValue;
		
		@FindBy(xpath="//td[text()='Select Colour']/parent::tr/td[3]")
		WebElement SelectColourFreeTextColumnValue;
		
		
		@FindBy(xpath="//table[@id='questionResponseForm:responseEditPanel']//select")
		WebElement SelectColourResponseDropDown;
		
		@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
		WebElement SelectColourResponseSaveButton;
		
		@FindBy(xpath="//td[text()='Select Colour']/parent::tr/td//a[contains(@id,'editLogBtn')]/span")
		WebElement SelectColourQuestionEditLogIcon;
		
		
		@FindBy(xpath="//tbody[@id='detailsForm:resultSetTable:97:editLog_data']/tr[1]/td[5]")
		WebElement SelectColourQuestionEditLogPreviousResponseValue;
		
		@FindBy(xpath="//div[@id='statusDialog_content']/center")
		WebElement loadingBarImage;
		
		@FindBy(xpath="//td/textarea[@id='questionResponseForm:freeText']")
		WebElement SelectColourQuestionFreeTextArea;
		
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//User Calculation question elements		
		
		@FindBy(xpath="//td[text()='Calculation Questions']/parent::tr/following-sibling::tr[4]/td/a[contains(@id,'editResultResponsetBtn')]")
		List<WebElement> editResponseButtonForCalculationQuestion;
		
		//Heading Question Type elements++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
		
		@FindBy(xpath="//td[text()='Inspection & Details']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		List<WebElement> editResponseButtonForHeadingQuestion;
		
		@FindBy(xpath="//td[text()='Inspection & Details']/parent::tr/td/a[contains(@id,'noteTag')]")
		List<WebElement> notesButtonForHeadingQuestion;
		
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//SinglePick question elements
				
		@FindBy(xpath="//td[text()='Condition of engine']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseConditionQuestion;
		
		@FindBy(xpath="//td[text()='Condition of engine']/parent::tr/td[2]/span")
		WebElement ConditionQuestionResponseColumnValue;
		
		@FindBy(xpath="//table[@id='questionResponseForm:responseEditPanel']//select")
		WebElement ConditionQuestionResponseDropDown;
		
		@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
		WebElement ConditionQuestionResponseSaveButton;
		
		@FindBy(xpath="//td[text()='Condition of engine']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement ConditionQuestionEditLogIcon;
		
		@FindBy(xpath="//tbody[@id='detailsForm:resultSetTable:23:editLog_data']/tr[1]/td[5]")
		WebElement ConditionQuestionEditLogPreviousResponseValue;
		
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// DECIMAL & UOM Question Type elements
		
		
		@FindBy(xpath="//div[@id='questionResponseForm:messages']//span[text()='Value must be numeric']")
		WebElement DecimalUOMQuestionInvalidResponseErrorMessage;
		
		@FindBy(xpath="//input[@id='questionResponseForm:desEditResponse']")
		WebElement DecimalUOMQuestionResponseInputTextBox;
		
		@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
		WebElement DecimalUOMQuestionResponseSaveButton;
		
		@FindBy(xpath="//td[text()='Record stopping distance at 80 kph']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseDecimalUOMQuestion;		
		
		@FindBy(xpath="//td[text()='Record stopping distance at 80 kph']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement DecimalUOMQuestionEditLogIcon;
		
		@FindBy(xpath="//td[text()='Record stopping distance at 80 kph']/parent::tr/td[2]/span")
		WebElement DecimalUOMQuestionResponseColumnValue;		
		
		@FindBy(xpath="//tbody[@id='detailsForm:resultSetTable:44:editLog_data']/tr[1]/td[5]")
		WebElement DecimalUOMQuestionEditLogPreviousResponseValue;
		
		
		// TaskList Question Type elements
		
		@FindBy(xpath="//td[text()='Carry out additional vehicle checks']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseTaskListQuestion;
		
		@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
		WebElement TaskListQuestionResponseSaveButton;
		
		@FindBy(xpath="//td[text()='Carry out additional vehicle checks']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement TaskListQuestionEditLogIcon;
		
		// Multi Pick Rule Based Question Type elements
		
		@FindBy(xpath="//td[text()='In Car Entertainment System']/parent::tr/td[2]/span")
		WebElement CarEnteretainmentSystemResponseColumnValue;
		
		@FindBy(xpath="//td[text()='In Car Entertainment System']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseCarEnteretainmentSystem;
		
		@FindBy(xpath="//table[@id='questionResponseForm:responseEditPanel']//select")
		WebElement CarEnteretainmentSystemResponseDropDown;
		
		@FindBy(xpath="//div[@id='questionResponseForm:editResSidePanel_footer']//span[text()='Save']")
		WebElement CarEnteretainmentSystemResponseSaveButton;
		
		@FindBy(xpath="//td[text()='In Car Entertainment System']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement CarEnteretainmentSystemEditLogIcon;
		
		@FindBy(xpath="//td[text()='In Car Entertainment System']/parent::tr/following-sibling::tr[1]//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]")
		WebElement CarEnteretainmentSystemEditLogPreviousResponseValue;
		
		@FindBy(xpath="//td[text()='Manufacturers']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseManufacturers;
		
		@FindBy(xpath="//td[text()='Manufacturers']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement ManufacturersEditLogIcon;
		
		// Single Pick Rule Based Question Type elements
		
		@FindBy(xpath="//td[text()='Is vehicle equipped with a CD changer?']/parent::tr/td[2]/span")
		WebElement CDchangerResponseColumnValue;
		
		@FindBy(xpath="//td[text()='Is vehicle equipped with a CD changer?']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseCDchanger;
		
		@FindBy(xpath="//table[@id='questionResponseForm:responseEditPanel']//select")
		WebElement CDchangerResponseDropDown;
		
		@FindBy(xpath="//td[text()='Is vehicle equipped with a CD changer?']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement CDchangerEditLogIcon;
		
		@FindBy(xpath="//td[text()='Is vehicle equipped with a CD changer?']/parent::tr/following-sibling::tr[1]//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]")
		WebElement CDchangerEditLogPreviousResponseValue;
		
		
		
		
		@FindBy(xpath="//td[text()='In Car Entertainment Type']/parent::tr/td[2]/span")
		WebElement carEntertainmentTypeResponseColumnValue;
		
		@FindBy(xpath="//td[text()='In Car Entertainment Type']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponsecarEntertainmentType;
		
		@FindBy(xpath="//table[@id='questionResponseForm:responseEditPanel']//select")
		WebElement carEntertainmentTypeResponseDropDown;
		
		@FindBy(xpath="//td[text()='In Car Entertainment Type']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement carEntertainmentTypeEditLogIcon;
		
		@FindBy(xpath="//td[text()='In Car Entertainment Type']/parent::tr/following-sibling::tr[1]//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]")
		WebElement carEntertainmentTypeEditLogPreviousResponseValue;
		
		// Multi Pick Question Type elements
		
		@FindBy(xpath="//td[text()='Indicate which tyres are in bad condition']/parent::tr/td[2]/ul/li/span")
		List<WebElement> multiPickQuestionResponseColumnValue;
		//div[contains(@id,'questionResponseForm')]//ul/li[@class='ui-selectlistbox-item ui-corner-all ui-state-highlight']
				
		@FindBy(xpath="//td[text()='Indicate which tyres are in bad condition']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponsemultiPickQuestion;
		
		@FindBy(xpath="//td[text()='Indicate which tyres are in bad condition']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement multiPickQuestionEditLogIcon;
		
		// Date Question Type elements
		
		@FindBy(xpath="//td[text()='Date of Inspection']/parent::tr/td[2]")
		WebElement DateQuestionResponseColumnValue;
		
		@FindBy(xpath="//td[text()='Date of Inspection']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseDateQuestion;
		
		@FindBy(xpath="//span[@id='questionResponseForm:editResDate']/input")
		WebElement DateQuestionInputField;
		
		
		@FindBy(xpath="//td[text()='Date of Inspection']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement DateQuestionEditLogIcon;
		
		@FindBy(xpath="//td[text()='Date of Inspection']/parent::tr/following-sibling::tr[1]//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]")
		WebElement DateQuestionEditLogPreviousResponseValue;
		
		// Time Question Type elements
		
		@FindBy(xpath="//td[text()='Time of Inspection']/parent::tr/td[2]")
		WebElement TimeQuestionResponseColumnValue;
		
		@FindBy(xpath="//td[text()='Time of Inspection']/parent::tr/td/a[contains(@id,'editResultResponsetBtn')]")
		WebElement EditResponseTimeQuestion;
		
		@FindBy(xpath="//span[@id='questionResponseForm:timePickerId']/input")
		WebElement TimeQuestionInputField;
		
		
		@FindBy(xpath="//td[text()='Time of Inspection']/parent::tr//a[contains(@id,'editLogBtn')]/span")
		WebElement TimeQuestionEditLogIcon;
		
		@FindBy(xpath="//td[text()='Time of Inspection']/parent::tr/following-sibling::tr[1]//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]")
		WebElement TimeQuestionEditLogPreviousResponseValue;
		
		
		

	public void editReponseForQuestionYourSystemUserNameNA() throws InterruptedException {
		
		WebDriverWait wait=new WebDriverWait(driver, 120);
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		logger.info("CLick Edit Response link");
		TimeUnit.SECONDS.sleep(5);	
		editResponseLinkForQuestionYourSystemUserName.click();
		TimeUnit.SECONDS.sleep(5);
		String togglesMode=toggleNA.getAttribute("class");
		String updatedResponseText;
		if(togglesMode.equalsIgnoreCase("ui-toggleswitch ui-widget editReponseCheckBox")) {
			logger.info("Turning the toggle N/A ON");
			toggleNA.click();
			logger.info("Save the response");
			responseSaveButton.click();					
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			updatedResponseText=updatedResponse.getText();
			logger.info("Validate the saved response");
			Assert.assertEquals(updatedResponseText, "N/A");
		}
		else {
			logger.info("The Reponse is already N/A");
			logger.info("Setting the toggle N/A OFF");
			toggleNA.click();
			TimeUnit.SECONDS.sleep(5);
			logger.info("Entering the Response");
			String reponseInput=RandomStringUtils.randomAlphabetic(4);
			responseTextArea.clear();
			responseTextArea.sendKeys(reponseInput);
			logger.info("Save the response");
			responseSaveButton.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			updatedResponseText=updatedResponse.getText();
			logger.info("Validate the saved response");
			Assert.assertEquals(updatedResponseText, reponseInput);
			
		}		
	}
	
	
	
	public void photographQuestionTypeViewOnly() throws InterruptedException {
		
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		logger.info("Click the image in photograph");
		TimeUnit.SECONDS.sleep(5);
		String parentWinHandle=driver.getWindowHandle();
		photographQuestionImage.click();
		TimeUnit.SECONDS.sleep(3);
		Set<String> winHandles=driver.getWindowHandles();
		for(String handle:winHandles) {
			if(!handle.equalsIgnoreCase(parentWinHandle)) {
				driver.switchTo().window(handle);
				break;
			}
		}
		TimeUnit.SECONDS.sleep(10);
		Assert.assertEquals(photographQuestionImageOnNewTab.isDisplayed(), true);
		driver.switchTo().window(parentWinHandle);
		TimeUnit.SECONDS.sleep(3);
	}
	
	
	public void bitmapQuestionTypeViewonly() throws InterruptedException {

		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		int editResponse=editResponseButtonForBitmapQuestion.size();
		if(editResponse==0) {
			logger.info("Edit response is not available for Bitmap Question");
		}
		else {
			logger.info("Edit response is available for Bitmap Question");
		}
		
		String parentWinHandle = driver.getWindowHandle();
		logger.info("Click the Bitmap image");
		bitmapImage.click();
		TimeUnit.SECONDS.sleep(3);
		Set<String> winHandles = driver.getWindowHandles();
		for (String handle : winHandles) {
			if (!handle.equalsIgnoreCase(parentWinHandle)) {
				driver.switchTo().window(handle);
				break;
			}
		}
		TimeUnit.SECONDS.sleep(10);
		Assert.assertEquals(bitmapImageOnNewTab.isDisplayed(), true);
		driver.switchTo().window(parentWinHandle);
		TimeUnit.SECONDS.sleep(3);
	}
	
	public void userSignatureQuestionTypeViewonly() throws InterruptedException {

		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		int editResponse=editResponseButtonForUserSignatureQuestion.size();
		if(editResponse==0) {
			logger.info("Edit response is not available for Signature Question");
		}
		else {
			logger.info("Edit response is available for Signature Question");
		}
		
		String parentWinHandle = driver.getWindowHandle();
		logger.info("Click the Bitmap image");
		userSignatureImage.click();
		TimeUnit.SECONDS.sleep(3);
		Set<String> winHandles = driver.getWindowHandles();
		for (String handle : winHandles) {
			if (!handle.equalsIgnoreCase(parentWinHandle)) {
				driver.switchTo().window(handle);
				break;
			}
		}
		TimeUnit.SECONDS.sleep(10);
		Assert.assertEquals(userSignatureImageOnNewTab.isDisplayed(), true);
		driver.switchTo().window(parentWinHandle);
		TimeUnit.SECONDS.sleep(3);
	}
	
	public void CSVQuestionTypeViewonly() throws InterruptedException {

		logger.info("Navigating to Detailed View");
		detailedViewIconForCSVQuestionScript.click();
		TimeUnit.SECONDS.sleep(5);
		int editResponse=editResponseButtonForCSVQuestion.size();
		if(editResponse==0) {
			logger.info("Edit response is not available for CSV Question");
		}
		else {
			logger.info("Edit response is available for CSV Question");
		}
		
	}
	
	public void LevelQuestionTypeEdit() throws InterruptedException {

		logger.info("Navigating to Detailed View");
		detailedViewIconForLevelQuestionScript.click();
		TimeUnit.SECONDS.sleep(5);
		String oldResponseValue=LevelQuestionResponseColumnValue.getText();
		EditResponseIconForLevelQuestion.click();
		TimeUnit.SECONDS.sleep(5);
		Actions slide=new Actions(driver);
		String OldSliderPosition=LevelQuestionSlider.getAttribute("style");
		if(OldSliderPosition.equalsIgnoreCase("left: 0%;")) {
			slide.dragAndDropBy(LevelQuestionSlider, 50, 0).perform();
			TimeUnit.SECONDS.sleep(5);
		}
		else if(OldSliderPosition.equalsIgnoreCase("left: 100%;")) {
			slide.dragAndDropBy(LevelQuestionSlider, -25, 0).perform();
			TimeUnit.SECONDS.sleep(5);
		}
		else {
			slide.dragAndDropBy(LevelQuestionSlider, 50, 0).perform();
			TimeUnit.SECONDS.sleep(5);
		}
		
		String UpdatedSliderPosition=LevelQuestionSliderValue.getText();
		LevelQuestionSaveButton.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(LevelQuestionResponseColumnValue.getText(), UpdatedSliderPosition);
		logger.info("Response Updated Successfully");
		LevelQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(LevelQuestionEditLogPreviousResponseValue.getText(), oldResponseValue);
		
		
	}
	
	public void GPSQuestionViewOnly() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		int editResponse=editResponseButtonForGPSQuestion.size();
		Assert.assertEquals(editResponse, 0);	
		
	}
	
	public void SinglePickQuestionTypeEdit() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		String oldResponseValue=SelectColourResponseColumnValue.getText();
		EditResponseSelectColourQuestion.click();
		TimeUnit.SECONDS.sleep(5);
		String selectedOption="";		
		Select colourDropDown=new Select(SelectColourResponseDropDown);
		List<WebElement> dropDownOptions=colourDropDown.getOptions();
		String OriginalSelectedOption=colourDropDown.getFirstSelectedOption().getText();
		String freetext="";
		
		for(WebElement option:dropDownOptions) {
			selectedOption=option.getText();
			//if selected option is not already selected OR selected option is not empty 
			if(!((selectedOption.equalsIgnoreCase(OriginalSelectedOption))||(selectedOption.equalsIgnoreCase("")))){
				//if OTHERS is selected then Free Text is mandatory
				if(selectedOption.equalsIgnoreCase("OTHER")) {
					colourDropDown.selectByVisibleText(selectedOption);	
					freetext=RandomStringUtils.randomAlphabetic(4);
					SelectColourQuestionFreeTextArea.clear();
					SelectColourQuestionFreeTextArea.sendKeys(freetext);
					break;
				}
				else {
					SelectColourQuestionFreeTextArea.clear();
					colourDropDown.selectByVisibleText(selectedOption);
					break;
				}
				
			}
			
		}
		
		SelectColourResponseSaveButton.click();
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	
		if(selectedOption.equalsIgnoreCase("OTHER")) {
			Assert.assertEquals(SelectColourFreeTextColumnValue.getText(), freetext);
		}
		Assert.assertEquals(SelectColourResponseColumnValue.getText(), selectedOption);
		SelectColourQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(SelectColourQuestionEditLogPreviousResponseValue.getText(), oldResponseValue);
		TimeUnit.SECONDS.sleep(3);
		
	}
	
	public void UserCalculationQuestionTypeViewonly() throws InterruptedException {

		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		int editResponse=editResponseButtonForCalculationQuestion.size();
		if(editResponse==0) {
			logger.info("Edit response is not available for User Calculation Question");
		}
		else {
			logger.info("Edit response is available for User Calculation Question");
		}
		
	}
	
	public void HeadingQuestionTypeViewonly() throws InterruptedException {

		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		int editResponse=editResponseButtonForCalculationQuestion.size();
		int notes=notesButtonForHeadingQuestion.size();
		if(editResponse==0 && notes==0) {
			logger.info("Edit response and Notes is not available for Heading Question");
		}
		else {
			logger.info("Edit response and Notes is available for User Calculation Question");
		}
		
	}
	
	
	public void ConditionQuestionTypeEdit() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		String oldResponseValue=ConditionQuestionResponseColumnValue.getText();
		EditResponseConditionQuestion.click();		
		TimeUnit.SECONDS.sleep(5);
		String selectedOption="";		
		Select conditionDropDown=new Select(ConditionQuestionResponseDropDown);
		List<WebElement> dropDownOptions=conditionDropDown.getOptions();
		String OriginalSelectedOption=conditionDropDown.getFirstSelectedOption().getText();
		
		for (WebElement option : dropDownOptions) {
			selectedOption = option.getText();
			// if selected option is not already selected OR selected option is not empty
			if (!((selectedOption.equalsIgnoreCase(OriginalSelectedOption)) || (selectedOption.equalsIgnoreCase("")))) {

				conditionDropDown.selectByVisibleText(selectedOption);
				break;

			}

		}
		
		ConditionQuestionResponseSaveButton.click();
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		Assert.assertEquals(ConditionQuestionResponseColumnValue.getText(), selectedOption);
		ConditionQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(ConditionQuestionEditLogPreviousResponseValue.getText(), oldResponseValue);
		TimeUnit.SECONDS.sleep(3);
		
	}
	
	
	public void DecimalUOMQuestionTypeEdit() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		String oldResponseValue=DecimalUOMQuestionResponseColumnValue.getText();
		EditResponseDecimalUOMQuestion.click();
		logger.info("Clicked Edit response link");
		TimeUnit.SECONDS.sleep(5);
		DecimalUOMQuestionResponseInputTextBox.clear();
		DecimalUOMQuestionResponseInputTextBox.sendKeys("ABCDEFGH");
		logger.info("Enetered the invalid input");
		DecimalUOMQuestionResponseSaveButton.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(true, DecimalUOMQuestionInvalidResponseErrorMessage.isDisplayed());
		logger.info("Validation message displayed for Invalid input");
		String input=RandomStringUtils.randomNumeric(3);
		DecimalUOMQuestionResponseInputTextBox.clear();
		DecimalUOMQuestionResponseInputTextBox.sendKeys(input);
		logger.info("Enetered the valid input");
		DecimalUOMQuestionResponseSaveButton.click();
		logger.info("Clicked Save button");
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		Assert.assertEquals(DecimalUOMQuestionResponseColumnValue.getText(), input);
		logger.info("Update response successfull");
		DecimalUOMQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		logger.info("Clicked Edit log button");
		Assert.assertEquals(DecimalUOMQuestionEditLogPreviousResponseValue.getText(), oldResponseValue);
		TimeUnit.SECONDS.sleep(3);		
		
	}
	
	
	public void TaskListQuestionTypeEdit() throws InterruptedException {
		
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		EditResponseTaskListQuestion.click();
		logger.info("Clicked Edit response link");
		TimeUnit.SECONDS.sleep(5);
		
		//List of already saved selected tasks checkboxes so that we can avoid selecting these again
		List<WebElement> alreadySelectedtasks=driver.findElements(By.xpath("//table[@id='questionResponseForm:responseEditPanel']//li[@class='ui-selectlistbox-item ui-corner-all ui-state-highlight']"));
		
		//List of actual span elements (check boxes for the selected ones)
		List<WebElement> alreadySelectedcheckboxesSpan=driver.findElements(By.xpath("//table[@id='questionResponseForm:responseEditPanel']//li[@class='ui-selectlistbox-item ui-corner-all ui-state-highlight']//span"));
		
		//fetching the string names from list alreadySelectedtasks and storing in this list, at the same time unchecking the item
		List<String> alreadySelectedtasksName=new ArrayList<String>();
	    for(int i=0;i<alreadySelectedtasks.size();i++){
	        alreadySelectedtasksName.add(alreadySelectedtasks.get(i).getText());
	        alreadySelectedcheckboxesSpan.get(i).click();	//unchecked the already selected item
	        TimeUnit.SECONDS.sleep(5);
	    }
	    
	    //Now fetching unchecked items
	    List<WebElement> Uncheckedtasks=driver.findElements(By.xpath("//table[@id='questionResponseForm:responseEditPanel']//li[@class='ui-selectlistbox-item ui-corner-all']"));
	    
	    //selecting the items which were previously not selected
	    
		for(int i=0;i<alreadySelectedtasksName.size();i++) {
			for(int j=0;j<Uncheckedtasks.size();j++) {
				if(!(Uncheckedtasks.get(j).getText().equalsIgnoreCase(alreadySelectedtasksName.get(i)))){
					Uncheckedtasks.get(j).click();
					break;
				}
			}
//			break;
			
		}
		
		//Creating new list of checked items so that we can comapre it with updated list on view screen
		List<WebElement> NewlySelectedtasks=driver.findElements(By.xpath("//table[@id='questionResponseForm:responseEditPanel']//li[@class='ui-selectlistbox-item ui-corner-all ui-state-highlight']"));
		List<String> NewlySelectedtasksName=new ArrayList<String>();
	    for(int i=0;i<NewlySelectedtasks.size();i++){
	        NewlySelectedtasksName.add(NewlySelectedtasks.get(i).getText());
	        TimeUnit.SECONDS.sleep(5);
	    }
	    
	    TaskListQuestionResponseSaveButton.click();
	    logger.info("Clicked Save button");
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		//Fetching the list if selected items from View screen to compare with edit screen selection
		List<WebElement> selectedtasksonresponsecolumn=driver.findElements(By.xpath("//td[text()='Carry out additional vehicle checks']/parent::tr/td[2]//li[@class=\"taskListComplete\"]"));
		List<String> selectedtasksonresponsecolumnName=new ArrayList<String>();
	    for(int i=0;i<selectedtasksonresponsecolumn.size();i++){
	        selectedtasksonresponsecolumnName.add(selectedtasksonresponsecolumn.get(i).getText());
	        TimeUnit.SECONDS.sleep(5);
	    }		
		
	    //sorting both lists so that we can compare
		Collections.sort(selectedtasksonresponsecolumnName);
	    Collections.sort(NewlySelectedtasksName);
		//Validating lists from Edit and View screen
	    Assert.assertEquals(selectedtasksonresponsecolumnName, NewlySelectedtasksName);
		
	    
//	    Click Edit log icon
	    
	    TaskListQuestionEditLogIcon.click();
	    TimeUnit.SECONDS.sleep(5);
	    
	    //Fetching a list of items from edit log previous response
	    List<WebElement> editLogtasks=driver.findElements(By.xpath("//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]//li[@class='taskListComplete']"));
		List<String> editLogtasksName=new ArrayList<String>();
	    for(int i=0;i<editLogtasks.size();i++){
	    	editLogtasksName.add(editLogtasks.get(i).getText());
	        TimeUnit.SECONDS.sleep(5);
	    }
		
	  //sorting both lists so that we can compare
	  	Collections.sort(alreadySelectedtasksName);
	  	Collections.sort(editLogtasksName);
	  	
	  	//Validating lists reponse before update and edit log after update
	  	Assert.assertEquals(alreadySelectedtasksName, editLogtasksName);
	  	
	}
	
	public void InCarEntertainmentSystemQuestion() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		String oldResponseValue=CarEnteretainmentSystemResponseColumnValue.getText();
		EditResponseCarEnteretainmentSystem.click();	
		logger.info("Click Edit response link");
		TimeUnit.SECONDS.sleep(5);
		String selectedOption="";		
		Select carEntertainmetntDropDown=new Select(CarEnteretainmentSystemResponseDropDown);
		List<WebElement> dropDownOptions=carEntertainmetntDropDown.getOptions();
		String OriginalSelectedOption=carEntertainmetntDropDown.getFirstSelectedOption().getText();
		
		for (WebElement option : dropDownOptions) {
			selectedOption = option.getText();
			// if selected option is not already selected OR selected option is not empty
			if (!((selectedOption.equalsIgnoreCase(OriginalSelectedOption)) || (selectedOption.equalsIgnoreCase("")))) {

				carEntertainmetntDropDown.selectByVisibleText(selectedOption);
				break;

			}

		}
		
		CarEnteretainmentSystemResponseSaveButton.click();
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		Assert.assertEquals(CarEnteretainmentSystemResponseColumnValue.getText(), selectedOption);
		CarEnteretainmentSystemEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(CarEnteretainmentSystemEditLogPreviousResponseValue.getText(), oldResponseValue);
		TimeUnit.SECONDS.sleep(3);
	
	}
	
	public void ManufacturersQuestion() throws InterruptedException {
		//List of the old response values on view screen
		List<WebElement> oldResponse=driver.findElements(By.xpath("//td[text()='Manufacturers']/parent::tr/td[2]/ul/li/span"));
		List<String> oldResponsetexts=new ArrayList<String>(); 
		for(WebElement item:oldResponse) {
			oldResponsetexts.add(item.getText());
		}		
		EditResponseManufacturers.click();
		logger.info("Click Edit response link");
		TimeUnit.SECONDS.sleep(5);
		
		// List of the check box Options on Edit screen
		List<WebElement> checkboxOptions = driver.findElements(By.xpath("//div[contains(@id,'questionResponseForm')]//div[@class='ui-selectlistbox-listcontainer']/ul/li"));
		List<String> checkboxOptiontexts = new ArrayList<String>();
		for (int i=1; i<=checkboxOptions.size();i++) {
			driver.findElement(By.xpath("//table[@id='questionResponseForm:responseEditPanel']//div[@class='ui-selectlistbox-listcontainer']/ul/li["+i+"]//span")).click();
			checkboxOptiontexts.add(checkboxOptions.get(i-1).getText());
		}
		
		CarEnteretainmentSystemResponseSaveButton.click();
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		//List of updated response values on view screen
		List<WebElement> newResponse=driver.findElements(By.xpath("//td[text()='Manufacturers']/parent::tr/td[2]/ul/li/span"));
		List<String> newResponsetexts=new ArrayList<String>(); 
		for(WebElement item:newResponse) {
			newResponsetexts.add(item.getText());
		}	
		
		Collections.sort(checkboxOptiontexts);
		Collections.sort(newResponsetexts);
		//Verifying the response on Edit and view screen matches
		Assert.assertEquals(newResponsetexts, checkboxOptiontexts);
		
		ManufacturersEditLogIcon.click();
		logger.info("Click Edit log link");
		TimeUnit.SECONDS.sleep(5);
		
		//List of edit log response values on view screen
		List<WebElement> editLogResponse=driver.findElements(By.xpath("//td[text()='Manufacturers']/parent::tr/following-sibling::tr[1]//tbody[contains(@id,'editLog_data')]/tr[1]/td[5]/ul/li"));
		List<String> editLogResponsetexts = new ArrayList<String>();
		for(WebElement item:editLogResponse) {
			editLogResponsetexts.add(item.getText());
		}
		
		Collections.sort(oldResponsetexts);
		Collections.sort(editLogResponsetexts);
		//Verifying the old response and edit log response matches
		Assert.assertEquals(oldResponsetexts, editLogResponsetexts);
		
	}
	
	public void CDchanger() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		String oldResponseValue=CDchangerResponseColumnValue.getText();
		EditResponseCDchanger.click();	
		logger.info("Click Edit response link");
		TimeUnit.SECONDS.sleep(5);
		String selectedOption="";		
		Select CDchangerDropDown=new Select(CDchangerResponseDropDown);
		List<WebElement> dropDownOptions=CDchangerDropDown.getOptions();
		String OriginalSelectedOption=CDchangerDropDown.getFirstSelectedOption().getText();
		
		for (WebElement option : dropDownOptions) {
			selectedOption = option.getText();
			// if selected option is not already selected OR selected option is not empty
			if (!((selectedOption.equalsIgnoreCase(OriginalSelectedOption)) || (selectedOption.equalsIgnoreCase("")))) {

				CDchangerDropDown.selectByVisibleText(selectedOption);
				break;

			}

		}
		
		CarEnteretainmentSystemResponseSaveButton.click();//save button same for all edit forms
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		Assert.assertEquals(CDchangerResponseColumnValue.getText(), selectedOption);
		CDchangerEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(CDchangerEditLogPreviousResponseValue.getText(), oldResponseValue);
		TimeUnit.SECONDS.sleep(3);
	}
	
	public void carEntertainmentType() throws InterruptedException{
		String oldResponseValue=carEntertainmentTypeResponseColumnValue.getText();
		EditResponsecarEntertainmentType.click();	
		logger.info("Click Edit response link");
		TimeUnit.SECONDS.sleep(5);
		String selectedOption="";		
		Select carEntertainmentTypeDropDown=new Select(carEntertainmentTypeResponseDropDown);
		List<WebElement> dropDownOptions=carEntertainmentTypeDropDown.getOptions();
		String OriginalSelectedOption=carEntertainmentTypeDropDown.getFirstSelectedOption().getText();
		
		for (WebElement option : dropDownOptions) {
			selectedOption = option.getText();
			// if selected option is not already selected OR selected option is not empty
			if (!((selectedOption.equalsIgnoreCase(OriginalSelectedOption)) || (selectedOption.equalsIgnoreCase("")))) {

				carEntertainmentTypeDropDown.selectByVisibleText(selectedOption);
				break;

			}

		}
		
		CarEnteretainmentSystemResponseSaveButton.click();//save button same for all edit forms
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		Assert.assertEquals(carEntertainmentTypeResponseColumnValue.getText(), selectedOption);
		carEntertainmentTypeEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		Assert.assertEquals(carEntertainmentTypeEditLogPreviousResponseValue.getText(), oldResponseValue);
		TimeUnit.SECONDS.sleep(3);
	}
	

	
	
	
	public void MultiPickQuestionType() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		
		//Fetching text from old response list
		List<String> oldResponseValueTexts=new ArrayList<>();
		for(WebElement item:multiPickQuestionResponseColumnValue) {
			oldResponseValueTexts.add(item.getText());
		}
		
		EditResponsemultiPickQuestion.click();	
		logger.info("Click Edit response link");
		TimeUnit.SECONDS.sleep(5);
		
		//List of already selected check boxes
		List<WebElement> alreadySelected=driver.findElements(By.xpath("//div[contains(@id,'questionResponseForm')]//ul/li[@class='ui-selectlistbox-item ui-corner-all ui-state-highlight']//span"));
		//Deselect these check boxes
		for(WebElement item:alreadySelected) {
			item.click();
		}
		
		List<WebElement> ResponseCheckBoxes=driver.findElements(By.xpath("//div[contains(@id,'questionResponseForm')]//ul/li"));
		List<String> selectedTextBoxesTexts = new ArrayList<>();
		int count=0;
		for(WebElement item:ResponseCheckBoxes) {
			if(oldResponseValueTexts.contains(item.getText())) {
				count++;
				continue;
			}
			else {
				String responseText=item.getText();
				selectedTextBoxesTexts.add(responseText);
				driver.findElement(By.xpath("//div[contains(@id,'questionResponseForm')]//ul/li[text()='"+responseText+"']//span")).click();
			}
		}
		//If all 4 options were selected previously then deselecting all and selecting the two options as follows
		if(count==4){
			driver.findElement(By.xpath("//div[contains(@id,'questionResponseForm')]//ul/li[text()='BACK LEFT']//span")).click();
			driver.findElement(By.xpath("//div[contains(@id,'questionResponseForm')]//ul/li[text()='FRONT LEFT']//span")).click();
			selectedTextBoxesTexts.add("BACK LEFT");
			selectedTextBoxesTexts.add("FRONT LEFT");			
		}

		
		CarEnteretainmentSystemResponseSaveButton.click();//save button same for all edit forms
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> updatedResponseColumn=driver.findElements(By.xpath("//td[text()='Indicate which tyres are in bad condition']/parent::tr/td[2]/ul/li/span"));
		List<String> updatedResponseColumnTexts=new ArrayList<>();
		for(WebElement item:updatedResponseColumn) {
			updatedResponseColumnTexts.add(item.getText());
		}
		
		//Sorting and asserting updated list and selected check box list
		Collections.sort(updatedResponseColumnTexts);
		Collections.sort(selectedTextBoxesTexts);
		Assert.assertEquals(updatedResponseColumnTexts, selectedTextBoxesTexts);
		logger.info("Updated successfully");
		
		multiPickQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		logger.info("Clicked Edit log icon");
		
		//Fetching text from edit log response list
		
		List<WebElement> PreviousResponseValue=driver.findElements(By.xpath("//td[text()='Indicate which tyres are in bad condition']/parent::tr/following-sibling::tr[1]//thead[contains(@id,'editLog_head')]/following-sibling::tbody/tr[1]/td[5]//li"));		
		List<String> previousResponseValueTexts=new ArrayList<>();
		for(WebElement item:PreviousResponseValue) {
			previousResponseValueTexts.add(item.getText());
		}
		
		
		Collections.sort(updatedResponseColumnTexts);
		Collections.sort(previousResponseValueTexts);
		Assert.assertEquals(previousResponseValueTexts, oldResponseValueTexts);
		logger.info("Edit log verified successfully");		
		
	}
	
	public void DateQuestion() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		
		String oldResponseValue=DateQuestionResponseColumnValue.getText();
		
		logger.info("Clicked Edit Response icon");
		EditResponseDateQuestion.click();
		TimeUnit.SECONDS.sleep(5);
		
		
		logger.info("Entered the date");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat simpleformat = new SimpleDateFormat("MM/dd/yyyy");
		DateQuestionInputField.clear();
		DateQuestionInputField.sendKeys(simpleformat.format(cal.getTime()));
		
		simpleformat = new SimpleDateFormat("MMM d,yyyy");
		StringBuilder sb = new StringBuilder(simpleformat.format(cal.getTime()));
		sb.insert(7, " ");
		String selectedDate=sb.toString();		
		
		logger.info("Clicked Save button");
		CarEnteretainmentSystemResponseSaveButton.click();//save button same for all edit forms
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		
		//Validating the input date on edit screen and updated date on view screen
		logger.info("Validate the Expected and Actual Response value");
		Assert.assertEquals(DateQuestionResponseColumnValue.getText(), selectedDate);
		
		logger.info("Click Edit Log icon");
		DateQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		
		logger.info("Validate the Edit log record generated successfully");
		Assert.assertEquals(DateQuestionEditLogPreviousResponseValue.getText(), oldResponseValue);		
		
		
	}
	
	public void TimeQuestion() throws InterruptedException {
		logger.info("Navigating to Detailed View");
		detailedViewicon.click();
		TimeUnit.SECONDS.sleep(5);
		
		String oldResponseValue=TimeQuestionResponseColumnValue.getText();
	
		logger.info("Clicked Edit Response icon");
		EditResponseTimeQuestion.click();
		TimeUnit.SECONDS.sleep(5);
		
		
		logger.info("Entered the time");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat simpleformat = new SimpleDateFormat("hh:mm");
		String selectedTime=simpleformat.format(cal.getTime());
		TimeQuestionInputField.clear();
		TimeQuestionInputField.sendKeys(selectedTime);
		
		logger.info("Clicked Save button");
		CarEnteretainmentSystemResponseSaveButton.click();//save button same for all edit forms
		WebDriverWait wait=new WebDriverWait(driver, 120);		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		
		//Validating the input date on edit screen and updated date on view screen
		logger.info("Validate the Expected and Actual Response value");
		Assert.assertEquals(TimeQuestionResponseColumnValue.getText().substring(0, 5), selectedTime);
		
		logger.info("Click Edit Log icon");
		TimeQuestionEditLogIcon.click();
		TimeUnit.SECONDS.sleep(5);
		
		logger.info("Validate the Edit log record generated successfully");
		Assert.assertEquals(TimeQuestionEditLogPreviousResponseValue.getText(), oldResponseValue);	
	
	}
	
}
