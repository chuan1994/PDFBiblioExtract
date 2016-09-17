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

public class Main {
	
	private static HashMap<String, File> inputFiles = new HashMap<String, File>();
	public static File outputFolder;
	public static File xsl;

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

		//creating a list to store all the processes.
		ArrayList<BiblioExtractor> beList = new ArrayList<BiblioExtractor>();

		//Creating template XSL file to view the extracted text in table format 
		generateXSL();

		//starting a worker for each input file
		for (String x : keys) {
			BiblioExtractor be = new BiblioExtractor(x, inputFiles.get(x));
			beList.add(be);
			be.execute();
		}

		//Waiting for all workers to complete
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

	/**
	 * generating the default xsl file to the target location.
	 */
	private static void generateXSL() {
		try {
			File xsl = new File(outputFolder.getAbsolutePath() + System.getProperty("file.separator") + "temp.xsl");

			//check to see if file already exists
			if (xsl.exists()) {
				return;
			}
			
			xsl.createNewFile();
			
			//Copying the resource xsl to the new file
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

	
	/**
	 * Processing the command line arguments provided.
	 * Checks to ensure the input files and output folder are valid.
	 * @param args
	 */
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

	
	/**
	 * Method to display how to use this jar file. Displayed when wrong input provided
	 */
	private static void printHelp() {
		System.out.println("To execute this jar please follow following:");
		System.out.println("Run the jar with a list of input files separated by a space followed by an output folder");
		System.out.println(
				"Example: java -jar PDFExtractPrototype.jar example.pdf example1.pdf example2.pdf outputFolder");
		return;
	}
}
