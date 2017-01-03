package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class PhrasePrefixed extends QueryTokenPrefixed {

	public PhrasePrefixed(String phraseString, String prefixEnding) {
		originalTokenString = ParseUtil.removeParensAndPipe(phraseString);
		escapeSpecialChars();
		splitIntoPrefixAndPostfix(prefixEnding);
	}

	@Override
	public String getModifiedQuery() {
		return String.format("+%s:%s ", prefixWithEnding, postfix);
	}

	@Override
	public String getHlQuery() throws ParseException {
		return String.format("%s_text%s:%s ", prefix, prefixEnding, postfix);
	}

	@Override
	public Map<String, String> getFacetQueries() {
		mapForFacetQueries.put(prefixWithEnding, prefixWithEnding + ":" + postfix);
		return mapForFacetQueries;
	}

}
