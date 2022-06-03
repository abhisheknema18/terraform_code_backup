package com.capita.fusionPortalPages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import BasePackage.Basepage;

public class Dashboard_AddScriptResultDashboardPage extends Basepage {
	
	public Dashboard_AddScriptResultDashboardPage () {
		PageFactory.initElements(driver, this);
	}
	//Script Result Dashboard - last 28 days		
	@FindBy(xpath="//span[text()='Dashboard']")
	WebElement dashboardTab;
	
	@FindBy(xpath="//a[@id='menuForm:addNewDBBtn']/span")
	WebElement addDashboardOption;
	
	@FindBy(xpath="//div[@id='dashAddForm:editPanel_content']/ul/li[1]")
	WebElement scriptResultsOption;
	
	@FindBy(xpath="//input[@id='dashEditFormADD:title']")
	WebElement enterTitle;
	
	@FindBy(xpath="//input[@id='dashEditFormADD:dateRange']")
	WebElement dateRange;
	
	@FindBy(xpath="//div[@id='dashEditFormADD:editPanel_content']//button[@id='dashEditFormADD:saveBtn']")
	WebElement okButton;
	
	@FindBy(xpath="//table[@id='dashEditFormADD:xAxis']//td[2] ")
	WebElement resultStatusRadioButton;
	
	@FindBy(xpath="//table[@id='dashEditFormADD:allXSelected']//td[2]")
	WebElement selectedScriptTypeRadioButton;
	
	@FindBy(xpath="//table[@id='dashEditFormADD:xAxisSelectPanel']//td[2]/button")
	WebElement selectButton1;
	
	@FindBy(xpath="//span[text()='AMT-SYBEX']")
	WebElement selectScriptType;
	
	@FindBy(xpath="//div[@id='dashEditFirstFilterFormADD:editFilterPanelADD_content']//button/span[text()='OK']")
	WebElement okButton1;
	
	@FindBy(xpath="//table[@id='dashEditFormADD:allXFilterSelected']//td[2]")
	WebElement selectedStatuseseRadioButton;
	
	@FindBy(xpath="//table[@id='dashEditFormADD:xFilterSelectPanel']//td[2]/button")
	WebElement selectButton2;
	
	@FindBy(xpath="//div[@id='dashEditSecondFilterFormADD:xFilterSelectTree']/ul//li//span[text()='ACCEPTED']")
	WebElement selectStatus1;
	
	@FindBy(xpath="//div[@id='dashEditSecondFilterFormADD:xFilterSelectTree']/ul//li//span[text()='COMPLETED']")
	WebElement selectStatus2;
	
	@FindBy(xpath="//div[@id='dashEditSecondFilterFormADD:editFilterPanelADD_content']/button/span[text()='OK']")
	WebElement okButton2;
	
	
	
	public void addNewDashboard(String name, String days) throws InterruptedException {
		
		Actions action = new Actions(driver);
		action.moveToElement(dashboardTab).build().perform();
		WebDriverWait wait = new WebDriverWait(driver, 5); 
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@id='menuForm:addNewDBBtn']/span")));
		addDashboardOption.click();
		TimeUnit.SECONDS.sleep(3);
		scriptResultsOption.click();
		TimeUnit.SECONDS.sleep(3);
		enterTitle.clear();
		enterTitle.sendKeys(name);
		dateRange.clear();
		dateRange.sendKeys(days);
		

	}
	
	public void selectResultStatus() throws InterruptedException {
		
		WebDriverWait wait = new WebDriverWait(driver, 5); 
		resultStatusRadioButton.click();
	}
	
	public void selectScriptType() throws InterruptedException {
		
		
		TimeUnit.SECONDS.sleep(5);
		selectButton1.click();	
		TimeUnit.SECONDS.sleep(5);
		selectScriptType.click();
		TimeUnit.SECONDS.sleep(5);
		okButton1.click();
		TimeUnit.SECONDS.sleep(5);
	}
	
	public void selectStatuses() throws InterruptedException {
		
//		TimeUnit.SECONDS.sleep(5);
//		selectedStatuseseRadioButton.click();
		TimeUnit.SECONDS.sleep(5);
		selectButton2.click();
		TimeUnit.SECONDS.sleep(5);
		selectStatus1.click();
		TimeUnit.SECONDS.sleep(5);
		selectStatus2.click();
		TimeUnit.SECONDS.sleep(5);
		okButton2.click();
		TimeUnit.SECONDS.sleep(5);
	}

	
	public void verifyDashboardIsAdded(String name) throws InterruptedException
	{
		WebDriverWait wait = new WebDriverWait(driver, 5); 
		okButton.click();
		//wait for the created dashboard to appear 
		//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//form/div/div[@id='dashItemForm1:dashItem1_header']/span")));
		TimeUnit.SECONDS.sleep(3);
		//WebElement dashboardTitle= driver.findElement(By.xpath("//form/div/div/span[text()='Script Results']"));
		WebElement dashboardTitle= driver.findElement(By.xpath("//form/div/div/span[text()='"+name+"']"));
		Assert.assertEquals(dashboardTitle.getText(), name);
		Assert.assertEquals(dashboardTitle.getText(), name, "dashboard "+name+ " found");
		System.out.println("Dashboard added successfully");
	}
	



	
}
