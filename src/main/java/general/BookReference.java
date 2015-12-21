package general;

import booksEGWOrg.BookEN;


public class BookReference {
	private Book book;
	private String refString;
	private String url;
	
	public BookReference (Book book, String refString)
	{
		this.book = book;
		this.refString = refString;
	}
	
	public BookReference (BookReference ref) {
		book = ref.book;
		refString = ref.refString;
		url = ref.url;
	}
	
	public Book getBook()
	{
		return book;
	}
	
	public void setURL (String url)
	{
		this.url = url;
	}
	
	public String getURL () {return url;}
	
	public String getRefString () {return refString;}
	
	static public BookReference byString (String str)
	{
		int i = str.lastIndexOf(',');
		
		String bookname = str.substring(0, i);
		String refString = str.substring(i+1);
		
		try {
			Book book = BooksCollection.BooksEn.get(bookname);
			
			if (book==null) {
				// some thing wrong
				// the book name might be a short name end with a space
				// move the substring of bookname to the refString
				int j = bookname.indexOf(" ");
				refString = bookname.substring(j+1) + "," + refString;
				bookname = bookname.substring(0, j);
				
				book = BooksCollection.BooksEnByShortName.get(bookname);
				
				if (book ==null) {
					// still not found the book
					System.err.println ("Book not found:" + str);
					
					// create a new book for this
					book = new BookEN (bookname, "", 0);
				}
			}
			
			try {
				PageParaRef ref=new PageParaRef(book, refString);
				return ref;
			} catch (Exception e) {
				// can not convert to page/para ref, use general reference instead
				return new BookReference(book, refString);
			}
		} catch (Exception e)
		{
			System.err.println ("Find book error " + bookname + "-" + refString);
			e.printStackTrace(System.err);
			return null;
		}
		
	}
	
	public String toString()
	{
		StringBuilder r=new StringBuilder();
		
		boolean isCN = (book.lang == Book.Language.CN);
		
		if (isCN)
			r.append("《");
		
		r.append(book.getName());// .toString());
		
		if (isCN)
			r.append("》");
		else
			r.append(",");
		
		r.append(refString);
		
		if (isCN)
			r.append("。");
		else
			r.append(".");
		
		return r.toString();
	}
	
	public Paragraph locate() {return null;}
}
