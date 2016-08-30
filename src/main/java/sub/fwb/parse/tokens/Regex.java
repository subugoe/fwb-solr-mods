package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class Regex extends QueryToken {

	public Regex(String regexString) {
		originalTokenString = regexString;
	}

	@Override
	public String getModifiedQuery() {
		return String.format("+(artikel:%s zitat:%s) ", originalTokenString, originalTokenString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		return String.format("artikel_text:%s zitat_text:%s ", originalTokenString, originalTokenString);
	}

}
