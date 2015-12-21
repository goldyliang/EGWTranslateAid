package booksEGWOrg;


import general.BookReference;
import general.PageParaRef;
import general.Paragraph;

public class BookCN extends Book_EGWOrg {

	private String title_seperator;
	
	public BookCN (
			String name, 
			String shortName, 
			int priority,
			String title_type,   //this looks like no use??
			String title_seperator)
	{
		super(Language.CN, name, shortName, priority);
		baseUrl += "&lang=zh";
		this.title_seperator = title_seperator;
	}
	


	String getChapterNumFromTitle (String title)
	{
		
		
//		int code = title.codePointAt(5);
		
//		System.out.println (code); //12288
		
		if (title_seperator.equals("-"))
			// no seperator, return the whole title
			return title;
		
		int i = title.indexOf( title_seperator);// '\u3000'); // Chinese space
		
		if (i>0)
			return title.substring(0,i + title_seperator.length());
		else
			return title;
	}
	

	public Paragraph getPageParagraph(PageParaRef ref)
	{
		Paragraph p = super.getPageParagraph(ref);
		
		if (p==null)
		{
			System.err.println ("Paragraph not found!");
			System.err.println (this);
			System.err.println (ref);
			
			return null;
		}
		
		String title = getChapterName();
		
		if (title == null)
		{
			System.err.println ("Title not found!");
			System.err.println (this);
			System.err.println (p);
			System.err.println (ref);
		} else
		{
			BookReference r = new BookReference (this, getChapterNumFromTitle(title));
		
			r.setURL(p.bestRef.getURL());
		
			p.bestRef = r;
		}
		
		return p;
	}

}
