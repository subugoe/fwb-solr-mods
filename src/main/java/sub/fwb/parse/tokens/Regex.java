package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Regex extends QueryToken {

	public Regex(String regexString) {
		originalTokenString = regexString;
	}

	@Override
	public String getModifiedQuery() {
		String articleField = ParseUtil.article(prefixEnding);
		String citationField = ParseUtil.citation(prefixEnding);
		return String.format("+(%s:%s %s:%s) ", articleField, originalTokenString, citationField, originalTokenString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		String articleTextField = ParseUtil.articleText(prefixEnding);
		String citationTextField = ParseUtil.citationText(prefixEnding);
		return String.format("%s:%s %s:%s ", articleTextField, originalTokenString, citationTextField,
				originalTokenString);
	}

}
