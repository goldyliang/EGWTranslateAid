package general;

public class PageParaRef extends BookReference {
	private int pageNum;
	private int paraNum;
	
	public PageParaRef (Book book, String refString)
	{
		super(book, refString);
		
		String r=refString.trim();
		
		if (r.startsWith("p"))
		{
			String [] rr = r.substring(1).split("[\\.()]");
			
			int i=0;
			
			while (rr[i].equals("")) i++;
			
			pageNum = Integer.parseInt(rr[i].trim());
			paraNum = Integer.parseInt(rr[i+1].trim());
		} else
			throw new IllegalArgumentException("Not a Page-Paragraph Reference");
	}
	
	public PageParaRef (PageParaRef ref) {
		super(ref);
		
		this.pageNum = ref.pageNum;
		this.paraNum = ref.paraNum;
	}
	
	public String toString()
	{
		StringBuilder r=new StringBuilder(super.toString());
		
		return r.toString();
	}
	
	public Paragraph locate() {return null;}
	
	public int getPageNum () {return pageNum;}
	
	public int getParaNum () {return paraNum;}
}