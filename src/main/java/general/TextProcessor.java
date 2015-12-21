package general;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TextProcessor {
	
	public static String eol = System.getProperty("line.separator");

	/* Retrive parts of the paragraph, 
	 * a part is continuous letters and '-' embraced by any other punctuations
	 * replace blanks with punctuation '+'
	 * omit any parts within [] and ()
	 */
	public static List<String> getParaParts(String str)
	{
		boolean inPart=false;
		StringBuffer part = null;
		List<String> r = new ArrayList<String>();
		
		boolean inBracket=false;
		boolean inParenthese=false;
		
		for (int i=0; i< str.length(); i++)
	//	for (char c:str.toCharArray())
		{
			char c = str.charAt(i);
			
			if (inPart)
			{
				if (Character.isLetter(c)) {
					part.append(c);
				} else if (c=='-') {
					// it could be the word hyphen, ignore this can upcoming spaces
					do { i++; } while (i < str.length() && Character.isSpaceChar(str.charAt(i)));
					i--; // prepare for next loop
				}
				else if (Character.isWhitespace(c))
					//part.append('+');
					part.append(' ');
				else
				{
					//finishing a part
					
					// remove all ending spaces
					//while (part.charAt(part.length()-1) == '+')
					//	part.deleteCharAt(part.length()-1);
					
					String p = part.toString().trim();
					

					if (!p.isEmpty())
						r.add(p);
					
					inPart=false;
					
					if (c=='[')
						inBracket=true;
					else if (c=='(')
						inParenthese=true;
				}
			} else if (inBracket)
			{
				if (c==']') 
					inBracket=false;
			} else if (inParenthese)
			{
				if (c==')')
					inParenthese=false;
			}
			else if (c=='[')
				inBracket=true;
			else if (c=='(')
				inParenthese=true;
			else if (Character.isLetter(c) ||
					Character.isWhitespace(c))
			{
				inPart = true;
				part = new StringBuffer ();
				part.append(c);
			}
		}
		
		return r;
	}
	
	
	
	/* return a string 
	 *  - only keeping the words, 
	 *  - removed all words between [ ] 
	 *  - removed all words with - inside (avoid confusion)
	 *  - removed all words in () which possibly are bible chapter names..
	 * return the words seperated by the sepChar character
	 * 
	 */
	public static String keepWordsOnly(String str, char sepChar)
	{
		StringBuilder r=new StringBuilder();
		boolean inword=false;
		boolean inBracket=false;
		boolean inParenthese=false;
		
		for (char c:str.toCharArray())
		{
			if (inBracket)
			{
				if (c!=']') 
					continue; //skip all words in brackets
				else
					inBracket=false;
			} if (inParenthese)
			{
				if (c!=')')
					continue;
				else
					inParenthese = false;
			}
			{
				if ( Character.isLetter(c) || c=='-')
				{
					r.append(c);
					inword=true;
				} else if (inword)
				{
					r.append(sepChar);
					inword=false;
				}
				
				if (c=='[')
					inBracket = true;
				if (c=='(')
					inParenthese = true;
			}
		}
		
		if (! Character.isLetter(r.charAt(r.length()-1)))
				r.deleteCharAt(r.length()-1);

		return r.toString();
	}
	
	final public static String[] numStr = {"○","一","二","三","四","五","六","七","八","九","十"};
	final public static Set<String> numSet = new TreeSet<String>( Arrays.asList(numStr));

	/* Get Chinese representaion of num (from 1-31) */
	public static String getChineseNum (int num)
	{
		
		if (num<=10)
			return numStr[num];
		else if (num<20)
			return numStr[10]+ numStr[num-10];
		else
		{
			String r = numStr[num/10] + numStr[10];
			if (num % 10 ==0)
				return r;
			else
				return r + numStr[num % 10];
		}
	}
	
	// get the integer from a single Chinese number 
	// If not a Chinese number, return -1
	public static int getFromChineseNum (String num) {
		if (!numSet.contains(num)) return -1;
		
		for (int i=0; i<numStr.length; i++) {
			if (numStr[i].equals(num))
				return i;
		}
		
		return -1;
	}
	
	// Get the first number from string s, started at position index[0]
	// Return the number (-1 if not found).
	// Fill index[0] with the new index right after the end of the number
	public static int getNextNumber (String s, int[] index) {
		boolean isChineseDigit = false;
		
		int i = index[0];
		
		// find the first number
		while (i<s.length()) {
			
			String sub = s.substring(i,i+1); // ch = desc.charAt(i);
			
			// is it a digit?
			if ( Character.isDigit(sub.charAt(0)) ) {
				isChineseDigit = false;
				break;
			}
			
			// is it a Chinese digit?
			if ( TextProcessor.numSet.contains (sub) ) {
				isChineseDigit = true;
				break;
			}
			
			i++;
		}
		
		if (i>=s.length()) // not found any digit, return null
			return -1;
		
		int num = 0;
		
		while (i<s.length()) {
			
			String sub = s.substring(i,i+1); // current character
			
			int n; // current digit
			
			if (isChineseDigit) {
				n = TextProcessor.getFromChineseNum( sub );
				if (n<0) {
					// the end of number
					// check whether this is actually a year number
					if (sub.equals("年")) 
						// this is actually a number, return -1
						return -1;
					else
						break;
				}
				
				if (n==10) {
					if (num==0) // start of the numbers
						n = 1; // when "十" starts, it shall be 1 actually
					else
						n = 0; // when "十" in the middle, it shall be 0
				}
			} else {
				if (!Character.isDigit(sub.charAt(0))) // end if not a digit
					break;
				n = Integer.parseInt(sub);
			}
			
			num  = num * 10 + n;
			i++;
		}
		
		index[0] = i; // set the new index
		
		return num;
	}
	
	/* Get the Chinese date string */
	public static String getCNDateString (Date date)
	{
		Calendar c = Calendar.getInstance();
		
		c.setTime(date);
		
		return getChineseNum(c.get(Calendar.MONTH)+1) + 
				"月" + 
		       getChineseNum(c.get(Calendar.DAY_OF_MONTH)) + 
		       "日";
	}
	
	/* convert all double quote to single quote and return the new text */
	public static String toSingleQuote( String text) {
		
		StringBuffer buf = new StringBuffer (text);
		
		boolean inQuote = false;
		
		List<Character> quotes = Arrays.asList('“','”','"'); // all quote signs
				
		for (int i=0;i<buf.length();i++) {
			char c = buf.charAt(i);
			
			if (!inQuote) {
				//now not in a quote
				if ( quotes.contains(c) ) {
					// left quote
					inQuote = true;
					// change it to left single quote
					buf.setCharAt(i, '‘');
				}
			} else {
				//now in a quote
				if ( quotes.contains(c) ) {
					// right quote
					inQuote = false;
					// change it to right single quote
					buf.setCharAt(i, '’');
				}
			}
				
		}
		
		return new String (buf);
	}
	
	public static boolean containsChineseWords (String s) {
		for (int i=0; i<s.length();) {
	        int codepoint = s.codePointAt(i);
	        i += Character.charCount(codepoint);
	        if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
	            return true;
	        }
		}
		return false;
	}
	
}
