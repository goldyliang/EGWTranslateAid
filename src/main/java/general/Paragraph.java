package general;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Exception;
import java.util.Comparator;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import bible.BibleCN;
import booksEGWOrg.BookEN;

public class Paragraph {

	private static final int PERCENT_CRITERIA = 50;

	private static final int MAXIMUM_CN_RESULT = 5;
	
	public List<BookReference> refs;
	public BookReference bestRef;
	
	private String text;
	
	private List<Paragraph> pCN_List;
	
	boolean isBible;
	
	// construce EGW paragraph
	public Paragraph(String text, boolean isBible)
	{
		this.text = text;
		this.isBible = isBible;
	}
	
	public Paragraph(String text) {
		this.text = text;
		this.isBible = false;
	}
	
	public String getText() { return text;}
	
	public String toString ()
	{
		StringBuffer r = new StringBuffer(text);
		
		r.append(TextProcessor.eol);
		
		int i=1;
		if (refs!=null)
			for (BookReference ref:refs)
			{
				r.append("Ref#" + String.valueOf(i++));
				r.append(TextProcessor.eol);
	
				r.append(ref.toString());
				r.append(TextProcessor.eol);
			}
		
		return r.toString(); //text + "\n" + bestRef.toString();
	}
	
/*	public String setUrl();
	public String getUrl();
	public String getText();
	public String setText();
	public String getFirstSentence();*/
	
	private List<WebElement> searchAndGetElements (String query)
	{
		// Go to the search result page using the whole paragraph text
		StringBuffer urlbuf = new StringBuffer("http://text.egwwritings.org/search.php?lang=en&collection=2"
				+ "&section=all&Search=Search&paragraphReferences=1&QUERY=");
			
		urlbuf.append(query);
		
		String url = urlbuf.toString();

		WebDriver driver = DriverPool.getDriverInstance();

		driver.get(url);
		
	    // Go through all book references
        return	driver.findElements(By.className("mb5"));		
	}
	
	
	/* Search for the text in English books, and get the list of reference
	 * 
	 */
	public void searchTextEn()
	{
		if (text==null) throw new IllegalArgumentException();
		
		refs = new ArrayList <BookReference>();
		
		
		StringBuffer buf=new StringBuffer();
		
		List <String> parts = TextProcessor.getParaParts(text);
		
		if (parts.isEmpty()) return;
		
		// query all sub-string as whole sentences, use the OR relation
		for (String part:parts)
		{
			System.out.println (part);
			buf.append("\"" + part + "\"|");
		}
		
		buf.deleteCharAt(buf.length()-1);
		
		String query = buf.toString();
		
		List<WebElement> r = searchAndGetElements(query);
       
		/*
        if (r.isEmpty())
        {
        	// nothing found, could be a little typo. try the longest to shortest part to search instead
        	
        	Collections.sort(parts, new Comparator<String> () {
        		public int compare(String a, String b)
        		{
        			return b.length() - a.length();
        		}
        	});
        	
        	Iterator <String> p = parts.iterator();
        	
        	while (r.isEmpty() && p.hasNext())
            	r = searchAndGetElements("\"" + p.next() + "\"");
        	
        	if (r.isEmpty())
        		//still no result
        		return;
        } */
        
        bestRef = null;
        
        for (WebElement b:r)
        {
        	try {
        		// filter the % number. Remove those with little revelant percentage
        		String titleStr = b.getText();
        		int i = titleStr.lastIndexOf("(");
        		int j = titleStr.lastIndexOf("%");
        		if (i<0 || j<0 || j<=i) {
        			// not valid
        			System.out.println ("Not valid searched item:\n" + titleStr);
        			continue;
        		}
        		int percent = Integer.parseInt(titleStr.substring(i+1, j));
        		if (percent < PERCENT_CRITERIA)
        			continue; // skip result with too low percentage
        		
        		
        		WebElement aa= b.findElement(By.tagName("a"));
        		BookReference ref = BookReference.byString(aa.getText());
        		
        		if (ref==null) continue;
        		
        		ref.setURL( aa.getAttribute("href") );
        		refs.add(ref);
        		
        		if (bestRef == null || ref.getBook().getPriority() < bestRef.getBook().getPriority())
        			bestRef = ref;
        		
        	} catch (Exception e)
        	{
        		e.printStackTrace();
        	}
        }		

		
		DriverPool.releaseDriverInstance();
	}
	
	public void getCNByEN ()
	{		
		
		if (isBible) {
			return;
		}
		
		searchTextEn();
		
		if (pCN_List == null) pCN_List = new ArrayList<Paragraph>();
		
		List <Paragraph> r = pCN_List;
		
		class Entry implements Comparable<Entry>
		{
			public Book book; 
			public PageParaRef ref;
			public int compareTo(Entry x) {
					return (this.book.getPriority() - x.book.getPriority());
			  }
		}
		
		List < Entry > bookCNOrdered= new ArrayList <Entry>();
		
		for (BookReference refEN : refs)
		{
			if ( ! (refEN instanceof PageParaRef ) )
				continue; // not a page/para reference, can not do anything
			
			BookEN bookEN = (BookEN) (refEN.getBook());
			
			for (Book bookCN:bookEN.getBookCNList())
			{
				Entry en = new Entry();
				en.book = bookCN;
				en.ref = (PageParaRef) refEN;
				bookCNOrdered.add(en);
			}
		}
		
		// DO NOT sort the list. As the order represent the most relevant to the least
		//Collections.sort(bookCNOrdered);
		
		int result_cnt = 0;
		
		for (Entry entry:bookCNOrdered)
		{
			PageParaRef ref = entry.ref;
			
			Book bookCN = entry.book;
			
			try 
			{
				bookCN.getWebDriver();
				Paragraph para = bookCN.getPageParagraph(ref);
				bookCN.releaseWebDriver();
				
				if (para!=null) 
				{
					r.add(para);
					result_cnt ++;
					if (result_cnt >= MAXIMUM_CN_RESULT)
						break; // enough result got
				}
			}  catch (Exception e) {
				//some error
				String errStr = "Error getting page paragraph. Refer to error log for detail : " + ref;
				System.err.println (errStr);
				e.printStackTrace(System.err);
				Paragraph para_err = new Paragraph (errStr, false);
				r.add(para_err);
			} finally
			{
				bookCN.releaseWebDriver();
			}
		}
		
	}
	
	public String formatHtmlOutput_Plain()
	{
		StringBuffer r=new StringBuffer();
		
		r.append("<p>-----------------------------------------------------------------------------</p>"+TextProcessor.eol);
		
		r.append("<p>" + getText() + "</p>" + TextProcessor.eol);
		
		if (isBible) {
			String s = getText();
			int i = s.lastIndexOf("(");
			int j = s.lastIndexOf(")");
			
			if (i<0 || j<0 || i>=j)
				r.append("<h4> Error in bible verse </h4>");
			else if (BibleCN.BIBLE_CN!=null) {
				r.append("<p>");
				r.append(BibleCN.BIBLE_CN.getVerseRangeText(s.substring(i+1, j)));
				r.append("</p>");
			} else
				r.append("<p>Bible load error</p>");
		} else {
		
			if(refs.isEmpty())
			{
				r.append("<h4> No search result, check manually </h4>"+TextProcessor.eol);
			} else {
				r.append("<ul>");
				for (BookReference ref: refs)
				{
					r.append ("<li>");
					r.append ("<a target ='_blank' href='" + ref.getURL() + "'>");
					r.append (ref.toString());
					r.append ("</a></li>" + TextProcessor.eol);
				}
				r.append("</ul>");
			}
	
			
			//System.out.println("“" + pEN.getText() + "”");
			for (Paragraph p:pCN_List)
			{
				BookReference ref = p.bestRef;
				r.append (
						"<p>" + p.getText()	+ p.bestRef.toString() + "</p>");
				
				r.append ("<a target ='_blank' href='" + ref.getURL() + "'> [Open page] </a>");
				r.append( TextProcessor.eol);
			}
		}
		
		r.append("<p>-----------------------------------------------------------------------------</p>"+TextProcessor.eol);
		
		return r.toString();
	}
	
	public String formatHtmlOutput_Table()
	{
		StringBuffer r=new StringBuffer();
		
		r.append("<tr> <td valign=top width=\"30%\">" + TextProcessor.eol);
		
		r.append("<p>" + getText() + "</p>" + TextProcessor.eol);
		
		if (isBible) {
			String s = getText();
			int i = s.lastIndexOf("(");
			int j = s.lastIndexOf(")");
			
			if (i<0 || j<0 || i>=j)
				r.append("<p> Error in bible verse </p></td><td></td></tr>");
			else if (BibleCN.BIBLE_CN!=null) {
				r.append("</td><td>>");
				r.append(BibleCN.BIBLE_CN.getVerseRangeText(s.substring(i+1, j)));
				r.append("</td></tr>");
			} else
				r.append("<p> Bible load error </p></td><td></td></tr>");
			
			return r.toString();
		}

		
		if(refs == null || refs.isEmpty())
		{
			r.append("<h4> No search result, check manually </h4>"+TextProcessor.eol);
		} else {
			r.append("<ul>");
			for (BookReference ref: refs)
			{
				r.append ("<li>");
				r.append ("<a target='_blank' href='" + ref.getURL() + "'>");
				r.append (ref.toString());
				r.append ("</a></li>" + TextProcessor.eol);
			}
			r.append("</ul>");
		}
		
		r.append("</td> <td valign=top width=\"70%\"> <ul>" + TextProcessor.eol);
		
		//System.out.println("“" + pEN.getText() + "”");
		for (Paragraph p:pCN_List)
		{
			BookReference ref = p.bestRef;
			
			r.append (
					"<li> <p>“" + TextProcessor.toSingleQuote(p.getText()) 
					+ "”");
			if (ref!=null)
				r.append(ref.toString());
			r.append( "</p>");
			
			if (ref!=null)
				r.append ("<a target='_blank' href='" + ref.getURL() + "'> [Open page] </a> </li>");
			
			r.append(TextProcessor.eol);
		}
		
		r.append("</ul></td></tr>");
		
		return r.toString();
	}
	
/*	public Paragraph getBestCNParagraph ()
	{
		return bestRef.getBook().getCNByENReference(bestRef);
	}*/
	
//	public convertReferenceTo {Reference ref};
}