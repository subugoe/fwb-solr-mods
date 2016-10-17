package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class PhrasePrefixed extends QueryTokenPrefixed {

	public PhrasePrefixed(String phraseString, String prefixEnding) {
		originalTokenString = phraseString;
		escapeSpecialChars();
		splitIntoPrefixAndPostfix(prefixEnding);
	}

	@Override
	public String getModifiedQuery() {
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
