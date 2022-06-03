package utility;

import org.apache.commons.io.FileUtils;




import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import BasePackage.Basepage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;

public class SuiteListeners implements ITestListener, IAnnotationTransformer {

    @Override
    public void onTestStart(ITestResult result) {

    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {
    	
    /*	Date date=new Date();
        String filename=System.getProperty("user.dir")+ File.separator+"screenshots";
        String destination=filename+date.toString().replace(":", "_").replace(" ", "_")+result.getMethod().getMethodName()+".png";
        File file=((TakesScreenshot) Basepage.driver).getScreenshotAs(OutputType.FILE);
        String relativepath="../screenshots/"+destination;
        */
        Date date= new Date();
		String screenshotFile= date.toString().replace(":", "_").replace(" ", "_")+".png";
		String absolutePath = System.getProperty("user.dir")+File.separator+"screenshots"+File.separator+screenshotFile;
		String relativePath = ".."+File.separator+"screenshots"+File.separator+screenshotFile;
		File file=((TakesScreenshot) Basepage.driver).getScreenshotAs(OutputType.FILE);
		
        try {
        	FileUtils.forceMkdir(new File(System.getProperty("user.dir")+File.separator+"screenshots"));
            FileUtils.copyFile(file, new File(absolutePath));
            Basepage.logger.fail("Test Failed : ", MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

/*    @Override
    public void onTestFailedWithTimeout(ITestResult result) {

    }*/

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyser.class);
    }
}
