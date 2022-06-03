package com.capita.fusionPortalPages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import BasePackage.Basepage;
import utility.CommonUtils;

public class MobileUsers_WorkGroupSelectionPage extends Basepage{
	public MobileUsers_WorkGroupSelectionPage(){
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(css="li[id^='WorkgroupSelectionFromWorkGroupAdmin:workgroupSelectTree']")
	List<WebElement> WorkGroupSelectionList;
	
	@FindBy(id="WorkgroupSelectionFromWorkGroupAdmin:workgroupSelectionP_footer")
	WebElement workGroupSelectionFooter;
	
	@FindBy(id="WorkgroupSelectionFormUserAdmin:workgroupSelectionP_footer")
	WebElement workGroupSelectionFooterForAdminUser;
	
	@FindBy(id="SingleWorkgroupSelectionFormUserAdmin:workgroupSelectionSingle_footer")
	WebElement singleWorkGroupSelectionFooterForAdminUser;
	
	@FindBy(className="ui-treenode-label")
	List<WebElement> WorkGroupNameList;
	
	@FindBy(id="WorkgroupSelectionFromWorkGroupAdmin:workgroupSelectTree_filter")
	WebElement selectWorkGroupInput;
	
	@FindBy(id="WorkgroupSelectionFormUserAdmin:workgroupSelectTree_filter")
	WebElement selectWorkGroupInputForAdminUser;
	
	@FindBy(id="SingleWorkgroupSelectionFormUserAdmin:workgroupSelectTree_filter")
	WebElement selectSingleWorkGroupInputForAdminUser;
	
	@FindBy(xpath="//div[@id='WorkgroupSelectionFromWorkGroupAdmin:workgroupSelectionP_content']//span[contains(@class,'ui-treenode-label')]")
	List<WebElement> MobileWorkGroupSelectionList;
	
	@FindBy(xpath="//div[@id='WorkgroupSelectionFormUserAdmin:workgroupSelectTree']//span[contains(@class,'ui-treenode-label')]")
	List<WebElement> MobileWorkGroupSelectionListForAdminUser;
	
	@FindBy(xpath="//li[contains(@id, 'SingleWorkgroupSelectionFormUserAdmin:workgroupSelectTree')]//li[contains(@id, 'SingleWorkgroupSelectionFormUserAdmin:workgroupSelectTree')]//span[contains(@class, 'ui-treenode-label')]/span")
	List<WebElement> TagNameList;
	
	@FindBy(xpath="//div[@id='editMobWorkGroupFrom:mobileWGEdit']//span[text()='Delete']")
	WebElement MobWorkGrpDeleteBtn;
	
	@FindBy(xpath="//div[@id='statusDialog_content']/center")
	WebElement loadingBarImage;
	
	@FindBy(xpath="//div[contains(@id, 'confirmDeleteId')]//span[text()='Yes']")
	WebElement YesBtn;
	
	@FindBy(xpath="//tbody[@id='mobileWGSearchTabs:mobileWGSearchListForm:workGroupList_data']//td[text()='No records found.']")
	WebElement workGroupListData;
	
	@FindBy(className="ui-messages-warn-summary")
	WebElement warningMessageElement;
	
	@FindBy(xpath="//div[@id='editMobWorkGroupFrom:mobileWGEdit']//span[contains(text(), 'Cancel')]")
	WebElement cancelBtn;
	
	WebDriverWait wait=new WebDriverWait(driver, 10);
	
	public void SelectAllWorkGroupFromList(String btnAction) {
		for (WebElement singleCheckBox : this.WorkGroupSelectionList) {
			singleCheckBox.findElement(By.className("ui-chkbox")).click();
		}
		
		this.clickworkGroupBtn(btnAction);
	}
	
	public void clickworkGroupBtn(String btnAction) {
		new CommonUtils().singleElementToReturn(this.workGroupSelectionFooter.findElements(By.className("ui-button-text")), btnAction).click();
	}
	
	public void clickworkGroupBtnForAdminUser(String btnAction) {
		new CommonUtils().singleElementToReturn(this.workGroupSelectionFooterForAdminUser.findElements(By.className("ui-button-text")), btnAction).click();
	}
	
	public void clickSingleWorkGroupBtnForAdminUser(String btnAction) {
		new CommonUtils().singleElementToReturn(this.singleWorkGroupSelectionFooterForAdminUser.findElements(By.className("ui-button-text")), btnAction).click();
	}
	
	public String getWorkGroupName() {
		return this.WorkGroupNameList.get(0).getText();
	}
	
	public void EnterWorkGroupNameAndSelect(String workGroupName) throws InterruptedException {
		new CommonUtils();
		CommonUtils.enterText(this.selectWorkGroupInput, workGroupName);
		new CommonUtils().singleElementToReturn(this.MobileWorkGroupSelectionList, workGroupName).click();
		
		this.clickworkGroupBtn("OK");
	}
	
	public void SelectSingleworkGroupForMobileUser(String workGroupName) throws InterruptedException {
		new CommonUtils();
		CommonUtils.enterText(this.selectSingleWorkGroupInputForAdminUser, workGroupName);
		new CommonUtils().singleElementToReturn(this.TagNameList, workGroupName).click();
		
		this.clickSingleWorkGroupBtnForAdminUser("OK");
	}
	
	public void EnterWorkGroupNameAndSelectForMobileUser(String workGroupName) throws InterruptedException {
		new CommonUtils();
		CommonUtils.enterText(this.selectWorkGroupInputForAdminUser, workGroupName);
		new CommonUtils().singleElementToReturn(this.MobileWorkGroupSelectionListForAdminUser, workGroupName).click();
		
		this.clickworkGroupBtnForAdminUser("OK");
	}
	
	public void clickDeleteButton() {
		this.MobWorkGrpDeleteBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public void clickYesButton() {
		this.YesBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
	
	public boolean VerifyZeroRecordPresent() {
		try {
			return this.workGroupListData.isDisplayed();
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean VerifyWarningMessage(String messageToVerify) {
		try {
			wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
			return this.warningMessageElement.getText().trim().contains(messageToVerify);
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public void clickCancelBtn() {
		this.cancelBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(loadingBarImage));
	}
}
