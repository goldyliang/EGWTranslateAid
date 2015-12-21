package booksEGWOrg;

import java.util.List;
//import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import general.Book;
import general.PageParaRef;
import general.Paragraph;
import general.TextProcessor;

public class Book_EGWOrg extends Book {


	
	public Book_EGWOrg (
			Language lang,
			String name, 
			String shortName, 
			int priority
			)
	{
		super(lang, name, shortName, priority);
		
		baseUrl = "http://text.egwwritings.org/publication.php?pubtype=Book&collection=78&section=all&bookCode=" 
		+ shortName;
		//&pagenumber=44
	}
	
	
	public String NavNextPage() 
	{
		return NavPageNum(curPage+1);
	}
	
	public String NavPrevPage()
	{
		return NavPageNum(curPage-1);
	}
	
	public String NavNextChapter()
	{
		List <WebElement> ee = driver.findElements(By.linkText("Ch»"));
		
		if (!ee.isEmpty()) {
			ee.get(0).click();
			return driver.getCurrentUrl();
		} else 
			return null;
	}
	
	public String NavPrevChapter()
	{
		List <WebElement> ee = driver.findElements(By.linkText("«Ch"));
		
		if (!ee.isEmpty()) {
			ee.get(0).click();
			return driver.getCurrentUrl();
		} else 
			return null;
	}
	
	public String getChapterName() 
	{
		List<WebElement> ee = driver.findElements(By.tagName("h3"));
		
		if (ee.isEmpty())
		{
			if (NavPrevChapter() ==null )
				return null;
		}
		
		ee = driver.findElements(By.tagName("h3"));
		
		if (ee.isEmpty()) return null;
		
		return ee.get(0).getText();
	}

	public String NavPageNum(int page)
	{
		String url = baseUrl + "&pagenumber=" + Integer.toString(page);
		this.driver.get(url);
		curPage =page;
		
		return url;
	}
	
	public int getCurrentPageNum() {
		String url = driver.getCurrentUrl();
		
		int i = url.indexOf("pagenumber=");
		
		int[] index = new int[1];
		index[0] = i;
		
		return TextProcessor.getNextNumber (url, index);
	}
	
	private boolean validateRefString (String refStr, int pageNum, int paraNum)
	{
		String [] refFields = refStr.trim().split("[ \\.]");
		
		if ( refFields[0].toUpperCase().equals(shortName.toUpperCase()) &&
			 Integer.parseInt(refFields[1]) == pageNum &&
			 Integer.parseInt(refFields[2]) == paraNum )
			return true;
		else return false;
	}
	

	/* From the current page, try to get a validated paragraph
	 * with the page and para number.
	 * 
	 */
	private String getValidatedParagraph (int page, int para)
	{
		try
		{
			List<WebElement> eParas = driver.findElements(By.name("para" + para));
			
			for ( WebElement ePara: eParas)
			{
				WebElement ref = ePara.findElement(By.className("reference"));
				String refText = ref.getText();
			
				if (validateRefString(refText, page, para))
				{
					String text = ePara.getText();
			
					text = text.substring(0, text.indexOf(refText));
			
					return text;
				}
			}
			
			return null;
		} catch (Exception e)
		{
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	/* Get the #para paragaph from current page */
	public Paragraph getParagraph(int para)
	{
		// get page name from the book title bar
		WebElement h2 = driver.findElement(By.tagName("h2"));
		String t = h2.getText().toLowerCase();
		t = t.substring(t.indexOf("page")+4).trim();
		int page = Integer.parseInt(t);
		
		// get the valid paragraph from current page
		String text = getValidatedParagraph (page, para);
		if (text==null)
			return null;
		
		// go to next page and see whether the paragraph spans across the page
		NavPageNum( page + 1);
		
		String text2 = getValidatedParagraph (page, para);
		
		if (text2 != null)
		{
			// second part of the paragraph crossing the boundary of page
			// let's merge them
			text = text + text2;
		}
		
		// we need to go back one page to restore the condition
		driver.navigate().back();
		
		return new Paragraph (text);
	}
	
	public Paragraph getPageParagraph(PageParaRef ref)
	{
		int page = ref.getPageNum();
		int para = ref.getParaNum();
		
		String url = NavPageNum (page);

		//ref.setURL(url);  -- why do this??

		Paragraph p = getParagraph (para);

		if (p==null)
		{
			System.err.println("Paragraph not found!");
			System.err.println("\t" + this);
			System.err.println("\t" + ref);
			System.err.println("\t" + url);
		} else{
			p.bestRef = new PageParaRef (ref);
			p.bestRef.setURL(url);
		}
		
		return p;
	}
	
	private int curPage;
}
