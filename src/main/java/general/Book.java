package general;


import org.openqa.selenium.WebDriver;

public abstract class Book {
	public enum Language {EN, CN};

	public Book (Language lang, String name, String shortName, int priority)
	{
		this.lang = lang;
		this.name = name;
		this.shortName = shortName;
		this.priority=priority;
	}
	
	public int getPriority() { return priority; }
	
	public String getName() { return name;}
	public String getShortName() { return shortName; }
	
	public String toString()
	{
		return lang.toString() + "-" + name + "-" + shortName;
	}
	
	public void getWebDriver()
	{
		if (driver == null)
			driver = DriverPool.getDriverInstance();
	}
	
	public void releaseWebDriver()
	{
		if (driver != null)
		{
			DriverPool.releaseDriverInstance();
			driver = null;
		}
	}
	
	/* Navigate and get the CN paragraph according to an EN reference
	 * To be overloaded by different descendant Book classes
	 * To be called under the EN Book object
	 */
//	abstract public Paragraph getCNByENReference (BookReference pEN);


	abstract public String NavNextPage();
	abstract public String NavPrevPage();
	abstract public String NavPageNum(int num);
	abstract public String getChapterName();
	abstract public Paragraph getPageParagraph(PageParaRef ref); //int pageNum, int paraNum);
	
	protected String baseUrl;
	protected WebDriver driver;
	
	protected Language lang;
	protected String name, shortName;
	protected int priority; // smaller, higher priority
}



