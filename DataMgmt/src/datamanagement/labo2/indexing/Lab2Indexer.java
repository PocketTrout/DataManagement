package datamanagement.labo2.indexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;

public class Lab2Indexer {

	private IndexWriter iwriter;
	private String filePath;
	private Analyzer analyzer;
	private DefaultSimilarity similarity;
	private FieldType fieldType;
	private String indexName;
	
	private static final int ID = 0;
	private static final int TITLE = 2;
	private static final int SUMMARY = 3;
	public static final String IDFIELD = "id";
	public static final String CONTENTFIELD = "content";
	public static final String INDEXDIR = "index";
	
	public Lab2Indexer(Analyzer analyzer, String path, String indexName) {
    	this.analyzer = analyzer;
    	this.filePath = path;
    	this.indexName = indexName;
    	this.similarity = null;
    	createAndConfigureWriter();
    	createAndConfigureFieldType();
    }
    

	public Lab2Indexer(Analyzer analyzer, String path, String indexName, DefaultSimilarity similarity) {
    	this(analyzer, path, indexName);
    	this.similarity = similarity;
    	createAndConfigureWriter();
    }
	
	private void createAndConfigureFieldType() {
		FieldType ft = new FieldType();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // Indexes documents, frequencies, positions and offsets.
		ft.setTokenized(true);
		ft.setStored(true);
		
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorPositions(true);
		ft.setStoreTermVectorOffsets(true);
		
		ft.setNumericType(NumericType.INT);
		
		ft.freeze();
		
		fieldType = ft;
	}
    
    private void createAndConfigureWriter() {
    	IndexWriterConfig indexWriterConf = new IndexWriterConfig(analyzer);
    	indexWriterConf.setOpenMode(OpenMode.CREATE); // always create and replace existing index
    	indexWriterConf.setUseCompoundFile(false);
    	
    	if(null != similarity) indexWriterConf.setSimilarity(similarity);
    	
    	try {
			iwriter = new IndexWriter(FSDirectory.open(FileSystems.getDefault().getPath(INDEXDIR + "/" + indexName)), indexWriterConf);
    	} catch (IOException e) {
			System.out.println(e.getMessage());
		}
    }
    
    private BufferedReader getFileBuffer() throws FileNotFoundException {
		
		return new BufferedReader(new FileReader(filePath));
	}
    
    private void addField(String id, String content, Document doc, FieldInitType initType) {
    	Field field;
    	switch (initType) {
		case IntField: field = new IntField(id, (int)Integer.parseInt(content), fieldType);
			
			break;
		case TextField: field = new TextField(id, content, Field.Store.YES);
			break;
		default: field = new Field(id, content, fieldType);
			break;
		}
		doc.add(field);
	}
    
    public void IndexPublications()
    {
    	if(null != iwriter && null != fieldType)
    	{
    		long beginningTime = System.currentTimeMillis();
    		try {
				BufferedReader br = getFileBuffer();
				String documentLine;
				
				if(iwriter.isOpen())
				{
				
				while(null != (documentLine = br.readLine()))
				{
					Document document = new Document(); // instantiate a new doc for each publication line
					String columns[] = documentLine.split("\t"); //split into columns
					
					addField(IDFIELD, columns[ID], document, FieldInitType.IntField); // add the id
					if(columns.length > 3)
						addField(CONTENTFIELD, columns[TITLE].concat(columns[SUMMARY]), document, FieldInitType.TextField); // add the content
					else
						addField(CONTENTFIELD, columns[TITLE], document, FieldInitType.TextField); // add the content

					iwriter.addDocument(document);
				}
				
				br.close();
				iwriter.close();
				iwriter.getDirectory().close();
				System.out.println("Index cr�� � " + FileSystems.getDefault().getPath(INDEXDIR + "/" + indexName).toAbsolutePath().toString());
				}
				else
				{
					System.out.println("Indexation impossible...");
				}
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
}
