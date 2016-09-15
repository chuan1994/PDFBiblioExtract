package extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.text.PDFTextStripper;

public class BiblioStripper extends PDFTextStripper{

	private int currentPage;
	private int endPage;
	private ArrayList<FontGroup> currentFontGroups = new ArrayList<FontGroup>();
	
	
	public BiblioStripper() throws IOException {
		super();
		
		currentPage = this.document.getNumberOfPages();
		endPage = currentPage;
	}
	
	
	
	
	
	public ArrayList<String> getBiblio(){
		return new ArrayList<String>();
	}
	
	
	//helper method to get largest font size on page
	private FontGroup getLargest(){
		FontGroup largest = currentFontGroups.get(0);
		
		for(FontGroup fg : currentFontGroups){
			if(fg.getFontSize() > largest.getFontSize()){
				largest = fg;
			}
		}
		return largest;
	}
}
