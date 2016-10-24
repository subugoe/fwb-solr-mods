package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public abstract class QueryToken {

	protected String originalTokenString;
	protected String escapedString = "";
	protected String prefixEnding = "";

	abstract public String getModifiedQuery() throws ParseException;

	abstract public String getHlQuery() throws ParseException;

	protected void escapeSpecialChars() {
		escapedString = originalTokenString.replaceAll("\\|", "\\\\|");
		escapedString = escapedString.replaceAll("\\(", "\\\\(");
		escapedString = escapedString.replaceAll("\\)", "\\\\)");
		escapedString = escapedString.replaceAll("\\[", "\\\\[");
		escapedString = escapedString.replaceAll("\\]", "\\\\]");
		escapedString = escapedString.replaceAll("-", "\\\\-");
		escapedString = ParseUtil.removeSpecialChars(escapedString);
	}

}
