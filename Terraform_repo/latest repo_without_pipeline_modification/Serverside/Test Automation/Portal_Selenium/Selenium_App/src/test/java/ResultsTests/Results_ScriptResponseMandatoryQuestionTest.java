package ResultsTests;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.capita.fusionPortalPages.Results_ResultSearchGeneralPage;
import com.capita.fusionPortalPages.Results_ScriptResponseMandatoryQuestionPage;

import BasePackage.Basepage;

public class Results_ScriptResponseMandatoryQuestionTest extends Basepage{
	
	Results_ResultSearchGeneralPage results_ResultSearchGeneralPage;
	Results_ScriptResponseMandatoryQuestionPage results_ScriptResponseMandatoryQuestionPage;
	
	
		
/*	
	@Test
	public void editReponseForQuestionYourSystemUserNameNA() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.editReponseForQuestionYourSystemUserNameNA();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void photographQuestionTypeViewOnly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.photographQuestionTypeViewOnly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void bitmapQuestionTypeViewonly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.bitmapQuestionTypeViewonly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void ConditionQuestionTypeEdit() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.ConditionQuestionTypeEdit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void userSignatureQuestionTypeViewonly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.userSignatureQuestionTypeViewonly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void CSVQuestionTypeViewonly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("CSV Question");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.CSVQuestionTypeViewonly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void GPSQuestionViewOnly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.GPSQuestionViewOnly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test@Test
	public void LevelQuestionTypeEdit() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("level question");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.LevelQuestionTypeEdit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void SinglePickQuestionTypeEdit() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.SinglePickQuestionTypeEdit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void UserCalculationQuestionTypeViewonly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.UserCalculationQuestionTypeViewonly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void HeadingQuestionTypeViewonly() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.HeadingQuestionTypeViewonly();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void DecimalUOMQuestionTypeEdit() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.DecimalUOMQuestionTypeEdit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void TaskListQuestionTypeEdit() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.TaskListQuestionTypeEdit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void MultiPickRuleBasedQuestion() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.InCarEntertainmentSystemQuestion();
			//Calling following method which follows the above line method
			results_ScriptResponseMandatoryQuestionPage.ManufacturersQuestion();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void SinglePickRuleBasedQuestion() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.CDchanger();
			//Calling following method which follows the above line method
			results_ScriptResponseMandatoryQuestionPage.carEntertainmentType();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void MultiPickQuestionType() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.MultiPickQuestionType();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void DateQuestion() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.DateQuestion();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	@Test
	public void TimeQuestion() {
		results_ResultSearchGeneralPage=new Results_ResultSearchGeneralPage();
		try {
			results_ResultSearchGeneralPage.scriptResultSearchByScriptTypes("Fieldreach test script");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results_ScriptResponseMandatoryQuestionPage=new Results_ScriptResponseMandatoryQuestionPage();		
		try {
			results_ScriptResponseMandatoryQuestionPage.TimeQuestion();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
