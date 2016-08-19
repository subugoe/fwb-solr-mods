package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class PrefixedPhrase extends PrefixedQueryToken {

	public PrefixedPhrase(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
	}
	
	@Override
	public String getModifiedQuery() {
		return String.format("+%s ", escapedString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		// TODO Auto-generated method stub
		return "";
	}

}
