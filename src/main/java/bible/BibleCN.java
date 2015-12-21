package bible;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;


public class BibleCN implements Bible {

	// map from book name to BibleBook object
	// book name could be EN short name, full name (other names), or Chines short/full name
	Map <String, BibleBook> books = new TreeMap <String, BibleBook> ();
	
	// static table of EN short name to CN short/full name
	// [][0] - EN short name
	// [][1] - Alternative EN short name
	// [][2] - EN full name
	// [][3] - Alternative EN full name
	static String [][] listBookNames= {
			{"Gen" , null, "Genesis", null, "创" , "创世纪"},
			{"Exo" , null, "Exotus", null, "出" , "出埃及记"},
			{"Lev" , null, "Leviticus", null, "利" , "利未记"},
			{"Num" , null, "Numbers", null, "民" , "民数记"},
			{"Deu" , null, "Deuteronomy", null, "申" , "申命记"},
			{"Jos" , null, "Joshua", null, "书" , "约书亚书"},
			{"Jug" , null, "Judges", null, "士" , "土师记"},
			{"Rut" , null, "Ruth", null, "得" , "路得记"},
			{"1Sa" , null, "Samuel1", null, "撒上" , "撒母士记上"},
			{"2Sa" , null, "Samuel2", null, "撒下" , "撒母士记下"},
			{"1Ki" , null, "1Kings", null, "王上" , "列王记上"},
			{"2Ki" , null, "2Kings", null, "王下" , "列王记下"},
			{"1Ch" , null, "1Chronicles", null, "代上" , "历代记上"},
			{"2Ch" , null, "2Chronicles", null, "代下" , "历代记下"},
			{"Ezr" , null, "Ezra", null, "拉" , "以斯拉"},
			{"Neh" , null, "Nehemiah", null, "尼" , "尼希米"},
			{"Est" , null, "Esther", null, "斯" , "以斯帖"},
			{"Job" , null, "Job", null, "伯" , "约伯记"},
			{"Psm" , null, "Psalms", "Psalm", "诗" , "诗篇"},
			{"Pro" , null, "Proverbs", null, "箴" , "箴言书"},
			{"Ecc" , null, "Ecclesiastes", null, "传" , "传道书"},
			{"Son" , null, "Songs", null, "歌" , "雅歌"},
			{"Isa" , null, "Isaiah", "Issiah", "赛" , "以赛亚书"},
			{"Jer" , null, "Jeremiah", null, "耶" , "耶利米书"},
			{"Lam" , null, "Lamentations", null, "哀" , "耶利米哀歌"},
			{"Eze" , null, "Ezekiel", null, "结" , "以西结书"},
			{"Dan" , null, "Daniel", null, "但" , "但以理书"},
			{"Hos" , null, "Hosea", null, "何" , "何西阿书"},
			{"Joe" , null, "Joel", null, "珥" , "约珥书"},
			{"Amo" , null, "Amos", null, "摩" , "阿摩司书"},
			{"Oba" , null, "Obadiah", null, "俄" , "俄巴底亚书"},
			{"Jon" , null, "Jonah", null, "拿" , "约拿书"},
			{"Mic" , null, "Micah", null, "弥" , "弥迦书"},
			{"Nah" , null, "Nahum", null, "鸿" , "那鸿书"},
			{"Hab" , null, "Habakkuk", null, "哈" , "哈巴谷书"},
			{"Zep" , null, "Zephaniah", null, "番" , "西番雅书"},
			{"Hag" , null, "Haggai", null, "该" , "哈该书"},
			{"Zec" , null, "Zechariah", null, "亚" , "撒迦利亚书"},
			{"Mal" , null, "Malachi", null, "玛" , "玛拉基书"},
			{"Mat" , null, "Matthew", null, "太" , "马太福音"},
			{"Mak" , null, "Mark", null, "可" , "马可福音"},
			{"Luk" , null, "Luke", null, "路" , "路加福音"},
			{"Jhn" , null, "John", null, "约" , "约翰福音"},
			{"Act" , null, "Acts", null, "徒" , "使徒行传"},
			{"Rom" , null, "Romans", null, "罗" , "罗马书"},
			{"1Co" , null, "1Corinthians", null, "林前" , "哥林多前书"},
			{"2Co" , null, "2Corinthians", null, "林后" , "哥林多后书"},
			{"Gal" , null, "Galatians", null, "加" , "加拉太书"},
			{"Eph" , null, "Ephesians", null, "弗" , "以弗所书"},
			{"Phl" , null, "Philippians", null, "腓" , "腓利比书"},
			{"Col" , null, "Colossians", null, "西" , "歌罗西"},
			{"1Ts" , null, "1Thessalonians", null, "帖前" , "帖前"},
			{"2Ts" , null, "2Thessalonians", null, "帖后" , "帖后"},
			{"1Ti" , null, "1Timothy", null, "提前" , "提前"},
			{"2Ti" , null, "2Timothy", null, "提后" , "提后"},
			{"Tit" , null, "Titus", null, "多" , "提多"},
			{"Phm" , null, "Philemon", null, "门" , "腓利门"},
			{"Heb" , null, "Hebrews", null, "来" , "希伯来书"},
			{"Jas" , null, "James", null, "雅" , "雅各书"},
			{"1Pe" , null, "1Peter", null, "彼前" , "彼得前书"},
			{"2Pe" , null, "2Peter", null, "彼后" , "彼得后书"},
			{"1Jn" , null, "1John", "1 John", "约一" , "约翰一书"},
			{"2Jn" , null, "2John", "2 John", "约二" , "约翰二书"},
			{"3Jn" , null, "3John", "3 John", "约三" , "约翰三书"},
			{"Jud" , null, "Jude", null, "犹" , "犹大书"},
			{"Rev" , null, "Revelation", null, "启" , "启示录"}
	};
	
	public final static BibleCN BIBLE_CN = getBibleFromFile(true);;
	
	static {
		if (BIBLE_CN==null) {
			System.err.println ("Bible file read error.");
		}
	}
	
	// get the book from shortName (either EN, or CN)
	public BibleBook getBook (String shortName) {
		return books.get(shortName);
	}
	
	// create a verse from the text, and add it a right book
	// text format: <Book EN short name> <chapter num> : <verse number> text
	@Override
	public Verse createVerseFromText (String txt) {
		int i = txt.indexOf(" ");
		if (i<0) return null;
		
		String bookName = txt.substring(0,i).trim();
		BibleBook bk = books.get(bookName);
		
		if (bk==null) return null;
		
		int j = txt.indexOf(":", i);
		if (j<0) return null;
		
		String chapter = txt.substring(i+1,j).trim();
		
		int chapterNum = Integer.parseInt(chapter);
		
		if (chapterNum <=0) return null;
		
		int k = txt.indexOf(" ", j+1);
		
		String verse;
		if (k<0) 
			verse = txt.substring(j+1);  // empty verse, until the end is the verse number
		else 
			verse = txt.substring(j+1,k);
		
		int verseNum = Integer.parseInt(verse);
		
		String verseTxt;
		if (k<0)
			verseTxt = "-";  // empty verse
		else
			verseTxt = txt.substring(k+1);
		
		Verse newVerse = new Verse (bk, chapterNum, verseNum, verseTxt);
		
		if (bk.addVerseToLast(newVerse))
			return newVerse;
		else
			return null;
	}
	
	public static BibleCN getBibleFromFile (boolean silence) {
		BibleCN bible = new BibleCN();
		
		Path path = FileSystems.getDefault().getPath("bible", "hgb.txt");
		
		if (bible.loadFromFile(path, silence))
			return bible;
		else
			return null;
	}
	
	boolean loadFromFile (Path path, boolean silence) {
		
		// create all books and map them first
		for ( String[] names : listBookNames) {
			BibleBook book = new BibleBook (this, names[0], names[2], names[4], names[5]);
			
			for (String name: names )
				if (name!=null)
					books.put(name, book); // use EN short name to index
		}
		
		// load verses from file and add to the book
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset) ) {
			
			String txt;
			
			while ( (txt = reader.readLine()) != null) {
				// create a verse and add to the book
				Verse verse = createVerseFromText(txt);
				if (verse==null) {
					System.err.println("Bible read error.");
					return false;
				}
				if (!silence)
					System.out.println ("Loaded verse: " + verse.toFullString());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		
		return true;
	}
	
	public Verse getVerse (String shortNameEN, int chapter, int verse) {
		BibleBook book = books.get(shortNameEN);
		if (book == null) return null;
		
		return book.getVerse(chapter, verse);
	}
	
	private static class StringScanner {
		String text;
		int idx;
		int idx_end;
		
		public StringScanner (String text) {
			this.text = text;
			idx = 0;
			idx_end = text.length();
		}
		
		String getNextWord () {
			getNextNoneBlank();

			if (isEnd()) return null;
			
			int idx_start = idx;
			while (!isEnd() && Character.isLetter(text.charAt(idx)) )
					idx++;
			
			return text.substring(idx_start, idx);
		}
		
		int getNextNum () {
			
			getNextNoneBlank();

			int idx_start = idx;
				
			while (!isEnd() && Character.isDigit(text.charAt(idx)))
				idx++;
			
			if (idx > idx_start)
				return Integer.parseInt(text.substring(idx_start, idx));
			else
				return -1;
		}
		
		char getNextNoneBlank() {
			while (!isEnd() && Character.isSpaceChar(text.charAt(idx))) 
				idx++;
			if (!isEnd())
				return text.charAt(idx);
			else
				return ' ';
		}
		
		void skipOneChar () { if (!isEnd()) idx++;}
		
		int getCurPos () { return idx;}
		
		boolean isEnd () { return idx>=idx_end; }
	}
	
	String lastSearchedBook = null; // remember the last book, in case we meet "verse.." in next searching
	int lastSearchedChapter = 0; // remeber the last chapter we searched, same reason as above
	
	// return verse text and book/chapter/verse, based on range (in English)
	public String getVerseRangeText (String range) {
		
		StringBuffer result = new StringBuffer();
		StringBuffer id = new StringBuffer();
		
		int lastVerse=-1; // the last verse number added
		
		try {
			// analyze the String range
			StringScanner scanner = new StringScanner (range);
			
			String bk = scanner.getNextWord();
			
			boolean followingVerse = bk.startsWith("verse"); // could be verses
			
			if (followingVerse) {
				bk = lastSearchedBook;
				if (bk==null) return null;
			}
			else if (bk.isEmpty()) {
				// no word at the beginning, could be a book name started with a number
				int n = scanner.getNextNum();
				if (n<=0 || n>3) // shall not happen
					return null;
				// now get the word and combined with the number
				bk = String.valueOf(n) + scanner.getNextWord();
			}
			
			BibleBook book = books.get(bk);
			if (book==null) return null;
			
			id.append( book.shortName );
			
			int chapter;
			
			// check if there is a chapter sign ":"
			if (range.indexOf(":", scanner.getCurPos()) >=0 ) {
				// there is a chapter sign, scan the chapter first
				
				chapter = scanner.getNextNum();
				
				if (scanner.getNextNoneBlank() != ':')
					return null; // shall not happen

				scanner.skipOneChar(); // skip the ':' sign
			} else {
				// there is no chapter sign
				
				// if the original book is "verse", 
				// try to use the last searched chapter
				if (followingVerse && lastSearchedChapter > 0)
					chapter = lastSearchedChapter;
				else
					// otherwise, shall be chapter 1
					chapter = 1;
			}
			
			id.append(String.valueOf(chapter) + ":");
			
			do {
				// scan one verse range, either "num", or "num-num"
				int verse = scanner.getNextNum();
				
				if (verse<0) break;
				
				int verse1 = verse;
				char c = scanner.getNextNoneBlank();
				
				if (c == '–' || c == '-') {
					scanner.skipOneChar();
					verse1 = scanner.getNextNum();
					if (verse1<0) // shall not happen
						return null;
				}
				
				// add the text of the range verse to verse1
				for (int v = verse; v<= verse1; v++) {
					// add "..." whenever the current verse is not the next of the last verse
					if ( lastVerse>0 && v != lastVerse+1)
						// there is already text, add "...."
						result.append(" ... ");
					
					result.append( book.getVerse(chapter, v).getText());
					lastVerse = v;
				}
				
				// add the text to ID
				id.append(String.valueOf(verse));
				if (verse1>verse)
					id.append("-" + String.valueOf(verse1));
				
				// get next non-blank, skip it if a comma
				if (scanner.getNextNoneBlank() == ',')
				{
					scanner.skipOneChar();
					// add comma to id
					id.append(",");
				}
				
			} while (!scanner.isEnd());
			
			// check whether we have some text left out
			if (scanner.getNextNoneBlank()!=' ')
				// some left out, still an error
				return null;
			
			// remember the last searched book and chapter
			lastSearchedBook = bk;
			lastSearchedChapter = chapter;
			
			// now all good, return
			return "“" + result.toString() + "”" + "（" + id.toString() + "）";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
