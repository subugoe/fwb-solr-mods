package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class ComplexPhrase extends QueryTokenSearchString {

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
		String parser = "complexphrase";
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		ParseUtil.checkIfOneWord(escapedPhrase);
		return String.format(
				"(_query_:\"{!%s}%s\" +(_query_:\"{!%s}%s:%s\" _query_:\"{!%s}%s:%s\")) ",
				parser, escapedPhrase, parser, articleField, escapedPhrase, parser, citationField, escapedPhrase);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String articleTextField = ParseUtil.articleText(prefixEnding);
		String citationTextField = ParseUtil.citationText(prefixEnding);
		String parser = "complexphrase";
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		return String.format("_query_:\"{!%s}%s:%s\" _query_:\"{!%s}%s:%s\" ",
				parser, articleTextField, escapedPhrase, parser, citationTextField, escapedPhrase);
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
