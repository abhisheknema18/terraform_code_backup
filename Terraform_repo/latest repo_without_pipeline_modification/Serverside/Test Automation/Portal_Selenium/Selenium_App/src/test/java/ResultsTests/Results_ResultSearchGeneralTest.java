package ResultsTests;

import org.testng.annotations.BeforeTest;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.portalLogin;

import BasePackage.Basepage;

public class Results_ResultSearchGeneralTest extends Basepage {
//	portalLogin portallogin;
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;

	@Test
	public void scriptResultSearchByAllFields() {
		results_ResultSearchGeneralPage = new Results_ResultSearchGeneralPage();
//		portallogin = new portalLogin();

		try {
//			portallogin.login();
			results_ResultSearchGeneralPage.scriptResultSearchByAllFields();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void scriptResultSearchByResultStatus() throws InterruptedException {
//		portallogin = new portalLogin();
//		portallogin.login();

		results_ResultSearchGeneralPage = new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.scriptResultSearchByResultStatus();

	}
	
	@Test
	public void scriptResultSearchByScriptTypes() throws InterruptedException {
//		portallogin = new portalLogin();
//		portallogin.login();

		results_ResultSearchGeneralPage = new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");

	}

	@Test
	public void scriptResultSearchByUser() throws InterruptedException {
//		portallogin = new portalLogin();
//		portallogin.login();

		results_ResultSearchGeneralPage = new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.scriptResultSearchByUsers();

	}

	@Test
	public void scriptResultSearchByWorkGroup() throws InterruptedException {
//		portallogin = new portalLogin();
//		portallogin.login();

		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		results_ResultSearchGeneralPage.scriptResultSearchByWorkGroups();
		
	}
}
