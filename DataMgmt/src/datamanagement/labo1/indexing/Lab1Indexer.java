package datamanagement.labo1.indexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;

public class Lab1Indexer {
	
	private IndexWriter iwriter;
	private String filePath;
	private Analyzer analyzer;
	private DefaultSimilarity similarity;
	private FieldType fieldType;
	
	private static final int ID = 0;
	private static final int AUTHOR = 1;
	private static final int TITLE = 2;
	private static final int SUMMARY = 3;
	public static final String IDFIELD = "id";
	public static final String AUTHORFIELD = "author";
	public static final String TITLEFIELD = "title";
	public static final String SUMMARYFIELD = "summary";
	public static final String INDEXDIR = "index";
	
    /** Creates a new instance of Indexer */
    public Lab1Indexer(Analyzer analyzer, String path) {
    	this.analyzer = analyzer;
    	this.filePath = path;
    	this.similarity = null;
    	createAndConfigureWriter();
    	createAndConfigureFieldType();
    }
    

	public Lab1Indexer(Analyzer analyzer, String path, DefaultSimilarity similarity) {
    	this(analyzer,path);
    	this.similarity = similarity;
    	createAndConfigureWriter();
    }
    

    public void IndexPublications()
    {
    	if(null != iwriter && null != fieldType)
    	{
    		long beginningTime = System.currentTimeMillis();
    		try {
				BufferedReader br = getFileBuffer();
				String documentLine;
				
				if(!iwriter.isOpen()) createAndConfigureWriter();
				
				while(null != (documentLine = br.readLine()))
				{
					Document document = new Document(); // instantiate a new doc for each publication line
					String columns[] = documentLine.split("\t"); //split into columns
					
					addField(IDFIELD, columns[ID], document); // add the id
					addAuthorsField(columns[AUTHOR], document); // add the author(s)
					addField(TITLEFIELD, columns[TITLE], document); // add the title
					if(columns.length > 3)
						addField(SUMMARYFIELD, columns[SUMMARY], document);
					
					iwriter.addDocument(document);
				}
				
				br.close();
				iwriter.close();
				iwriter.getDirectory().close();
				System.out.println("Index cr�� � " + FileSystems.getDefault().getPath(INDEXDIR).toAbsolutePath().toString());
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
    		long endTime = System.currentTimeMillis();
    		System.out.println("Temps total pour l'indexation: " + (endTime - beginningTime)+ "ms");
    	}
    	else
    	{
    		System.out.println("Impossible de lancer l'indexation.");
    	}
    }
    
    private void createAndConfigureFieldType() {
		FieldType ft = new FieldType();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // Indexes documents, frequencies, positions and offsets.
		ft.setTokenized(true);
		ft.setStored(true);
		
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorPositions(true);
		ft.setStoreTermVectorOffsets(true);
		
		ft.freeze();
		
		fieldType = ft;
	}
    
    private void createAndConfigureWriter() {
    	IndexWriterConfig indexWriterConf = new IndexWriterConfig(analyzer);
    	indexWriterConf.setOpenMode(OpenMode.CREATE); // always create and replace existing index
    	indexWriterConf.setUseCompoundFile(false);
    	
    	if(null != similarity) indexWriterConf.setSimilarity(similarity);
    	
    	try {
			iwriter = new IndexWriter(FSDirectory.open(FileSystems.getDefault().getPath(INDEXDIR)), indexWriterConf);
    	} catch (IOException e) {
			System.out.println(e.getMessage());
		}
    }

	private void addAuthorsField(String authors, Document doc) {
		if(authors.length() > 0)
		{
			String[] authorsArray = authors.split(";");
			FieldType authorFieldType = new FieldType(fieldType);
			authorFieldType.setTokenized(false);
			for(String author: authorsArray)
			{
				Field authorField = new Field(AUTHORFIELD, author, authorFieldType);
				doc.add(authorField);
			}
		}
	}

	private void addField(String id, String content, Document doc) {
		Field fieldId = new Field(id,content,fieldType);
		doc.add(fieldId);
	}

	private BufferedReader getFileBuffer() throws FileNotFoundException {
		
		return new BufferedReader(new FileReader(filePath));
	}
    
   
}
