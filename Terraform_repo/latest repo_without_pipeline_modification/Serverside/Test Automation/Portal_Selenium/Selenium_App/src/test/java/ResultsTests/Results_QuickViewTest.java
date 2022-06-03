package ResultsTests;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.Results_ScriptResultSearchPage;

import BasePackage.Basepage;

public class Results_QuickViewTest extends Basepage {

	Results_ScriptResultSearchPage results_ScriptResultSearchPage;
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;
	
	@Test
	public void verifyQuickViewOption() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		
		results_ScriptResultSearchPage.verifyQuickViewOption();
		
	}

}
