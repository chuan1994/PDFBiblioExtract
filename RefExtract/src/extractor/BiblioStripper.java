package extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.text.PDFTextStripper;

public class BiblioStripper extends PDFTextStripper{

	private int currentPage;
	private int endPage;
	
	
	public BiblioStripper() throws IOException {
		super();
		
		currentPage = this.document.getNumberOfPages();
		endPage = currentPage;
	}
	
	
	
	
	
	public ArrayList<String> getBiblio(){
		return new ArrayList<String>();
	}
	
}
