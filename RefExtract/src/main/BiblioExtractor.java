package main;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import extractor.BiblioStripper;
import extractor.FontGroup;

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
		bs.getBiblio();
		boolean found = false;
		
		while(!found){
			
		}
		return null;
	}
}
