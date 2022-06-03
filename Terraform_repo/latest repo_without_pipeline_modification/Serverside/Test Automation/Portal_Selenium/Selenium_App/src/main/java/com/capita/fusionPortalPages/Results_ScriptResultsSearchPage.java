package com.capita.fusionPortalPages;

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

public class Results_ScriptResultsSearchPage extends Basepage {
	
	public Results_ScriptResultsSearchPage(){
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//span[text()='Results']")
	WebElement Tab_Results;
	
	@FindBy(xpath="//a[@id='menuForm:viewResultsBtn']")
	WebElement Menu_Search;
	
	@FindBy(xpath="//button[@id='resultSearchTabs:searchForm:saveBtn']/span")
	WebElement Btn_Search;
	
	@FindBy(xpath="//a[@title='Return to List']")
	WebElement icon_ReturnToList;
	
	
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	
	public void navigateToResultsSearchPage() throws InterruptedException {
		if(Tab_Results.isDisplayed()) {
			logger.info("Logged In User is an Admin User");
			logger.info("Click Admin tab");
			Tab_Results.click();
//			TimeUnit.SECONDS.sleep(3);
			
			if(Menu_Search.isDisplayed()) {
				logger.info("User has access to Shared Access Token Module");
				Menu_Search.click();
//				TimeUnit.SECONDS.sleep(3);
				wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
				logger.info("click View Access Token sub menu");
				TimeUnit.SECONDS.sleep(2);
			}
			else {
				logger.info("User has no access to Module Shared Access Tokens");
			}
			
		} else {
			logger.info("Logged In User is not an Admin User");
			
		}
	}
	
	public void SearchResult() {
		Btn_Search.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("Logged In User is not an Admin User");
	}
	
	public void clickDetailedScreenForOwnResults() {
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String mobileUserName=Basepage.readProperty("mobileusername");
		List<WebElement> resultMobileUsers=driver.findElements(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr/td[9]"));
		for(int i=1;i<=resultMobileUsers.size();i++) {
			
			String usernameColValue=driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[9]")).getText();
			if(usernameColValue.equalsIgnoreCase(mobileUserName)) {
				driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[12]/a")).click();
				wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
				logger.info("Clicked detailed screen icon");
				break;
			}
		}
		
	}
	
	public void clickDetailedScreenForOtherResults() {
		Basepage.initializeProp(System.getProperty("user.dir")+"//src//main//java//Config//data.properties");
		String mobileUserName=Basepage.readProperty("mobileusername");
		List<WebElement> resultMobileUsers=driver.findElements(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr/td[9]"));
		for(int i=1;i<=resultMobileUsers.size();i++) {
			
			String usernameColValue=driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[9]")).getText();
			if(!usernameColValue.equalsIgnoreCase(mobileUserName)) {
				driver.findElement(By.xpath("//tbody[@id='searchTabs:searchForm:resultsTable_data']/tr["+i+"]/td[12]/a")).click();
				wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
				logger.info("Clicked detailed screen icon");
				break;
			}
		}
		
	}
	
	public void verifyEditResponseIconAvailablity() {
		List<WebElement> scripts=driver.findElements(By.xpath("//tbody[@id='detailsForm:resultSetTable_data']/tr"));
		if(scripts.size()>1) {
			List<WebElement> editResponseIcon=driver.findElements(By.xpath("//tbody[@id='detailsForm:resultSetTable_data']/tr[2]/td[4]/a"));
			if(editResponseIcon.size()==0) {
				Assert.assertTrue(editResponseIcon.size()==0);
				logger.info("Edit Response Icon is not available");
			}
			else {
				Assert.assertTrue(editResponseIcon.size()>0);
				logger.info("Edit Response Icon is Displayed");
			}
				
		}
	}
	
	public void clickReturnToListIcon() {
		icon_ReturnToList.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
		logger.info("clicked Return to List Icon");
	}
	
}
