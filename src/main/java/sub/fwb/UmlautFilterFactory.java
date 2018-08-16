package sub.fwb;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class UmlautFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {

	private String mappingsFile;
	private CharArraySet mappings;
	
	
	public UmlautFilterFactory(Map<String, String> args) {
		super(args);
		mappingsFile = get(args, "file");
	}

	@Override
	public void inform(ResourceLoader loader) throws IOException {
		if (mappingsFile != null) {
			mappings = getWordSet(loader, mappingsFile, false);
		}
	}

	@Override
	public TokenStream create(TokenStream ts) {
		return new UmlautFilter(ts, mappings);
	}
}