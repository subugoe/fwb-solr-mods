package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

import sub.fwb.parse.ParseUtil;

public class Term extends QueryToken {

	public Term(String tokenString) {
		originalTokenString = tokenString;
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() {
		if (escapedString.endsWith("~1") || escapedString.endsWith("~2")) {
			String s = escapedString.substring(0, escapedString.length() - 2);
			s = ParseUtil.freeFromCircumflexAndDollar(s);
			String fuzzy = escapedString.substring(escapedString.length() - 2);
			return String.format("%s%s +(artikel:%s%s zitat:%s%s) ", s, fuzzy, s, fuzzy, s, fuzzy);
		} else if (escapedString.startsWith("^") && escapedString.endsWith("$")) {
			String s = escapedString.substring(1, escapedString.length() - 1);
			return String.format("%s +(artikel:%s zitat:%s) ", s, s, s);
		} else if (escapedString.startsWith("^")) {
			String s = escapedString.substring(1, escapedString.length());
			return String.format("%s %s* +(artikel:%s* zitat:%s*) ", s, s, s, s);
		} else if (escapedString.endsWith("$")) {
			String s = escapedString.substring(0, escapedString.length() - 1);
			return String.format("*%s +(artikel:*%s zitat:*%s) ", s, s, s);
		} else {
			return String.format("%s %s* *%s* +(artikel:*%s* zitat:*%s*) ", escapedString, escapedString, escapedString,
					escapedString, escapedString);
		}
	}

	@Override
	public String getHlQuery() throws ParseException {
		// TODO Auto-generated method stub
		return "";
	}

}