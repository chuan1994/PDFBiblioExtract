package extractor;

import java.io.PrintStream;

/**
 * Class to store font-based information to a block of text extracted from a pdf
 * 
 * Stores the font name, font size, corresponding text and page number
 * @author cwu323
 *
 */
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
	//====================================================
	//getters 
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
	
	//===================================================
	//print function for testing purposes, to display all information
	public void print(PrintStream ps){
		ps.println("font :" + font);
		ps.println("size :" + fontSize);
		ps.println("text :" + text);
		ps.println("page :" + pageNum);
	}
}
