package sub.fwb.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.tokens.OperatorAnd;
import sub.fwb.parse.tokens.ComplexPhrase;
import sub.fwb.parse.tokens.OperatorNot;
import sub.fwb.parse.tokens.OperatorOr;
import sub.fwb.parse.tokens.ParenthesisLeft;
import sub.fwb.parse.tokens.ParenthesisRight;
import sub.fwb.parse.tokens.Phrase;
import sub.fwb.parse.tokens.ComplexPhrasePrefixed;
import sub.fwb.parse.tokens.PhrasePrefixed;
import sub.fwb.parse.tokens.RegexPrefixed;
import sub.fwb.parse.tokens.TermPrefixed;
import sub.fwb.parse.tokens.QueryToken;
import sub.fwb.parse.tokens.Regex;
import sub.fwb.parse.tokens.Term;

public class TokenFactory {

	private Map<String, String> boosts;
	private Map<String, String> mapForFacetQueries;
	private List<QueryToken> allTokens = new ArrayList<>();
	private String solrFieldEnding = "";

	public List<QueryToken> createTokens(String queryString, String qfWithBoosts, boolean exactSearch) throws ParseException {
		if (exactSearch) {
			solrFieldEnding = ParseUtil.EXACT;
		} else {
			solrFieldEnding = "";
		}
		createMapWithBoosts(qfWithBoosts, solrFieldEnding);
		createMapForFacetQueries(qfWithBoosts, solrFieldEnding);
		allTokens = new ArrayList<>();
		String[] qParts = queryString.trim().split("\\s+");
		String currentPhrase = "";
		for (String q : qParts) {
			String qWithoutSpecials = ParseUtil.removeSpecialChars(q);
			if (qWithoutSpecials.equals("")) {
				continue;
			} else if (qWithoutSpecials.length() > 50) {
				throw new ParseException("Suchanfrage zu lang: " + q.substring(0, 15) + "...");
			}

			String qOrig = q;
			if (!q.equals("(") && startsWithParen(q)) {
				allTokens.add(new ParenthesisLeft());
				q = q.substring(1);
			}
			boolean addRightParen = false;
			if (!qOrig.equals(")") && endsWithParen(qOrig)) {
				q = q.substring(0, q.length() - 1);
				addRightParen = true;
			}

			if ("OR".equals(q)) {
				allTokens.add(new OperatorOr());
			} else if ("AND".equals(q)) {
				allTokens.add(new OperatorAnd());
			} else if ("NOT".equals(q)) {
				allTokens.add(new OperatorNot());
			} else if ("(".equals(q)) {
				allTokens.add(new ParenthesisLeft());
			} else if (")".equals(q)) {
				allTokens.add(new ParenthesisRight());
			} else if (isRegex(q)) {
				addRegexOrPrefixedRegex(q);
			} else if (isOneWordPhrase(q)) {
				addTermInsteadOfPhrase(q);
			} else if (startingAPhrase(q) || insideAPhrase(currentPhrase, q)) {
				currentPhrase += q + " ";
			} else if (finishingAPhrase(currentPhrase, q)) {
				addPhraseOrPrefixedPhrase(currentPhrase + q);
				currentPhrase = "";
			} else {
				addTermOrPrefixedTerm(q);
			}

			if (addRightParen) {
				allTokens.add(new ParenthesisRight());
			}
		}
		checkIfIncomplete(currentPhrase);

		return allTokens;
	}

	private void createMapWithBoosts(String qf, String solrFieldEnding) {
		boosts = new HashMap<String, String>();
		boosts.put("artikel" + solrFieldEnding, "");
		String[] fields = qf.trim().split("\\s+");
		for (String fieldWithBoost : fields) {
			String fieldName = fieldWithBoost.split("\\^")[0];
			String boostValue = "^" + fieldWithBoost.split("\\^")[1];
			boosts.put(fieldName, boostValue);
		}
	}

	private void createMapForFacetQueries(String qf, String solrFieldEnding) {
		mapForFacetQueries = new HashMap<String, String>();
		String[] fields = qf.trim().split("\\s+");
		for (String fieldWithBoost : fields) {
			String fieldName = fieldWithBoost.split("\\^")[0];
			mapForFacetQueries.put(fieldName, "");
		}
	}

	private boolean startsWithParen(String q) {
		return q.startsWith("(");
	}

	private boolean endsWithParen(String q) {
		if (q.startsWith("(") && q.endsWith(")") && parensMatch(q.substring(1, q.length() - 1))) {
			// (imbis)
			// (legatar(ius))
			return true;
		} else if (q.endsWith(")") && parensMatch(q.substring(0, q.length() - 1))) {
			// imbis)
			// legatar(ius))
			return true;
		}
		// legatar(ius)
		// (legatar(ius)
		return false;
	}

	private boolean parensMatch(String s) {
		return s.contains("(") && s.contains(")") || !s.contains("(") && !s.contains(")");
	}

	private void addTermInsteadOfPhrase(String oneWordPhrase) throws ParseException {
		String withReplaced = oneWordPhrase.replaceFirst("\"", "^");
		withReplaced = withReplaced.substring(0, withReplaced.length() - 1) + "$";
		if (hasPrefix(oneWordPhrase)) {
			allTokens.add(new TermPrefixed(withReplaced, solrFieldEnding, boosts));
		} else {
			allTokens.add(new Term(withReplaced, solrFieldEnding, mapForFacetQueries));
		}
	}

	private void addPhraseOrPrefixedPhrase(String phraseString) {
		if (hasPrefix(phraseString) && isComplex(phraseString)) {
			allTokens.add(new ComplexPhrasePrefixed(phraseString, solrFieldEnding));
		} else if (hasPrefix(phraseString)) {
			allTokens.add(new PhrasePrefixed(phraseString, solrFieldEnding));
		} else if (isComplex(phraseString)) {
			allTokens.add(new ComplexPhrase(phraseString, solrFieldEnding, mapForFacetQueries));
		} else {
			allTokens.add(new Phrase(phraseString, solrFieldEnding, mapForFacetQueries));
		}
	}

	private void addTermOrPrefixedTerm(String termString) throws ParseException {
		if (hasPrefix(termString)) {
			allTokens.add(new TermPrefixed(termString, solrFieldEnding, boosts));
		} else {
			allTokens.add(new Term(termString, solrFieldEnding, mapForFacetQueries));
		}
	}

	private void addRegexOrPrefixedRegex(String regexString) {
		if (hasPrefix(regexString)) {
			allTokens.add(new RegexPrefixed(regexString, solrFieldEnding));
		} else {
			allTokens.add(new Regex(regexString, solrFieldEnding, mapForFacetQueries));
		}
	}

	private boolean startingAPhrase(String q) throws ParseException {
		boolean startOfPhrase = q.startsWith("\"") && !q.endsWith("\"");
		boolean startOfPrefixedPhrase = q.matches("[^:\"]+:\"[^:\"]+");
		return startOfPhrase || startOfPrefixedPhrase;
	}

	private boolean insideAPhrase(String currentPhrase, String q) {
		return !currentPhrase.isEmpty() && !q.endsWith("\"");
	}

	private boolean finishingAPhrase(String currentPhrase, String q) throws ParseException {
		if (q.endsWith("\"") && currentPhrase.isEmpty()) {
			throw new ParseException("Phrase ohne Anfang: " + q);
		}
		return q.endsWith("\"");
	}

	private boolean isRegex(String q) {
		return (q.indexOf("/") < q.length() - 1) && q.endsWith("/");
	}

	private boolean isOneWordPhrase(String q) {
		return (q.indexOf("\"") < q.length() - 1) && q.endsWith("\"");
	}

	private void checkIfIncomplete(String currentPhrase) throws ParseException {
		if (!currentPhrase.isEmpty()) {
			throw new ParseException("Phrase nicht komplett: " + currentPhrase);
		}
	}

	private boolean hasPrefix(String tokenString) {
		return tokenString.matches("[a-z0-9_]+:.*");
	}

	private boolean isComplex(String phrase) {
		return phrase.contains("*") || phrase.contains("?");
	}

	public QueryToken createOneToken(String tokenString, String queryFieldsWithBoosts, boolean exactSearch) throws ParseException {
		return createTokens(tokenString, queryFieldsWithBoosts, exactSearch).get(0);
	}

}
