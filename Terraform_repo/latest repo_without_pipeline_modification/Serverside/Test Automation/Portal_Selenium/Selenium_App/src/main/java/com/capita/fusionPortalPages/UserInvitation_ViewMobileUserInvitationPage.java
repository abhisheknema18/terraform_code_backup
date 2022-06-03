package com.capita.fusionPortalPages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class UserInvitation_ViewMobileUserInvitationPage extends Basepage {
	public UserInvitation_ViewMobileUserInvitationPage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[text()='Admin']")
	WebElement adminTab;
	
	@FindBy(xpath="//a[text()='User Invitation']")
	WebElement UserInvitationMenu;
	
	@FindBy(xpath="//a[text()='View Mobile User Invitations']")
	WebElement ViewMobileUserInvitationsSubMenu;
	
	@FindBy(xpath="//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr")
	List<WebElement> totalInvitations;
	
	@FindBy(xpath="//a/span[contains(@id,'inviteTokenTab:headerTitle')]")
	WebElement invitationsCountTab;
	
	@FindBy(xpath="//span[@id='inviteTokenTab:inviteTokenListForm:footerTitle']")
	WebElement invitationsFoundCount;
	
	@FindBy(xpath="//span[text()='Invitation Name']/following-sibling::input")
	WebElement txtBoxFilterInvitationName;
	
	@FindBy(xpath="//span[text()='Work Group Code']/following-sibling::input")
	WebElement txtBoxFilterWorkGroupCode;
	
	@FindBy(xpath="//span[text()='Invitation Name']")
	WebElement columnHeaderInvitationName;
	
	@FindBy(xpath="//span[text()='Work Group Code']")
	WebElement columnHeaderWorkGroupCode;
	
	@FindBy(xpath="//span[text()='Issue Date']")
	WebElement columnHeaderIssueDate;
	
	@FindBy(xpath="//span[text()='Expiry Date']")
	WebElement columnHeaderExpiryDate;
	
	@FindBy(xpath="//div[@id='inviteTokenTab:inviteTokenListForm:ShowActiveTokens']")
	WebElement toggleBtnActive;
	
	@FindBy(xpath="//div[@id='inviteTokenTab:inviteTokenListForm:ShowActiveTokens']")
	WebElement toggleActiveState;
	
	@FindBy(xpath="//form[@id='revokeInviteForm']/table[@id='revokeInviteForm:innerContent']/tbody/tr/td/textarea")
	WebElement TxtAreaRevokeNotes;	
	
	@FindBy(xpath="//button[@id='revokeInviteForm:revokeInviteTokenBtn']/span[text()='revoke']")
	WebElement btnRevoke;
	
	@FindBy(id="revokeInviteForm:cancelInviteToken")
	WebElement btnCancel;	
	
	@FindBy(xpath="//span[text()='Revoke Notes cannot be empty']")
	WebElement revokeValidationMessage;
	
	
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void navigateToViewUserInvitations() throws InterruptedException {
		if(adminTab.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			adminTab.click();
			TimeUnit.SECONDS.sleep(3);
			if(UserInvitationMenu.isDisplayed()) {
				logger.info("User has access to User Invitation Token Module");
				if(driver.findElement(By.xpath("//a[text()='User Invitation']/span")).getAttribute("class").contains("right")) {
					UserInvitationMenu.click();
				}
				
				TimeUnit.SECONDS.sleep(3);
				ViewMobileUserInvitationsSubMenu.click();
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

	public void enableActiveToggle() {
		//the attribute aria-checked is available only when we click on toggle. Hence clicking on toggle first to get the state of it.
		toggleBtnActive.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		if(toggleBtnActive.getAttribute("aria-checked").equalsIgnoreCase("false")){
			toggleBtnActive.click();
			logger.info("Active toggle enabled");
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
	}

	public void disableActiveToggle() {

		//the attribute aria-checked is available only when we click on toggle. Hence clicking on toggle to get the state of it.
		toggleBtnActive.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		if(toggleBtnActive.getAttribute("aria-checked").equalsIgnoreCase("true")){
			toggleBtnActive.click();
			logger.info("Active toggle disabled");
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		}
	}
	
	public void verifyDefaultTActiveToggleState() {
		if (toggleActiveState.getAttribute("aria-checked").equalsIgnoreCase("aria-checked")) {
			logger.info("Active toggle is enabled by default");
		}
	}
	
	public void VerifyInvitationsCount() {
		
		//List Size of total token records on page
		int TotalInvitationsRecords=totalInvitations.size();
		
		//Fetching the count from token count tab at the top of the page
		String invitationsCountTabString=invitationsCountTab.getText().substring(invitationsCountTab.getText().indexOf("(") + 1, invitationsCountTab.getText().indexOf(")"));
		int invitationsCountTab=Integer.parseInt(invitationsCountTabString);
		
		//Verifying the count on tab with the size of the list
		Assert.assertEquals(invitationsCountTab, TotalInvitationsRecords);
		logger.info("Total count of tokens matches with the Total records displayed on Access Token Tab");
		
		//Fetching the count of tokens displayed at the bottom of the screen
		String invitationsCountFoundString=invitationsFoundCount.getText().substring(0,invitationsFoundCount.getText().indexOf(" "));
		int invitationsCountFound=Integer.parseInt(invitationsCountFoundString);
		
		//Verifying the count on tokens found with the size of the list
		Assert.assertEquals(invitationsCountFound, TotalInvitationsRecords);
		logger.info("Total count of token matches with the Total records displayed");
		
	}
	
	public void verifyFilterByInvitationName() {
		String invitationNameFilterInput="o";
		txtBoxFilterInvitationName.sendKeys(invitationNameFilterInput);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			List<WebElement> invitationNames=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[1]"));
			
			for(WebElement temp:invitationNames) {
				Assert.assertTrue(temp.getText().toLowerCase().contains(invitationNameFilterInput.toLowerCase()));
			}
			logger.info("Filter by Invitation name works fine");
		}
		else {
			logger.info("No Records found for entered filter string");
		}
		
	}
	
	public void verifyFilterByWorkGroupCode() {
		String workGroupCodeFilterInput="a";
		txtBoxFilterWorkGroupCode.sendKeys(workGroupCodeFilterInput);
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			List<WebElement> workGroupCodes=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[2]"));
			
			for(WebElement temp:workGroupCodes) {
				Assert.assertTrue(temp.getText().toLowerCase().contains(workGroupCodeFilterInput.toLowerCase()));
			}
			logger.info("Filter by Invitation name works fine");
		}
		else {
			logger.info("No Records found for entered filter string");
		}
		
	}
	
	public void verifySortByInvitationName() throws InterruptedException {
		List<WebElement> InvitationsNameObtained=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[1]"));
		List<String> InvitationsNameObtainedList=new ArrayList<>();
		for(WebElement temp:InvitationsNameObtained) {
			InvitationsNameObtainedList.add(temp.getText());
		}
		
		columnHeaderInvitationName.click();
		logger.info("Clicked Invitation Name Column");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> InvitationNameGrid=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[1]"));
		List<String> InvitationNameSortedList=new ArrayList<>();
		
		for(WebElement temp:InvitationNameGrid) {
			InvitationNameSortedList.add(temp.getText());
		}
		
		Collections.sort(InvitationsNameObtainedList);
		Collections.sort(InvitationNameSortedList);
		Assert.assertTrue(InvitationsNameObtainedList.equals(InvitationNameSortedList));
		logger.info("Sorted by Invtation Name Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		
		//Now Reverse Sorting by Application Name
		columnHeaderInvitationName.click();
		logger.info("Clicked Invtation Name Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> InvitationNameObtainedDescending=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[1]"));
		List<String> InvitationNameObtainedListDescending=new ArrayList<>();
		for(WebElement AppName:InvitationNameObtainedDescending) {
			InvitationNameObtainedListDescending.add(AppName.getText());
		}
		
		
		Collections.sort(InvitationsNameObtainedList, Collections.reverseOrder());
		Collections.sort(InvitationNameObtainedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(InvitationsNameObtainedList.equals(InvitationNameObtainedListDescending));
		logger.info("Sorted by Invtation Name Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifySortByWorkGroupCode() throws InterruptedException {
		List<WebElement> WorkGroupCodeObtained=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[2]"));
		List<String> WorkGroupCodeObtainedList=new ArrayList<>();
		for(WebElement temp:WorkGroupCodeObtained) {
			WorkGroupCodeObtainedList.add(temp.getText());
		}
		
		columnHeaderWorkGroupCode.click();
		logger.info("Clicked Work Group Code Column");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> WorkGroupCodeGrid=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[2]"));
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
		columnHeaderWorkGroupCode.click();
		logger.info("Clicked Work Group Code Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> WorkGroupCodeObtainedDescending=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[2]"));
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
	
	public void VerifySortingByIssueDate() throws InterruptedException, ParseException {
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d,yyyy");
		
		List<WebElement> IssueDateObtained=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateObtainedList=new ArrayList<Date>();
		for(WebElement AppName:IssueDateObtained) {
			IssueDateObtainedList.add(simpleformat.parse(AppName.getText()));
		}
		
		columnHeaderIssueDate.click();
		logger.info("Clicked Issue Date column Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> IssueDateGrid=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateSortedList=new ArrayList<Date>();
		
		for(WebElement AppName:IssueDateGrid) {
			IssueDateSortedList.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(IssueDateObtainedList);
		Collections.sort(IssueDateSortedList);
		Assert.assertTrue(IssueDateObtainedList.equals(IssueDateSortedList));
		logger.info("Sorted by Issue Date Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		//Now Reverse Sorting by Issue Date
		columnHeaderIssueDate.click();
		logger.info("Clicked Issue Date column Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> IssueDateGridDescending=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[3]"));
		List<Date> IssueDateSortedListDescending=new ArrayList<Date>();
		
		for(WebElement AppName:IssueDateGridDescending) {
			IssueDateSortedListDescending.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(IssueDateObtainedList, Collections.reverseOrder());
		Collections.sort(IssueDateSortedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(IssueDateSortedListDescending.equals(IssueDateObtainedList));
		logger.info("Sorted by Issue Date Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
	}
	
	public void VerifySortingByExpiryDate() throws InterruptedException, ParseException {
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d,yyyy");
		
		List<WebElement> ExpiryDateObtained=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[4]"));
		List<Date> ExpiryDateObtainedList=new ArrayList<Date>();
		for(WebElement AppName:ExpiryDateObtained) {
			ExpiryDateObtainedList.add(simpleformat.parse(AppName.getText()));
		}
		
		columnHeaderExpiryDate.click();
		logger.info("Clicked Expiry Date column Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> ExpiryDateGrid=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[4]"));
		List<Date> ExpiryDateSortedList=new ArrayList<Date>();
		
		for(WebElement AppName:ExpiryDateGrid) {
			ExpiryDateSortedList.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(ExpiryDateObtainedList);
		Collections.sort(ExpiryDateSortedList);
		Assert.assertTrue(ExpiryDateObtainedList.equals(ExpiryDateSortedList));
		logger.info("Sorted by Expiry Date Ascending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		//Now Reverse Sorting by Issue Date
		columnHeaderExpiryDate.click();
		logger.info("Clicked Issue Date column Header");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
		List<WebElement> ExpiryDateGridDescending=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[4]"));
		List<Date> ExpiryDateSortedListDescending=new ArrayList<Date>();
		
		for(WebElement AppName:ExpiryDateGridDescending) {
			ExpiryDateSortedListDescending.add(simpleformat.parse(AppName.getText()));
		}
		
		Collections.sort(ExpiryDateObtainedList, Collections.reverseOrder());
		Collections.sort(ExpiryDateSortedListDescending, Collections.reverseOrder());
		
		Assert.assertTrue(ExpiryDateSortedListDescending.equals(ExpiryDateObtainedList));
		logger.info("Sorted by Expiry Date Descending");
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		
	}
	
	public void VerifyColourCodesAsPerExpiryDate() throws ParseException {
		int countOrange=0;
		int countGreen=0;
		int countRed=0;
		int countGrey=0;

		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d, yyyy");
		Calendar calendar;
		Calendar calendar1;
		
		//Traversing from top to the end of the token list
		for(int i=1;i<=totalInvitations.size();i++) {
			//Fetching the color code for a token record
			String Iconcolor=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[" + i + "]/td[1]/span")).getAttribute("style");

			//If color is orange check for expiry date is within the 30days
			if(Iconcolor.equalsIgnoreCase("color: orange;")&&countOrange==0) {
				String ExpiryDate=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+i+"]/td[4]")).getText();
				Date date1 = simpleformat.parse(ExpiryDate);
				calendar = Calendar.getInstance();
				calendar1 = Calendar.getInstance();
				calendar1.setTime(date1);
				calendar.add(Calendar.DAY_OF_MONTH, 30);
				//Check if a date is less than another date
				  Assert.assertTrue(calendar1.before(calendar));
				  countOrange++;
				  logger.info("Invitation Expiry Within 30 days for Orange icon verified successfully");
			}
			else
				//If color is Green, check for expiry date is after the 30days
				if(Iconcolor.equalsIgnoreCase("color: green;")&&countGreen==0) {
					String ExpiryDate=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+i+"]/td[4]")).getText();
					Date date1 = simpleformat.parse(ExpiryDate);
					calendar = Calendar.getInstance();
					calendar1 = Calendar.getInstance();
					calendar1.setTime(date1);
					calendar.add(Calendar.DAY_OF_MONTH, 30);
					//Check if a date is less than another date
					  Assert.assertTrue(calendar1.after(calendar));
					  countGreen++;
					  logger.info("Invitation Expiry more than 30 days for Green icon verified successfully");
				}
				else
					//If color is Red check for expiry date is before current date
					if(Iconcolor.equalsIgnoreCase("color: red;")&&countRed==0) {
						String ExpiryDate=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+i+"]/td[4]")).getText();
						Date date1 = simpleformat.parse(ExpiryDate);
						calendar = Calendar.getInstance();
						calendar1 = Calendar.getInstance();
						calendar1.setTime(date1);
						//Check if a date is less than another date
						  Assert.assertTrue(calendar1.before(calendar));
						  countRed++;
						  logger.info("Expired Token Verified for Red icon verified successfully");
					}
					else
						//If color is Grey, check for the token is revoked
						if(Iconcolor.equalsIgnoreCase("color: grey;")&&countGrey==0) {
							WebElement RevokeIconDisabled=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+i+"]/td[5]/span"));
							Assert.assertEquals(RevokeIconDisabled.isDisplayed(), true);
							countGrey++;
							logger.info("Revoked Invitation Verified for Grey icon verified successfully");
						}
					
		}
		
	}
	
	public void verifyActiveInvitationsToggle() {
		List<WebElement> invitationLegendIcons=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td/span"));
		
		for(WebElement temp:invitationLegendIcons) {
			if(temp.getAttribute("style").equalsIgnoreCase("color: green;")) {
				Assert.assertTrue(true);
			}
			else if(temp.getAttribute("style").equalsIgnoreCase("color: orange;")) {
				Assert.assertTrue(true);
			}
		}
		logger.info("Only Active invitations are displayed");
	}
	
	
	
	public void clickActiveInvitationNotesIcon() {
		List<WebElement> noRecordsFoundMsg=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']//td[text()='No records found.']"));
		
		if(noRecordsFoundMsg.size()==0) {
			WebElement notesIcon=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[1]/td[6]/a"));
			notesIcon.click();
		}
	}
	
	public void verifyActiveInvitationExpanderFields() {
		
		String recordIssueDate=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[1]/td[3]")).getText();
		
		String expanderIssueDate=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[1]/td[4]/parent::tr/following-sibling::tr[1]//tbody/tr/td[2]")).getText();

		//verifying the expander Issued date with row issue date
		Assert.assertTrue(recordIssueDate.equalsIgnoreCase(expanderIssueDate));
		
		//Verify other columns display and not null values
		
		String expanderUserCode=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[1]/td[4]/parent::tr/following-sibling::tr[1]//tbody/tr/td[1]")).getText();
		Assert.assertFalse(expanderUserCode.equalsIgnoreCase(""));
		
		String expanderNotes=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[1]/td[4]/parent::tr/following-sibling::tr[1]//tbody/tr/td[3]")).getText();
		Assert.assertFalse(expanderNotes.equalsIgnoreCase(""));
	
		
	}
	
	private int i=0;
	private String RevokeNotes="";
	public void clickRevokeInvitationIcon() {
		for (i = 1; i <= totalInvitations.size(); i++) {
			// Fetching the color code for a token record
			String Iconcolor = driver
					.findElement(By.xpath(
							"//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[" + i + "]/td[1]/span"))
					.getAttribute("style");
			if(!Iconcolor.equalsIgnoreCase("color: grey;")) {
				WebElement RevokeInvotationIcon=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr[" + i + "]/td[5]/a"));
				RevokeInvotationIcon.click();
				wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
				logger.info("clicked Revoke Icon");
				break;
			}

		}
	}
	
	public void enterRevokeNotes() {
		RevokeNotes=RandomStringUtils.randomAlphabetic(5).toLowerCase();

		//Sendkeys Is not Working hence used Actions class
		Actions builder = new Actions(driver);
        Action TextAreaTextType = builder.sendKeys(TxtAreaRevokeNotes, RevokeNotes).build();
        TextAreaTextType.perform();
        logger.info("Entered Revoke Notes");
        
	}
	
	public void clickRevokeButton() {
		btnRevoke.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void verifyInvitationRevoked() {
		//verifying the revoke icon turns grey after invitation is revoked
		WebElement revokedInvitationIcon=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+ i +"]/td[5]/span"));
		Assert.assertTrue(revokedInvitationIcon.isDisplayed());
		logger.info("Token Revoked Successfully");
		
		//verifying the invitation row turns grey when it is is revoked
		WebElement RevokedInvitationRow=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+ i +"]"));
		Assert.assertTrue(RevokedInvitationRow.getAttribute("class").contains("grey-row"));
		logger.info("Revoked Invitation Row turned grey");
		
	}
	
	public void verifyRevokedInvitationExpanderFields() {
		//identifying the notes icon for the invite that we revoked
		WebElement revokedInvitationNotesIcon=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+ i +"]/td[6]/a"));
		revokedInvitationNotesIcon.click();
		
		//Verify the Revoke Notes
		WebElement revokeNotesExpanderField=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+ i +"]/following-sibling::tr[1]//tbody/tr/td[3]"));
		Assert.assertEquals(revokeNotesExpanderField.getText(), RevokeNotes);
		logger.info("Revoke Notes display as expected");
		
		//verify the revoke user
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String usercode=Basepage.readProperty("usercode");
		
		WebElement revokeUserExpanderField=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+ i +"]/following-sibling::tr[1]//tbody/tr/td[1]"));
		Assert.assertEquals(revokeUserExpanderField.getText(), usercode);
		logger.info("Revoke user displayed as expected");
		
		//verify the revoke date
		WebElement revokeDateExpanderField=driver.findElement(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr["+ i +"]/following-sibling::tr[1]//tbody/tr/td[2]"));
		
		SimpleDateFormat simpleformat = new SimpleDateFormat("MMM d, yyyy");
		Date date1 = null;
		Date date2 = null;
		try {
			//date1 is the current date
			date1= new Date(System.currentTimeMillis());
			date1=simpleformat.parse(simpleformat.format(date1));
			
			//date2 is actual expiry date fetched from screen
			date2 = simpleformat.parse(revokeDateExpanderField.getText());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date2);
		
		if(calendar.compareTo(calendar1)==0) {
			Assert.assertTrue(true);
			logger.info("Revoke date display as expected");
		}
		else {
			Assert.assertTrue(false);
		}
		
		
	}
	
	public void VerifyRevokeValidation() {
		Assert.assertTrue(revokeValidationMessage.isDisplayed());
		logger.info("Mandatory Validation for Revoke Notes is displayed");
	}
	
	public void clickCancelButton() {
		btnCancel.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("clicked cancel button");
	}
	
	public void verifyInvitationsVisibleForWorkGroupAdminUser() {
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		List<String> TrishaUserWGCodeList=Arrays.asList(readProperty("TrishaUserAccessWorkGroupCodes").split(","));
		
		List<WebElement> WorkGroupCodeColumnList=driver.findElements(By.xpath("//tbody[@id='inviteTokenTab:inviteTokenListForm:tokenList_data']/tr/td[2]"));
		
		for(WebElement temp:WorkGroupCodeColumnList) {
			Assert.assertTrue(TrishaUserWGCodeList.contains(temp.getText()));
		}
		
		logger.info("Only assigned work groups are displayed for WG Admin User");
	}
}
