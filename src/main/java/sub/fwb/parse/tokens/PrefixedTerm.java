package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

public class PrefixedTerm extends PrefixedQueryToken {
	
	private Map<String, String> boosts;

	public PrefixedTerm(String termString, Map<String, String> qfWithBoosts) {
		originalTokenString = termString;
		escapeSpecialChars();
		boosts = qfWithBoosts;
	}
	
	@Override
	public String getModifiedQuery() throws ParseException {
		int colonCount = escapedString.length() - escapedString.replaceAll(":", "").length();
		if (colonCount > 1) {
			throw new ParseException("Doppelpunkt nur einmal erlaubt: " + originalTokenString);
		}
		String[] prePost = escapedString.split(":");
		if (prePost.length == 1) {
			throw new ParseException("Unvollständige Suchanfrage: " + originalTokenString);
		}
		String prefix = prePost[0];
		String boost = getBoost(prefix);
		String postfix = prePost[1];
		return String.format("+%s:(%s %s* *%s*)%s ", prefix, postfix, postfix, postfix, boost);
	}

	private String getBoost(String prefix) throws ParseException {
		String boost = boosts.get(prefix);
		if (boost == null) {
			throw new ParseException("Suchfeld existiert nicht: " + prefix);
		}
		return boost;
	}

}
