package sub.fwb.parse.tokens;

import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class TermPrefixed extends QueryTokenPrefixed {

	private Map<String, String> boosts;

	public TermPrefixed(String termString, String prefixEnding, Map<String, String> qfWithBoosts)
			throws ParseException {
		originalTokenString = termString;
		escapeSpecialChars();
		checkForCorrectness();
		splitIntoPrefixAndPostfix(prefixEnding);
		ParseUtil.checkForProhibitedCharsInTerm(postfix);
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
		String boost = getBoost(prefixWithEnding);
		if (postfix.endsWith("~1") || postfix.endsWith("~2")) {
			String fuzzy = postfix.substring(postfix.length() - 2);
			String postfixTemp = postfix.substring(0, postfix.length() - 2);
			postfixTemp = ParseUtil.freeFromCircumflexAndDollar(postfixTemp);
			return String.format("+%s:%s%s%s ", prefixWithEnding, postfixTemp, fuzzy, boost);
		} else if (postfix.startsWith("^") && postfix.endsWith("$")) {
			String postfixTemp = postfix.substring(1, postfix.length() - 1);
			return String.format("+%s:%s%s ", prefixWithEnding, postfixTemp, boost);
		} else if (postfix.startsWith("^")) {
			String postfixTemp = postfix.substring(1, postfix.length());
			return String.format("+%s:(%s %s*)%s ", prefixWithEnding, postfixTemp, postfixTemp, boost);
		} else if (postfix.endsWith("$")) {
			String postfixTemp = postfix.substring(0, postfix.length() - 1);
			return String.format("+%s:*%s%s ", prefixWithEnding, postfixTemp, boost);
		} else {
			return String.format("+%s:(%s %s* *%s*)%s ", prefixWithEnding, postfix, postfix, postfix, boost);
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
		String prefixForHl = prefix + "_text" + prefixEnding;
		if (postfix.endsWith("~1") || postfix.endsWith("~2")) {
			String fuzzy = postfix.substring(postfix.length() - 2);
			String postfixTemp = postfix.substring(0, postfix.length() - 2);
			postfixTemp = ParseUtil.freeFromCircumflexAndDollar(postfixTemp);
			return String.format("%s:%s%s ", prefixForHl, postfixTemp, fuzzy);
		} else if (postfix.startsWith("^") && postfix.endsWith("$")) {
			String postfixTemp = postfix.substring(1, postfix.length() - 1);
			return String.format("%s:%s ", prefixForHl, postfixTemp);
		} else if (postfix.startsWith("^")) {
			String postfixTemp = postfix.substring(1, postfix.length());
			return String.format("%s:%s* ", prefixForHl, postfixTemp);
		} else if (postfix.endsWith("$")) {
			String postfixTemp = postfix.substring(0, postfix.length() - 1);
			return String.format("%s:*%s ", prefixForHl, postfixTemp);
		} else {
			return String.format("%s:*%s* ", prefixForHl, postfix);
		}
	}

}
