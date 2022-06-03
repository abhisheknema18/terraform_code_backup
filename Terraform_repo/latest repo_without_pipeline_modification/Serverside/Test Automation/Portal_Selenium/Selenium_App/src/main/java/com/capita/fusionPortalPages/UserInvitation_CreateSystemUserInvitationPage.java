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

public class UserInvitation_CreateSystemUserInvitationPage extends Basepage {
	public UserInvitation_CreateSystemUserInvitationPage() {
		PageFactory.initElements(driver, this);
	}
	
	// Locators used to navigate to the Create System User Invite menu
	@FindBy(xpath = "//span[text()='Admin']")
	WebElement adminTab;

	@FindBy(xpath = "//a[text()='User Invitation']")
	WebElement userInvitationMenu;

	@FindBy(xpath = "//a[text()='Create System User Invitation']")
	WebElement addSystemUserInviteSubMenu;
	
	@FindBy(xpath = "//span[text()='Create System User Invitation']")
	WebElement createSystemUserInvitationTitle;

	// Locators for Text Areas - Short Description and Invite Notes
	@FindBy(xpath = "//input[@id='addSysUserInvitationForm:shortDescription']")
	WebElement shortDescriptionInputBox;

	// Locators for the Expiry Dropdown and input box
	@FindBy(xpath = "//label[text()='Expiry']/following::select[contains(@id,'addSysUserInvitationForm')][1]//following::label[1]")
	WebElement dropDownBoxExpiry;

	@FindBy(xpath = "//input[contains(@id,'addSysUserInvitationForm:expiryDateReadOnly')]")
	WebElement expiryDateBox;

	@FindBy(xpath = "//table[contains(@id,'addSysUserInvitationForm')]//tr[3]/td[2]/textarea")
	WebElement invitationNotesTextArea;

	@FindBy(xpath = "//input[@id='addSysUserInvitationForm:userCodeAdd']")
	WebElement userCodeInputBox;

	@FindBy(xpath = "//input[@id='addSysUserInvitationForm:userName']")
	WebElement userNameInputBox;

	@FindBy(xpath = "//label[text()='Expiry']/following::select[contains(@id,'addSysUserInvitationForm')][1]")
	WebElement dropDownExpiry;

	// save, cancel
	@FindBy(xpath = "//button[contains(@id,'addSysUserInvitationForm')]/span[text()='Save']")
	WebElement saveButton;

	@FindBy(xpath = "//button[contains(@id,'addSysUserInvitationForm')]/span[text()='Cancel']")
	WebElement cancelButton;

	// Locators for copy and close
	@FindBy(xpath = "//textarea[@id='inviteTokenId']")
	WebElement tokenTextArea;

	@FindBy(xpath = "//div[@id='showUserInvitation']//span[text()='Copy and Close']")
	WebElement copyAndCloseButton;
	
	@FindBy(xpath = "//div[@role='alert']/div/span")
	WebElement actualSystemUserCreationSuccessMessage;
	
	// Locators for validations
	@FindBy(xpath = "//div[@id='addSysUserInvitationForm:messages']//span[text()='Please enter a Short Description']")
	WebElement shortDescriptionValidationMessage;

	@FindBy(xpath = "//div[@id='addSysUserInvitationForm:messages']//span[text()='Invitation Notes can not be empty']")
	WebElement invitationNotesValidationEmptyMessage;

	@FindBy(xpath = "//div[@id='addSysUserInvitationForm:messages']//span[text()='User Code is invalid']")
	WebElement userCodeValidationMessage;

	@FindBy(xpath = "//div[@id='addSysUserInvitationForm:messages']//span[text()='User Name is invalid']")
	WebElement userNameValidationMessage;

	@FindBy(xpath = "//div[@id='addSysUserInvitationForm:messages']//span[contains(text(),'System User belonging to an SB Access Group.')]")
	WebElement windowsLoginValidationMessage;

	@FindBy(xpath = "(//label[text()='User Name']/following::select[contains(@id,'addSysUserInvitationForm')])[2]")
	WebElement DropDownPortalAccessGroup;

	@FindBy(xpath = "(//label[text()='User Name']/following::select[contains(@id,'addSysUserInvitationForm')])[3]")
	WebElement DropDownSbAccessGroup;

	// Locator for click the Portal Access Group
	@FindBy(xpath = "//*[contains(@id, 'addSysUserInvitationForm:fdmAccessGroup_label')]")
	WebElement clickPortalAccessGroup;

	// Locator for selection of first Portal Access Group
	@FindBy(xpath = "//*[contains(@id, 'addSysUserInvitationForm:fdmAccessGroup_items')]//li[2]")
	WebElement selectPortalAccessGroup;

	// Locator for click the SB Access Group
	@FindBy(xpath = "//*[contains(@id, 'addSysUserInvitationForm:sbAccessGroup_label')]")
	WebElement clickSbAccessGroup;

	// Locator for Windows Login
	@FindBy(xpath = "//*[contains(@id, 'addSysUserInvitationForm:windowsLoginInput')]")
	WebElement windowsLoginInputBox;

	// Locator for User Class
	@FindBy(xpath = "//*[contains(@id, 'addSysUserInvitationForm:editUserClass')]")
	WebElement userClassInputBox;
	
	@FindBy(xpath = "(//*[contains(@class, 'r ui-state-default ui-corner-right')]//span)[9]")
	WebElement userClassListBox;

	// Locator for Admin checkbox
	@FindBy(xpath = "//*[contains(@id, 'addSysUserInvitationForm:adminUser')]//span")
	WebElement adminCheckBox;

	// Locator for Loading Bar
	@FindBy(xpath = "//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;

	WebDriverWait wait = new WebDriverWait(driver, 10);

	// Navigate to the Create System User Invite sub menu after checking if user has
	// access
	public void navigateToCreateSystemUserInvite() throws InterruptedException {
		if (adminTab.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			adminTab.click();
			TimeUnit.SECONDS.sleep(3);

			if (userInvitationMenu.isDisplayed()) {
				logger.info("User has access to Access User Invite Module");
				userInvitationMenu.click();
				TimeUnit.SECONDS.sleep(3);
				addSystemUserInviteSubMenu.click();
				createSystemUserInvitationTitle.isDisplayed();
				logger.info("click System User Invite sub menu");
				TimeUnit.SECONDS.sleep(3);
			} else {
				logger.info("User has no access to create a new System user token");
			}

		} else {
			logger.info("Logged In User is not an Admin User");
		}
	}

	String random = "";
	String randomSystemUser = "";
	
	// Enter Short Description in the text area using randomly generated characters
	public void enterShortDescription(String text)
		throws InterruptedException, UnsupportedFlavorException, IOException {
		shortDescriptionInputBox.sendKeys(text);
		logger.info("Enter Short description");
	}

	// Select Expiry date from dropdown and validate with readonly textbox
	public void selectAndVerifyExpiryDate() throws InterruptedException {

		Select expiryDropDown = new Select(dropDownExpiry);
		List<WebElement> expirydropDownOptions = expiryDropDown.getOptions();

		List<String> expirydropDownOptionValues = new ArrayList<>();

		for (WebElement option : expirydropDownOptions) {
			expirydropDownOptionValues.add(option.getAttribute("value"));
		}

		// generate random value from list
		Random randomizer = new Random();
		random = expirydropDownOptionValues.get(randomizer.nextInt(expirydropDownOptionValues.size()));

		// Select is not working for expiry drop down so handled it with click
		dropDownBoxExpiry.click();
		TimeUnit.SECONDS.sleep(5);
		if (random.equalsIgnoreCase("1")) {
			String DropDownValueXpath = "//div[@id='addSysUserInvitationForm:j_idt1111_panel']//ul/li[text()='" + "+ "
					+ random + " Month " + "']";
			driver.findElement(By.xpath(DropDownValueXpath)).click();
			TimeUnit.SECONDS.sleep(5);
			logger.info("Selected a value from expiry drop down");
		} else {
			String dropDownValueXpath = "//div[@id='addSysUserInvitationForm:j_idt1111_panel']//ul/li[text()='" + "+ "
					+ random + " Months " + "']";
			driver.findElement(By.xpath(dropDownValueXpath)).click();
			TimeUnit.SECONDS.sleep(5);
			logger.info("Selected a value from expiry drop down");
		}
		// Adding months and days to the current date
		Calendar calendar = Calendar.getInstance();

		// Add "random" months to the Calendar to get the expected Expiry Date
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d, yyyy");
		calendar.add(Calendar.MONTH, Integer.parseInt(random));

		String expectedExpiryDate = simpleformat.format(calendar.getTime());
		String ActualexpiryDate = expiryDateBox.getAttribute("value");

		// Verifying the actual and expected expiry date
		Assert.assertEquals(ActualexpiryDate, expectedExpiryDate);
		logger.info("Expiry date verified successfully");
	}

	// Enter Invitation notes in the text area using randomly generated characters
	public void enterInvitationNotes(String text) throws InterruptedException {
		invitationNotesTextArea.sendKeys(text);
		logger.info("Entered token notes");
	}

	// Enter User Code in the text area using randomly generated characters
	public void enterUserCode(String text) throws InterruptedException {
		userCodeInputBox.sendKeys(text);
		logger.info("Entered User Code ");
	}

	// Enter User Name in the text area using randomly generated characters
	public void enterUserName(String text) throws InterruptedException {
		userNameInputBox.sendKeys(text);
		logger.info("Entered User Name");
	}

	// Select the Portal Access Group from the list
	public void selectPortalAccessGroup() throws InterruptedException {
		clickPortalAccessGroup.click();
		TimeUnit.SECONDS.sleep(2);
		selectPortalAccessGroup.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Portal Access Group is selected");
	}

	// Select the SB Access Group from the list
	public void selectSbAccessGroup(int index) throws InterruptedException {
		clickSbAccessGroup.click();
		TimeUnit.SECONDS.sleep(2);
		driver.findElement(By.xpath("//*[contains(@id, 'addSysUserInvitationForm:sbAccessGroup_panel')]//li["+index+"]")).click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("SB Access Group is selected");
	}
	
	// Enter Windows Login in the text area using randomly generated characters
	public void enterWindowsLogin(String text) throws InterruptedException {
		windowsLoginInputBox.sendKeys(text);
		logger.info("Entered User Name");
	}

	// If User Class is type of dropdown list else enter random characters in text field
	public void enterUserClass(String text) throws InterruptedException, IOException {
	try 
		{
				if(userClassListBox.isDisplayed())
				{
					WebElement userclass= driver.findElement(By.xpath("(//div[contains(@id, 'addSysUserInvitationForm')][@role='combobox'])[4]"));
					
					// Retrieving html attribute value using getAttribute() method
					String typeValue=userclass.getAttribute("role");
					
					if(typeValue.equalsIgnoreCase("combobox"))
					{
						driver.findElement(By.xpath("//*[contains(@id,'addSysUserInvitationForm:userClass')]")).click();
						List<WebElement> visibleUserClass=driver.findElements(By.xpath("((//ul[contains(@id,'addSysUserInvitationForm') and @role='listbox'])[3])//li"));
						if(visibleUserClass.size()!=0)
						{
							driver.findElement(By.xpath("((//ul[contains(@id,'addSysUserInvitationForm') and @role='listbox'])[3])//li[3]")).click();
							logger.info("User Class is selected from the dropdown list");
						}
						else
						{
							logger.info("Unable to select the User class from the dropdown list");
						}
					}
				}
		}
		catch(Exception e){
				userClassInputBox.sendKeys(text);
				logger.info("Entered User Name");
		}	
	}

	// Click on the Admin checkbox
	public void clickAdminCheckbox() throws InterruptedException {
		adminCheckBox.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Admin checkbox is selected");
	}

	// Click on the save button
	public void clickSaveButton() throws InterruptedException {
		saveButton.click();
		logger.info("Clicked Save button for Creating System user");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}

	// Click on Cancel button
	public void clickCancelButton() throws InterruptedException {
		cancelButton.click();
		logger.info("Cancelled Add Access Token for System user");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}

	// Copy the generated access token and compare with clipboard text
	public void clickCopyAndCloseButton() throws UnsupportedFlavorException, IOException, InterruptedException {
		String Generatedtoken = tokenTextArea.getText();
		copyAndCloseButton.click();
		logger.info("Clicked Copy and Close button");

		// Create a clipboard object using getSystemClipboard() method
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

		// Get data stored in the clipboard that is in the form of a string (text)
		String clipboardTokenText = (String) c.getData(DataFlavor.stringFlavor);

		// Verify the Generated token and copied token
		Assert.assertEquals(Generatedtoken, clipboardTokenText);
		logger.info("Generated token copied successfully");
	}
	
	public void verifySystemUserCreationSuccessMessage() throws IOException, InterruptedException
	{
		String expectedSuccessMessage= "System User Invitation copied to clipboard";
		String actualSystemUserCreationSuccessMessage= driver.findElement(By.xpath("//div[@role='alert']/div/span")).getText();
		
		Assert.assertEquals(actualSystemUserCreationSuccessMessage, expectedSuccessMessage);
		logger.info("Actual andExpected System User Creation is matched successfully");
	
	}

	public void validateShortDescriptionErrorMessage() throws InterruptedException, IOException{
		logger.info("Clicked Save button for field validation- Short Description");
		Assert.assertTrue(shortDescriptionValidationMessage.isDisplayed());
		logger.info("Mandatory field Short Description is validated for invalid input");

	}

	public void validateInvitationNotesErrorMessage() throws InterruptedException, IOException{
		logger.info("Clicked Save button for field validation- Invitation Notes");
		Assert.assertTrue(invitationNotesValidationEmptyMessage.isDisplayed());
		logger.info("Mandatory field Invitation Notes is validated for invalid input");

	}

	public void validateUserCodeErrorMessage() throws InterruptedException, IOException{
    	logger.info("Clicked Save button for field validation- User Code");
		Assert.assertTrue(userCodeValidationMessage.isDisplayed());
		logger.info("Mandatory field User Code is validated for invalid input");	
    }

	public void validateUserNameErrorMessage() throws InterruptedException, IOException{
		logger.info("Clicked Save button for field validation- User Name");
		Assert.assertTrue(userNameValidationMessage.isDisplayed());
		logger.info("Mandatory field User Name is validated for invalid input");
	}
	
	public void validateWindowsLoginErrorMessage() throws InterruptedException, IOException {
		logger.info("Clicked Save button for field validation- Windows Login");
		Assert.assertTrue(windowsLoginValidationMessage.isDisplayed());
		logger.info("Mandatory field for System User belongs to SB Access Group");
	}

	public void verifyShortDescriptionMaxLengthProperty() throws InterruptedException{
		shortDescriptionInputBox.sendKeys(RandomStringUtils.randomAlphabetic(51));
		String ShortDescriptionMaxLength = shortDescriptionInputBox.getAttribute("maxlength");
		Assert.assertEquals(ShortDescriptionMaxLength, "50");
		logger.info("Max length of the field Short Description is" + ShortDescriptionMaxLength + " as expected");
	}
	
	public void verifyExpiryDateReadOnlyProperty() throws InterruptedException{
		String ExpiryDateReadonly = expiryDateBox.getAttribute("readonly");
		Assert.assertEquals(ExpiryDateReadonly, "true");
		logger.info("Field expiry Date is Read Only");
	}
	
	public void verifyInvitationNotesMaxLengthProperty() throws InterruptedException{
		invitationNotesTextArea.sendKeys(RandomStringUtils.randomAlphabetic(251));
		String invitationNotesMaxLength = invitationNotesTextArea.getAttribute("maxlength");
		Assert.assertEquals(invitationNotesMaxLength, "250");
		logger.info("Max length of the field Invitation Notes is" + invitationNotesMaxLength + " as expected");
	}
	
	public void verifyUserCodeMaxLengthProperty() throws InterruptedException{
		userCodeInputBox.sendKeys(RandomStringUtils.randomAlphabetic(11));
		String userCodeMaxLength = userCodeInputBox.getAttribute("maxlength");
		Assert.assertEquals(userCodeMaxLength, "10");
		logger.info("Max length of the field User Code is" + userCodeMaxLength + " as expected");
	}
	
	public void verifyUserNameMaxLengthProperty() throws InterruptedException{
		userNameInputBox.sendKeys(RandomStringUtils.randomAlphabetic(41));
		String userNameMaxLength = userNameInputBox.getAttribute("maxlength");
		Assert.assertEquals(userNameMaxLength, "40");
		logger.info("Max length of the field User Name is" + userNameMaxLength + " as expected");
	}
	
	public void verifyWindowsLoginMaxLengthProperty() throws InterruptedException{
		windowsLoginInputBox.sendKeys(RandomStringUtils.randomAlphabetic(33));
		String windowsLoginMaxLength = windowsLoginInputBox.getAttribute("maxlength");
		Assert.assertEquals(windowsLoginMaxLength, "32");
		logger.info("Max length of the field Windows Login is" + windowsLoginMaxLength + " as expected");
	}
	
	// If User Class is type of textbox validates max length else ignore it
	public void verifyUserClassTextMaxLengthProperty() throws InterruptedException{
		try {
			if(userClassListBox.isDisplayed())
			{
				logger.info("User Class is a dropdown list");
			}	
		}
		catch(Exception e)
		{
			userClassInputBox.sendKeys(RandomStringUtils.randomAlphabetic(81));
			String userClassMaxLength = userClassInputBox.getAttribute("maxlength");
			Assert.assertEquals(userClassMaxLength, "80");
			logger.info("Max length of the field User Class is" + userClassMaxLength + " as expected");
		
		}
	}	
}