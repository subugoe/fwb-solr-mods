package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Phrase extends QueryTokenSearchString {

	public Phrase(String phraseString, String prefixEnding, Map<String, String> mapForFacetQueries) {
		this.mapForFacetQueries = new HashMap<>(mapForFacetQueries);
		this.prefixEnding = prefixEnding;
		originalTokenString = ParseUtil.removeParensAndPipe(phraseString);
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() {
		String articleField = ParseUtil.article(prefixEnding);
		String citationField = ParseUtil.citation(prefixEnding);
		return String.format("(%s +(%s:%s %s:%s)) ", escapedString, articleField, escapedString, citationField,
				escapedString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String articleTextField = ParseUtil.articleText(prefixEnding);
		String citationTextField = ParseUtil.citationText(prefixEnding);
		return String.format("%s:%s %s:%s ", articleTextField, escapedString, citationTextField, escapedString);
	}

	@Override
	public Map<String, String> getFacetQueries() {
		for (String searchField : mapForFacetQueries.keySet()) {
			mapForFacetQueries.put(searchField, escapedString);
		}
		return mapForFacetQueries;
	}

}
