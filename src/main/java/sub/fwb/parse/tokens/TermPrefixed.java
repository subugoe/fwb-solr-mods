package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

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
		if (postfix.endsWith("~1") || postfix.endsWith("~2")) {
			String fuzzy = postfix.substring(postfix.length() - 2);
			postfix = postfix.substring(0, postfix.length() - 2);
			postfix = ParseUtil.freeFromCircumflexAndDollar(postfix);
			return String.format("+%s:%s%s%s ", prefix, postfix, fuzzy, boost);
		} else if (postfix.startsWith("^") && postfix.endsWith("$")) {
			postfix = postfix.substring(1, postfix.length() - 1);
			return String.format("+%s:%s%s ", prefix, postfix, boost);
		} else if (postfix.startsWith("^")) {
			postfix = postfix.substring(1, postfix.length());
			return String.format("+%s:(%s %s*)%s ", prefix, postfix, postfix, boost);
		} else if (postfix.endsWith("$")) {
			postfix = postfix.substring(0, postfix.length() - 1);
			return String.format("+%s:*%s%s ", prefix, postfix, boost);
		} else {
			return String.format("+%s:(%s %s* *%s*)%s ", prefix, postfix, postfix, postfix, boost);
		}
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
