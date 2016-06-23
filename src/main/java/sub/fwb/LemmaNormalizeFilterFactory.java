package sub.fwb;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LemmaNormalizeFilterFactory extends TokenFilterFactory {

	public LemmaNormalizeFilterFactory(Map<String, String> args) {
		super(args);
	}

	@Override
	public TokenStream create(TokenStream ts) {
		return new LemmaNormalizeFilter(ts);
	}

}
