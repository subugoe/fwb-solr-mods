package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class OperatorNot extends QueryTokenSymbol {

	@Override
	public String getModifiedQuery() throws ParseException {
		return "NOT ";
	}

	@Override
	public String getHlQuery() throws ParseException {
		return "";
	}

}
