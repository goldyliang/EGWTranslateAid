package booksEGWOrg;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import booksEGWOrg.BookCN_ST.CN_to_EN_Index;
import general.Book;
import general.DriverPool;
import general.GoogleSearch;
import general.PageParaRef;
import general.Paragraph;
import general.TextProcessor;

public class BookCN_ST_Test {

	@Test
	public void Test_TextProcessor() {
		assertEquals("一", TextProcessor.getChineseNum(1));
		assertEquals("九", TextProcessor.getChineseNum(9));

		assertEquals(1, TextProcessor.getFromChineseNum("一"));
		assertEquals(9, TextProcessor.getFromChineseNum("九"));
		assertEquals(0, TextProcessor.getFromChineseNum("○"));
		
		int[] idx = {0};
		String s = ",啊 109,209";
		
		assertEquals(109, TextProcessor.getNextNumber(s, idx));
		assertEquals(6, idx[0]);
		
		assertEquals(209, TextProcessor.getNextNumber(s, idx));
		assertEquals(10, idx[0]);
		
		s = ",二○一 - 二一九";
		idx[0]=0;
		
		assertEquals(201, TextProcessor.getNextNumber(s, idx));
		assertEquals(4, idx[0]);
		
		assertEquals(219, TextProcessor.getNextNumber(s, idx));
		assertEquals(10, idx[0]);
		
		s = ",九至十三";
		idx[0]=0;
		
		assertEquals(9, TextProcessor.getNextNumber(s, idx));
		assertEquals(2, idx[0]);
		
		assertEquals(13, TextProcessor.getNextNumber(s, idx));
		assertEquals(5, idx[0]);
		
		
		s = ", 117，“论疏忽”，一九○四年著";
		idx[0]=0;
		
		assertEquals(117, TextProcessor.getNextNumber(s, idx));
		assertEquals(5, idx[0]);
		
		assertEquals(-1, TextProcessor.getNextNumber(s, idx));
		
		s = ", 一一九，一九○○年著)";
		idx[0]=0;
		
		assertEquals(119, TextProcessor.getNextNumber(s, idx));
		assertEquals(5, idx[0]);
		
		assertEquals(-1, TextProcessor.getNextNumber(s, idx));
		
		
		
		s = "这些改革者们所高举的真理和宗教自由的旗帜，在这最后的斗争中已交在我们手中了。这个伟大恩赐的责任已落在那些蒙上帝赐福明白祂圣言之人的身上了。我们应接受上帝的道为最高的权威。我们自己必须亲自接受它的真理。而且我们只有在藉着亲自研究查究这些真理时，才能欣赏它们。然后，当我们使上帝的话成为自己人生的向导时，我们就响应了基督的祷告：“求祢用真理使他们成圣；祢的道就是真理”（约17:17）。用言语和行为承认真理，就是我们信仰声明。只有这样，别人才能知道我们相信《圣经》。";
		
		String rs = TextProcessor.toSingleQuote(s);
		
		System.out.println (rs);
		
		assertEquals("这些改革者们所高举的真理和宗教自由的旗帜，在这最后的斗争中已交在我们手中了。这个伟大恩赐的责任已落在那些蒙上帝赐福明白祂圣言之人的身上了。我们应接受上帝的道为最高的权威。我们自己必须亲自接受它的真理。而且我们只有在藉着亲自研究查究这些真理时，才能欣赏它们。然后，当我们使上帝的话成为自己人生的向导时，我们就响应了基督的祷告：‘求祢用真理使他们成圣；祢的道就是真理’（约17:17）。用言语和行为承认真理，就是我们信仰声明。只有这样，别人才能知道我们相信《圣经》。",
				       rs);
		
	}
	
	@Test
	public void Test_RegString () {
		String s = "adfdsf{1T 201.1}";
		
		System.out.println (s.replaceAll("\\{.*\\}", ""));
	}
	
	@Test
	public void Test_BookCN_EGWOrg_ST_LoadIdx () {
		
		BookCN_ST book1 = new BookCN_ST ("证言精选 卷一", "ST1", 1, false);
		
		assertTrue(book1.isIndexValid());
		
		List <CN_to_EN_Index> pageIndex = book1.getBookPageRangeIndex();
		assertEquals(110, pageIndex.size());  // one of the chapter invalid for ST1
		
		
		BookCN_ST book2 = new BookCN_ST ("证言精选 卷二", "ST2", 1, false);
		
		assertTrue(book2.isIndexValid());
		
		pageIndex = book2.getBookPageRangeIndex();
		assertEquals(76, pageIndex.size());  // one of the chapter invalid for ST1
		
		BookCN_ST book3 = new BookCN_ST ("证言精选 卷三", "ST3", 1, false);
		
		assertTrue(book3.isIndexValid());
		
		pageIndex = book3.getBookPageRangeIndex();
		assertEquals(78, pageIndex.size());  // one of the chapter invalid for ST1
		
		Book bkEN1 = new Book_EGWOrg(Book.Language.EN, "Testimonies for the Church, vol. 1",
				"1T", 1);
//		Book bkEN2 = new Book_EGWOrg(Book.Language.EN, "Testimonies for the Church, vol. 2",
//				"2T", 1);
//		Book bkEN3 = new Book_EGWOrg(Book.Language.EN, "Testimonies for the Church, vol. 3",
//				"3T", 1);

		
		// test search
		PageParaRef ref = new PageParaRef(bkEN1, "p.142.2");
		Paragraph p = book1.getPageParagraph(ref);
		assertNotEquals (null, p);
		
		// test search (null)
		ref = new PageParaRef(bkEN1, "p.150.2");
		p = book1.getPageParagraph(ref);
		assertNull ( p);  // */
		
	}
	
	@Test
	public void Test_XPath () {
		WebDriver d = DriverPool.getDriverInstance();
		
		d.get("http://www.zgaxr.com/book/005/21/16.htm");
		
		{
	       	// find the paragraph
			String s = "101.4";
	    	String xpathstr = "//*[text()[contains(.,'" + s + "')]]"; // why this does not work???
	    	List<WebElement> elems = d.findElements( //By.xpath("//*"));
	    			By.xpath(xpathstr ));
	    	
	    	for (WebElement e: elems) {
	    		System.out.println("<" + e.getTagName() + ">" + e.getText());
	    	}
	   		
	    	//assertFalse (elems.isEmpty());
		}
    	
	}
	
	@Test
	public void testGoogleSearch () {
		
		WebDriver driver = DriverPool.getDriverInstance();
		
		List<WebElement> elems = GoogleSearch.searchSite(driver, "www.zgaxr.com", "\"1T\" \"210.3\"");
		
		assertTrue(!elems.isEmpty());
		
		
		 elems = GoogleSearch.searchSite(driver, "www.zgaxr.com", "\"1T\" \"215.3\"");
		 
			assertTrue(!elems.isEmpty());

	}

	
}
