package journals;

import java.util.List;

import javax.swing.JOptionPane;

import bible.Verse;
import general.*;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

public class SBL {
	private List<String> textEN;
	
/*	public class EG_paragraph
	{
		public EG_paragraph (int pnum, String text)
		{
			pEN = new Paragraph(text);
			paraNum = pnum;
		}
		public Paragraph pEN;
		public List<Paragraph> pCN_List;
		public int paraNum;
		
		public String formatHtmlOutput_Plain()
		{
			StringBuffer r=new StringBuffer();
			
			r.append("<p>----------</p>"+TextProcessor.eol);
			
			r.append("<p>" + pEN.getText() + "</p>" + TextProcessor.eol);
			
			if(pEN.refs.isEmpty())
			{
				r.append("<h4> No search result, check manually </h4>"+TextProcessor.eol);
			} else
				for (BookReference ref: pEN.refs)
				{
					r.append ("<h4>");
					r.append ("<a href='" + ref.getURL() + "'>");
					r.append (ref.toString());
					r.append ("</a></h4>" + TextProcessor.eol);
				}
			
			//System.out.println("“" + pEN.getText() + "”");
			for (Paragraph p:pCN_List)
			{
				BookReference ref = p.bestRef;
				r.append (
						"<p>“" + p.getText() + "”" 
						+ p.bestRef.toString() + "</p>");
				
				r.append ("<a href='" + ref.getURL() + "'> [Open page] </a>");
				r.append(TextProcessor.eol);
			}
			
			return r.toString();
		}
		
		public String formatHtmlOutput_Table()
		{
			StringBuffer r=new StringBuffer();
			
			r.append("<tr> <td valign=top width=\"30%\">" + TextProcessor.eol);
			
			r.append("<p>" + pEN.getText() + "</p>" + TextProcessor.eol);
			
			if(pEN.refs == null || pEN.refs.isEmpty())
			{
				r.append("<h4> No search result, check manually </h4>"+TextProcessor.eol);
			} else {
				r.append("<ul>");
				for (BookReference ref: pEN.refs)
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
	} */
	
	private List<Paragraph> EG_paragraphs_List;
	
	/* Load and parse the English SBL
	 *  
	 */
	public boolean loadENSBL (String file_path)
	{
		Path path = Paths.get(file_path);
		Charset charset = StandardCharsets.UTF_8;

		boolean inPara=false;
		
		textEN = new ArrayList<String> ();
		
		EG_paragraphs_List = new ArrayList<Paragraph> ();
		
		try (BufferedReader reader = Files.newBufferedReader(path , charset)) {

			String line;
			StringBuffer p=null;

			while ((line = reader.readLine()) != null ) {
				
				boolean blank_line = (line.trim().isEmpty());
				boolean start_blank = (!blank_line && 
									   line.length()>2 &&
									   Character.isWhitespace(line.charAt(0)) &&
									   Character.isWhitespace(line.charAt(1)));
						
				if (!inPara)
				{
					if (blank_line) continue;
					
					// check whether it is a verse
					
/*					boolean isVerse = false;
					
					String text = line.replaceAll("[“”]", "\"");
					int i = text.lastIndexOf(")");
					if (i>0) {
						isVerse = true;
						// check if any text after )
						for (int j=i+1; j<line.length(); j++)
							if (Character.isLetter(line.charAt(j))) {
								isVerse = false;
								break;
							}
						
						if (!isVerse) break;
						
						// then check if there is ( and " before )
						int j = text.lastIndexOf("(");
						int k = text.lastIndexOf("\"");
						if (j<i && k<j) {
							
							for (int k=k+1; k<j; k++) {
								if (Character.isLetter(text.charAt(k))) {
									isVerse=false;
									break;
								}
							}
						}
						
						if (isVerse) {
							int i0 = text.indexOf("\"");
							textEN.add(text.substring(i0,i+1));
							continue;
						}
					}  */
					
					if (!start_blank)
					{
						// one line paragraph
						textEN.add(line);
					} else
					{
						inPara = true;
						p = new StringBuffer(line);
					}
				} else
				{
					if (blank_line || start_blank)
					{
						// paragraph ends
						String pp = p.toString();
						textEN.add(pp);
						inPara=false;
						
						if (start_blank)
						{
							p = new StringBuffer(line);
							inPara=true;
						} else // blank_line
							inPara=false;
					} else
					{
						// continue the paragrah
						p.append(line);
					}
				}
			}
		} catch (IOException e) {
		    System.err.println(e);
		    return false;
		}
		
		for (String para:textEN)
		{
			int i = para.indexOf("“");
			if (i<0) i = para.indexOf("\"");
			int j = para.lastIndexOf("”");
			if (j<0) j = para.lastIndexOf("\"", i+1);

			if (i<0) continue;
			
			if (j>0)
			{
				// detect if the paragraph is an EGW paragraph or bible
				// , if not, neglect
				int k = j+1;
				while (k < para.length() &&
					   Character.isWhitespace(para.charAt(k))) k++;
				if (k >= para.length() || 
					(para.charAt(k) != '—' &&
					para.charAt(k) != '-') &&
					para.charAt(k) != '(')
					continue;
				
				if (para.charAt(k) == '(') {
					//bible
					int k1 = para.lastIndexOf(")");
					EG_paragraphs_List.add ( new Paragraph( para.substring(i,k1+1), true));
				} else
					// new paragraph and add to the list
					EG_paragraphs_List.add( new Paragraph( para.substring(i+1,j), false));
			} else
				EG_paragraphs_List.add( new Paragraph( para.substring(i+1), false));

		}

/*		
		for (EG_paragraph p : EG_paragraphs_List)
		{
			System.out.println(p.paraNum);
			System.out.println (p.pEN);
			System.out.println("----");
		}
*/		
		return true;
	}
	
	public boolean buildCNSBL(String outputfile)
	{
		Path path = Paths.get(outputfile);
		Charset charset = StandardCharsets.UTF_8;

		if (Files.exists(path)) {
        	int option = JOptionPane.showConfirmDialog(null, 
        			"File " + outputfile + " exists, do you confirm to override?");
        	
        	if (option != JOptionPane.YES_OPTION)
        		return false;
		}
		
		try (BufferedWriter w = Files.newBufferedWriter(path , charset)) 
		{
			w.write("<html><head><meta charset=\"UTF-8\"> </head><body><table border=1>" + TextProcessor.eol);
			
			int n = 0;
			int total = EG_paragraphs_List.size();
			
			for (Paragraph p:EG_paragraphs_List)
			{
				p.getCNByEN();

				String pp = p.formatHtmlOutput_Table();
				
				w.write(pp);
				
				//System.out.println (pp);
				System.out.println ("============ Completed " + (++n) + "/" + total + 
						"==============" +TextProcessor.eol);
			}
			w.write("</table></body></html>");
		}
		catch(IOException e)
		{
			System.err.println(e);
			return false;
		}
		
		return true;
	}
	
    public static void main(String[] args) {
    	
    	
    	BooksCollection.loadBooks();
    	
    	SBL sbl = new SBL ();
    	
    	sbl.loadENSBL(System.getProperty("user.home") + "/Documents/SBL2016-1/xx08");
    	
    	sbl.buildCNSBL(System.getProperty("user.home") + "/Documents/SBL2016-1/Lesson08.html");
    	
    	//sbl.print();
    	
    	return;
    }
/*	public void print()
	{
		if (EG_paragraphs_List == null) return;
		
		for (Paragraph p:EG_paragraphs_List)
		{
			System.out.println(p);
			
			for (Paragraph pCN : p.pCN_List)
				System.out.println(pCN);
		}
	} */
}
