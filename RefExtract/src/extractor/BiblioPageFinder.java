package extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * This class is responsible for identifying where the bibliography is
 * in terms of pages and other metadata regarding to format
 * @author cwu323
 *
 */
public class BiblioPageFinder extends PDFTextStripper {

	private int currentPage;
	private ArrayList<FontGroup> currentFontGroups = new ArrayList<FontGroup>();
	private PDDocument pdDoc;
	private boolean startOfLine = false;
	private ArrayList<Float> leftPos = new ArrayList<Float>();

	private String prevBaseFont = "";
	private float prevBaseFontSize = 0;
	private StringBuilder localString = new StringBuilder();

	public BiblioPageFinder() throws IOException {
		super();
	}

	/**
	 * Setting the document to extract the bibliography from
	 * @param pdDoc
	 */
	public void setDocument(PDDocument pdDoc) {
		this.pdDoc = pdDoc;
		currentPage = pdDoc.getNumberOfPages();
	}

	/**
	 * Method responsible for locating the pages of the bibliography
	 * @return
	 * @throws IOException
	 */
	public int findPages() throws IOException {
		boolean biblioFound = false;
		boolean enterBiblio = false;

		//Search from the back of the document to find bibliography header.
		while (!biblioFound && currentPage > 1) {

			this.setStartPage(currentPage);
			this.setEndPage(currentPage);
			this.getText(pdDoc);
			FontGroup fg = getLargest();
			if (fg.getText().matches("(?i)(.*(bibliography|works cited|references).*)")) {

				if (!enterBiblio) {

					enterBiblio = true;
				}
				
			} else if (enterBiblio) {

				biblioFound = true;
				break;
			}

			currentPage--;

			currentFontGroups = new ArrayList<FontGroup>();
		}

		return currentPage;
	}

	/**
	 * This method is responsible for returning the start page of the bibliography
	 * @return
	 * @throws IOException
	 */
	public int getBiblioStart() throws IOException {
		findPages();
		return currentPage + 1;
	}

	/**
	 * This method is responsible for finding the rough location of where
	 * the reference item starts
	 * @return
	 */
	public float getLeftMost() {
		leftPos.sort(Comparator.<Float> naturalOrder());
		
		ArrayList<Float> temp = new ArrayList<Float>();
		
		for(float f: leftPos){
			temp.add(f);
			if(temp.size() == (pdDoc.getNumberOfPages() - currentPage + 5)){
				break;
			}
		}
		
		return temp.stream().max(Comparator.<Float> naturalOrder()).get();
	}

	// helper method to get the font group with the largest font
	private FontGroup getLargest() {
		FontGroup largest = new FontGroup("", 0, "", currentPage);

		for (FontGroup fg : currentFontGroups) {
			if (fg.getFontSize() > largest.getFontSize()) {
				largest = fg;
			}
		}
		return largest;
	}

	//============================================================================
	//Overriding methods to allow logic to know when a new line starts
	@Override
	protected void writeLineSeparator() throws IOException {
		startOfLine = true;
		super.writeLineSeparator();
	}

	@Override
	protected void startPage(PDPage page) throws IOException {
		startOfLine = true;
		super.startPage(page);
	}
	//============================================================================

	/**
	 * 	Sorting the text into fontgroups. Identifies the font metadata of the current text to be written
	 * creates a new fontgroup when the font metadata changes to create a list of text organised by 
	 * this metadata
	 */

	@Override
	public void writeString(String text, List<TextPosition> textPositions) throws IOException {

		text = text.trim();

		if (startOfLine) {
			startOfLine = false;
			TextPosition first = textPositions.get(0);
			leftPos.add(textPositions.get(0).getXDirAdj());
		}

		if (text.matches("\\s+") || text.equals("")) {
			return;
		}

		ArrayList<String> fonts = new ArrayList<String>();
		ArrayList<Float> fontSizes = new ArrayList<Float>();

		for (TextPosition t : textPositions) {

			fonts.add(t.getFont().getName());
			fontSizes.add(t.getFontSize());
		}

		String commonFont = this.getCommonFont(fonts);
		float commonFontSize = this.getCommonFontSize(fontSizes);

		if (commonFont.matches("\\s+") || commonFont.equals("") || ((int) commonFontSize) == 0) {
			return;
		}

		if (commonFont.equals(prevBaseFont) && Math.abs(commonFontSize - prevBaseFontSize) < 0.1f) {
			localString.append(" " + text);
			writeString(text);
		} else {
			// Add previous block into a group
			FontGroup f = new FontGroup(prevBaseFont, prevBaseFontSize, localString.toString(),
					this.getCurrentPageNo());
			currentFontGroups.add(f);

			// Resetting for next block
			prevBaseFont = commonFont;
			prevBaseFontSize = commonFontSize;
			writeString("[" + commonFont + "," + commonFontSize + "," + this.getCurrentPageNo() + "] " + text);
			localString = new StringBuilder();
			localString.append(text);
		}
	}

	// Helper methods to retrieve most common font and size in a text block
	private String getCommonFont(ArrayList<String> fonts) {
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();
		for (String s : fonts) {
			if (countMap.containsKey(s)) {
				countMap.put(s, countMap.get(s) + 1);
			} else {
				countMap.put(s, 0);
			}
		}

		Entry<String, Integer> max = null;
		for (Entry<String, Integer> e : countMap.entrySet()) {
			if (max == null || max.getValue() < e.getValue()) {
				max = e;
			}
		}

		return max.getKey();
	}

	private float getCommonFontSize(ArrayList<Float> fontSizes) {
		HashMap<Float, Integer> countMap = new HashMap<Float, Integer>();
		for (Float f : fontSizes) {
			if (countMap.containsKey(f)) {
				countMap.put(f, countMap.get(f) + 1);
			} else {
				countMap.put(f, 0);
			}
		}

		Entry<Float, Integer> max = null;
		for (Entry<Float, Integer> e : countMap.entrySet()) {
			if (max == null || max.getValue() < e.getValue()) {
				max = e;
			}
		}

		return max.getKey();
	}

}
