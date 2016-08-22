package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class Phrase extends QueryToken {

	public Phrase(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() {
		return String.format("%s +(artikel:%s zitat:%s) ", escapedString, escapedString, escapedString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		// TODO Auto-generated method stub
		return "";
	}

}
