package datamanagement.labo1.searching;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import datamanagement.labo1.indexing.Lab1Indexer;
import datamanagement.labo1.tuning.Similarity;

public class Main {
	
	public static void main(String[] args) {
		boolean useCustomSimilarity = false; //change here to set custom similarity
		Path indexPath = FileSystems.getDefault().getPath(Lab1Indexer.INDEXDIR);
		try {
			Directory indexDir = FSDirectory.open(indexPath);
			IndexReader iReader = DirectoryReader.open(indexDir);
			HighFreqQuestions(iReader);
			Lab1Searcher searcher;
			int topResults = 10;
			
			if(!useCustomSimilarity)
				searcher = new Lab1Searcher(new EnglishAnalyzer(), iReader);
			else
				searcher  = new Lab1Searcher(new EnglishAnalyzer(), iReader, new Similarity());
			
        	//searcher.QueryAndDisplayScore("compiler program", 15);
        	
        	System.out.println("Publications containing \"Information Retrieval\"");
			searcher.QueryAndDisplayScore("Information Retrieval", topResults);
			System.out.println("Publications containing both \"Information\" and \"Retrieval\"");
            searcher.QueryAndDisplayScore("Information AND Retrieval", topResults);
            System.out.println("Publications containing at least \"Retrieval\" and possibly \"Information\" but not \"Database\"");
            searcher.QueryAndDisplayScore("(Information^0.1 AND Retrieval) NOT Database", topResults);
            System.out.println("Publications containing a term starting with \"info\"");
            searcher.QueryAndDisplayScore("info*", topResults);
            System.out.println("Publications containing \"Information\" close to \"Retrieval\"");
            searcher.QueryAndDisplayScore("\"Information Retrieval\"~5", topResults);
            searcher.close();
            indexDir.close();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}	
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void HighFreqQuestions(IndexReader reader)
	{
		try {
			TermStats[] authorStats = HighFreqTerms.getHighFreqTerms(reader, 10, Lab1Indexer.AUTHORFIELD, new Comparator<TermStats>() {
				
				@Override
				public int compare(TermStats o1, TermStats o2) {
					
					return o1.docFreq - o2.docFreq;
				}
			});
			
			System.out.println("Top des auteurs: ");
			for (TermStats termStats : authorStats) {
				System.out.println(termStats.termtext.utf8ToString() + " - nombre de publications: " + termStats.docFreq);
			}
			
			TermStats[] termsStats = HighFreqTerms.getHighFreqTerms(reader, 10, Lab1Indexer.TITLEFIELD, new Comparator<TermStats>() {
				
				@Override
				public int compare(TermStats o1, TermStats o2) {
					return (int) Math.ceil(o1.totalTermFreq - o2.totalTermFreq);
				}
			});
			
			System.out.println("Top des termes: ");
			for (TermStats termStats : termsStats) {
				System.out.println(termStats.termtext.utf8ToString() + " - fr�quence: " + termStats.totalTermFreq);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
