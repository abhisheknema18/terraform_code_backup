package com.capita.fusionPortalPages;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class SharedAccessToken_AccessTokenListPage extends Basepage {
	public SharedAccessToken_AccessTokenListPage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement adminTab;
	
	@FindBy(xpath="//a[text()='Shared Access Tokens']")
	WebElement sharedAccessTokenMenu;
	
	@FindBy(xpath="//a[text()='View Access Token']")
	WebElement ViewAccessTokenSubMenu;
	
	@FindBy(xpath="//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr")
	List<WebElement> totalTokens;
	
	@FindBy(xpath="//th/span[text()='Application Name']")
	WebElement headerApplicationName;
	
	@FindBy(xpath="//th/span[text()='User Code']")
	WebElement headerUserCode;
	
	@FindBy(xpath="//th/span[text()='Issue Date']")
	WebElement headerIssueDate;
	
	@FindBy(xpath="//th/span[text()='Expiry Date']")
	WebElement headerExpiryDate;
	
	@FindBy(xpath="//a/span[contains(@id,'accessTokenTab')]")
	WebElement accessTokenTab;
	
	@FindBy(xpath="//form[@id='accessTokenTab:accessTokenListForm']/span")
	WebElement totalTokenFound;
	
	@FindBy(xpath="//tr/td/span[@style='color:green']/following::td[5]/a[@style='color:black']")
	WebElement notesIconblackActiveToken;
	
	@FindBy(xpath="//tr/td/span[@style='color:green']/following::td[5]/a[@style='color:red']")
	WebElement notesIconredActiveToken;
	
	@FindBy(xpath="//tr/td/span[@style='color:grey']/following::td[5]/a[@style='color:red']")
	WebElement notesIconRevokedToken;
	
	
	@FindBy(xpath="//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']//tr[1]/td/a[contains(@id,'noteTag') and contains(@style,'color:red')]/following::tr/td[3]")
	WebElement notesNonEmpty;
	
	@FindBy(xpath="//th/span[text()='Application Name']/following::input[1]")
	WebElement InputBoxFilterApplicationName;
	
	@FindBy(xpath="//th/span[text()='User Code']/following::input[1]")
	WebElement InputBoxFilterUserCode;
	
	@FindBy(xpath="//button[@id='revokeForm:revokeAccessTokenBtn']")
	WebElement ButtonRevoke;
	
	@FindBy(xpath="//button[@id='revokeForm:cancelAccessToken']")
	WebElement ButtonCancel;
	
	
	@FindBy(xpath="//form[@id='revokeForm']/table[@id='revokeForm:innerContent']/tbody/tr/td/textarea")
	WebElement TextAreaRevokeNotes;
	
	@FindBy(xpath="//div[@id='revokeForm:messages']//ul/li/span")
	WebElement ErrorMessageEmptyRevokeNotes;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToAddAccessToken() throws InterruptedException {
		if(adminTab.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			adminTab.click();
			TimeUnit.SECONDS.sleep(3);
			
			if(sharedAccessTokenMenu.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				sharedAccessTokenMenu.click();
				TimeUnit.SECONDS.sleep(3);
				ViewAccessTokenSubMenu.click();
				logger.info("click View Access Token sub menu");
				TimeUnit.SECONDS.sleep(3);
			}
			else {
				logger.info("User has no access to Module Shared Access Tokens");
			}
			
		} else {
			logger.info("Logged In User is not an Admin User");
			
		}
	}
	
	public void VerifyTokenListScreen() {
		
		//Verifying if all the columns are displayed
		
		Assert.assertTrue(headerApplicationName.isDisplayed());
		logger.info("Application Name columns is displayed");
		
		Assert.assertTrue(headerUserCode.isDisplayed());
		logger.info("User Code columns is displayed");
		
		Assert.assertTrue(headerIssueDate.isDisplayed());
		logger.info("Issue Date columns is displayed");
		
		Assert.assertTrue(headerExpiryDate.isDisplayed());
		logger.info("Expiry Date columns is displayed");
	}
	
	public void VerifyTokenCount() {
		
		//List Size of total token records on page
		int TotalTokenRecords=totalTokens.size();
		
		//Fetching the count from token count tab at the top of the page
		String tokenCountTabString=accessTokenTab.getText().substring(accessTokenTab.getText().indexOf("(") + 1, accessTokenTab.getText().indexOf(")"));
		int tokenCountTab=Integer.parseInt(tokenCountTabString);
		
		//Verifying the count on tab with the size of the list
		Assert.assertEquals(tokenCountTab, TotalTokenRecords);
		logger.info("Total count of tokens matches with the Total records displayed on Access Token Tab");
		
		//Fetching the count of tokens displayed at the bottom of the screen
		String tokenCountFoundString=totalTokenFound.getText().substring(0,totalTokenFound.getText().indexOf(" "));
		int tokenCountFound=Integer.parseInt(tokenCountFoundString);
		
		//Verifying the count on tokens found with the size of the list
		Assert.assertEquals(tokenCountFound, TotalTokenRecords);
		logger.info("Total count of token matches with the Total records displayed");
		
	}
	
	public void VerifyTokenCountByFilterApplicationName() throws InterruptedException {

		InputBoxFilterApplicationName.sendKeys("k");
		logger.info("Entered a string for Application Name Filter");

		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> NoRecordsMessage = driver
				.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']//td[text()='No records found.']"));
		
		if(NoRecordsMessage.size()==0) {
			//Fetching the list of tokens when input in entered for filter
			List<WebElement> TokenRows = driver
					.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr"));

			int TotalTokenRecords = TokenRows.size();
			
			//fetching the count from tokens tab at the top
			String tokenCountTabString = accessTokenTab.getText().substring(accessTokenTab.getText().indexOf("("));
			int tokenCountTab = Integer.parseInt(tokenCountTabString.substring(1, tokenCountTabString.indexOf(" ")));
			Assert.assertEquals(tokenCountTab, TotalTokenRecords);
			logger.info("Total count of tokens matches with the Total records displayed on Access Token Tab");

			//fetching the count of tokens displayed at the bottom of the page
			String tokenCountFoundString = totalTokenFound.getText().substring(0, totalTokenFound.getText().indexOf(" "));
			int tokenCountFound = Integer.parseInt(tokenCountFoundString);
			Assert.assertEquals(tokenCountFound, TotalTokenRecords);
			logger.info("Total count of token matches with the Total records displayed");
		}
		else {
			logger.info("No Records displayed for entered filter");
		}
	}
	
	public void VerifyTokenCountByFilterUserCode() throws InterruptedException {

		InputBoxFilterUserCode.sendKeys("c");
		logger.info("Entered a string for User Code Filter");

		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> NoRecordsMessage = driver
				.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']//td[text()='No records found.']"));
		
		if(NoRecordsMessage.size()==0) {
			//Fetching the list of tokens when input in entered for filter
			List<WebElement> TokenRows = driver
					.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr"));

			int TotalTokenRecords = TokenRows.size();
			
			//fetching the count from tokens tab at the top
			String tokenCountTabString = accessTokenTab.getText().substring(accessTokenTab.getText().indexOf("("));
			int tokenCountTab = Integer.parseInt(tokenCountTabString.substring(1, tokenCountTabString.indexOf(" ")));
			Assert.assertEquals(tokenCountTab, TotalTokenRecords);
			logger.info("Total count of tokens matches with the Total records displayed on Access Token Tab");

			//fetching the count of tokens displayed at the bottom of the page
			String tokenCountFoundString = totalTokenFound.getText().substring(0, totalTokenFound.getText().indexOf(" "));
			int tokenCountFound = Integer.parseInt(tokenCountFoundString);
			Assert.assertEquals(tokenCountFound, TotalTokenRecords);
			logger.info("Total count of token matches with the Total records displayed");
		}
		else {
			logger.info("No Records displayed for entered filter");
		}
	}
	
	public void clickBlackNotesIcon() {
		if(notesIconblackActiveToken.isDisplayed()) {
			notesIconblackActiveToken.click();
			logger.info("Clicked Black Coloured Notes Icon");
		}
		else
			logger.info("Active token with blank Notes does not exist");
	}
	
	public void clickRedNotesIcon() {
		if(notesIconredActiveToken.isDisplayed()) {
			notesIconredActiveToken.click();
			logger.info("Clicked Red Coloured Notes Icon");
		}
		else
			logger.info("Active token with Notes does not exist");
	}
	
	public void clickNotesIconForRevokedToken() {
		if(notesIconRevokedToken.isDisplayed()) {
			notesIconRevokedToken.click();
			logger.info("Clicked Red Coloured Notes Icon");
		}
		else
			logger.info("Active token with Notes does not exist");
	}
	
	public void VerifyTokenNotes() {
		//Fetching the expander notes field
		WebElement ExpanderNotes=driver.findElement(By.xpath("//span[text()='Token Notes']/following::tbody/tr/td[3]"));
		
		if(!ExpanderNotes.getText().isEmpty()) {
			logger.info("Captured notes available for Red Coloured Notes Icon");
		}
		else
			logger.info("Notes are blank for Black Coloured Notes Icon");
	}
	
	public void VerifyExpanderFieldsActiveToken() throws InterruptedException {

		int countOrange = 0;

		//Traversing from the top to bottom until an Active token is found
		for (int i = 1; i < totalTokens.size(); i++) {
			String Iconcolor = driver
					.findElement(By
							.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[1]/span"))
					.getAttribute("style");

			//If the Token is active then click notes icon and check for the expander fields
			if ((Iconcolor.equalsIgnoreCase("color: orange;")||(Iconcolor.equalsIgnoreCase("color: green;"))) && countOrange == 0) {
				WebElement notesIcon = driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr[" + i + "]/td[6]"));
				notesIcon.click();
				TimeUnit.SECONDS.sleep(3);
				
				//Fetching the expander field Issue Date
				
				WebElement ExpanderIssueDate = driver
						.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/following-sibling::tr//tbody/tr/td[2]"));

				//Fetching the main row field Issue Date for the same token
				
				WebElement RowIssueDate = driver.findElement(By.xpath(
						"//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[3]"));

				//Verifying the Main row and expander fields match

				Assert.assertEquals(ExpanderIssueDate.getText(), RowIssueDate.getText());
				
				break;
			}
		}
	}
	
	public void VerifyColourCodesAsPerExpiryDate() throws ParseException {
		int countOrange=0;
		int countGreen=0;
		int countRed=0;
		int countGrey=0;

		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d, yyyy");
		Calendar calendar = Calendar.getInstance();
		Calendar calendar1 = Calendar.getInstance();
		
		//Traversing from top to the end of the token list
		for(int i=1;i<=totalTokens.size();i++) {
			//Fetching the color code for a token record
			String Iconcolor=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[1]/span")).getAttribute("style");

			//If color is orange check for expiry date is within the 30days
			if(Iconcolor.equalsIgnoreCase("color: orange;")&&countOrange==0) {
				String ExpiryDate=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[4]")).getText();
				Date date1 = simpleformat.parse(ExpiryDate);
				calendar1.setTime(date1);
				calendar.add(Calendar.DAY_OF_MONTH, 30);
				//Check if a date is less than another date
				  Assert.assertTrue(calendar1.before(calendar));
				  countOrange++;
				  logger.info("Token Expiry Within 30 days for Orange icon verified successfully");
			}
			else
				//If color is Green, check for expiry date is after the 30days
				if(Iconcolor.equalsIgnoreCase("color: green;")&&countGreen==0) {
					String ExpiryDate=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[4]")).getText();
					Date date1 = simpleformat.parse(ExpiryDate);
					calendar1.setTime(date1);
					calendar.add(Calendar.DAY_OF_MONTH, 30);
					//Check if a date is less than another date
					  Assert.assertFalse(calendar1.before(calendar));
					  countGreen++;
					  logger.info("Token Expiry more than 30 days for Green icon verified successfully");
				}
				else
					//If color is Red check for expiry date is before current date
					if(Iconcolor.equalsIgnoreCase("color: red;")&&countRed==0) {
						String ExpiryDate=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[4]")).getText();
						Date date1 = simpleformat.parse(ExpiryDate);
						calendar1.setTime(date1);
						//Check if a date is less than another date
						  Assert.assertTrue(calendar1.before(calendar));
						  countRed++;
						  logger.info("Expired Token Verified for Red icon verified successfully");
					}
					else
						//If color is Grey, check for the token is revoked
						if(Iconcolor.equalsIgnoreCase("color: grey;")&&countGrey==0) {
							WebElement RevokeIconDisabled=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/span"));
							Assert.assertEquals(RevokeIconDisabled.isDisplayed(), true);
							countGrey++;
							logger.info("Revoked Token Verified for Grey icon verified successfully");
						}
					
		}
		
	}
	private int i=0;
	private String RevokeNotes="";
	public void clickRevokeIcon() throws InterruptedException {
		//Traversing from top to bottom until the grey colored icon is found
		for (i = 1; i <= totalTokens.size(); i++) {
			String Iconcolor = driver
					.findElement(By
							.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[1]/span"))
					.getAttribute("style");

			if (!Iconcolor.equalsIgnoreCase("color: grey;")) {
				WebElement RevokeIcon = driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/a"));
				TimeUnit.SECONDS.sleep(3);
				RevokeIcon.click();
				logger.info("Clicked Revoke Icon");
				TimeUnit.SECONDS.sleep(3);
				break;
			}
		}
		
	}
	
	public void EnterRevokeNotes() {
		RevokeNotes=RandomStringUtils.randomAlphabetic(5).toLowerCase();

		//Sendkeys Is not Working hence used Actions class
		Actions builder = new Actions(driver);
        Action TextAreaTextType = builder.sendKeys(TextAreaRevokeNotes, RevokeNotes).build();
        TextAreaTextType.perform();
        logger.info("Entered Revoke Notes");
        
        
	}
	
	public void submitRevokeToken() throws InterruptedException {
		TimeUnit.SECONDS.sleep(3);
		ButtonRevoke.click();
		logger.info("Clicked Submit Button");
		TimeUnit.SECONDS.sleep(3);
	}
	
	public void VerifyTokenRevoked() throws InterruptedException {
		TimeUnit.SECONDS.sleep(3);
		
		WebElement RevokedTokenRecord=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/span"));
		Assert.assertTrue(RevokedTokenRecord.isDisplayed());
		logger.info("Token Revoked Successfully");
		
		WebElement RevokedTokenRow=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]"));
		Assert.assertTrue(RevokedTokenRow.getAttribute("class").contains("grey-row"));
		logger.info("Revoked Token Row turned grey");
	}
	
	
	
	public void VerifyExpanderFieldsForRevokedToken() {
		//Verifying the Expander Fields for the Revoked Token
		WebElement RevokedTokenNotesIcon=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/span/following::td[1]"));
		RevokedTokenNotesIcon.click();
		
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		//Fetching the Expander field details
		String RevokeUserCode=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/span/following::tr[1]//tbody/tr/td[1]")).getText();
		String RevokeDate=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/span/following::tr[1]//tbody/tr/td[2]")).getText();
		String TokenNotesView=driver.findElement(By.xpath("//form[@id='accessTokenTab:accessTokenListForm']//tbody/tr["+i+"]/td[5]/span/following::tr[1]//tbody/tr/td[3]")).getText();
		
		
		String filepath=System.getProperty("user.dir")+"//src//main//java//config//data.properties";
		Basepage.initializeProp(filepath);
	
		//Asserting the Revoke User code with the user code stored in data.properties file
		Assert.assertEquals(RevokeUserCode, (Basepage.readProperty("usercode")));
		
		//Asserting the Revoke Notes
		Assert.assertEquals(TokenNotesView, RevokeNotes);
		
		//Asserting the Revoke Date
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d, yyyy");
		Calendar calendar = Calendar.getInstance();
		String CurrentDate=simpleformat.format(calendar.getTime());
		
		Assert.assertEquals(CurrentDate, RevokeDate);
		
		
	}
	
	public void VerifyEmptyRevokeNotesErrorMessage() {
		
		Assert.assertTrue(ErrorMessageEmptyRevokeNotes.isDisplayed());
		Assert.assertEquals(ErrorMessageEmptyRevokeNotes.getText(), "Revoke Notes cannot be empty");
		
	}
	
	public void ClickCancelButton() {
		ButtonCancel.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	
	public void VerifySortingByApplicationName() throws InterruptedException {
		
		//Fetching the default list of tokens
		List<WebElement> ApplicationNameObtained=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[1]"));
		List<String> ApplicationNameObtainedList=new ArrayList<>();
		for(WebElement AppName:ApplicationNameObtained) {
			ApplicationNameObtainedList.add(AppName.getText());
		}
		
		headerApplicationName.click();
		logger.info("Clicked Application Name Header");
		TimeUnit.SECONDS.sleep(5);
		
		//Fetching the list of tokens sorted in ascending order with application name
		List<WebElement> ApplicationNameGrid=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[1]"));
		List<String> ApplicationNameSortedList=new ArrayList<>();
		
		for(WebElement AppName:ApplicationNameGrid) {
			ApplicationNameSortedList.add(AppName.getText());
		}
		
		Collections.sort(ApplicationNameObtainedList);
		Collections.sort(ApplicationNameSortedList);
		Assert.assertTrue(ApplicationNameObtainedList.equals(ApplicationNameSortedList));
		logger.info("Sorted by Application Name Ascending");
		TimeUnit.SECONDS.sleep(3);
		
		
		//Now Reverse Sorting by Application Name
		headerApplicationName.click();
		logger.info("Clicked Application Name Header");
		TimeUnit.SECONDS.sleep(5);
		
		//Fetching the list of tokens sorted in descending order with application name
		List<WebElement> ApplicationNameObtainedDescending=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[1]"));
		List<String> ApplicationNameObtainedListDescending=new ArrayList<>();
		for(WebElement AppName:ApplicationNameObtainedDescending) {
			ApplicationNameObtainedListDescending.add(AppName.getText());
		}
		
		
		Collections.sort(ApplicationNameObtainedList, Collections.reverseOrder());
		Collections.sort(ApplicationNameObtainedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(ApplicationNameObtainedList.equals(ApplicationNameObtainedListDescending));
		logger.info("Sorted by Application Name Descending");
		TimeUnit.SECONDS.sleep(3);
		
	}
	

	public void VerifySortingByUserCode() throws InterruptedException {
		
		List<WebElement> UserCodeObtained=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[2]"));
		List<String> UserCodeObtainedList=new ArrayList<>();
		for(WebElement AppName:UserCodeObtained) {
			UserCodeObtainedList.add(AppName.getText());
		}
		
		headerUserCode.click();
		logger.info("Clicked User Code Header");
		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> UserCodeGrid=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[2]"));
		List<String> UserCodeSortedList=new ArrayList<>();
		
		for(WebElement AppName:UserCodeGrid) {
			UserCodeSortedList.add(AppName.getText());
		}
		
		Collections.sort(UserCodeObtainedList);
		Collections.sort(UserCodeSortedList);
		Assert.assertTrue(UserCodeObtainedList.equals(UserCodeSortedList));
		logger.info("Sorted by User Code Ascending");
		TimeUnit.SECONDS.sleep(3);
		
		
		//Now Reverse Sorting by Application Name
		headerUserCode.click();
		logger.info("Clicked User Code Header");
		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> UserCodeObtainedDescending=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[2]"));
		List<String> UserCodeObtainedListDescending=new ArrayList<>();
		for(WebElement AppName:UserCodeObtainedDescending) {
			UserCodeObtainedListDescending.add(AppName.getText());
		}
		
		
		Collections.sort(UserCodeObtainedList, Collections.reverseOrder());
		Collections.sort(UserCodeObtainedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(UserCodeObtainedList.equals(UserCodeObtainedListDescending));
		logger.info("Sorted by User Code Descending");
		TimeUnit.SECONDS.sleep(3);
		
	}

	public void VerifyDefaultSortingByIssueDate() throws InterruptedException, ParseException {

		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d,yyyy");

		List<WebElement> IssueDateObtained = driver
				.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateObtainedList = new ArrayList<Date>();
		for (WebElement AppName : IssueDateObtained) {
			IssueDateObtainedList.add(simpleformat.parse(AppName.getText()));
		}
		
		List<WebElement> IssueDateGrid=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateSortedList=new ArrayList<Date>();
		
		for(WebElement AppName:IssueDateGrid) {
			IssueDateSortedList.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(IssueDateObtainedList);
		Collections.sort(IssueDateSortedList);
		Assert.assertTrue(IssueDateObtainedList.equals(IssueDateSortedList));
		logger.info("Default Sorting by Issue Date Ascending");
		TimeUnit.SECONDS.sleep(3);
		
		
	}
	
	public void VerifySortingByIssueDate() throws InterruptedException, ParseException {
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d,yyyy");
		
		List<WebElement> IssueDateObtained=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateObtainedList=new ArrayList<Date>();
		for(WebElement AppName:IssueDateObtained) {
			IssueDateObtainedList.add(simpleformat.parse(AppName.getText()));
		}
		
		headerIssueDate.click();
		logger.info("Clicked Issue Date column Header");
		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> IssueDateGrid=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateSortedList=new ArrayList<Date>();
		
		for(WebElement AppName:IssueDateGrid) {
			IssueDateSortedList.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(IssueDateObtainedList);
		Collections.sort(IssueDateSortedList);
		Assert.assertTrue(IssueDateObtainedList.equals(IssueDateSortedList));
		logger.info("Sorted by Issue Date Ascending");
		TimeUnit.SECONDS.sleep(3);
		
		//Now Reverse Sorting by Issue Date
		headerIssueDate.click();
		logger.info("Clicked Issue Date column Header");
		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> IssueDateGridDescending=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateSortedListDescending=new ArrayList<Date>();
		
		for(WebElement AppName:IssueDateGridDescending) {
			IssueDateSortedListDescending.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(IssueDateObtainedList, Collections.reverseOrder());
		Collections.sort(IssueDateSortedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(IssueDateSortedListDescending.equals(IssueDateObtainedList));
		logger.info("Sorted by Issue Date Descending");
		TimeUnit.SECONDS.sleep(3);
		
	}
	
	public void VerifySortingByExpiryDate() throws InterruptedException, ParseException {
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d,yyyy");
		
		List<WebElement> ExpiryDateObtained=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[4]"));
		List<Date> ExpiryDateObtainedList=new ArrayList<Date>();
		for(WebElement AppName:ExpiryDateObtained) {
			ExpiryDateObtainedList.add(simpleformat.parse(AppName.getText()));
		}
		
		headerExpiryDate.click();
		logger.info("Clicked Expiry Date column Header");
		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> ExpiryDateGrid=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[4]"));
		List<Date> ExpiryDateSortedList=new ArrayList<Date>();
		
		for(WebElement AppName:ExpiryDateGrid) {
			ExpiryDateSortedList.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(ExpiryDateObtainedList);
		Collections.sort(ExpiryDateSortedList);
		Assert.assertTrue(ExpiryDateObtainedList.equals(ExpiryDateSortedList));
		logger.info("Sorted by Expiry Date Ascending");
		TimeUnit.SECONDS.sleep(3);
		
		//Now Reverse Sorting by Issue Date
		headerExpiryDate.click();
		logger.info("Clicked Issue Date column Header");
		TimeUnit.SECONDS.sleep(5);
		
		List<WebElement> ExpiryDateGridDescending=driver.findElements(By.xpath("//tbody[@id='accessTokenTab:accessTokenListForm:tokenList_data']/tr/td[4]"));
		List<Date> ExpiryDateSortedListDescending=new ArrayList<Date>();
		
		for(WebElement AppName:ExpiryDateGridDescending) {
			ExpiryDateSortedListDescending.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(ExpiryDateObtainedList, Collections.reverseOrder());
		Collections.sort(ExpiryDateSortedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(ExpiryDateSortedListDescending.equals(ExpiryDateObtainedList));
		logger.info("Sorted by Expiry Date Descending");
		TimeUnit.SECONDS.sleep(3);
		
	}
	
	

}
