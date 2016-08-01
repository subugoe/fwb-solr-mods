package sub.fwb;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class SimplifiedSimilarity extends ClassicSimilarity {

//	@Override
//	public float coord(int overlap, int maxOverlap) {
//		return (float) 1.0;
//	}

	@Override
	public float lengthNorm(FieldInvertState state) {
		return state.getBoost();
	}

	@Override
	public float idf(long docFreq, long numDocs) {
		return (float) 1.0;
	}

	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		return (float) 1.0;
	}

}
