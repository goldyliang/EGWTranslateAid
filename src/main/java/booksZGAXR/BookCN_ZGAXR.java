package booksZGAXR;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import general.Book;
import general.BookReference;
import general.GoogleSearch;
import general.PageParaRef;
import general.Paragraph;
import general.TextProcessor;

public class BookCN_ZGAXR extends general.Book {

	public BookCN_ZGAXR (
			String name, 
			String shortName, 
			int priority,
			String url)
	{
		super (Book.Language.CN, name, shortName, priority);
		this.baseUrl = url;
	}
	

	@Override
	public Paragraph getPageParagraph(PageParaRef ref) {
		
		Paragraph p = null;
		
		getWebDriver();
		
        String strPagePara = ref.getPageNum() + "." + ref.getParaNum();
        String strBook = ref.getBook().getShortName();
        
		List <WebElement> allLinks = GoogleSearch.searchSite(
				driver, 
				"www.zgaxr.com/", 
				"\"" + strBook + "\" \"" + strPagePara + "\"");
		
        WebElement para = null;
        
        for (WebElement link : allLinks) {
        	//WebElement href = link.findElement(By.tagName("a"));
           // System.out.println(link.getText());
            
            WebElement pp = link.findElement(By.xpath("../.."));
            String s = pp.getText();
            
            //Judge whether this is a valid result
            if (s.indexOf(strBook)>=0 && s.indexOf(strPagePara)>=0) {
            	// should be valid, click the link
            	driver.get(link.getAttribute("href"));
            	
            	// find the paragraph
            	// Note: xpath  //*[contains(text(), "xxx")] does not always work
            	//       but    //*[text()[contains(., "xxx")]] works
            	String xpathstr = "//*[text()[contains(.,'" + strPagePara + "')]]"; // why this does not work???
            	List<WebElement> elems = driver.findElements(By.xpath(xpathstr ));
           		
            	if (elems!=null && !elems.isEmpty())
            		para = elems.get(0);
            	
           		break;
            }
        }
        
        if (para!=null) {
	        // may look up several levels until we get the Chinese text
	        do {
	        	String s = para.getText();
	        	
	        	if (!TextProcessor.containsChineseWords(s)) {
	        		// no Chinese found yet, get another paraent
	        		para = para.findElement(By.xpath(".."));
	        	} else
	        		break;
	        } while (para!=null);
	        
	        if (para!=null) {
		        // remove {...} in the text
		        String text = para.getText();
		   		text = text.replaceAll("\\{.*\\}",""); 
		        		
		        p = new Paragraph( text );
		        
		        String chapter = this.getChapterName();
		        
		        BookReference r = new BookReference(this, chapter);// r.setURL(url);
		
				r.setURL(driver.getCurrentUrl());
				
				p.bestRef = r;
	        }
        } 
        
        if (para==null) {
        	// we don't find the exact paragraph. Return a artificial paragraph
        	// with guidance of manual search
        	p = new Paragraph ("[[页码和段数搜索失败. 可以尝试点击以下链接手动搜索.]]");
        	BookReference r = new BookReference(this, name);
        	r.setURL(baseUrl);
        	p.bestRef = r;
        }

        releaseWebDriver();
        return p;

	}
	
	
	@Override
	public String NavNextPage() {
		System.err.println ("Navigation not supported - " + this.shortName);
		return null;
	}


	@Override
	public String NavPrevPage() {
		System.err.println ("Navigation not supported - " + this.shortName);
		return null;
	}


	@Override
	public String NavPageNum(int num) {
		System.err.println ("Navigation not supported - " + this.shortName);
		return null;
	}


	@Override
	public String getChapterName() {
		return driver.getTitle();
	}





}
