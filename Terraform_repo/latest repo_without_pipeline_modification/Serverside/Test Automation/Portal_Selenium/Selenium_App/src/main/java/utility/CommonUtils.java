package utility;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class CommonUtils {

	public static void selectDropDownValueByText(WebElement dropdown, String value) {

		Select select=new Select(dropdown);
		select.selectByVisibleText(value);
	}

	public static void selectDropDownValueByIndex(WebElement dropdown, int index) {

		Select select=new Select(dropdown);
		select.selectByIndex(index);
	}
	
	public static void selectDropDownValueByValue(WebElement dropdown, String value) {

		Select select=new Select(dropdown);
		select.selectByValue(value);
	}
	
	public static void selectRadioButton(WebElement radiobutton) {
		if (radiobutton.isDisplayed()) {
			radiobutton.click();			
		}
		else {
			System.out.println("Radio button is not displayed " + radiobutton);
		}		
	}
	
	public static void selectCheckBox(WebElement checkbox) {
		if(!checkbox.isSelected()) {
			checkbox.click();
		}
		else {
			System.out.println("Check box is already Selected ");
		}
	}
	
	public static void isImageDisplayed(WebElement image) {

		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(image.getAttribute("src"));
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				Assert.assertEquals(response.getStatusLine().getStatusCode(), 200, "Image is displayed");
			} else {
				System.out.println("Image is Broken");
			}

		} catch (

		IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String charRemoveAt(String str, int p) {  
        return str.substring(0, p) + str.substring(p + 1);  
     }  
	
	public WebElement singleElementToReturn(List<WebElement> elementList, String elementText) throws NullPointerException{
		WebElement ele = null;
		for(int i = 0; i <= elementList.size(); i++) {
			if(elementList.get(i).getText().contains(elementText)) {
				ele = elementList.get(i);
				break;
			}
		}
		return ele;
	}
	
	public static void enterText(WebElement element, String TextToEnter) throws InterruptedException {
		element.click();
		element.clear();
		element.sendKeys(TextToEnter);
		Thread.sleep(1500);
	}

	public static void RefreshPage(WebDriver driver) {
		driver.navigate().refresh();
	}

	public static int isFileDownloadedSuccessFully(String filepath, String filename) {
		int count=0;
		try {
			File filelocator = new File(filepath);

			File[] totalfiles = filelocator.listFiles();

			
			for (File temp : totalfiles) {
				if (temp.getName().equalsIgnoreCase(filename)) {
					// deleting the file after assertion so that next download will not conflict the
					// file name
					count++;
					temp.delete();
					break;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
}
