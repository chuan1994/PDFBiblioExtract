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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import extractor.BiblioPageFinder;
import extractor.BibliographyParser;
import extractor.FontGroup;
import extractor.FreeCiteConnection;

/**
 * This class extends swingworker to perform the extraction process in the background.
 * It is responsible for:
 * Retrieving the bibliography
 * Calling freecite API
 * Processing websites reply
 * Putting results in the output folder
 * 
 * @author cwu323
 *
 */
public class BiblioExtractor extends SwingWorker<Void, Void> {

	private String path;
	private File pdf;
	private PDDocument pdDoc;
	private File output;

	public BiblioExtractor(String path, File pdf) {
		this.path = path;
		this.pdf = pdf;

		getOutput();
		this.setup();
	}

	private void setup() {
		this.pdDoc = new PDDocument();

		try {
			pdDoc = pdDoc.load(pdf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		//Retrieving bibliography metadata using the finder
		BiblioPageFinder bpf = new BiblioPageFinder();
		bpf.setDocument(pdDoc);
		int startPage = bpf.getBiblioStart();		
		
		//Retrieving the references from bibliography
		BibliographyParser bp = new BibliographyParser(bpf.getLeftMost());
		bp.setStartPage(startPage);
		bp.getText(pdDoc);

		ArrayList<String> extracted = bp.getBiblio();
		
		pdDoc.close();
		
		//Connecting to the freecite webapi
		FreeCiteConnection fcc = new FreeCiteConnection(extracted);
		String returnVal = fcc.sendPostData();

		System.out.println("Processing reply");
		// Transform to XML document
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(returnVal)));

		// Removing invalid citations
		Element root = doc.getDocumentElement();
		NodeList citations = doc.getElementsByTagName("citation");
		for (int i = citations.getLength() - 1; i >= 0; i--) {
			String value = citations.item(i).getAttributes().getNamedItem("valid").getNodeValue();
			if (value.equals("false")) {
				root.removeChild(citations.item(i));
			}
		}

		// Removing xmlns nodes
		NodeList ctx = doc.getElementsByTagName("ctx:context-objects");
		for (int i = ctx.getLength() - 1; i >= 0; i--) {
			root.removeChild(ctx.item(i));
		}

		//Adding in the references to the xml stylesheet
		Node pi = doc.createProcessingInstruction("xml-stylesheet", ("type=\"text/xsl\" href=\"" + Main.xsl.getName() + "\""));
		doc.insertBefore(pi, root);

		//Transforming dom document to be printed
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc),
				new StreamResult(new OutputStreamWriter(new FileOutputStream(output), "UTF-8")));
		return null;
	}

	/**
	 * Helper method to get the output files name
	 * Name of the file is the same as the name of the pdf document
	 * (considers if a file already exists)
	 */
	private void getOutput() {

		try {
			
			String outputPath = Main.outputFolder.getAbsolutePath() + File.separator + pdf.getName().split("\\.")[0] + ".xml";

			System.out.println(outputPath);
			output = new File(outputPath);
			if (!output.exists()) {
				System.out.println(output.createNewFile());
				
			} else {
				int i = 0;
				while (output.exists()) {
					i++;
					outputPath = Main.outputFolder.getAbsolutePath() + File.separator + pdf.getName().split("\\.")[0]
							+ "(" + i + ")" + ".xml";

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
