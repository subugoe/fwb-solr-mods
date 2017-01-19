package sub.fwb.parse.tokens;

public abstract class QueryTokenPrefixed extends QueryTokenSearchString {

	protected String prefix = "";
	protected String prefixWithEnding = "";
	protected String postfix = "";

	protected void splitIntoPrefixAndPostfix(String possibleEnding) {
		prefixEnding = possibleEnding;
		String[] prePost = escapedString.split(":");
		prefix = prePost[0];
		prefixWithEnding = prefix + possibleEnding;
		postfix = prePost[1];
	}

	public String getPrefix() {
		return prefix;
	}

}
