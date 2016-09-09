package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class RegexPrefixed extends QueryTokenPrefixed {

	public RegexPrefixed(String regexString) {
		originalTokenString = regexString;
		escapedString = regexString;
		splitIntoPrefixAndPostfix();
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		return String.format("+%s ", originalTokenString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		if (prefix.equals("lemma")) {
			return "";
		}
		return String.format("%s_text:%s ", prefix, postfix);
	}

}
