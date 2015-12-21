package bible;

public class Verse {
	BibleBook book;
	int chapterNum;
	int verseNum;
	String text;
	
	public Verse (BibleBook bk, int chapter, int verse, String text) {
		this.book = bk;
		this.chapterNum = chapter;
		this.verseNum = verse;
		this.text = text;
	}
	
	public BibleBook getBook () { return book;}
	public int getChapterNum() { return chapterNum;}
	public int getVerseNum() { return verseNum;}
	public String getText() { return text;}
	
	// a user-friendly identification of the verse (
	public String toString () {
		return book.shortName + chapterNum + ":" + verseNum;
	}
	
	public String toFullString () {
		return toString () + " " + text;
	}
}
