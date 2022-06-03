package AccessSharedToken;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.text.ParseException;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.SharedAccessToken_AccessTokenListPage;
import com.capita.fusionPortalPages.SharedAccessToken_AddAccessTokenPage;

import BasePackage.Basepage;
import utility.CommonUtils;

public class AccessTokenList extends Basepage{
	
	SharedAccessToken_AccessTokenListPage sharedAccessToken_AccessTokenListPage;
	SharedAccessToken_AddAccessTokenPage sharedAccessToken_AddAccessTokenPage;
	
	@Test
	public void VerifyCountOfTotalToken() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies total tokens in the list with the total count displayed
		sharedAccessToken_AccessTokenListPage.VerifyTokenCount();
		
		
		
	}

	
	@Test
	public void VerifyTokenCountByFilterApplicationName() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies total tokens in the list with the total count displayed filtered by Application Name
		sharedAccessToken_AccessTokenListPage.VerifyTokenCountByFilterApplicationName();
		
	}
	
	@Test
	public void VerifyTokenCountByFilterUserCode() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies total tokens in the list with the total count count displayed filtered by User Code
		sharedAccessToken_AccessTokenListPage.VerifyTokenCountByFilterUserCode();
		
	}
	
	
	@Test
	public void VerifyExpanderFieldsForActiveToken() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
			
		//This function verifies the captured notes
		sharedAccessToken_AccessTokenListPage.VerifyExpanderFieldsActiveToken();
		
	}
	
	
	@Test
	public void VerifyTokenListScreen() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies total tokens in the list with the total count displayed
		sharedAccessToken_AccessTokenListPage.VerifyTokenListScreen();		
	}
	
	
	@Test
	public void VerifyColumnSorting() throws InterruptedException, ParseException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies the token records are sorted as per the application name
		sharedAccessToken_AccessTokenListPage.VerifySortingByApplicationName();
		
		//This function verifies the token records are sorted as per the User Code
		sharedAccessToken_AccessTokenListPage.VerifySortingByUserCode();
		
		//This function verifies the token records are sorted as per the Issue Date
		sharedAccessToken_AccessTokenListPage.VerifySortingByIssueDate();
		
		//This function verifies the token records are sorted as per the Issue Date
		sharedAccessToken_AccessTokenListPage.VerifySortingByExpiryDate();
	
		
	}
	
	@Test
	public void VerifyDefaultSortingByIssueDate() throws InterruptedException, ParseException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.VerifyDefaultSortingByIssueDate();
		
	}
	
	
	@Test
	public void VerifyCapturedNotesforActiveToken() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function clicks the Red Colored notes Icon for Active Token
		sharedAccessToken_AccessTokenListPage.clickRedNotesIcon();
		
		//This function verifies the captured notes
		sharedAccessToken_AccessTokenListPage.VerifyTokenNotes();
		
	}
	
	@Test
	public void VerifyBlankNotesforActiveToken() throws InterruptedException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function clicks the Black Colored notes Icon for Active Token
		sharedAccessToken_AccessTokenListPage.clickBlackNotesIcon();
		
		//This function verifies the captured notes
		sharedAccessToken_AccessTokenListPage.VerifyTokenNotes();
		
	}
	
	
	@Test
	public void VerifyColourCodesAsPerExpiryDate() throws InterruptedException, ParseException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies the color codes as per the status and the expiry date
		sharedAccessToken_AccessTokenListPage.VerifyColourCodesAsPerExpiryDate();
		
	}
	
	@Test
	public void VerifyRevokeToken() throws InterruptedException, ParseException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This method clicks on revoke icon for a token
		sharedAccessToken_AccessTokenListPage.clickRevokeIcon();
		
		//This method enters revoke notes
		sharedAccessToken_AccessTokenListPage.EnterRevokeNotes();
		
		//This method clicks on Revoke button
		sharedAccessToken_AccessTokenListPage.submitRevokeToken();
		
		//This method clicks on Revoke button
		sharedAccessToken_AccessTokenListPage.VerifyTokenRevoked();
		
		//This method verifies the revoke expander fields
		sharedAccessToken_AccessTokenListPage.VerifyExpanderFieldsForRevokedToken();
		
		
	}
	
	
	@Test
	public void VerifyMandatoryFieldsForRevokeToken() throws InterruptedException, ParseException {
		
		sharedAccessToken_AccessTokenListPage = new SharedAccessToken_AccessTokenListPage();
		
		//This function navigates a user to the Add Access token screen
		sharedAccessToken_AccessTokenListPage.navigateToAddAccessToken();
		
		//This function verifies the color codes as per the status and the expiry date
		sharedAccessToken_AccessTokenListPage.clickRevokeIcon();

		//This method clicks on Revoke button
		sharedAccessToken_AccessTokenListPage.submitRevokeToken();
		
		//This method verifies Error Message for empty revoke notes
		sharedAccessToken_AccessTokenListPage.VerifyEmptyRevokeNotesErrorMessage();
		
		//This method clicks on cancel button
		sharedAccessToken_AccessTokenListPage.ClickCancelButton();
	}
	
}
