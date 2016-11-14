package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Phrase extends QueryToken {

	public Phrase(String phraseString, String prefixEnding) {
		this.prefixEnding = prefixEnding;
		originalTokenString = ParseUtil.removeParensAndPipe(phraseString);
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() {
		String articleField = ParseUtil.article(prefixEnding);
		String citationField = ParseUtil.citation(prefixEnding);
		return String.format("%s +(%s:%s %s:%s) ", escapedString, articleField, escapedString, citationField,
				escapedString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String articleTextField = ParseUtil.articleText(prefixEnding);
		String citationTextField = ParseUtil.citationText(prefixEnding);
		return String.format("%s:%s %s:%s ", articleTextField, escapedString, citationTextField, escapedString);
	}

}
