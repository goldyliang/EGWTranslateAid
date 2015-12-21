package journals;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bible.BibleCN;
import general.BooksCollection;
import general.Paragraph;


public class Devotional {

	String pathIn, pathOut;
	
	BufferedReader reader;
	PrintStream output;
	
	public Devotional (String pathIn, String pathOut) {
		
		this.pathIn = pathIn;
		this.pathOut = pathOut;
	}
	
	void loadAndTranslate() {
		
		BibleCN bible = BibleCN.getBibleFromFile(true);
		if (bible==null) {
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
			
			int state = 0; //0 - nothing; 1 - got a date; 2 - got the title; 3- within text
			
			String text = reader.readLine();
			
			Date dt = null;
			
			while ( text != null ) {
				
				boolean readNewLine = true; // whether a new line shall be read after the switch statement
				
				
				try {
					switch (state) {
						case 0:	{
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
							dt = sdf.parse(text);
							if (dt==null) {// error
								break; // wait for next 
							}
							OutputDate (dt);
							state = 1;
							break;
						}
						case 1: {
							if (!text.trim().isEmpty()) {
								// got the title
								OutputTitle (text);
								state = 2;
							}
							break;
						}
						case 2: {
							// now get the memory verse
							int i = text.indexOf("“");
							if (i<0) i = text.indexOf("\"");
							
							int j = text.indexOf("”", i+1);
							if (j<0) j = text.indexOf("\"", i+1);
							
							if (i<0 || j<0) {
								System.err.println ("Expect verse:" + text);
							}
							
							i = text.indexOf("(", j+1);
							j = text.indexOf(")", i+1);
							
							if (i<0 || j<0) {
								System.err.println ("Expect verse:" + text);
							}
							
							String verse = text.substring(i+1, j);
							
							OutputMemoryVerse (text, bible.getVerseRangeText(verse));
							state = 3;
							break;
						}
						case 3: {
							// within texts
							// check whether it is empty
							if (text.isEmpty()) break;
							
							// check whether if it is a new date
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
							Date dt1 = null;
							
							try {dt1= sdf.parse(text);} catch (Exception e) {}
							
							if (dt1!=null) {
								
								// it is a new data
								
								// print out the status
								System.out.println ("==================   Completed " + dt.toString()+ "   ======================");
								state = 0;
								readNewLine = false; // do not read new line for next round
								break;
							}
							
							// now still within text
							
							// remove the last index number if it is there
							int i = text.length()-1;
							char c;
							do {
								c = text.charAt(i--);
							} while (i>0 && (Character.isDigit(c) || Character.isSpaceChar(c)));
							
							String para = text.substring(0, i+2);
							
							Paragraph p = new Paragraph (para, false);
							
							p.getCNByEN();
							
							output.println(p.formatHtmlOutput_Plain());
							break;
						}
					} // end of switch
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
				
				if (readNewLine) {
					text = reader.readLine();
				}
			} 
				
			OutputEnd();
		
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
	}
		
	void OutputHeader() {
		output.print("<html><head><meta charset=\"UTF-8\"> </head><body>");
	}
	
	void OutputTitle(String title) {
		output.println("<h3>" + title + "</h3>");
	}
	
	void OutputEnd() {
		output.print("</body></html>");
	}
	
	void OutputMemoryVerse(String enVerse, String cnVerse) {
		output.println("<span>" + enVerse + "</span><br>");
		output.println("<span>" + cnVerse + "</span><br>");
	}
	
	void OutputDate (Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("M月d日", Locale.CHINESE);
		output.println("<h3>" + sdf.format(date) + "</h3>");
	}
	
	
	public static void main (String[] args) {
		
    	BooksCollection.loadBooks();

    	Devotional dev = new Devotional ("/home/gordon/fear not/9-12", "/home/gordon/fear not/9-12.html");
		
		dev.loadAndTranslate();
	}
}
