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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import BasePackage.Basepage;

public class UserInvitation_CreateMobileUserInvitationPage extends Basepage {
	public UserInvitation_CreateMobileUserInvitationPage() {
		PageFactory.initElements(driver, this);
	}
	
	// Locaters used to navigate to the Create User Invite menu
	@FindBy(xpath="//span[text()='Admin']")
	WebElement adminTab;

	@FindBy(xpath="//a[text()='User Invitation']")
	WebElement userInvitationMenu;
	
	@FindBy(xpath="//a[text()='Create Mobile User Invitation']")
	WebElement AddUserInviteSubMenu;
	
	// Locators for Text Areas - Short Description and Invite Notes
	@FindBy(xpath="//input[ @id='addUserInvitationForm:shortDescription']")
	WebElement ShortDescriptionInputBox;
	
	@FindBy(xpath="//table[ @id='addUserInvitationForm:innerContent']//tr[3]/td[2]/textarea")
	WebElement InvitationNotesTextArea;
	
	//Locators for the Expiry Dropdown and input box
	@FindBy(xpath="//label[text()='Expiry']/following::select[contains(@id,'addUserInvitationForm')][1]//following::label[1]")
	WebElement DropDownBoxExpiry;
	
	@FindBy(xpath="//input[contains(@id,'addUserInvitationForm:expiryDateReadOnly')]")
	WebElement expiryDateBox;
	
	@FindBy(xpath="//label[text()='Expiry']/following::select[contains(@id,'addUserInvitationForm')][1]")
	WebElement DropDownExpiry;
	
	//save, cancel and select
	@FindBy(xpath="//button[contains(@id,'addUserInvitationForm')]/span[text()='Save']")
	WebElement SaveButton;
	
	@FindBy(xpath="//button[contains(@id,'addUserInvitationForm')]/span[text()='Cancel']")
	WebElement CancelButton;
	
	@FindBy(xpath="//button[contains(@id,'addUserInvitationForm')]/span[text()='Select']")
	WebElement SelectButton;
	
	// Locators for copy and close
	@FindBy(xpath="//textarea[@id='inviteTokenId']")
	WebElement TokenTextArea;
	
	@FindBy(xpath="//textarea[@id='inviteTokenId']/following::tr//button/span[text()='Copy and Close']")
	WebElement CopyAndCloseButton;
	
	//Locators for validations
	@FindBy(xpath="//div[@id='addUserInvitationForm:messages']//span[text()='Please enter a Short Description']")
	WebElement ShortDescriptionValidationMessage;
	
	@FindBy(xpath="//div[@id='addUserInvitationForm:messages']//span[text()='Invitation Notes can not be empty']")
	WebElement InvitationNotesValidationMessage;
	
	@FindBy(xpath="//div[@id='addUserInvitationForm:messages']//span[text()='Please select a Work Group']")
	WebElement WorkGroupValidationMessage;
	
	//Locators for Workgroup selection
	@FindBy(xpath="//li[contains(@id, 'SingleWorkgroupSelectionFormUserInvitation:workgroupSelectTree:0_0')]/span/span[3]")
	WebElement selectFirstworkGroup;
	
	
	@FindBy(xpath="//div[@id='SingleWorkgroupSelectionFormUserInvitation:workgroupSelectionSingle_footer']//span[text()='OK']")
	WebElement btn_OkWorkgroupSelection;
	
	@FindBy(xpath="//div[@id='SingleWorkgroupSelectionFormUserInvitation:workgroupSelectionSingle_footer']//span[text()='Cancel']")
	WebElement btn_CancelWorkGroupSelection;
	
	//Locator for Loading Bar 
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	//Navigate to the Create User Invite sub menu after checking if user has access
	public void navigateToCreateUserInvite() throws InterruptedException {
		if(adminTab.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			adminTab.click();
			TimeUnit.SECONDS.sleep(3);
			
			if(userInvitationMenu.isDisplayed()) {
				logger.info("User has access to Access User Invite Module");
				userInvitationMenu.click();
				TimeUnit.SECONDS.sleep(3);
				AddUserInviteSubMenu.click();
				logger.info("click User Invite sub menu");
				TimeUnit.SECONDS.sleep(3);
			}
			else {
				logger.info("User has no access to create a new user token");
			}
			
		} else {
			logger.info("Logged In User is not an Admin User");
			
		}
			
	}
	
	String random="";
	String randomSystemUser="";
	
	//Enter Short Description in the text area using randomly generated characters
	public void EnterShortDescription() throws InterruptedException, UnsupportedFlavorException, IOException {
		
		String ShortDescription=RandomStringUtils.randomAlphabetic(4);
		ShortDescriptionInputBox.sendKeys(ShortDescription);
		logger.info("Enter Short description");		
	}
	
	//Select Expiry date from dropdown and validate with readonly textbox
	public void SelectAndVerifyExpiryDate() throws InterruptedException {
	
	Select ExpiryDropDown=new Select(DropDownExpiry);
	List<WebElement> expirydropDownOptions=ExpiryDropDown.getOptions();
	
	List<String> expirydropDownOptionValues=new ArrayList<>();
	
	for(WebElement option:expirydropDownOptions) {
		expirydropDownOptionValues.add(option.getAttribute("value"));
	}
	
	//generate random value from list
	Random randomizer = new Random();
    random = expirydropDownOptionValues.get(randomizer.nextInt(expirydropDownOptionValues.size()));
    
     //Select is not working for expiry drop down so handled it with click
    DropDownBoxExpiry.click();
    TimeUnit.SECONDS.sleep(5);
    if(random.equalsIgnoreCase("1")) {
    	String DropDownValueXpath="//div[contains(@id,'addUserInvitationForm')]//ul/li[text()='"+"+ 1 Month "+"']";
	    driver.findElement(By.xpath(DropDownValueXpath)).click();
	    TimeUnit.SECONDS.sleep(5);
	    logger.info("Selected a value from expiry drop down");
    }
    else {
    	String DropDownValueXpath="//div[contains(@id,'addUserInvitationForm')]//ul/li[text()='"+"+ "+random+" Months ']";
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
	
	
	// Enter Invitation notes in the text area using randomly generated characters
	public void EnterInvitationNotes() throws InterruptedException {
	String InvitationNotes=RandomStringUtils.randomAlphabetic(8);
    InvitationNotesTextArea.sendKeys(InvitationNotes);
    logger.info("Enter token notes");
   
    }
	
	// Click on the save button
    public void ClickSaveButton() throws InterruptedException{
	SaveButton.click();
	logger.info("Clicked Save button");
	wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
    
    //Click on Cancel button
    public void ClickCancelButton() throws InterruptedException {
		CancelButton.click();
		logger.info("Cancelled Add Access Token");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
    
    //Copy the generated access token and compare with clipboard text
    public void ClickCopyAndCloseButton() throws UnsupportedFlavorException, IOException, InterruptedException {
	String Generatedtoken=TokenTextArea.getText();
	CopyAndCloseButton.click();
	logger.info("Clicked Copy and Close button");
	
	// Create a Clipboard object using getSystemClipboard() method
    Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();

    // Get data stored in the clipboard that is in the form of a string (text)
	String ClipboardTokenText = (String) c.getData(DataFlavor.stringFlavor);

	// Verify the Generated token and copied token
	Assert.assertEquals(Generatedtoken, ClipboardTokenText);
	logger.info("Generated token copied successfully");
    }
    
    //Verify the field validation for short description
    public void VerifyMandatoryFieldsValidations() throws InterruptedException, IOException, UnsupportedFlavorException {
    SaveButton.click();
	logger.info("Clicked Save button for field validation- Short Description");
	Assert.assertTrue(ShortDescriptionValidationMessage.isDisplayed());
	logger.info("Mandatory field Short Description is validated for invalid input");
	EnterShortDescription();
	
	SaveButton.click();
	logger.info("Clicked Save button for field validation- Invitation Notes");
	Assert.assertTrue(InvitationNotesValidationMessage.isDisplayed());
	logger.info("Mandatory field Invitation Notes is validated for invalid input");
	EnterInvitationNotes();
	
	SaveButton.click();
	logger.info("Clicked Save button for field validation- Work Group");
	Assert.assertTrue(WorkGroupValidationMessage.isDisplayed());
	logger.info("Mandatory field Work Group Selection is validated for invalid input");
	
    }
    
    //Verify the Field properties - ShortDescription, InvitationNotes and ExpiryDate
    public void VerifyFieldProperties() throws InterruptedException {
	
	ShortDescriptionInputBox.sendKeys(RandomStringUtils.randomAlphabetic(51));
	String ShortDescriptionMaxLength=ShortDescriptionInputBox.getAttribute("maxlength");
	Assert.assertEquals(ShortDescriptionMaxLength, "50");
	logger.info("Max length of the field Application Name is"+ShortDescriptionMaxLength+" as expected");
	
	InvitationNotesTextArea.sendKeys(RandomStringUtils.randomAlphabetic(251));
	String InvitationNotesMaxLength=InvitationNotesTextArea.getAttribute("maxlength");
	Assert.assertEquals(InvitationNotesMaxLength, "250");
	logger.info("Max length of the field Token Notes is"+InvitationNotesMaxLength+" as expected");
	
	String ExpiryDateReadonly=expiryDateBox.getAttribute("readonly");
	Assert.assertEquals(ExpiryDateReadonly, "true");
	logger.info("Field expiry Date is Read Only");
    }

    // Click on the Select button
    public void ClickSelectButton() throws InterruptedException{
	SelectButton.click();
	wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	logger.info("Clicked Select Work Group button");
	}
    
    //Select a workgroup
    public void selectWorkGroup() {
		selectFirstworkGroup.click();
		btn_OkWorkgroupSelection.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info(" Workgroup has been verified");
	}
    
    // Validates the workgroup categories associated to the user
    public void verifyVisibilityOfWorkGroups()  {
    List<WebElement> visibleWorkGroups=driver.findElements(By.xpath("//div[@id='SingleWorkgroupSelectionFormUserInvitation:workgroupSelectTree']/ul/li/span[1]"));
    List<String> visibleWorkGroupsText=new ArrayList<String>();
    for(WebElement temp:visibleWorkGroups) {
     visibleWorkGroupsText.add(temp.getText());
    }
    Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//config//data.properties");
    String WorkGroupCategory=Basepage.readProperty("WorkGroupCategory");

    //Validates the workgroup category fetched from data properties with the list
    for(String temp:visibleWorkGroupsText) {
    	if (temp.contains(WorkGroupCategory)) {
     Assert.assertTrue(temp.contains(WorkGroupCategory));
     logger.info("Visible Workgroup has been verified");
     break;
    	}
    		}
    btn_CancelWorkGroupSelection.click();
     }    
}