package sub.fwb.parse.tokens;

public abstract class QueryTokenPrefixed extends QueryToken {

	protected String prefix = "";
	protected String postfix = "";

	protected void splitIntoPrefixAndPostfix() {
		String[] prePost = escapedString.split(":");
		prefix = prePost[0];
		postfix = prePost[1];
	}

}
