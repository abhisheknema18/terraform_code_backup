package com.capita.fusionPortalPages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;
import utility.CommonUtils;

public class Work_AssignPage extends Basepage{
	public Work_AssignPage () {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(id="assignWorkForm:workgroupSelectTree_filter")
	WebElement workGroupInputElement;
	
	@FindBy(className="ui-button-text")
	List<WebElement> AssignPagefooterBtn;
	
	@FindBy(xpath="//div[@id='assignWorkForm:workgroupSelectTree']//span")
	List<WebElement> WorkLabelElement;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(tagName="span")
	List<WebElement> TagNameList;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void searchWorkGroup(String wrkgrpInput, String btnAction) throws InterruptedException {
         new CommonUtils().enterText(this.workGroupInputElement, wrkgrpInput);
         TimeUnit.SECONDS.sleep(1);
         new CommonUtils().singleElementToReturn(this.WorkLabelElement, wrkgrpInput).click();
         this.clickBtn(btnAction);
         wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickBtn(String btnAction) {
		new CommonUtils().singleElementToReturn(this.AssignPagefooterBtn, btnAction).click();
	}
}
