package journals;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bible.BibleCN;
import general.BooksCollection;
import general.Paragraph;


public class General {

	String pathIn, pathOut;
	
	BufferedReader reader;
	PrintStream output;
	
	public General (String pathIn, String pathOut) {
		
		this.pathIn = pathIn;
		this.pathOut = pathOut;
	}
	
	void loadAndTranslate() {
		
		//BibleCN bible = BibleCN.getBibleFromFile(true);
		if (BibleCN.BIBLE_CN==null) {
			System.err.println ("Bible file read error.");
			return;
		}
		
		Path pIn = Paths.get(pathIn);
		
		Charset charset = Charset.forName("UTF-8");
		
		try (	BufferedReader reader = Files.newBufferedReader(pIn, charset);
				PrintStream output = new PrintStream (pathOut)	) {

			this.reader = reader;
			this.output = output;
			
			OutputHeader();
			
		
			String text;
			
			
			// the rule is to scan each line, if it contains contents within "", or “”
			// they are either EGW paragraph , or bible. Search for those text
			while ( (text=reader.readLine()) != null ) {
				// replace “”  with " to simplify handling
				text = text.replaceAll("[“”]", "\"");
				
				// search each text in "", or single "
				int i = 0, j = 0;
				do {
					i = text.indexOf("\"", i);
					if (i >= 0) {
						j = text.indexOf("\"",i+1);
						
						if (j<0) j = text.length(); // we do not find the second ", still a valid paragraph

						// we found a paragraph
						String s = text.substring(i+1,j);
						
						// get the next non-blank char
						int k = j + 1;
						while (k<text.length() &&
							   Character.isSpaceChar(text.charAt(k)))
							k++;
						
						if (k < text.length() && text.charAt(k) == '(') {
							// it is a bible verse
							
							int k1 = text.indexOf(")",k+1);
							if (k1>0) {
								String verseText = text.substring(i,k1+1);
								String verse = text.substring(k+1,k1);
							
								if (BibleCN.BIBLE_CN!=null)
									OutputBibleVerseTable (verseText, BibleCN.BIBLE_CN.getVerseRangeText(verse));
							}

						} else {
							// it is a quote of EGW
							Paragraph p = new Paragraph (s, false);
							
							p.getCNByEN();
							output.println(p.formatHtmlOutput_Table());
						}
						
						i = j + 1;
					} // i >= 0
					
				} while (i>=0 && j>0);
			}
				
			OutputEnd();
		
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
	}
		
	void OutputHeader() {
		output.print("<html><head><meta charset=\"UTF-8\"> </head><body><table border=1>");
	}

	
	void OutputEnd() {
		output.print("</table></body></html>");
	}
	
	void OutputBibleVerse(String enVerse, String cnVerse) {
		output.println("<span>" + enVerse + "</span><br>");
		output.println("<span>" + cnVerse + "</span><br>");
	}
	
	void OutputBibleVerseTable (String enVerse, String cnVerse) {
		output.println ("<tr><td>" + enVerse + "</td>");
		output.println ("<td>" + cnVerse + "</td></tr>");
	}
	
	
	public static void main (String[] args) {
		
    	BooksCollection.loadBooks();

    	General article = new General ("/home/gordon/Documents/WOP/1-2.txt", "/home/gordon/Documents/WOP/1-2.html");
		
    	//General article = new General ("/home/gordon/Documents/WOP/test_verses", "/home/gordon/Documents/WOP/test.html");
    	
		article.loadAndTranslate();
	}
}
