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

	public List<QueryToken> createTokens(String queryString) throws ParseException {
		List<QueryToken> allTokens = new ArrayList<>();
		String[] qParts = queryString.trim().split("\\s+");
		String currentPhrase = "";
		for (String q : qParts) {
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

	private void addPhraseOrPrefixedPhrase(List<QueryToken> allTokens, String phraseString) {
		if (hasPrefix(phraseString) && isComplex(phraseString)) {
			allTokens.add(new ComplexPhrasePrefixed(phraseString));
		} else if (hasPrefix(phraseString)) {
			allTokens.add(new PhrasePrefixed(phraseString));
		} else if (isComplex(phraseString)) {
			allTokens.add(new ComplexPhrase(phraseString));
		} else {
			allTokens.add(new Phrase(phraseString));
		}
	}

	private void addTermOrPrefixedTerm(List<QueryToken> allTokens, String termString) throws ParseException {
		if (hasPrefix(termString)) {
			allTokens.add(new TermPrefixed(termString, boosts));
		} else {
			allTokens.add(new Term(termString));
		}
	}

	private void addRegexOrPrefixedRegex(List<QueryToken> allTokens, String regexString) {
		if (hasPrefix(regexString)) {
			allTokens.add(new RegexPrefixed(regexString));
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
