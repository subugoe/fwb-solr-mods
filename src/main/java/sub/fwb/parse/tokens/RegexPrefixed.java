package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class RegexPrefixed extends QueryTokenPrefixed {

	public RegexPrefixed(String regexString, String prefixEnding) {
		originalTokenString = regexString;
		escapedString = regexString;
		splitIntoPrefixAndPostfix(prefixEnding);
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		return String.format("+%s:%s ", prefixWithEnding, postfix);
	}

	@Override
	public String getHlQuery() throws ParseException {
		if (prefix.equals("lemma")) {
			return String.format("artikel_text%s:%s ", prefixEnding, postfix);
		}
		return String.format("%s_text%s:%s ", prefix, prefixEnding, postfix);
	}

}
