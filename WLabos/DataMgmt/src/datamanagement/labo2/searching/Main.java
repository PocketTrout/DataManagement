package datamanagement.labo2.searching;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.packed.PackedLongValues.Iterator;

import datamanagement.labo1.indexing.Lab1Indexer;
import datamanagement.labo2.indexing.AnalyzersType;
import datamanagement.labo2.indexing.IndexNamesStore;
import datamanagement.labo2.tools.StopWordsReader;

public class Main {
	
	public static void main(String[] args) {
		AnalyzersType type = AnalyzersType.Standard;
		Analyzer analyzer = null;
		String indexName = IndexNamesStore.IndexNameForAnalyzerType(type);
		
		switch(type)
		{
		case WhiteSpace: analyzer = new WhitespaceAnalyzer();
			break;
		case EnglishStop: try {
				analyzer = new EnglishAnalyzer(StopWordsReader.getStopWords("common_words.txt"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			break;
		case English: analyzer = new EnglishAnalyzer();
			break;
		case Standard: 
		default: analyzer = new StandardAnalyzer();
		}
		
		if(null != analyzer)
		{
			Path indexPath = FileSystems.getDefault().getPath(Lab1Indexer.INDEXDIR + "/" + indexName);
			try
			{
				Directory indexDir = FSDirectory.open(indexPath);
				IndexReader iReader = DirectoryReader.open(indexDir);
				
				Lab2Searcher searcher = new Lab2Searcher(analyzer, iReader);
				Map<Integer, Query> queries = searcher.prepareQueryListFromFile("query.txt");
				
				java.util.Iterator<Entry<Integer, Query>> it = queries.entrySet().iterator();
				while(it.hasNext())
				{
					Entry<Integer, Query> pair = it.next();
					System.out.println("Query id: " + pair.getKey());
					System.out.println(searcher.proceedQuery(pair.getValue(), 50).totalHits);
				}
				
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			catch(ParseException e)
			{
				System.err.println(e.getMessage());
			}
		}
	}
	
}
