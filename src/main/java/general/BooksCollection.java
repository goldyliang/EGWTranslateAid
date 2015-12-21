package general;

import java.util.Map;
import java.util.TreeMap;

import booksEGWOrg.BookCN;
import booksEGWOrg.BookCN_Dev;
import booksEGWOrg.BookCN_ST;
import booksEGWOrg.BookEN;
import booksZGAXR.BookCN_ZGAXR;

import java.util.List;

import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.io.IOException;

import java.io.BufferedReader;
import java.nio.charset.Charset;

public class BooksCollection {
	
	static private void addCNBook (String shortNameEn, Book book)
	{
		String shortNameCn = book.getShortName();
		
		BooksCn.put(shortNameCn, book);
		
		BookEN bookEN = BooksEnByShortName.get(shortNameEn);
		
		if (bookEN!=null)
		{
			List <Book> cnBookList = bookEN.getBookCNList();
			cnBookList.add(book);
		} else
			throw new IllegalArgumentException();
	}
	
	static private void addENBook (BookEN book)
	{
		BooksEn.put(book.getName(), book); // key by full name
		BooksEnByShortName.put(book.getShortName(), book); // key by short name
	}
	
/*	static private void addBook (String nameEn, String shortNameEn, String nameCn, int priority)
	{
		Book ben = new Book (Book.Language.EN, nameEn, shortNameEn, priority);
		Book bcn = new Book (Book.Language.CN, nameCn, "", priority);
		
		BooksEn.put(nameEn, ben);
		BooksCn.put(nameCn, bcn);
//		EnCnMap.put(nameEn, bcn);
	}*/
	
	static private final String bookCNLoadFile = "CNBookList.csv";
	static private final String bookENLoadFile = "ENBookList.csv";
	
	static boolean checkFields (String header, String ... fields)
	{
		int i=0;
		
		String [] fds = header.split("\t");
		
		if (fds.length != fields.length ) return false;
		
		for (String f:fds)
		{
			if (! f.equals(fields[i++].trim()))
				return false;
		}
		
		return true;
		
	}
	static public void loadBooks()
	{

		Path path = FileSystems.getDefault().getPath("config", bookENLoadFile);
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedReader reader = Files.newBufferedReader( path , charset)) {
			
		    String line = reader.readLine();
		    
		    if ( ! checkFields(line, "ShortName", "ENName") )
		    	throw new IllegalArgumentException();
		    
		    while ((line = reader.readLine()) != null) {
		    	
		    	String [] fields = line.split("\t");
		    	for (String s:fields) s=s.trim();
		    	
				addENBook (new BookEN(
						fields[1], fields[0], 0 ) );
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
		path = FileSystems.getDefault().getPath("config", bookCNLoadFile);
		
		try (BufferedReader reader = Files.newBufferedReader( path , charset)) {
			
		    String line = reader.readLine();
		    
		    if ( ! checkFields(line, "EnShortName", "CNName", "CNShortName", "BookClass", "Property1", "Property2", "Property3") )
		    	throw new IllegalArgumentException();
		    
		    while ((line = reader.readLine()) != null) {
		    	
		    	String [] fields = line.split("\t");
		    	for (String s:fields) s=s.trim();
		    	
		    	//verify if the EN book name is valid
		    	if (BooksEnByShortName.get(fields[0])==null) {
		    		// should not happen, some error
		    		System.err.println ("Error found in file " + path + " - EN book name not valid: " + fields[0]);
		    	}
		    	
		    	switch (fields[3])
		    	{
		    	case "EGW_WRITINGS_ORG":
					addCNBook ( 
							fields[0], // EN short name
							new BookCN (
									fields[1], //CN name
								fields[2], // CN short name
								Integer.parseInt(fields[4]), // priority
								fields[5], // Title type
								fields[6]  // L1 Title seperator
										)
							);
					break;
		    	case "EGW_WRITINGS_ORG_DEV":
		    		addCNBook (
		    				fields[0],
		    				new BookCN_Dev(
		    						fields[1],
		    						fields[2],
		    						Integer.parseInt(fields[4])
		    						)
		    				);
		    		break;
		    	case "EGW_WRITINGS_ORG_ST":
		    		addCNBook (
		    				fields[0],
		    				new BookCN_ST(
		    						fields[1],
		    						fields[2],
		    						Integer.parseInt(fields[4]),
		    						false  // do not recreate index
		    						)
		    				);
		    		break;		
		    	case "EGW_ZGAXR":
		    		addCNBook (
		    				fields[0],
		    				new BookCN_ZGAXR (
		    						fields[1],
		    						fields[2],
		    						Integer.parseInt(fields[4]),
		    						fields[5])
		    				);
		    		break;
		    	default:
		    		System.err.println("Book class not defined:" + fields[3]);
		    		break;
		    	}

		    }
		} catch (Exception x) {
		    System.err.format("Exception: %s%n", x);
		    x.printStackTrace();
		    
		}
			
	}
	

	
	static public Map<String, BookEN> BooksEn = new TreeMap<String,BookEN> (); // key by full name
	static public Map<String, BookEN> BooksEnByShortName = new TreeMap<String,BookEN> (); // key by short name
	static public Map<String, Book> BooksCn = new TreeMap<String,Book> (); // key by short name (CN)
	
//	static public Map<String, List<Book>> EnCnMap = new TreeMap <String, List<Book>> ();
//	static public Map<String, Book>  EnCnMap = new TreeMap<String,Book> ();
}
