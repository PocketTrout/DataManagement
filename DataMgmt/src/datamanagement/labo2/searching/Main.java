package datamanagement.labo2.searching;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.packed.PackedLongValues.Iterator;

import datamanagement.labo1.indexing.Lab1Indexer;
import datamanagement.labo2.evaluation.RelevantParser;
import datamanagement.labo2.evaluation.ResultsAnalyzer;
import datamanagement.labo2.evaluation.StatisticsCalculator;
import datamanagement.labo2.indexing.AnalyzersType;
import datamanagement.labo2.indexing.IndexNamesStore;
import datamanagement.labo2.tools.StopWordsReader;

public class Main {
	
	public static void main(String[] args) {
		Analyzer analyzer = null;
		String indexName = "";
		RelevantParser parser = new RelevantParser("qrels.txt");
		
		for(AnalyzersType type: AnalyzersType.values())
		{
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
			
			indexName = IndexNamesStore.IndexNameForAnalyzerType(type);
			System.out.println("Recherche dans l'index " + indexName);
			if(null != analyzer)
			{
				Path indexPath = FileSystems.getDefault().getPath(Lab1Indexer.INDEXDIR + "/" + indexName);
				try
				{
					Directory indexDir = FSDirectory.open(indexPath);
					IndexReader iReader = DirectoryReader.open(indexDir);
					System.out.println("Total Number of documents: " + iReader.numDocs());
					
					Lab2Searcher searcher = new Lab2Searcher(analyzer, iReader);
					Map<Integer, Query> queries = searcher.prepareQueryListFromFile("query.txt");
					//double[] recallsLevels = getRecallsLevels();
					//List<Map<Double,Double>> precisionsAtLevels = new ArrayList<Map<Double,Double>>();
					double sumMAP = 0.0;
					double sumRPrecision20 = 0;
					
					java.util.Iterator<Entry<Integer, Query>> it = queries.entrySet().iterator();
					while(it.hasNext())
					{
						Entry<Integer, Query> pair = it.next();
						int queryId = pair.getKey();
						TopDocs docs = searcher.proceedQuery(pair.getValue(), Integer.MAX_VALUE);
						parser.GetRelevantDocs(queryId);
						ResultsAnalyzer resAnalyzer = new ResultsAnalyzer(parser.GetRelevantDocs(queryId), queryId, docs);
						printSummaryStatisticsForArray(resAnalyzer);
						//precisionsAtLevels.add(resAnalyzer.getPrecisionAtStandardRecall(recallsLevels));
						sumMAP += resAnalyzer.getAveragePrecision();
						sumRPrecision20 += resAnalyzer.getRPrecision(20);
					}
					//System.out.println("Average precisions at recall levels:");
					//printKeySortedMapForArray(ResultsAnalyzer.getAveragePrecisionsAtRecallLevels(precisionsAtLevels));
					System.out.println("\nAverage Across Queries (MAP):\t" + sumMAP / (double)queries.size());
					System.out.println("Average R-Precision, R = 20:\t" + sumRPrecision20 / (double)queries.size());
					
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
				catch(ParseException e)
				{
					System.err.println(e.getMessage());
				}
			}
			System.out.println("\n\n----------------------------------------\n\n");
		}
	}

	private static double[] getRecallsLevels() {
		double[] recallsLevels = new double[11];
		for(int i = 0; i < 11; i++)
			recallsLevels[i] = (double)i * 0.1;
		return recallsLevels;
	}
	
	public static void printSummaryStatistics(ResultsAnalyzer resAnalyzer)
	{
		System.out.println("For query N°" + resAnalyzer.getQueryId());
		System.out.println("\tTotal Number retrieved documents: " + resAnalyzer.getQueryResults().totalHits);
		System.out.println("\tTotal Number of relevant documents: " + resAnalyzer.getTotalNumberOfRelevantDocuments());
		System.out.println("\tNumber of relevant retrieved documents: " + resAnalyzer.getNumberOfRelevantDocumentsRetrieved());
	
	}
	
	public static void printSummaryStatisticsForArray(ResultsAnalyzer resAnalyzer)
	{
		System.out.print(resAnalyzer.getQueryId());
		System.out.print("\t" + resAnalyzer.getQueryResults().totalHits);
		System.out.print("\t" + resAnalyzer.getTotalNumberOfRelevantDocuments());
		System.out.print("\t" + resAnalyzer.getNumberOfRelevantDocumentsRetrieved());
		System.out.print("\n");
	}
	
	public static void printKeySortedMapForArray(Map<Double,Double> map)
	{
		SortedSet<Double> keys = new TreeSet<Double>(map.keySet());
		for (Double key : keys) { 
		   Double value = map.get(key);
		   System.out.println(key + "\t" + value);
		}
	}
	
	
	
}
