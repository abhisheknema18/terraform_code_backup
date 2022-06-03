package ConfigurationFile;

import java.text.ParseException;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.capita.fusionPortalPages.ConfigurationFilesListPage;

import BasePackage.Basepage;
import utility.CommonUtils;

public class ConfigurationFiles extends Basepage {
	ConfigurationFilesListPage configurationFilesListPage;
	String filepath;
	
	
	@Test
	public void verifyDefaultConfigSelection() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		// This function validates that WM config is by default selected
		configurationFilesListPage.verifyDefaultTWMConfigSelected();

		// This function validates that Upload Button is displayed
		configurationFilesListPage.verifyUploadButtonIsDisplayed();
		
		// This function validates the count of WM Config Files
		configurationFilesListPage.verifyWMConfigCountFiles();
		
		// This function validates the Script config tab is displayed 
		configurationFilesListPage.verifyScriptConfigTabDisplayed();
		
		// This function validates the count of Script Config Files
		configurationFilesListPage.verifyScriptConfigCountFiles();
		
	}
	
	@Test
	public void verifyColumnSorting() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		// This function validates the sort by Published Date
		configurationFilesListPage.verifySortByPublishedDate();

		// This function validates the sort by Published By
		configurationFilesListPage.verifySortByPublishedBy();
		
		// This function validates the sort by Work Group
		configurationFilesListPage.verifySortByWorkGroup();
		  
	   // This function validates the sort by Configuration File Name
	   configurationFilesListPage.verifySortByConfigurationFileName();
		 
		
	}
	
	
	
	@Test
	public void verifyFilterByPublishedBy() throws InterruptedException {

		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();

		// This function verifies total tokens in the list with the total count
		// displayed
		configurationFilesListPage.verifyFilterByPublishedBy();
	}
	
	@Test
	public void verifyFilterByWorkGroup() throws InterruptedException {

		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();

		// This function verifies total tokens in the list with the total count
		// displayed
		configurationFilesListPage.verifyFilterByWorkGroup();
	}
	
	@Test
	public void verifyFilterByConfigurationName() throws InterruptedException {

		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();

		// This function verifies total tokens in the list with the total count
		configurationFilesListPage.verifyFilterConfigurationFileName();
	}
	
	//Following Upload Scenarios
	
	@Test
	public void verifyUploadWMConfigSuccessful() throws InterruptedException, ParseException {
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMCONFIG_valid.XML";
		
		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		else {
			configurationFilesListPage.deleteWMConfigFile("WMConfig_valid.xml");
			configurationFilesListPage.clickYesDeleteFilePopUp();
			configurationFilesListPage.UploadFile(filepath);
		}
		
		configurationFilesListPage.verifyUploadSuccessful();
		
		if(configurationFilesListPage.isWMConfigFileAvailable("WMConfig_valid.xml")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void verifyScriptConfigUploadSuccessful() throws InterruptedException, ParseException {
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//ScriptConfig_valid.XML";
		
		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		else {
			configurationFilesListPage.deleteScriptConfigFile("ScriptConfig_valid.xml");
			configurationFilesListPage.clickYesDeleteFilePopUp();
			TimeUnit.SECONDS.sleep(2);
			configurationFilesListPage.UploadFile(filepath);
		}
		
		if(configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertTrue(false);
		}
	}
	
	
	
	@Test
	public void verifyWMConfigUploadUnSuccessful() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMCONFIG_invalid.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMConfig_invalid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.verifyWMConfigUploadUnSuccessful();
		
	}
	
	@Test
	public void verifyScriptConfigUploadUnSuccessful() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//ScriptConfig_invalid.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_invalid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.verifyScriptConfigUploadUnSuccessful();
		
	}
	
	@Test
	public void verifyUploadInvalidFileType() throws InterruptedException {
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//data.properties";
		
		configurationFilesListPage = new ConfigurationFilesListPage();

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.UploadFile(filepath);
		
		configurationFilesListPage.verifyWMConfigUploadUnSuccessful();
	}
	
	@Test
	public void verifyInvalidXSDWMConfigFileUpload() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMCONFIG_CAPP.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMCONFIG_CAPP.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.verifyWMConfigUploadUnSuccessful();
		
	}
	
	@Test
	public void verifyInvalidXSDScriptConfigFileUpload() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//SCRIPTCONFIG_CAPP.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("SCRIPTCONFIG_CAPP.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.verifyScriptConfigUploadUnSuccessful();
	}
	
	//Following upload Replace Scenarios
	
	@Test
	public void verifyWMConfigReplaceYes() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMCONFIG_valid.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.UploadFile(filepath);
		configurationFilesListPage.clickYesReplaceFilePopUp();
		
		configurationFilesListPage.verifyUploadSuccessful();
		
	}
	
	@Test
	public void verifyWMConfigReplaceNo() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMCONFIG_valid.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.UploadFile(filepath);
		configurationFilesListPage.clickNoReplaceFilePopUp();
		if(configurationFilesListPage.isWMConfigFileAvailable("WMCONFIG_valid.xml")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertTrue(false);
		}
		
		
	}
	
	@Test
	public void verifyScriptConfigReplaceYes() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		filepath=System.getProperty("user.dir")+"//src//main//java//Config//ScriptConfig_valid.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		
		configurationFilesListPage.UploadFile(filepath);
		configurationFilesListPage.clickYesReplaceFilePopUp();
		configurationFilesListPage.verifyUploadSuccessful();
		
	}
	
	@Test
	public void verifyScriptConfigReplaceNo() throws InterruptedException, ParseException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		filepath=System.getProperty("user.dir")+"//src//main//java//Config//ScriptConfig_valid.XML";

		// This function navigates an admin to the Configuration File screen
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		
		configurationFilesListPage.UploadFile(filepath);
		
		configurationFilesListPage.clickNoReplaceFilePopUp();
		
		if(configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertTrue(false);
		}
		
		
	}
	
	//Following Delete Scenarios
	
	@Test
	public void verifyScriptConfigFileDeleteYes() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//ScriptConfig_valid.XML";
		
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		configurationFilesListPage.deleteScriptConfigFile("ScriptConfig_valid.xml");
		
		configurationFilesListPage.clickYesDeleteFilePopUp();
		
		if(configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			Assert.assertTrue(false);
		}
		else {
			Assert.assertTrue(true);
		}
		
	}
	
	@Test
	public void verifyWMConfigFileDeleteYes() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMConfig_valid.XML";
		
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		
		configurationFilesListPage.deleteWMConfigFile("WMCONFIG_valid.xml");
		
		
		configurationFilesListPage.clickYesDeleteFilePopUp();
		
		if(configurationFilesListPage.isWMConfigFileAvailable("WMCONFIG_valid.xml")) {
			Assert.assertTrue(false);
		}
		else {
			Assert.assertTrue(true);
		}
		
	}
	
	@Test
	public void verifyScriptConfigFileDeleteNo() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//ScriptConfig_valid.XML";
		
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.switchToTabScriptConfig();
		
		if(!configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		
		configurationFilesListPage.deleteScriptConfigFile("ScriptConfig_valid.xml");
		
		configurationFilesListPage.clickNoDeleteFilePopUp();
		
		if(configurationFilesListPage.isScriptConfigFileAvailable("ScriptConfig_valid.xml")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertTrue(false);
		}
		
	}
	
	@Test
	public void verifyWMConfigFileDeleteNo() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//WMConfig_valid.XML";
		
		configurationFilesListPage.navigateToConfigurationFiles();
		
		if(!configurationFilesListPage.isWMConfigFileAvailable("WMConfig_valid.xml")) {
			configurationFilesListPage.UploadFile(filepath);
			
		}
		
		configurationFilesListPage.deleteWMConfigFile("WMCONFIG_valid.xml");
		
		configurationFilesListPage.clickNoDeleteFilePopUp();
		
		if(configurationFilesListPage.isWMConfigFileAvailable("WMCONFIG_valid.xml")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertTrue(false);
		}
		
	}
	
	@Test
	public void verifyDeleteDefaultWMConfigFile() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		configurationFilesListPage.navigateToConfigurationFiles();
		configurationFilesListPage.isDeleteButtonAvailableForDefaultWMConfigFile();
	}
	
	@Test
	public void verifyDeleteDefaultScriptConfigFile() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		configurationFilesListPage.navigateToConfigurationFiles();
		configurationFilesListPage.switchToTabScriptConfig();
		configurationFilesListPage.isDeleteButtonAvailableForDefaultScriptConfigFile();
	}
	
	//Download Scenarios as follows
	@Test
	public void verifyDownloadWMConfigFile() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();
		
		filepath=System.getProperty("user.dir")+"//src//main//java//Config//PortalDownloads";
		configurationFilesListPage.navigateToConfigurationFiles();
		
		configurationFilesListPage.downloadWMConfigFile("WMCONFIG.xml");
		if(CommonUtils.isFileDownloadedSuccessFully(filepath, "WMCONFIG.xml")>0) {
			Assert.assertTrue(true);
		}else {
			Assert.fail();
		}
		
	}
	@Test
	public void verifyDownloadScriptConfigFile() throws InterruptedException {
		configurationFilesListPage = new ConfigurationFilesListPage();

		filepath=System.getProperty("user.dir")+"//src//main//java//Config//PortalDownloads";
		
		configurationFilesListPage.navigateToConfigurationFiles();
		configurationFilesListPage.switchToTabScriptConfig();
		
		configurationFilesListPage.downloadScriptConfigFile("SCRIPTCONFIG.xml");
		if(CommonUtils.isFileDownloadedSuccessFully(filepath, "SCRIPTCONFIG.xml")>0) {
			Assert.assertTrue(true);
		}else {
			Assert.fail();
		}
		
	}
	
	
	
}