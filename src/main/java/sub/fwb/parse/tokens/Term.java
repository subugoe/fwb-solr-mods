package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Term extends QueryToken {

	private String articleField;
	private String citationField;
	private String articleTextField;
	private String citationTextField;
	protected String searchWord;

	public Term(String tokenString, String prefixEnding, Map<String, String> mapForFacetQueries) throws ParseException {
		this.prefixEnding = prefixEnding;
		originalTokenString = tokenString;
		this.mapForFacetQueries = new HashMap<>(mapForFacetQueries);
		escapeSpecialChars();
		ParseUtil.checkForProhibitedCharsInTerm(escapedString);
		articleField = ParseUtil.article(prefixEnding);
		citationField = ParseUtil.citation(prefixEnding);
		articleTextField = ParseUtil.articleText(prefixEnding);
		citationTextField = ParseUtil.citationText(prefixEnding);
	}
	
	private Term() {		
	}
	
	private class Fuzzy extends Term {

		private String fuzzy;

		public Fuzzy(String fuzzyEnding) {
			this.fuzzy = fuzzyEnding;
		}

		@Override
		public String getModifiedQuery() {
			return String.format("%s%s +(%s:%s%s %s:%s%s) bla", searchWord, fuzzy, articleField, searchWord, fuzzy, citationField, searchWord, fuzzy);			
		}

		@Override
		public String getHlQuery() throws ParseException {
			return String.format("%s:%s%s %s:%s%s ", articleTextField, searchWord, fuzzy, citationTextField, searchWord, fuzzy);
		}

	}
	
	private Term createSpecialTerm() {
		if (escapedString.endsWith("~1") || escapedString.endsWith("~2")) {
			searchWord = escapedString.substring(0, escapedString.length() - 2);
			searchWord = ParseUtil.freeFromCircumflexAndDollar(searchWord);
			String fuzzyEnding = escapedString.substring(escapedString.length() - 2);
			return new Fuzzy(fuzzyEnding);
		} else if (escapedString.startsWith("^") && escapedString.endsWith("$")) {
			return new Term();
		} else if (escapedString.startsWith("^")) {
			return new Term();
		} else if (escapedString.endsWith("$")) {
			return new Term();
		} else {
			return new Term();
		}
	}

	@Override
	public String getModifiedQuery() {
//		return createSpecialTerm().getModifiedQuery();
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