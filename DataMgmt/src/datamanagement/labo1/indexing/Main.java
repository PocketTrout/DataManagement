package datamanagement.labo1.indexing;

import java.io.IOException;
import java.nio.file.FileSystems;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import datamanagement.labo1.tuning.Similarity;

public class Main {
	
	public static void main(String[] args) {
		
		AnalyzersType type = AnalyzersType.English; //change here to select analyzer
		boolean useCustomSimilarity = false; // change here to use custom similarity
		String selectedAnalyzer = "";
		Analyzer analyzer = null;
		switch(type)
		{
		case WhiteSpace: analyzer = new WhitespaceAnalyzer();
			selectedAnalyzer = "WhiteSpace";
			break;
		case Shingle2: analyzer = new ShingleAnalyzerWrapper(2, 2);
			selectedAnalyzer = "Shingle size 2";
			break;
		case Shingle3: analyzer = new ShingleAnalyzerWrapper(3, 3);
			selectedAnalyzer = "Shingle size 3";
			break;
		case Stop: try {
				analyzer = new StopAnalyzer(FileSystems.getDefault().getPath("common_words.txt"));
				selectedAnalyzer = "Stop";
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			break;
		case English: analyzer = new EnglishAnalyzer();
			selectedAnalyzer = "English";
			break;
		case Standard: 
		default: analyzer = new StandardAnalyzer();
			selectedAnalyzer = "Standard";
		}
		
		if(null != analyzer)
		{
			System.out.println("S�lection de l'analyzer de type : " + selectedAnalyzer);
			Lab1Indexer indexer;
			if(!useCustomSimilarity)
				indexer = new Lab1Indexer(analyzer, "cacm.txt");
			else
				indexer = new Lab1Indexer(analyzer, "cacm.txt", new Similarity());
			indexer.IndexPublications();
		}
	}
}
