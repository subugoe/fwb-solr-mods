package sub.fwb;

import org.apache.lucene.search.similarities.ClassicSimilarity;

public class SimplifiedSimilarity extends ClassicSimilarity {

	@Override
	public float lengthNorm(int numTerms) {
		return (float) 1.0;
	}

	@Override
	public float idf(long docFreq, long numDocs) {
		return (float) 1.0;
	}

}
