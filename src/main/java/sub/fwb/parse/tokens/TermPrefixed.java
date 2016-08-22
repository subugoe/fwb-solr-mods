package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

public class TermPrefixed extends QueryTokenPrefixed {

	private Map<String, String> boosts;

	public TermPrefixed(String termString, Map<String, String> qfWithBoosts) throws ParseException {
		originalTokenString = termString;
		escapeSpecialChars();
		checkForCorrectness();
		splitIntoPrefixAndPostfix();
		boosts = qfWithBoosts;
	}

	private void checkForCorrectness() throws ParseException {
		int colonCount = escapedString.length() - escapedString.replaceAll(":", "").length();
		if (colonCount > 1) {
			throw new ParseException("Doppelpunkt nur einmal erlaubt: " + originalTokenString);
		}
		String[] prePost = escapedString.split(":");
		if (prePost.length == 1) {
			throw new ParseException("Unvollständige Suchanfrage: " + originalTokenString);
		}
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		String boost = getBoost(prefix);
		return String.format("+%s:(%s %s* *%s*)%s ", prefix, postfix, postfix, postfix, boost);
	}

	private String getBoost(String prefix) throws ParseException {
		String boost = boosts.get(prefix);
		if (boost == null) {
			throw new ParseException("Suchfeld existiert nicht: " + prefix);
		}
		return boost;
	}

	@Override
	public String getHlQuery() throws ParseException {
		if (prefix.equals("zitat")) {
			return "zitat_text:*" + postfix + "* ";
		} else {
			return "artikel_text:*" + postfix + "* ";
		}
	}

}
