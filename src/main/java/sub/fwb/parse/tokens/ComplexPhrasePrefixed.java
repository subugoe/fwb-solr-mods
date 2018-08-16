package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class ComplexPhrasePrefixed extends QueryTokenPrefixed {

	public ComplexPhrasePrefixed(String phraseString, String prefixEnding) {
		originalTokenString = phraseString;
		escapeSpecialChars();
		splitIntoPrefixAndPostfix(prefixEnding);
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		ParseUtil.checkIfOneWord(escapedPhrase);
		String parser = "complexphrase";
		return String.format("_query_:\"{!%s}%s:%s\" ", parser, prefixWithEnding,
				postfix.replaceAll("\"", "\\\\\""));
	}

	@Override
	public String getHlQuery() throws ParseException {
		if (prefix.equals("zitat") && prefixEnding.isEmpty()) {
			throw new ParseException("Phrasensuche mit * und ? ist in Zitaten nur als exakte Suche möglich.");
		}
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		String postfixTemp = escapedPhrase.split(":")[1];
		String parser = "complexphrase";
		return String.format("_query_:\"{!%s}%s_text%s:%s\" ", parser, prefix, prefixEnding, postfixTemp);
	}

//	@Override
//	public Map<String, String> getFacetQueries() {
//		String newQuery = String.format("_query_:\"{!complexphrase}%s:%s\"", prefixWithEnding,
//				postfix.replaceAll("\"", "\\\\\""));
//		mapForFacetQueries.put(prefixWithEnding, newQuery);
//		return mapForFacetQueries;
//	}

}
