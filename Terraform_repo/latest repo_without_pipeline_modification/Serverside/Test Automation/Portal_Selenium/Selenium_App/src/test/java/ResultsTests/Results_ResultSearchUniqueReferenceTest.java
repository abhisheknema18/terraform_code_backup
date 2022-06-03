package ResultsTests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.Results_ResultSearchUniqueReferencePage;

import BasePackage.Basepage;

public class Results_ResultSearchUniqueReferenceTest extends Basepage {
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;
	Results_ResultSearchUniqueReferencePage results_ResultSearchUniqueReferencePage;
	
	@Test
	public void scriptSearchWithInvalidUniqueReference() throws InterruptedException {
		
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.navigateToResultSearch();
		
		results_ResultSearchUniqueReferencePage=new Results_ResultSearchUniqueReferencePage();
		results_ResultSearchUniqueReferencePage.invalidUniqueReferenceSearch();	
	
	}
	
	@Test
	public void scriptSearchWithInvalidInputUniqueReference() throws InterruptedException {
		
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.navigateToResultSearch();
		
		results_ResultSearchUniqueReferencePage=new Results_ResultSearchUniqueReferencePage();
		results_ResultSearchUniqueReferencePage.invalidSearchInput();
		
	}
	
	@Test
	public void scriptSearchWithValidInputUniqueReference() throws InterruptedException {
		
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.navigateToResultSearch();
		
		results_ResultSearchUniqueReferencePage=new Results_ResultSearchUniqueReferencePage();
		results_ResultSearchUniqueReferencePage.validUniqueReferenceSearch("36");
		
	}
	
}
