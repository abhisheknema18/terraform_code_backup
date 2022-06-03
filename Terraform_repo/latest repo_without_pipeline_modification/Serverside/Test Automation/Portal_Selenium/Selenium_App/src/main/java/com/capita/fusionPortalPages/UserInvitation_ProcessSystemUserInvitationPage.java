package com.capita.fusionPortalPages;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import BasePackage.Basepage;

public class UserInvitation_ProcessSystemUserInvitationPage extends Basepage {
	public UserInvitation_ProcessSystemUserInvitationPage() {
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//div[@class='inner shadow']/div/span")
	WebElement validationMessage;	

	//As part of this functionality, only 1 page is created invite.html
	
	public void navigateToInvitationUrl(String inviteUrl) {
		driver.get(inviteUrl);

	}
	
	public void verifyValidationMessage(String expectedValidationMessage) {
		Assert.assertEquals(validationMessage.getText(), expectedValidationMessage);
	}
	
}