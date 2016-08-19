package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class Term extends QueryToken {

	public Term(String tokenString) {
		originalTokenString = tokenString;
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() {
		return String.format("%s %s* *%s* +(artikel:*%s* zitat:*%s*) ", escapedString, escapedString, escapedString,
				escapedString, escapedString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		// TODO Auto-generated method stub
		return "";
	}

}