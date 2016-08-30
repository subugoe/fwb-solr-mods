package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class ComplexPhrasePrefixed extends QueryTokenPrefixed {

	public ComplexPhrasePrefixed(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
		splitIntoPrefixAndPostfix();
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		ParseUtil.checkIfOneWord(escapedPhrase);
		ParseUtil.checkForLeadingWildcards(escapedPhrase);
		return String.format("+_query_:\"{!complexphrase}%s\" ", escapedPhrase);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		String postfixTemp = escapedPhrase.split(":")[1];
		if (prefix.equals("zitat")) {
			return String.format("_query_:\"{!complexphrase}zitat_text:%s\" ", postfixTemp);
		} else {
			return String.format("_query_:\"{!complexphrase}artikel_text:%s\" ", postfixTemp);
		}
	}

}
