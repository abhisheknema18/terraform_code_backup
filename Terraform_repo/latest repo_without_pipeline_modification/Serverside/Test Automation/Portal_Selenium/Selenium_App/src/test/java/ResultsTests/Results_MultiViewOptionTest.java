package ResultsTests;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.Results_ScriptResultSearchPage;

import BasePackage.Basepage;

public class Results_MultiViewOptionTest extends Basepage{
	
	Results_ScriptResultSearchPage results_ScriptResultSearchPage;
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;
	
	@Test
	public void multiViewOptionDisabledWhenAllRecordsSelected() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		
		try {
			results_ScriptResultSearchPage.multiViewOptionDisabledWhenAllRecordsSelected();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void multiViewOptionDisabledForDifferentScriptVersion() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		
		try {
			results_ScriptResultSearchPage.multiViewOptionDisabledForDifferentScriptVersion("PRO-NSP-TIMEBAS/2","PRO-NSP-TIMEBAS/4");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	@Test
	public void multiViewOptionEnabledForSameScriptVersion() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		
		try {
			results_ScriptResultSearchPage.multiViewOptionEnabledForSameScriptVersion("PRO-NSP-TIMEBAS/2");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
