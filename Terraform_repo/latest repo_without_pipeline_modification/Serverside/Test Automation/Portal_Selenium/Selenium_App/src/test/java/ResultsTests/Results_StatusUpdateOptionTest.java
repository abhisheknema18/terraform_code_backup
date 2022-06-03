package ResultsTests;

import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.Results_ScriptResultSearchPage;

import BasePackage.Basepage;

public class Results_StatusUpdateOptionTest extends Basepage{
	
	Results_ScriptResultSearchPage results_ScriptResultSearchPage;
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;
	
	@Test
	public void statusUpdateForSingleScript() throws InterruptedException {
		
		//Perform a general search
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		//Call method status update for single script
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		results_ScriptResultSearchPage.statusUpdateForSingleScript();
				
	}
	
	@Test
	public void statusUpdateDisabledForDifferentScriptVersion() throws InterruptedException {
		
		//Perform a general search
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		//Call method status update for single script
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		results_ScriptResultSearchPage.statusUpdateDisabledForDifferentScriptVersion("PRO-NSP-TIMEBAS/2","PRO-NSP-TIMEBAS/4");
				
	}
	
	@Test
	public void statusUpdateDisabledWhenAllRecordsSelected() throws InterruptedException {
		
		//Perform a general search
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		//Call method status update for single script
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		results_ScriptResultSearchPage.statusUpdateDisabledWhenAllRecordsSelected();
				
	}

	@Test
	public void statusUpdateForMultipleRecords() throws InterruptedException {
		
		//Perform a general search
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearch();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		//Call method status update for single script
		results_ScriptResultSearchPage=new Results_ScriptResultSearchPage();
		results_ScriptResultSearchPage.statusUpdateForMultipleRecords("Abhishek");	//("TMP-PARTS/1");			
	}	

}
