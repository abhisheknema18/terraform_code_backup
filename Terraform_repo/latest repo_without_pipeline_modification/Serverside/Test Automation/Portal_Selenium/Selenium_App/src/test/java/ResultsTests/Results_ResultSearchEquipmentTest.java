package ResultsTests;

import org.testng.Assert;
import org.testng.annotations.Test;


import com.capita.fusionPortalPages.Results_ResultSearchEquipmentPage;
import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.portalLogin;

import BasePackage.Basepage;

public class Results_ResultSearchEquipmentTest extends Basepage {
	
//	portalLogin portallogin;
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;
	Results_ResultSearchEquipmentPage results_ResultSearchEquipmentPage;
	

	@Test
	public void scriptSearchWithValidEquipmentReference() throws InterruptedException {
//		portallogin=new portalLogin();
//		portallogin.login();
		
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.navigateToResultSearch();
		
		results_ResultSearchEquipmentPage=new Results_ResultSearchEquipmentPage();
		results_ResultSearchEquipmentPage.validEquipmentSearch();	
		
	}
	
	@Test
	public void scriptSearchWithInvalidEquipmentReference() throws InterruptedException {
//		portallogin=new portalLogin();
//		portallogin.login();
		
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.navigateToResultSearch();
		
		results_ResultSearchEquipmentPage=new Results_ResultSearchEquipmentPage();
		results_ResultSearchEquipmentPage.invalidEquipmentSearch();	
		
	}

}
