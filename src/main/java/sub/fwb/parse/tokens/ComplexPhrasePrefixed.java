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
		if (prefix.equals("lemma")) {
			return "";
		}
		if (prefix.equals("zitat")) {
			throw new ParseException("Komplexe Phrasensuche in Zitaten ist leider nicht möglich.");
		}
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		String postfixTemp = escapedPhrase.split(":")[1];
		return String.format("_query_:\"{!complexphrase}%s_text:%s\" ", prefix, postfixTemp);
	}

}
