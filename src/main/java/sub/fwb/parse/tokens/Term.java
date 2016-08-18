package sub.fwb.parse.tokens;

public class Term extends QueryToken {

	public Term(String tokenString) {
		originalTokenString = tokenString;
		escapeSpecialChars();
	}

	@Override
	public String getModifiedQuery() {
		return String.format("%s %s* *%s* +(artikel:*%s* zitat:*%s*) ", escapedString, escapedString, escapedString,
				escapedString, escapedString);
	}

}