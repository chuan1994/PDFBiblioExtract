package extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

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

	public void setDocument(PDDocument pdDoc) {
		this.pdDoc = pdDoc;
		currentPage = pdDoc.getNumberOfPages();
	}

	public int findPages() throws IOException {
		boolean biblioFound = false;
		boolean enterBiblio = false;

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
			}

			currentPage--;

			currentFontGroups = new ArrayList<FontGroup>();
		}

		return currentPage;
	}

	public int getBiblioStart() throws IOException {
		findPages();
		return currentPage + 2;
	}

	public float getLeftMost() {
		return leftPos.stream().min(Comparator.<Float> naturalOrder()).get();
	}

	// helper method to get largest font size on page
	private FontGroup getLargest() {
		FontGroup largest = new FontGroup("", 0, "", currentPage);

		for (FontGroup fg : currentFontGroups) {
			if (fg.getFontSize() > largest.getFontSize()) {
				largest = fg;
			}
		}
		return largest;
	}

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

			// Resetting for
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
