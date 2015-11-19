package datamanagement.labo2.evaluation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.DiversifiedTopDocsCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class ResultsAnalyzer {
	
	private Set<Integer> relevantDocsForQuery;
	private int queryId;
	private TopDocs queryResults;
	
	public ResultsAnalyzer(Set<Integer> relevantDocsForQuery, int queryId, TopDocs queryResults) {
		if(relevantDocsForQuery != null)
			this.relevantDocsForQuery = relevantDocsForQuery;
		else
			this.relevantDocsForQuery = new HashSet<Integer>();
		this.queryId = queryId;
		this.queryResults = queryResults;
	}
	
	public int getTotalNumberOfRelevantDocuments()
	{
		return relevantDocsForQuery.size();
	}
	
	public int getNumberOfRelevantDocumentsRetrieved()
	{
		int relevants = 0;
		if(relevantDocsForQuery != null)
		{
			for(ScoreDoc doc : queryResults.scoreDocs)
			{
				if(relevantDocsForQuery.contains(doc.doc))
					relevants++;
			}
		}
		return relevants;
	}
	
	public static double getPrecision(int tp, int fp)
	{
		return (double) tp / ((double)tp + (double)fp);
	}
	
	public static double getRecall(int tp, int fn)
	{
		return (double)tp/ ((double)tp +(double)fn);
	}

	public Set<Integer> getRelevantDocsForQuery() {
		return relevantDocsForQuery;
	}

	public void setRelevantDocsForQuery(Set<Integer> relevantDocsForQuery) {
		this.relevantDocsForQuery = relevantDocsForQuery;
	}

	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}

	public TopDocs getQueryResults() {
		return queryResults;
	}

	public void setQueryResults(TopDocs queryResults) {
		this.queryResults = queryResults;
	}
	
	public List<PrecisionRecallPoint> getPrecisionsAndRecallGraphPoints()
	{
		int tp = 0;
		int fp = 0;
		int fn = 0;
		boolean isRelevant = false;
		ArrayList<PrecisionRecallPoint> points = new ArrayList<PrecisionRecallPoint>();
		
		for(ScoreDoc doc: queryResults.scoreDocs)
		{
			if(relevantDocsForQuery.contains(doc.doc)) {
				tp++;
				isRelevant = true;
			}
			else {
				fp++;
				isRelevant = false;
			}
			fn = getTotalNumberOfRelevantDocuments() - tp;
			points.add(new PrecisionRecallPoint(getPrecision(tp, fp), getRecall(tp, fn), isRelevant));
		}
		return points;
	}
	
	public double getRPrecision(int R)
	{
		List<PrecisionRecallPoint> points = getPrecisionsAndRecallGraphPoints();
		int relevants = 0;
		for(int i = 0; i < R && i < points.size(); i++)
		{
			if(points.get(i).isRelevant())
				relevants++;
		}
		return (double)relevants / R;
	}
	
	public double getAveragePrecision()
	{
		List<PrecisionRecallPoint> points = getPrecisionsAndRecallGraphPoints();
		double averageP = 0;
		int nbRelevants = 0;
		int totalRelevant = getTotalNumberOfRelevantDocuments();
		
		for(PrecisionRecallPoint p: points)
		{
			if(p.isRelevant())
			{
				averageP += p.getPrecision();
				nbRelevants++;
			}
			if(nbRelevants >= totalRelevant) break;
		}
		return nbRelevants > 0. ? averageP / nbRelevants : 0.;
	}
	
	public Map<Double, Double> getPrecisionAtStandardRecall(double[] recallLevels)
	{
		List<PrecisionRecallPoint> points = getPrecisionsAndRecallGraphPoints();
		Map<Double, Double> resultsPrecision = new HashMap<Double, Double>();
		for(double recall: recallLevels)
		{
			double precision = 0.;
			for(PrecisionRecallPoint p : points)
			{
				if(p.getRecall() >= recall && p.getPrecision() > precision)
					precision = p.getPrecision();
			}
			resultsPrecision.put(recall, precision);
		}
		return resultsPrecision;
	}
	
	public static Map<Double, Double> getAveragePrecisionsAtRecallLevels(List<Map<Double,Double>> precisionsAtLevel)
	{
		Map<Double,Double> results = new HashMap<Double,Double>();
		int total = 0;
		for(Map<Double, Double> map: precisionsAtLevel)
		{
			for(Map.Entry<Double, Double> pair: map.entrySet())
			{
				Double sumPrecision = results.get(pair.getKey());
				sumPrecision = sumPrecision == null ? 0 : sumPrecision;
				results.put(pair.getKey(), sumPrecision + pair.getValue());
			}
			total++;
		}
		System.out.println(total);
		for(Map.Entry<Double, Double> pair: results.entrySet())
		{
			Double sumPrecision = results.get(pair.getKey());
			sumPrecision = sumPrecision == null ? 0 : sumPrecision;
			results.put(pair.getKey(), sumPrecision / (double)total);
		}
		return results;
	}
}
