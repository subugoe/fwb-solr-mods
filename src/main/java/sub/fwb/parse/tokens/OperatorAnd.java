package sub.fwb.parse.tokens;

import org.apache.solr.parser.ParseException;

public class OperatorAnd extends QueryTokenSymbol {

	@Override
	public String getModifiedQuery() throws ParseException {
		return "AND ";
	}

	@Override
	public String getHlQuery() throws ParseException {
		return "";
	}

}
