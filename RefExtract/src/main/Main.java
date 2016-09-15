package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import extractor.FreeCiteConnection;

public class Main {

	private static HashMap<String, File> inputFiles = new HashMap<String, File>();
	public static File outputFolder;
	public boolean validInput;
	
	public static void main(String[] args) {
		
		
//		ArrayList<String> temp= new ArrayList<String>();
//		temp.add("[1] Bifet,A.,Holmes,G.,Pfahringer,B.,&amp;Gavald,R.(2011).Miningfrequentclosedgraphson evolvingdatastreams.In: Proceeding of the 17th ACMSIGKDD International Conference on Knowledge Discovery and Data Mining, pp 591-599.");
//		temp.add("[2] Yan, X., &amp; Han, J. (2002). gSpan: graph-based substructure pattern mining. In: Proceeding of the 2002 International Conference on Data Mining (ICDM02), Maebashi, Japan, pp 72172");
//		temp.add("[28] Weininger, D. (1988). SMILES, a chemical language and information system. Introduction to methodology and encoding rules. Journal of Chemical Information and Computer Sciences, 28(1), pp 31-36 ");
//		temp.add("[32] Stanford Large Network Dataset Collection. (n.d.). Retrieved from https://snap.stanford.edu/data/index.html");
//		temp.add("[38] Holder,L.B.,Cook,D.J.,&Djoko,S.(1994).SubstuctureDiscoveryintheSUBDUESystem. In: Proceeding of the AAAI94 Workshop Knowledge Discovery in Databases, pp 169-180 ");
//		FreeCiteConnection fcc = new FreeCiteConnection(temp);
//		fcc.sendPostData();
		
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
		
		generateXSL();
		
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
	
	private static void generateXSL(){
		
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
