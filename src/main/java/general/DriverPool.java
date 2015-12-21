package general;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class DriverPool {
	static private WebDriver driver;
	
	static public WebDriver getDriverInstance() 
	{
		FirefoxProfile profile = new FirefoxProfile();
		
/*		String path = "/home/gordon/quickjava-2.0.6-fx.xpi";
		try {
			profile.addExtension(new File (path));
		} catch (IOException e) {
			System.err.println ("File " + path + " not found. Images can not be disabled.");
		}
		
		profile.setPreference("thatoneguydotnet.QuickJava.curVersion", "2.0.6.1");
		profile.setPreference("thatoneguydotnet.QuickJava.startupStatus.Images", 2);
		profile.setPreference("thatoneguydotnet.QuickJava.startupStatus.AnimatedImage", 2);
		
		*/
		if (driver == null)
		{
			try
			{
/*			  java.net.URL uri = new java.net.URL ("http://localhost:7055/hub");
			  driver = new RemoteWebDriver(uri, DesiredCapabilities.firefox()); */
			 // driver = new FirefoxDriver();
				driver = new FirefoxDriver(profile);
			}
			catch (Exception e)
			{
			  //driver = new FirefoxDriver();
			}
	    //    driver = new FirefoxDriver();//HtmlUnitDriver();
		}
		
		return driver;
	}
	
	static public void releaseDriverInstance()
	{
		return;
	}
	
}
