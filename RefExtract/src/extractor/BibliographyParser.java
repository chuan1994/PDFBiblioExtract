package extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * This class is responsible for obtaining the bibliographical items in the pdf file
 * 
 * It functions by splitting the extracted text whenever it starts at a new line at the x coordinate specified by the variable leftVal
 * @author cwu323
 *
 */
public class BibliographyParser extends PDFTextStripper{

	private boolean startOfLine = false;
	private float leftVal;
	StringBuilder currentBuilder = new StringBuilder();
	ArrayList<String> biblioList = new ArrayList<String>();
	
	
	public BibliographyParser(float leftVal) throws IOException {
		super();
		this.leftVal = leftVal;
	}
	
	//================================================================
	//Overriding methods to know when a line starts
	@Override
	protected void writeLineSeparator() throws IOException{
		startOfLine = true;
		super.writeLineSeparator();
	}
	
	@Override
    protected void startPage(PDPage page) throws IOException{
        startOfLine = true;
        super.startPage(page);
    }
	//================================================================

	
	@Override
	public void writeString(String text, List<TextPosition> textPositions) throws IOException {
		if(startOfLine){
//			System.out.println(text + "    " + textPositions.get(0).getXDirAdj());
			startOfLine = false;
			if(Math.abs((textPositions.get(0).getXDirAdj()- leftVal)) < 10){
				if(currentBuilder.length()> 0){
					biblioList.add(currentBuilder.toString());
					currentBuilder.setLength(0);
				}
			}
		}
		
		currentBuilder.append(text +" ");
		
		super.writeString(text, textPositions);
	}
	

	/**
	 * Method to retrieve the list of extracted bibliographical items
	 * @return
	 */
	public ArrayList<String> getBiblio(){
//		for (String x: biblioList){
//			System.out.println(x);
//			System.out.println("");
//		}
		
		return biblioList;
	}
	
}
