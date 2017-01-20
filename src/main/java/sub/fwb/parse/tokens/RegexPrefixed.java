package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

public class RegexPrefixed extends QueryTokenPrefixed {

	public RegexPrefixed(String regexString, String prefixEnding) {
		originalTokenString = regexString;
		escapedString = regexString;
		splitIntoPrefixAndPostfix(prefixEnding);
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		return String.format("%s:%s ", prefixWithEnding, postfix);
	}

	@Override
	public String getHlQuery() throws ParseException {
		return String.format("%s_text%s:%s ", prefix, prefixEnding, postfix);
	}

	@Override
	public Map<String, String> getFacetQueries() {
		mapForFacetQueries.put(prefixWithEnding, postfix);
		return mapForFacetQueries;
	}

}
