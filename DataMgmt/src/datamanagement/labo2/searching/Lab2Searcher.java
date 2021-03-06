package datamanagement.labo2.searching;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.DefaultSimilarity;

import datamanagement.labo1.indexing.Lab1Indexer;
import datamanagement.labo2.indexing.Lab2Indexer;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

public class Lab2Searcher {

	private Analyzer analyzer;
	private IndexSearcher iSearcher;
	
	public Lab2Searcher(Analyzer analyzer, IndexReader indexReader)
	{
		this.analyzer = analyzer;
		this.iSearcher = new IndexSearcher(indexReader);
	}
	
	public Lab2Searcher(Analyzer analyzer, IndexReader indexReader, DefaultSimilarity similarity)
	{
		this(analyzer, indexReader);
		this.iSearcher.setSimilarity(similarity);
	}
	
	
	public Map<Integer,Query> prepareQueryListFromFile(String filepath) throws IOException
	{
		Map<Integer,Query> queryMap = new TreeMap<Integer,Query>();
		QueryParser queryParser = new QueryParser(Lab2Indexer.CONTENTFIELD, analyzer);
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String query;
		while(null != (query = reader.readLine()))
		{
			String column[] = query.split("\t");
			if(column.length >= 2)
			{
				try {
					queryMap.put(Integer.parseInt(column[0]), queryParser.parse(QueryParser.escape(column[1])));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				} catch (ParseException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		reader.close();
		return queryMap;
	}
	
	public TopDocs proceedQuery(String queryText, int numberOfTopDocs) throws IOException, ParseException
	{
		QueryParser queryParser = new QueryParser(Lab2Indexer.CONTENTFIELD, analyzer);
		Query query = queryParser.parse(queryText);
		
		TopDocs results = iSearcher.search(query, numberOfTopDocs);	
		return results;
	}
	
	public TopDocs proceedQuery(Query query, int numberOfTopDocs) throws IOException, ParseException
	{	
		TopDocs results = iSearcher.search(query, numberOfTopDocs);	
		return results;
	}
	
	public void close() throws IOException
	{
		if(null != iSearcher)
		{
			iSearcher.getIndexReader().close();
		}	
	}
}
