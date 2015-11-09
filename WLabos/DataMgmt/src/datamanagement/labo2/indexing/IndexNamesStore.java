package datamanagement.labo2.indexing;

public class IndexNamesStore {
	public static final String STANDARD = "Standard";
	public static final String WHITESPACE = "WhiteSpace";
	public static final String ENGLISH = "English";
	public static final String ENGLISH_STOP = "EnglishStop";

	public static String IndexNameForAnalyzerType(AnalyzersType type)
	{
		switch (type) {
		case English: return ENGLISH;
		case WhiteSpace: return WHITESPACE;
		case EnglishStop: return ENGLISH_STOP;
		case Standard: 
		default: return ENGLISH;
		}
	}

}
