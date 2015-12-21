package booksEGWOrg;

import java.util.List;

import general.Book;

import java.util.ArrayList;

/* EN book, with the capability of access corresponding CN book with page# and paragraph#
 * 
 */
public class BookEN extends Book_EGWOrg{

	private List <Book> bookCNList = new ArrayList <Book> ();
	
	public BookEN (String name, String shortName, int priority)
	{
		super(Book.Language.EN, name, shortName, priority);
		
		baseUrl += "&lang=en";
	}
	
	public List<Book> getBookCNList() { return bookCNList;}
	
/*	public List<Paragraph> getCNByENReference (List<BookReference> refENList)
	{		
		List <Paragraph> r=new ArrayList<Paragraph>();

		// sort the CN books by priority
		class Entry implements Comparable<Entry>
		{
			public Book book; 
			public PageParaRef ref;
			public int compareTo(Entry x) {
					return (this.book.getPriority() - x.book.getPriority());
			  }
		}
		
		List < Entry > bookCNOrdered= new ArrayList <Entry>();
		
		for (BookReference refEN : refENList)
		{
			if ( refEN.getBook() != this ||
			   ! (refEN instanceof PageParaRef ) )
					throw new IllegalArgumentException();
			
			BookEN bookEN = (BookEN) (refEN.getBook());
			
			for (Book bookCN:bookEN.getBookCNList())
			{
				Entry en = new Entry();
				en.book = bookCN;
				en.ref = (PageParaRef) refEN;
				bookCNOrdered.add(en);
			}
		}
		
		Collections.sort(bookCNOrdered);
		
		for (Entry entry:bookCNOrdered)
		{
			PageParaRef ref = entry.ref;
			
			Book bookCN = entry.book;
			
			try 
			{
				bookCN.getWebDriver();
				Paragraph para = bookCN.getPageParagraph(ref);
				bookCN.releaseWebDriver();
				r.add(para);
			} catch (Exception e)
			{
			} finally
			{
				bookCN.releaseWebDriver();
			}
		}
		
		return r;
	}
	*/
	
}
