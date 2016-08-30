package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class PhrasePrefixed extends QueryTokenPrefixed {

	public PhrasePrefixed(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
		splitIntoPrefixAndPostfix();
	}

	@Override
	public String getModifiedQuery() {
		return String.format("+%s ", escapedString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		if (prefix.equals("zitat")) {
			return String.format("zitat_text:%s ", postfix);
		} else {
			return String.format("artikel_text:%s ", postfix);
		}
	}

}
