package sub.fwb;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class UmlautFilterFactory extends TokenFilterFactory {
	public UmlautFilterFactory(Map<String, String> args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TokenStream create(TokenStream ts) {
		return new UmlautFilter(ts);
	}
}