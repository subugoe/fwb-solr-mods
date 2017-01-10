package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class ComplexPhrase extends QueryToken {

	public ComplexPhrase(String phraseString, String prefixEnding, Map<String, String> mapForFacetQueries) {
		this.mapForFacetQueries = new HashMap<>(mapForFacetQueries);
		this.prefixEnding = prefixEnding;
		originalTokenString = phraseString;
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		String articleField = ParseUtil.article(prefixEnding);
		String citationField = ParseUtil.citation(prefixEnding);
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		ParseUtil.checkIfOneWord(escapedPhrase);
		ParseUtil.checkForLeadingWildcards(escapedPhrase);
		return String.format(
				"_query_:\"{!complexphrase}%s\" +(_query_:\"{!complexphrase}%s:%s\" _query_:\"{!complexphrase}%s:%s\") ",
				escapedPhrase, articleField, escapedPhrase, citationField, escapedPhrase);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String articleTextField = ParseUtil.articleText(prefixEnding);
		String citationTextField = ParseUtil.citationText(prefixEnding);
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		return String.format("_query_:\"{!complexphrase}%s:%s\" _query_:\"{!complexphrase}%s:%s\" ",
				articleTextField, escapedPhrase, citationTextField, escapedPhrase);
	}

//	@Override
//	public Map<String, String> getFacetQueries() {
//		for (String searchField : mapForFacetQueries.keySet()) {
//			String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
//			String newQuery = String.format("_query_:\"{!complexphrase}%s\"", escapedPhrase);
//			mapForFacetQueries.put(searchField, newQuery);
//		}
//		return mapForFacetQueries;
//	}

}
