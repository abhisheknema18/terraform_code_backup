package com.capita.fusionPortalPages;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import BasePackage.Basepage;
import utility.CommonUtils;

public class FieldSmart_HomePage extends Basepage{
	public FieldSmart_HomePage () {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(className="navLink")
	List<WebElement> navLinkList;
	
	@FindBy(className="ui-commandlink")
	List<WebElement> AdminDropOptions;
	
	@FindBy(xpath="//a[contains(@onclick,'mobileWorkGroupSearchForm')]")
	WebElement SearchMobileWorkGroupsElement;
	
	@FindBy(xpath="//a[contains(@onclick, 'adminUserSearchWidget')]")
	WebElement SearchMobileUserElement;
	
	@FindBy(xpath="//a[text()='Manage Mobile Work Group Category']")
	WebElement ManageMobileWorkGroupCategory;
	
	@FindBy(xpath="//a[text()='Add Access Groups']")
	WebElement AddAccessGroups;
	
	
	@FindBy(xpath="//a[contains(@onclick, 'mobileWorkGroupAddWidget')]")
	WebElement AddMobileWorkGroupElement;
	
	@FindBy(xpath="//a[contains(@onclick,'adminUserEditWidget')]")
	WebElement AddMobileUserElement;
	
	@FindBy(id="menuForm:viewWorkBtn")
	WebElement SearchWorkBtn;
	
	@FindBy(id="menuForm:viewWorkAllocationBtn")
	WebElement WorkAssignmentBtn;
	
	@FindBy(id="menuForm:viewWorkCloseureApproveBtn")
	WebElement WorkClosureApprovalBtn;
	
	public void selectAdminOptionsElement(String menuItem, String subMenuItem ) throws InterruptedException{
		new Actions(driver).moveToElement(new CommonUtils().singleElementToReturn(this.navLinkList, "Admin")).build().perform();
		new CommonUtils().singleElementToReturn(this.AdminDropOptions, menuItem).click();
		TimeUnit.SECONDS.sleep(1);
	
		switch(subMenuItem) {
		case "Search Mobile Users":
			this.SearchMobileUserElement.click();
			break;
		case "Add Mobile User":
			this.AddMobileUserElement.click();
			break;
		case "Search Mobile Work Groups":
			this.SearchMobileWorkGroupsElement.click();
			break;
		case "Add Mobile Work Group":
			this.AddMobileWorkGroupElement.click();
			TimeUnit.SECONDS.sleep(1);
			break;
		case "Manage Mobile Work Group Category":
			this.ManageMobileWorkGroupCategory.click();
			TimeUnit.SECONDS.sleep(1);
			break;
		case "Search System Users":
			break;
		case "Add System User":
			break;
		case "View All Access Groups":
			break;
		case "Search Access Groups":
			break;
		case "Add Access Groups":
			this.AddAccessGroups.click();
			TimeUnit.SECONDS.sleep(1);
			break;
		case "Create Mobile User Invitation":
			break;
		case "View Mobile User Invitations":
			break;
		case "Create System User Invitation":
			break;
		case "View System User Invitations":
			break;
		case "Shared Access Tokens":
			break;
		case "Add Access Token":
			break;
		case "View Access Token":
			break;
		}
		
		TimeUnit.SECONDS.sleep(2);
	}
	
	public void selectWorkOptionsElement(String menuOptions) throws InterruptedException {
		new CommonUtils().singleElementToReturn(this.navLinkList, "Work").click();
		TimeUnit.SECONDS.sleep(1);
		
		switch(menuOptions) {
		case "Search":
			this.SearchWorkBtn.click();
			break;
		case "Work Assignment":
			this.WorkAssignmentBtn.click();
			break;
		case "Work Closure Approval":
			this.WorkClosureApprovalBtn.click();
			break;
		}
	}
	
	public void RefreshPage() {
		driver.navigate().refresh();
	}
}
