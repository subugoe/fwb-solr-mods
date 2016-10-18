package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class OperatorOr extends QueryTokenSymbol {

	@Override
	public String getModifiedQuery() throws ParseException {
		return "OR ";
	}

	@Override
	public String getHlQuery() throws ParseException {
		return "";
	}

}