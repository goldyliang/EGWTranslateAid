package general;

import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GoogleSearch {

	static long lastGoogleVisit = 0;
	static final long DELAY_GOOGLE = 8000; // msec
	

	// wait until any of the element got by xpaths, or timeout
	// return the index of the search matched. or -1 if timeout
	private static int waitForResult (WebDriver driver, List<String> xpaths, long timeout) {
        // wait until we get the manual code verification, or get the div of result
        // or timeout
        long end = System.currentTimeMillis() + timeout;

		int res = -1;

        while (System.currentTimeMillis() < end) {
        	
        	for (int i = 0; i<xpaths.size(); i++) {
        		String xpath = xpaths.get(i);
        		WebElement elem = null;
	        	try {
	        		elem = driver.findElement(By.xpath(xpath));
	        	} catch (NoSuchElementException e) {
	        		elem = null;
	        	}
	        	if (elem!=null) {
	        		res = i;
	        		break;
	        	}
        	}
        	
        	if (res>=0) break;
        }
        
        return (res);
	}
        	
	public static List<WebElement> searchSite (WebDriver driver, String site, String text) {
		// Ensure not navigating into google too frequently
		// Check last time visit and delay if needed
		long tm = System.currentTimeMillis();
		if (lastGoogleVisit>0 && (tm-lastGoogleVisit < DELAY_GOOGLE)) {
			try {
				Thread.sleep( lastGoogleVisit + DELAY_GOOGLE - tm );
			} catch (InterruptedException e) {
				//ignore
			}
			// get current time again
			tm = System.currentTimeMillis();
		}
		lastGoogleVisit = tm;
		
		// Go to google
		driver.get("http://www.google.com");
		
        // Enter the query string "Cheese"
        WebElement query = driver.findElement(By.name("q"));
        
        String qstr = "site:" + site + " " + text;
        		
        query.sendKeys(qstr);
        
        WebElement btn = driver.findElement(By.name("btnG"));
        btn.click();
        
        // wait until we get the manual code verification, or get the div of result
        // or timeout
        int matched = waitForResult (driver, 
        		Arrays.asList("//*[contains(@alt, 'Please enable images')]",
        				      "//*[@class='rgsep'") , 5000 );
        
        // check if the manual code needs to be input
        if (matched == 0) {
        	// required manual operation here
        	// create a popup
        	JOptionPane.showMessageDialog(null, "Google asks for manual confirmation. Click OK after confirmation is done.");
        }
        
        // And now list the results
        List <WebElement> allLinks = driver.findElements(By.xpath("//h3[@class='r']/a"));	
        return allLinks;
	}
	
}
