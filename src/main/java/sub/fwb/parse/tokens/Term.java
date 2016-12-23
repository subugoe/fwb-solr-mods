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
	private String searchString;

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

	@Override
	public String getModifiedQuery() {
		return createSpecialTerm().getModifiedQuery();
	}

	@Override
	public String getHlQuery() throws ParseException {
		return createSpecialTerm().getHlQuery();
	}

	private Term createSpecialTerm() {
		if (escapedString.endsWith("~1") || escapedString.endsWith("~2")) {
			searchString = escapedString.substring(0, escapedString.length() - 2);
			searchString = ParseUtil.freeFromCircumflexAndDollar(searchString);
			String fuzzyEnding = escapedString.substring(escapedString.length() - 2);
			return new WithFuzzy(fuzzyEnding);
		} else if (escapedString.startsWith("^") && escapedString.endsWith("$")) {
			searchString = escapedString.substring(1, escapedString.length() - 1);
			return new PreciseWord();
		} else if (escapedString.startsWith("^")) {
			searchString = escapedString.substring(1, escapedString.length());
			return new WordBegin();
		} else if (escapedString.endsWith("$")) {
			searchString = escapedString.substring(0, escapedString.length() - 1);
			return new WordEnd();
		} else {
			searchString = escapedString;
			return new PartOfWord();
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

	private class WithFuzzy extends Term {
		private String fuzzy;

		public WithFuzzy(String fuzzyEnding) {
			this.fuzzy = fuzzyEnding;
		}

		@Override
		public String getModifiedQuery() {
			return String.format("%s%s +(%s:%s%s %s:%s%s) ", searchString, fuzzy, articleField, searchString, fuzzy,
					citationField, searchString, fuzzy);
		}

		@Override
		public String getHlQuery() throws ParseException {
			return String.format("%s:%s%s %s:%s%s ", articleTextField, searchString, fuzzy, citationTextField,
					searchString, fuzzy);
		}
	}

	private class PreciseWord extends Term {
		@Override
		public String getModifiedQuery() {
			return String.format("%s +(%s:%s %s:%s) ", searchString, articleField, searchString, citationField,
					searchString);
		}

		@Override
		public String getHlQuery() throws ParseException {
			return String.format("%s:%s %s:%s ", articleTextField, searchString, citationTextField, searchString);
		}
	}

	private class WordBegin extends Term {
		@Override
		public String getModifiedQuery() {
			return String.format("%s %s* +(%s:%s* %s:%s*) ", searchString, searchString, articleField, searchString,
					citationField, searchString);
		}

		@Override
		public String getHlQuery() throws ParseException {
			return String.format("%s:%s* %s:%s* ", articleTextField, searchString, citationTextField, searchString);
		}
	}

	private class WordEnd extends Term {
		@Override
		public String getModifiedQuery() {
			return String.format("*%s +(%s:*%s %s:*%s) ", searchString, articleField, searchString, citationField,
					searchString);
		}

		@Override
		public String getHlQuery() throws ParseException {
			return String.format("%s:*%s %s:*%s ", articleTextField, searchString, citationTextField, searchString);
		}
	}

	private class PartOfWord extends Term {
		@Override
		public String getModifiedQuery() {
			return String.format("%s %s* *%s* +(%s:*%s* %s:*%s*) ", searchString, searchString, searchString,
					articleField, searchString, citationField, searchString);
		}

		@Override
		public String getHlQuery() throws ParseException {
			return String.format("%s:*%s* %s:*%s* ", articleTextField, searchString, citationTextField, searchString);
		}
	}

}