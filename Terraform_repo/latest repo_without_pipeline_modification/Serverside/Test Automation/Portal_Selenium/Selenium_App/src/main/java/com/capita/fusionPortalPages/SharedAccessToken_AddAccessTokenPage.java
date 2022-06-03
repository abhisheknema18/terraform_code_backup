package com.capita.fusionPortalPages;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.record.PageBreakRecord.Break;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import utility.CommonUtils;

public class SharedAccessToken_AddAccessTokenPage extends Basepage {
	public SharedAccessToken_AddAccessTokenPage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement adminTab;
	
	@FindBy(xpath="//a[text()='Shared Access Tokens']")
	WebElement sharedAccessTokenMenu;
	
	@FindBy(xpath="//a[text()='System Users']")
	WebElement SystemUsers;
	
	@FindBy(xpath="//a[text()='Add System User']")
	WebElement AddSystemUsersSubMenu;
	
	
	@FindBy(xpath="//a[text()='Add Access Token']")
	WebElement AddAccessTokenSubMenu;
	
	@FindBy(xpath="//td/input[contains(@id,'applicationName')]")
	WebElement ApplicationNameInputBox;
	
	@FindBy(xpath="//td/textarea[contains(@id,'addForm')]")
	WebElement TokenNotesTextArea;
	
	@FindBy(xpath="//button[contains(@id,'addForm')]/span[text()='Save']")
	WebElement SaveButton;
	
	@FindBy(xpath="//button[contains(@id,'addForm')]/span[text()='Cancel']")
	WebElement CancelButton;	
	
	@FindBy(xpath="//label[text()='Linked System User']/following::select[contains(@id,'addForm')][1]")
	WebElement DropDownLinkedSystemUser;
	
	@FindBy(xpath="//label[text()='Linked System User']/following::select[contains(@id,'addForm')][1]//following::label[1]")
	WebElement DropDownBoxLinkedSystemUser;
	
	@FindBy(xpath="//label[text()='Expiry']/following::select[contains(@id,'addForm')][1]")
	WebElement DropDownExpiry;
	
	@FindBy(xpath="//textarea[@id='accessTokenId']")
	WebElement TokenTextArea;
	
	@FindBy(xpath="//button/span[text()='Copy and Close']")
	WebElement CopyAndCloseButton;
	
	@FindBy(xpath="//label[text()='Expiry']/following::select[contains(@id,'addForm')][1]//following::label[1]")
	WebElement DropDownBoxExpiry;
	
	@FindBy(xpath="//td/input[contains(@id,'expiryDateReadOnly')]")
	WebElement expiryDateBox;
	
	@FindBy(xpath="//li/span[text()='Application Name is invalid']")
	WebElement ApplicationNameValidationMessage;
	
	@FindBy(xpath="//td/input[@id='addSystemUserForm:userCodeAdd']")
	WebElement UserCodeTextBox;
	
	@FindBy(xpath="//td/input[@id='addSystemUserForm:userName']")
	WebElement UserNameTextBox;
	
	@FindBy(xpath="//div/label[@id='addSystemUserForm:sbAccessGroup_label']")
	WebElement SbAccessGroupDropDown;
	
	@FindBy(xpath="//ul/li[text()='SB PRIMARY ACCESS GROUP']")
	WebElement SbAccessGroupDropDownValue;
	
	@FindBy(xpath="//div/label[@id='addSystemUserForm:fdmAccessGroup_label']")
	WebElement PortalAccessGroupDropDown;
	
	@FindBy(xpath="//ul/li[text()='INTEGRATION ACCESS (RESERVED)']")
	WebElement PortalAccessGroupDropDownValue;
	
	@FindBy(xpath="//td/label[text()='Admin ']/following::span[1]")
	WebElement AdminCheckBox;
	
	@FindBy(xpath="//button[contains(@id,'addSystemUserForm')]/span[text()='Save']")
	WebElement AddSystemUserSaveButton;
	
	@FindBy(xpath="//li/span[text()='User Saved Successfully']")
	WebElement UserSavedMessage;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//input[@id='addSystemUserForm:windowsLoginInput']")
	WebElement txtBox_WindowsLogin;
	
	
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToAddAccessToken() throws InterruptedException {
		if(adminTab.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			adminTab.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			
			if(sharedAccessTokenMenu.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				sharedAccessTokenMenu.click();
//				TimeUnit.SECONDS.sleep(3);
				AddAccessTokenSubMenu.click();
				logger.info("click Add Access Token sub menu");
				TimeUnit.SECONDS.sleep(3);
			}
			else {
				logger.info("User has no access to Module Shared Access Tokens");
			}
			
		} else {
			logger.info("Logged In User is not an Admin User");
			
		}
			
	}
	
	String random="";
	String randomSystemUser="";
	public void EnterApplicationName() throws InterruptedException, UnsupportedFlavorException, IOException {
		
		String ApplicationName=RandomStringUtils.randomAlphabetic(4);
		ApplicationNameInputBox.sendKeys(ApplicationName);
		logger.info("Enter application name");
//		TimeUnit.SECONDS.sleep(3);
		
	}
	
	public void SelectSystemUser() throws InterruptedException {
//		TimeUnit.SECONDS.sleep(3);
		List<WebElement> userdropDownOptions=driver.findElements(By.xpath("//div[contains(@id,'addForm')][1]/div/ul/li"));
		
		Random randomizer = new Random();
		int index=randomizer.nextInt(userdropDownOptions.size()+1);
		
		//if Random is 1 then xpath text is different
		if(!userdropDownOptions.isEmpty()) {
			DropDownBoxLinkedSystemUser.click();
			TimeUnit.SECONDS.sleep(3);
			driver.findElement(By.xpath("//div[contains(@id,'addForm')][1]/div/ul/li["+index+"]")).click();
			logger.info("System User is Selected");
//			TimeUnit.SECONDS.sleep(3);
		}
		else
			logger.info("No System User is available");
	}
	
	public void EnterTokenNotes() throws InterruptedException {
		String TokenNotes=RandomStringUtils.randomAlphabetic(8);
	    TokenNotesTextArea.sendKeys(TokenNotes);
	    logger.info("Enter token notes");
//	    TimeUnit.SECONDS.sleep(3);
	}

	public void SelectAndVerifyExpiryDate() throws InterruptedException {
		
		Select ExpiryDropDown=new Select(DropDownExpiry);
		List<WebElement> expirydropDownOptions=ExpiryDropDown.getOptions();
		
		List<String> expirydropDownOptionValues=new ArrayList<>();
		
		for(WebElement option:expirydropDownOptions) {
			expirydropDownOptionValues.add(option.getAttribute("value"));
//			expirydropDownOptionValues.add(option.getAttribute("value").substring(1, option.getAttribute("value").indexOf(" ")));
		}
		
		//generate random value from list
		Random randomizer = new Random();
	    random = expirydropDownOptionValues.get(randomizer.nextInt(expirydropDownOptionValues.size()));
	    
	    //Select is not working for expiry drop down so handled it with click
	    DropDownBoxExpiry.click();
	    TimeUnit.SECONDS.sleep(5);
	    if(random.equalsIgnoreCase("1")) {
	    	String DropDownValueXpath="//ul[contains(@id,'addForm')]/li[text()='+ "+random+" Month ']";
		    driver.findElement(By.xpath(DropDownValueXpath)).click();
		    TimeUnit.SECONDS.sleep(5);
		    logger.info("Selected a value from expiry drop down");
	    }
	    else {
	    	String DropDownValueXpath="//ul[contains(@id,'addForm')]/li[text()='+ "+random+" Months ']";
		    driver.findElement(By.xpath(DropDownValueXpath)).click();
		    TimeUnit.SECONDS.sleep(5);
		    logger.info("Selected a value from expiry drop down");
	    }
	    
			//Adding months and days to the current date
			Calendar calendar = Calendar.getInstance();
			
			// Add "random" months to the Calendar to get the expected Expiry Date
			SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d, yyyy");
			calendar.add(Calendar.MONTH, Integer.parseInt(random));
			
			String expectedExpiryDate=simpleformat.format(calendar.getTime());
			String ActualexpiryDate=expiryDateBox.getAttribute("value");
			
			//Verifying the actual and expected expiry date
			Assert.assertEquals(ActualexpiryDate, expectedExpiryDate);
			logger.info("Expiry date verified successfully");
	}
	
	public void ClickSaveButton() throws InterruptedException{
		SaveButton.click();
		logger.info("Clicked Save button");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
//		TimeUnit.SECONDS.sleep(3);
	}
	
	public void ClickCopyAndCloseButton() throws UnsupportedFlavorException, IOException {
		String Generatedtoken=TokenTextArea.getText();
		CopyAndCloseButton.click();
		logger.info("Clicked Copy and Close button");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		// Create a Clipboard object using getSystemClipboard() method
        Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();

        // Get data stored in the clipboard that is in the form of a string (text)
		String ClipboardTokenText = (String) c.getData(DataFlavor.stringFlavor);

		// Verify the Generated token and copied token
		Assert.assertEquals(Generatedtoken, ClipboardTokenText);
		logger.info("Generated token copied successfully");
		
	}
	
	public void VerifyMandatoryFieldsValidations() throws InterruptedException {
		SaveButton.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Clicked Save button");
//		TimeUnit.SECONDS.sleep(2);
		Assert.assertTrue(ApplicationNameValidationMessage.isDisplayed());
		logger.info("Mandatory field Application Name is validated for invalid input");
		
	}
	
	public void VerifyFieldProperties() throws InterruptedException {
		
		ApplicationNameInputBox.sendKeys(RandomStringUtils.randomAlphabetic(51));
		String ApplicationNameMaxLength=ApplicationNameInputBox.getAttribute("maxlength");
		Assert.assertEquals(ApplicationNameMaxLength, "50");
		logger.info("Max length of the field Application Name is"+ApplicationNameMaxLength+" as expected");
		
		TokenNotesTextArea.sendKeys(RandomStringUtils.randomAlphabetic(251));
		String TokenNotesMaxLength=TokenNotesTextArea.getAttribute("maxlength");
		Assert.assertEquals(TokenNotesMaxLength, "250");
		logger.info("Max length of the field Token Notes is"+TokenNotesMaxLength+" as expected");
		
		String ExpiryDateReadonly=expiryDateBox.getAttribute("readonly");
		Assert.assertEquals(ExpiryDateReadonly, "true");
		logger.info("Field expiry Date is Read Only");
	
	}
	
	public void ClickCancelButton() throws InterruptedException {
		CancelButton.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
//		TimeUnit.SECONDS.sleep(2);
		logger.info("Cancelled Add Access Token");
//		TimeUnit.SECONDS.sleep(2);
	}
	
	public void NavigateToCreateSysUser() throws InterruptedException {
//		TimeUnit.SECONDS.sleep(3);
		if(adminTab.isDisplayed()) {
			logger.info("Click Admin tab");
			adminTab.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
//			TimeUnit.SECONDS.sleep(3);
			logger.info("User has access to Shared Access Token Module");
			SystemUsers.click();
//			TimeUnit.SECONDS.sleep(3);
			AddSystemUsersSubMenu.click();
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			logger.info("click Add Access Token sub menu");
//			TimeUnit.SECONDS.sleep(3);
		} else {
			logger.info("User doesn't have access to Shared Access Token Module");
//			TimeUnit.SECONDS.sleep(3);
			
		}
	}
	
	public String CreateSysUser() throws InterruptedException {
		String UserCode="SYS"+RandomStringUtils.randomAlphabetic(4);
		UserCodeTextBox.sendKeys(UserCode);
//		TimeUnit.SECONDS.sleep(3);
		String Username=RandomStringUtils.randomAlphabetic(4);
		UserNameTextBox.sendKeys(Username);
//		TimeUnit.SECONDS.sleep(3);
		SbAccessGroupDropDown.click();
//		TimeUnit.SECONDS.sleep(3);
		SbAccessGroupDropDownValue.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
//		TimeUnit.SECONDS.sleep(3);
		PortalAccessGroupDropDown.click();
//		TimeUnit.SECONDS.sleep(3);
		PortalAccessGroupDropDownValue.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
//		TimeUnit.SECONDS.sleep(3);
		
		txtBox_WindowsLogin.sendKeys(RandomStringUtils.randomAlphabetic(8));
		AdminCheckBox.click();

//		TimeUnit.SECONDS.sleep(3);
		AddSystemUserSaveButton.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		TimeUnit.SECONDS.sleep(8);
		
		return Username;
	}
	
	public void ValidateSysUserInAddAccessToken() throws InterruptedException {
//		TimeUnit.SECONDS.sleep(3);
		String Username=CreateSysUser();
//		TimeUnit.SECONDS.sleep(3);
		navigateToAddAccessToken();		
		DropDownBoxLinkedSystemUser.click();
//		TimeUnit.SECONDS.sleep(3);		
		WebElement sysUser=driver.findElement(By.xpath("//ul/li[text()='"+Username+"']"));
//		TimeUnit.SECONDS.sleep(3);
		sysUser.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
//		TimeUnit.SECONDS.sleep(3);
		Assert.assertEquals(sysUser.isEnabled(), true);
	}
	
	

}
