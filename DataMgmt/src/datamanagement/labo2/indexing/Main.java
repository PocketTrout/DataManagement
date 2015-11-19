package datamanagement.labo2.indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

import datamanagement.labo2.tools.StopWordsReader;


public class Main {
	
	public static void main(String[] args) {
		String selectedAnalyzer = "";
		Analyzer analyzer = null;
		
		for(AnalyzersType type: AnalyzersType.values())
		{
			switch(type)
			{
			case WhiteSpace: analyzer = new WhitespaceAnalyzer();
				selectedAnalyzer = IndexNamesStore.WHITESPACE;
				break;
			case EnglishStop: try {
					analyzer = new EnglishAnalyzer(StopWordsReader.getStopWords("common_words.txt"));
					selectedAnalyzer = IndexNamesStore.ENGLISH_STOP;
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				break;
			case English: analyzer = new EnglishAnalyzer();
				selectedAnalyzer = IndexNamesStore.ENGLISH;
				break;
			case Standard: 
			default: analyzer = new StandardAnalyzer();
				selectedAnalyzer = IndexNamesStore.STANDARD;
			}
			
			if(null != analyzer)
			{
				System.out.println("Sélection de l'analyzer de type : " + selectedAnalyzer);
				Lab2Indexer indexer;
				indexer = new Lab2Indexer(analyzer, "cacm.txt", selectedAnalyzer);
				indexer.IndexPublications();
			}
		}
	}
}
