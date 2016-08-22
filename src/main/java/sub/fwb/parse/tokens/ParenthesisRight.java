package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class ParenthesisRight extends QueryToken {

	@Override
	public String getModifiedQuery() throws ParseException {
		return ") ";
	}

	@Override
	public String getHlQuery() throws ParseException {
		return "";
	}

}
