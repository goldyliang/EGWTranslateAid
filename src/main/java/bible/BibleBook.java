package bible;

import java.util.ArrayList;
import java.util.List;

public class BibleBook {
	
	Bible bible;
	
	String shortNameEN;
	String fullNameEN;
	String shortName;
	String fullName;
	
	// list of all verses of this book
	// first index is the chapter number
	// second is the verse number
	List < List<Verse> > verses = new ArrayList < List<Verse> > ();
	{ 
		verses.add(null);  // need to add the first fate chapter (index 0)
	}
	
	public BibleBook (Bible bible, String sEN, String fEN, String sName, String fName) {
		this.bible = bible;
		shortNameEN = sEN;
		fullNameEN = fEN;
		shortName = sName;
		fullName = fName;
	}
	
	// try to add a verse to the last of the table
	// if it is not the one next to the last, return false
	boolean addVerseToLast (Verse verse) {
		
		// the chapter shall be the last of the one after the last chapter
		if ( verse.chapterNum != verses.size()-1 &&
			 verse.chapterNum != verses.size() ) {
			System.err.println ("Bible verse read error, not continurous verse: " + verse.toFullString());
			return false;
		}
		
		List<Verse> lv;
				
		if ( verse.chapterNum == verses.size()) {
			// this is a new chapter, add to the last
			lv = new ArrayList<Verse> ();
			lv.add(null); // need to add the first fate verse (index 0)
			verses.add(lv);
		} else
			lv = verses.get(verse.chapterNum);
		
		// ensure the new verse to add is just next to the one of the last
		if ( verse.verseNum != lv.size() ) {
			System.err.println ("Bible verse read errr, not continurous verse: " + verse.toFullString());
			return false;
		}
		
		lv.add(verse);	
		return true;
	}
	

	
	public Verse getVerse (int chapterNum, int verseNum) {
		if (chapterNum>0 && chapterNum <= verses.size()-1) {
			
			List<Verse> v = verses.get(chapterNum);
			
			if (verseNum > 0 && verseNum <= v.size()-1)
				return v.get(verseNum);
		}

		return null; // invalid chapter/verse num
	}
	
}
