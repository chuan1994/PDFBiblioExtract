package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import extractor.BiblioPageFinder;
import extractor.BibliographyParser;
import extractor.FontGroup;
import extractor.FreeCiteConnection;

public class BiblioExtractor extends SwingWorker<Void, Void> {

	private String path;
	private File pdf;
	private PDDocument pdDoc;
	private File output;
	
	private ArrayList<FontGroup> fontGroups = new ArrayList<FontGroup>();
	
	public BiblioExtractor(String path, File pdf) {
		this.path = path;
		this.pdf = pdf;
		
		getOutput();
		this.setup();
	}

	private void setup(){
		this.pdDoc = new PDDocument();
		
		try {
			pdDoc = pdDoc.load(pdf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		BiblioPageFinder bpf = new BiblioPageFinder();
		bpf.setDocument(pdDoc);
		int startPage = bpf.getBiblioStart();
		
		BibliographyParser bp = new BibliographyParser(bpf.getLeftMost());
		bp.setStartPage(startPage);
		bp.getText(pdDoc);
		
		ArrayList<String> extracted = bp.getBiblio();
		
		pdDoc.close();
		FreeCiteConnection fcc = new FreeCiteConnection(extracted);
		String returnVal = fcc.sendPostData();
		
		//Transform to XML document
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(returnVal)));
		
		//Print to Output
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		
		transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(new FileOutputStream(output), "UTF-8")));
		return null;
	}
	
	private void getOutput(){
		String outputPath = Main.outputFolder.getPath() + File.separator + pdf.getName().split("\\.")[0] + ".xml";

		try {
			output = new File(outputPath);
			if (!output.exists()) {
				output.createNewFile();
			} else{
				int i = 0;
				while(output.exists()){
					i++;
					outputPath = Main.outputFolder.getCanonicalPath() + File.separator + pdf.getName().split("\\.")[0] + "("
							+ i + ")" + ".xml";

					output = new File(outputPath);
				}
				
				output.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
