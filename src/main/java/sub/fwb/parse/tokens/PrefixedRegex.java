package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class PrefixedRegex extends PrefixedQueryToken {

	public PrefixedRegex(String regexString) {
		originalTokenString = regexString;
	}

	@Override
	public String getModifiedQuery() throws ParseException {
		return String.format("+%s ", originalTokenString);
	}

	@Override
	public String getHlQuery() throws ParseException {
		// TODO Auto-generated method stub
		return "";
	}

}
