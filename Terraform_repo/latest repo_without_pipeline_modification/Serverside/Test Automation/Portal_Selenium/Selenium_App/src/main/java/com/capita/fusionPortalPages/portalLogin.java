package com.capita.fusionPortalPages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class portalLogin extends Basepage {
	public portalLogin(){
		PageFactory.initElements(driver, this);
		
	}
	
	@FindBy(xpath="//input[contains(@name,'j_username')]")
	WebElement usernameTextBox;
	
	@FindBy(xpath="//input[contains(@name,'j_password')]")
	WebElement passwordTextBox;
	
	@FindBy(xpath="//input[@type='submit' and @value='Login']")
	WebElement loginButton;
	
	@FindBy(xpath="//input[@type='reset' and @value='Reset']")
	WebElement resetButton;
	
	@FindBy(xpath="//a[@id='menuForm:menuLogoutBtn']")
	WebElement logoutButton;
	
	
	// Following elements for auth0 screen (temporary)
	
	@FindBy(xpath="//input[@id='username']")
	WebElement authUsername;
	
	@FindBy(xpath="//input[@id='password']")
	WebElement authPassword;
	
	@FindBy(xpath="//button[text()='Continue']")
	WebElement authContinueButton;
	
	@FindBy(xpath="//a[@id='menuForm:menuHomeBtn']/span[text()='Home']")
	WebElement menuHome;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void login(String username, String password) {
			authUsername.sendKeys(username);
			authPassword.sendKeys(password);
			authContinueButton.click();
			Assert.assertTrue(menuHome.isDisplayed());
	
	}
	
	public void logout() {
		
		List<WebElement> alert=driver.findElements(By.xpath("//div[@role='alert']"));
		if(alert.size()>0) {
			wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//div[@role='alert']"))));
		}
		
		logoutButton.click();

	}
}
