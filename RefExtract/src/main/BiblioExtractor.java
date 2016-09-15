package main;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import extractor.BiblioStripper;
import extractor.FontGroup;
import extractor.FreeCiteConnection;

public class BiblioExtractor extends SwingWorker<Void, Void> {

	private String path;
	private File pdf;
	private File output;
	
	private ArrayList<FontGroup> fontGroups = new ArrayList<FontGroup>();
	
	public BiblioExtractor(String path, File pdf) {
		this.path = path;
		this.pdf = pdf;

	}

	@Override
	protected Void doInBackground() throws Exception {
		BiblioStripper bs = new BiblioStripper();
		ArrayList<String> extracted = bs.getBiblio();
		
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
		
		transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
		
		return null;
	}
}
