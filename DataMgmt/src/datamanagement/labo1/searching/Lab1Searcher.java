package datamanagement.labo1.searching;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;

import datamanagement.labo1.indexing.Lab1Indexer;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public class Lab1Searcher {

	private Analyzer analyzer;
	private IndexSearcher iSearcher;
	public Lab1Searcher(Analyzer analyzer, IndexReader indexReader)
	{
		this.analyzer = analyzer;
		this.iSearcher = new IndexSearcher(indexReader);
	}
	
	public Lab1Searcher(Analyzer analyzer, IndexReader indexReader, DefaultSimilarity similarity)
	{
		this(analyzer, indexReader);
		this.iSearcher.setSimilarity(similarity);
	}
	
	public TopDocs proceedQuery(String queryText, int numberOfTopDocs) throws IOException, ParseException
	{
		QueryParser queryParser = new QueryParser(Lab1Indexer.SUMMARYFIELD, analyzer);
		Query query = queryParser.parse(queryText);
		
		TopDocs results = iSearcher.search(query, numberOfTopDocs);	
		System.out.println("Recherche de " + queryText + ": " + results.totalHits + " resultats");
		return results;
	}
	
	public void QueryAndDisplayScore(String queryText, int numberOfTopDocs) throws IOException, ParseException
	{
		TopDocs results = proceedQuery(queryText, numberOfTopDocs);
		for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = iSearcher.doc(scoreDoc.doc);
            System.out.printf("%4d : %s (%f)\n", scoreDoc.doc+1, doc.getField(Lab1Indexer.TITLEFIELD).stringValue(), scoreDoc.score);
        }
	}
	
	public void close() throws IOException
	{
		if(null != iSearcher)
		{
			iSearcher.getIndexReader().close();
		}
			
	}
}
