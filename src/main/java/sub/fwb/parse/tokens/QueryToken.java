package sub.fwb.parse.tokens;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public abstract class QueryToken {

	protected String originalTokenString;
	protected String escapedString = "";
	protected String prefixEnding = "";
	protected Map<String, String> mapForFacetQueries;

	abstract public String getModifiedQuery() throws ParseException;

	abstract public String getHlQuery() throws ParseException;

	public Map<String, String> getFacetQueries() {
		return new HashMap<>();
	}

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
