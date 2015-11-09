package datamanagement.labo1.tuning;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class Similarity extends DefaultSimilarity {

    @Override
    public float tf(float freq) {
       return (float)(1 + Math.log(freq));
    }
    @Override
    public float idf(long docFreq, long numDocs) {
        return (float)Math.log((double)numDocs / (double)docFreq);
    }
    @Override
    public float coord(int overlap, int maxOverlap) {
        return (float)Math.sqrt((double)overlap / (double)maxOverlap);
    }

    @Override
    public float lengthNorm(FieldInvertState state) {
        return 1;
    }
}