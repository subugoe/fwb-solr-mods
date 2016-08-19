package sub.fwb.parse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.tokens.AndOperator;
import sub.fwb.parse.tokens.ComplexPhrase;
import sub.fwb.parse.tokens.OrOperator;
import sub.fwb.parse.tokens.Phrase;
import sub.fwb.parse.tokens.PrefixedComplexPhrase;
import sub.fwb.parse.tokens.PrefixedPhrase;
import sub.fwb.parse.tokens.PrefixedRegex;
import sub.fwb.parse.tokens.PrefixedTerm;
import sub.fwb.parse.tokens.QueryToken;
import sub.fwb.parse.tokens.Regex;
import sub.fwb.parse.tokens.Term;

public class TokenFactory {

	private Map<String, String> boosts;

	public TokenFactory(String qfWithBoosts) {
		createMapWithBoosts(qfWithBoosts);
	}
	
	private void createMapWithBoosts(String qf) {
		boosts = new HashMap<String, String>();
		boosts.put("artikel", "");
		String[] fields = qf.trim().split("\\s+");
		for (String fieldWithBoost : fields) {
			String fieldName = fieldWithBoost.split("\\^")[0];
			String boostValue = "^" + fieldWithBoost.split("\\^")[1];
			boosts.put(fieldName, boostValue);
		}
	}

	public LinkedList<QueryToken> createTokens(String queryString) throws ParseException {
		LinkedList<QueryToken> allTokens = new LinkedList<>();
		String[] qParts = queryString.trim().split("\\s+");
		String currentPhrase = "";
		for (String q : qParts) {
			if ("OR".equals(q)) {
				allTokens.add(new OrOperator());
			} else if ("AND".equals(q)) {
				allTokens.add(new AndOperator());
			} else if (isRegex(q)) {
				addRegexOrPrefixedRegex(allTokens, q);
			} else if (isOneWordPhrase(q)) {
				addPhraseOrPrefixedPhrase(allTokens, q);
			} else if (startingAPhrase(q) || insideAPhrase(currentPhrase, q)) {
				currentPhrase += q + " ";
			} else if (finishingAPhrase(currentPhrase, q)) {
				addPhraseOrPrefixedPhrase(allTokens, currentPhrase + q);
				currentPhrase = "";
			} else {
				addTermOrPrefixedTerm(allTokens, q);
			}
		}
		checkIfIncomplete(currentPhrase);

		return allTokens;
	}

	private void addPhraseOrPrefixedPhrase(LinkedList<QueryToken> allTokens, String phraseString) {
		if (hasPrefix(phraseString) && isComplex(phraseString)) {
			allTokens.add(new PrefixedComplexPhrase(phraseString));
		} else if (hasPrefix(phraseString)) {
			allTokens.add(new PrefixedPhrase(phraseString));
		} else if (isComplex(phraseString)) {
			allTokens.add(new ComplexPhrase(phraseString));
		} else {
			allTokens.add(new Phrase(phraseString));
		}
	}

	private void addTermOrPrefixedTerm(LinkedList<QueryToken> allTokens, String termString) throws ParseException {
		if (hasPrefix(termString)) {
			allTokens.add(new PrefixedTerm(termString, boosts));
		} else {
			allTokens.add(new Term(termString));
		}
	}

	private void addRegexOrPrefixedRegex(LinkedList<QueryToken> allTokens, String regexString) {
		if (hasPrefix(regexString)) {
			allTokens.add(new PrefixedRegex(regexString));
		} else {
			allTokens.add(new Regex(regexString));
		}
	}

	private boolean startingAPhrase(String q) {
		return q.contains("\"") && !q.endsWith("\"");
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

	public QueryToken createOneToken(String tokenString) throws ParseException {
		return createTokens(tokenString).get(0);
	}
	
}
