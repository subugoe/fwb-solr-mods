package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Term extends QueryToken {

	public Term(String tokenString, String prefixEnding, Map<String, String> mapForFacetQueries) throws ParseException {
		this.prefixEnding = prefixEnding;
		originalTokenString = tokenString;
		this.mapForFacetQueries = new HashMap<>(mapForFacetQueries);
		escapeSpecialChars();
		ParseUtil.checkForProhibitedCharsInTerm(escapedString);
	}

	@Override
	public String getModifiedQuery() {
		String articleField = ParseUtil.article(prefixEnding);
		String citationField = ParseUtil.citation(prefixEnding);
		if (escapedString.endsWith("~1") || escapedString.endsWith("~2")) {
			String s = escapedString.substring(0, escapedString.length() - 2);
			s = ParseUtil.freeFromCircumflexAndDollar(s);
			String fuzzy = escapedString.substring(escapedString.length() - 2);
			return String.format("%s%s +(%s:%s%s %s:%s%s) ", s, fuzzy, articleField, s, fuzzy, citationField, s, fuzzy);
		} else if (escapedString.startsWith("^") && escapedString.endsWith("$")) {
			String s = escapedString.substring(1, escapedString.length() - 1);
			return String.format("%s +(%s:%s %s:%s) ", s, articleField, s, citationField, s);
		} else if (escapedString.startsWith("^")) {
			String s = escapedString.substring(1, escapedString.length());
			return String.format("%s %s* +(%s:%s* %s:%s*) ", s, s, articleField, s, citationField, s);
		} else if (escapedString.endsWith("$")) {
			String s = escapedString.substring(0, escapedString.length() - 1);
			return String.format("*%s +(%s:*%s %s:*%s) ", s, articleField, s, citationField, s);
		} else {
			return String.format("%s %s* *%s* +(%s:*%s* %s:*%s*) ", escapedString, escapedString, escapedString,
					articleField, escapedString, citationField, escapedString);
		}
	}

	@Override
	public String getHlQuery() throws ParseException {
		String articleTextField = ParseUtil.articleText(prefixEnding);
		String citationTextField = ParseUtil.citationText(prefixEnding);
		if (escapedString.endsWith("~1") || escapedString.endsWith("~2")) {
			String s = escapedString.substring(0, escapedString.length() - 2);
			s = ParseUtil.freeFromCircumflexAndDollar(s);
			String fuzzy = escapedString.substring(escapedString.length() - 2);
			return String.format("%s:%s%s %s:%s%s ", articleTextField, s, fuzzy, citationTextField, s, fuzzy);
		} else if (escapedString.startsWith("^") && escapedString.endsWith("$")) {
			String s = escapedString.substring(1, escapedString.length() - 1);
			return String.format("%s:%s %s:%s ", articleTextField, s, citationTextField, s);
		} else if (escapedString.startsWith("^")) {
			String s = escapedString.substring(1, escapedString.length());
			return String.format("%s:%s* %s:%s* ", articleTextField, s, citationTextField, s);
		} else if (escapedString.endsWith("$")) {
			String s = escapedString.substring(0, escapedString.length() - 1);
			return String.format("%s:*%s %s:*%s ", articleTextField, s, citationTextField, s);
		} else {
			return String.format("%s:*%s* %s:*%s* ", articleTextField, escapedString, citationTextField, escapedString);
		}
	}

	@Override
	public Map<String, String> getFacetQueries() {
		if (escapedString.endsWith("~1") || escapedString.endsWith("~2")) {
		} else if (escapedString.startsWith("^") && escapedString.endsWith("$")) {
		} else if (escapedString.startsWith("^")) {
		} else if (escapedString.endsWith("$")) {
		} else {
			for (String searchField : mapForFacetQueries.keySet()) {
				mapForFacetQueries.put(searchField, searchField + ":*" + escapedString + "*");
			}
		}
		return mapForFacetQueries;
	}

}