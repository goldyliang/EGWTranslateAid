package booksEGWOrg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import general.BookReference;
import general.PageParaRef;
import general.Paragraph;
import general.TextProcessor;

public class BookCN_ST extends BookCN {

	
	// class to store the index from EN book to CN book
	// each record, storing the relationship between:
	//     The book name and page ranges in the EN 1T~8T book
	//     The starting page number in this book
	public static class CN_to_EN_Index {
		public String shortNameEN;
		public int fromEN, toEN;
		public int fromCN;
		public CN_to_EN_Index (String shortNameEN, int fromEN, int toEN, int fromCN) {
			this.shortNameEN = shortNameEN;
			this.fromEN = fromEN;
			this.toEN = toEN;
			this.fromCN = fromCN;
		}
		
		public String toString() {
			return shortNameEN + "; fromEN:" + fromEN + "; toEN:" + toEN + "; fromCN" + fromCN;
		}
	}
	
	// List of EN/CN index information
	List <CN_to_EN_Index>  listBookPageRange = new ArrayList<CN_to_EN_Index> (50);
	
	boolean indexValid;
	
	public boolean isIndexValid() { return indexValid;}
	
	public List <CN_to_EN_Index> getBookPageRangeIndex () { return listBookPageRange;}
	
	String getIndexFileName () {
		return shortName + "-index.csv";
	}
	
	public BookCN_ST (
			String name, 
			String shortName, 
			int priority,
			boolean recreateIdx
			)
	{
		super (name, shortName, priority, 
				"", ""); // skip the title type and title separator, as this book will has
		                 // his own mechanism
		
		if (recreateIdx || !loadIndexFromFile ()) {
			// Index file error or not exist, or need recreation
			// search and create the index
			
			if (!createIndex() || !writeIndexToFile()) {
				// index creation and store error
				// mark index not valid
				indexValid = false;
			} else indexValid = true;
		} else indexValid = true;
	}
	
	// Load the chapter index from a file
	boolean loadIndexFromFile () {
		
		Path path = FileSystems.getDefault().getPath("index_data", getIndexFileName());
		
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedReader reader = Files.newBufferedReader( path , charset)) {
			
		    String line;
		    
		    while ((line = reader.readLine()) != null) {
		    	
		    	String [] fields = line.split("\t");
		    	
		    	if (fields.length != 4) {
		    		System.err.println ("Index file " + path + " format error.");
		    		return false;
		    	}
		    	
		    	int from = Integer.parseInt(fields[1]);
		    	int to = Integer.parseInt(fields[2]);
		    	int fromCN = Integer.parseInt(fields[3]);
		    	
		    	CN_to_EN_Index  index = new CN_to_EN_Index (fields[0], from, to, fromCN);
		    	
		    	listBookPageRange.add(index);
		    }
		} catch (IOException x) {
		    //System.err.format("IOException: %s%n", x);
			System.err.println ("No index file found " + path.toString());
		    return false;
		}
		
		return true;
					
	}
	
	// get the EN <-> CN index described in desc
	// fromEN not yet fill in the return structure
	CN_to_EN_Index getIndexFromDesc (String desc) {
		
		// sample descriptions:
		//    证言卷一, 120-121
		//    证言卷五, 二三六至二四八
		//    证言卷五, 九四至九八
		//    证言卷五, 九八至一○五
		//    证言卷六, 404-408
		//    证言卷八, 156，157
		//    证言卷九, 281-284
		
		int i = desc.indexOf("卷");
		
		if (i<0) return null;
		
		String bookNum = desc.substring(i+1, i+2);
		
		int iBookNum = TextProcessor.getFromChineseNum(bookNum);
		
		i += 4;
		

		// find the first number
		int [] index = new int[1];
		index[0] = i;
		
		int p1 = TextProcessor.getNextNumber(desc, index);
		
		if (p1<0) return null;
		
		int p2 = TextProcessor.getNextNumber(desc, index);
		
		if (p2<0) 
			// there is no second number, assuming one page
			p2 = p1;

		return new CN_to_EN_Index( 
				iBookNum + "T", // EN book short name
				p1, p2, // from and to page number in EN
				0); // the EN page number TBD
		
	}
	
	// Search the book sources and create the index
	boolean createIndex () {
		
		getWebDriver();
		
		NavPageNum(1);  // go to page 1
		
		//test only
		//int cnt=0;
		
		do {
			// try to get the chapter description
			List<WebElement> elems = driver.findElements(By.className("center"));
			
			boolean newIndexFound = false;
			for (WebElement elem:elems) {
				String desc = elem.getText();
				// get book name from it
				CN_to_EN_Index index = getIndexFromDesc (desc);
				
				if (index!=null) {
					index.fromCN = this.getCurrentPageNum();
					listBookPageRange.add(index);
					newIndexFound = true;
					System.out.println ("Created index: " + index);
					break;
				}
			}
			
			if (!newIndexFound) {
				System.err.println ("WARNING: no index description found in:" + driver.getCurrentUrl());
			}
			
		} while (NavNextChapter()!=null);// && listBookPageRange.size()<10);
		
		releaseWebDriver();
		
		return (!listBookPageRange.isEmpty());
	
	}
	
	// Write the index to file
	boolean writeIndexToFile () {
		Path path = FileSystems.getDefault().getPath("index_data", getIndexFileName());
		
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedWriter writer = Files.newBufferedWriter( path , charset)) {
			
			for (CN_to_EN_Index index : listBookPageRange) {
				writer.write( 
						 index.shortNameEN + "\t"
						+index.fromEN + "\t"
						+index.toEN + "\t"
						+index.fromCN + "\n");
			}
			
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		    return false;
		}
		
		return true;
	}
	
	// Get the paragraph corresponding to ref
	// The ref is the page/paragraph in the EN books of 1T - 8T
	// While in the CN book of 1ST - 3ST, they are not correspondence 
	// So, a special lookup is implemented here
	public Paragraph getPageParagraph(PageParaRef ref) {
		
		// can not do anything without valid index
		if (!indexValid) return null;
		
		// search the index to find the page in listBookPageRange
		// (assuming only one found. It means that a page in EN book only belongs to one chapter)
		
		CN_to_EN_Index index = null;
		
		for (int i=1; i< listBookPageRange.size(); i++) {
			index = listBookPageRange.get(i);
			
			if (index!=null && 
			    ref.getBook().getShortName().equals( index.shortNameEN) &&
				ref.getPageNum() >= index.fromEN &&
				ref.getPageNum() <= index.toEN) {
				// found it
				break;
			} else index = null;
		}
		
		if (index == null) // can not find a match, return null
			return null;
					
		
		getWebDriver();
		
		String url = NavPageNum( index.fromCN );
		String chapter = getChapterName();
		
		List<WebElement> elems = this.driver.findElements(By.className("center"));
		
		String desc=null; // string of chapter description
		
		index = null;
		
		for (WebElement elem: elems) {
			desc = elem.getText();
			index = getIndexFromDesc(desc);
			
			if (index!=null)
				break;
		}
		
		if (index ==null) {
			// error, could not find the chapter description
			System.err.println ("Error, could not find the chapter description");
			return null;
		}
		
		Paragraph para = new Paragraph( chapter + 
				TextProcessor.eol + 
				desc + TextProcessor.eol + 
				"[... 需要点击以下链接,手工查找对应段落 ...]");
		
		BookReference r = new BookReference(this, chapter);// r.setURL(url);
		
		r.setURL(url);
		
		para.bestRef = r;
		
		releaseWebDriver();

		return para;
	}


}
