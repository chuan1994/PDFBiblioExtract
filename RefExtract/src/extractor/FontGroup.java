package extractor;

public class FontGroup {
	private String font;
	private float fontSize;
	private String text;
	private int pageNum;
	
	public FontGroup(String font, float fontSize, String text, int pageNum){
		this.font = font;
		this.fontSize = fontSize;
		this.text = text;
		this.pageNum = pageNum;
	}
	
	public String getFont(){
		return this.font;
	}
	
	public float getFontSize(){
		return this.fontSize;
	}
	
	public String getText(){
		return this.text;
	}
	
	public int getPageNum(){
		return this.pageNum;
	}
	
	public void print(){
		System.out.println("font :" + font);
		System.out.println("size :" + fontSize);
		System.out.println("text :" + text);
		System.out.println("page :" + pageNum);
	}
}
