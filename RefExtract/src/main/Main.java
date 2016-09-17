package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import extractor.FreeCiteConnection;

public class Main {
	
	private static HashMap<String, File> inputFiles = new HashMap<String, File>();
	public static File outputFolder;

	public static void main(String[] args) {

		//receiving and processing the input arguments from the command line
		if (args.length < 2) {
			printHelp();
			return;
		}

		processArgs(args);
		if (outputFolder.isFile()) {
			printHelp();
			return;
		}

		//retrieving the list paths 
		Set<String> keys = inputFiles.keySet();

		ArrayList<BiblioExtractor> beList = new ArrayList<BiblioExtractor>();

		generateXSL();

		for (String x : keys) {
			BiblioExtractor be = new BiblioExtractor(x, inputFiles.get(x));
			beList.add(be);
			be.execute();
		}

		while (!beList.isEmpty()) {
			ArrayList<BiblioExtractor> removeList = new ArrayList<BiblioExtractor>();
			for (BiblioExtractor be : beList) {
				if (be.isDone()) {
					removeList.add(be);
				}
			}

			for (BiblioExtractor be : removeList) {
				beList.remove(be);
			}
		}

	}

	private static void generateXSL() {
		try {
			File xsl = new File(outputFolder.getAbsolutePath() + System.getProperty("file.separator") + "temp.xsl");

			if(outputFolder.exists()){
				System.out.println("exists");
			}
			
			
			if (xsl.isFile()) {
				return;
			}
			
			xsl.createNewFile();
			
			InputStream is = Main.class.getResourceAsStream("/resources/temp.xsl");
			OutputStream os = new FileOutputStream(xsl);
			
			int read = 0;
			
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			
			
			os.close();
			is.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void processArgs(String[] args) {
		File outTemp = new File(args[args.length - 1]);
		setOutput(outTemp);

		for (int i = 0; i < args.length - 1; i++) {
			addInput(args[i]);
		}
	}

	private static void setOutput(File outFolder) {
		if (outFolder.isFile()) {
			System.out.println("Invalid output directory");
			printHelp();
			return;
		}
		outputFolder = outFolder;
		if (!outputFolder.exists()) {
			System.out.println(outputFolder.mkdir()) ;
		}
		
	}

	private static void addInput(String path) {
		File y = new File(path);
		if (!y.isFile()) {
			System.out.println("Invalid input file: " + path);
			return;
		}

		inputFiles.put(path, y);
	}

	private static void printHelp() {
		System.out.println("To execute this jar please follow following:");
		System.out.println("Run the jar with a list of input files separated by a space followed by an output folder");
		System.out.println(
				"Example: java -jar PDFExtractPrototype.jar example.pdf example1.pdf example2.pdf outputFolder");
		return;
	}
}
