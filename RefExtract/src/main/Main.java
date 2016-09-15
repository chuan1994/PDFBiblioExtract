package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Main {

	private static HashMap<String, File> inputFiles = new HashMap<String, File>();
	public static File outputFolder;
	public boolean validInput;
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			printHelp();
			return;
		}

		processArgs(args);
		if (outputFolder.isFile()) {
			return;
		}

		Set<String> keys = inputFiles.keySet();


		ArrayList<BiblioExtractor> beList = new ArrayList<BiblioExtractor>();
		
		for (String x : keys) {
			BiblioExtractor be = new BiblioExtractor(x, inputFiles.get(x));
			beList.add(be);
			be.execute();
		}
		
		while(!beList.isEmpty()){
			ArrayList<BiblioExtractor> removeList = new ArrayList<BiblioExtractor>();
			for(BiblioExtractor be : beList){
				if(be.isDone()){
					removeList.add(be);
				}
			}
			
			for(BiblioExtractor be : removeList){
				beList.remove(be);
			}
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
			outputFolder.mkdir();
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
