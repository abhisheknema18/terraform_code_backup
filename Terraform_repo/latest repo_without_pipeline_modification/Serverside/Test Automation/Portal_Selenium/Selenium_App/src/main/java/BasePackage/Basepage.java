package BasePackage;

import java.io.File;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;
import com.capita.fusionPortalPages.portalLogin;
import com.mongodb.diagnostics.logging.Logger;
import com.relevantcodes.extentreports.LogStatus;
import utility.CommonUtils;

import com.capita.fusionPortalPages.portalLogin;

public class Basepage {

	public static File file;
	public static Properties prop;
	public static FileInputStream fis;
    public static WebDriver driver;
    public ExtentHtmlReporter htmlReporter;
    public static ExtentReports extent;
    public static ExtentTest logger;

    portalLogin portallogin;

     @BeforeSuite
    @Parameters(value = {"browsername"})
    public void beforeSuite(String browsername) throws IOException{
    	Date date=new Date();
    	String filname=browsername+"_"+date.toString().replace(":", "_").replace(" ", "_").concat(".html");
    	
    	File reportDir = new File(System.getProperty("user.dir")+File.separator+"reports");
    	FileUtils.forceMkdir(reportDir);
        htmlReporter=new ExtentHtmlReporter(System.getProperty("user.dir")+File.separator+"reports"+File.separator+filname);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setDocumentTitle("Fusion Automation Test Report");
        htmlReporter.config().setReportName("Fusion Automation Test Execution Result");
        htmlReporter.config().setTheme(Theme.STANDARD);
        extent=new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("QA","10.79.120.60:8080");
    }
        @BeforeTest
        @Parameters(value = {"browsername", "username", "password"})
        public void beforeTest(String browsername, String username, String password){
        setupDriver(browsername);
        driver.manage().window().maximize();
        String filepath=System.getProperty("user.dir")+"//src//main//java//Config//data.properties";
		Basepage.initializeProp(filepath);
        driver.get(Basepage.readProperty("applicationurl"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        portallogin=new portalLogin();
		portallogin.login(username, password);
    }


    @BeforeMethod
    public void beforeMethod(Method testMethod){
        logger=extent.createTest(testMethod.getName());
        
    }

    @AfterMethod
    public void afterMethod(ITestResult result){
		
        if (result.getStatus()==ITestResult.SUCCESS){
            String methodName=result.getMethod().getMethodName();
            String logText="Test Case : "+methodName+" PASSED";
            Markup m= MarkupHelper.createLabel(logText, ExtentColor.GREEN);
            logger.log(Status.PASS, m);
        }else if (result.getStatus()==ITestResult.FAILURE){
            String methodName=result.getMethod().getMethodName();
            String logText="Test Case : "+methodName+" FAILED";
            Markup m= MarkupHelper.createLabel(logText, ExtentColor.RED);
            logger.log(Status.FAIL, m);
            logger.log(Status.FAIL, result.getThrowable());
            CommonUtils.RefreshPage(driver);
        }
      

    }
    
    

    @AfterTest
    public void afterTest(){
    	portallogin = new portalLogin();
		portallogin.logout();
		driver.quit();
		
		String downloadLocation=System.getProperty("user.dir")+"\\src\\main\\java\\Config\\PortalDownloads";
    	File downloadDir = new File(downloadLocation);
		try {
			if(downloadDir.exists()) {
				FileUtils.deleteDirectory(downloadDir);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    @AfterSuite
    public void afterSuite(){
        extent.flush();
        
    }

    public void setupDriver(String browserName){
        if (browserName.equalsIgnoreCase("chrome")){
        	String downloadLocation=System.getProperty("user.dir")+"\\src\\main\\java\\Config\\PortalDownloads";
        	
        	File downloadDir = new File(downloadLocation);
        	try {
				FileUtils.forceMkdir(downloadDir);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//add arguments method to set the chrome experimental flags
            ChromeOptions chromeOptions = new ChromeOptions().
             addArguments("--verbose","--headless","--unsafely-treat-insecure-origin-as-secure=http://10.79.120.60:8080","--disable-web-security","--ignore-certificate-errors","--allow-running-insecure-content","--allow-insecure-localhost","--no-sandbox","--disable-gpu","--window-size=1920,1080","start-maximized");
        	chromeOptions.setAcceptInsecureCerts(true);
            
            HashMap<String, Object> pref=new HashMap<String, Object>();
            pref.put("plugins.always_open_pdf_externally", true);
            pref.put("download.prompt_for_download", false);
            pref.put("safebrowsing.enabled", true);
            pref.put("download.default_directory", downloadLocation);
            
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setExperimentalOption("prefs", pref);
            
          //Using WebDriverManager to make the driver initialization independent of browser driver exe
    		WebDriverManager.chromedriver().setup();
    		driver = new ChromeDriver(chromeOptions);

        }else if (browserName.equalsIgnoreCase("edge")){
        	EdgeOptions edgeOptions = new EdgeOptions();
        	edgeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        	WebDriverManager.edgedriver().setup();
            driver=new EdgeDriver(edgeOptions);
        }else driver=null;
    }
    
	// Method to initialize the properties file
	public static void initializeProp(String filepath) {
		file = new File(filepath);
		try {
			fis = new FileInputStream(file);
			prop = new Properties();
			prop.load(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("FileNotFoundException" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Method to read a property from properties file
	public static String readProperty(String property) {
		return prop.getProperty(property);
	}

}
