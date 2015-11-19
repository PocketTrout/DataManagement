package datamanagement.labo2.evaluation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RelevantParser {
	
	private String filePath;
	private Map<Integer, Set<Integer>> relevantDocumentsForQueries;
	
	public RelevantParser(String filepath)
	{
		this.filePath = filepath;
		this.relevantDocumentsForQueries = parse();
	}
	
	private Map<Integer, Set<Integer>> parse()
	{
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
		
			HashMap<Integer, Set<Integer>> relevantsDocForQueries = new HashMap<Integer, Set<Integer>>();
			String relevantDoc = "";
			while(null != (relevantDoc = reader.readLine()))
			{
				String column[] = relevantDoc.split(" ");
				if(column.length > 1)
				{
					Integer idQuery = Integer.valueOf(column[0]);
					if(relevantsDocForQueries.containsKey(idQuery))
					{
						relevantsDocForQueries.get(idQuery).add(Integer.valueOf(column[1]));
					}
					else
					{
						HashSet<Integer> docIds = new HashSet<Integer>();
						docIds.add(Integer.valueOf(column[1]));
						relevantsDocForQueries.put(idQuery, docIds);
					}
				}
			}
			reader.close();
			return relevantsDocForQueries;
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return new HashMap<Integer, Set<Integer>>();
	}
	
	public Set<Integer> GetRelevantDocs(int idQuery)
	{
		return this.relevantDocumentsForQueries.get(idQuery);
	}
}
