package sub.fwb.parse.tokens;

public class PrefixedPhrase extends PrefixedQueryToken {

	public PrefixedPhrase(String phraseString) {
		originalTokenString = phraseString;
		escapeSpecialChars();
	}
	
	@Override
	public String getModifiedQuery() {
		return String.format("+%s ", escapedString);
	}

}
