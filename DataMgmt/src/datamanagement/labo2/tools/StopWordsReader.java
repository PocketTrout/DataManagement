package datamanagement.labo2.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.util.CharArraySet;

public class StopWordsReader {
	
	public static CharArraySet getStopWords(String path) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		ArrayList<String> stopWords = new ArrayList<String>();
		String line;
		while(null != (line = reader.readLine()))
		{
			stopWords.add(line);
		}
		reader.close();
		return new CharArraySet(stopWords, true);
	}
}
