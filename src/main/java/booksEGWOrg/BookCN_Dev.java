package booksEGWOrg;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import general.Book;
import general.BookReference;
import general.PageParaRef;
import general.Paragraph;
import general.TextProcessor;

import java.text.SimpleDateFormat;
import java.text.ParseException;

public class BookCN_Dev extends BookCN {

	public BookCN_Dev (
			String name, 
			String shortName, 
			int priority)
	{
		super(name, shortName, priority, null, null);
		
	}
	
	/* Get the date from English chapter name of devotional
	 * 
	 */
	private static Date getDateFromChapterName (String chapter)
	{
		String dt = chapter.substring(chapter.lastIndexOf(",")+1);
		
		SimpleDateFormat df = new SimpleDateFormat("MMMMM d");
		
		try 
		{
			Date date = df.parse(dt.trim());
			return date;
		} catch (ParseException e)
		{
			return null;
		}	
		
	}
	

	
	private String NavFindDate (String cndate)
	{
		String url = "http://text.egwwritings.org/search.php?lang=zh&collection=78&section=all&QUERY=" +
				shortName + "+\"" + cndate + "\"";
		driver.get(url);
		
		String xp = "//span[contains(text(),'" + cndate + "')]/..";
		List <WebElement> es_txt = driver.findElements(By.xpath(xp));//.."));
		
		xp = "//span[contains(text(),'" + cndate + "')]/../preceding-sibling::*[1]";
		List <WebElement> es_link = driver.findElements(By.xpath(xp));
		
		if (es_txt.size() != es_link.size())
			throw new IllegalArgumentException();
		
		Iterator<WebElement> it = es_txt.iterator();
		
		boolean found = false;
		
		String url_cn = null;
		
		for (WebElement e: es_link)
		{
			WebElement a = e.findElement(By.tagName("a"));
			
			WebElement hightlight = it.next();
			
			String hl = hightlight.getText();
			
			int i = hl.indexOf(cndate);
			
			if (i>0 &&
				TextProcessor.numSet.contains(hl.substring(i-1,i)))
				// This is not a real date... the date has one more digit before
				continue;
			else if (found)
					// duplicate, should give a warnig
				System.err.println ("duplicate date");
			else
			{
				url_cn = a.getAttribute("href");
				found = true;
			}
		}

		if (url_cn!=null)
			driver.get(url_cn);
		
		return url_cn;
	}
	
/*	private Paragraph getParagraph(int paraNum)
	{
		List <WebElement> es = driver.findElements(   By.className("reference"));// .partialLinkText(this.shortName + " "));
		
		for (WebElement e : es)
		{
			String text[] = e.getText().split("[\\. ]");
			
			int pnum = Integer.parseInt(text[2]);
			
			if (pnum == paraNum)
			{
				//Found
				
				text = text.substring(0, i - shortName.length());
				
				Paragraph p = new Paragraph (text);
				
				return p;
			}
		}
		return null;
	}*/
	
	public Paragraph getPageParagraph(PageParaRef ref)
	{
		// Turn EN book to page
		Book bookEn = ref.getBook();
		
		bookEn.getWebDriver();
		bookEn.NavPageNum(ref.getPageNum());
		
		// Get chapter name and date
		String title = bookEn.getChapterName();
		Date date = getDateFromChapterName(title);
		
		bookEn.releaseWebDriver();

		getWebDriver();
		
		String dateCN = TextProcessor.getCNDateString (date); // new
		
		String url=NavFindDate(dateCN);
		
		Paragraph p = getParagraph(ref.getParaNum());
		
		BookReference r = new BookReference(this, dateCN);// r.setURL(url);
		
		r.setURL(url);
		
		releaseWebDriver();
		
		p.bestRef = r;
		
		
		return p;
	}
	
}
