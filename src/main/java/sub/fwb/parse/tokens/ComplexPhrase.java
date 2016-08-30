package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class ComplexPhrase extends QueryToken {

	public ComplexPhrase(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		ParseUtil.checkIfOneWord(escapedPhrase);
		ParseUtil.checkForLeadingWildcards(escapedPhrase);
		return String.format(
				"_query_:\"{!complexphrase}%s\" +(_query_:\"{!complexphrase}artikel:%s\" _query_:\"{!complexphrase}zitat:%s\") ",
				escapedPhrase, escapedPhrase, escapedPhrase);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String escapedPhrase = escapedString.replaceAll("\"", "\\\\\"");
		return String.format("_query_:\"{!complexphrase}artikel_text:%s\" _query_:\"{!complexphrase}zitat_text:%s\" ",
				escapedPhrase, escapedPhrase);
	}

}
