package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Term extends QueryTokenSearchString {

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

	@Override
	public String getModifiedQuery() {
		return createSubTerm().getModifiedQuery();
	}

	@Override
	public String getHlQuery() throws ParseException {
		return createSubTerm().getHlQuery();
	}

	@Override
	public Map<String, String> getFacetQueries() {
		return createSubTerm().getFacetQueries();
	}

	private SubTerm createSubTerm() {
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

	private interface SubTerm {
		public String getModifiedQuery();
		public String getHlQuery();
		public Map<String, String> getFacetQueries();
	}

	private class WithFuzzy implements SubTerm {
		private String fuzzy;

		public WithFuzzy(String fuzzyEnding) {
			this.fuzzy = fuzzyEnding;
		}

		@Override
		public String getModifiedQuery() {
			return String.format("(%s%s +(%s:%s%s %s:%s%s)) ", searchString, fuzzy, articleField, searchString, fuzzy,
					citationField, searchString, fuzzy);
		}

		@Override
		public String getHlQuery() {
			return String.format("%s:%s%s %s:%s%s ", articleTextField, searchString, fuzzy, citationTextField,
					searchString, fuzzy);
		}

		@Override
		public Map<String, String> getFacetQueries() {
			for (String searchField : mapForFacetQueries.keySet()) {
				mapForFacetQueries.put(searchField, searchString + fuzzy);
			}
			return mapForFacetQueries;
		}
	}

	private class PreciseWord implements SubTerm {
		@Override
		public String getModifiedQuery() {
			return String.format("(%s +(%s:%s %s:%s)) ", searchString, articleField, searchString, citationField,
					searchString);
		}

		@Override
		public String getHlQuery() {
			return String.format("%s:%s %s:%s ", articleTextField, searchString, citationTextField, searchString);
		}

		@Override
		public Map<String, String> getFacetQueries() {
			for (String searchField : mapForFacetQueries.keySet()) {
				mapForFacetQueries.put(searchField, searchString);
			}
			return mapForFacetQueries;
		}
	}

	private class WordBegin implements SubTerm {
		@Override
		public String getModifiedQuery() {
			return String.format("(%s %s* +(%s:%s* %s:%s*)) ", searchString, searchString, articleField, searchString,
					citationField, searchString);
		}

		@Override
		public String getHlQuery() {
			return String.format("%s:%s* %s:%s* ", articleTextField, searchString, citationTextField, searchString);
		}

		@Override
		public Map<String, String> getFacetQueries() {
			for (String searchField : mapForFacetQueries.keySet()) {
				mapForFacetQueries.put(searchField, searchString + "*");
			}
			return mapForFacetQueries;
		}
	}

	private class WordEnd implements SubTerm {
		@Override
		public String getModifiedQuery() {
			return String.format("(*%s +(%s:*%s %s:*%s)) ", searchString, articleField, searchString, citationField,
					searchString);
		}

		@Override
		public String getHlQuery() {
			return String.format("%s:*%s %s:*%s ", articleTextField, searchString, citationTextField, searchString);
		}

		@Override
		public Map<String, String> getFacetQueries() {
			for (String searchField : mapForFacetQueries.keySet()) {
				mapForFacetQueries.put(searchField, "*" + searchString);
			}
			return mapForFacetQueries;
		}
	}

	private class PartOfWord implements SubTerm {
		@Override
		public String getModifiedQuery() {
			return String.format("(%s %s* *%s* +(%s:*%s* %s:*%s*)) ", searchString, searchString, searchString,
					articleField, searchString, citationField, searchString);
		}

		@Override
		public String getHlQuery() {
			return String.format("%s:*%s* %s:*%s* ", articleTextField, searchString, citationTextField, searchString);
		}

		@Override
		public Map<String, String> getFacetQueries() {
			for (String searchField : mapForFacetQueries.keySet()) {
				mapForFacetQueries.put(searchField, "*" + searchString + "*");
			}
			return mapForFacetQueries;
		}
	}

}